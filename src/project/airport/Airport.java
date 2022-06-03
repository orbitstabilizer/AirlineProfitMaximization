package project.airport;

import java.util.ArrayList;
import java.util.HashMap;

import project.airline.aircraft.Aircraft;
import project.passenger.Passenger;


public abstract class Airport {
	
	
	private final int ID;
	private final double x, y;// Coordinates of the airport. Used when calculating distance.

	protected double fuelCost; // Price of fuel in this airport.
	protected double operationFee;// The fee paid to this airport for certain operations.
	protected int aircraftCapacity;// The maximum number of aircraft this airport can hold.

	protected int airCraftCount;
	protected HashMap<Long,Passenger> passengers;
	private final AirportType airportType;

	protected enum AirportType{
		Hub(0),Major(1),Regional(2);
		public final int value;
		AirportType(int i) {
			this.value = i;
		}
	}



	/**
	 * Returns fullness coefficient of the airport
	 * @return fullnessCoefficient
	 */
	protected double getFullnessCoeff() {
		double aircraftRatio = (double)this.airCraftCount/this.aircraftCapacity;
		return 0.6*Math.pow(Math.E, aircraftRatio);//fullness coefficient
	}

	/**
	 * Returns fuel cost of this airport
	 * @return fuelCost
	 */
	public double getFuelCost() {
		return fuelCost;
	}


	/**
	 * @param iD the iD of the airport
	 * @param x the x coordinate of the airport
	 * @param y the y coordinate of the airport
	 * @param fuelCost the price of fuel in this airport
	 * @param operationFee the fee paid to this airport for certain operations
	 * @param aircraftCapacity the maximum number of aircraft this airport can hold
	 */
	public Airport(int iD, double x, double y, double fuelCost, double operationFee, int aircraftCapacity,AirportType airportType) {
		this.ID = iD;
		this.x = x;
		this.y = y;
		this.fuelCost = fuelCost;
		this.operationFee = operationFee;
		this.aircraftCapacity = aircraftCapacity;
		this.passengers = new HashMap<>();
		this.airportType = airportType;
	}


	/**
	 * Does the necessary departure operations and returns the departure fee.
	 * @param aircraft the aircraft that is departing
	 * @return departure fee
	 */
	public abstract double departAircraft(Aircraft aircraft);

	/**
	 * Does the necessary landing operations and returns the landing fee.
	 * @param aircraft the aircraft that is landing
	 * @return landing fee
	 */
	public abstract double landAircraft(Aircraft aircraft);


	/**
	 * Checks if airport contains maximum amount of aircraft
	 * @return true if airport is full, false otherwise
	 */
	public boolean isFull() {
		return (this.aircraftCapacity <= this.airCraftCount);
	}

	/**
	 * Checks if two airports are the same
	 * @param  other the other airport
	 * @return if the airports are the same
	 */
	public boolean equals(Airport other) {
		return (this.ID == other.ID);
	}

	/**
	 * Returns the distance between the current airport and the next airport
	 * @param airport the next airport
	 * @return distance between the current airport and the next airport
	 */
	public double getDistance(Airport airport) {
		return Math.sqrt(Math.pow(this.x - airport.x,2) +Math.pow(this.y - airport.y,2) );
	}

	/**
	 * Adds the passenger to the airport
	 * @param passenger the passenger to be added
	 */
	public void addPassenger(Passenger passenger) {
		getPassengers().put(passenger.getID(), passenger);

	}

	/**
	 * Removes the passenger from the airport
	 * @param passenger the passenger to be removed
	 */
	public void removePassenger(Passenger passenger) {
		getPassengers().remove(passenger.getID());
	}

	/**
	 * increases the number of aircraft in the airport, used on aircraft creation
	 */
	public void incAircraftCount() {
		airCraftCount++;
	}


	public int getAirportType() {
		return airportType.value;
	}

	public int getID() {
		return this.ID;
	}

	/**
	 * @return if the passenger is in the airport
	 */
	public boolean containsPassenger(Passenger passenger) {
		return getPassengers().containsKey(passenger.getID());
	}
	@Override
	public String toString() {
		return String.format("(%d)", ID);
	}

	/**
	 * sorts the passengers by their priority
	 * @return the sorted passengers
	 */
	public HashMap<Integer,ArrayList<ArrayList<Long>> >  prioritizePassengers() {
		HashMap<Integer,ArrayList<ArrayList<Long>> > sortedPass = new HashMap<>();
		for(Passenger passenger : getPassengers().values()) {
			int id = passenger.getLastDestination().getID();
			if (sortedPass.containsKey(id)) {
				sortedPass.get(id).get(passenger.getPassengerType()).add(passenger.getID());
			}
			else {
				sortedPass.put(id, new ArrayList<>());
				for (int i=0 ; i< 4; i++) {
					sortedPass.get(id).add(new ArrayList<>());
				}

				sortedPass.get(id).get(passenger.getPassengerType()).add(passenger.getID());
			}

		}
		return sortedPass;

	}

	public HashMap<Long,Passenger> getPassengers() {
		return passengers;
	}







}
