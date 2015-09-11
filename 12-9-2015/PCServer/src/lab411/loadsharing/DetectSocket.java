package lab411.loadsharing;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import lab411.neuralnetworks.Net;

public class DetectSocket extends Thread
{
	ServerSocket listener;
	Socket socket;
	@Override
	public void run() {
		try {
			listener = new ServerSocket(Main.PORT - 3);
			socket = listener.accept();
			System.out.println("Detect signal...");
			Net.initNet(7, 10, 2);
			Net.loadNetworks();
			ArrayList<String> list = new ArrayList<String>();
			double[] signal = new double[2000];
			while(true)
			{
				BufferedReader in = new BufferedReader( new InputStreamReader(socket.getInputStream()));
				String receive = in.readLine();
				String[] s1 = receive.split("\t");
				for (int i = 0; i < s1.length; i++)
                {
                    list.add(s1[i]);
                }
				if (list.size() == 2000)
                {
					for(int i=0; i<list.size(); i++)
					{
						signal[i] = Double.parseDouble(list.get(i));
					}
					String result = Net.detect(signal);
					if(result.equalsIgnoreCase("fail"))
					{
						list.subList(0, 100).clear();
					}
					else
					{
						PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
			            out.println(result);
						list.clear();
						System.out.println(result);
					}
                }
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