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

import com.google.gson.JsonElement;
import net.kyori.examination.ExaminableProperty;
import net.kyori.examination.string.StringExaminer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import team.unnamed.creative.item.property.ItemStringProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import static java.util.Objects.requireNonNull;

record SelectItemModelImpl(ItemStringProperty property, List<Case> cases, ItemModel fallback, Transformation transformation) implements SelectItemModel {
    SelectItemModelImpl(final @NotNull ItemStringProperty property, final @NotNull List<Case> cases, final @Nullable ItemModel fallback, final @Nullable Transformation transformation) {
        this.property = requireNonNull(property, "property");
        this.cases = requireNonNull(cases, "cases");
        this.fallback = fallback;
        this.transformation = transformation;
    }

    @Override
    public @NotNull ItemStringProperty property() {
        return property;
    }

    @Override
    public @NotNull List<Case> cases() {
        return cases;
    }

    @Override
    public @Nullable ItemModel fallback() {
        return fallback;
    }

    @Override
    public @NotNull Stream<? extends ExaminableProperty> examinableProperties() {
        return Stream.of(
                ExaminableProperty.of("property", property),
                ExaminableProperty.of("cases", cases),
                ExaminableProperty.of("fallback", fallback),
                ExaminableProperty.of("transformation", transformation)
        );
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        SelectItemModelImpl that = (SelectItemModelImpl) o;
        return property.equals(that.property) && cases.equals(that.cases) && Objects.equals(fallback, that.fallback) && Objects.equals(transformation, that.transformation);
    }

    @Override
    public @NotNull String toString() {
        return examine(StringExaminer.simpleEscaping());
    }

    record CaseImpl(List<JsonElement> when, ItemModel model, Transformation transformation) implements Case {
        CaseImpl(final @NotNull List<JsonElement> when, final @NotNull ItemModel model, final @Nullable Transformation transformation) {
            this.when = requireNonNull(when, "when");
            this.model = requireNonNull(model, "model");
            this.transformation = transformation;
        }

        @Override
        public @NotNull List<JsonElement> when() {
            return when;
        }

        @Override
        public @NotNull ItemModel model() {
            return model;
        }

        @Override
        public @Nullable Transformation transformation() {
            return transformation;
        }

        @Override
        public @NotNull Stream<? extends ExaminableProperty> examinableProperties() {
            return Stream.of(
                    ExaminableProperty.of("when", when),
                    ExaminableProperty.of("model", model),
                    ExaminableProperty.of("transformation", transformation)
            );
        }

        @Override
        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass()) return false;
            CaseImpl that = (CaseImpl) o;
            return when.equals(that.when) && model.equals(that.model) && Objects.equals(transformation, that.transformation);
        }

        @Override
        public @NotNull String toString() {
            return examine(StringExaminer.simpleEscaping());
        }
    }

    static final class BuilderImpl implements Builder {
        private ItemStringProperty property;
        private final List<Case> cases = new ArrayList<>();
        private ItemModel fallback;
        private Transformation transformation;

        @Override
        public @NotNull Builder transformation(@Nullable Transformation transformation) {
            this.transformation = transformation;
            return this;
        }

        @Override
        public @NotNull Builder property(final @NotNull ItemStringProperty property) {
            this.property = requireNonNull(property, "property");
            return this;
        }

        @Override
        public @NotNull Builder addCase(final @NotNull Case _case) {
            requireNonNull(_case, "_case");
            cases.add(_case);
            return this;
        }

        @Override
        public @NotNull Builder fallback(final @Nullable ItemModel fallback) {
            this.fallback = fallback;
            return this;
        }

        @Override
        public @NotNull SelectItemModel build() {
            return new SelectItemModelImpl(requireNonNull(property, "property"), cases, fallback, transformation);
        }
    }
}
