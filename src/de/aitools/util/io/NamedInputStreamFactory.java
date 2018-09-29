package de.aitools.util.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import org.apache.commons.compress.archivers.ArchiveException;

import de.aitools.util.Streams;

public class NamedInputStreamFactory {
  
  // -------------------------------------------------------------------------
  // LOGGING
  // -------------------------------------------------------------------------
  
  private static final Logger LOG =
      Logger.getLogger(NamedInputStreamFactory.class.getName());

  private Uncompressor uncompressor;
  
  private Unarchiver unarchiver;
  
  private boolean isRecursive;
  
  private boolean isFollowingLinks;
  
  private Pattern namePattern;
  
  public NamedInputStreamFactory() {
    this("");
  }
  
  public NamedInputStreamFactory(final String extension) {
    this.setUncompressor(new Uncompressor());
    this.setUnarchiver(new Unarchiver());
    this.setIsRecursive(true);
    this.setIsFollowingLinks(true);
    this.setNameExtension(extension);
  }
  
  public Uncompressor getUncompressor() {
    return this.uncompressor;
  }
  
  public Unarchiver getUnarchiver() {
    return this.unarchiver;
  }
  
  public boolean isRecursive() {
    return this.isRecursive;
  }
  
  public boolean isFollowingLinks() {
    return this.isFollowingLinks;
  }
  
  public Pattern getFilenamePattern() {
    return this.namePattern;
  }
  
  public void setUncompressor(final Uncompressor uncompressor) {
    this.uncompressor = uncompressor;
  }
  
  public void setUnarchiver(final Unarchiver unarchiver) {
    this.unarchiver = unarchiver;
  }
  
  public void setIsRecursive(final boolean isRecursive) {
    this.isRecursive = isRecursive;
  }
  
  public void setIsFollowingLinks(final boolean isFollowingLinks) {
    this.isFollowingLinks = isFollowingLinks;
  }
  
  public void setNamePattern(final String namePattern) {
    this.setNamePattern(Pattern.compile(namePattern));
  }
  
  public void setNamePattern(final Pattern namePattern) {
    this.namePattern = namePattern;
  }
  
  public void setNameExtension(final String extension) {
    this.setNamePattern(
        Pattern.compile(".*\\." + extension, Pattern.CASE_INSENSITIVE));
  }
  
  public Stream<NamedInputStream> stream(final File root) {
    try {
      LOG.info("Opening streams for " + root);
      return Streams.flatten(Files.walk(
            root.toPath(), this.getWalkDepth(), this.getWalkOptions())
          .filter(path -> Files.isRegularFile(path))
          .map(path -> this.streamFile(path.toFile())));
    } catch (final IOException e) {
      throw new UncheckedIOException(e);
    }
  }
  
  protected Stream<NamedInputStream> streamFile(final File file) {
    try {
      LOG.fine("Opening stream for file " + file);
      final Stream<NamedInputStream> stream =
          this.streamFile(new NamedInputStream(file));
      return stream;
    } catch (final FileNotFoundException e) {
      throw new UncheckedIOException(e);
    }
  }
  
  protected Stream<NamedInputStream> streamFile(final NamedInputStream input) {
    try {
      final NamedInputStream uncompressed = this.uncompressIfCompressed(input);
      final Stream<NamedInputStream> unarchived =
          this.unarchiveIfArchived(uncompressed);
      final Stream<NamedInputStream> filtered = this.filterByName(unarchived);
      return filtered;
    } catch (final IOException e) {
      throw new UncheckedIOException(e);
    }
  }
  
  protected NamedInputStream uncompressIfCompressed(
      final NamedInputStream input) {
    final Uncompressor uncompressor = this.getUncompressor();
    if (uncompressor != null) {
      return uncompressor.uncompressIfCompressed(input);
    }

    return input;
  }
  
  protected Stream<NamedInputStream> unarchiveIfArchived(
      final NamedInputStream input)
  throws IOException {
    final Unarchiver unarchiver = this.getUnarchiver();
    if (unarchiver != null) {
      try {
        final Stream<NamedInputStream> unarchived = unarchiver.unarchive(input);
        return unarchived;
      } catch (final ArchiveException e) {
        // just not an archive -> return as such
      }
    }

    return Stream.of(input);
  }
  
  protected Stream<NamedInputStream> filterByName(
      final Stream<NamedInputStream> inputs) {
    final Pattern namePattern = this.getFilenamePattern();
    if (namePattern != null) {
      return inputs.filter(
          input -> {
            return namePattern.matcher(input.getName()).matches();
          });
    }
    
    return inputs;
  }
  
  protected int getWalkDepth() {
    if (this.isRecursive()) {
      return Integer.MAX_VALUE;
    } else {
      return 1; // Also children if a directory is given
    }
  }

  protected FileVisitOption[] getWalkOptions() {
    if (this.isFollowingLinks()) {
      return new FileVisitOption[] { FileVisitOption.FOLLOW_LINKS };
    } else {
       return new FileVisitOption[] {};
    }
  }

}
