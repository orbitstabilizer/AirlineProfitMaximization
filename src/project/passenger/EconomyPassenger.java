package project.passenger;

import java.util.ArrayList;

import project.airport.Airport;


public class EconomyPassenger extends Passenger {

	public EconomyPassenger(long id, double weight, int baggageCount, ArrayList<Airport> destinations) {

		super(id, weight, baggageCount, destinations,PassengerType.Economy);

	}

	@Override
	protected double calculateTicketPrice(Airport toAirport, double aircraftTypeMultiplier) {
		double passengerMultiplier = 0.6;

		return ticketPriceCalculator(toAirport, aircraftTypeMultiplier, passengerMultiplier);

	}


}
