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
import org.medline.PubmedArticle;

import edu.ucdenver.ccp.common.collections.CollectionsUtil;
import edu.ucdenver.ccp.common.test.DefaultTestCase;

public class MedlineCitationUtilTest extends DefaultTestCase {

	private static final String SAMPLE_FILE_NAME = "pubmedsample18n0001.xml.gz";
	private static final String ABSTRACT_1 = "BACKGROUND: The prevalence of mental health problems in women is 1:3 and such problems tend to be persistent. There is evidence from a range of studies to suggest that a number of factors relating to maternal psychosocial health can have a significant effect on the mother-infant relationship, and that this can have consequences for the psychological health of the child. It is now thought that parenting programmes may have an important role to play in the improvement of maternal psychosocial health.\n"
			+ "OBJECTIVES: The objective of this review is to address whether group-based parenting programmes are effective in improving maternal psychosocial health including anxiety, depression and self-esteem.\n"
			+ "SEARCH STRATEGY: A range of biomedical, social science, educational and general reference electronic databases were searched including MEDLINE, EMBASE CINAHL, PsychLIT, ERIC, ASSIA, Sociofile and the Social Science Citation Index. Other sources of information included the Cochrane Library (SPECTR, CENTRAL), and the National Research Register (NRR).\n"
			+ "SELECTION CRITERIA: Only randomised controlled trials were included in which participants had been randomly allocated to an experimental and a control group, the latter being either a waiting-list, no-treatment or a placebo control group. Studies had to include at least one group-based parenting programme, and one standardised instrument measuring maternal psychosocial health.\n"
			+ "DATA COLLECTION AND ANALYSIS: A systematic critical appraisal of all included studies was undertaken using the Journal of the American Medical Association (JAMA) published criteria. The data were summarised using effect sizes but were not combined in a meta-analysis due to the small number of studies within each group and the presence of significant heterogeneity.\n"
			+ "MAIN RESULTS: A total of 22 studies were included in the review but only 17 provided sufficient data to calculate effect sizes. These 17 studies reported on a total of 59 outcomes including depression, anxiety, stress, self-esteem, social competence, social support, guilt, mood, automatic thoughts, dyadic adjustment, psychiatric morbidity, irrationality, anger and aggression, mood, attitude, personality, and beliefs. Approximately 22% of the outcomes measured suggested significant differences favouring the intervention group. A further 40% showed differences favouring the intervention group but which failed to achieve conventional levels of statistical significance, in some cases due to the small numbers that were used. Approximately 38% of outcomes suggested no evidence of effectiveness.\n"
			+ "REVIEWER'S CONCLUSIONS: It is suggested that parenting programmes can make a significant contribution to the improvement of psychosocial health in mothers. While the critical appraisal suggests some variability in the quality of the included studies, it is concluded that there is sufficient evidence to support their use with diverse groups of parents. However, it is also suggested that some caution should be exercised before the results are generalised to parents irrespective of the level of pathology present, and that further research is still required.";

	private static final String ABSTRACT_2 = "Modification of the hexahydronaphthalene ring 5-position in simvastatin 2a via oxygenation and oxa replacement afforded two series of derivatives which were evaluated in vitro for inhibition of 3-hydroxy-3-methylglutaryl-coenzyme A reductase and acutely in vivo for oral effectiveness as inhibitors of cholesterogenesis in the rat. Of the compounds selected for further biological evaluation, the 6 beta-methyl-5-oxa 10 and 5 alpha-hydroxy 16 derivatives of 3,4,4a,5-tetrahydro 2a, as well as, the 6 beta-epimer 14 of 16 proved orally active as hypocholesterolemic agents in cholestyramine-primed dogs. Subsequent acute oral metabolism studies in dogs demonstrated that compounds 14 and 16 evoke lower peak plasma drug activity and area-under-the-curve values than does compound 10 and led to the selection of 14 and 16 for toxicological evaluation.";
	private static final Map<String, String> pmid2AbstractMap = new HashMap<String, String>() {
		{
			put("11034741", ABSTRACT_1);
			put("1875346", ABSTRACT_2);
			put("973217", null);
		}
	};

	@Test
	public void testGetAbstractTest() throws IOException, JAXBException, XMLStreamException {
		File sampleMedlineXmlFile = copyClasspathResourceToTemporaryFile(getClass(), SAMPLE_FILE_NAME);
		for (MedlineXmlParser parser = new MedlineXmlParser(sampleMedlineXmlFile); parser.hasNext();) {
			PubmedArticle article = parser.next();
			MedlineCitation citation = article.getMedlineCitation();
			String pmid = citation.getPMID().getvalue();
			if (pmid2AbstractMap.containsKey(pmid)) {
				assertEquals(pmid2AbstractMap.get(pmid), MedlineCitationUtil.getAbstractText(citation));
			}
		}
	}

}