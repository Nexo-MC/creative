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

import net.kyori.examination.Examinable;
import net.kyori.examination.ExaminableProperty;
import net.kyori.examination.string.StringExaminer;
import org.jetbrains.annotations.NotNull;
import team.unnamed.creative.base.QuaternionFloat;
import team.unnamed.creative.base.Vector3Float;

import java.util.stream.Stream;

import static java.util.Objects.requireNonNull;

public record Transformation(Vector3Float translation, Vector3Float scale, QuaternionFloat leftRotation, QuaternionFloat rightRotation) implements Examinable {

    public static final Transformation DEFAULT = new Transformation(Vector3Float.ZERO, Vector3Float.ONE, QuaternionFloat.DEFAULT, QuaternionFloat.DEFAULT);

    public Transformation(
            final @NotNull Vector3Float translation,
            final @NotNull Vector3Float scale,
            final @NotNull QuaternionFloat leftRotation,
            final @NotNull QuaternionFloat rightRotation
    ) {
        this.translation = requireNonNull(translation, "translation");
        this.scale = requireNonNull(scale, "scale");
        this.leftRotation = requireNonNull(leftRotation, "leftRotation");
        this.rightRotation = requireNonNull(rightRotation, "rightRotation");
    }

    public @NotNull Vector3Float translation() {
        return translation;
    }

    public @NotNull Transformation translation(Vector3Float translation) {
        if (this.translation == translation) return this;
        return new Transformation(translation, this.scale, this.leftRotation, this.rightRotation);
    }

    public @NotNull Vector3Float scale() {
        return scale;
    }

    public @NotNull Transformation scale(Vector3Float scale) {
        if (this.scale == scale) return this;
        return new Transformation(this.translation, scale, this.leftRotation, this.rightRotation);
    }

    public @NotNull QuaternionFloat leftRotation() {
        return leftRotation;
    }

    public @NotNull Transformation leftRotation(QuaternionFloat leftRotation) {
        if (this.leftRotation == leftRotation) return this;
        return new Transformation(this.translation, this.scale, leftRotation, this.rightRotation);
    }

    public @NotNull QuaternionFloat rightRotation() {
        return rightRotation;
    }

    public @NotNull Transformation rightRotation(QuaternionFloat rightRotation) {
        if (this.rightRotation == rightRotation) return this;
        return new Transformation(this.translation, this.scale, this.leftRotation, rightRotation);
    }

    public @NotNull Builder toBuilder() {
        return builder()
                .translation(translation)
                .scale(scale)
                .leftRotation(leftRotation)
                .rightRotation(rightRotation);
    }

    @Override
    public @NotNull Stream<? extends ExaminableProperty> examinableProperties() {
        return Stream.of(
                ExaminableProperty.of("translation", translation),
                ExaminableProperty.of("scale", scale),
                ExaminableProperty.of("leftRotation", leftRotation),
                ExaminableProperty.of("rightRotation", rightRotation)
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transformation that = (Transformation) o;
        return translation.equals(that.translation)
                && scale.equals(that.scale)
                && leftRotation.equals(that.leftRotation)
                && rightRotation.equals(that.rightRotation);
    }

    @Override
    public @NotNull String toString() {
        return examine(StringExaminer.simpleEscaping());
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private Vector3Float translation = Vector3Float.ZERO;
        private Vector3Float scale = Vector3Float.ONE;
        private QuaternionFloat leftRotation = QuaternionFloat.DEFAULT;
        private QuaternionFloat rightRotation = QuaternionFloat.DEFAULT;

        private Builder() {
        }

        public Builder translation(Vector3Float translation) {
            this.translation = requireNonNull(translation, "translation");
            return this;
        }

        public Builder scale(Vector3Float scale) {
            this.scale = requireNonNull(scale, "scale");
            return this;
        }

        public Builder leftRotation(QuaternionFloat leftRotation) {
            this.leftRotation = requireNonNull(leftRotation, "leftRotation");
            return this;
        }

        public Builder rightRotation(QuaternionFloat rightRotation) {
            this.rightRotation = requireNonNull(rightRotation, "rightRotation");
            return this;
        }

        public Transformation build() {
            return new Transformation(translation, scale, leftRotation, rightRotation);
        }
    }
}
