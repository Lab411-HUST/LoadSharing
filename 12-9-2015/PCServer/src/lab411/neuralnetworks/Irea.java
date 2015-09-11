package lab411.neuralnetworks;

public class Irea {
	public static int[] irea;
	public static void setIrea(int len)
	{
		irea = new int[len];
		for(int i=0; i<len; i++)
		{
			irea[i] = i;
		}
	}
}
