package project.passenger;

import java.util.ArrayList;

import project.airport.Airport;

public abstract class Passenger implements Comparable<Passenger> {
	
	private final long ID;
	private final double weight;
	private final int baggageCount;
	private final ArrayList<Airport> destinations;

	protected double seatMultiplier;
	protected double connectionMultiplier;

	public int getBaggageCount() {
		return this.baggageCount;
	}


	protected PassengerType passengerType;
	protected SeatType seatType;



	protected Airport previousAirport;// last airport disembarked

	protected enum PassengerType{
		Economy(0),Business(1),FirstClass(2), Luxury(3);

		public final int value;
		PassengerType(int i) {
			this.value = i;
		}
	}
	protected enum SeatType{
		Economy(0),Business(1),FirstClass(2);

		public final int value;
		SeatType(int i) {
			this.value = i;
		}
	}

	protected Passenger(Long id, double weight, int baggageCount, ArrayList<Airport> destinations, PassengerType passengerType) {
		this.ID = id;
		this.weight = weight;
		this.baggageCount = baggageCount;
		this.destinations = destinations;
		this.seatMultiplier = 1;
		this.connectionMultiplier = 1;
		this.previousAirport = destinations.get(0);
		this.passengerType = passengerType;

	}

	public boolean board(int seatType) {
		if (seatType>passengerType.value) {
			return false;
		}

		switch (seatType) {
			case 0 -> {// economy
				this.seatMultiplier = 0.6;
				this.seatType = SeatType.Economy;
			}
			case 1 -> {// business
				this.seatMultiplier = 1.2;
				this.seatType = SeatType.Business;
			}
			default -> {// first-class
				this.seatMultiplier = 3.2;
				this.seatType = SeatType.FirstClass;
			}
		}
		return true;
	}

	/*
	 * If the airport is not a future destination,
	 * this method should return 0 and not perform any operation.
	 *  If not, it should call calculateTicketPrice() and return this price.
	 *   Before returning, it should set the airport and reset the necessary multipliers.
	 */
	public double disembark(Airport airport, double aircraftTypeMultiplier) {
		double ticketPrice = 0;

		if (destinations.contains(airport)) {
			int currIndex = destinations.indexOf(airport);
			int prevIndex = destinations.indexOf(previousAirport);
			if (currIndex >= prevIndex) {// is future or current
				ticketPrice = calculateTicketPrice(airport, aircraftTypeMultiplier);
				this.previousAirport = airport;
				seatMultiplier = 1;

			}

		}

		return ticketPrice;

	}

	public boolean connection(int seatType) {
		if (seatType>passengerType.value) {
			return false;
		}

		switch (seatType) {
			case 0 -> this.seatMultiplier *= 0.6;// economy
			case 1 -> this.seatMultiplier *= 1.2;// business
			default -> this.seatMultiplier *= 3.2;// first-class
		}
		connectionMultiplier *=  0.8;

		return true;

	}

	private static final double[][] airportMultipliers =
			  {{0.5, 0.7, 1  },
			   {0.6, 0.8, 1.8},
			   {0.9, 1.6, 3.0} };// H M R->H M R


	protected double calculateAirportMultiplier(Airport toAirport) {
		int prev = this.previousAirport.getAirportType();
		int next = toAirport.getAirportType();
		return airportMultipliers[prev][next];

	}
	abstract protected double calculateTicketPrice(Airport toAirport, double aircraftTypeMultiplier);


	public double getWeight() {
		return this.weight;
	}
	public long getID() {
		return this.ID;
	}
	public int getPassengerType() {
		return this.passengerType.value;
	}
	public int getSeatType() {
		return this.seatType.value;
	}

	public Airport getLastDestination() {
		if(destinations.size() > 1) // TODO: check profitability
			return destinations.get(1);
		else
			return destinations.get(destinations.size()-1);
	}

	public boolean canDisembark(Airport airport) {
		if (destinations.contains(airport)) {
			int currIndex = destinations.indexOf(airport);
			int prevIndex = destinations.indexOf(previousAirport);
			return currIndex >= prevIndex;// is future or current

		}
		return false;
	}
	@Override
	public String toString() {
		return "Passenger [ID=" + ID + ", weight=" + weight + ", baggageCount=" + baggageCount + ", destinations="
				+ destinations + ", passengerType=" + passengerType
				+ ", seatType=" + seatType + ", previousAirport=" + previousAirport + "]";
	}

	protected double ticketPriceCalculator(Airport toAirport, double aircraftTypeMultiplier, double passengerMultiplier) {
		double airportMultiplier = this.calculateAirportMultiplier(toAirport);
		double distance = toAirport.getDistance(previousAirport);
		double ticketPrice = distance* aircraftTypeMultiplier *connectionMultiplier*seatMultiplier*airportMultiplier* passengerMultiplier;
		ticketPrice *= (1+getBaggageCount()*0.05);
		
		

		return ticketPrice;
	}
	
	@Override
	public int compareTo(Passenger o) {
		ArrayList<Double> cmp1 = new ArrayList<>(),cmp2 = new ArrayList<>();
		cmp1.add((double)getPassengerType());
		cmp1.add((double)baggageCount);
		cmp1.add((double)-weight);
		cmp1.add((double)ID);
		cmp2.add((double)o.getPassengerType());
		cmp2.add((double)o.getBaggageCount());
		cmp2.add((double)-o.getWeight());
		cmp2.add((double)o.getID());
		
		for (int i = 0 ;i< 3; i++) {
			int res = cmp1.get(i).compareTo(cmp2.get(i));
			if(res != 0)
				return -res;
		}
		return 0;	
		
	}





}
