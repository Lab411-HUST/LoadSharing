package lab411.androidserver.features;

public class MyVector {
	public double[] pattern;
    public double[] target;
    public MyVector()
    {
    }
    public MyVector(int nI, int nO)
    {
        pattern = new double[nI];
        target = new double[nO];
    }
}
