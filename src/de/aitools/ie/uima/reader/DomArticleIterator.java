package de.aitools.ie.uima.reader;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class DomArticleIterator implements Iterator<Element> {
  
  private final NodeList nodeList;
  
  private int index;
  
  public DomArticleIterator(final InputStream input) {
    final DocumentBuilderFactory builderFactory =
        DocumentBuilderFactory.newInstance();
    try {
      final DocumentBuilder builder = builderFactory.newDocumentBuilder();
      final Document document = builder.parse(input);
      this.nodeList = document.getElementsByTagName(ArticleReader.TAG_ARTICLE);
      this.index = 0;
    } catch (final ParserConfigurationException e) {
      // should not happen since we are using the default configuration
      throw new RuntimeException(e);
    } catch (final SAXException | IOException e) {
      throw new RuntimeException(e);
    }
  }
  
  public DomArticleIterator(final NodeList nodeList) {
    this.nodeList = Objects.requireNonNull(nodeList);
    this.index = 0;
  }
  
  public Stream<Element> toStream() {
    final Spliterator<Element> spliterator =
        Spliterators.spliteratorUnknownSize(this, Spliterator.ORDERED);
    final boolean parallel = false;
    return StreamSupport.stream(spliterator, parallel);
  }

  @Override
  public boolean hasNext() {
    return (this.nodeList != null) && (this.index < this.nodeList.getLength());
  }

  @Override
  public Element next() {
    if (!this.hasNext()) { throw new NoSuchElementException(); }
    final Node current = this.nodeList.item(this.index);
    this.index++;
    return (Element) current;
  }

}
