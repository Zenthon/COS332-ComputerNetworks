import java.io.File;
import java.util.*;

public abstract class FileWatcher extends TimerTask {
    private long timestamp;
    private File indexFile;

    public FileWatcher(File file) {
        indexFile = file;
        timestamp = file.lastModified();
    }

    public final void run() {
        long newTimestamp = indexFile.lastModified();
        if (timestamp != newTimestamp) {
            timestamp = newTimestamp;
            onChange(indexFile);
        }
    }

    protected abstract void onChange( File file );
}
