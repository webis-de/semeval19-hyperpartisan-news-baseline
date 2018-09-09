package de.aitools.util.uima;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import org.apache.uima.cas.CAS;
import org.apache.uima.cas.CASException;
import org.apache.uima.collection.CollectionException;
import org.apache.uima.collection.CollectionReader_ImplBase;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.util.Progress;
import org.apache.uima.util.ProgressImpl;

import de.aitools.util.Streams;
import de.aitools.util.io.NamedInputStream;
import de.aitools.util.io.NamedInputStreamFactory;

public abstract class AbstractCollectionReader
extends CollectionReader_ImplBase {
  
  // -------------------------------------------------------------------------
  // CONSTANTS
  // -------------------------------------------------------------------------

  /**
   * Name for the parameter used to specify the input of the collection reader.
   */
  public static final String PARAMETER_INPUT = "input";

  /**
   * Name for the parameter used to specify whether directories should be opened
   * recursively.
   * <p>Default: <code>true</code></p>
   */
  public static final String PARAMETER_READ_RECURSIVE = "input.readrecursive";

  /**
   * Name for the parameter used to specify whether to follow symbolic links.
   * <p>Default: <code>true</code></p>
   */
  public static final String PARAMETER_READ_LINKS = "input.readlinks";
  
  // -------------------------------------------------------------------------
  // MEMBERS
  // -------------------------------------------------------------------------
  
  private final NamedInputStreamFactory factory;
  
  private Stream<NamedInputStream> inputStreams;
  
  private Iterator<NamedInputStream> inputStreamIterator;
  
  private int read;
  
  // -------------------------------------------------------------------------
  // CONSTRUCTORS
  // -------------------------------------------------------------------------
  
  public AbstractCollectionReader(final String extension) {
    this(Pattern.compile(".*\\." + extension, Pattern.CASE_INSENSITIVE));
  }
  
  public AbstractCollectionReader(final Pattern namePattern) {
    this.factory = new NamedInputStreamFactory();
    this.factory.setNamePattern(namePattern);
    this.inputStreams = null;
    this.inputStreamIterator = null;
    this.read = 0;
  }
  
  // -------------------------------------------------------------------------
  // FUNCTIONALITY
  // -------------------------------------------------------------------------

  @Override
  public void initialize() throws ResourceInitializationException {
    super.initialize();
    this.read = 0;

    final boolean readRecursive =
        (boolean) this.getConfigParameterValue(PARAMETER_READ_RECURSIVE);
    final boolean readLinks =
        (boolean) this.getConfigParameterValue(PARAMETER_READ_LINKS);
    this.factory.setIsRecursive(readRecursive);
    this.factory.setIsFollowingLinks(readLinks);
    
    final String[] inputs =
        (String[]) this.getConfigParameterValue(PARAMETER_INPUT);
    this.inputStreams = Streams.flatten(Arrays.stream(inputs).map(
        input -> this.factory.stream(new File(input))));
    this.inputStreamIterator = this.inputStreams.iterator();

    if (this.inputStreamIterator.hasNext()) {
      try {
        this.openInput(this.inputStreamIterator.next());
      } catch (final IOException e) {
        throw new UncheckedIOException(e);
      }
    } else {
      this.close();
    }
  }
  
  @Override
  public boolean hasNext() {
    if (this.inputStreams == null) {
      return false; // already closed;
    } else if (this.hasNextForInput()) {
      return true;
    } else if (this.inputStreamIterator.hasNext()) {
      final NamedInputStream next = this.inputStreamIterator.next();
      try {
        this.openInput(next);
        return this.hasNext();
      } catch (final IOException e) {
        throw new UncheckedIOException(e);
      }
    } else {
      return false;
    }
  }
  
  @Override
  public void getNext(final CAS cas) throws IOException, CollectionException {
    if (!this.hasNext()) { throw new NoSuchElementException(); }
    try {
      cas.reset();
      final JCas jcas = cas.getJCas();
      this.getNextForInput(jcas);
      this.read++;
    } catch (final CASException e) {
      throw new CollectionException(e);
    }
  }
  
  @Override
  public Progress[] getProgress() {
    final int totalIsUnknown = -1;
    final int total = this.hasNext() ? totalIsUnknown : this.read;
    return new Progress[] {
        new ProgressImpl(this.read, total, "entries")
    };
  }
  
  @Override
  public void close() {
    if (this.inputStreams != null) {
      this.inputStreams.close();
    }
    this.inputStreams = null;
  }
  
  // -------------------------------------------------------------------------
  // IMPLEMENTATION
  // -------------------------------------------------------------------------
  
  protected abstract void openInput(final NamedInputStream input)
  throws IOException;
  
  protected abstract boolean hasNextForInput();
  
  protected abstract void getNextForInput(final JCas jcas)
  throws IOException, CollectionException;

}
