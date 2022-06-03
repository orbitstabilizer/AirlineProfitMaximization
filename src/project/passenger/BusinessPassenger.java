package project.passenger;

import java.util.ArrayList;

import project.airport.Airport;

public class BusinessPassenger extends Passenger {

	public BusinessPassenger(long id, double weight, int baggageCount, ArrayList<Airport> destinations) {
		super(id, weight, baggageCount, destinations,PassengerType.Business);
	}

	@Override
	protected double calculateTicketPrice(Airport toAirport, double aircraftTypeMultiplier) {
		double passengerMultiplier = 1.2;
		return ticketPriceCalculator(toAirport, aircraftTypeMultiplier, passengerMultiplier);
	}




}
