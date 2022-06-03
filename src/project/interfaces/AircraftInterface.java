package project.interfaces;

import project.airport.Airport;

public interface AircraftInterface {
	double fly(Airport toAirport);
	double addFuel(double fuel);
	double fillUp();//Refuels the aircraft to its full capacity.
	boolean hasFuel(double fuel);//Checks if the aircraft has the specified amount of fuel.
	double getWeightRatio();//Returns the ratio of weight to maximum weight.
}
