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

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import edu.ucdenver.ccp.common.collections.CollectionsUtil;
import edu.ucdenver.ccp.common.test.DefaultTestCase;
import edu.ucdenver.ccp.medline.xml.MedlineFileOrderer.FileOrder;

/**
 * @author Colorado Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
 * 
 */
public class MedlineFileOrdererTest extends DefaultTestCase {

	/**
	 * 
	 */
	private static final String MEDLINE12N0010_XML = "medline12n0010.xml.gz";
	/**
	 * 
	 */
	private static final String MEDLINE12N0890_XML = "medline12n0890.xml.gz";
	/**
	 * 
	 */
	private static final String MEDLINE12N0645_XML = "medline12n0645.xml.gz";
	/**
	 * 
	 */
	private static final String MEDLINE12N0002_XML = "medline12n0002.xml.gz";
	/**
	 * 
	 */
	private static final String MEDLINE12N0001_XML = "medline12n0001.xml.gz";

	private static final String MEDLINE12N0001_XML_MD5 = "medline12n0001.xml.gz.md5";
	private static final String MEDLINE12N0001_STATS = "medline12n0001_stats.html";

	@Before
	public void setUp() throws IOException {
		folder.newFile(MEDLINE12N0890_XML);
		folder.newFile(MEDLINE12N0010_XML);
		folder.newFile(MEDLINE12N0001_XML);
		folder.newFile(MEDLINE12N0001_STATS);
		folder.newFile(MEDLINE12N0001_XML_MD5);
		folder.newFile(MEDLINE12N0645_XML);
		folder.newFile(MEDLINE12N0002_XML);
	}

	@Test
	public void testIncreasingOrder() {
		List<File> orderedFiles = CollectionsUtil.createList(MedlineFileOrderer.getOrderedMedlineFileIterable(
				folder.getRoot()).iterator());
		assertEquals(7, orderedFiles.size());
		assertEquals(MEDLINE12N0001_XML, orderedFiles.get(0).getName());
		assertEquals(MEDLINE12N0002_XML, orderedFiles.get(1).getName());
		assertEquals(MEDLINE12N0010_XML, orderedFiles.get(2).getName());
		assertEquals(MEDLINE12N0645_XML, orderedFiles.get(3).getName());
		assertEquals(MEDLINE12N0890_XML, orderedFiles.get(4).getName());
	}

	@Test
	public void testDecreasingOrder() {
		List<File> orderedFiles = CollectionsUtil.createList(MedlineFileOrderer.getOrderedMedlineFileIterable(
				folder.getRoot(), FileOrder.DEC).iterator());
		assertEquals(7, orderedFiles.size());
		assertEquals(MEDLINE12N0890_XML, orderedFiles.get(0).getName());
		assertEquals(MEDLINE12N0645_XML, orderedFiles.get(1).getName());
		assertEquals(MEDLINE12N0010_XML, orderedFiles.get(2).getName());
		assertEquals(MEDLINE12N0002_XML, orderedFiles.get(3).getName());
		assertEquals(MEDLINE12N0001_XML, orderedFiles.get(4).getName());

	}

}
