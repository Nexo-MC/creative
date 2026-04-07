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

import net.kyori.examination.ExaminableProperty;
import net.kyori.examination.string.StringExaminer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import team.unnamed.creative.item.property.ItemNumericProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import static java.util.Objects.requireNonNull;

record RangeDispatchItemModelImpl(ItemNumericProperty property, float scale, List<Entry> entries, ItemModel fallback, Transformation transformation) implements RangeDispatchItemModel {
    RangeDispatchItemModelImpl(final @NotNull ItemNumericProperty property, final float scale, final @NotNull List<Entry> entries, final @Nullable ItemModel fallback, final @Nullable Transformation transformation) {
        this.property = requireNonNull(property, "property");
        this.scale = scale;
        this.entries = requireNonNull(entries, "entries");
        this.fallback = fallback;
        this.transformation = transformation;
    }

    @Override
    public @NotNull ItemNumericProperty property() {
        return property;
    }

    @Override
    public @NotNull List<Entry> entries() {
        return entries;
    }

    @Override
    public @Nullable ItemModel fallback() {
        return fallback;
    }

    @Override
    public @NotNull Stream<? extends ExaminableProperty> examinableProperties() {
        return Stream.of(
                ExaminableProperty.of("property", property),
                ExaminableProperty.of("scale", scale),
                ExaminableProperty.of("entries", entries),
                ExaminableProperty.of("fallback", fallback),
                ExaminableProperty.of("transformation", transformation)
        );
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        RangeDispatchItemModelImpl that = (RangeDispatchItemModelImpl) o;
        return Float.compare(that.scale, scale) == 0 &&
                property.equals(that.property) &&
                entries.equals(that.entries) &&
                Objects.equals(transformation, that.transformation) &&
                Objects.equals(fallback, that.fallback);
    }

    @Override
    public @NotNull String toString() {
        return examine(StringExaminer.simpleEscaping());
    }

    record EntryImpl(float threshold, ItemModel model, Transformation transformation) implements Entry {
        EntryImpl(final float threshold, final @NotNull ItemModel model, final @Nullable Transformation transformation) {
            this.threshold = threshold;
            this.model = requireNonNull(model, "model");
            this.transformation = transformation;
        }

        @Override
        public @NotNull ItemModel model() {
            return model;
        }

        @Override
        public @NotNull Stream<? extends ExaminableProperty> examinableProperties() {
            return Stream.of(
                    ExaminableProperty.of("threshold", threshold),
                    ExaminableProperty.of("model", model),
                    ExaminableProperty.of("transformation", transformation)
            );
        }

        @Override
        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass()) return false;
            EntryImpl entry = (EntryImpl) o;
            return Float.compare(entry.threshold, threshold) == 0 &&
                    model.equals(entry.model) &&
                    Objects.equals(transformation, entry.transformation);
        }

        @Override
        public @NotNull String toString() {
            return examine(StringExaminer.simpleEscaping());
        }
    }

    static final class BuilderImpl implements Builder {
        private ItemNumericProperty property;
        private float scale = DEFAULT_SCALE;
        private final List<Entry> entries = new ArrayList<>();
        private ItemModel fallback;
        private Transformation transformation;

        @Override
        public @NotNull Builder transformation(@Nullable Transformation transformation) {
            this.transformation = transformation;
            return this;
        }

        @Override
        public @NotNull Builder property(@NotNull ItemNumericProperty property) {
            this.property = requireNonNull(property, "property");
            return this;
        }

        @Override
        public @NotNull Builder scale(float scale) {
            this.scale = scale;
            return this;
        }

        @Override
        public @NotNull Builder addEntry(final @NotNull Entry entry) {
            entries.add(requireNonNull(entry, "entry"));
            return this;
        }

        @Override
        public @NotNull Builder fallback(@Nullable ItemModel fallback) {
            this.fallback = fallback;
            return this;
        }

        @Override
        public @NotNull RangeDispatchItemModel build() {
            return new RangeDispatchItemModelImpl(property, scale, entries, fallback, transformation);
        }
    }
}
