package com.pcs.ztqtj.control.tool;

import java.io.File;

public class DealWidthFile {
	private String fileNameParentPath = "";

	public String seachFilePath(String path, String filename) {
		try {
			File file = new File(path);
			if (!file.exists()) {
				file.mkdirs();
			}
			File[] files = file.listFiles();
			if (files == null) {

			} else {
				for (int i = 0; i < files.length; i++) {
					System.out.println(files[i].getName());
					if (files[i].isDirectory()) {
						seachFilePath(files[i].getPath(), filename);
					} else {
						if (files[i].getName().equals(filename)) {
							fileNameParentPath = files[i].getParent();
							break;
						}
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return fileNameParentPath;
	}
}
