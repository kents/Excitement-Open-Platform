package eu.excitementproject.eop.biutee.rteflow.endtoend;

import java.util.List;

import org.apache.log4j.Logger;

import eu.excitementproject.eop.biutee.classifiers.Classifier;
import eu.excitementproject.eop.biutee.classifiers.LinearClassifier;
import eu.excitementproject.eop.biutee.script.OperationsScript;
import eu.excitementproject.eop.biutee.utilities.BiuteeException;
import eu.excitementproject.eop.common.representation.parse.representation.basic.Info;
import eu.excitementproject.eop.common.representation.parse.tree.dependency.basic.BasicNode;

/**
 * 
 * @author Asher Stern
 * @since Jul 14, 2013
 *
 * @param <I>
 * @param <P>
 */
public class Tester<I extends Instance, P extends Proof>
{
	public Tester(Dataset<I> dataset,
			List<OperationsScript<Info, BasicNode>> scripts,
			ClassifierGenerator classifierGenerator, Prover<I, P> prover,
			int numberOfThreads, ResultsFactory<I, P> resultsFactory)
	{
		super();
		this.dataset = dataset;
		this.scripts = scripts;
		this.classifierGenerator = classifierGenerator;
		this.prover = prover;
		this.numberOfThreads = numberOfThreads;
		this.resultsFactory = resultsFactory;
	}
	
	public void test() throws BiuteeException
	{
		LinearClassifier classifierForSearch = classifierGenerator.loadClassifierForSearch();
		Classifier classifierForPredictions = classifierGenerator.loadClassifierForPredictions();
		
		DatasetProcessor<I,P> datasetProcessor = new DatasetProcessor<I,P>(dataset,scripts,classifierForSearch,prover,numberOfThreads);
		datasetProcessor.process();
		List<InstanceAndProof<I, P>> proofs = datasetProcessor.getProofs();
		
		results = resultsFactory.createResults(proofs, classifierForPredictions);
		results.compute();
		logger.info("Test done. Results:\n"+results.print());
	}
	
	

	public Results<I, P> getResults() throws BiuteeException
	{
		if (null==results) throw new BiuteeException("Results have not yet been computed.");
		return results;
	}



	// input
	private final Dataset<I> dataset;
	private final List<OperationsScript<Info, BasicNode>> scripts;
	private final ClassifierGenerator classifierGenerator;
	private final Prover<I, P> prover;
	private final int numberOfThreads;
	private final ResultsFactory<I, P> resultsFactory;
	
	// output
	private Results<I, P> results = null;
	
	private static final Logger logger = Logger.getLogger(Tester.class);
}
