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
package team.unnamed.creative.item;

import net.kyori.adventure.key.Key;
import net.kyori.examination.ExaminableProperty;
import net.kyori.examination.string.StringExaminer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import team.unnamed.creative.item.tint.TintSource;

import java.util.List;
import java.util.stream.Stream;

import static java.util.Objects.requireNonNull;

record ReferenceItemModelImpl(Key model, List<TintSource> tints, Transformation transformation) implements ReferenceItemModel {
    ReferenceItemModelImpl(final @NotNull Key model, final @NotNull List<TintSource> tints, final @Nullable Transformation transformation) {
        this.model = requireNonNull(model, "model");
        this.tints = requireNonNull(tints, "tints");
        this.transformation = transformation;
    }

    @Override
    public @NotNull Key model() {
        return model;
    }

    @Override
    public @NotNull List<TintSource> tints() {
        return tints;
    }

    @Override
    public @Nullable Transformation transformation() {
        return transformation;
    }

    @Override
    public @NotNull Stream<? extends ExaminableProperty> examinableProperties() {
        return Stream.of(
                ExaminableProperty.of("model", model),
                ExaminableProperty.of("tints", tints),
                ExaminableProperty.of("transformation", transformation)
        );
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ReferenceItemModelImpl that = (ReferenceItemModelImpl) o;
        return model.equals(that.model) && tints.equals(that.tints);
    }

    @Override
    public @NotNull String toString() {
        return examine(StringExaminer.simpleEscaping());
    }
}
