package lab411.androidserver.features;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import lab411.androidserver.file.MyFile;

import android.os.Environment;

public class DataEntry {
	public static void extract() throws IOException
	{
		String[] type = {"wake", "stage1", "stage2", "stage3"};
		String[] target = {"0.0	0.0", "0.0	1.0", "1.0	0.0", "1.0	1.0"};
		File sdcard = Environment.getExternalStorageDirectory();
		File fo = new File(sdcard, "lsdata/FEATURES.txt");
		if (!fo.exists()) {
            fo.createNewFile();
        }
		FileWriter fw = new FileWriter(fo.getAbsoluteFile());
		BufferedWriter bw = new BufferedWriter(fw);
		StringBuilder content = new StringBuilder();
		
		for(int i=0; i<type.length; i++)
		{
			String tmp = "lsdata/train/" + type[i] + "/";
			File folder = new File(sdcard, tmp);
			File[] listOfFiles = folder.listFiles();
			for(File file : listOfFiles)
			{
				if(file.isFile())
				{
		            ArrayList<String> lines = MyFile.readAllFiles(tmp + file.getName());
					double[] signal = new double[lines.size()];
		            for (int j = 0; j < lines.size(); j++)
		            {
		                signal[j] = Double.parseDouble(lines.get(j));
		            }
		            double[] extract = Features.extract(signal);
		            for(int j=0; j<extract.length; j++)
		            {
		            	content.append(Double.toString(extract[j]) + "\t");
		            }
		            content.append(target[i] + "\n");
		            bw.write(content.toString());
		            content.delete(0, content.length());
				}
			}
		}
		bw.close();
		fw.close();
	}
	
	public static MyVector[] load() throws IOException
	{
		int nInput = 7;
		int nTarget = 2;
		ArrayList<String> lines = MyFile.readAllFiles("lsdata/FEATURES.txt");
		MyVector[] result = new MyVector[lines.size()];
 		for(int i=0; i<result.length; i++)
 		{
 			String[] tmp = lines.get(i).split("\t");
 			result[i] = new MyVector(nInput, nTarget);
 			for(int j=0; j<nInput; j++)
 			{
 				result[i].pattern[j] = Double.parseDouble(tmp[j]);
 			}
 			for(int j=0; j<nTarget; j++)
 			{
 				result[i].target[j] = Double.parseDouble(tmp[nInput + j]);
 			}
 		}
 		return result;
	}
}