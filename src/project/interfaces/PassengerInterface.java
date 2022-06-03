package project.interfaces;

import project.airline.aircraft.PassengerAircraft;
import project.passenger.Passenger;

public interface PassengerInterface {
	double transferPassenger(Passenger passenger, PassengerAircraft toAircraft);
	double loadPassenger(Passenger passenger);
	double unloadPassenger(Passenger passenger);
	boolean isFull();//Checks whether the aircraft is full or not
	boolean isFull(int seatType);//Checks whether a certain seat type is full or not.
	boolean isEmpty();//Checks whether the aircraft is empty or not.
	double getAvailableWeight();//Returns the leftover weight capacity of the aircraft.
	boolean setSeats(int economy, int business, int firstClass);
	boolean setAllEconomy();// Sets every seat to economy.
	boolean setAllBusiness(); //Sets every seat to business.
	boolean setAllFirstClass(); // Sets every seat to first class.
	boolean setRemainingEconomy();//Does not change previously set seats, sets the remaining to economy.
	boolean setRemainingBusiness();// Does not change previously set seats, sets the remaining to business.
	boolean setRemainingFirstClass();// Does not change previously set seats, sets the remaining to first class.
	double getFullness();// Returns the ratio of occupied seats to all seats.


}
