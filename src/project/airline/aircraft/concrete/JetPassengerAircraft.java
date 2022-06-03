package project.airline.aircraft.concrete;



import project.airline.aircraft.PassengerAircraft;
import project.airport.Airport;

public class JetPassengerAircraft extends PassengerAircraft{

	public JetPassengerAircraft(Airport airport,double operationFee){
		super(airport, operationFee);

		this.fuel = 0;
		this.weight = 10000;
		this.maxWeight = 18000;
		this.floorArea = 30;
		this.fuelCapacity = 10000;
		this.fuelConsumption = 0.7;
		this.aircraftTypeMultiplier = 5;
		
		this.constants[0] = 5000;
		this.constants[1] = 0.1;


	}

	@Override
	protected double getFuelConsumption(double distance) {

		double distanceRatio = distance/5000;
		double bathtubCoeff = this.getBathtubCoeff(distanceRatio);

		double takeoffPart = this.weight * 0.1/this.fuelWeight;
		double cruisePart = this.fuelConsumption * bathtubCoeff * distance;

		return takeoffPart + cruisePart;

	}
	@Override
	public double getFlightCost(Airport toAirport) {
		double FlightOperationCostConstant= 0.08;
		return flightCostCalculator(toAirport, FlightOperationCostConstant);
	}


    




}
