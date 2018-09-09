package de.aitools.ie.uima.reader;

import java.io.IOException;
import java.util.Iterator;

import org.apache.uima.cas.CASException;
import org.apache.uima.collection.CollectionException;
import org.apache.uima.jcas.JCas;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.aitools.ie.uima.type.core.Paragraph;
import de.aitools.ie.uima.type.news.ArticleMetadata;
import de.aitools.ie.uima.type.news.Link;
import de.aitools.ie.uima.type.news.Quotation;
import de.aitools.ie.uima.type.supertype.Unit;
import de.aitools.util.io.NamedInputStream;
import de.aitools.util.uima.AbstractCollectionReader;

public class ArticleReader extends AbstractCollectionReader {
  
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
  // MEMBERS
  // -------------------------------------------------------------------------
  
  private Iterator<Element> articles;
  
  // -------------------------------------------------------------------------
  // CONSTRUCTORS
  // -------------------------------------------------------------------------
  
  public ArticleReader() {
    super("xml");
    this.articles = null;
  }
  
  // -------------------------------------------------------------------------
  // FUNCTIONALITY
  // -------------------------------------------------------------------------
  
  @Override
  protected void openInput(final NamedInputStream input) {
    this.articles = new DomArticleIterator(input);
  }
  
  @Override
  protected boolean hasNextForInput() {
    return this.articles.hasNext();
  }
  
  @Override
  protected void getNextForInput(final JCas jcas)
  throws IOException, CollectionException {
    try {
      final StringBuilder text = new StringBuilder();
      final Element articleElement = this.articles.next();
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

}
