package project.airline;
import project.airport.Airport;
import project.passenger.Passenger;

import java.util.*;
public class Flight implements Comparable<Flight>{
	int[] seatCounts = {0,0,0};
	double[] seatArea = {1,3,8,8};
	int range;
	List<Long> toFly;
	Airport fromAirport;
	Airport toAirport;
	double passengerWeight;

	public Flight(int range,ArrayList<ArrayList<Long>> pl , double area,Airport fromAirport, Airport toAirport) {
		this.range = range;
		this.fromAirport = fromAirport;
		this.toAirport = toAirport;
		toFly = new ArrayList<>();
		generateFlights(pl, area, fromAirport);

	}
	public Flight(int range,ArrayList<ArrayList<Long>> pl , double area,Airport fromAirport, Airport toAirport,boolean alternative){
		this.range = range;
		this.fromAirport = fromAirport;
		this.toAirport = toAirport;
		toFly = new ArrayList<>();
		generateFlightsAlternative(pl, area, fromAirport);
	}


	private void generateFlights(ArrayList<ArrayList<Long>> pl, double area, Airport fromAirport) {
		ArrayList<Long> L = pl.get(3);
		L.addAll(pl.get(2));
		L.addAll(pl.get(1));
		L.addAll(pl.get(0));
		for (long p : L) {
			int type = fromAirport.getPassengers().get(p).getPassengerType();
			if(seatArea[type]<= area) {
				if(type == 3){
					seatCounts[2]++;
					area -= seatArea[type];
				}else {
					seatCounts[type]++;
					area -= seatArea[type];
				}

			}
		}
		if(seatCounts[2]+ seatCounts[1]>0) {//Heuristic
			toFly =  L.subList(0, seatCounts[2]+ seatCounts[1]);
		}else if (seatCounts[0]!= 0)  {
			toFly.add(L.get(0));
		}

		passengerWeight = 0;
		for(long pid: toFly)
			passengerWeight += fromAirport.getPassengers().get(pid).getWeight();
	}
	//TODO: this is surprisingly unprofitable, why?
	private void generateFlightsAlternative(ArrayList<ArrayList<Long>> pl, double area, Airport fromAirport) {
		ArrayList<Long> L = pl.get(3);
		L.addAll(pl.get(2));
		L.addAll(pl.get(1));
		L.addAll(pl.get(0));


		PriorityQueue<Passenger> pQueue = new PriorityQueue<>();
		for(long id : L) {
			pQueue.add(fromAirport.getPassengers().get(id));
		}
		//TODO: this is surprisingly unprofitable, why?
		boolean addedPassenger = false;
		while (!pQueue.isEmpty()) {
			var p = pQueue.poll();
			int type = p.getPassengerType();
			if(seatArea[type]<=area) {
				if(type == 3 || type == 2){
					seatCounts[2]++;
					area -= seatArea[type];
					toFly.add(p.getID());
					addedPassenger =true;

				}else if (!addedPassenger ||( type == 1 && p.getBaggageCount()>5)){
					seatCounts[type]++;
					area -= seatArea[type];
					toFly.add(p.getID());
					addedPassenger =true;
				}

			}else {
				break;
			}
			if(type == 0) break;

		}

		passengerWeight = 0;
		for(long pid: toFly)
			passengerWeight += fromAirport.getPassengers().get(pid).getWeight();
	}



	// Heuristic function:
	Double profitability() {
//			return (int) ( (-seatCounts[0] + seatCounts[1]*2 + seatCounts[2]*10)/(Math.pow(Math.E, -toAirport.getDistance(fromAirport)/3169)));
//			return (int) ( (-seatCounts[0] + seatCounts[1]*2 + seatCounts[2]*10)/(Math.pow(31, (range-1)*(range-2)*(range) ) ));
		return  ((fromAirport.getDistance(toAirport)/(3169))* (-seatCounts[0] + seatCounts[1]*3 + seatCounts[2]*8)/(Math.pow(1000,(range-2)* (range-1)*(range))));
	}


	@Override
	public int compareTo(Flight o) {
		return -profitability().compareTo(o.profitability());
	}

	@Override
	public String toString() {
		return  Arrays.toString(seatCounts);
	}

}
