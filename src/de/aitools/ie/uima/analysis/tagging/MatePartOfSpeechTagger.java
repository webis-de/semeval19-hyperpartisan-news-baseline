package de.aitools.ie.uima.analysis.tagging;

import is2.data.SentenceData09;
import is2.tag.Options;
import is2.tag.Tagger;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;

import de.aitools.ie.uima.type.core.Sentence;
import de.aitools.ie.uima.type.core.Token;

/**
 * Wrapper of the very effective Mate Tools Part-of-Speech Tagger that 
 * classifies part-of-speech tags based on a large margin model (Bj�rkelund 
 * et. al., COLING 2010).  
 * 
 * Requires token annotations with lemmas and sentence annotations and produces 
 * the part-of-speech features of the token annotations.
 * 
 * Targets at well-formatted texts, but should also work with other texts. 
 * 
 * For more details, see http://code.google.com/p/mate-tools/.
 * 
 * @author henning.wachsmuth
 *
 */
public class MatePartOfSpeechTagger extends JCasAnnotator_ImplBase{
	
	// -------------------------------------------------------------------------
	// UIMA PARAMETERS
	// -------------------------------------------------------------------------

	/**
	 * Config parameter for the model used by the POS tagger 
	 */
	public static final String PARAM_MODEL = "MateToolsPOSTaggerModel";
	
	
	
	// -------------------------------------------------------------------------
	// OBJECTS
	// -------------------------------------------------------------------------

	/**
	 * The mate-tools POS tagger. Notice that every instance of this
	 * analysis engine uses the same instance of the part-of-speech tagger.
	 */
	private Tagger posTagger = null;
	
	/**
	 * The path of the machine learning model of the part-of-speech tagger.
	 */
	private String modelPath = null;
	
	/**
	 * Options for the POS tagger
	 */
	private Options optsTagger;

	
	
	// -------------------------------------------------------------------------
	// INITIALIZATION
	// -------------------------------------------------------------------------

	@Override
	public void initialize(UimaContext aContext) 
	throws ResourceInitializationException {
		super.initialize(aContext);
		// Load model from first existing config path
		String [] newModelPaths = (String []) 
				aContext.getConfigParameterValue(PARAM_MODEL);
		for (String newModelPath : newModelPaths) {
			if (new File(newModelPath).exists()){
				// Load model only if not loaded before
				if (!newModelPath.equals(modelPath)){
					modelPath = newModelPath;
					optsTagger = new Options(new String[]{"-model", modelPath});
					posTagger = new Tagger(optsTagger);
				}
				break;
			}
		}
	}

	
	
	// -------------------------------------------------------------------------
	// PROCESSING
	// -------------------------------------------------------------------------

	@Override
	public void process(JCas jcas){
		FSIterator<Annotation> sentenceIter = 
				jcas.getAnnotationIndex(Sentence.type).iterator();
		while (sentenceIter.hasNext()){
			Annotation sentence = sentenceIter.next();
			this.tagSentence(jcas, sentence);
		}
	}

	/**
	 * Creates POS tags for all tokens of the given sentence.
	 * 
	 * @param jcas The JCas object
	 * @param sentence The sentence
	 */
	private void tagSentence(JCas jcas, Annotation sentence){
		// Get tokens and convert to mate-tools structure
		List<Token> tokens = this.statementToTokens(jcas, sentence);
		List<String> tokenStrings = this.tokensToStrings(tokens);
		if (!tokenStrings.isEmpty()) {
    	SentenceData09 sentenceData = new SentenceData09();
    	sentenceData.init(tokenStrings.toArray(new String[0]));
    	// Tag and relate POS tags to tokens
    	posTagger.tag(sentenceData);
    	for(int i=0; i<sentenceData.ppos.length; i++) {
    		tokens.get(i).setPos(sentenceData.ppos[i]);
    	}
		}
	}
	
	/**
	 * Converts the given annotation to the list of tokens contained within
	 * that annotation and returns the list.
	 * 
	 * @param jcas The JCas object
	 * @param annotation The annotation
	 * @return The list of tokens
	 */
	private List<Token> statementToTokens(JCas jcas, 
			Annotation annotation){
		List<Token> tokens = new ArrayList<Token>();
		FSIterator<Annotation> tokenIter = 
				jcas.getAnnotationIndex(Token.type).subiterator(annotation);
		while (tokenIter.hasNext()){
			Token token = (Token) tokenIter.next();
			tokens.add(token);	
		}
		return tokens;
	}
	
	/**
	 * Converts the given list of tokens to a list of the strings that refer to
	 * the tokens.
	 * 
	 * @param tokens The list of tokens
	 * @return The list of token strings
	 */
	private List<String> tokensToStrings(List<Token> tokens){
		List<String> strings = new ArrayList<String>();
		for (Token token : tokens) 
			strings.add(token.getCoveredText());
		return strings;
	}
}
