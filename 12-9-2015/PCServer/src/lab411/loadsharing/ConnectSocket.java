package lab411.loadsharing;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class ConnectSocket extends Thread
{
	ServerSocket listener;
	@Override
	public void run() {
		try {
			System.out.println("PORT : " + Integer.toString(Main.PORT));
			System.out.println("IPAddress : " + IPAddress.getIP());
			listener = new ServerSocket(Main.PORT);
			System.out.println("Server started. Waiting for clients...");
			while(true)
			{
				Socket socket = listener.accept();
				BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				System.out.println(input.readLine() + " connected.");
	            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
	            out.println(IPAddress.getHostName());
	            socket.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				listener.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}