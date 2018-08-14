package de.aitools.ie.uima;

import org.apache.uima.cas.FSIterator;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;

import de.aitools.ie.uima.type.news.ArticleMetadata;

public class Articles {
  
  public static ArticleMetadata getMetadata(final JCas article) {
    final FSIterator<Annotation> metadataAnnotations =
        article.getAnnotationIndex(ArticleMetadata.type).iterator();
    if (!metadataAnnotations.hasNext()) {
      throw new IllegalArgumentException(
          "Article without metadata: " + article);
    }
    
    return (ArticleMetadata) metadataAnnotations.next();
  }
  
  public static int getId(final JCas article) {
    final ArticleMetadata metadata = Articles.getMetadata(article);
    return metadata.getId();
  }

}
