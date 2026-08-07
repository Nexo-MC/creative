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

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import team.unnamed.creative.base.CubeFace;
import team.unnamed.creative.base.Vector3Float;
import team.unnamed.creative.metadata.pack.FormatVersion;
import team.unnamed.creative.metadata.pack.PackFormat;
import team.unnamed.creative.model.Element;
import team.unnamed.creative.model.ElementFace;
import team.unnamed.creative.model.Model;
import team.unnamed.creative.serialize.minecraft.GsonUtil;
import team.unnamed.creative.texture.TextureUV;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class ModelUvClampTest {

    private static final PackFormat MODERN = PackFormat.format(FormatVersion.of(FormatVersion.FORMAT_26_2));
    private static final PackFormat LEGACY = PackFormat.format(FormatVersion.of(FormatVersion.FORMAT_1_21_4));

    @Test
    @DisplayName("Out-of-bounds UVs are clamped for Minecraft 26.1+")
    void test_out_of_bounds_uv_is_clamped() throws Exception {
        // the UV Minecraft reported as '[-57, -19, -38, 0] in 128x128 image'
        Model model = cube(TextureUV.uv(-7.125f / 16f, -2.375f / 16f, -4.75f / 16f, 0f));

        assertArrayEquals(new float[]{0f, 0f, 0f, 0f}, uvOf(model, MODERN));
    }

    @Test
    @DisplayName("UVs above the texture size are clamped for Minecraft 26.1+")
    void test_too_large_uv_is_clamped() throws Exception {
        Model model = cube(TextureUV.uv(8f / 16f, 0f, 32f / 16f, 24f / 16f));

        assertArrayEquals(new float[]{8f, 0f, 16f, 16f}, uvOf(model, MODERN));
    }

    @Test
    @DisplayName("UVs derived from an oversized element are written back clamped")
    void test_derived_uv_is_clamped() throws Exception {
        // no explicit UV, Minecraft derives an out-of-bounds one from the element itself
        Model model = Model.model()
                .key(Key.key("test:oversized"))
                .addElement(Element.element()
                        .from(new Vector3Float(-16f, 0f, 0f))
                        .to(new Vector3Float(32f, 16f, 16f))
                        .faces(Map.of(CubeFace.SOUTH, ElementFace.face()
                                .texture("#test")
                                .build()))
                        .build())
                .build();

        assertArrayEquals(new float[]{0f, 0f, 16f, 16f}, uvOf(model, MODERN));
    }

    @Test
    @DisplayName("Out-of-bounds UVs are left untouched for older pack formats")
    void test_out_of_bounds_uv_is_kept_on_legacy_formats() throws Exception {
        Model model = cube(TextureUV.uv(-7.125f / 16f, -2.375f / 16f, -4.75f / 16f, 0f));

        assertArrayEquals(new float[]{-7.125f, -2.375f, -4.75f, 0f}, uvOf(model, LEGACY));
    }

    @Test
    @DisplayName("In-bounds UVs equal to the default one are still omitted")
    void test_default_uv_is_omitted() throws Exception {
        Model model = cube(TextureUV.uv(0f, 0f, 1f, 1f));

        assertNull(uvOf(model, MODERN));
    }

    private static Model cube(TextureUV uv) {
        return Model.model()
                .key(Key.key("test:cube"))
                .addElement(Element.element()
                        .from(Vector3Float.ZERO)
                        .to(new Vector3Float(16f, 16f, 16f))
                        .faces(Map.of(CubeFace.SOUTH, ElementFace.face()
                                .uv(uv)
                                .texture("#test")
                                .build()))
                        .build())
                .build();
    }

    private static float @Nullable [] uvOf(Model model, PackFormat packFormat) throws Exception {
        JsonObject face = GsonUtil.parseString(ModelSerializer.INSTANCE.serializeToJsonString(model, packFormat))
                .getAsJsonObject()
                .getAsJsonArray("elements").get(0).getAsJsonObject()
                .getAsJsonObject("faces")
                .getAsJsonObject("south");

        if (!face.has("uv")) return null;

        JsonArray uv = face.getAsJsonArray("uv");
        return new float[]{
                uv.get(0).getAsFloat(), uv.get(1).getAsFloat(),
                uv.get(2).getAsFloat(), uv.get(3).getAsFloat()
        };
    }
}
