package lab411.loadsharing;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import lab411.features.DataEntry;

public class ExtractSocket extends Thread
{
	ServerSocket listener;
	Socket socket;
	@Override
	public void run() {
		try {
			listener = new ServerSocket(Main.PORT - 1);
			while(true)
			{
				socket = listener.accept();
				System.out.println("Extracting ...");
				DataEntry.extract();
	            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
	            out.println("done");
	            System.out.println("Features extracted!");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				socket.close();
				listener.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
