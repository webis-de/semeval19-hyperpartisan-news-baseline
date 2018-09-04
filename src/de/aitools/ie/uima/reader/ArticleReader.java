package de.aitools.ie.uima.reader;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.stream.Stream;

import org.apache.uima.cas.CAS;
import org.apache.uima.cas.CASException;
import org.apache.uima.collection.CollectionException;
import org.apache.uima.collection.CollectionReader_ImplBase;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.util.Progress;
import org.apache.uima.util.ProgressImpl;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.aitools.ie.uima.type.core.Paragraph;
import de.aitools.ie.uima.type.news.ArticleMetadata;
import de.aitools.ie.uima.type.news.Link;
import de.aitools.ie.uima.type.news.Quotation;
import de.aitools.ie.uima.type.supertype.Unit;
import de.aitools.util.io.NamedInputStreamFactory;
import de.aitools.util.uima.Pipeline;

public class ArticleReader extends CollectionReader_ImplBase {
  
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
  
  // -------------------------------------------------------------------------
  // MEMBERS
  // -------------------------------------------------------------------------
  
  private Stream<Element> streamOfStreams;
  
  private Iterator<Element> streams;
  
  private int read;
  
  // -------------------------------------------------------------------------
  // CONSTRUCTORS
  // -------------------------------------------------------------------------
  
  public ArticleReader() {
    this.streamOfStreams = null;
    this.streams = null;
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
      final String input = (String) this.getConfigParameterValue(PARAM_INPUT);
      final NamedInputStreamFactory factory = new NamedInputStreamFactory();
      this.streamOfStreams = factory.stream(new File(input))
          .filter(stream -> stream.getName().endsWith(".xml"))
          .flatMap(stream -> new DomArticleIterator(stream).toStream());
      this.streams = this.streamOfStreams.iterator();
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

      final Element articleElement = this.streams.next();
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

    final String publishedAt =
        articleElement.getAttribute(ATTRIBUTE_ARTICLE_PUBLISHED_AT);
    if (!publishedAt.isEmpty()) {
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
    return this.streams.hasNext();
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
    this.streamOfStreams.close();
  }

}
