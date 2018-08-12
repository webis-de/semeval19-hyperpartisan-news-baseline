
/* First created by JCasGen Wed Aug 08 23:11:28 CEST 2018 */
package de.aitools.ie.uima.type.core;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.cas.impl.TypeImpl;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.impl.FeatureImpl;
import org.apache.uima.cas.Feature;
import de.aitools.ie.uima.type.supertype.NumericExpression_Type;

/** A date.
 * Updated by JCasGen Sun Aug 12 22:51:22 CEST 2018
 * @generated */
public class Date_Type extends NumericExpression_Type {
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = Date.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("de.aitools.ie.uima.type.core.Date");
 
  /** @generated */
  final Feature casFeat_normalized;
  /** @generated */
  final int     casFeatCode_normalized;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getNormalized(int addr) {
        if (featOkTst && casFeat_normalized == null)
      jcas.throwFeatMissing("normalized", "de.aitools.ie.uima.type.core.Date");
    return ll_cas.ll_getStringValue(addr, casFeatCode_normalized);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setNormalized(int addr, String v) {
        if (featOkTst && casFeat_normalized == null)
      jcas.throwFeatMissing("normalized", "de.aitools.ie.uima.type.core.Date");
    ll_cas.ll_setStringValue(addr, casFeatCode_normalized, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	 * @generated
	 * @param jcas JCas
	 * @param casType Type 
	 */
  public Date_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_normalized = jcas.getRequiredFeatureDE(casType, "normalized", "uima.cas.String", featOkTst);
    casFeatCode_normalized  = (null == casFeat_normalized) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_normalized).getCode();

  }
}



    