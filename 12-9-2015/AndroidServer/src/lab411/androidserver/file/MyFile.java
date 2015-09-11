package lab411.androidserver.file;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import android.os.Environment;

public class MyFile {
	public static ArrayList<String> readAllFiles(String fileName) throws IOException
	{
		ArrayList<String> result = new ArrayList<String>();
		File sdcard = Environment.getExternalStorageDirectory();
		File file = new File(sdcard,fileName);
        FileReader fr = new FileReader(file.getAbsoluteFile());
	    BufferedReader br = new BufferedReader(fr);
        String line;
        while ((line = br.readLine()) != null) {
            result.add(line);
        }
        br.close();
        fr.close();
        return result;
	}
}
