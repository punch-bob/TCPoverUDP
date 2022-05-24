import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class TCPSocket
{
    private InetAddress address;
    private double chance;
    private Serializer serializer;

    public TCPSocket(String hostname, double chance)
    {
        serializer = new Serializer();
        try 
        {
            address = InetAddress.getByName(hostname);
        } 
        catch (UnknownHostException e) 
        {
            System.out.println("Unknown host!");
            e.printStackTrace();
        }
        this.chance = chance;
    }

    public void send(DatagramSocket socket, int port, TCPPacket packet)
    {
        if (Math.random() > chance)
        {
            try 
            {
                byte[] buf = serializer.serialize(packet);
                DatagramPacket datagramPacket = new DatagramPacket(buf, buf.length, address, port);
                socket.send(datagramPacket);
            } 
            catch (IOException e) 
            {
                System.out.println("Failed to send message!");
                e.printStackTrace();
            }
        }
        else
        {
            System.out.println("The packet was lost! AAA! Packet number: " + packet.getSeqNumber());
        }
    }

    public TCPPacket receive(DatagramSocket socket) throws ClassNotFoundException, IOException
    {
        byte[] buf = new byte[1024 * 1024]; //1Mb
        DatagramPacket datagramPacket = new DatagramPacket(buf, buf.length);
        try 
        {
            socket.receive(datagramPacket);
        }
        catch (IOException e) 
        {
            return null;
        }
        TCPPacket packet = (TCPPacket)serializer.deserialize(buf);
        return packet;
    }

    public void setChance(double chance)
    {
        this.chance = chance;
    }
}
