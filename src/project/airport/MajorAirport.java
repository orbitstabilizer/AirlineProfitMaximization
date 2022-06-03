package project.airport;

import project.airline.aircraft.Aircraft;

public class MajorAirport extends Airport {

	public MajorAirport(int iD, double x, double y, double fuelCost, double operationFee, int aircraftCapacity) {
		super(iD, x, y, fuelCost, operationFee, aircraftCapacity,AirportType.Major);
	}

	/**
	 * This method should do the necessary departure operations. Returns the departure fee.
	 * @return departureFee
	 */
	@Override
	public double departAircraft(Aircraft aircraft) {

		double fullnessCoeff = this.getFullnessCoeff();
		double aircraftWeightRatio = aircraft.getWeightRatio();
		double departureFee = this.operationFee * aircraftWeightRatio * fullnessCoeff * 0.9;
		

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
		double landingFee = operationFee * aircraftWeightRatio * fullnessCoeff * 1.0 ;
		

		this.airCraftCount++;
		return landingFee;
	}

}
