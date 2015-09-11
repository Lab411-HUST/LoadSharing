package lab411.loadsharing;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class IPAddress {
	public static String getIP() throws UnknownHostException
	{
		return InetAddress.getLocalHost().getHostAddress();
	}
	public static String getHostName() throws UnknownHostException
	{
		return InetAddress.getLocalHost().getHostName();
	}
}
