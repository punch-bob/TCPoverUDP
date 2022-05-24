import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class TCPLogger
{
    private final Logger log;

    public TCPLogger(String filename)
    {
        log = Logger.getLogger(Logger.class.getName());
        log.setUseParentHandlers(false);
        FileHandler fh = null;
        try 
        {
            fh = new FileHandler(filename);
            fh.setFormatter(new SimpleFormatter());
        } 
        catch (SecurityException | IOException e) 
        {
            e.printStackTrace();
        }  
        log.addHandler(fh);
    }

    public void getExceptionMessage(Level lvl, Throwable e)
    {
        log.log(lvl, "Exception: ", e);
    }

    public void getInfoMessage(String message)
    {
        log.log(Level.INFO, "Operation performed: " + message);
    }
}