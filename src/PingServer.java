import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Date;
import java.util.Random;

public class PingServer {

	/* Variable declarations. */
	private DatagramSocket ds;
	private int PORT_NUMBER = 5530;
	private int PACKET_SIZE = 512;
    private byte[] buffer = new byte[PACKET_SIZE];
    private double LOSS_RATE = 0.3;
    private int AVERAGE_DELAY = 100;
    
    public static void main(String[] args) {
		
		PingServer server = new PingServer();
		server.run();
    }
    
    /* 
     * Constructor creates a connection to a DatagramSocket using PortNumber. 
     */
    public PingServer() {
    	
    	try {
			ds = new DatagramSocket(PORT_NUMBER);
		} catch (SocketException e) {
			System.out.println("Error: Server side Datagram Socket could not be opened.");
			e.printStackTrace();
		}
    }
    
    /*
     * Generates a random number and determines whether or not their should
     * be a packet loss, resulting in the return of the appropriate boolean value.
     */
    public boolean losePacket() {
    	
    	boolean packetLoss;
    	
    	// Generate a random number between 0 and 1.
    	double randNum = Math.random();
    	System.out.println(randNum);
        if (randNum <= LOSS_RATE) {
        	packetLoss = true;
        } else {
        	packetLoss = false;
        }
        return packetLoss;
    }
    
    /*
     * Runs the PingServer.
     */
    public void run() {
    	
    	System.out.println("Ping server running...");
    	while (true) {
    		
    		DatagramPacket packet = new DatagramPacket(buffer, PACKET_SIZE);
    		try {
    			//simulate transmission delay
    			Random random = new Random(new Date().getTime());
    			System.out.println("Waiting for UDP Packet...");
				Thread.sleep((long) (Math.random() * 2 * AVERAGE_DELAY));
				ds.receive(packet);
			} catch (IOException e) {
				System.out.println("Error: Server side Datagram Socket could not receive packet.");
				e.printStackTrace();
			} catch (InterruptedException e) {
				System.out.println("Error: Failed to call Thread.sleep()");
				e.printStackTrace();
			}
    		
    		InetAddress address = packet.getAddress();
    		int port = packet.getPort();
    		packet = new DatagramPacket(buffer, buffer.length, address, port);
    		
    		try {
				Thread.sleep((long) (Math.random() * 2 * AVERAGE_DELAY));
    			String output = packet.getSocketAddress().toString();
    			String payload = new String(buffer, "UTF-8");
				System.out.printf("Received from: %s %s\n", output, payload);
    			
    			if (losePacket()) {
    				System.out.println("Packet loss..., reply not sent.");
    			} else {
    				ds.send(packet);
    				System.out.println("Reply sent");
    			}
				
			} catch (IOException e) {
				System.out.println("Packet loss..., reply not sent.");
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
    		

    	}
    	
    }
    
}
