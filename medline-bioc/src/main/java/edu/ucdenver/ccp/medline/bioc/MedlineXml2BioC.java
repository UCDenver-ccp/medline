package edu.ucdenver.ccp.medline.bioc;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.GZIPOutputStream;

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.medline.MedlineCitation;

import com.pengyifan.bioc.BioCCollection;
import com.pengyifan.bioc.BioCDocument;
import com.pengyifan.bioc.BioCPassage;
import com.pengyifan.bioc.io.BioCCollectionWriter;

import edu.ucdenver.ccp.common.file.FileUtil;
import edu.ucdenver.ccp.medline.core.PmidDirectoryFactory;
import edu.ucdenver.ccp.medline.xml.MedlineCitationUtil;
import edu.ucdenver.ccp.medline.xml.MedlineFileOrderer;
import edu.ucdenver.ccp.medline.xml.MedlineXmlParser;

/**
 * This class facilitates the conversion of Medline XML files to the BioC
 * format.
 */
public class MedlineXml2BioC {

	private static final Logger logger = Logger.getLogger(MedlineXml2BioC.class);

	private static final String BIOC_COLLECTION_SOURCE = "medline";

	/**
	 * Process the input Medline XML document and generate individual BioC XML
	 * files for each record therein. Stores the resulting BioC XML files in a
	 * two-level directory structure based on PubMed ID in order to prevent too
	 * many files from ending up in a single directory.
	 * 
	 * @param medlineXmlFile
	 *            MEDLINE XML file to be processed
	 * @param baseOutputDirectory
	 *            base directory where the BioC transformations will be stored.
	 *            Under the base directory will be a two-level directory
	 *            structure in order to spread out the 24+ million MEDLINE
	 *            documents. This two-level structure will be based on the
	 *            PubMed ID of a given document. Output files are compressed
	 *            using gzip.
	 * @throws IOException
	 * @throws XMLStreamException
	 * @throws JAXBException
	 */
	public static void processMedlineXmlFile(File medlineXmlFile, File baseOutputDirectory) throws IOException,
			XMLStreamException, JAXBException {

		logger.info("BioC conversion in progress for: " + medlineXmlFile.getAbsolutePath());
		int count = 0;
		for (MedlineXmlParser parser = new MedlineXmlParser(medlineXmlFile); parser.hasNext();) {
			if (count++ % 10000 == 0) {
				logger.info("conversion progress: " + (count - 1));
			}
			MedlineCitation citation = parser.next();
			int pmid = Integer.parseInt(citation.getPMID().getvalue());
			String title = citation.getArticle().getArticleTitle();
			String abstractText = MedlineCitationUtil.getAbstractText(citation);

			BioCDocument document = new BioCDocument(Integer.toString(pmid));
			BioCPassage titlePassage = new BioCPassage();
			titlePassage.setOffset(0);
			titlePassage.setText(title);
			document.addPassage(titlePassage);

			if (abstractText != null) {
				BioCPassage abstractPassage = new BioCPassage();
				abstractPassage.setOffset(title.length() + 1);
				abstractPassage.setText(abstractText);
				document.addPassage(abstractPassage);
			}

			BioCCollection collection = new BioCCollection(BIOC_COLLECTION_SOURCE, medlineXmlFile.getName());
			collection.addDocument(document);
			File outputDirectory = PmidDirectoryFactory.getDirectory(baseOutputDirectory, pmid);
			File outputFile = new File(outputDirectory, pmid + ".bioc.xml.gz");
			FileUtil.mkdir(outputFile.getParentFile());
			OutputStream os = new GZIPOutputStream(new FileOutputStream(outputFile));
			BioCCollectionWriter collectionWriter = new BioCCollectionWriter(os);
			collectionWriter.writeCollection(collection);
			os.close();
			collectionWriter.close();
		}

	}

	/**
	 * process the input directory of medline XML files to create BioC format
	 * versions (1 output file for each PubMed ID)
	 * 
	 * @param directory
	 * @param baseOutputDirectory
	 * @throws IOException
	 * @throws XMLStreamException
	 * @throws JAXBException
	 */
	public static void processDirectoryOfMedlineXml(File directory, File baseOutputDirectory) throws IOException,
			XMLStreamException, JAXBException {
		for (File medlineXmlFile : MedlineFileOrderer.getOrderedMedlineFileIterable(directory)) {
			processMedlineXmlFile(medlineXmlFile, baseOutputDirectory);
		}
	}

	/**
	 * @param args
	 *            args[0] = Medline XML file or directory to process<br>
	 *            args[1] = base output directory to store BioC formatted files
	 *            (1 per PubMed ID processed)
	 */
	public static void main(String[] args) {
		BasicConfigurator.configure();
		File input = new File(args[0]);
		File baseOutputDirectory = new File(args[1]);

		try {
			if (input.isFile()) {
				processMedlineXmlFile(input, baseOutputDirectory);
			} else {
				processDirectoryOfMedlineXml(input, baseOutputDirectory);
			}
		} catch (IOException | XMLStreamException | JAXBException e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}
}
