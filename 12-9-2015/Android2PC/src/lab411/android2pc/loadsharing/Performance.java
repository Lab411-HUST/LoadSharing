package lab411.android2pc.loadsharing;

import java.io.IOException;
import java.io.RandomAccessFile;

import android.app.ActivityManager.MemoryInfo;
public class Performance extends Thread{
	public static long cpuUsage, cpuIdle;
	public static double cpu;
	public static double mem;
	public static double memProcess;
	public static void init()
	{
		cpuUsage = 0;
		cpuIdle = 0;
		cpu = 0;
		mem = 0;
		memProcess = 0;
	}
	public static void update() throws IOException
	{
		RandomAccessFile reader = new RandomAccessFile("/proc/stat", "r");
        String load = reader.readLine();
        String[] toks = load.split(" +");
        long currCpuIdle = Long.parseLong(toks[4]);
        long currCpuUsage = Long.parseLong(toks[2]) + Long.parseLong(toks[3]) + Long.parseLong(toks[5])
              + Long.parseLong(toks[6]) + Long.parseLong(toks[7]) + Long.parseLong(toks[8]);
        reader.close();
        double result = (double) (currCpuUsage - cpuUsage) / ((currCpuUsage + currCpuIdle) - (cpuUsage + cpuIdle));
        if(result < 0)
        	cpu = 0;
        else
        	cpu = result*100;
        cpuUsage = currCpuUsage;
        cpuIdle = currCpuIdle;
        
        MemoryInfo mi = new MemoryInfo();
		MainActivity.activityManager.getMemoryInfo(mi);
		mem = (double) mi.availMem*100 / mi.totalMem;
		
		Runtime info = Runtime.getRuntime();
        long freeSize = info.freeMemory();
        long totalSize = info.totalMemory();
        long usedSize = totalSize - freeSize;
	    memProcess = (double) usedSize/(1024*1024);
	}
	@Override
	public void run() {
		init();
		// TODO Auto-generated method stub
		try {
			while(true)
			{
				update();
				Thread.sleep(100);
			}
			
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
