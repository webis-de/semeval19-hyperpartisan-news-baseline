package de.aitools.ie.uima.reader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.util.Iterator;
import java.util.Objects;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipFile;

import org.apache.uima.cas.CAS;
import org.apache.uima.cas.CASException;
import org.apache.uima.cas.impl.XmiCasDeserializer;
import org.apache.uima.jcas.JCas;
import org.xml.sax.SAXException;


/**
 * Class for reading CAS objects from UIMA XMI files.
 * <p>
 * As usual for UIMA, this class uses only a single CAS object that it reuses
 * and just overwrites when the next one is demanded.
 * </p>
 *
 * @author johannes.kiesel@uni-weimar.de
 * @see CasSerializer
 */
public class CasReader implements Iterator<JCas> {
  
  // -------------------------------------------------------------------------
  // LOGGING
  // -------------------------------------------------------------------------
  
  private static final Logger LOG =
      Logger.getLogger(CasReader.class.getName());
  
  // -------------------------------------------------------------------------
  // MEMBERS
  // -------------------------------------------------------------------------
  
  private final Iterator<InputStream> xmiStreams;
  
  private final CAS cas;
  
  // -------------------------------------------------------------------------
  // CONSTRUCTORS
  // -------------------------------------------------------------------------
  
  /**
   * Creates a new reader for UIMA CAS.
   * @param input File to be read or directory containing the files to be read;
   * directories and ZIP files are traversed recursively and all files in them
   * are checked for the XMI file suffix and used accordingly
   * @param cas CAS to use
   * @throws IOException when the root file can not be accessed
   */
  public CasReader(final File root, final CAS cas)
  throws IOException {
    this.xmiStreams = this.openXmiStreamsRecursively(root).iterator();
    this.cas = Objects.requireNonNull(cas);
  }
  
  // -------------------------------------------------------------------------
  // FUNCTIONALITY
  // -------------------------------------------------------------------------

  @Override
  public boolean hasNext() {
    return this.xmiStreams.hasNext();
  }

  @Override
  public JCas next() {
    try (final InputStream xmiStream = this.xmiStreams.next()) {
      XmiCasDeserializer.deserialize(xmiStream, this.cas);
      return this.cas.getJCas();
    } catch (final IOException e) {
      throw new UncheckedIOException(e);
    } catch (final SAXException | CASException e) {
      throw new IllegalArgumentException(e);
    }
  }
  
  /**
   * Streams the CAS objects of this iterator.
   * <p>
   * When this method is called, this iterator should no longer be considered
   * (except for closing the input file).
   * </p>
   * @return A stream of the remaining CAS objects
   */
  public Stream<JCas> toStream() {
    final Spliterator<JCas> spliterator =
        Spliterators.spliteratorUnknownSize(this, Spliterator.ORDERED);
    final boolean parallel = false;
    return StreamSupport.stream(spliterator, parallel);
  }
  
  // -------------------------------------------------------------------------
  // HELPERS
  // -------------------------------------------------------------------------
  
  protected boolean isXmi(final File file) {
    final String fileName = file.getName().toLowerCase();
    return fileName.endsWith(".xmi");
  }
  
  protected boolean isCompressedXmi(final File file) {
    final String fileName = file.getName().toLowerCase();
    return fileName.endsWith(".xmi.gz");
  }
  
  protected boolean isZipArchive(final File file) {
    final String fileName = file.getName().toLowerCase();
    return fileName.endsWith(".zip");
  }
  
  /**
   * Opens the input stream(s) to read the CAS(es) from the file.
   * @param file The file to which to read the CAS(es) from
   * @return An iterator for opening input stream(s) for given file
   */
  protected Stream<InputStream> openXmiStreamsForFile(final File file) {
    try {
      if (this.isXmi(file)) {
        LOG.finer("Open XMI file " + file);
        return Stream.of(new FileInputStream(file));
      } else if (this.isCompressedXmi(file)) {
        LOG.finer("Open compressed XMI file " + file);
        return Stream.of(new GZIPInputStream(new FileInputStream(file)));
      } else if (this.isZipArchive(file)) {
        LOG.finer("Open zip archive " + file);
        @SuppressWarnings("resource")
        final Stream<InputStream> zippedXmiFiles = new ZipIterator(
            new ZipFile(file), Pattern.compile("^.*\\.xmi$")).toStream();
        return zippedXmiFiles;
      } else {
        LOG.finer("Skipping to open file " + file);
        return Stream.empty();
      }
    } catch (final IOException e) {
      throw new UncheckedIOException(e);
    }
  }
  
  protected Stream<InputStream> openXmiStreamsRecursively(final File file)
  throws IOException {
    return Files.walk(file.toPath(), FileVisitOption.FOLLOW_LINKS)
      .flatMap(path -> this.openXmiStreamsForFile(path.toFile()));
  }

}
