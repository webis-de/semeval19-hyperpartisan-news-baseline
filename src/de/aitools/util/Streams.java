package de.aitools.util;

import java.io.Closeable;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Collections;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class Streams {
  
   private Streams() {};
   
   public static <T> Stream<T> stream(final Iterator<T> iterator) {
     final boolean parallelStream = false;
     final Spliterator<T> spliterator =
         Spliterators.spliteratorUnknownSize(iterator, Spliterator.ORDERED);
     return StreamSupport.stream(spliterator, parallelStream);
   }
   
   public static <T> Stream<T> parallelStream(final Iterator<T> iterator) {
     final boolean parallelStream = true;
     final Spliterator<T> spliterator =
         Spliterators.spliteratorUnknownSize(iterator, Spliterator.ORDERED);
     return StreamSupport.stream(spliterator, parallelStream);
   }
   
   public static <T> Stream<T> flatten(
       final Stream<Stream<T>> streamOfStreams) {
     return Streams.stream(
         new FlatteningIterator<>(streamOfStreams.iterator()));
   }
   
   public static Runnable close(final Closeable closeable) {
     return () -> {
       try {
         closeable.close();
       } catch (final IOException e) {
         throw new UncheckedIOException(e);
       }
     };
   }
   
   public static class FlatteningIterator<T> implements Iterator<T> {
     
     private final Iterator<Stream<T>> streams;
     
     private Iterator<T> current;
     
     public FlatteningIterator(final Iterator<Stream<T>> streams) {
       this.streams = Objects.requireNonNull(streams);
       if (this.streams.hasNext()) {
         this.current = this.streams.next().iterator();
       } else {
         this.current = Collections.<T>emptyList().iterator();
       }
     }

     @Override
     public boolean hasNext() {
       if (this.current.hasNext()) {
         return true;
       } else if (this.streams.hasNext()) {
         this.current = this.streams.next().iterator();
         return this.hasNext();
       } else {
         return false;
       }
     }

     @Override
     public T next() {
       if (!this.hasNext()) { throw new NoSuchElementException(); }
       return this.current.next();
     }
     
   }

}
