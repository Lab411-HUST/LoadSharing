package lab411.loadsharing;

public class Main {
	public static int PORT = 9999;
	public static void main(String[] args) throws Exception
	{
		ConnectSocket cs = new ConnectSocket();
		cs.start();
		ExtractSocket es = new ExtractSocket();
		es.start();
		TrainSocket ts = new TrainSocket();
		ts.start();
		DetectSocket ds = new DetectSocket();
		ds.start();
	}
}
