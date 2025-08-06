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
import net.kyori.examination.ExaminableProperty;
import net.kyori.examination.string.StringExaminer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import team.unnamed.creative.base.WoodType;

import java.util.Objects;
import java.util.stream.Stream;

import static java.util.Objects.requireNonNull;

record SignSpecialRenderImpl(boolean hanging, WoodType woodType, Key texture) implements SignSpecialRender {
    SignSpecialRenderImpl(final boolean hanging, final @NotNull WoodType woodType, final @Nullable Key texture) {
        this.hanging = hanging;
        this.woodType = requireNonNull(woodType, "woodType");
        this.texture = texture;
    }

    @Override
    public @NotNull WoodType woodType() {
        return woodType;
    }

    @Override
    public @Nullable Key texture() {
        return texture;
    }

    @Override
    public @NotNull Stream<? extends ExaminableProperty> examinableProperties() {
        return Stream.of(
                ExaminableProperty.of("hanging", hanging),
                ExaminableProperty.of("woodType", woodType),
                ExaminableProperty.of("texture", texture)
        );
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        SignSpecialRenderImpl that = (SignSpecialRenderImpl) o;
        return hanging == that.hanging &&
                woodType == that.woodType &&
                Objects.equals(texture, that.texture);
    }

    @Override
    public @NotNull String toString() {
        return examine(StringExaminer.simpleEscaping());
    }
}
