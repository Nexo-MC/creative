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
package team.unnamed.creative.serialize.minecraft.metadata;

import net.kyori.adventure.key.Key;
import org.intellij.lang.annotations.Language;
import org.junit.jupiter.api.Test;
import team.unnamed.creative.metadata.Metadata;
import team.unnamed.creative.metadata.trims.TrimsMeta;
import team.unnamed.creative.serialize.minecraft.GsonUtil;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TrimsMetaTest {
    private static final TrimsMetaCodec CODEC = new TrimsMetaCodec();

    @Test
    void test_serialization() {
        TrimsMeta meta = TrimsMeta.basePalette(Key.key("minecraft:trim/color_palettes/trim_palette"));

        assertEquals("{\"base_palette\":\"minecraft:trim/color_palettes/trim_palette\"}", CODEC.toJson(meta));
    }

    @Test
    void test_deserialization() {
        @Language("JSON") String serialized = "{\"base_palette\":\"minecraft:trim/color_palettes/trim_palette\"}";

        assertEquals(Key.key("minecraft:trim/color_palettes/trim_palette"), CODEC.fromJson(serialized).basePalette());
    }

    @Test
    void test_metadata_serialization() throws Exception {
        Metadata metadata = Metadata.metadata()
                .addPart(TrimsMeta.basePalette(Key.key("creative:palette")))
                .build();

        assertEquals("{\"palette\":{\"base_palette\":\"creative:palette\"}}", MetadataSerializer.INSTANCE.serializeToJsonString(metadata));
    }

    @Test
    void test_metadata_deserialization() {
        @Language("JSON") String serialized = "{\"palette\":{\"base_palette\":\"creative:palette\"}}";
        Metadata metadata = MetadataSerializer.INSTANCE.readFromTree(GsonUtil.parseString(serialized));

        assertEquals(
                Metadata.metadata().addPart(TrimsMeta.basePalette(Key.key("creative:palette"))).build(),
                metadata
        );
    }
}
