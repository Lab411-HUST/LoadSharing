package lab411.loadsharing;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import lab411.features.DataEntry;
import lab411.features.MyVector;
import lab411.neuralnetworks.Net;

public class TrainSocket extends Thread
{
	ServerSocket listener;
	Socket socket;
	@Override
	public void run() {
		try {
			listener = new ServerSocket(Main.PORT - 2);
			while(true)
			{
				socket = listener.accept();
				System.out.println("Training ...");
				MyVector[] train = DataEntry.load();
				Net.initNet(7, 10, 2);
				Net.initializeWeights();
				Net.setLearningParameters(0.005, 0.05);
				Net.trainNetworks(train, 10000);
				Net.saveNetworks();
	            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
	            out.println("done");
	            System.out.println("Training complete!!!");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
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