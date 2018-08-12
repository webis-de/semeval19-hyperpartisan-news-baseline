
/* First created by JCasGen Wed Aug 08 23:11:28 CEST 2018 */
package de.aitools.ie.uima.type.core;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.cas.impl.TypeImpl;
import org.apache.uima.cas.Type;
import de.aitools.ie.uima.type.supertype.NamedEntity_Type;

/** A misc entity (as proposed in the CoNLL-2003 shared task.
 * Updated by JCasGen Sun Aug 12 22:51:22 CEST 2018
 * @generated */
public class Misc_Type extends NamedEntity_Type {
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = Misc.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("de.aitools.ie.uima.type.core.Misc");



  /** initialize variables to correspond with Cas Type and Features
	 * @generated
	 * @param jcas JCas
	 * @param casType Type 
	 */
  public Misc_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

  }
}



    