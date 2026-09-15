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
package team.unnamed.creative.metadata.trims;

import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import team.unnamed.creative.metadata.MetadataPart;

/**
 * Metadata applicable to trim-pattern textures
 *
 * @sincePackFormat 97
 * @sinceMinecraft 26.3
 * @since 1.15.0
 */
public interface TrimsMeta extends MetadataPart {
    /**
     * Creates a new {@link TrimsMeta} instance from the given values
     *
     * @param basePalette The base palette
     * @return A new instance of {@link TrimsMeta}
     * @sinceMinecraft 26.3
     * @sincePackFormat 97
     * @since 1.15.0
     */
    @Contract("_ -> new")
    static @NotNull TrimsMeta basePalette(final @NotNull Key basePalette) {
        return new TrimsMetaImpl(basePalette);
    }

    /**
     * Determines the TrimPatterns palette
     *
     * @sinceMinecraft 26.3
     * @sincePackFormat 97
     * @since 1.15.0
     */
    @NotNull Key basePalette();
}
