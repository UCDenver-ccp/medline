package edu.ucdenver.ccp.medline.bioc;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.zip.GZIPOutputStream;

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.logging.log4j.core.config.DefaultConfiguration;
import org.medline.MedlineCitation;
import org.medline.PubmedArticle;

import com.pengyifan.bioc.BioCCollection;
import com.pengyifan.bioc.BioCDocument;
import com.pengyifan.bioc.BioCPassage;
import com.pengyifan.bioc.io.BioCCollectionWriter;

import edu.ucdenver.ccp.common.file.CharacterEncoding;
import edu.ucdenver.ccp.common.file.FileReaderUtil;
import edu.ucdenver.ccp.common.file.FileUtil;
import edu.ucdenver.ccp.common.file.FileWriterUtil;
import edu.ucdenver.ccp.medline.core.PmidDirectoryFactory;
import edu.ucdenver.ccp.medline.xml.MedlineCitationUtil;
import edu.ucdenver.ccp.medline.xml.MedlineFileOrderer;
import edu.ucdenver.ccp.medline.xml.MedlineXmlParser;

/**
 * This class facilitates the conversion of Medline XML files to the BioC
 * format.
 */
public class MedlineXml2BioC {

	private static final Logger logger = LogManager.getLogger(MedlineXml2BioC.class);

	private static final String BIOC_COLLECTION_SOURCE = "medline";

	public enum OutputSegmentation {
		ONE_FILE_PER_PUBMED_ID, ONE_OUTPUT_FILE_PER_INPUT_XML_FILE
	}

	/**
	 * Process the input Medline XML document and generate BioC XML based on the
	 * OutputSegmentation specified. If ONE_FILE_PER_PUBMED_ID: Stores the
	 * resulting BioC XML files in a two-level directory structure based on
	 * PubMed ID in order to prevent too many files from ending up in a single
	 * directory.
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
	 * @param biocLogFile
	 *            a file that lists the absolute paths of all bioc files that
	 *            have been generated from the input XML file. This is only used
	 *            if the OutputSegmentation == ONE_FILE_PER_PUBMED_ID
	 * @throws IOException
	 * @throws XMLStreamException
	 * @throws JAXBException
	 */
	public static void processMedlineXmlFile(File medlineXmlFile, File baseOutputDirectory,
			OutputSegmentation outputSegmentation, File biocLogFile) throws IOException, XMLStreamException,
			JAXBException {

		BufferedWriter biocFileLogger = null;

		logger.info("BioC conversion in progress for: " + medlineXmlFile.getAbsolutePath());
		BioCCollection multiPmidCollection = null;
		if (outputSegmentation == OutputSegmentation.ONE_OUTPUT_FILE_PER_INPUT_XML_FILE) {
			multiPmidCollection = new BioCCollection(BIOC_COLLECTION_SOURCE, medlineXmlFile.getName());
		} else if (outputSegmentation == OutputSegmentation.ONE_FILE_PER_PUBMED_ID) {
			biocFileLogger = FileWriterUtil.initBufferedWriter(biocLogFile);
		}
		int count = 0;
		for (MedlineXmlParser parser = new MedlineXmlParser(medlineXmlFile); parser.hasNext();) {
			if (count++ % 10000 == 0) {
				logger.info("conversion progress: " + (count - 1));
			}
			PubmedArticle article = parser.next();
			MedlineCitation citation = article.getMedlineCitation();
			int pmid = Integer.parseInt(citation.getPMID().getvalue());
			String title = citation.getArticle().getArticleTitle().getvalue();
			String abstractText = MedlineCitationUtil.getAbstractText(citation);

			BioCDocument document = new BioCDocument(Integer.toString(pmid));
			BioCPassage titlePassage = new BioCPassage();
			titlePassage.setOffset(0);
			titlePassage.setText(title);
			titlePassage.putInfon("type", "title");
			document.addPassage(titlePassage);

			/*
			 * add a second passage regardless. the GNormPlus code seems to fail
			 * if there aren't at least two passages for a given document
			 */
			BioCPassage abstractPassage = new BioCPassage();
			abstractPassage.setOffset(title.length() + 1);
			if (abstractText != null) {
				abstractPassage.putInfon("type", "abstract");
				abstractPassage.setText(abstractText);
			}
			document.addPassage(abstractPassage);

			if (outputSegmentation == OutputSegmentation.ONE_FILE_PER_PUBMED_ID) {
				BioCCollection singlePmidCollection = new BioCCollection(BIOC_COLLECTION_SOURCE,
						medlineXmlFile.getName());
				singlePmidCollection.addDocument(document);
				File outputDirectory = PmidDirectoryFactory.getDirectory(baseOutputDirectory, pmid);
				File outputFile = new File(outputDirectory, pmid + ".bioc.xml.gz");
				biocFileLogger.write(outputFile.getAbsolutePath() + "\n");
				FileUtil.mkdir(outputFile.getParentFile());
				OutputStream os = new GZIPOutputStream(new FileOutputStream(outputFile));
				BioCCollectionWriter collectionWriter = new BioCCollectionWriter(os);
				collectionWriter.writeCollection(singlePmidCollection);
				os.close();
				collectionWriter.close();
			} else {
				multiPmidCollection.addDocument(document);
			}
		}

		/* write the collection to file if this is a multi-pmid collection */
		if (outputSegmentation == OutputSegmentation.ONE_OUTPUT_FILE_PER_INPUT_XML_FILE) {
			String outputFileName = medlineXmlFile.getName().replace(".xml", ".bioc.xml");
			if (!outputFileName.endsWith(".gz")) {
				outputFileName += ".gz";
			}
			File outputFile = new File(baseOutputDirectory, outputFileName);
			OutputStream os = new GZIPOutputStream(new FileOutputStream(outputFile));
			BioCCollectionWriter collectionWriter = new BioCCollectionWriter(os);
			collectionWriter.writeCollection(multiPmidCollection);
			os.close();
			collectionWriter.close();
		}

		if (biocFileLogger != null) {
			biocFileLogger.close();
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
	public static void processDirectoryOfMedlineXml(File directory, File baseOutputDirectory,
			OutputSegmentation outputSegmentation, File biocLogFile) throws IOException, XMLStreamException,
			JAXBException {
		for (File medlineXmlFile : MedlineFileOrderer.getOrderedMedlineFileIterable(directory)) {
			processMedlineXmlFile(medlineXmlFile, baseOutputDirectory, outputSegmentation, biocLogFile);
		}
	}

	/**
	 * @param args
	 *            args[0] = Medline XML file or directory to process<br>
	 *            args[1] = base output directory to store BioC formatted files
	 *            (1 per PubMed ID processed) <br>
	 *            args[2] = output segmentation <br>
	 *            args[3] = bioc log file - a file that logs the absolute path
	 *            for each generated bioc file <br>
	 *            args[4] = <optional> "list" to signify that the first argument
	 *            is a list of XML files to process
	 * 
	 */
	public static void main(String[] args) {
		Configurator.initialize(new DefaultConfiguration());
	    Configurator.setRootLevel(Level.INFO);
		File input = new File(args[0]);
		File baseOutputDirectory = new File(args[1]);
		OutputSegmentation outputSegmentation = OutputSegmentation.valueOf(args[2]);
		File biocLogFile = null;
		if (outputSegmentation == OutputSegmentation.ONE_FILE_PER_PUBMED_ID) {
			biocLogFile = new File(args[3]);
		}
		String inputDesignation = null;
		if (args.length > 4) {
			inputDesignation = args[4];
		}

		try {
			if (inputDesignation != null && inputDesignation.equals("list")) {
				List<String> filesToProcess = FileReaderUtil.loadLinesFromFile(input, CharacterEncoding.UTF_8);
				for (String fileToProcess : filesToProcess) {
					processMedlineXmlFile(new File(fileToProcess), baseOutputDirectory, outputSegmentation, biocLogFile);
				}
			} else {
				if (input.isFile()) {
					processMedlineXmlFile(input, baseOutputDirectory, outputSegmentation, biocLogFile);
				} else {
					processDirectoryOfMedlineXml(input, baseOutputDirectory, outputSegmentation, biocLogFile);
				}
			}
		} catch (IOException | XMLStreamException | JAXBException e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}
}
