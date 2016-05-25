/*
 Copyright (c) 2012, Regents of the University of Colorado
 All rights reserved.

 Redistribution and use in source and binary forms, with or without modification, 
 are permitted provided that the following conditions are met:

 * Redistributions of source code must retain the above copyright notice, this 
    list of conditions and the following disclaimer.
   
 * Redistributions in binary form must reproduce the above copyright notice, 
    this list of conditions and the following disclaimer in the documentation 
    and/or other materials provided with the distribution.
   
 * Neither the name of the University of Colorado nor the names of its 
    contributors may be used to endorse or promote products derived from this 
    software without specific prior written permission.

 THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND 
 ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED 
 WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE 
 DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR
 ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES 
 (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; 
 LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON 
 ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT 
 (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS 
 SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package edu.ucdenver.ccp.medline.xml;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.ucdenver.ccp.common.string.StringUtil;

/**
 * Iterates over a collection of Medline files according to the indexes that are part of their file
 * names, e.g. medline12n0123.xml.gz
 * 
 * @author Center for Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
 * 
 */
public class MedlineFileOrderer {

	public enum FileOrder {
		INC,
		DEC
	}

	/**
	 * @param directory
	 * @return by default we iterate over files in increasing order
	 */
	public static Iterable<File> getOrderedMedlineFileIterable(File directory) {
		return getOrderedMedlineFileIterable(directory, FileOrder.INC);
	}

	/**
	 * 
	 * @param directory
	 * @return an {@link Iterable} of {@link File} objects for each Medline XML file in the
	 *         specified directory. The collection is orderd according to the index in the file
	 *         names, e.g. medline12n0123.xml.gz, medline12n0124.xml.gz, medline12n0125.xml.gz, and
	 *         so on.
	 */
	public static Iterable<File> getOrderedMedlineFileIterable(File directory, final FileOrder fileOrder) {
		final Pattern medlineFilePattern = Pattern.compile("^medline\\d\\dn(\\d\\d\\d\\d)\\.xml\\.?g?z?$");
		List<File> files = Arrays.asList(directory.listFiles());
		
		/* remove any irrelevant files, e.g. md5 files */
		List<File> medlineFiles = new ArrayList<File>();
		for (File f : files) {
			Matcher m = medlineFilePattern.matcher(f.getName());
			if (m.find()) {
				medlineFiles.add(f);
			}
		}
		
		Collections.sort(medlineFiles, new Comparator<File>() {

			public int compare(File f1, File f2) {
				Integer f1Number = null;
				Integer f2Number = null;
				Matcher m = medlineFilePattern.matcher(f1.getName());
				if (m.find()) {
					f1Number = Integer.parseInt(StringUtil.removePrefixRegex(m.group(1), "0*"));
				}
				m = medlineFilePattern.matcher(f2.getName());
				if (m.find()) {
					f2Number = Integer.parseInt(StringUtil.removePrefixRegex(m.group(1), "0*"));
				}

				if (f1Number == null) {
					if (f2Number != null) {
						return 1;
					}
					return 0; // both are null so it doesn't matter
				} else if (f2Number == null) {
					return -1;
				}

				int orderer = 1;
				if (fileOrder.equals(FileOrder.DEC)) {
					orderer = -1;
				}
				return orderer * f1Number.compareTo(f2Number);
			}
		});
		return medlineFiles;
	}

}
