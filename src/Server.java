import java.io.IOException;
import java.net.DatagramSocket;

public class Server extends TCPClient
{
    private int totalRecvPacket;
    private DatagramSocket socket;
    private int recvPort;
    private double chance;

    public Server(String hostname, int myPort, int recvPort, double chance, int timeout, String logFileName) throws IOException 
    {
        super(hostname, myPort, recvPort, chance, timeout, logFileName);
        socket = getSocket();
        this.recvPort = recvPort;
        totalRecvPacket = 0;
        this.chance = chance;
    }

    @Override
    public void handshake() throws ClassNotFoundException, IOException
    {
        setChance(0);
        TCPPacket synPacket = receive(socket);
        if (synPacket.getSYN() && !synPacket.getACK())
        {
            TCPPacket synAckPacket = new TCPPacket(true, true, false, 1, synPacket.getSeqNumber() + 1, "0");
            send(socket, recvPort, synAckPacket);
            receive(socket);
        }
        setChance(chance);
    }

    public int getTotalRecvPacket()
    {
        return totalRecvPacket;
    }    
}
