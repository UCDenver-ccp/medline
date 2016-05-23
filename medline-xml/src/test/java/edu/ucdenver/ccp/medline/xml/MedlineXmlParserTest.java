package edu.ucdenver.ccp.medline.xml;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;

import org.junit.Test;
import org.medline.MedlineCitation;

import edu.ucdenver.ccp.common.collections.CollectionsUtil;
import edu.ucdenver.ccp.common.test.DefaultTestCase;

public class MedlineXmlParserTest extends DefaultTestCase {

	private static final String SAMPLE_FILE_NAME = "medline-xml-sample-2016.xml";

	/**
	 * Tests the {@link MedlineXmlParser} over a sample XML file containing 3
	 * citations.
	 * 
	 * @throws IOException
	 * @throws JAXBException
	 * @throws XMLStreamException
	 */
	@Test
	public void testOnSampleXml() throws IOException, JAXBException, XMLStreamException {
		File sampleMedlineXmlFile = copyClasspathResourceToTemporaryFile(getClass(), SAMPLE_FILE_NAME);
		int count = 0;
		List<String> expectedPmids = CollectionsUtil.createList("10787327", "10847728", "10970178");
		for (MedlineXmlParser parser = new MedlineXmlParser(sampleMedlineXmlFile); parser.hasNext();) {
			MedlineCitation citation = parser.next();
			String pmid = citation.getPMID().getvalue();
			String expectedPmid = expectedPmids.remove(0);
			assertEquals(expectedPmid, pmid);
			count++;
		}

		assertEquals(3, count);
	}

}
