package eu.excitementproject.eop.lap.biu.test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import eu.excitementproject.eop.common.representation.parse.tree.dependency.basic.BasicNode;
import eu.excitementproject.eop.lap.biu.PreprocessUtilities;
import eu.excitementproject.eop.lap.biu.en.ner.stanford.StanfordNamedEntityRecognizer;
import eu.excitementproject.eop.lap.biu.en.parser.BasicParser;
import eu.excitementproject.eop.lap.biu.en.parser.ParserRunException;
import eu.excitementproject.eop.lap.biu.en.parser.easyfirst.EasyFirstParser;
import eu.excitementproject.eop.lap.biu.en.postagger.stanford.MaxentPosTagger;
import eu.excitementproject.eop.lap.biu.en.sentencesplit.LingPipeSentenceSplitter;
import eu.excitementproject.eop.lap.biu.en.tokenizer.MaxentTokenizer;
import eu.excitementproject.eop.lap.biu.en.tokenizer.Tokenizer;
import eu.excitementproject.eop.lap.biu.en.tokenizer.TokenizerException;
import eu.excitementproject.eop.lap.biu.ner.NamedEntityRecognizer;
import eu.excitementproject.eop.lap.biu.ner.NamedEntityRecognizerException;
import eu.excitementproject.eop.lap.biu.postagger.PosTagger;
import eu.excitementproject.eop.lap.biu.postagger.PosTaggerException;
import eu.excitementproject.eop.lap.biu.sentencesplit.SentenceSplitter;
import eu.excitementproject.eop.lap.biu.sentencesplit.SentenceSplitterException;

/***
 * Uses BIU LAP tools to build parse trees of a given text.<BR>
 * Used for testing.<BR>
 * The choice of tools and their parameters are hard-coded, according to what's required in the tests.
 * 
 * @author Ofer Bronstein
 * @since March 2013
 */
public class BiuTreeBuilder {

	public BiuTreeBuilder() throws TokenizerException, PosTaggerException, NamedEntityRecognizerException, ParserRunException {
		splitter = new LingPipeSentenceSplitter();
		tokenizer = new MaxentTokenizer();
		tagger = new MaxentPosTagger("..\\third-party\\stanford-postagger-full-2008-09-28\\models\\left3words-wsj-0-18.tagger");
		ner = new StanfordNamedEntityRecognizer(new File("..\\third-party\\stanford-ner-2009-01-16\\classifiers\\ner-eng-ie.crf-3-all2008-distsim.ser.gz"));
		parser = new EasyFirstParser(
				"localhost",
				8080,
				tokenizer,
				tagger
				);
		
		ner.init();
		parser.init();
	}
	
	/**
	 * @param text Some full text
	 * @return A list of roots of trees, one for each sentence in the text, according to the order of the sentences.
	 * @throws SentenceSplitterException
	 * @throws TokenizerException
	 * @throws PosTaggerException
	 * @throws NamedEntityRecognizerException
	 * @throws ParserRunException
	 */
	public List<BasicNode> buildTrees(String text) throws SentenceSplitterException, TokenizerException, PosTaggerException, NamedEntityRecognizerException, ParserRunException {
		List<BasicNode> result = new ArrayList<BasicNode>();
		
		splitter.setDocument(text);
		splitter.split();
		List<String> sentences = splitter.getSentences();
		
		for (String sentence : sentences) {
//			tokenizer.setSentence(sentence);
//			tokenizer.tokenize();
//			List<String> tokens = tokenizer.getTokenizedSentence();
//			
//			tagger.setTokenizedSentence(tokens);
//			tagger.process();
//			List<PosTaggedToken> taggedTokens = tagger.getPosTaggedTokens();
//			
//			ner.setSentence(tokens);
//			ner.recognize();
//			List<NamedEntityWord> neWords = ner.getAnnotatedSentence();
//			
//			parser.setSentence(taggedTokens);
//			parser.parse();
//			addNeToNodes(parser.getMutableParseTree(), parser.getNodesOrderedByWords(), neWords);
			BasicNode root = PreprocessUtilities.generateParseTree(sentence, parser, ner, true);
			result.add(root);
		}
		
		return result;
	}
	
//	/**
//	 * Adds NE tags to given tree.
//	 * Taken (with some modifications) from {@link eu.excitementproject.eop.biutee.rteflow.preprocess.PreprocessUtilities}
//	 */
//	private void addNeToNodes(BasicConstructionNode mutableParseTree, ArrayList<BasicConstructionNode> nodes, List<NamedEntityWord> neWords) {
//
//		// Handle normal nodes
//		Matcher<NamedEntityWord, BasicConstructionNode> matcher = new Matcher<NamedEntityWord, BasicConstructionNode>(neWords.iterator(), nodes.iterator(),NamedEntityMergeServices.getMatchFinder(),NamedEntityMergeServices.getOperator());
//		matcher.makeMatchOperation();
//
//		// Handle extra nodes
//		Set<BasicConstructionNode> mutableNodes = AbstractNodeUtils.treeToSet(mutableParseTree);
//		for (BasicConstructionNode mutableNode : mutableNodes)
//		{
//			if (mutableNode.getAntecedent()!=null)
//			{
//				BasicConstructionNode antecedent = AbstractNodeUtils.getDeepAntecedentOf(mutableNode);
//				if (antecedent.getInfo().getNodeInfo().getNamedEntityAnnotation()!=null)
//				{
//					NamedEntity ne = antecedent.getInfo().getNodeInfo().getNamedEntityAnnotation();
//					Info newInfo = new DefaultInfo(mutableNode.getInfo().getId(), new DefaultNodeInfo(mutableNode.getInfo().getNodeInfo().getWord(), mutableNode.getInfo().getNodeInfo().getWordLemma(), mutableNode.getInfo().getNodeInfo().getSerial(), ne, mutableNode.getInfo().getNodeInfo().getSyntacticInfo()), mutableNode.getInfo().getEdgeInfo());
//					mutableNode.setInfo(newInfo);
//				}
//			}
//		}
//	}

	private SentenceSplitter splitter;
	private Tokenizer tokenizer;
	private PosTagger tagger;
	private NamedEntityRecognizer ner;
	private BasicParser parser;
	
}
