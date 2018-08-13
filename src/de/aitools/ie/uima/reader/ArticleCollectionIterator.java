package de.aitools.ie.uima.reader;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.regex.Pattern;
import java.util.zip.ZipFile;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Element;
import org.xml.sax.SAXException;

public class ArticleCollectionIterator implements Iterator<Element> {
  
  private final ZipIterator documents;
  
  private Iterator<Element> articles;
  
  public ArticleCollectionIterator(final ZipFile zipFile) {
    this.documents = new ZipIterator(zipFile, Pattern.compile(".*\\.xml"));
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
    final Element element = this.articles.next();
    if (!this.hasNext()) {
      try {
        this.documents.close();
      } catch (final IOException e) {
        throw new UncheckedIOException(e);
      }
    }
    return element;
  }
  
  protected Iterator<Element> readNextDocument() {
    try {
      return new DomArticleIterator(this.documents);
    } catch (final ParserConfigurationException | SAXException | IOException e) {
      throw new RuntimeException(e);
    }
  }

}
