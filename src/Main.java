
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        System.out.println("House Alarm System Activated. Sensors On!");
        System.out.println("The Door is connected to the ENTER key. Opening the door will cause ENTER key to be triggered.");
        System.out.println("Waiting for door to open........");

        System.in.read();
        System.out.println("Door has been opened. Sensors and alarm triggered.");


    }
}