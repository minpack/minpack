/*
 * Copyright 2024 the minipack project authors
 * SPDX-License-Identifier: Apache-2.0
 */
package org.translatenix.minipack;

import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;
import org.translatenix.minipack.internal.ChannelSink;
import org.translatenix.minipack.internal.OutputStreamSink;

/** The underlying sink of a {@link MessageWriter}. */
public interface MessageSink extends Closeable {
  /**
   * Writes the {@linkplain ByteBuffer#remaining() remaining} bytes of the given buffer.
   *
   * <p>Conceptually, this method makes {@code n} calls to {@link ByteBuffer#get()}, where {@code n}
   * is the number of bytes {@linkplain ByteBuffer#remaining() remaining} in the buffer.
   */
  int write(ByteBuffer buffer) throws IOException;

  /** Flushes this message sink. */
  void flush() throws IOException;

  default int writeAtLeast(ByteBuffer buffer, int minBytes) throws IOException {
    var totalBytesWritten = 0;
    while (totalBytesWritten < minBytes) {
      var bytesWritten = write(buffer);
      totalBytesWritten += bytesWritten;
    }
    return totalBytesWritten;
  }

  default void ensureRemaining(ByteBuffer buffer, int byteCount) throws IOException {
    assert byteCount <= buffer.capacity();
    var minBytes = byteCount - buffer.remaining();
    if (minBytes > 0) {
      buffer.flip();
      writeAtLeast(buffer, minBytes);
      buffer.compact();
    }
  }

  /** Returns a message sink that writes to the given stream. */
  static MessageSink of(OutputStream stream) {
    return new OutputStreamSink(stream);
  }

  /** Returns a message sink that writes to the given channel. */
  static MessageSink of(WritableByteChannel channel) {
    return new ChannelSink(channel);
  }
}
