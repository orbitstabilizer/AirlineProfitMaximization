package project.executable;

import java.io.FileWriter;
import java.io.IOException;

import project.airline.Airline;
import project.io.InputReader;


public class Main {
	public static void main(String[] args) {
		FileWriter log;
		InputReader reader = new InputReader();
		Airline loungeAirline;
		try {
			log = new FileWriter(args[1]);
			loungeAirline = reader.readInput(args[0], log);

			if (loungeAirline != null) {
				loungeAirline.run();

			}
			log.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}





