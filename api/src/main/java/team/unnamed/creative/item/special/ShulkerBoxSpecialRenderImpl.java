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
package team.unnamed.creative.item.special;

import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;
import team.unnamed.creative.base.CubeFace;

import java.util.stream.Stream;

import static java.util.Objects.requireNonNull;

record ShulkerBoxSpecialRenderImpl(Key texture, float openness,
                                   CubeFace orientation) implements ShulkerBoxSpecialRender {
    ShulkerBoxSpecialRenderImpl(final @NotNull Key texture, final float openness, final @NotNull CubeFace orientation) {
        this.texture = requireNonNull(texture, "texture");
        this.openness = openness;
        this.orientation = requireNonNull(orientation, "orientation");

        if (openness > 1 || openness < 0) {
            throw new IllegalArgumentException("Openness must be between 0 and 1, got: " + openness);
        }
    }

    @Override
    public @NotNull Key texture() {
        return texture;
    }

    @Override
    public @NotNull CubeFace orientation() {
        return orientation;
    }

    @Override
    public @NotNull String toString() {
        return getClass().getSimpleName() + "{" + "texture=" + texture + ", openness=" + openness + ", orientation=" + orientation + "}";
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ShulkerBoxSpecialRenderImpl that = (ShulkerBoxSpecialRenderImpl) o;
        return Float.compare(that.openness, openness) == 0 &&
                texture.equals(that.texture) &&
                orientation == that.orientation;
    }

}
