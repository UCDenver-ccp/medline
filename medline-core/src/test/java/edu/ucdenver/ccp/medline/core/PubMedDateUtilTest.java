package edu.ucdenver.ccp.medline.core;

import static org.junit.Assert.*;
import static edu.ucdenver.ccp.medline.core.PubMedDateUtil.parseMedlineDate;
import org.junit.Test;

public class PubMedDateUtilTest {

	@Test
	public void testMedlineDateParse_1() {
		assertArrayEquals(new int[] { 12, 1998 }, parseMedlineDate("1998 Dec-1999 Jan"));
	}

	@Test
	public void testMedlineDateParse_2() {
		assertArrayEquals(new int[] { 4, 2000 }, parseMedlineDate("2000 Spring"));
	}

	@Test
	public void testMedlineDateParse_2a() {
		assertArrayEquals(new int[] { 4, 2000 }, parseMedlineDate("2000 SPRING"));
	}
	
	@Test
	public void testMedlineDateParse_3() {
		assertArrayEquals(new int[] { 4, 2000 }, parseMedlineDate("2000 Spring-Summer"));
	}

	@Test
	public void testMedlineDateParse_4() {
		assertArrayEquals(new int[] { 11, 2000 }, parseMedlineDate("2000 Nov-Dec"));
	}

	@Test
	public void testMedlineDateParse_5() {
		assertArrayEquals(new int[] { 12, 2000 }, parseMedlineDate("2000 Dec 23- 30"));
	}
}
