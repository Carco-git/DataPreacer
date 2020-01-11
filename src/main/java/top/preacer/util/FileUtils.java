package top.preacer.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileUtils {
	public static List<String> getDirectory(String path) {
		List<String> directories = new ArrayList<String>();
		File file = new File(path);
		System.out.println(file.getAbsolutePath());
		if(!file.exists()) {
			return null;
		}
        File[] tempList = file.listFiles();
        for (int i = 0; i < tempList.length; i++) {
            if (tempList[i].isFile()) {
            	continue;
            }
            if (tempList[i].isDirectory()) {
            	directories.add(tempList[i].getName());
            }
        }
        return directories;
	}
}
