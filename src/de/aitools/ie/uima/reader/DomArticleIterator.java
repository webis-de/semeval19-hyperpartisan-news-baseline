package de.aitools.ie.uima.reader;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Objects;

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
  
  public DomArticleIterator(final Iterator<InputStream> inputs)
  throws SAXException, IOException, ParserConfigurationException {
    final DocumentBuilderFactory builderFactory =
        DocumentBuilderFactory.newInstance();
    final DocumentBuilder builder = builderFactory.newDocumentBuilder();
    try (final InputStream inputStream = inputs.next()) {
      final Document document = builder.parse(inputStream);
      this.nodeList = document.getElementsByTagName(ArticleReader.TAG_ARTICLE);
    }
  }
  
  public DomArticleIterator(final NodeList nodeList) {
    this.nodeList = Objects.requireNonNull(nodeList);
    this.index = 0;
  }

  @Override
  public boolean hasNext() {
    return this.index < this.nodeList.getLength();
  }

  @Override
  public Element next() {
    final Node current = this.nodeList.item(this.index);
    this.index++;
    return (Element) current;
  }

}
