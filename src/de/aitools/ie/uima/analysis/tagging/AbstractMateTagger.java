package de.aitools.ie.uima.analysis.tagging;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Logger;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.resource.ResourceInitializationException;

/**
 * Abstract class for Mate Tools wrappers.
 * 
 * Automatically identifies the model path from the UIMA parameters.
 *
 * @author johannes.kiesel@uni-weimar.de
 */
public abstract class AbstractMateTagger extends JCasAnnotator_ImplBase {
  
  // -------------------------------------------------------------------------
  // LOGGING
  // -------------------------------------------------------------------------
  
  private static final Logger LOG =
      Logger.getLogger(AbstractMateTagger.class.getName());

  // -------------------------------------------------------------------------
  // UIMA PARAMETERS
  // -------------------------------------------------------------------------

  /**
   * Configuration parameter for the model used by this tagger
   */
  public static final String PARAM_MODEL = "model";

  // -------------------------------------------------------------------------
  // INITIALIZATION
  // -------------------------------------------------------------------------
  
  @Override
  public void initialize(final UimaContext context)
  throws ResourceInitializationException {
    super.initialize(context);

    try {
      final String modelPath = this.getModelPath(context);
      if (modelPath == null) {
        throw new ResourceInitializationException(
            ResourceInitializationException.NO_RESOURCE_FOR_PARAMETERS,
            new Object[] { PARAM_MODEL });
      }
      this.initialize(modelPath, context);
    } catch (final IOException e) {
      throw new ResourceInitializationException(e);
    }
  }

  protected String getModelPath(final UimaContext context)
  throws IOException {
    final String [] modelPaths = (String []) 
        context.getConfigParameterValue(PARAM_MODEL);
    
    // first: try files
    for (final String modelPath : modelPaths) {
      LOG.fine("Try file: " + modelPath);
      if (Files.exists(Paths.get(modelPath))) {
        LOG.info("Loading " + this.getClass() + " from file " + modelPath);
        return modelPath;
      }
    }

    // second: try resources
    for (final String modelPath : modelPaths) {
      LOG.fine("Try resource: " + modelPath);
      final URL modelResource = ClassLoader.getSystemResource(modelPath);
      if (modelResource != null) {
        LOG.info("Loading " + this.getClass() + " from resource " + modelPath);
        final Path modelFile = Files.createTempFile("mate", "model");
        modelFile.toFile().delete();
        LOG.fine("Copying model to " + modelFile);
        try (final InputStream model = modelResource.openStream()) {
          Files.copy(model, modelFile);
        }
        modelFile.toFile().deleteOnExit();
        return modelFile.toString();
      }
    }

    return null;
  }
  
  protected abstract void initialize(
      final String modelPath, final UimaContext context)
  throws IOException, ResourceInitializationException;
  

}
