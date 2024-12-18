/*
 * Copyright 2024 the MxPack project authors
 * SPDX-License-Identifier: Apache-2.0
 */
package org.odenix.mxpack.core.example;

import org.odenix.mxpack.core.MessageReader;
import org.odenix.mxpack.core.MessageWriter;

import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;

abstract class Example {
  protected final PipedOutputStream outStream = new PipedOutputStream();
  protected final InputStream inStream = newInputStream(outStream);
  protected final WritableByteChannel outChannel = Channels.newChannel(outStream);
  protected final ReadableByteChannel inChannel = Channels.newChannel(inStream);
  protected final MessageWriter writer = MessageWriter.of(outStream);
  protected final MessageReader reader = MessageReader.of(inStream);

  private static InputStream newInputStream(PipedOutputStream out) {
    try {
      return new PipedInputStream(out, 1024 * 20);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}