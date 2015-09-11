package lab411.file;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class MyFile {
	public static ArrayList<String> readAllFiles(String fileName) throws IOException
	{
		ArrayList<String> result = new ArrayList<String>();
		File file = new File(fileName);
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
