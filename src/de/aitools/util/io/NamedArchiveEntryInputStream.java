package de.aitools.util.io;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.logging.Logger;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveInputStream;

public class NamedArchiveEntryInputStream
extends WrappedNamedInputStream {
  
  // -------------------------------------------------------------------------
  // LOGGING
  // -------------------------------------------------------------------------
  
  private static final Logger LOG =
      Logger.getLogger(NamedArchiveEntryInputStream.class.getName());
  
  private boolean open;

  public NamedArchiveEntryInputStream(
      final ArchiveInputStream input, final ArchiveEntry currentEntry)
  throws IOException {
    super(currentEntry.getName(), new BufferedInputStream(input));
    LOG.info("  open " + currentEntry.getName() + " from archive");
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
