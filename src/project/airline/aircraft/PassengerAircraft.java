package project.airline.aircraft;

import java.util.HashMap;

import project.airport.Airport;
import project.interfaces.PassengerInterface;
import project.passenger.Passenger;

/*
 * This class will hold functions related to passenger operations for aircraft.
 * Passenger objects must be held in this class if they are loaded in.
 * Passenger objects should switch between airport and aircraft objects;
 * they should not be stored anywhere else.
 * You can use any collection you like to store passenger objects.
 */
public abstract class PassengerAircraft extends Aircraft implements PassengerInterface{

	protected double floorArea;

	// Count of seats assigned for this aircraft for each seat type.
	private int economySeats, businessSeats, firstClassSeats;

	public int getEconomySeats() {
		return economySeats;
	}


	public int getBusinessSeats() {
		return businessSeats;
	}


	public int getFirstClassSeats() {
		return firstClassSeats;
	}
	// Count of seats that are occupied for each seat type.
	private int occupiedEconomySeats, occupiedBusinessSeats, occupiedFirstClassSeats;


	protected HashMap<Long,Passenger> passengers;

	public PassengerAircraft(Airport airport,double operationFee) {
		this.operationFee = operationFee;
		this.passengers = new HashMap<>();
		this.currentAirport = airport;
		airport.incAircraftCount();
	}





	/* A passenger cannot be loaded into an aircraft
	 *  if the aircraft does not have seats for that passenger
	 *  or if the aircraft exceeds the maximum weight limit with the addition of that passenger.
	 *  Passenger weight calculation will be explained in the passenger class.
	 *  Passengers cannot sit in seats of higher class.
	 *  However, if they have to, they can sit in lower-class seats.
	 *  Do not forget that the priority is always the seat of higher class
	 *  when a passenger is assigned a seat.
	 */

	private int findSeatFor(Passenger passenger) {
		int seatType = -1;

		switch(passenger.getPassengerType()) {
			case 0:
				if(this.economySeats> this.occupiedEconomySeats) {
					seatType = 0;
				}
				break;

			case 1:
				if(this.businessSeats> this.occupiedBusinessSeats) {
					seatType = 1;
				}else if (this.economySeats> this.occupiedEconomySeats ){
					seatType = 0;
				}
				break;

			default:
				if(this.firstClassSeats> this.occupiedFirstClassSeats) {
					seatType = 2;
				}
				else if(this.businessSeats> this.occupiedBusinessSeats) {
					seatType = 1;
				}else if (this.economySeats> this.occupiedEconomySeats ){
					seatType = 0;
				}

		}
		return seatType;
	}
	public boolean canLoadPassenger(Passenger passenger) {
		boolean haveAvailableSeat = (findSeatFor(passenger)!= -1);

		return (haveAvailableSeat && this.weight + passenger.getWeight()<= maxWeight);

	}
	/*
	 * This method loads the passenger to the appropriate seat.
	 * It should return the loading fee. Loading fees are based on the operationFee,
	 * which is a fee specific to each type of aircraft.
	 * If the loading operation cannot be completed return value should only be the operationFee.
	 * If the loading operation is completed, the complete loading fee is returned.
	 *
	 * If you do not want to pay the operationFee every time you do an invalid loading operation,
	 * you can create a boolean method that will check if the operation is valid.
	 * If you use both methods together, you can avoid unnecessary expenses.
	 */
	@Override
	public double loadPassenger(Passenger passenger) {// visibility

		int seatType = findSeatFor(passenger);
		double loadingFee = operationFee;

		double seatConstant;
		switch (seatType) {
			case 0:
				seatConstant = 1.2;break;
			case 1:
				seatConstant = 1.5; break;
			case 2:
				seatConstant = 2.5; break;
			default:
				return loadingFee;
		}

		boolean boardComplete = passenger.board(seatType);
		if (boardComplete) {
			switch (seatType) {
				case 0 -> occupiedEconomySeats++;
				case 1 -> occupiedBusinessSeats++;
				case 2 -> occupiedFirstClassSeats++;
			}
			loadingFee = operationFee*aircraftTypeMultiplier*seatConstant;
			this.addPassenger(passenger);
			this.currentAirport.removePassenger(passenger);
			this.weight += passenger.getWeight();
		}

		return loadingFee;

	}
	/*
	 * This method unloads the passenger.
	 * A passenger can be unloaded if it can disembark at the aircraft’s airport.
	 * This is where the ticket revenue will be calculated and collected.
	 * Ticket price is calculated by multiplying disembarkation revenue with the seat constant.
	 * Disembarkation revenue will be calculated in the passenger classes.
	 *
	 * If the passenger was seated in economy, the seat constant is 1.0;
	 * if the passenger was seated in business, the seat constant is 2.8;
	 * if the passenger was seated in first-class, the seat constant is 7.5.
	 *
	 * If the passenger cannot disembark, you should return the operationFee.
	 * So, this method can return both revenue and expense, do not forget to consider this.
	 */
	@Override
	public double unloadPassenger(Passenger passenger) {// visibility
		double disembRevenue = passenger.disembark(currentAirport, aircraftTypeMultiplier);
		if (disembRevenue != 0) {
			double seatConstant = 0;
			switch (passenger.getSeatType()) {
				case 0 -> {
					seatConstant = 1.0;
					occupiedEconomySeats--;
				}
				case 1 -> {
					seatConstant = 2.8;
					occupiedBusinessSeats--;
				}
				case 2 -> {
					seatConstant = 7.5;
					occupiedFirstClassSeats--;
				}
			}
			double ticketPrice = disembRevenue * seatConstant;
			this.weight -= passenger.getWeight();
			this.removePassenger(passenger);
			this.currentAirport.addPassenger(passenger);
			return ticketPrice;
		}else {
			return -operationFee;
		}

	}
	/*
	 * This method transfers the passenger from the current aircraft to the toAircraft.
	 * If the passenger cannot disembark in an airport, you can use this method.
	 * When moving a passenger, it acts as a bypass to the unloadPassenger() operation.
	 *
	 * The implementation of this method is very similar to the loadPassenger operation;
	 * the only difference is this operation is between aircraft
	 * rather than between an aircraft and an airport.
	 *
	 * If the transfer operation is invalid, you should return the operationFee as an expense.
	 * If the transfer operation is valid, then you should return the loading fee.
	 * This loading fee is what you get when you use the loadPassenger() method above.
	 */
	@Override
	public double transferPassenger(Passenger passenger, PassengerAircraft toAircraft) {
		int type = passenger.getPassengerType();
		boolean boarded = passenger.board(type);
		double loadingFee = operationFee;
		if (boarded) { 
			double constant = switch (type) {
				case 0 -> 1.2;// economy
				case 1 -> 1.5;// business
				default -> 2.5; // first-class

			};

			loadingFee = operationFee*aircraftTypeMultiplier*constant;
			this.removePassenger(passenger); // TODO: passenger must be in the aircraft
			this.weight -= passenger.getWeight();

			toAircraft.addPassenger(passenger);
			toAircraft.weight += passenger.getWeight();
		}

		return loadingFee;

	}

	public int getOccupiedSeats() {
		return this.occupiedBusinessSeats + this.occupiedEconomySeats + this.occupiedFirstClassSeats;
	}
	public int getSeatCount() {
		return this.businessSeats + this.economySeats + this.firstClassSeats;
	}

	/* You can set and reset seats as much as you want;
	 * however, you should ensure that the seat you are deleting is empty.
	 * If not, you should unload that passenger first.
	 */
	@Override
	public boolean isFull() {
		return (occupiedEconomySeats == economySeats &&
				occupiedBusinessSeats == businessSeats &&
				occupiedFirstClassSeats == firstClassSeats);
	}
	@Override
	public boolean isFull(int seatType) {
		return switch (seatType) {
			case 0 -> occupiedEconomySeats == economySeats;
			case 1 -> occupiedBusinessSeats == businessSeats;
			case 2 -> occupiedFirstClassSeats == firstClassSeats;
			default -> true;
		};

	}
	@Override
	public boolean isEmpty() {
		return (occupiedEconomySeats == 0 &&
				occupiedBusinessSeats == 0 &&
				occupiedFirstClassSeats == 0);
	}
	@Override
	public double getAvailableWeight() {
		return this.maxWeight - this.weight;
	}

	/*
	 * Economy seats have an area of 1,
	 *  business seats have an area of 3,
	 *  and first-class seats have an area of 8.
	 */
	@Override
	public boolean setSeats(int economy, int business, int firstClass) {


		int area= economy + business*3 + firstClass*8;
		if (area <= this.floorArea) {
			this.economySeats = economy;
			this.businessSeats = business;
			this.firstClassSeats = firstClass;
			return true;
		}

		return false;
	}
	@Override
	public boolean setAllEconomy() {
		if (this.firstClassSeats == 0 && this.businessSeats == 0) {
			this.economySeats =(int) this.floorArea;
			return true;
		}
		return false;
	}
	@Override
	public boolean setAllBusiness() {
		if (this.firstClassSeats == 0 && this.economySeats == 0) {
			this.businessSeats =(int) this.floorArea/3;
			return true;
		}
		return false;
	}
	@Override
	public boolean setAllFirstClass() {
		if (this.businessSeats == 0 && this.economySeats == 0) {
			this.firstClassSeats =(int) this.floorArea/8;
			return true;
		}
		return false;
	}
	@Override
	public boolean setRemainingEconomy() {
		double remainingArea = this.floorArea - (this.economySeats + this.businessSeats*3 + this.firstClassSeats*8);
		this.economySeats += (int)remainingArea;
		return true;
	}
	@Override
	public boolean setRemainingBusiness() {
		double remainingArea = this.floorArea - (this.economySeats + this.businessSeats*3 + this.firstClassSeats*8);
		this.businessSeats += (int)(remainingArea/3);
		return true;
	}
	@Override
	public boolean setRemainingFirstClass() {
		double remainingArea = this.floorArea - (this.economySeats + this.businessSeats*3 + this.firstClassSeats*8);
		this.businessSeats += (int)(remainingArea/8);
		return true;
	}


	@Override
	public double getFullness() {
		return (double)getOccupiedSeats()/getSeatCount();
	}

	private void addPassenger(Passenger passenger) {
		long id = passenger.getID();
		getPassengers().put(id,passenger);

	}
	private void removePassenger(Passenger passenger) {
		long id = passenger.getID();
		getPassengers().remove(id,passenger);
	}


	public HashMap<Long,Passenger> getPassengers() {
		return passengers;
	}

	protected double flightCostCalculator(Airport toAirport, double FlightOpearationCostConstant) {
		double landingCost, departureCost, operationCost;
		departureCost = currentAirport.departAircraft(this);
		

		landingCost = toAirport.landAircraft(this);
		
		// opeartionCost
		double fullness, distance, constant;
		distance = toAirport.getDistance(currentAirport);
		fullness = (double) this.getOccupiedSeats()/this.getSeatCount();
		constant = FlightOpearationCostConstant;
		
		operationCost = constant*fullness*distance;
		
//		System.out.printf("d %f dep %f la %f f %f c %f op %f ",distance,departureCost,landingCost,fullness,constant,operationCost);
		return landingCost + departureCost + operationCost;
	}


	public double getFloorArea() {
		
		return floorArea;
	}


}
