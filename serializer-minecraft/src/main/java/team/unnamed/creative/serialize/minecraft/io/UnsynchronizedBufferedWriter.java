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

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.Writer;

import static java.util.Objects.requireNonNull;

/**
 * JsonWriter writes a few characters per call, and both OutputStreamWriter and BufferedWriter take a lock on every call.
 * Not thread-safe
 */
@ApiStatus.Internal
public final class UnsynchronizedBufferedWriter extends Writer {

    private static final int BUFFER_SIZE = 1024;

    private final Writer out;
    private final char[] buffer = new char[BUFFER_SIZE];
    private int position;

    public UnsynchronizedBufferedWriter(final @NotNull Writer out) {
        this.out = requireNonNull(out, "out");
    }

    @Override
    public void write(final int c) throws IOException {
        if (position == BUFFER_SIZE) {
            flushBuffer();
        }
        buffer[position++] = (char) c;
    }

    @Override
    public void write(final @NotNull String str, final int off, final int len) throws IOException {
        if (len > BUFFER_SIZE - position) {
            flushBuffer();
            if (len > BUFFER_SIZE) {
                out.write(str, off, len);
                return;
            }
        }
        str.getChars(off, off + len, buffer, position);
        position += len;
    }

    @Override
    public void write(final char @NotNull [] cbuf, final int off, final int len) throws IOException {
        if (len > BUFFER_SIZE - position) {
            flushBuffer();
            if (len > BUFFER_SIZE) {
                out.write(cbuf, off, len);
                return;
            }
        }
        System.arraycopy(cbuf, off, buffer, position, len);
        position += len;
    }

    private void flushBuffer() throws IOException {
        if (position > 0) {
            out.write(buffer, 0, position);
            position = 0;
        }
    }

    @Override
    public void flush() throws IOException {
        flushBuffer();
        out.flush();
    }

    @Override
    public void close() throws IOException {
        try {
            flushBuffer();
        } finally {
            out.close();
        }
    }

}
