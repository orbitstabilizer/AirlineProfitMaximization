package project.airline.aircraft;


import project.airport.Airport;
import project.interfaces.AircraftInterface;

public abstract class Aircraft implements AircraftInterface{
	protected double fuelConsumption;
	protected double aircraftTypeMultiplier;

	protected Airport currentAirport; // The airport which the plane is currently in.
	protected double weight; // This value should not exceed maxWeight.
	protected double maxWeight; //This is the maximum allowable weight of the aircraft.

	protected double fuelWeight = 0.7;//it is the constant we use to convert fuel volume to fuel weight.
	protected double fuel;//holds the amount of fuel the aircraft has at any moment. 

	protected double fuelCapacity;//Fuel capacity of the airplane. It is different and fixed for every type of aircraft.
	protected double operationFee;


	public boolean canFly(Airport toAirport) {
		double necessaryFuel = this.getFuelConsumption(currentAirport.getDistance(toAirport));
		return this.hasFuel(necessaryFuel) && !toAirport.isFull() && !toAirport.equals(currentAirport);
	}
	protected double getBathtubCoeff(double x) {
		return (((25.9324*x-50.5633)*x+35.0554)*x-9.90346)*x+1.97413;
	}

	@Override
	public double fly(Airport toAirport) {// visibility
		double distance = currentAirport.getDistance(toAirport);

		double fuelConsumption = this.getFuelConsumption(distance);
		
		double flightCost = this.getFlightCost(toAirport);

		this.fuel-= fuelConsumption;
		this.weight -= fuelConsumption*fuelWeight;
		
		this.currentAirport = toAirport;
		return flightCost;
	}


	@Override
	public double addFuel(double fuel) {
		return canAddFuel(fuel);
	}

	private double canAddFuel(double fuel) {

		if (this.fuel + fuel<= this.fuelCapacity + 0.000002 && this.weight+ fuel*this.fuelWeight <= maxWeight ) {
			this.fuel+= fuel;
			this.weight += fuel*this.fuelWeight;
			return fuel*this.currentAirport.getFuelCost();
		}
		return 0;
	}
	

	@Override
	public double fillUp() {
		double fuel = this.fuelCapacity - this.fuel;
		return canAddFuel(fuel);
	}



	//Checks if the aircraft has the specified amount of fuel.
	@Override
	public boolean hasFuel(double fuel) {
		return this.fuel>=fuel;
		//(Math.abs(this.fuel - fuel) < 1e-6); useless
	}

	//Returns the ratio of weight to maximum weight.
	@Override
	public double getWeightRatio() {
		
		return this.weight/this.maxWeight;
	}
	
	protected double[] constants = new double[2];// {range, takeoffConst, fuelConsumption, fuelCapacity};
    private double getFuelConsumption(double distance,double weight, double[] constants) {
        return weight * constants[1] / 0.7 + this.fuelConsumption * this.getBathtubCoeff(distance / constants[0])* distance;
    }
    private boolean f(double r,double distance,double weight){
        return  r >= getFuelConsumption(distance, weight + 0.7*r, constants);
    }

    public double getOptimalFuel(double distance, double passangerWeight){
        double l = 0;
        double r = fuelCapacity;
        while(r-l > 0.01){
            double m = (l+r)/2;
            if(f(m,distance,this.weight+ passangerWeight)){
                r = m;
            }else{
                l = m;
            }
        }
        return (Math.ceil(l+10)) ;
    }

	abstract public double getFlightCost(Airport toAirport);

	abstract protected double getFuelConsumption(double distance);

	public boolean isInAirport(Airport airport) {
		return (airport.equals(currentAirport) );
	}
	public Airport getCurrentAirport() {
		return this.currentAirport;
	}
	public double getAircraftTypeMultiplier() {
		return aircraftTypeMultiplier;
	}

}
