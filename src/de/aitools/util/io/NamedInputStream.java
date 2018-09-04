package de.aitools.util.io;

import java.io.InputStream;
import java.util.Objects;

public abstract class NamedInputStream
extends InputStream {
  
  private final String name;
  
  public NamedInputStream(final String name) {
    this.name = Objects.requireNonNull(name);
  }

  public String getName() {
    return this.name;
  }
  
  @Override
  public String toString() {
    return "read:" + this.getName();
  }

}
