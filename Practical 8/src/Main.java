import java.net.UnknownHostException;
import java.util.*;
import java.io.*;

public class Main {

    public static void main(String[] args) throws UnknownHostException, IOException {
        FTP ftp = new FTP("localhost");
        String fileName = "index.html";

        System.out.println("Watching: " + fileName);
        // Track whether the file has been changed
        TimerTask timerTask = new FileWatcher(new File(fileName)) {
            @Override
            protected void onChange(File file) {
                System.out.println("File " + file.getName() + " has changed.");
                // Upload the changes to the apache server
                try {
                    ftp.upload(fileName);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };

        Timer timer = new Timer();
        timer.schedule(timerTask, new Date(), 1000);
    }
}
