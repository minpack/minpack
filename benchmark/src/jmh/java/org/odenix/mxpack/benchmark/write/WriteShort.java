/*
 * Copyright 2024 the MxPack project authors
 * SPDX-License-Identifier: Apache-2.0
 */
package org.odenix.mxpack.benchmark.write;

import java.io.IOException;
import net.jqwik.api.Arbitraries;

@SuppressWarnings("unused")
public class WriteShort extends WriteValues {
  short[] values;

  @Override
  void generate256Values() {
    values = Arbitraries.shorts().array(short[].class).ofSize(256).sample();
  }

  @Override
  void writeValue(int index) throws IOException {
    writer.write(values[index]);
  }

  @Override
  void writeValueMp(int index) throws IOException {
    packer.packShort(values[index]);
  }
}
