package project.airport;

import project.airline.aircraft.Aircraft;

public class HubAirport extends Airport {

	public HubAirport(int iD, double x, double y, double fuelCost, double operationFee, int aircraftCapacity) {
		super(iD, x, y, fuelCost, operationFee, aircraftCapacity,AirportType.Hub);

	}

	/**
	 * This method should do the necessary departure operations. Returns the departure fee.
	 * @return departureFee
	 */
	@Override
	public double departAircraft(Aircraft aircraft) {

		double fullnessCoeff = this.getFullnessCoeff();
		double aircraftWeightRatio = aircraft.getWeightRatio();
		double departureFee = this.operationFee * aircraftWeightRatio * fullnessCoeff * 0.7;
		
		
		this.airCraftCount--;

		return departureFee;

	}

	/**
	 * This method should do the necessary landing operations. Returns the landing fee.
	 * @return landingFee
	 */
	@Override
	public double landAircraft(Aircraft aircraft) {


		double fullnessCoeff = this.getFullnessCoeff();
		double aircraftWeightRatio = aircraft.getWeightRatio();
		double landingFee = operationFee * aircraftWeightRatio * fullnessCoeff * 0.8 ;
		

		this.airCraftCount++;
		return landingFee;
	}

}
