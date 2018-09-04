package de.aitools.util.io;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.util.stream.Stream;

import org.apache.commons.compress.archivers.ArchiveException;

public class NamedInputStreamFactory {
  
  private Compressions compressions;
  
  private boolean isRecursive;
  
  private boolean isFollowingLinks;
  
  public NamedInputStreamFactory() {
    this.setCompressions(new Compressions());
    this.setIsRecursive(true);
    this.setIsFollowingLinks(true);
  }
  
  public Compressions getCompressions() {
    return this.compressions;
  }
  
  public boolean isRecursive() {
    return this.isRecursive;
  }
  
  public boolean isFollowingLinks() {
    return this.isFollowingLinks;
  }
  
  public void setCompressions(final Compressions compressions) {
    this.compressions = compressions;
  }
  
  public void setIsRecursive(final boolean isRecursive) {
    this.isRecursive = isRecursive;
  }
  
  public void setIsFollowingLinks(final boolean isFollowingLinks) {
    this.isFollowingLinks = isFollowingLinks;
  }
  
  public Stream<NamedInputStream> stream(final File file)
  throws IOException {
    return Files.walk(file.toPath(), this.getWalkDepth(), this.getWalkOptions())
      .flatMap(path -> this.streamFile(path.toFile()));
  }
  
  protected Stream<NamedInputStream> streamFile(final File file) {
    try {
      final NamedFileInputStream input = new NamedFileInputStream(file);
      final NamedInputStream uncompressed = this.uncompressIfCompressed(input);
      try {
        System.err.println(uncompressed.getName());
        final Stream<NamedInputStream> unarchived =
            Archives.stream(uncompressed);
        // uncompress each archive entry
        return unarchived.map(entry  -> this.uncompressIfCompressed(entry));
      } catch (final ArchiveException e) {
        // just not an archive -> return as such
        return Stream.of(uncompressed);
      }
    } catch (final IOException e) {
      throw new UncheckedIOException(e);
    }
  }
  
  protected NamedInputStream uncompressIfCompressed(
      final NamedInputStream input) {
    return this.getCompressions().uncompress(input);
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
