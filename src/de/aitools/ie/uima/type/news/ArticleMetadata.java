

/* First created by JCasGen Sun Aug 12 22:50:02 CEST 2018 */
package de.aitools.ie.uima.type.news;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import de.aitools.ie.uima.type.supertype.Metadata;


/** Metadata for a news article
 * Updated by JCasGen Wed Aug 15 23:47:38 CEST 2018
 * XML source: /home/dogu3912/eclipse-workspace/code/semeval19-hyperpartisan-news-baseline/conf/uima/typesystems/NewsArticleTypeSystem.xml
 * @generated */
public class ArticleMetadata extends Metadata {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(ArticleMetadata.class);
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
  protected ArticleMetadata() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated
   * @param addr low level Feature Structure reference
   * @param type the type of this Feature Structure 
   */
  public ArticleMetadata(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated
   * @param jcas JCas to which this Feature Structure belongs 
   */
  public ArticleMetadata(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated
   * @param jcas JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end offset to the end spot in the SofA 
  */  
  public ArticleMetadata(JCas jcas, int begin, int end) {
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
  //* Feature: id

  /** getter for id - gets ID of the article
   * @generated
   * @return value of the feature 
   */
  public int getId() {
    if (ArticleMetadata_Type.featOkTst && ((ArticleMetadata_Type)jcasType).casFeat_id == null)
      jcasType.jcas.throwFeatMissing("id", "de.aitools.ie.uima.type.news.ArticleMetadata");
    return jcasType.ll_cas.ll_getIntValue(addr, ((ArticleMetadata_Type)jcasType).casFeatCode_id);}
    
  /** setter for id - sets ID of the article 
   * @generated
   * @param v value to set into the feature 
   */
  public void setId(int v) {
    if (ArticleMetadata_Type.featOkTst && ((ArticleMetadata_Type)jcasType).casFeat_id == null)
      jcasType.jcas.throwFeatMissing("id", "de.aitools.ie.uima.type.news.ArticleMetadata");
    jcasType.ll_cas.ll_setIntValue(addr, ((ArticleMetadata_Type)jcasType).casFeatCode_id, v);}    
   
    
  //*--------------*
  //* Feature: publishedAt

  /** getter for publishedAt - gets The date at which the article was published
   * @generated
   * @return value of the feature 
   */
  public String getPublishedAt() {
    if (ArticleMetadata_Type.featOkTst && ((ArticleMetadata_Type)jcasType).casFeat_publishedAt == null)
      jcasType.jcas.throwFeatMissing("publishedAt", "de.aitools.ie.uima.type.news.ArticleMetadata");
    return jcasType.ll_cas.ll_getStringValue(addr, ((ArticleMetadata_Type)jcasType).casFeatCode_publishedAt);}
    
  /** setter for publishedAt - sets The date at which the article was published 
   * @generated
   * @param v value to set into the feature 
   */
  public void setPublishedAt(String v) {
    if (ArticleMetadata_Type.featOkTst && ((ArticleMetadata_Type)jcasType).casFeat_publishedAt == null)
      jcasType.jcas.throwFeatMissing("publishedAt", "de.aitools.ie.uima.type.news.ArticleMetadata");
    jcasType.ll_cas.ll_setStringValue(addr, ((ArticleMetadata_Type)jcasType).casFeatCode_publishedAt, v);}    
   
    
  //*--------------*
  //* Feature: title

  /** getter for title - gets The title of the news article.
   * @generated
   * @return value of the feature 
   */
  public String getTitle() {
    if (ArticleMetadata_Type.featOkTst && ((ArticleMetadata_Type)jcasType).casFeat_title == null)
      jcasType.jcas.throwFeatMissing("title", "de.aitools.ie.uima.type.news.ArticleMetadata");
    return jcasType.ll_cas.ll_getStringValue(addr, ((ArticleMetadata_Type)jcasType).casFeatCode_title);}
    
  /** setter for title - sets The title of the news article. 
   * @generated
   * @param v value to set into the feature 
   */
  public void setTitle(String v) {
    if (ArticleMetadata_Type.featOkTst && ((ArticleMetadata_Type)jcasType).casFeat_title == null)
      jcasType.jcas.throwFeatMissing("title", "de.aitools.ie.uima.type.news.ArticleMetadata");
    jcasType.ll_cas.ll_setStringValue(addr, ((ArticleMetadata_Type)jcasType).casFeatCode_title, v);}    
  }

    