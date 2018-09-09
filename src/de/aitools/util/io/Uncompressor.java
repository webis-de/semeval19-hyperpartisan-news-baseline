package de.aitools.util.io;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

import org.apache.commons.compress.compressors.CompressorException;
import org.apache.commons.compress.compressors.CompressorInputStream;
import org.apache.commons.compress.compressors.CompressorStreamFactory;
import org.apache.commons.compress.compressors.FileNameUtil;
import org.apache.commons.compress.compressors.bzip2.BZip2Utils;
import org.apache.commons.compress.compressors.gzip.GzipUtils;
import org.apache.commons.compress.compressors.lzma.LZMAUtils;
import org.apache.commons.compress.compressors.xz.XZUtils;

public class Uncompressor {

  private Map<String, Function<String, String>> suffixChangers;
  
  private CompressorStreamFactory factory;
  
  public Uncompressor() {
    this.setSuffixChangers(new HashMap<>());
    this.setFactory(new CompressorStreamFactory());
    this.addStandardSuffixChangers();
  }
  
  protected void addStandardSuffixChangers() {
    final Map<String, Function<String, String>> suffixChangers =
        this.getSuffixChangers();
    suffixChangers.put(CompressorStreamFactory.BROTLI,
        this.createSuffixStripper("br", "bro"));
    suffixChangers.put(CompressorStreamFactory.BZIP2,
        filename -> BZip2Utils.getUncompressedFilename(filename));
    suffixChangers.put(CompressorStreamFactory.GZIP,
        filename -> GzipUtils.getUncompressedFilename(filename));
    suffixChangers.put(CompressorStreamFactory.LZ4_BLOCK,
        this.createSuffixStripper("lz4"));
    suffixChangers.put(CompressorStreamFactory.LZ4_FRAMED,
        this.createSuffixStripper("lz4"));
    suffixChangers.put(CompressorStreamFactory.LZMA,
        filename -> LZMAUtils.getUncompressedFilename(filename));
    suffixChangers.put(CompressorStreamFactory.PACK200,
        this.createSuffixStripper("pack"));
    suffixChangers.put(CompressorStreamFactory.XZ,
        filename -> XZUtils.getUncompressedFilename(filename));
    suffixChangers.put(CompressorStreamFactory.Z,
        this.createSuffixStripper("z"));
    suffixChangers.put(CompressorStreamFactory.ZSTANDARD,
        this.createSuffixStripper("zst", "zstd"));
  }
  
  protected Function<String, String> createSuffixStripper(
      final String defaultSuffix, final String... otherSuffixes) {
    final Map<String, String> uncompressSuffix = new HashMap<>();
    uncompressSuffix.put("." + defaultSuffix, "");
    for (final String otherSuffix : otherSuffixes) {
      uncompressSuffix.put("." + otherSuffix, "");
    }

    final FileNameUtil utility =
        new FileNameUtil(uncompressSuffix, defaultSuffix);
    return filename -> utility.getUncompressedFilename(filename);
  }
  
  public Map<String, Function<String, String>> getSuffixChangers() {
    return this.suffixChangers;
  }
  
  public CompressorStreamFactory getFactory() {
    return this.factory;
  }
  
  public void setSuffixChangers(
      final Map<String, Function<String, String>> suffixChangers) {
    this.suffixChangers = Objects.requireNonNull(suffixChangers);
  }
  
  public void setFactory(final CompressorStreamFactory factory) {
    this.factory = Objects.requireNonNull(factory);
  }

  public NamedInputStream uncompressIfCompressed(final NamedInputStream input) {
    try {
      return this.uncompress(input);
    } catch (final CompressorException e) {
      return input; // it seems to be not compressed
    }
  }

  public NamedInputStream uncompress(final NamedInputStream input)
  throws CompressorException {
    final String compressionId = CompressorStreamFactory.detect(input);
    final String uncompressedFilename =
        this.getUncompressedFilename(compressionId, input.getName());
    final CompressorInputStream uncompressedStream =
        this.getFactory().createCompressorInputStream(compressionId, input);
    return new NamedInputStream(uncompressedStream, uncompressedFilename);
  }

  protected String getUncompressedFilename(
      final String compressionId, final String filename)
  throws CompressorException {
    final Function<String, String> suffixChanger =
        this.getSuffixChangers().get(compressionId);
    if (suffixChanger == null) {
      return filename;
    } else {
      return suffixChanger.apply(filename);
    }
  }

}
