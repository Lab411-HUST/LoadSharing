package lab411.androidserver.loadsharing;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import android.os.Environment;

public class WritePerform extends Thread{
	FileWriter fw1, fw2;
	BufferedWriter bw1, bw2;
	public boolean run;
	public WritePerform(String cpuFile, String memFile) throws IOException
	{
		File sdcard = Environment.getExternalStorageDirectory();
		File fo1 = new File(sdcard, cpuFile);
		File fo2 = new File(sdcard, memFile);
		if (!fo1.exists())
			fo1.createNewFile();
		if (!fo2.exists())
			fo2.createNewFile();
		fw1 = new FileWriter(fo1.getAbsoluteFile());
		fw2 = new FileWriter(fo2.getAbsoluteFile());
		bw1 = new BufferedWriter(fw1);
		bw2 = new BufferedWriter(fw2);
	}
	@Override
	public void run() {
		run  = true;
		// TODO Auto-generated method stub
		try {
			while (run) {
				bw1.write(String.format("%.2f", Performance.cpu) + "\n");
				bw2.write(String.format("%.2f", Performance.memProcess) + "\n");
				Thread.sleep(100);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				bw1.close();
				fw1.close();
				bw2.close();
				fw2.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
