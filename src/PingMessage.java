import java.net.InetAddress;
import java.util.Date;

public class PingMessage {
	
	private InetAddress address;
	private int port;
	private String payload;
	

	public PingMessage(InetAddress address, int port, String payload) {
		this.address = address;
		this.port = port;
		this.payload = payload;
	}
	
	public InetAddress getIP() {
		return address;
	}

	public int getPort() {
		return port;
	}
	
	public String getPayload() {
		return payload;
	}
	
	
	
}	