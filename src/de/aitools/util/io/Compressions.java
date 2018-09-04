package de.aitools.util.io;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.compress.compressors.CompressorException;
import org.apache.commons.compress.compressors.CompressorInputStream;
import org.apache.commons.compress.compressors.CompressorStreamFactory;
import org.apache.commons.compress.compressors.FileNameUtil;
import org.apache.commons.compress.compressors.bzip2.BZip2Utils;
import org.apache.commons.compress.compressors.gzip.GzipUtils;
import org.apache.commons.compress.compressors.lzma.LZMAUtils;
import org.apache.commons.compress.compressors.xz.XZUtils;

public class Compressions {
  
  private final Map<String, CompressionFileNameUtility> utilities;
  
  private final CompressorStreamFactory factory;
  
  public Compressions() {
    this(CompressorStreamFactory.getSingleton());
  }
  
  public Compressions(final CompressorStreamFactory factory) {
    this.factory = Objects.requireNonNull(factory);
    this.utilities = new HashMap<>();
    this.add(
        new CompressionFileNameExtensionListUtility(
            CompressorStreamFactory.BROTLI, "br", "bro"),
        new Bzip2(),
        new Gzip(),
        new CompressionFileNameExtensionListUtility(
            CompressorStreamFactory.LZ4_BLOCK, "lz4"),
        new CompressionFileNameExtensionListUtility(
            CompressorStreamFactory.LZ4_FRAMED, "lz4"),
        new Lzma(),
        new CompressionFileNameExtensionListUtility(
            CompressorStreamFactory.PACK200, "pack"),
        new Xz(),
        new CompressionFileNameExtensionListUtility(
            CompressorStreamFactory.Z, "z"),
        new CompressionFileNameExtensionListUtility(
            CompressorStreamFactory.ZSTANDARD, "zst", "zstd"));
  }
  
  public CompressionFileNameUtility get(final String compressionId) {
    return this.utilities.get(compressionId);
  }
  
  public void add(final CompressionFileNameUtility... utilities) {
    for (final CompressionFileNameUtility utility : utilities) {
      this.utilities.put(utility.getId(), utility);
    }
  }
  
  public NamedInputStream uncompress(final NamedInputStream input) {
    try {
      final String compressionId = CompressorStreamFactory.detect(input);
      final String uncompressedName =
          this.getUncompressedName(input.getName(), compressionId);

      final CompressorInputStream uncompressedInput =
          this.factory.createCompressorInputStream(compressionId, input);
      return new WrappedNamedInputStream(uncompressedName, uncompressedInput);
    } catch (final CompressorException e) {
      return input; // not compressed
    }
  }
  
  protected String getUncompressedName(
      final String name, final String compressionId) {
    final CompressionFileNameUtility utility = this.get(compressionId);
    if (utility == null) {
      return name;
    } else {
      return utility.getUncompressedFileName(name);
    }
  }
  
  public static abstract class CompressionFileNameUtility {
    
    private final String id;
    
    protected CompressionFileNameUtility(final String id) {
      this.id = Objects.requireNonNull(id);
    }
    
    public String getId() {
      return this.id;
    }

    public abstract boolean isCompressedFileName(final String filename);

    public abstract String getCompressedFileName(final String filename);

    public abstract String getUncompressedFileName(final String filename);
    
  }
  
  public static class CompressionFileNameExtensionListUtility
  extends CompressionFileNameUtility {
    
    private final FileNameUtil utility;
    
    public CompressionFileNameExtensionListUtility(
        final String id, final String defaultExtension,
        final String... otherExtensions) {
      super(id);
      final Map<String, String> uncompressSuffix = new HashMap<>();
      uncompressSuffix.put("." + defaultExtension, "");
      for (final String otherExtension : otherExtensions) {
        uncompressSuffix.put("." + otherExtension, "");
      }
      this.utility = new FileNameUtil(uncompressSuffix, defaultExtension);
    }
    
    @Override
    public boolean isCompressedFileName(final String filename) {
      return this.utility.isCompressedFilename(filename);
    }

    @Override
    public String getCompressedFileName(final String filename) {
      return this.utility.getCompressedFilename(filename);
    }

    @Override
    public String getUncompressedFileName(final String filename) {
      return this.utility.getUncompressedFilename(filename);
    }
    
  }

  protected static class Bzip2 extends CompressionFileNameUtility {

    public Bzip2() {
      super(CompressorStreamFactory.BZIP2);
    }

    @Override
    public boolean isCompressedFileName(final String filename) {
      return BZip2Utils.isCompressedFilename(filename);
    }

    @Override
    public String getCompressedFileName(final String filename) {
      return BZip2Utils.getCompressedFilename(filename);
    }

    @Override
    public String getUncompressedFileName(final String filename) {
      return BZip2Utils.getUncompressedFilename(filename);
    }

  }
  
  protected static class Gzip extends CompressionFileNameUtility {
    
    public Gzip() {
      super(CompressorStreamFactory.GZIP);
    }

    @Override
    public boolean isCompressedFileName(final String filename) {
      return GzipUtils.isCompressedFilename(filename);
    }

    @Override
    public String getCompressedFileName(final String filename) {
      return GzipUtils.getCompressedFilename(filename);
    }

    @Override
    public String getUncompressedFileName(final String filename) {
      return GzipUtils.getUncompressedFilename(filename);
    }

  }
  
  protected static class Lzma extends CompressionFileNameUtility {
    
    public Lzma() {
      super(CompressorStreamFactory.LZMA);
    }

    @Override
    public boolean isCompressedFileName(final String filename) {
      return LZMAUtils.isCompressedFilename(filename);
    }

    @Override
    public String getCompressedFileName(final String filename) {
      return LZMAUtils.getCompressedFilename(filename);
    }

    @Override
    public String getUncompressedFileName(final String filename) {
      return LZMAUtils.getUncompressedFilename(filename);
    }

  }
  
  protected static class Xz extends CompressionFileNameUtility {
    
    public Xz() {
      super(CompressorStreamFactory.XZ);
    }

    @Override
    public boolean isCompressedFileName(final String filename) {
      return XZUtils.isCompressedFilename(filename);
    }

    @Override
    public String getCompressedFileName(final String filename) {
      return XZUtils.getCompressedFilename(filename);
    }

    @Override
    public String getUncompressedFileName(final String filename) {
      return XZUtils.getUncompressedFilename(filename);
    }

  }

}
