package de.aitools.util.io;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;

public class Archives {
  
  private Archives() {};
  
  public static Stream<NamedInputStream> stream(final NamedInputStream input)
  throws ArchiveException, IOException {
    final ArchiveStreamFactory factory = new ArchiveStreamFactory();
    return Archives.stream(factory.createArchiveInputStream(input));
  }

  public static Stream<NamedInputStream> stream(final File file)
  throws ArchiveException, IOException {
    final ArchiveStreamFactory factory = new ArchiveStreamFactory();
    final InputStream input =
        new BufferedInputStream(new FileInputStream(file));
    return Archives.stream(factory.createArchiveInputStream(input));
  }
  
  public static Stream<NamedInputStream> stream(final ArchiveInputStream input)
  throws IOException  {
    final Iterator<NamedInputStream> archiveIterator =
        new ArchiveIterator(input);
    final Spliterator<NamedInputStream> spliterator =
        Spliterators.spliteratorUnknownSize(
            archiveIterator, Spliterator.ORDERED);
    final boolean parallel = false;
    return StreamSupport.stream(spliterator, parallel).onClose(() -> {
      try {
        input.close();
      } catch (final IOException e) {
        throw new UncheckedIOException(e);
      }
    });
  }
  
  protected static class ArchiveIterator
  implements Iterator<NamedInputStream>, AutoCloseable {
    
    private final ArchiveInputStream input;
    
    private NamedArchiveEntryInputStream current;
    
    private boolean advanced;
    
    protected ArchiveIterator(final ArchiveInputStream input) throws IOException {
      this.input = Objects.requireNonNull(input);
      this.advance();
    }

    @Override
    public boolean hasNext() {
      if (!this.advanced) {
        try {
          this.advance();
        } catch (final IOException e) {
          throw new UncheckedIOException(e);
        }
      }
      return this.current != null;
    }

    @Override
    public NamedInputStream next() {
      if (!this.hasNext()) { throw new NoSuchElementException(); }
      this.advanced = false;
      return this.current;
    }

    @Override
    public void close() throws Exception {
      this.closeCurrent();
      this.input.close();
    }
    
    protected void closeCurrent() {
      if (this.current != null) {
        this.current.close();
      }
      this.current = null;
    }
    
    protected ArchiveEntry getNextFileEntry() throws IOException {
      ArchiveEntry entry = this.input.getNextEntry();
      while (entry != null && entry.isDirectory()) {
        entry = this.input.getNextEntry();
      }
      return entry;
    }
    
    protected void advance() throws IOException {
      this.closeCurrent();
      
      final ArchiveEntry currentEntry = this.getNextFileEntry();
      if (currentEntry != null) {
        this.current = new NamedArchiveEntryInputStream(this.input, currentEntry);
      }
      
      this.advanced = true; 
    }
    
  }

}
