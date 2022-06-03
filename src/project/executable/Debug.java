package project.executable;

import project.debug.Debugger;
import project.io.InputReader;

import java.io.FileWriter;
import java.io.IOException;

public class Debug {
    public static void main(String[] args) {
        FileWriter log;
        InputReader reader = new InputReader();
        Debugger loungeAirline;
        try {
            log = new FileWriter(args[1]);
            reader.setDebugMode(true);
            loungeAirline = (Debugger)reader.readInput(args[0],log );
            loungeAirline.reproduceLog(args[2]);
            log.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
