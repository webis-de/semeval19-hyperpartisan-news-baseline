package de.aitools.util.io;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

public class NamedInputStream
extends InputStream {
  
  private final InputStream wrappedStream;
  
  private final String name;
  
  public NamedInputStream(final File file) throws FileNotFoundException {
    this(new BufferedInputStream(new FileInputStream(file)), file.getName());
  }
  
  public NamedInputStream(final InputStream wrappedStream, final String name) {
    this.wrappedStream = Objects.requireNonNull(wrappedStream);
    this.name = Objects.requireNonNull(name);
  }

  public String getName() {
    return this.name;
  }
  
  protected InputStream getWrappedStream() {
    return this.wrappedStream;
  }
  
  @Override
  public String toString() {
    return "read:" + this.getName();
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
