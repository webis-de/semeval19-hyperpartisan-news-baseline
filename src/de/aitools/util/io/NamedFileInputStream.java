package de.aitools.util.io;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class NamedFileInputStream
extends WrappedNamedInputStream {

  public NamedFileInputStream(final File file)
  throws FileNotFoundException {
    super(file.getName(), new BufferedInputStream(new FileInputStream(file)));
  }

}
