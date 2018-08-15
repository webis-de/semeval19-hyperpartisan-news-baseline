package de.aitools.ie.uima.analysis.tagging;

import is2.data.SentenceData09;
import is2.lemmatizer.Lemmatizer;
import is2.lemmatizer.Options;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.uima.UimaContext;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;

import de.aitools.ie.uima.type.core.Sentence;
import de.aitools.ie.uima.type.core.Token;

/**
 * Wrapper of the very effective Mate Tools Lemmatizer that classifies lemmas 
 * based on a large margin model (Björkelund et. al., COLING 2010).  
 * 
 * Requires token and sentence annotations and produces the lemma features of 
 * the token annotations.
 * 
 * For more details, see http://code.google.com/p/mate-tools/.
 * 
 * @author henning.wachsmuth
 * @author johannes.kiesel@uni-weimar.de
 *
 */
public class MateLemmatizer extends AbstractMateTagger {

  // -------------------------------------------------------------------------
  // REFERENCES
  // -------------------------------------------------------------------------

  /**
   * The mate-tools lemmatizer.
   */
  private Lemmatizer lemmatizer = null;

  // -------------------------------------------------------------------------
  // INITIALIZATION
  // -------------------------------------------------------------------------

  @Override
  protected void initialize(
      final String modelPath, final UimaContext context)
  throws IOException, ResourceInitializationException {
    final Options lemmatizerOptions = 
        new Options(new String[] { "-model", modelPath });
    this.lemmatizer = new Lemmatizer(lemmatizerOptions);
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
      this.lemmatizeSentence(jcas, sentence);
    }
  }

  /**
   * Lemmatizes all tokens of the given sentence.
   * 
   * @param jcas The JCas object
   * @param sentence The sentence
   */
  private void lemmatizeSentence(JCas jcas, Annotation sentence){
    // Get tokens and convert to mate-tools structure
    List<Token> tokens = this.statementToTokens(jcas, sentence);
    List<String> tokenStrings = this.tokensToStrings(tokens);
    tokenStrings.add(0, "<root>");
    SentenceData09 sentenceData = new SentenceData09();
    sentenceData.init(tokenStrings.toArray(new String[0]));
    // Lemmatize and relate lemmas to tokens
    this.lemmatizer.apply(sentenceData);
    if (sentenceData.lemmas != null) {
      for(int i=1; i<sentenceData.lemmas.length; i++){
        tokens.get(i-1).setLemma(sentenceData.lemmas[i]);
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
