package de.aitools.ie.uima.reader;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.zip.ZipFile;

import org.apache.uima.cas.CAS;
import org.apache.uima.cas.CASException;
import org.apache.uima.collection.CollectionException;
import org.apache.uima.collection.CollectionReader_ImplBase;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.util.InvalidXMLException;
import org.apache.uima.util.Progress;
import org.apache.uima.util.ProgressImpl;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.aitools.ie.uima.type.core.Date;
import de.aitools.ie.uima.type.core.Paragraph;
import de.aitools.ie.uima.type.news.ArticleMetadata;
import de.aitools.ie.uima.type.news.Link;
import de.aitools.ie.uima.type.news.Quotation;
import de.aitools.ie.uima.type.supertype.Unit;
import de.aitools.util.uima.CollectionIterator;
import de.aitools.util.uima.Pipeline;

public class ArticleReader extends CollectionReader_ImplBase {
  
  public static void main(final String[] args)
  throws InvalidXMLException, ResourceInitializationException, IOException {
    final CollectionIterator collection = new CollectionIterator(
        TYPE_SYSTEM_NEWS_ARTICLES,
        Pipeline.DESCRIPTOR_PACKAGE_FOR_COLLECTION_READERS + "ArticleReader.xml",
        PARAM_INPUT, "/home/dogu3912/data/semeval19/test.zip");
    collection.forEachRemaining(jcas -> {
      System.out.println("NEXT");
      jcas.getAnnotationIndex().forEach(annotation -> {
        System.out.println(annotation);
      });
    });
  }
  
  // -------------------------------------------------------------------------
  // CONSTANTS
  // -------------------------------------------------------------------------
  
  public static final String TYPE_SYSTEM_NEWS_ARTICLES =
      Pipeline.DESCRIPTOR_PACKAGE_FOR_TYPE_SYSTEMS +
      "NewsArticleTypeSystem.xml";
  
  public static final String TAG_ARTICLE = "article";
  
  public static final String ATTRIBUTE_ARTICLE_ID = "id";
  
  public static final String ATTRIBUTE_ARTICLE_TITLE = "title";
  
  public static final String ATTRIBUTE_ARTICLE_PUBLISHED_AT = "published-at";
  
  public static final String TAG_LINK = "a";
  
  public static final String ATTRIBUTE_LINK_HREF = "href";
  
  public static final String ATTRIBUTE_LINK_TYPE = "type";
  
  public static final String ATTRIBUTE_LINK_EXTERNAL = "external";
  
  public static final String ATTRIBUTE_LINK_INTERNAL = "internal";
  
  public static final String TAG_PARAGRAPH = "p";
  
  public static final String TAG_QUOTATION = "q";
  
  // -------------------------------------------------------------------------
  // UIMA PARAMETERS
  // -------------------------------------------------------------------------

  /**
   * Parameter that specifies the path of the directory or file with the texts
   * to be processed.
   */
  public static final String PARAM_INPUT =
      Pipeline.DEFAULT_COLLECTION_READER_INPUT_PARAMETER;

  /**
   * Parameter that specifies the encoding of the texts to be processed.
   */
  public static final String PARAM_INPUT_ENCODING = "encoding";
  
  // -------------------------------------------------------------------------
  // MEMBERS
  // -------------------------------------------------------------------------
  
  private Iterator<Element> articleIterator;
  
  private int read;
  
  // -------------------------------------------------------------------------
  // CONSTRUCTORS
  // -------------------------------------------------------------------------
  
  public ArticleReader() {
    this.articleIterator = null;
    this.read = 0;
  }
  
  // -------------------------------------------------------------------------
  // FUNCTIONALITY
  // -------------------------------------------------------------------------

  @Override
  public void initialize() throws ResourceInitializationException {
    super.initialize();
    this.read = 0;
    try {
      final String encoding = (String)
          this.getConfigParameterValue(PARAM_INPUT_ENCODING);
      final Charset charset = encoding == null
          ? Charset.defaultCharset()
          : Charset.forName(encoding);
      
      final ZipFile zipFile = new ZipFile(
          (String) this.getConfigParameterValue(PARAM_INPUT), charset);
      this.articleIterator = new ArticleCollectionIterator(zipFile);
      this.read = 0;
    } catch (final IOException e) {
      throw new ResourceInitializationException(e);
    }
  }
  

  @Override
  public void getNext(final CAS cas) throws IOException, CollectionException {
    try {
      cas.reset();
      final JCas jcas = cas.getJCas();
      final StringBuilder text = new StringBuilder();

      final Element articleElement = this.articleIterator.next();
      this.addMetadata(articleElement, jcas);
      this.addNode(articleElement, jcas, text);
      jcas.setDocumentText(text.toString());
    } catch (final CASException e) {
      throw new CollectionException(e);
    }
  }
  
  protected void addMetadata(final Element articleElement, final JCas jcas) {
    final ArticleMetadata metadata = new ArticleMetadata(jcas);
    metadata.setId(Integer.parseInt(
        articleElement.getAttribute(ATTRIBUTE_ARTICLE_ID)));

    final String publishedAtValue =
        articleElement.getAttribute(ATTRIBUTE_ARTICLE_PUBLISHED_AT);
    if (!publishedAtValue.isEmpty()) {
      final Date publishedAt = new Date(jcas);
      publishedAt.setNormalized(publishedAtValue);
      metadata.setPublishedAt(publishedAt);
    }

    metadata.setTitle(articleElement.getAttribute(ATTRIBUTE_ARTICLE_TITLE));

    metadata.addToIndexes();
  }
  
  protected void addNode(
      final Node node, final JCas jcas, final StringBuilder text)
  throws CASException {
    final NodeList childNodes = node.getChildNodes();
    for (int n = 0; n < childNodes.getLength(); ++n) {
      final Node childNode = childNodes.item(n);
      switch (childNode.getNodeType()) {
      case Node.TEXT_NODE:
      case Node.ENTITY_NODE:
      case Node.ENTITY_REFERENCE_NODE:
        text.append(childNode.getTextContent());
        break;
      case Node.ELEMENT_NODE:
        final Element childElement = (Element) childNode;
        final Unit unit = this.getElementAnnotation(childElement, jcas);
        unit.setBegin(text.length());
        this.addNode(childNode, jcas, text);
        unit.setEnd(text.length());
        unit.addToIndexes();
        break;
      }
    }
  }
  
  protected Unit getElementAnnotation(final Element element, final JCas jcas) {
    switch (element.getTagName()) {
    case TAG_PARAGRAPH:
      return new Paragraph(jcas);
    case TAG_QUOTATION:
      return new Quotation(jcas);
    case TAG_LINK:
      final Link link = new Link(jcas);
      final String type = element.getAttribute(ATTRIBUTE_LINK_TYPE);
      if (type.isEmpty() || type.equals(ATTRIBUTE_LINK_EXTERNAL)) {
        link.setExternal(true);
        link.setHref(element.getAttribute(ATTRIBUTE_LINK_HREF));
      } else {
        link.setExternal(false);
      }
      return link;
    }
    throw new IllegalArgumentException("Unknown tag: " + element.getTagName());
  }

  @Override
  public boolean hasNext() {
    if (this.articleIterator == null) { return false; }
    return this.articleIterator.hasNext();
  }

  @Override
  public Progress[] getProgress() {
    final int totalIsUnknown = -1;
    final int total = this.hasNext() ? totalIsUnknown : this.read;
    return new Progress[] {
        new ProgressImpl(this.read, total, "articles")
    };
  }

  @Override
  public void close() throws IOException {
    this.articleIterator = null;
  }

}
