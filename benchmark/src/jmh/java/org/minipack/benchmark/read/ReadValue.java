/*
 * Copyright 2024 the MiniPack contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package org.minipack.benchmark.read;

import java.io.IOException;
import java.nio.ByteBuffer;
import org.minipack.java.*;
import org.msgpack.core.MessagePack;
import org.msgpack.core.MessageUnpacker;
import org.msgpack.core.buffer.ArrayBufferInput;
import org.msgpack.core.buffer.MessageBuffer;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

@State(Scope.Thread)
public abstract class ReadValue {
  BufferAllocator allocator;
  ByteBuffer buffer;
  MessageReader reader;
  MessageBuffer messageBuffer;
  ArrayBufferInput bufferInput;
  MessageUnpacker unpacker;

  abstract void writeValue(MessageWriter writer) throws IOException;

  @CompilerControl(CompilerControl.Mode.INLINE)
  abstract void readValue(Blackhole hole) throws IOException;

  @CompilerControl(CompilerControl.Mode.INLINE)
  abstract void readValueMp(Blackhole hole) throws IOException;

  @Setup
  public void setUp() throws IOException {
    allocator = BufferAllocator.ofUnpooled();
    buffer = allocator.acquireByteBuffer(1024 * 16);
    var sink = MessageSink.of(buffer, options -> options.allocator(allocator));
    var writer = MessageWriter.of(sink);
    writeValue(writer);
    buffer.flip();
    var source = MessageSource.of(buffer, options -> options.allocator(allocator));
    reader = MessageReader.of(source);
    messageBuffer = MessageBuffer.wrap(buffer.array());
    bufferInput = new ArrayBufferInput(messageBuffer);
    unpacker = MessagePack.newDefaultUnpacker(bufferInput);
  }

  @Benchmark
  public void run(Blackhole hole) throws IOException {
    buffer.position(0);
    readValue(hole);
  }

  @Benchmark
  public void runMp(Blackhole hole) throws IOException {
    bufferInput.reset(messageBuffer);
    unpacker.reset(bufferInput);
    readValueMp(hole);
  }
}
