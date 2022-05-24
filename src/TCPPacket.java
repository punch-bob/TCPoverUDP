import java.io.Serializable;

public class TCPPacket implements Serializable
{
    private int ackNumber;
    private int seqNumber;
    private String message; 
    private boolean ACK;
    private boolean SYN;
    private boolean RST;

    public TCPPacket(boolean SYN, boolean ACK, boolean RST, int seqNumber, int ackNumber, String message)
    {
        this.seqNumber = seqNumber;
        this.ackNumber = ackNumber;
        this.message = message;
        this.SYN = SYN;
        this.ACK = ACK;
        this.RST = RST;
    }

    public int getAckNumber()
    {
        return ackNumber;
    }

    public void setAckNumber(int ackNumber)
    {
        this.ackNumber = ackNumber;
    }

    public int getSeqNumber()
    {
        return seqNumber;
    }

    public boolean getSYN()
    {
        return SYN;
    }

    public boolean getACK()
    {
        return ACK;
    }

    public boolean getRST()
    {
        return RST;
    }

    public String getMessage()
    {
        return message;
    }
}
