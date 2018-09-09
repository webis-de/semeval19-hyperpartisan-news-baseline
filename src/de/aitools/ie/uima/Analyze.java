package de.aitools.ie.uima;

import java.io.FileOutputStream;
import java.util.Arrays;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.cas.impl.XmiCasSerializer;
import org.apache.uima.jcas.JCas;

import de.aitools.util.uima.AbstractCollectionReader;
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
    if (args.length < 2) {
      throw new IllegalArgumentException(
          "Usage: <input> [<input> [...]] <output>");
    }
    final String[] input = new String[args.length - 1];
    System.arraycopy(args, 0, input, 0, input.length);
    final String output = args[args.length - 1];
    
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
        AbstractCollectionReader.PARAMETER_INPUT;
    
    final CollectionIterator articles = new CollectionIterator(typeSystemPath,
        collectionReaderPath, collectionReaderInputParameter, input);
    final AnalysisEngine analysisEngine =
        Pipeline.createAnalysisEngine(analysisEnginePath);

    LOG.info("Start to process " + Arrays.toString(input));
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
