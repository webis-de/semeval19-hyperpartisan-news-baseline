
/* First created by JCasGen Sun Aug 12 22:50:02 CEST 2018 */
package de.aitools.ie.uima.type.news;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.cas.impl.TypeImpl;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.impl.FeatureImpl;
import org.apache.uima.cas.Feature;
import de.aitools.ie.uima.type.supertype.Unit_Type;

/** A link to another web page
 * Updated by JCasGen Sun Aug 12 22:51:23 CEST 2018
 * @generated */
public class Link_Type extends Unit_Type {
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = Link.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("de.aitools.ie.uima.type.news.Link");
 
  /** @generated */
  final Feature casFeat_external;
  /** @generated */
  final int     casFeatCode_external;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public boolean getExternal(int addr) {
        if (featOkTst && casFeat_external == null)
      jcas.throwFeatMissing("external", "de.aitools.ie.uima.type.news.Link");
    return ll_cas.ll_getBooleanValue(addr, casFeatCode_external);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setExternal(int addr, boolean v) {
        if (featOkTst && casFeat_external == null)
      jcas.throwFeatMissing("external", "de.aitools.ie.uima.type.news.Link");
    ll_cas.ll_setBooleanValue(addr, casFeatCode_external, v);}
    
  
 
  /** @generated */
  final Feature casFeat_href;
  /** @generated */
  final int     casFeatCode_href;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getHref(int addr) {
        if (featOkTst && casFeat_href == null)
      jcas.throwFeatMissing("href", "de.aitools.ie.uima.type.news.Link");
    return ll_cas.ll_getStringValue(addr, casFeatCode_href);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setHref(int addr, String v) {
        if (featOkTst && casFeat_href == null)
      jcas.throwFeatMissing("href", "de.aitools.ie.uima.type.news.Link");
    ll_cas.ll_setStringValue(addr, casFeatCode_href, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	 * @generated
	 * @param jcas JCas
	 * @param casType Type 
	 */
  public Link_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_external = jcas.getRequiredFeatureDE(casType, "external", "uima.cas.Boolean", featOkTst);
    casFeatCode_external  = (null == casFeat_external) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_external).getCode();

 
    casFeat_href = jcas.getRequiredFeatureDE(casType, "href", "uima.cas.String", featOkTst);
    casFeatCode_href  = (null == casFeat_href) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_href).getCode();

  }
}



    