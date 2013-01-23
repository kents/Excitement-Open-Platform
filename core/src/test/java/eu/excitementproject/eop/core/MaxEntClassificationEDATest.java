package eu.excitementproject.eop.core;

import static org.junit.Assert.assertTrue;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.logging.Logger;

import org.apache.uima.jcas.JCas;
import org.junit.Test;

import eu.excitementproject.eop.common.configuration.CommonConfig;
import eu.excitementproject.eop.lap.LAPAccess;
import eu.excitementproject.eop.lap.LAPException;
import eu.excitementproject.eop.lap.PlatformCASProber;
import eu.excitementproject.eop.lap.dkpro.TreeTaggerDE;
import eu.excitementproject.eop.lap.dkpro.TreeTaggerEN;

/**
 * The test contains three parts: 1) use LAP to do preprocessing and generate
 * xmi files; 2) train a MaxEnt model; 3) test on the input example(s).
 * 
 * @author Rui
 */
public class MaxEntClassificationEDATest {
	static Logger logger = Logger.getLogger(MaxEntClassificationEDATest.class
			.getName());
	
	@Test
	public void test() {
		// testLAP_DE() is a very very long test. (More than build process itself) 
		// Commented for that reason. 
		// Uncomment the following tests to do the Full Test 
		// on MaxEntClassificationEDA --Gil 
		
		// Rui: if you want to test MaxEntClassificationEDA with different lexical resources, please check MaxEntClassificationEDA.initialize() for the moment
		// Rui: Make sure the trained model is consistent with the testing configuration!
				
		/* German RTE tests
		testLAP_DE(); 
		testTraining_DE(); 
		testTesting_SingleTH_DE(); 
		testTesting_MultiTH_DE(); 
		testTesting_MultiTH_AND_Output_DE("./target/DE/" + this.getClass().getName() + "_" + "config" + ".txt");
		*/
		
		// Rui: testLAP_EN(), testTraining_EN(), and testTesting_MultiTH_EN() also take long time
		/* English RTE tests
		testLAP_EN();
		testTraining_EN();
		testTesting_SingleTH_EN();
		testTesting_MultiTH_EN();
		testTesting_MultiTH_AND_Output_EN("./target/EN/" + this.getClass().getName() + "_" + "config" + ".txt");
		 */
	}
	
	public void testLAP_DE() {
		File inputFile = null;
		File outputDir = null;
		
		// generate XMI files for the training data
		inputFile = new File("./src/test/resources/German_dev.xml");
		assertTrue(inputFile.exists());
		outputDir = new File("./target/DE/dev/");
		if (!outputDir.exists()) {
			outputDir.mkdirs();
		}
		assertTrue(outputDir.exists());

		LAPAccess lap = null;

		try {
			lap = new TreeTaggerDE();
			lap.processRawInputFormat(inputFile, outputDir);
		} catch (LAPException e) {
			logger.info(e.getMessage());
		}
		
		// generate XMI files for the testing data
		inputFile = new File("./src/test/resources/German_test.xml");
		assertTrue(inputFile.exists());
		outputDir = new File("./target/DE/test/");
		if (!outputDir.exists()) {
			outputDir.mkdirs();
		}
		assertTrue(outputDir.exists());
		
		try {
			lap = new TreeTaggerDE();
			lap.processRawInputFormat(inputFile, outputDir);
		} catch (LAPException e) {
			logger.info(e.getMessage());
		}
	}
	
	public void testLAP_EN() {
		File inputFile = null;
		File outputDir = null;
		
		// generate XMI files for the training data
		inputFile = new File("./src/test/resources/English_dev.xml");
		assertTrue(inputFile.exists());
		outputDir = new File("./target/EN/dev/");
		if (!outputDir.exists()) {
			outputDir.mkdirs();
		}
		assertTrue(outputDir.exists());

		LAPAccess lap = null;

		try {
			lap = new TreeTaggerEN();
			lap.processRawInputFormat(inputFile, outputDir);
		} catch (LAPException e) {
			logger.info(e.getMessage());
		}
		
		// generate XMI files for the testing data
		inputFile = new File("./src/test/resources/English_test.xml");
		assertTrue(inputFile.exists());
		outputDir = new File("./target/EN/test/");
		if (!outputDir.exists()) {
			outputDir.mkdirs();
		}
		assertTrue(outputDir.exists());
		
		try {
			lap = new TreeTaggerEN();
			lap.processRawInputFormat(inputFile, outputDir);
		} catch (LAPException e) {
			logger.info(e.getMessage());
		}
	}
	
	public void testTraining_DE() {
		File trainingDir = null;
		trainingDir = new File("./target/DE/dev/");
		assertTrue(trainingDir.exists());
		
		MaxEntClassificationEDA meceda = new MaxEntClassificationEDA();
		meceda.setLanguage("DE");

		CommonConfig config = null;

		try {
			meceda.setTrain(true);
			meceda.initialize(config);
			File modelFile = new File(meceda.getModelFile());
			assertTrue(!modelFile.exists());

			meceda.startTraining(config);
			assertTrue(modelFile.exists());
			logger.info("training done");
		} catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	public void testTraining_EN() {
		File trainingDir = null;
		trainingDir = new File("./target/EN/dev/");
		assertTrue(trainingDir.exists());
		
		MaxEntClassificationEDA meceda = new MaxEntClassificationEDA();
		meceda.setLanguage("EN");

		CommonConfig config = null;

		try {
			meceda.setTrain(true);
			meceda.initialize(config);
			File modelFile = new File(meceda.getModelFile());
			assertTrue(!modelFile.exists());

			meceda.startTraining(config);
			assertTrue(modelFile.exists());
			logger.info("training done");
		} catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	public void testTesting_SingleTH_DE() {
		MaxEntClassificationEDA meceda = new MaxEntClassificationEDA();
		meceda.setLanguage("DE");
		
		CommonConfig config = null;
		
		LAPAccess lap = null;
		
		try {
			meceda.setTrain(false);
			meceda.initialize(config);
			File modelFile = new File(meceda.getModelFile());
			assertTrue(modelFile.exists());
			
			logger.info("build CASes for input sentence pairs:");
			lap = new TreeTaggerDE();
			JCas test1Cas = lap.generateSingleTHPairCAS("Es wird die Registrierung der Software verlangt, diese ist jedoch nicht möglich. Es wird \"Fehlercode -27\" angezeigt.", "Fehlercode -27 erscheint beim Registrieren");
			JCas test2Cas = lap.generateSingleTHPairCAS("Als ich heute noch einmal etwas weiter machen wollte, stellte ich fest, daß jetzt immer wenn ich den Startzeiger an eine Stelle setze und von dort aus die Preview ansehen möchte, das Anzeigen ganz von Beginn an startet.", "Startzeiger zeigt immer von Anfang an");
//			JCas test1Cas = lap.generateSingleTHPairCAS("Wenn ich auf Preview drücke, beginnt die Anzeige immer von vorn bei Datensatz 1, statt an derStelle wo ich zuletzt bearbeitet habe.", "Startzeiger zeigt immer von Anfang an");
//			JCas test2Cas = lap.generateSingleTHPairCAS("Beim Öffnen des Programmes erscheint die Fehlermeldung\" das smartcard device...\"", "Leider erscheint die Fehlermeldung -9 beim ausführen.");
//			JCas test1Cas = lap.generateSingleTHPairCAS("Leider erscheint die Fehlermeldung -9 beim ausführen.", "Beim Öffnen des Programmes erscheint die Fehlermeldung\" das smartcard device...\"");
//			JCas test2Cas = lap.generateSingleTHPairCAS("Beim Öffnen des Programmes erscheint die Fehlermeldung\" das smartcard device...\"", "Leider erscheint die Fehlermeldung -9 beim ausführen.");
//			JCas test1Cas = lap.generateSingleTHPairCAS("Fehlercode 27 erscheint beim Registrieren", "ein Fehlercode 27 aufgetreten ist");
//			JCas test1Cas = lap.generateSingleTHPairCAS("Fehlercode 27 erscheint beim Registrieren", "meldet das Programm Fehlercode 27");
//			JCas test2Cas = lap.generateSingleTHPairCAS("Fehlercode 27 erscheint beim Registrieren", "bekomme ich den Fehlercode 9");

			logger.info("Answers are:");
			ClassificationTEDecision decision1 = meceda.process(test1Cas);
			System.out.println(decision1.getDecision().toString());
			System.out.println(decision1.getConfidence());
			ClassificationTEDecision decision2 = meceda.process(test2Cas);
			System.out.println(decision2.getDecision().toString());
			System.out.println(decision2.getConfidence());
		} catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	public void testTesting_SingleTH_EN() {
		MaxEntClassificationEDA meceda = new MaxEntClassificationEDA();
		meceda.setLanguage("EN");
		
		CommonConfig config = null;
		
		LAPAccess lap = null;
		
		try {
			meceda.setTrain(false);
			meceda.initialize(config);
			File modelFile = new File(meceda.getModelFile());
			assertTrue(modelFile.exists());
			
			logger.info("build CASes for input sentence pairs:");
			lap = new TreeTaggerEN();
			// ENTAILMENT
			JCas test1Cas = lap.generateSingleTHPairCAS("The person is hired as a postdoc.","The person is hired as a postdoc.");
			// NONENTAILMENT
			JCas test2Cas = lap.generateSingleTHPairCAS("The train was uncomfortable", "The train was comfortable");
			logger.info("Answers are:");
			ClassificationTEDecision decision1 = meceda.process(test1Cas);
			System.out.println(decision1.getDecision().toString());
			System.out.println(decision1.getConfidence());
			ClassificationTEDecision decision2 = meceda.process(test2Cas);
			System.out.println(decision2.getDecision().toString());
			System.out.println(decision2.getConfidence());
		} catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	public void testTesting_MultiTH_DE() {
		File testingDir = null;
		testingDir = new File("./target/DE/test/");
		assertTrue(testingDir.exists());
		
		MaxEntClassificationEDA meceda = new MaxEntClassificationEDA();
		meceda.setLanguage("DE");
		
		CommonConfig config = null;
		
		try {
			meceda.setTrain(false);
			meceda.initialize(config);
			File modelFile = new File(meceda.getModelFile());
			assertTrue(modelFile.exists());
			
			int correct = 0;
			int sum = 0;
			for (File file : testingDir.listFiles()) {
				// ignore all the non-xmi files
				if (!file.getName().endsWith(".xmi")) {
					continue;
				}
				JCas cas = PlatformCASProber.probeXmi(file, null);
				ClassificationTEDecision decision = meceda.process(cas);
				logger.info(decision.getPairID());
				logger.info(meceda.getGoldLabel(cas));
				logger.info(decision.getDecision().toString());
				if (meceda.getGoldLabel(cas).equalsIgnoreCase(decision.getDecision().toString())) {
					correct ++;
				}
				sum ++;
				System.out.println(decision.getConfidence());
			}
			logger.info("The correctly predicted pairs are " + correct + " / " + sum);
		} catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	public void testTesting_MultiTH_EN() {
		File testingDir = null;
		testingDir = new File("./target/EN/test/");
		assertTrue(testingDir.exists());
		
		MaxEntClassificationEDA meceda = new MaxEntClassificationEDA();
		meceda.setLanguage("EN");
		
		CommonConfig config = null;
		
		try {
			meceda.setTrain(false);
			meceda.initialize(config);
			File modelFile = new File(meceda.getModelFile());
			assertTrue(modelFile.exists());
			
			int correct = 0;
			int sum = 0;
			for (File file : testingDir.listFiles()) {
				// ignore all the non-xmi files
				if (!file.getName().endsWith(".xmi")) {
					continue;
				}
				JCas cas = PlatformCASProber.probeXmi(file, null);
				ClassificationTEDecision decision = meceda.process(cas);
				System.out.println(decision.getPairID());
				System.out.println(meceda.getGoldLabel(cas));
				System.out.println(decision.getDecision().toString());
				if (meceda.getGoldLabel(cas).equalsIgnoreCase(decision.getDecision().toString())) {
					correct ++;
				}
				sum ++;
				System.out.println(decision.getConfidence());
			}
			System.out.println("The correctly predicted pairs are " + correct + " / " + sum);
		} catch (Exception e) {
			logger.info(e.getMessage());
		}
	}

	public void testTesting_MultiTH_AND_Output_DE(String outputFile) {
		File testingDir = null;
		testingDir = new File("./target/DE/test/");
		assertTrue(testingDir.exists());
		
		MaxEntClassificationEDA meceda = new MaxEntClassificationEDA();
		meceda.setLanguage("DE");
		
		CommonConfig config = null;
		
		BufferedWriter output = null;
		
		try {
			meceda.setTrain(false);
			meceda.initialize(config);
			File modelFile = new File(meceda.getModelFile());
			assertTrue(modelFile.exists());
			
			output = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputFile)));

			for (File file : testingDir.listFiles()) {
				// ignore all the non-xmi files
				if (!file.getName().endsWith(".xmi")) {
					continue;
				}
				JCas cas = PlatformCASProber.probeXmi(file, null);
				ClassificationTEDecision decision = meceda.process(cas);
				output.write(decision.getPairID());
				output.write("\t");
				output.write(meceda.getGoldLabel(cas).toUpperCase());
				output.write("\t");
				output.write(decision.getDecision().toString().toUpperCase());
				output.write("\t");
				output.write(String.valueOf(decision.getConfidence()));
				output.newLine();
			}
			output.close();
		} catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	public void testTesting_MultiTH_AND_Output_EN(String outputFile) {
		File testingDir = null;
		testingDir = new File("./target/EN/test/");
		assertTrue(testingDir.exists());
		
		MaxEntClassificationEDA meceda = new MaxEntClassificationEDA();
		meceda.setLanguage("EN");
		
		CommonConfig config = null;
		
		BufferedWriter output = null;
		
		try {
			meceda.setTrain(false);
			meceda.initialize(config);
			File modelFile = new File(meceda.getModelFile());
			assertTrue(modelFile.exists());
			
			output = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputFile)));

			for (File file : testingDir.listFiles()) {
				// ignore all the non-xmi files
				if (!file.getName().endsWith(".xmi")) {
					continue;
				}
				JCas cas = PlatformCASProber.probeXmi(file, null);
				ClassificationTEDecision decision = meceda.process(cas);
				output.write(decision.getPairID());
				output.write("\t");
				output.write(meceda.getGoldLabel(cas).toUpperCase());
				output.write("\t");
				output.write(decision.getDecision().toString().toUpperCase());
				output.write("\t");
				output.write(String.valueOf(decision.getConfidence()));
				output.newLine();
			}
			output.close();
		} catch (Exception e) {
			logger.info(e.getMessage());
		}
	}

}
