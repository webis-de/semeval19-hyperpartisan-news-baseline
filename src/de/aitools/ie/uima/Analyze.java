package de.aitools.ie.uima;

import java.io.FileOutputStream;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.cas.impl.XmiCasSerializer;
import org.apache.uima.jcas.JCas;

import de.aitools.ie.uima.reader.ArticleReader;
import de.aitools.util.uima.CollectionIterator;
import de.aitools.util.uima.Pipeline;

public class Analyze {
  
  // -------------------------------------------------------------------------
  // LOGGING
  // -------------------------------------------------------------------------
  
  private static final Logger LOG =
      Logger.getLogger(Analyze.class.getName());
  
  // -------------------------------------------------------------------------
  // MAIN
  // -------------------------------------------------------------------------
  
  public static void main(final String[] args) throws Exception {
    final String input = args[0];
    final String output = args[1];
    
    final String typeSystemPath =
        Pipeline.DESCRIPTOR_PACKAGE_FOR_TYPE_SYSTEMS
        + "NewsArticleTypeSystem.xml";
    final String collectionReaderPath =
        Pipeline.DESCRIPTOR_PACKAGE_FOR_COLLECTION_READERS
        + "ArticleReader.xml";
    final String analysisEnginePath =
        Pipeline.DESCRIPTOR_PACKAGE_FOR_AGGREGATE_ANALYSIS_ENGINES
        + "StylePipeline.xml";
    final String collectionReaderInputParameter =
        ArticleReader.PARAM_INPUT;
    
    final CollectionIterator articles = new CollectionIterator(typeSystemPath,
        collectionReaderPath, collectionReaderInputParameter, input);
    final AnalysisEngine analysisEngine =
        Pipeline.createAnalysisEngine(analysisEnginePath);

    LOG.info("Start to process " + input);
    int count = 0;
    final int logEach = 1000;
    try (final ZipOutputStream zipOutputStream =
        new ZipOutputStream(new FileOutputStream(output))) {
      while (articles.hasNext()) {
        final JCas article = articles.next();
        analysisEngine.process(article);
        
        final int articleId = Articles.getId(article);
        zipOutputStream.putNextEntry(new ZipEntry(articleId + ".xmi"));
        XmiCasSerializer.serialize(article.getCas(), zipOutputStream);
        zipOutputStream.closeEntry();
        
        ++count;
        if (count % logEach == 0) {
          LOG.info("Processed: " + count);
        }
      }
    }
    LOG.info("Finished with " + count + " processed");
  }

}
