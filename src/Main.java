import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException
    {
        TCPClient clientSocket = new TCPClient("localhost", 1234, 2345, 0.5, 1000, "src/logs/client_log.txt");
        Server serverSocket = new Server("localhost", 2345, 1234, 0.5, 1000, "src/logs/server_log.txt");
        clientSocket.test("message.txt");

        Thread client = new Thread(clientSocket);
        Thread server = new Thread(serverSocket);
        server.start();
        client.start();
    }
}
