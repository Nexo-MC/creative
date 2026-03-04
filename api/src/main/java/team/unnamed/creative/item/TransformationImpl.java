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
import team.unnamed.creative.base.QuaternionFloat;
import team.unnamed.creative.base.Vector3Float;

import java.util.stream.Stream;

import static java.util.Objects.requireNonNull;

record TransformationImpl(Vector3Float translation, Vector3Float scale, QuaternionFloat leftRotation, QuaternionFloat rightRotation) implements Transformation {

    static Transformation DEFAULT = new TransformationImpl(Vector3Float.ZERO, Vector3Float.ONE, QuaternionFloat.DEFAULT, QuaternionFloat.DEFAULT);

    TransformationImpl(final @NotNull Vector3Float translation, final @NotNull Vector3Float scale, final @NotNull QuaternionFloat leftRotation, final @NotNull QuaternionFloat rightRotation) {
        this.translation = requireNonNull(translation, "translation");
        this.scale = requireNonNull(scale, "scale");
        this.leftRotation = requireNonNull(leftRotation, "leftRotation");
        this.rightRotation = requireNonNull(rightRotation, "rightRotation");
    }

    @Override
    public @NotNull Vector3Float translation() {
        return translation;
    }

    @Override
    public @NotNull Transformation translation(Vector3Float translation) {
        return new TransformationImpl(translation, scale, leftRotation, rightRotation);
    }

    @Override
    public @NotNull Vector3Float scale() {
        return scale;
    }

    @Override
    public @NotNull Transformation scale(Vector3Float scale) {
        return new TransformationImpl(translation, scale, leftRotation, rightRotation);
    }

    @Override
    public @NotNull QuaternionFloat leftRotation() {
        return leftRotation;
    }

    @Override
    public @NotNull Transformation leftRotation(QuaternionFloat leftRotation) {
        return new TransformationImpl(translation, scale, leftRotation, rightRotation);
    }

    @Override
    public @NotNull QuaternionFloat rightRotation() {
        return rightRotation;
    }

    @Override
    public @NotNull Transformation rightRotation(@NotNull QuaternionFloat rightRotation) {
        return new TransformationImpl(translation, scale, leftRotation, rightRotation);
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
        if (o == null || getClass() != o.getClass()) return false;
        TransformationImpl that = (TransformationImpl) o;
        return translation.equals(that.translation)
                && scale.equals(that.scale)
                && leftRotation.equals(that.leftRotation)
                && rightRotation.equals(that.rightRotation);
    }

    @Override
    public @NotNull String toString() {
        return examine(StringExaminer.simpleEscaping());
    }
}
