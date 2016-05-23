package edu.ucdenver.ccp.medline.xml;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;

import org.junit.Test;
import org.medline.MedlineCitation;

import edu.ucdenver.ccp.common.collections.CollectionsUtil;
import edu.ucdenver.ccp.common.test.DefaultTestCase;

public class MedlineCitationUtilTest extends DefaultTestCase {

	private static final String SAMPLE_FILE_NAME = "medline-xml-sample-2016.xml";
	private static final String ABSTRACT_1 = "OBJECTIVES: To examine the relationship between speech intelligibilities among the similar level of hearing loss and threshold elevation of the auditory brainstem response (ABR).\n"
			+ "METHODS: The relationship between maximum speech intelligibilities among similar levels of hearing loss and relative threshold elevation of the click-evoked ABR (ABR threshold - pure tone average at 2,000 and 4,000 Hz) was retrospectively reviewed in patients with sensorineural hearing loss (SNHL) other than apparent retrocochlear lesions as auditory neuropathy, vestibular schwannoma and the other brain lesions.\n"
			+ "RESULTS: Comparison of the speech intelligibilities in subjects with similar levels of hearing loss found that the variation in maximum speech intelligibility was significantly correlated with the threshold elevation of the ABR.\n"
			+ "CONCLUSION: The present results appear to support the idea that variation in maximum speech intelligibility in patients with similar levels of SNHL may be related to the different degree of dysfunctions of the inner hair cells and/or cochlear nerves in addition to those of outer hair cells.";
	private static final String ABSTRACT_2 = "Trauma to soft tissues is an important consideration in cases of seatbelt injury, as soft-tissue injury can cause shock. Careful observation for hours in the clinic at least, along with appropriate imaging studies, is necessary if signs of a seatbelt injury exist. ";
	private static final Map<String, String> pmid2AbstractMap = new HashMap<String, String>() {
		{
			put("10787327", ABSTRACT_1);
			put("10847728", ABSTRACT_2);
			put("10970178", null);
		}
	};

	@Test
	public void testGetAbstractTest() throws IOException, JAXBException, XMLStreamException {
		File sampleMedlineXmlFile = copyClasspathResourceToTemporaryFile(getClass(), SAMPLE_FILE_NAME);
		int count = 0;
		List<String> expectedPmids = CollectionsUtil.createList("10787327", "10847728", "10970178");
		for (MedlineXmlParser parser = new MedlineXmlParser(sampleMedlineXmlFile); parser.hasNext();) {
			MedlineCitation citation = parser.next();
			String pmid = citation.getPMID().getvalue();
			String expectedPmid = expectedPmids.remove(0);
			assertEquals(expectedPmid, pmid);
			assertEquals(pmid2AbstractMap.get(pmid), MedlineCitationUtil.getAbstractText(citation));
			count++;
		}

		assertEquals(3, count);
	}

}
