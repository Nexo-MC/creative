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
package team.unnamed.creative.serialize.minecraft.atlas;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import team.unnamed.creative.ResourcePack;
import team.unnamed.creative.atlas.Atlas;
import team.unnamed.creative.atlas.AtlasSource;
import team.unnamed.creative.base.Writable;
import team.unnamed.creative.metadata.overlays.OverlayEntry;
import team.unnamed.creative.metadata.overlays.OverlaysMeta;
import team.unnamed.creative.metadata.pack.FormatVersion;
import team.unnamed.creative.metadata.pack.PackFormat;
import team.unnamed.creative.metadata.pack.PackMeta;
import team.unnamed.creative.overlay.Overlay;
import team.unnamed.creative.serialize.minecraft.MinecraftResourcePackWriter;
import team.unnamed.creative.texture.Texture;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PaletteTextureWriteTest {

    private static final String LEGACY_PALETTE = "assets/test/textures/color_palettes/red.png";
    private static final String MODERN_PALETTE = "assets/test/textures/palettes/color_palettes/red.png";
    private static final String LEGACY_KEY = "assets/test/textures/color_palettes/key.png";
    private static final String MODERN_KEY = "assets/test/textures/palettes/color_palettes/key.png";

    @Test
    @DisplayName("Palettes are written only where a pre-26.3 format resolves them")
    void test_legacy_only() throws IOException {
        Set<String> paths = write(PackFormat.format(FormatVersion.of(FormatVersion.FORMAT_26_2)));
        assertTrue(paths.contains(LEGACY_PALETTE));
        assertTrue(paths.contains(LEGACY_KEY));
        assertFalse(paths.contains(MODERN_PALETTE));
        assertFalse(paths.contains(MODERN_KEY));
    }

    @Test
    @DisplayName("Palettes are moved under textures/palettes for a 26.3-only format")
    void test_modern_only() throws IOException {
        Set<String> paths = write(PackFormat.format(FormatVersion.of(FormatVersion.FORMAT_26_3)));
        assertTrue(paths.contains(MODERN_PALETTE));
        assertTrue(paths.contains(MODERN_KEY));
        assertFalse(paths.contains(LEGACY_PALETTE));
        assertFalse(paths.contains(LEGACY_KEY));
    }

    @Test
    @DisplayName("Palettes are written twice for a format range spanning 26.3")
    void test_both() throws IOException {
        Set<String> paths = write(PackFormat.format(FormatVersion.of(FormatVersion.FORMAT_26_2), FormatVersion.of(FormatVersion.FORMAT_26_3)));
        assertTrue(paths.contains(LEGACY_PALETTE));
        assertTrue(paths.contains(MODERN_PALETTE));
        assertTrue(paths.contains(LEGACY_KEY));
        assertTrue(paths.contains(MODERN_KEY));
    }

    @Test
    @DisplayName("Base textures of a paletted source are never moved")
    void test_base_texture_untouched() throws IOException {
        for (PackFormat format : new PackFormat[]{
                PackFormat.format(FormatVersion.of(FormatVersion.FORMAT_26_2)),
                PackFormat.format(FormatVersion.of(FormatVersion.FORMAT_26_3))
        }) {
            Set<String> paths = write(format);
            assertTrue(paths.contains("assets/test/textures/block/planks.png"), "Base texture must stay in place for " + format);
            assertFalse(paths.contains("assets/test/textures/palettes/block/planks.png"), "Base texture must not be moved for " + format);
        }
    }

    @Test
    @DisplayName("Pack-root palettes cover the declared formats, not just the targeted one")
    void test_root_covers_declared_formats() throws IOException {
        // an overlay only shadows the files it holds, so the root palette has to serve the 26.3
        // clients of the pack even when the writer targets a single older format
        PackFormat declared = PackFormat.format(FormatVersion.of(FormatVersion.FORMAT_26_2), FormatVersion.of(FormatVersion.FORMAT_26_3));
        Set<String> paths = write(declared, PackFormat.format(FormatVersion.of(FormatVersion.FORMAT_26_2)));
        assertTrue(paths.contains(LEGACY_PALETTE));
        assertTrue(paths.contains(MODERN_PALETTE));
    }

    @Test
    @DisplayName("Overlays place their own palette copies by their own entry range")
    void test_overlays_use_their_own_range() throws IOException {
        PackFormat legacyOnly = PackFormat.format(FormatVersion.of(FormatVersion.FORMAT_26_2));
        PackFormat modernOnly = PackFormat.format(FormatVersion.of(FormatVersion.FORMAT_26_3));

        ResourcePack pack = basePack(PackFormat.format(FormatVersion.of(FormatVersion.FORMAT_26_2), FormatVersion.of(FormatVersion.FORMAT_26_3)));
        Overlay legacyOverlay = Overlay.overlay("legacy_only");
        Overlay modernOverlay = Overlay.overlay("modern_only");
        // both ship the palette at the pre-26.3 location, each overlay decides where it lands
        legacyOverlay.texture(Texture.texture(Key.key("test:color_palettes/red.png"), Writable.bytes(new byte[]{4})));
        modernOverlay.texture(Texture.texture(Key.key("test:color_palettes/red.png"), Writable.bytes(new byte[]{5})));
        pack.overlay(legacyOverlay);
        pack.overlay(modernOverlay);
        pack.overlaysMeta(OverlaysMeta.of(Arrays.asList(
                OverlayEntry.of(legacyOnly, "legacy_only"),
                OverlayEntry.of(modernOnly, "modern_only")
        )));

        Set<String> paths = write(pack, PackFormat.UNKNOWN);
        assertTrue(paths.contains("legacy_only/" + LEGACY_PALETTE));
        assertFalse(paths.contains("legacy_only/" + MODERN_PALETTE));
        assertTrue(paths.contains("modern_only/" + MODERN_PALETTE));
        assertFalse(paths.contains("modern_only/" + LEGACY_PALETTE));
    }

    private static Set<String> write(PackFormat format) throws IOException {
        return write(format, format);
    }

    private static Set<String> write(PackFormat declaredFormat, PackFormat targetFormat) throws IOException {
        return write(basePack(declaredFormat), targetFormat);
    }

    private static ResourcePack basePack(PackFormat declaredFormat) {
        ResourcePack pack = ResourcePack.resourcePack();
        pack.packMeta(PackMeta.of(declaredFormat, Component.text("test")));
        pack.atlas(Atlas.atlas(Atlas.BLOCKS, AtlasSource.palettedPermutations(
                Collections.singletonList(Key.key("test:block/planks")),
                Key.key("test:color_palettes/key"),
                Collections.singletonMap("red", Key.key("test:color_palettes/red"))
        )));
        pack.texture(Texture.texture(Key.key("test:block/planks.png"), Writable.bytes(new byte[]{1})));
        pack.texture(Texture.texture(Key.key("test:color_palettes/key.png"), Writable.bytes(new byte[]{2})));
        pack.texture(Texture.texture(Key.key("test:color_palettes/red.png"), Writable.bytes(new byte[]{3})));
        return pack;
    }

    private static Set<String> write(ResourcePack pack, PackFormat targetFormat) throws IOException {
        byte[] bytes = MinecraftResourcePackWriter.builder()
                .targetPackFormat(targetFormat)
                .build()
                .build(pack)
                .data()
                .toByteArray();

        Set<String> paths = new HashSet<>();
        try (ZipInputStream input = new ZipInputStream(new ByteArrayInputStream(bytes))) {
            ZipEntry entry;
            while ((entry = input.getNextEntry()) != null) {
                paths.add(entry.getName());
            }
        }
        return paths;
    }

}
