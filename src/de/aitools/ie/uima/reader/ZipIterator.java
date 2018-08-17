package de.aitools.ie.uima.reader;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ZipIterator implements Iterator<InputStream>, AutoCloseable {
  
  private final ZipFile zipFile;
  
  private final Enumeration<? extends ZipEntry> internIterator;
  
  private final Pattern fileNamePattern;
  
  private ZipEntry next;
  
  public ZipIterator(final ZipFile zipFile, final Pattern fileNamePattern) {
    this.zipFile = zipFile;
    this.internIterator = this.zipFile.entries();
    this.fileNamePattern = fileNamePattern;
    this.next = null;
    this.advance();
  }

  @Override
  public boolean hasNext() {
    return this.next != null;
  }

  @Override
  public InputStream next() {
    if (!this.hasNext()) { throw new NoSuchElementException(); }
    final ZipEntry current = this.next;
    this.advance();
    try {
      return this.zipFile.getInputStream(current);
    } catch (final IOException e) {
      throw new UncheckedIOException(e);
    }
  }
  
  /**
   * Streams the input streams of this iterator.
   * <p>
   * When this method is called, this iterator should no longer be considered
   * (except for closing the input file).
   * </p>
   * @return A stream of the remaining input stream objects
   */
  public Stream<InputStream> toStream() {
    final Spliterator<InputStream> spliterator =
        Spliterators.spliteratorUnknownSize(this, Spliterator.ORDERED);
    final boolean parallel = false;
    return StreamSupport.stream(spliterator, parallel);
  }

  @Override
  public void close() throws IOException {
    this.zipFile.close();
    this.next = null;
  }
  
  protected void advance() {
    if (!this.internIterator.hasMoreElements()) { // end of zip file
      this.next = null;
      return;
    }
    
    this.next = this.internIterator.nextElement();
    boolean nextMatches = this.nextMatchesPattern();
    
    while (!nextMatches && this.internIterator.hasMoreElements()) {
      this.next = this.internIterator.nextElement();
      nextMatches = this.nextMatchesPattern();
    }

    if (!nextMatches) { this.next = null; } // end of zip file
  }
  
  protected boolean nextMatchesPattern() {
    return this.fileNamePattern.matcher(this.next.getName()).matches();
  }

}
