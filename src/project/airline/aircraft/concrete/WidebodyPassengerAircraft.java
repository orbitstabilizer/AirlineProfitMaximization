package project.airline.aircraft.concrete;


import project.airline.aircraft.PassengerAircraft;
import project.airport.Airport;

public class WidebodyPassengerAircraft extends PassengerAircraft {

	public WidebodyPassengerAircraft(Airport airport,double operationFee){
		super(airport, operationFee);
		this.fuel = 0;
		this.weight = 135000;
		this.maxWeight = 250000;
		this.floorArea = 450;
		this.fuelCapacity = 140000;
		this.fuelConsumption = 3.0;
		this.aircraftTypeMultiplier = 0.7;
		
		this.constants[0] = 14000;
		this.constants[1] = 0.1;

	}
	@Override
	protected double getFuelConsumption(double distance) {
		double distanceRatio = distance/14000;
		double bathtubCoeff = this.getBathtubCoeff(distanceRatio);
		double takeoffPart = this.weight * 0.1/this.fuelWeight;
		double cruisePart = this.fuelConsumption * bathtubCoeff * distance;

		return takeoffPart + cruisePart;
	}
	@Override
	public double getFlightCost(Airport toAirport) {
		double FlightOperationCostConstant= 0.15;
		return flightCostCalculator(toAirport, FlightOperationCostConstant);
	}


}
