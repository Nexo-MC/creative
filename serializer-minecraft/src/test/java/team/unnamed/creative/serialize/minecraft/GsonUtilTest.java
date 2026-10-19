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
package team.unnamed.creative.serialize.minecraft;

import com.google.gson.Strictness;
import com.google.gson.stream.JsonWriter;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GsonUtilTest {

    @Test
    void test_write_float_matches_json_writer() throws IOException {
        Random random = new Random(0);
        for (int i = -1100; i <= 1100; i++) {
            assertFloat(i / 16F);
            assertFloat(i / 32F);
            assertFloat(i / 3F);
        }
        float[] specials = {-0F, 0F, Float.MIN_VALUE, -Float.MIN_VALUE, Float.MAX_VALUE, 1e-5F, 1e7F, 64F, -64F, 64.0625F, Float.NaN, Float.POSITIVE_INFINITY, Float.NEGATIVE_INFINITY};
        for (float special : specials) {
            assertFloat(special);
        }
        for (int i = 0; i < 100_000; i++) {
            assertFloat(Float.intBitsToFloat(random.nextInt()));
            assertFloat(random.nextFloat() * 128F - 64F);
        }
    }

    private static void assertFloat(float value) throws IOException {
        StringWriter expected = new StringWriter();
        JsonWriter expectedWriter = new JsonWriter(expected);
        expectedWriter.setStrictness(Strictness.LENIENT);
        expectedWriter.beginArray().value(value).endArray();

        StringWriter actual = new StringWriter();
        JsonWriter actualWriter = new JsonWriter(actual);
        actualWriter.setStrictness(Strictness.LENIENT);
        actualWriter.beginArray();
        GsonUtil.writeFloat(actualWriter, value);
        actualWriter.endArray();

        assertEquals(expected.toString(), actual.toString(), () -> "bits " + Integer.toHexString(Float.floatToRawIntBits(value)));
    }

}
