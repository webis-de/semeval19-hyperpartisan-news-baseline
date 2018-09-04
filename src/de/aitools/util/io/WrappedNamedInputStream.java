package de.aitools.util.io;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

public class WrappedNamedInputStream
extends NamedInputStream {
  
  private final InputStream wrappedStream;
  
  public WrappedNamedInputStream(
      final String name, final InputStream wrappedStream) {
    super(name);
    this.wrappedStream = Objects.requireNonNull(wrappedStream);
  }
  
  protected InputStream getWrappedStream() {
    return this.wrappedStream;
  }
  
  @Override
  public int available() throws IOException {
    return this.getWrappedStream().available();
  }
  
  @Override
  public long skip(final long n) throws IOException {
    return this.getWrappedStream().skip(n);
  }

  @Override
  public int read() throws IOException {
    return this.getWrappedStream().read();
  }
  
  @Override
  public int read(final byte[] b) throws IOException {
    return this.getWrappedStream().read(b);
  }
  
  @Override
  public int read(final byte[] b, final int off, final int len)
  throws IOException {
    return this.getWrappedStream().read(b, off, len);
  }
  
  @Override
  public boolean markSupported() {
    return this.getWrappedStream().markSupported();
  }
  
  @Override
  public synchronized void mark(final int readlimit) {
    this.getWrappedStream().mark(readlimit);
  }

  @Override
  public synchronized void reset() throws IOException {
    this.getWrappedStream().reset();
  }
  
  @Override
  public void close() throws IOException {
    this.getWrappedStream().close();
  }

}
