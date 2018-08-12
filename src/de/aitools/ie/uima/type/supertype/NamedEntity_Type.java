
/* First created by JCasGen Wed Aug 08 23:11:28 CEST 2018 */
package de.aitools.ie.uima.type.supertype;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.cas.impl.TypeImpl;
import org.apache.uima.cas.Type;

/** The supertype of all named entity types.
 * Updated by JCasGen Sun Aug 12 22:51:23 CEST 2018
 * @generated */
public class NamedEntity_Type extends Entity_Type {
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = NamedEntity.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("de.aitools.ie.uima.type.supertype.NamedEntity");



  /** initialize variables to correspond with Cas Type and Features
	 * @generated
	 * @param jcas JCas
	 * @param casType Type 
	 */
  public NamedEntity_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

  }
}



    