/*
 * This file is part of creative, licensed under the MIT license
 *
 * Copyright (c) 2021-2025 Unnamed Team
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package team.unnamed.creative.serialize.minecraft.io;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

class UnsynchronizedBufferedWriterTest {

    @Test
    void test_same_bytes_as_output_stream_writer() throws IOException {
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < 5000; i++) {
            text.append("abcé中").appendCodePoint(0x1F600 + (i % 40));
        }
        String string = text.toString();

        ByteArrayOutputStream expected = new ByteArrayOutputStream();
        try (Writer writer = new OutputStreamWriter(expected, StandardCharsets.UTF_8)) {
            writeMixed(writer, string);
        }

        ByteArrayOutputStream actual = new ByteArrayOutputStream();
        try (Writer writer = new UnsynchronizedBufferedWriter(new OutputStreamWriter(actual, StandardCharsets.UTF_8))) {
            writeMixed(writer, string);
        }

        assertArrayEquals(expected.toByteArray(), actual.toByteArray());
    }

    // odd chunk sizes split surrogate pairs across buffer flushes
    private static void writeMixed(Writer writer, String string) throws IOException {
        int position = 0;
        int step = 1;
        while (position < string.length()) {
            int length = Math.min(step, string.length() - position);
            if (step % 3 == 0) {
                writer.write(string.charAt(position));
                length = 1;
            } else if (step % 3 == 1) {
                writer.write(string, position, length);
            } else {
                writer.write(string.toCharArray(), position, length);
            }
            position += length;
            step = step * 7 % 2003 + 1;
        }
        writer.write(string);
    }

}
