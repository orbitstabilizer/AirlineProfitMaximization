package project.airline.aircraft.concrete;


import project.airline.aircraft.PassengerAircraft;
import project.airport.Airport;


public class RapidPassengerAircraft extends PassengerAircraft {
	public RapidPassengerAircraft(Airport airport,double operationFee){
		super(airport, operationFee);

		this.fuel = 0;
		this.weight = 80000;
		this.maxWeight = 185000;
		this.floorArea = 120;
		this.fuelCapacity = 120000;
		this.fuelConsumption = 5.3;
		this.aircraftTypeMultiplier = 1.9;
		
		this.constants[0] = 7000;
		this.constants[1] = 0.1;

	}

	@Override
	protected double getFuelConsumption(double distance) {
		double distanceRatio = distance/7000;
		double bathtubCoeff = this.getBathtubCoeff(distanceRatio);
		double takeoffPart = this.weight * 0.1/this.fuelWeight;
		double cruisePart = this.fuelConsumption * bathtubCoeff * distance;

		return takeoffPart + cruisePart;

	}

	@Override
	public double getFlightCost(Airport toAirport) {
		double FlightOperationCostConstant= 0.2;
		return flightCostCalculator(toAirport, FlightOperationCostConstant);
	}


}
