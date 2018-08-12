

/* First created by JCasGen Sun Aug 12 22:50:02 CEST 2018 */
package de.aitools.ie.uima.type.news;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import de.aitools.ie.uima.type.supertype.Unit;


/** A link to another web page
 * Updated by JCasGen Sun Aug 12 22:51:23 CEST 2018
 * XML source: /home/dogu3912/eclipse-workspace/code/semeval19-hyperpartisan-news-baseline/conf/uima/typesystems/NewsArticleTypeSystem.xml
 * @generated */
public class Link extends Unit {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(Link.class);
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int type = typeIndexID;
  /** @generated
   * @return index of the type  
   */
  @Override
  public              int getTypeIndexID() {return typeIndexID;}
 
  /** Never called.  Disable default constructor
   * @generated */
  protected Link() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated
   * @param addr low level Feature Structure reference
   * @param type the type of this Feature Structure 
   */
  public Link(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated
   * @param jcas JCas to which this Feature Structure belongs 
   */
  public Link(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated
   * @param jcas JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end offset to the end spot in the SofA 
  */  
  public Link(JCas jcas, int begin, int end) {
    super(jcas);
    setBegin(begin);
    setEnd(end);
    readObject();
  }   

  /** 
   * <!-- begin-user-doc -->
   * Write your own initialization here
   * <!-- end-user-doc -->
   *
   * @generated modifiable 
   */
  private void readObject() {/*default - does nothing empty block */}
     
 
    
  //*--------------*
  //* Feature: external

  /** getter for external - gets Whether the target page is from another domain as this article
   * @generated
   * @return value of the feature 
   */
  public boolean getExternal() {
    if (Link_Type.featOkTst && ((Link_Type)jcasType).casFeat_external == null)
      jcasType.jcas.throwFeatMissing("external", "de.aitools.ie.uima.type.news.Link");
    return jcasType.ll_cas.ll_getBooleanValue(addr, ((Link_Type)jcasType).casFeatCode_external);}
    
  /** setter for external - sets Whether the target page is from another domain as this article 
   * @generated
   * @param v value to set into the feature 
   */
  public void setExternal(boolean v) {
    if (Link_Type.featOkTst && ((Link_Type)jcasType).casFeat_external == null)
      jcasType.jcas.throwFeatMissing("external", "de.aitools.ie.uima.type.news.Link");
    jcasType.ll_cas.ll_setBooleanValue(addr, ((Link_Type)jcasType).casFeatCode_external, v);}    
   
    
  //*--------------*
  //* Feature: href

  /** getter for href - gets URL of the target page, only given for external links
   * @generated
   * @return value of the feature 
   */
  public String getHref() {
    if (Link_Type.featOkTst && ((Link_Type)jcasType).casFeat_href == null)
      jcasType.jcas.throwFeatMissing("href", "de.aitools.ie.uima.type.news.Link");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Link_Type)jcasType).casFeatCode_href);}
    
  /** setter for href - sets URL of the target page, only given for external links 
   * @generated
   * @param v value to set into the feature 
   */
  public void setHref(String v) {
    if (Link_Type.featOkTst && ((Link_Type)jcasType).casFeat_href == null)
      jcasType.jcas.throwFeatMissing("href", "de.aitools.ie.uima.type.news.Link");
    jcasType.ll_cas.ll_setStringValue(addr, ((Link_Type)jcasType).casFeatCode_href, v);}    
  }

    