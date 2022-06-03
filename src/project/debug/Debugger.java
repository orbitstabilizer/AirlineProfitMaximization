package project.debug;

import project.airline.Airline;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class Debugger extends Airline {

    public Debugger(int maxAircraftCount, double[] operationalCosts, FileWriter log) {
        super(maxAircraftCount, operationalCosts, log);
        this.maxAircraftCount = maxAircraftCount;

        this.operationalCost = operationalCosts[4];
        this.AircraftOperationalCoasts = operationalCosts;


        this.aircraft = new ArrayList<>();
        this.airports = new HashMap<>();
        this.log = log;

        this.expenses = 0;
        this.revenue = 0;
    }


    /**
     * WARNING: this is for debugging purposes, don't use externally!
     * @param fileName path of log file to be reproduced
     */
    public void reproduceLog(String fileName) {
        String operationString  = "";

        File inputFile = new File(fileName);
        int line = 1;
        try (Scanner sc = new Scanner(inputFile)) {
            while(sc.hasNext()) {

                operationString  = sc.nextLine();
                try (Scanner ls = new Scanner(operationString)) {
                    if(!ls.hasNextInt()) {
                        break;
                    }
                    int opType = ls.nextInt();line++;

                    switch (opType) {
                        case 0 -> {

                            int airportId0 = ls.nextInt();
                            int aircraftType = ls.nextInt();
                            if(airports.get(airportId0).isFull()) System.out.println("Failed to create aircraft due to aircraft size limit");
                            switch (aircraftType) {
                                case 0 : if(!addPropPassengerAircraft(airports.get(airportId0)) ) System.out.println("couldn't create aircraft at "+airportId0);break;
                                case 1 : if(! addWidebodyPassengerAircraft(airports.get(airportId0)))System.out.println("couldn't create aircraft at "+airportId0);break;
                                case 2 : if(!addRapidPassengerAircraft(airports.get(airportId0)))System.out.println("couldn't create aircraft at "+airportId0);break;
                                case 3 : if(!addJetPassengerAircraft(airports.get(airportId0)))System.out.println("couldn't create aircraft at "+airportId0);break;
                            }


                        }
                        case 1 -> {
                            int airportId1 = ls.nextInt();
                            int aircraftIndex = ls.nextInt();
                            fly(airports.get(airportId1), aircraftIndex);
                        }
                        case 2 -> {

                            int aircraftIndex2 = ls.nextInt();
                            int e = ls.nextInt();
                            int b = ls.nextInt();
                            int f = ls.nextInt();
                            setSeats(aircraftIndex2, e, b, f);
                        }
                        case 3 -> {
                            int aircraftIndex3 = ls.nextInt();
                            double fuel_amount = ls.nextDouble();
                            loadFuel(aircraftIndex3, fuel_amount);
                        }
                        case 4 -> {
                            long passId = ls.nextLong();
                            int aircraftInd = ls.nextInt();
                            int airportId = ls.nextInt();
                            loadPassenger(airports.get(airportId).getPassengers().get(passId), airports.get(airportId), aircraftInd);
                        }
                        case 5 -> {
                            long passId5 = ls.nextLong();
                            int aircraftInd5 = ls.nextInt();

                            unloadPassenger(aircraft.get(aircraftInd5).getPassengers().get(passId5), aircraftInd5);
                        }
                        default -> System.out.println("debugger can't handle this op-type "+ opType);
                    }
                }

            }
        }
        catch (Exception e) {
            System.out.println("ERROR: at line "+ line);
            System.out.println("operation: " + operationString);
            e.printStackTrace();
        }
        logger(String.format("%f", getProfits()));
        System.out.printf("%f\n",getProfits());

    }
}
