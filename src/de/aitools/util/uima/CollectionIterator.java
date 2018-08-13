package de.aitools.util.uima;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Iterator;
import java.util.Objects;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.apache.uima.cas.CAS;
import org.apache.uima.cas.CASException;
import org.apache.uima.collection.CollectionException;
import org.apache.uima.collection.CollectionReader;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.util.InvalidXMLException;

/**
 * A one-time iterator over a collection.
 *
 * @author johannes.kiesel@uni-weimar.de
 *
 */
public class CollectionIterator implements Iterator<JCas> {
  
  // -------------------------------------------------------------------------
  // MEMBER VARIABLES
  // -------------------------------------------------------------------------
  
  private final CollectionReader collectionReader;
  
  private final CAS cas;
  
  private boolean finished;
  
  // -------------------------------------------------------------------------
  // CONSTRUCTORS
  // -------------------------------------------------------------------------
  
  /**
   * Creates a collection iterator from given collection reader.
   * @param cas The CAS object to reuse on {@link #next()}
   * @param collectionReader The collection reader to read from
   */
  public CollectionIterator(
      final CAS cas, final CollectionReader collectionReader) {
    this.cas = Objects.requireNonNull(cas);
    this.collectionReader = Objects.requireNonNull(collectionReader);
    this.finished = false;
  }
  
  /**
   * Creates a collection iterator from given collection reader.
   * @param typeSystemPath The path of the XML file of the type system
   * @param collectionReader The collection reader to read from
   * @throws IOException If the type system file could not be read
   * @throws InvalidXMLException If the type system is not valid
   * @throws ResourceInitializationException If the CAS could not be initialized
   */
  public CollectionIterator(
      final String typeSystemPath, final CollectionReader collectionReader)
  throws IOException, InvalidXMLException, ResourceInitializationException {
    this(Pipeline.createCas(typeSystemPath), collectionReader);
  }
  
  /**
   * Creates a collection iterator by instantiating a collection reader.
   * @param typeSystemName Name of the type system file (without .xml) as it
   * resides in a <code>type-systems</code> directory within the class path
   * @param collectionReaderPath The path of the XMI file of the collection
   * reader to be used to iterate over all files to be processed
   * @param inputParameter The name of the parameter of the collection reader
   * that specifies the input file or directory
   * @param input The input file or directory to read from
   * @throws IOException If the type system or collection reader files could not
   * be read
   * @throws InvalidXMLException If the type system or collection reader files
   * are not valid
   * @throws ResourceInitializationException If the collection reader could not
   * be initialized
   */
  public CollectionIterator(
      final String typeSystemName, final String collectionReaderPath,
      final String inputParameter, final String input)
  throws IOException, InvalidXMLException, ResourceInitializationException {
    this(typeSystemName, Pipeline.createCollectionReader(
        collectionReaderPath, inputParameter, input));
  }
  
  // -------------------------------------------------------------------------
  // FUNCTIONALITY
  // -------------------------------------------------------------------------
  
  @Override
  public boolean hasNext() {
    if (this.finished) { return false; }

    try {
      final boolean has = this.collectionReader.hasNext();
      if (!has) {
        this.collectionReader.destroy();
        this.finished = true;
      }
      return has;
    } catch (final CollectionException e) {
      throw new RuntimeException(e);
    } catch (final IOException e) {
      throw new UncheckedIOException(e);
    }
  }
  
  @Override
  public JCas next() {
    try {
      this.collectionReader.getNext(this.cas);
      return this.cas.getJCas();
    } catch (final CollectionException | CASException e) {
      throw new RuntimeException(e);
    } catch (final IOException e) {
      throw new UncheckedIOException(e);
    }
  }
  
  /**
   * Streams the CAS objects of this iterator.
   * <p>
   * When this method is called, this iterator should no longer be considered.
   * </p>
   * @return A stream of the remaining CAS objects
   */
  public Stream<JCas> toStream() {
    final Spliterator<JCas> spliterator =
        Spliterators.spliteratorUnknownSize(this, Spliterator.ORDERED);
    final boolean parallel = false;
    return StreamSupport.stream(spliterator, parallel);
  }

}
