package project.passenger;

import java.util.ArrayList;

import project.airport.Airport;

public class FirstClassPassenger extends Passenger {

	public FirstClassPassenger(long iD, double weight, int baggageCount, ArrayList<Airport> destinations) {
		super(iD, weight, baggageCount, destinations,PassengerType.FirstClass);
	}
	@Override
	protected double calculateTicketPrice(Airport toAirport, double aircraftTypeMultiplier) {
		double passengerMultiplier = 3.2;
		return ticketPriceCalculator(toAirport, aircraftTypeMultiplier, passengerMultiplier);

	}



}