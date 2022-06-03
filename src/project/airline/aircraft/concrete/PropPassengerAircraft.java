package project.airline.aircraft.concrete;

import project.airline.aircraft.PassengerAircraft;
import project.airport.Airport;

public class PropPassengerAircraft extends PassengerAircraft {
	public PropPassengerAircraft(Airport airport,double operationFee){
		super(airport, operationFee);
		this.fuel = 0;
		this.weight = 14000;
		this.maxWeight = 23000;
		this.floorArea = 60;
		this.fuelCapacity = 6000;
		this.fuelConsumption = 0.6;
		this.aircraftTypeMultiplier = 0.9;
		
		this.constants[0] = 2000;
		this.constants[1] = 0.08;

	}

	@Override
	protected double getFuelConsumption(double distance) {
		double distanceRatio = distance/2000;
		double bathtubCoeff = this.getBathtubCoeff(distanceRatio);
		double takeoffPart = this.weight * 0.08/this.fuelWeight;
		double cruisePart = this.fuelConsumption * bathtubCoeff * distance;

		return takeoffPart + cruisePart;
	}

	@Override
	public double getFlightCost(Airport toAirport) {
		double FlightOperationCostConstant = 0.1;
		return flightCostCalculator(toAirport, FlightOperationCostConstant);
	}

	



}
