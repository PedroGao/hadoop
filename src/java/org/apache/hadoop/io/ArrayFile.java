package org.apache.hadoop.io;

import java.io.*;

import org.apache.hadoop.fs.*;
import org.apache.hadoop.conf.*;

/**
 * A dense file-based mapping from integers to values.
 */
public class ArrayFile extends MapFile {

  protected ArrayFile() {
  }                            // no public ctor

  /**
   * Write a new array file.
   */
  public static class Writer extends MapFile.Writer {
    private LongWritable count = new LongWritable(0);

    /**
     * Create the named file for values of the named class.
     */
    public Writer(FileSystem fs, String file, Class valClass) throws IOException {
      super(fs, file, LongWritable.class, valClass);
    }

    /**
     * Append a value to the file.
     */
    public synchronized void append(Writable value) throws IOException {
      super.append(count, value);                 // add to map
      count.set(count.get() + 1);                   // increment count
    }
  }

  /**
   * Provide access to an existing array file.
   */
  public static class Reader extends MapFile.Reader {
    private LongWritable key = new LongWritable();

    /**
     * Construct an array reader for the named file.
     */
    public Reader(FileSystem fs, String file, Configuration conf) throws IOException {
      super(fs, file, conf);
    }

    /**
     * Positions the reader before its <code>n</code>th value.
     */
    public synchronized void seek(long n) throws IOException {
      key.set(n);
      seek(key);
    }

    /**
     * Read and return the next value in the file.
     */
    public synchronized Writable next(Writable value) throws IOException {
      return next(key, value) ? value : null;
    }

    /**
     * Returns the key associated with the most recent call to {@link
     * #seek(long)}, {@link #next(Writable)}, or {@link
     * #get(long, Writable)}.
     */
    public synchronized long key() throws IOException {
      return key.get();
    }

    /**
     * Return the <code>n</code>th value in the file.
     */
    public synchronized Writable get(long n, Writable value)
      throws IOException {
      key.set(n);
      return get(key, value);
    }
  }

}
