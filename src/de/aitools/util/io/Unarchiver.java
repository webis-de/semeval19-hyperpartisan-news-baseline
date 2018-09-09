package de.aitools.util.io;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.logging.Logger;
import java.util.stream.Stream;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;

import de.aitools.util.Streams;

public class Unarchiver {
  
  private ArchiveStreamFactory factory;
  
  public Unarchiver() {
    this.setFactory(new ArchiveStreamFactory());
  }
  
  public ArchiveStreamFactory getFactory() {
    return this.factory;
  }
  
  public void setFactory(final ArchiveStreamFactory factory) {
    this.factory = factory;
  }
  
  public Stream<NamedInputStream> unarchive(final NamedInputStream input)
  throws ArchiveException, IOException {
    final ArchiveInputStream archiveStream =
        this.getFactory().createArchiveInputStream(input);
    final Iterator<NamedInputStream> archiveIterator =
        new ArchiveIterator(archiveStream);
    return Streams.stream(archiveIterator).onClose(Streams.close(input));
  }
  
  protected static class ArchiveIterator
  implements Iterator<NamedInputStream>, AutoCloseable {
    
    private final ArchiveInputStream input;
    
    private NamedArchiveEntryInputStream current;
    
    private boolean advanced;
    
    protected ArchiveIterator(final ArchiveInputStream input)
    throws IOException {
      this.input = Objects.requireNonNull(input);
      this.advanced = false;
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
        this.current = new NamedArchiveEntryInputStream(
            this.input, currentEntry);
      }
      
      this.advanced = true; 
    }
    
  }

  protected static class NamedArchiveEntryInputStream
  extends NamedInputStream {
    
    // -------------------------------------------------------------------------
    // LOGGING
    // -------------------------------------------------------------------------
    
    private static final Logger LOG =
        Logger.getLogger(NamedArchiveEntryInputStream.class.getName());
    
    private boolean open;

    public NamedArchiveEntryInputStream(
        final ArchiveInputStream input, final ArchiveEntry currentEntry)
    throws IOException {
      super(new BufferedInputStream(input), currentEntry.getName());
      LOG.info("  open " + this.getName() + " from archive");
      this.open = true;
    }
    
    public boolean isOpen() {
      return this.open;
    }
    
    protected void checkOpen() {
      if (!this.isOpen()) {
        throw new IllegalStateException(this + " had been closed");
      }
    }
    
    @Override
    public int available() throws IOException {
      this.checkOpen();
      return super.available();
    }
    
    @Override
    public long skip(final long n) throws IOException {
      this.checkOpen();
      return super.skip(n);
    }

    @Override
    public int read() throws IOException {
      this.checkOpen();
      return super.read();
    }
    
    @Override
    public int read(final byte[] b) throws IOException {
      this.checkOpen();
      return super.read(b);
    }
    
    @Override
    public int read(final byte[] b, final int off, final int len)
    throws IOException {
      this.checkOpen();
      return super.read(b, off, len);
    }
    
    @Override
    public boolean markSupported() {
      this.checkOpen();
      return super.markSupported();
    }
    
    @Override
    public synchronized void mark(final int readlimit) {
      this.checkOpen();
      super.mark(readlimit);
    }

    @Override
    public synchronized void reset() throws IOException {
      this.checkOpen();
      super.reset();
    }
    
    @Override
    public void close() {
      LOG.fine("  closing " + this.getName() + " from archive");
      this.open = false;
      // Do not close underlying archive stream!
    }

  }

}
