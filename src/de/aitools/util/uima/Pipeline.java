package de.aitools.util.uima;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.logging.Logger;

import org.apache.uima.UIMAFramework;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.cas.CAS;
import org.apache.uima.collection.CollectionReader;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.ResourceSpecifier;
import org.apache.uima.resource.metadata.ConfigurationParameterSettings;
import org.apache.uima.resource.metadata.FsIndexDescription;
import org.apache.uima.resource.metadata.ProcessingResourceMetaData;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import org.apache.uima.util.CasCreationUtils;
import org.apache.uima.util.InvalidXMLException;
import org.apache.uima.util.XMLInputSource;

/**
 * Utility class for creating UIMA objects used in UIMA pipelines.
 *
 * @author johannes.kiesel@uni-weimar.de
 */
public class Pipeline {
  
  // -------------------------------------------------------------------------
  // LOGGING
  // -------------------------------------------------------------------------
  
  private static final Logger LOG =
      Logger.getLogger(Pipeline.class.getName());
  
  // -------------------------------------------------------------------------
  // CONSTANTS
  // -------------------------------------------------------------------------
  
  /**
   * Directory in the class path that contains the primitive analysis engine
   * descriptors.
   */
  public static final String DESCRIPTOR_PACKAGE_FOR_PRIMITIVE_ANALYSIS_ENGINES =
      "uima/primitives/";
  
  /**
   * Directory in the class path that contains the aggregate analysis engine
   * descriptors.
   */
  public static final String DESCRIPTOR_PACKAGE_FOR_AGGREGATE_ANALYSIS_ENGINES =
      "uima/aggregates/";
  
  /**
   * Directory in the class path that contains the collection reader
   * descriptors.
   */
  public static final String DESCRIPTOR_PACKAGE_FOR_COLLECTION_READERS =
      "uima/collectionreaders/";
  
  /**
   * Directory in the class path that contains the type system descriptors.
   */
  public static final String DESCRIPTOR_PACKAGE_FOR_TYPE_SYSTEMS =
      "uima/typesystems/";
  
  // -------------------------------------------------------------------------
  // CONSTRUCTOR
  // -------------------------------------------------------------------------
  
  protected Pipeline() {}
  
  // -------------------------------------------------------------------------
  // FUNCTIONALITY
  // -------------------------------------------------------------------------

  /**
   * Creates a new CAS object for given type system.
   * @param typeSystemPath The path of the XML file of the CAS' type system
   * @return The CAS object
   * @throws InvalidXMLException If the type system file is not valid
   * @throws IOException If the type system file could not be found or read
   * @throws ResourceInitializationException If the CAS could not be initialized
   */
  public static CAS createCas(final String typeSystemPath)
  throws InvalidXMLException, IOException, ResourceInitializationException {
    LOG.info("Initializing CAS for " + typeSystemPath);

    final URL typeSystemUrl = ClassLoader.getSystemResource(typeSystemPath);
    if (typeSystemUrl == null) {
      throw new FileNotFoundException("No such type system in class path: "
          + typeSystemPath);
    }
    
    final XMLInputSource typeSystemSource = new XMLInputSource(typeSystemUrl);
    final TypeSystemDescription typeSystem = UIMAFramework.getXMLParser()
        .parseTypeSystemDescription(typeSystemSource);
    typeSystem.resolveImports();
    return CasCreationUtils.createCas(
        typeSystem, null, new FsIndexDescription[0]);
  }

  /**
   * Creates a new collection reader.
   * @param collectionReaderPath The path of the XML file of the collection
   * reader to be used to iterate over all files to be processed
   * @param inputParameter The name of the parameter of the collection reader
   * that specifies the input file or directory
   * @param input The input file or directory to read from
   * @return The created collection reader
   * @throws IOException If the collection reader file could not be read
   * @throws InvalidXMLException If the collection reader file is not valid
   * @throws ResourceInitializationException If the collection reader could not
   * be initialized
   */
  public static CollectionReader createCollectionReader(
      final String collectionReaderPath,
      final String inputParameter, final String... input)
  throws IOException, InvalidXMLException, ResourceInitializationException {
    LOG.info("Initializing CollectionReader " + collectionReaderPath + " with "
        + inputParameter + " = " + input);

    final XMLInputSource xmlInputSource =
        new XMLInputSource(ClassLoader.getSystemResource(collectionReaderPath));
    final ResourceSpecifier specifier =
        UIMAFramework.getXMLParser().parseResourceSpecifier(xmlInputSource);

    final ProcessingResourceMetaData metaData = (ProcessingResourceMetaData)
        specifier.getAttributeValue("metaData");
    final ConfigurationParameterSettings settings =
        metaData.getConfigurationParameterSettings();
    settings.setParameterValue(inputParameter, input);

    final CollectionReader collectionReader =
        UIMAFramework.produceCollectionReader(specifier);
    return collectionReader;
  }

  /**
   * Creates a new analysis engine.
   * @param collectionReaderPath The path of the XML file of the analysis engine
   * @return The created analysis engine
   * @throws IOException If the analysis engine file could not be read
   * @throws InvalidXMLException If the analysis engine file is not valid
   * @throws ResourceInitializationException If the analysis engine could not be
   * initialized
   */
  public static AnalysisEngine createAnalysisEngine(
      final String analysisEnginePath)
  throws IOException, InvalidXMLException, ResourceInitializationException {
    LOG.info("Initializing AnalysisEngine " + analysisEnginePath);

    final XMLInputSource xmlInputSource =
        new XMLInputSource(ClassLoader.getSystemResource(analysisEnginePath));
    final ResourceSpecifier specifier =
        UIMAFramework.getXMLParser().parseResourceSpecifier(xmlInputSource);
    final AnalysisEngine analysisEngine =
        UIMAFramework.produceAnalysisEngine(specifier);
    return analysisEngine;
  }

}
