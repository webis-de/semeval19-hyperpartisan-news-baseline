package de.aitools.ie.uima.reader;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.regex.Pattern;
import java.util.zip.ZipFile;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Element;
import org.xml.sax.SAXException;

public class ArticleCollectionIterator implements Iterator<Element> {
  
  private final Iterator<InputStream> documents;
  
  private Iterator<Element> articles;
  
  public ArticleCollectionIterator(final ZipFile zipFile) {
    this.documents = new ZipIterator(zipFile, Pattern.compile("\\.xml$"));
    if (this.documents.hasNext()) {
      this.articles = this.readNextDocument();
    } else {
      throw new IllegalArgumentException(
          "ZIP file " + zipFile + " contains no XML files");
    }
  }

  @Override
  public boolean hasNext() {
    if (this.articles.hasNext()) {
      return true;
    } else if (this.documents.hasNext()) {
      this.articles = this.readNextDocument();
      return this.hasNext();
    } else {
      return false;
    }
  }

  @Override
  public Element next() {
    if (!this.hasNext()) { throw new NoSuchElementException(); }
    return this.articles.next();
  }
  
  protected Iterator<Element> readNextDocument() {
    try {
      return new DomArticleIterator(this.documents);
    } catch (final ParserConfigurationException | SAXException | IOException e) {
      throw new RuntimeException(e);
    }
  }

}
