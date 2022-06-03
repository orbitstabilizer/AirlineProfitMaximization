package project.passenger;

import java.util.ArrayList;

import project.airport.Airport;

public class LuxuryPassenger extends Passenger {

	public LuxuryPassenger(long id, double weight, int baggageCount, ArrayList<Airport> destinations) {
		super(id, weight, baggageCount, destinations,PassengerType.Luxury);
	}
	@Override
	protected double calculateTicketPrice(Airport toAirport, double aircraftTypeMultiplier) {
		double passengerMultiplier = 15;
		return ticketPriceCalculator(toAirport, aircraftTypeMultiplier, passengerMultiplier);

	}
}
