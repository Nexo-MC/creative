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
import org.jetbrains.annotations.NotNull;
import team.unnamed.creative.metadata.pack.FormatVersion;
import team.unnamed.creative.metadata.pack.PackFormat;

import static java.util.Objects.requireNonNull;

/**
 * Converts between Creative's canonical palette texture locations and the
 * palette texture IDs used by Minecraft 26.3+.
 */
final class PaletteTextureIdSerializer {

    private static final String PALETTE_TEXTURE_DIRECTORY = "palettes/";

    private PaletteTextureIdSerializer() {
    }

    static @NotNull Key serialize(final @NotNull Key texture, final @NotNull PackFormat packFormat) {
        requireNonNull(texture, "texture");
        requireNonNull(packFormat, "packFormat");
        if (!usesPaletteTextureIds(packFormat)) {
            return texture;
        }

        final String value = texture.value();
        if (!value.startsWith(PALETTE_TEXTURE_DIRECTORY)) {
            throw new IllegalArgumentException("Palette texture '" + texture
                    + "' must be located under textures/palettes for pack format "
                    + packFormat.min());
        }
        return Key.key(texture.namespace(), value.substring(PALETTE_TEXTURE_DIRECTORY.length()));
    }

    static @NotNull Key deserialize(final @NotNull Key id, final @NotNull PackFormat packFormat) {
        requireNonNull(id, "id");
        requireNonNull(packFormat, "packFormat");
        if (!usesPaletteTextureIds(packFormat)) {
            return id;
        }
        return Key.key(id.namespace(), PALETTE_TEXTURE_DIRECTORY + id.value());
    }

    private static boolean usesPaletteTextureIds(final @NotNull PackFormat packFormat) {
        return packFormat != PackFormat.UNKNOWN
                && packFormat.min().major() >= FormatVersion.FORMAT_26_3;
    }
}
