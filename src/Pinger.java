import java.awt.EventQueue;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Scanner;

public class Pinger {

	private int MAX_PACKET_SIZE = 512;
    private DatagramSocket ds;
    private InetAddress ip;
    int timeout = 1000;
    private String host_dest = "constance.cs.rutgers.edu";
	private int port_dest = 5530;
	
    public Pinger() {
    	try {
			this.ds = new DatagramSocket();
			this.ip = InetAddress.getLocalHost();
			ds.setSoTimeout(timeout);
		} catch (SocketException e) {
			System.out.println("Error building Datagram Socket.");
			e.printStackTrace();
		} catch (UnknownHostException e) {
			System.out.println("Error finding IP address of local machine.");
			e.printStackTrace();
		}
    }
    
    
    // Default behavior is to send 10 PING messages using UDP
    public void run() {
    	
    	Scanner scan = new Scanner(System.in);
    	System.out.println("Enter 'y' to use default host and port server (constance.cs.rutgers.edu 5530).");
    	System.out.println("Otherwise enter the host name as a single token");
    	String userHost = scan.next();
    	if (userHost.equals("y")) {
    		// Do default behavior
    	} else {
    		System.out.println("Now enter the port number as a single token");
    		String portStr = scan.next();
    		Integer userPort = Integer.parseInt(portStr);
    		setHost_dest(userHost);
    		setPort_dest(userPort);
    	}
    	
    	ArrayList<Integer> pings = new ArrayList<Integer>();
    	ArrayList<String> pingBoolean = new ArrayList<String>();
    	
    	for (int i=0; i < 10; i++) {
    		
    		String payload = createPayload(i);
    		
    		//Ask for Destination IP and Port
    		String host_dest = getHost_dest();
    		int port_dest = getPort_dest();

    		// Create and send Ping message
    		InetAddress IP_dest;
			try {
				IP_dest = InetAddress.getByName(host_dest);
				PingMessage message = new PingMessage(IP_dest, port_dest, payload);
				
				// send Packet through UDP
				byte[] payloadBytes = message.getPayload().getBytes();
				DatagramPacket packet = new DatagramPacket(payloadBytes, payloadBytes.length, IP_dest, port_dest);
				
				Date date1 = new Date();
				long timestamp_send = date1.getTime();
				ds.send(packet);
				ds.receive(packet);
				Date date2 = new Date();
				long timestamp_rec = date2.getTime();
				
				Integer RTT = (int) (timestamp_rec - timestamp_send);
				pings.add(RTT);
				
				String socketAddress = packet.getSocketAddress().toString();
				System.out.printf("Received packet from %s %s\n", socketAddress, timestamp_rec);
				pingBoolean.add("true");
				if (i == 9) {
					timeout = 5000;
				}
				
				
			} catch (UnknownHostException e) {
				System.out.println("Error finding IP address from given host name.");
				System.out.println("Please try again, and enter an appropriate host name.");
				return;
			} catch (IOException e) {
				Integer RTT = 1000;
				pings.add(RTT);
				pingBoolean.add("false");
				System.out.println(e.toString());
			}

    	}
    	
    	showRTT(pings, pingBoolean);
    }
    
    public String createPayload(int sequenceNumber) {
    	
    	Date date = new Date();
		long timestamp = date.getTime();
		String payload = "PING " + sequenceNumber + " " + timestamp;
		
		return payload;
    }
    
    public void showRTT(ArrayList<Integer> pings, ArrayList<String> pingBoolean) {
    	
    	Integer[] array = new Integer[10]; 
    	int sum = 0;
    	for (int i = 0; i < pings.size(); i++) {
    		System.out.printf("PING %d: %s RTT: %d\n", i, pingBoolean.get(i), pings.get(i));
    		array[i] = pings.get(i);
    		sum += pings.get(i);
    	}    	
    	float avg = (float) sum / pings.size(); 
    	int min = Collections.min(Arrays.asList(array));
    	int max = Collections.max(Arrays.asList(array));
    	
    	System.out.printf("Minimum = %dms, Maximum = %dms, Average = %.1fms\n", min, max, avg);
    	
    }


	public String getHost_dest() {
		return host_dest;
	}


	public void setHost_dest(String host_dest) {
		this.host_dest = host_dest;
	}


	public int getPort_dest() {
		return port_dest;
	}


	public void setPort_dest(int port_dest) {
		this.port_dest = port_dest;
	}
	
	
	
}
