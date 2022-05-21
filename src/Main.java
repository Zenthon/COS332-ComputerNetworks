import java.util.*;
import java.io.*;


public class Main {

    public static void main(String[] args) {

        // Track whether the file has been changed
        TimerTask timerTask = new FileWatcher(new File("index.html")) {
            @Override
            protected void onChange(File file) {
                System.out.println("File " + file.getName() + " has changed.");
                // Upload the changes to the apache server

            }
        };

        Timer timer = new Timer();
        timer.schedule(timerTask, new Date(), 1000);
    }
}
