/*
 * Copyright 2024 the MxPack project authors
 * SPDX-License-Identifier: Apache-2.0
 */
package org.odenix.mxpack.kotlin.example

import org.odenix.mxpack.kotlin.MessageReaders
import org.odenix.mxpack.kotlin.MessageWriters
import java.nio.channels.FileChannel
import java.nio.file.Path
import java.nio.file.StandardOpenOption
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir

@Suppress("UNUSED_VARIABLE")
class ReadFromFile : Example() {
  // -8<- [start:snippet]
  fun read(file: Path) {
    val channel = FileChannel.open(file) //(1)
    MessageReaders.of(channel).use { reader ->
      val string = reader.readString()
      val number = reader.readInt()
    }
  }
  // -8<- [end:snippet]

  @Test
  fun test(@TempDir dir: Path) {
    var file = dir.resolve("data.bin")
    var writer = MessageWriters.of(FileChannel.open(file, StandardOpenOption.CREATE, StandardOpenOption.WRITE))
    writer.write("Hello, MxPack!")
    writer.write(42)
    writer.close()
    read(file)
  }
}