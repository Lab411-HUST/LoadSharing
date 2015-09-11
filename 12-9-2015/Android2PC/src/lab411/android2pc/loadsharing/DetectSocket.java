package lab411.android2pc.loadsharing;

import java.net.Socket;

public class DetectSocket {
	public static Socket socket;
	public static void set(Socket s)
	{
		socket = s;
	}
}
