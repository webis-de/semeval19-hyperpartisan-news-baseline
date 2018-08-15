
/* First created by JCasGen Sun Aug 12 22:50:02 CEST 2018 */
package de.aitools.ie.uima.type.news;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.cas.impl.TypeImpl;
import org.apache.uima.cas.Type;
import de.aitools.ie.uima.type.supertype.Unit_Type;

/** A piece of text that is quoted from somewhere else
 * Updated by JCasGen Wed Aug 15 23:47:38 CEST 2018
 * @generated */
public class Quotation_Type extends Unit_Type {
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = Quotation.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("de.aitools.ie.uima.type.news.Quotation");



  /** initialize variables to correspond with Cas Type and Features
	 * @generated
	 * @param jcas JCas
	 * @param casType Type 
	 */
  public Quotation_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

  }
}



    