package edu.ucdenver.ccp.medline.core;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.Test;

public class PmidDirectoryFactoryTest {

	private static final File baseDirectory = new File("/base");

	@Test
	public void test_1() {
		File dir = PmidDirectoryFactory.getDirectory(baseDirectory, 1);
		File expectedDir = new File("/base/0");

		assertEquals(expectedDir, dir);
	}

	@Test
	public void test_123() {
		File dir = PmidDirectoryFactory.getDirectory(baseDirectory, 123);
		File expectedDir = new File("/base/0");

		assertEquals(expectedDir, dir);
	}

	@Test
	public void test_1234() {
		File dir = PmidDirectoryFactory.getDirectory(baseDirectory, 1234);
		File expectedDir = new File("/base/1");

		assertEquals(expectedDir, dir);
	}

	@Test
	public void test_12345() {
		File dir = PmidDirectoryFactory.getDirectory(baseDirectory, 12345);
		File expectedDir = new File("/base/12");

		assertEquals(expectedDir, dir);
	}

	@Test
	public void test_123456() {
		File dir = PmidDirectoryFactory.getDirectory(baseDirectory, 123456);
		File expectedDir = new File("/base/123");

		assertEquals(expectedDir, dir);
	}

	@Test
	public void test_1234567() {
		File dir = PmidDirectoryFactory.getDirectory(baseDirectory, 1234567);
		File expectedDir = new File("/base/1/234");

		assertEquals(expectedDir, dir);
	}

	@Test
	public void test_12345678() {
		File dir = PmidDirectoryFactory.getDirectory(baseDirectory, 12345678);
		File expectedDir = new File("/base/12/345");

		assertEquals(expectedDir, dir);
	}

	@Test
	public void test_maxFilesInDirectory() {
		Map<File, Integer> dirToCountMap = new HashMap<File, Integer>();

		for (int i = 1; i < 25000000; i++) {
			File dir = PmidDirectoryFactory.getDirectory(baseDirectory, i);
			if (dirToCountMap.containsKey(dir)) {
				Integer count = dirToCountMap.get(dir);
				count++;
				dirToCountMap.put(dir, count);
			} else {
				dirToCountMap.put(dir, 1);
			}
		}

		int max = Integer.MIN_VALUE;
		for (Entry<File, Integer> entry : dirToCountMap.entrySet()) {
			Integer count = entry.getValue();
			if (count > max) {
				max = count;
			}
		}

		assertEquals(1000, max);
	}

}
