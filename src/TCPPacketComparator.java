import java.util.Comparator;

public class TCPPacketComparator implements Comparator<TCPPacket>
{
    @Override
    public int compare(TCPPacket packet1, TCPPacket packet2) 
    {
        return packet1.getSeqNumber() - packet2.getSeqNumber();
    } 
}
