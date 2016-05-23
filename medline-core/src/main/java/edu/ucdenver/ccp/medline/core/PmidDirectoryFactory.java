package edu.ucdenver.ccp.medline.core;

import java.io.File;

/**
 * Given a PubMed ID, this code returns a 3-level directory structure in order
 * to balance storage of many files across the file system.
 */
public class PmidDirectoryFactory {

	public static File getDirectory(File baseDirectory, int pmid) {
		String pmidStr = Integer.toString(pmid);
		StringBuffer sb = new StringBuffer(pmidStr);
		sb.reverse();
		int index = 3;
		while (index < sb.length()) {
			sb.insert(index, "/");
			index += 4;
		}
		// System.out.println("buffer1: " + sb.toString());
		/* for pmids of length < 4, put them in the base/0/ directory */
		if (sb.length() < 4) {
			sb.append("/0");
		}
		// System.out.println("buffer2: " + sb.toString());
		/* drop the final /# to keep directories at a minimum */
		int firstSlash = sb.indexOf("/");
		sb = new StringBuffer(sb.substring(firstSlash + 1));
		// System.out.println("buffer3: " + sb.toString());
		sb.reverse();
		// System.out.println("buffer4: " + sb.toString());

		return new File(baseDirectory, sb.toString());
	}
}
