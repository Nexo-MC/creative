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
package team.unnamed.creative.serialize.minecraft.model;

import net.kyori.adventure.key.Key;
import org.junit.jupiter.api.Test;
import team.unnamed.creative.base.CubeFace;
import team.unnamed.creative.base.Vector3Float;
import team.unnamed.creative.model.Element;
import team.unnamed.creative.model.ElementFace;
import team.unnamed.creative.model.Model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ShadeDirectionOverrideTest {
    private static final Key KEY = Key.key("creative:block/test");

    private static Model modelWith(final CubeFace shadeDirectionOverride) {
        return Model.model()
                .key(KEY)
                .addElement(Element.element()
                        .from(Vector3Float.ZERO)
                        .to(new Vector3Float(16f, 16f, 16f))
                        .shadeDirectionOverride(shadeDirectionOverride)
                        .addFace(CubeFace.NORTH, ElementFace.face().texture("#test").build())
                        .build())
                .build();
    }

    @Test
    void test_serialization() throws Exception {
        String serialized = ModelSerializer.INSTANCE.serializeToJsonString(modelWith(CubeFace.UP));

        assertTrue(serialized.contains("\"shade_direction_override\":\"up\""), serialized);
    }

    @Test
    void test_not_serialized_when_unset() throws Exception {
        String serialized = ModelSerializer.INSTANCE.serializeToJsonString(modelWith(null));

        assertTrue(!serialized.contains("shade_direction_override"), serialized);
    }

    @Test
    void test_round_trip() throws Exception {
        Model model = modelWith(CubeFace.EAST);
        Model deserialized = ModelSerializer.INSTANCE.deserializeFromJsonString(ModelSerializer.INSTANCE.serializeToJsonString(model), KEY);

        assertEquals(CubeFace.EAST, deserialized.elements().getFirst().shadeDirectionOverride());
        assertEquals(model, deserialized);
    }

    @Test
    void test_deserialization_without_field() throws Exception {
        Model deserialized = ModelSerializer.INSTANCE.deserializeFromJsonString(ModelSerializer.INSTANCE.serializeToJsonString(modelWith(null)), KEY);

        assertNull(deserialized.elements().getFirst().shadeDirectionOverride());
    }
}
