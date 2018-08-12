package de.aitools.ie.uima.reader;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.Iterator;

import org.apache.uima.UIMAFramework;
import org.apache.uima.cas.CAS;
import org.apache.uima.collection.CollectionException;
import org.apache.uima.collection.CollectionReader_ImplBase;
import org.apache.uima.resource.ResourceConfigurationException;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.metadata.FsIndexDescription;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import org.apache.uima.util.CasCreationUtils;
import org.apache.uima.util.InvalidXMLException;
import org.apache.uima.util.Progress;
import org.apache.uima.util.ProgressImpl;
import org.apache.uima.util.XMLInputSource;
import org.w3c.dom.Element;

public class ArticleReader extends CollectionReader_ImplBase {
  
  public static final String TYPE_SYSTEM =
      "uima/typesystems/NewsArticleTypeSystem.xml";
  
  public static final String TAG_ARTICLE = "article";
  
  private Iterator<Element> articleIterator;
  
  private CAS cas;
  
  private int read;
  
  @Override
  public void reconfigure() throws ResourceConfigurationException {
    super.reconfigure();
    this.cas = this.createCas();
    this.read = 0;
    // TODO this.articleIterator = ;
  }
  

  @Override
  public void getNext(final CAS cas) throws IOException, CollectionException {
    // TODO Auto-generated method stub
    
  }

  @Override
  public boolean hasNext() throws IOException, CollectionException {
    return this.articleIterator.hasNext();
  }

  @Override
  public Progress[] getProgress() {
    final int totalIsUnknown = -1;
    return new Progress[] {
        new ProgressImpl(this.read, totalIsUnknown, "articles")
    };
  }

  @Override
  public void close() throws IOException {
    this.articleIterator = null;
  }
  
  protected CAS createCas() throws ResourceConfigurationException {
    try {
      final URL typeSystemUrl = this.getClass().getClassLoader()
          .getResource(TYPE_SYSTEM);
      if (typeSystemUrl == null) {
        throw new FileNotFoundException("No such type system in class path: "
            + TYPE_SYSTEM);
      }
      
      final XMLInputSource typeSystemSource = new XMLInputSource(typeSystemUrl);
      final TypeSystemDescription typeSystem = UIMAFramework.getXMLParser()
          .parseTypeSystemDescription(typeSystemSource);
      typeSystem.resolveImports();
      final CAS cas = CasCreationUtils.createCas(
          typeSystem, null, new FsIndexDescription[0]);
      return cas;
    } catch (final IOException | InvalidXMLException | ResourceInitializationException e) {
      throw new ResourceConfigurationException(e);
    }
  }

}
