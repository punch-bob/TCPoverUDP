import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class Serializer {

    public byte[] serialize(TCPPacket packet) throws IOException 
    {
        try (ByteArrayOutputStream b = new ByteArrayOutputStream())
        {
            try (ObjectOutputStream o = new ObjectOutputStream(new BufferedOutputStream(b)))
            {
                o.writeObject(packet);
            }
            return b.toByteArray();
        }
    }

    public Object deserialize(byte[] bytes) throws IOException, ClassNotFoundException 
    {
        try (ByteArrayInputStream b = new ByteArrayInputStream(bytes))
        {
            try (ObjectInputStream o = new ObjectInputStream(b))
            {
                return o.readObject();
            }
        }
    }
}