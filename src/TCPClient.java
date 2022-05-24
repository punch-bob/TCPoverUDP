import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.ConnectException;
import java.net.DatagramSocket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.HashMap;
import java.util.TreeSet;
import java.util.logging.Level;

public class TCPClient extends TCPSocket implements Runnable
{
    private DatagramSocket socket;
    private HashMap<TCPPacket, Long> timeouts;
    private int timeout;
    private double chance;
    private int recvPort;
    private int seqNumber;
    private List<TCPPacket> sendMessage;
    private TreeSet<TCPPacket> recvMessage;
    private TCPLogger logger;

    public TCPClient(String hostname, int myPort, int recvPort, double chance, int timeout, String logFileName) throws IOException 
    {
        super(hostname, chance);
        this.chance = chance;
        socket = new DatagramSocket(myPort);
        socket.setSoTimeout(timeout);

        this.timeout = timeout;
        this.recvPort = recvPort;

        seqNumber = 0;

        logger = new TCPLogger(logFileName);
        timeouts = new HashMap<>();
        sendMessage = new ArrayList<>();
        recvMessage = new TreeSet<>(new TCPPacketComparator());
    }

    public DatagramSocket getSocket()
    {
        return socket;
    }

    public void sendPacket(DatagramSocket socket, int port, TCPPacket packet)
    {

        timeouts.put(packet, System.currentTimeMillis());
        send(socket, port, packet);
        logger.getInfoMessage("A package with a number " + seqNumber + " has been sent!\n");
    }

    public void test(String messageFileName) throws FileNotFoundException, IOException
    {
        sendMessage = new ArrayList<>();
        File file = new File(messageFileName);
        try (BufferedReader br = new BufferedReader(new FileReader(file))) 
        {
            String str;
            while ((str = br.readLine()) != null)
            {
                TCPPacket newPacket = new TCPPacket(false, false, false, seqNumber, 0, str);
                sendMessage.add(newPacket);
                seqNumber++;
            }
        }
    }

    public void handshake() throws ClassNotFoundException, IOException
    {
        setChance(0);
        TCPPacket synPacket = new TCPPacket(true, false, false, seqNumber, 0, "0");
        send(socket, recvPort, synPacket);
        TCPPacket synAckPacket = receive(socket);
        if (synAckPacket.getAckNumber() == (seqNumber + 1) && synAckPacket.getACK() && synAckPacket.getSYN())
        {
            seqNumber++;
            TCPPacket ackPacket = new TCPPacket(false, true, false, seqNumber, synAckPacket.getSeqNumber() + 1, "0");
            send(socket, recvPort, ackPacket);
        }
        else
        {
            throw new ConnectException();
        }
        seqNumber = 0;
        setChance(chance);
    }

    public TCPPacket receivePacket(DatagramSocket socket) 
    {
        TCPPacket recvPacket = null;
        try 
        {
            recvPacket = receive(socket);
        } 
        catch (ClassNotFoundException | IOException e) 
        {
            logger.getExceptionMessage(Level.WARNING, e);
            System.out.println("Failed to recieve packet!");
            e.printStackTrace();
        }
        return recvPacket;
    }

    public void disconnect()
    {
        TCPPacket rstAckPacket = new TCPPacket(false, true, true, seqNumber, 0, null);
        sendPacket(socket, recvPort, rstAckPacket);
        logger.getInfoMessage("Connection is closed!");
    }

    @Override
    public void run() 
    {
        try 
        {
            handshake();
        } 
        catch (ClassNotFoundException | IOException e) 
        {
            logger.getExceptionMessage(Level.WARNING, e);
            e.printStackTrace();
        }
        logger.getInfoMessage("The connection is established!\n");

        int ackNumber = 0;
        while (true)
        {
            Iterator<TCPPacket> iter = sendMessage.iterator();
            if (iter.hasNext())
            {
                TCPPacket packet = iter.next();
                packet.setAckNumber(ackNumber);
                sendPacket(socket, recvPort, packet);
                iter.remove();
            }
            
            TCPPacket recvPacket = receivePacket(socket);
            if (recvPacket != null)
            {
                if (recvPacket.getRST())
                {
                    break;
                }

                if (!recvPacket.getACK())
                {
                    recvMessage.add(recvPacket);
                    TCPPacket ackAnswer = new TCPPacket(false, true, false, recvPacket.getAckNumber(), recvPacket.getSeqNumber() + 1, null);
                    send(socket, recvPort, ackAnswer);
                    logger.getInfoMessage("ACK with a number " + ackAnswer.getAckNumber() + " has been sent!\n");
                }
                else
                {
                    timeouts.remove(recvPacket);
                    logger.getInfoMessage("A package with a number " + (recvPacket.getAckNumber() - 1) + " has been delivered!\n");
                    if (timeouts.isEmpty())
                    {
                        break;
                    }
                }
                ackNumber = recvPacket.getSeqNumber() + 1;
            }
            else
            {
                for (TCPPacket packet : timeouts.keySet())
                {
                    if (System.currentTimeMillis() - timeouts.get(packet) >= timeout)
                    {
                        logger.getInfoMessage("A package with a number " + (packet.getAckNumber() - 1) + " hasn't been delivered!\n");
                        sendPacket(socket, recvPort, packet);
                    }
                }
            }

            //Checking received packets
            int i = 1;
            for(TCPPacket printPack : recvMessage)
            {
                System.out.println(i + " " + printPack.getMessage());
                i++;
            }
        }

        disconnect();
    }
    
}
