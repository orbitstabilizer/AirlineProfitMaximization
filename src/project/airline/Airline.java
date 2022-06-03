package project.airline;
import java.io.FileWriter;
import java.io.IOException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.PriorityQueue;
import java.util.Stack;

import project.airline.aircraft.concrete.*;
import project.airline.aircraft.*;
import project.airport.*;
import project.passenger.*;

public class Airline {
	private final boolean DEBUG = true;

	/**
	 * input:
	 */
	protected int maxAircraftCount;

	/**
	 * input:
	 */
	protected double operationalCost;

	/**
	 * input:
	 * stores operational costs of different types of aircraft
	 */
	protected double[] AircraftOperationalCoasts;

	/**
	 * input:
	 * used for logging
	 * passed from main
	 */
	protected FileWriter log;


	protected double revenue;
	protected double expenses;


	/**
	 * returns revenue less expenses of airline
	 * @return profit
	 */
	public double getProfits() {
		return revenue - expenses;
	}


	/**
	 * aircraft container, indexed by creation order
	 */
	protected ArrayList<PassengerAircraft> aircraft;


	/**
	 * airport container
	 */
	protected HashMap<Integer, Airport> airports;

	public Airline(int maxAircraftCount, double[] operationalCosts, FileWriter log) {
		this.maxAircraftCount = maxAircraftCount;

		// operationalCosts array keeps airline specific cost as last index
		this.operationalCost = operationalCosts[4];
		this.AircraftOperationalCoasts = operationalCosts;


		this.aircraft = new ArrayList<>();
		this.airports = new HashMap<>();
		this.log = log;

		this.expenses = 0;
		this.revenue = 0;
	}



	/**
	 * flies the aircraft at aircraftIndex to toAirport
	 * if flight can happen no change is made
	 * except increase in expenses the by amount of runningCost
	 * @param toAirport airport to fly to
	 * @param aircraftIndex index of aircraft to fly
	 * @return true if flight is successful
	 */
	private boolean canFly(Airport toAirport, int aircraftIndex) {

		return aircraft.get(aircraftIndex).canFly(toAirport);
	}
	protected boolean fly(Airport toAirport, int aircraftIndex) {
		double runningCost = this.operationalCost * aircraft.size();
		
		if ( canFly(toAirport,aircraftIndex)) {
			this.expenses += runningCost; //TODO: running cost!!
			double flightCost = aircraft.get(aircraftIndex).fly(toAirport);
			this.expenses += flightCost;
			logger(String.format("1 %d %d", toAirport.getID(),aircraftIndex));
			return true;
		}
		else {
			if(DEBUG)System.out.println("fly:cant fly");

		}
		return false;
	}

	/**
	 * Loads passenger at given airport to the aircraft with aircraftIndex
	 * @param passenger passenger to load
	 * @param airport airport to load from
	 * @param aircraftIndex index of aircraft to load to
	 * @return true if passenger is loaded
	 */
	protected boolean loadPassenger(Passenger passenger, Airport airport, int aircraftIndex) {
		boolean isLoadingSuccessful = false;
		PassengerAircraft currAircraft = aircraft.get(aircraftIndex);

		if (airport.containsPassenger(passenger) && currAircraft.isInAirport(airport) && currAircraft.canLoadPassenger(passenger) ) {
			double loadingCost = currAircraft.loadPassenger(passenger);
			expenses += loadingCost;
			isLoadingSuccessful = true;
			logger(String.format("4 %d %d %d",passenger.getID(), aircraftIndex,airport.getID())); 

		}

		return isLoadingSuccessful;

	}

	/**
	 * unload a passenger from a given aircraft
	 * @param passenger passenger to unload
	 * @param aircraftIndex index of aircraft to unload from
	 * @return true if passenger is unloaded
	 */
	protected boolean unloadPassenger(Passenger passenger, int aircraftIndex) {
		boolean isUnloadSuccessful = false;
		PassengerAircraft aircraft = this.aircraft.get(aircraftIndex);
		Airport curAirport = aircraft.getCurrentAirport();
		if (passenger == null) {
			return false;
		}

		boolean canDisembark  = passenger.canDisembark(curAirport);
		if(canDisembark) {// unload operation is successful
			double ticketPrice = aircraft.unloadPassenger(passenger);
			if (ticketPrice<0) {
				expenses -= ticketPrice;
				if(DEBUG)System.out.println("You are logging invalid unload!");// TODO: this should never happen
			}else {
			revenue += ticketPrice;
			}
			isUnloadSuccessful = true;
			logger(String.format("5 %d %d %d",passenger.getID(), aircraftIndex,curAirport.getID()));
		}else {
			if(DEBUG)System.out.println("unload failed "+ passenger.getID());
		}
		return isUnloadSuccessful;

	}

	
	/**
	 * fills up aircraft fuel to its max fuel capacity
	 * @param aircraftIndex aircraft to fuel
	 */
	private void fulle(int aircraftIndex) {
		PassengerAircraft aircraft = this.aircraft.get(aircraftIndex);
		double fuelCost = aircraft.fillUp();
		if (fuelCost != 0) {
			this.expenses += fuelCost;
			double fuel_amount = fuelCost/ this.aircraft.get(aircraftIndex).getCurrentAirport().getFuelCost();
			logger(String.format("3 %d %f", aircraftIndex, fuel_amount));

		}else {
			if(DEBUG)System.out.println("cant fuel");
		}

	}

	
	/**
	 * loads specified amount of fuel to aircraft
	 * @param aircraftIndex aircraft to fuel
	 * @param fuel amount
	 * @return if fuel loading is successful
	 */
	protected boolean loadFuel(int aircraftIndex, double fuel) {
		PassengerAircraft aircraft = this.aircraft.get(aircraftIndex);
		double fuelCost = aircraft.addFuel(fuel);
		if (fuelCost != 0) {
			this.expenses += fuelCost;
			logger(String.format("3 %d %f", aircraftIndex, fuel));
			return true;

		} //			if(DEBUG)System.out.println("cant fuel loadFuel");

		return false;
	}

	
	/**
	 * using distance and extra passenger weight calculates optimum fuel amount
	 * to fly a given distance
	 * @param aircraftIndex aircraft to fuel
	 * @param distance to travel
	 * @param passengerWeight extra weight coming from passengers
	 * @return if fuel loading is successful
	 */
	private boolean fillOptimally(int aircraftIndex,double distance,double passengerWeight) {
		PassengerAircraft a = aircraft.get(aircraftIndex);
		double fuelAmount = a.getOptimalFuel(distance,passengerWeight);
		return loadFuel(aircraftIndex, fuelAmount);
	}



	/*
	 * Aircraft creation functions:
	 */

	/**
	 * creates a PropPassengerAircraft
	 * @param airport airport to create the aircraft at
	 * @return true if aircraft is created
	 */
	protected boolean addPropPassengerAircraft(Airport airport) {
		double operationalCost = AircraftOperationalCoasts[0];
		if (aircraft.size()>= maxAircraftCount)
			return false;
		if (!airport.isFull()) {
			aircraft.add(new PropPassengerAircraft(airport,operationalCost));
			logger(String.format("0 %d %d",airport.getID(), 0));
			return true;
		}
		return false;
	}
	/**
	 * creates a WidebodyPassengerAircraft
	 * @param airport airport to create the aircraft at
	 * @return true if aircraft is created
	 */
	protected boolean addWidebodyPassengerAircraft(Airport airport) {
		double operationalCost = AircraftOperationalCoasts[1];
		if (aircraft.size()>= maxAircraftCount)
			return false;
		if (!airport.isFull() ) {
			aircraft.add(new WidebodyPassengerAircraft(airport,operationalCost));
			logger(String.format("0 %d %d",airport.getID(), 1));
			return true;
		}
		return false;
	}
	/**
	 * creates a RapidPassengerAircraft
	 * @param airport airport to create the aircraft at
	 * @return true if aircraft is created
	 */
	protected boolean addRapidPassengerAircraft(Airport airport) {
		double operationalCost = AircraftOperationalCoasts[2];
		if (aircraft.size()>= maxAircraftCount)
			return false;
		if (!airport.isFull()) {
			aircraft.add(new RapidPassengerAircraft(airport,operationalCost));
			logger(String.format("0 %d %d",airport.getID(), 2));
			return true;
		}

		return false;
	}
	/**
	 * creates a JetPassengerAircraft
	 * @param airport airport to create the aircraft at
	 * @return true if aircraft is created
	 */
	protected boolean addJetPassengerAircraft(Airport airport) {
		double operationalCost = AircraftOperationalCoasts[3];
		if (aircraft.size()>= maxAircraftCount)
			return false;
		if (!airport.isFull()) {
			aircraft.add(new JetPassengerAircraft(airport,operationalCost));
			logger(String.format("0 %d %d",airport.getID(), 3));
			return true;
		}

		return false;
	}

	/*
	 * Seat setting functions:
	 */
	protected void setSeats(int aircraftIndex, int e, int b, int f) {
		PassengerAircraft aircraft = this.aircraft.get(aircraftIndex);
		boolean isSuccessful = aircraft.setSeats(e, b, f);
		if( isSuccessful)
			logger(String.format("2 %d %d %d %d",aircraftIndex,
					aircraft.getEconomySeats(),aircraft.getBusinessSeats(),aircraft.getFirstClassSeats() ));
		else {
			if(DEBUG) System.out.println("invalid seat setting "+ aircraft.getFloorArea()+ " "+ e +  " " + b + " "+ f);
				
				
		}


	}

	/**
	 * creates an Airport of given description
	 * @param type type of airport
	 * @param id id of airport
	 * @param x x coordinate of airport
	 * @param y y coordinate of airport
	 * @param fuelCost fuel cost of airport
	 * @param operationFee operation fee of airport
	 * @param aircraftCapacity capacity of airport
	 */
	public void createAirport(String type, int id, double x, double y, double fuelCost, double operationFee,int aircraftCapacity) {
		switch (type) {
			case "hub" -> airports.put(id, new HubAirport(id, x, y, fuelCost, operationFee, aircraftCapacity));
			case "major" -> airports.put(id, new MajorAirport(id, x, y, fuelCost, operationFee, aircraftCapacity));
			case "regional" -> airports.put(id, new RegionalAirport(id, x, y, fuelCost, operationFee, aircraftCapacity));
		}

	}

	/**
	 * creates a passenger
	 * @param passengerDestinations destinations of passenger
	 * @param type type of passenger
	 * @param id id of passenger
	 * @param weight weight of passenger
	 * @param baggageCount number of baggage of passenger
	 */
	public void createPassenger(String[] passengerDestinations, String type, long id, double weight, int baggageCount) {

		ArrayList<Airport> destinations =new ArrayList<>();

		for (String passengerDestination : passengerDestinations) {
			destinations.add(airports.get(Integer.parseInt(passengerDestination)));
		}
		switch (type) {
			case "first" -> destinations.get(0).addPassenger(new FirstClassPassenger(id, weight, baggageCount, destinations));
			case "business" -> destinations.get(0).addPassenger(new BusinessPassenger(id, weight, baggageCount, destinations));
			case "economy" -> destinations.get(0).addPassenger(new EconomyPassenger(id, weight, baggageCount, destinations));
			case "luxury" -> destinations.get(0).addPassenger(new LuxuryPassenger(id, weight, baggageCount, destinations));
		}
	}

	/**
	 * prints log string to log-file
	 */
	private double prevProfit = 0; // TODO: comment this out
	protected void logger(String logString) {
		try {
//			if(DEBUG)System.out.println(":" + (getProfits()-prevProfit));// TODO: comment this out
			if(DEBUG)log.write(logString+" = "+ (getProfits()-prevProfit) + '\n');// TODO: comment this out
			else log.write(logString+'\n');
			if(DEBUG)prevProfit = getProfits();// TODO: comment this out
//			log.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	/**
	 * execute flights generated by heuristics
	 * if no heuristic flight is found execute emergencyProtocol
	 */
	public void run() {
		boolean hasUnloaded = false;
		ArrayList<Airport> ap = new ArrayList<>(airports.values());
		PriorityQueue<Flight> flights = new PriorityQueue<>();
		Flight goldenFlight = generateFlights(ap, flights);
		double prevProfit =-1;
		int ai = -1;
		int sd = 2;

		while(getProfits()>prevProfit || sd>0) {
			if (ai == maxAircraftCount-1)
				break;
			if (getProfits()<prevProfit)sd--;
			Flight aFlight = flights.poll();
			if(aFlight == null) break;
			if (aFlight.fromAirport.isFull() || aFlight.toAirport.isFull())
				continue;
			boolean flag = false;
			

			switch (aFlight.range) {
				case 0:
					if(addPropPassengerAircraft(aFlight.fromAirport)) {
						ai++;
						flag= true;
					}
					break;
				case 1:
					if(addJetPassengerAircraft(aFlight.fromAirport)){
						ai++;
						flag= true;
					}break;
				case 2:
					if(addRapidPassengerAircraft(aFlight.fromAirport)){
						ai++;
						flag= true;
					}break;
				case 3:
					if(addWidebodyPassengerAircraft(aFlight.fromAirport)){
						ai++;
						flag= true;
					}break;
			}
			if(!flag) break;
			setSeats(ai, aFlight.seatCounts[0], aFlight.seatCounts[1], aFlight.seatCounts[2]);

					
			if (!fillOptimally(ai, aFlight.fromAirport.getDistance(aFlight.toAirport),aFlight.passengerWeight))continue;

			if(!canFly(aFlight.toAirport, ai)) {
				if(DEBUG)System.out.println("cant fly "+ ai);
				continue;
			}
			for(long p: aFlight.toFly) {
				if(!loadPassenger(aFlight.fromAirport.getPassengers().get(p), aFlight.fromAirport, ai)) {
					if(DEBUG)System.out.println("can't load "+ p);
				}
			}
			
			if(fly(aFlight.toAirport, ai)) {
				for(long p: aFlight.toFly) {
					if(unloadPassenger(aircraft.get(ai).getPassengers().get(p), ai)){
						hasUnloaded = true;
					}else{
						if(DEBUG)System.out.println("can't unload "+ p);
					}
				}
			}
			prevProfit = getProfits();
		}
		if (!hasUnloaded && goldenFlight != null ) {
			try {
				emergencyProtocol( goldenFlight );
			} catch (Exception e) {
				if(DEBUG)System.out.println("sad"); // unlucky, graph was not connected :(
			}
			
		}
//		if(DEBUG)System.out.printf("%f\n",getProfits());
		logger(String.format("%f", getProfits()));
	}

	/**
	 * generates the flights according to heuristics:
	 * 	   - prioritize passengers according to their class and destinations
	 * 	   - execute the flights according to their priority
	 * 	   - if two successive flights incur loss stop operation (this guarantees one flight)
	 * 	   if the above algorithm fails to fly any passenger, this means path-finding is needed,
	 * 	   emergency flight goldenFlight is carried
	 * @param ap the airports
	 * @param flights the flights
	 * @return the golden flight
	 */
	private Flight generateFlights(ArrayList<Airport> ap, PriorityQueue<Flight> flights) {
		Flight goldenFlight = null;
		for (Airport fa : ap) {
			if (fa.isFull()) continue;

			HashMap<Integer, ArrayList<ArrayList<Long>>> pl = fa.prioritizePassengers();

			for (int aid : pl.keySet()) {
				Airport to = airports.get(aid);

				if (to.isFull()) continue;

				if (fa.getDistance(to) < 2000) { // prop

					flights.add(new Flight(0, pl.get(aid), 60, fa, to));

				} else if (fa.getDistance(to) < 5000) {// jet
					flights.add(new Flight(1, pl.get(aid), 30, fa, to));

				} else if (fa.getDistance(to) < 7000) {

					flights.add(new Flight(2, pl.get(aid), 120, fa, to));


				} else if (fa.getDistance(to) < 14000) {
					flights.add(new Flight(3, pl.get(aid), 450, fa, to));

				} else {
					Flight tmpFlight = new Flight(4, pl.get(aid), 450, fa, to);
					if(goldenFlight == null)
						goldenFlight = tmpFlight;
					else if (goldenFlight.toFly.size()==0&&pl.get(aid).size()!=0)
						goldenFlight = tmpFlight;
					else if (goldenFlight.fromAirport.getDistance(goldenFlight.toAirport) >fa.getDistance(to))
						goldenFlight = tmpFlight;
					
				}

			}


		}
		return goldenFlight;
	}

	/**
	 * emergency protocol: guarantees that at least one passenger is unloaded
	 * @param goldenFlight the golden flight
	 */
	private void emergencyProtocol(Flight goldenFlight) {
		Airport toA = goldenFlight.toAirport;
		Airport fromA = goldenFlight.fromAirport;
		Stack<Airport> pathList;
		PathFinder pf = new PathFinder(airports);
		pathList = pf.generatePath(fromA,toA);
		if (addWidebodyPassengerAircraft(fromA)){
			int ai = aircraft.size()-1;
			
			setSeats(ai, goldenFlight.seatCounts[0], goldenFlight.seatCounts[1], goldenFlight.seatCounts[2]);
			boolean loadedOne = false;
			int i =0;
			for(long p: goldenFlight.toFly) {
				if(!loadPassenger(goldenFlight.fromAirport.getPassengers().get(p), goldenFlight.fromAirport, ai)) {
					if(DEBUG)System.out.println("can't load "+ p);
				}else {
					loadedOne = true;i++;
				}
				if(i== 13) 
					break;
			}
			
			if(loadedOne) {
				while (!pathList.isEmpty()) {
					Airport next = pathList.pop();
					fulle(ai);
					if(!fly(next, ai)){
						if(DEBUG)System.out.println("can't fly");
						if(DEBUG)System.out.println(next);
					}

				}
				int k = 0;
				for(long p:goldenFlight.toFly ) {
					if(unloadPassenger(aircraft.get(ai).getPassengers().get(p), ai)){
						k++;
					}else{
						if(DEBUG)System.out.println("can't unload "+ p);
					}
					if(k==13) 
						break;
				}
			}
		}

	}





}



