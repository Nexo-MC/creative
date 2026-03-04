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
package team.unnamed.creative.base;

import net.kyori.examination.Examinable;
import net.kyori.examination.ExaminableProperty;
import net.kyori.examination.string.StringExaminer;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Iterator;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * Represents a fixed-size vector of three 32-bit
 * floating-point numbers, immutable.
 *
 * @since 1.0.0
 */
public record QuaternionFloat(float x, float y, float z, float w) implements Examinable, Iterable<Float> {

    /**
     * Constant for {@link QuaternionFloat} value with
     * abscissa, ordinate and applicate of {@code 0} zero.
     *
     * @since 1.0.0
     */
    public static final QuaternionFloat DEFAULT = new QuaternionFloat(0F, 0F, 0F, 1F);

    /**
     * Constructs a new {@link QuaternionFloat} with the
     * given {@code x}, {@code y} and {@code z} values.
     *
     * @param x The vector abscissa
     * @param y The vector ordinate
     * @param z The vector applicate
     * @param w The vector applicate
     * @since 1.0.0
     */
    public QuaternionFloat {
    }

    /**
     * Returns the "X" component or abscissa
     * of this vector.
     *
     * @return The vector abscissa
     * @since 1.0.0
     */
    @Override
    public float x() {
        return x;
    }

    /**
     * Returns a new {@link QuaternionFloat vector} with
     * the given {@code x} value and the same {@code y}
     * and {@code z} values as this vector.
     *
     * @param x The new X value
     * @return The updated vector
     * @since 1.0.0
     */
    @Contract(value = "_ -> new", pure = true)
    public @NotNull QuaternionFloat x(final float x) {
        return new QuaternionFloat(x, this.y, this.z, this.w);
    }

    /**
     * Returns the "Y" component or ordinate
     * of this vector.
     *
     * @return The vector ordinate
     * @since 1.0.0
     */
    @Override
    public float y() {
        return y;
    }

    /**
     * Returns a new {@link QuaternionFloat vector} with
     * the given {@code y} value and the same {@code x}
     * and {@code z} values as this vector.
     *
     * @param y The new Y value
     * @return The updated vector
     * @since 1.0.0
     */
    @Contract(value = "_ -> new", pure = true)
    public @NotNull QuaternionFloat y(final float y) {
        return new QuaternionFloat(this.x, y, this.z, this.w);
    }

    /**
     * Returns the "Z" component or applicate
     * of this vector.
     *
     * @return The vector applicate
     * @since 1.0.0
     */
    @Override
    public float z() {
        return z;
    }

    /**
     * Returns a new {@link QuaternionFloat vector} with
     * the given {@code z} value and the same {@code x}
     * and {@code y} values as this vector.
     *
     * @param z The new Z value
     * @return The updated vector
     * @since 1.0.0
     */
    @Contract(value = "_ -> new", pure = true)
    public @NotNull QuaternionFloat z(final float z) {
        return new QuaternionFloat(this.x, this.y, z, this.w);
    }

    /**
     * Returns the "W" component or applicate
     * of this vector.
     *
     * @return The vector applicate
     * @since 1.13.0
     */
    @Override
    public float w() {
        return w;
    }

    /**
     * Returns a new {@link QuaternionFloat vector} with
     * the given {@code w} value and the same {@code x}
     * and {@code y} values as this vector.
     *
     * @param w The new W value
     * @return The updated vector
     * @since 1.13.0
     */
    @Contract(value = "_ -> new", pure = true)
    public @NotNull QuaternionFloat w(final float w) {
        return new QuaternionFloat(this.x, this.y, this.z, w);
    }

    /**
     * Returns a new {@link QuaternionFloat vector} result of
     * adding the given {@code x}, {@code y} and {@code z}
     * values to this vector.
     *
     * @param x The value to add to the abscissa
     * @param y The value to add to the ordinate
     * @param z The value to add to the applicate
     * @param w The value to add to the applicate
     * @return The result vector
     * @since 1.0.0
     */
    @Contract(value = "_, _, _, _ -> new", pure = true)
    public @NotNull QuaternionFloat add(final float x, final float y, final float z, final float w) {
        return new QuaternionFloat(this.x + x, this.y + y, this.z + z, this.w + w);
    }

    /**
     * Returns a new {@link QuaternionFloat vector} result of
     * adding the given {@code value} to this vector.
     *
     * @param value The value to add
     * @return The result vector
     * @since 1.0.0
     */
    @Contract(value = "_ -> new", pure = true)
    public @NotNull QuaternionFloat add(final @NotNull QuaternionFloat value) {
        return new QuaternionFloat(this.x + value.x(), this.y + value.y(), this.z + value.z(), this.w + value.w());
    }

    /**
     * Returns a new {@link QuaternionFloat vector} result of
     * subtracting the given {@code x}, {@code y} and {@code z}
     * values from this vector.
     *
     * @param x The value to subtract from the abscissa
     * @param y The value to subtract from the ordinate
     * @param z The value to subtract from the applicate
     * @return The result vector
     * @since 1.0.0
     */
    @Contract(value = "_, _, _, _ -> new", pure = true)
    public @NotNull QuaternionFloat subtract(final float x, final float y, final float z, final float w) {
        return new QuaternionFloat(this.x - x, this.y - y, this.z - z, this.w - w);
    }

    /**
     * Returns a new {@link QuaternionFloat vector} result of
     * subtracting the given {@code value} from this vector.
     *
     * @param value The value to subtract
     * @return The result vector
     * @since 1.0.0
     */
    @Contract(value = "_ -> new", pure = true)
    public @NotNull QuaternionFloat subtract(final @NotNull QuaternionFloat value) {
        return new QuaternionFloat(this.x - value.x(), this.y - value.y(), this.z - value.z(), this.w - value.w());
    }

    /**
     * Returns a new {@link QuaternionFloat vector} result of
     * multiplying this vector by the given scalar {@code value}.
     *
     * @param value The scalar value
     * @return The result vector
     * @since 1.0.0
     */
    public @NotNull QuaternionFloat multiply(final float value) {
        return new QuaternionFloat(this.x * value, this.y * value, this.z * value, this.w * value);
    }

    /**
     * Returns a new {@link QuaternionFloat vector} result of
     * multiplying this vector by the given {@code x}, {@code y}
     * and {@code z} values, one by one.
     *
     * @param x The factor for the abscissa
     * @param y The factor for the ordinate
     * @param z The factor for the applicate
     * @return The result vector
     * @since 1.0.0
     */
    public @NotNull QuaternionFloat multiply(final float x, final float y, final float z, final float w) {
        return new QuaternionFloat(this.x * x, this.y * y, this.z * z, this.w * w);
    }

    /**
     * Returns a new {@link QuaternionFloat vector} result of
     * multiplying this vector by the given {@code value},
     * one by one (Xa*Xb, Ya*Yb, Za*Zb).
     *
     * @param value The factor vector
     * @return The result vector
     * @since 1.0.0
     */
    public @NotNull QuaternionFloat multiply(final @NotNull QuaternionFloat value) {
        return new QuaternionFloat(this.x * value.x(), this.y * value.y(), this.z * value.z(), this.w * value.w());
    }

    /**
     * Returns a new {@link QuaternionFloat vector} result of
     * dividing this vector by the given scalar {@code value}.
     *
     * @param value The divisor
     * @return The result vector
     * @since 1.0.0
     */
    @Contract(value = "_ -> new", pure = true)
    public @NotNull QuaternionFloat divide(final float value) {
        return new QuaternionFloat(this.x / value, this.y / value, this.z / value, this.w / value);
    }

    /**
     * Returns a new {@link QuaternionFloat vector} result of
     * dividing this vector by the given {@code x}, {@code y}
     * and {@code z} values, one by one.
     *
     * @param x The divisor for the abscissa
     * @param y The divisor for the ordinate
     * @param z The divisor for the applicate
     * @return The result vector
     * @since 1.0.0
     */
    @Contract(value = "_, _, _, _ -> new", pure = true)
    public @NotNull QuaternionFloat divide(final float x, final float y, final float z, final float w) {
        return new QuaternionFloat(this.x / x, this.y / y, this.z / z, this.w / z);
    }

    /**
     * Returns a new {@link QuaternionFloat vector} result of
     * dividing this vector by the given {@code value},
     * one by one (Xa/Xb, Ya/Yb, Za/Zb).
     *
     * @param value The divisor vector
     * @return The result vector
     * @since 1.0.0
     */
    @Contract(value = "_ -> new", pure = true)
    public @NotNull QuaternionFloat divide(final @NotNull QuaternionFloat value) {
        return new QuaternionFloat(this.x / value.x(), this.y / value.y(), this.z / value.z(), this.w / value.w());
    }

    /**
     * Returns the dot product of this and the given
     * vector.
     *
     * @param vector The vector to calculate the dot product with
     * @return The dot product
     * @since 1.0.0
     */
    @Contract(pure = true)
    public float dot(final @NotNull QuaternionFloat vector) {
        return this.x * vector.x() + this.y * vector.y() + this.z * vector.z() + this.w * vector.w();
    }

    /**
     * Converts this vector to an array of
     * {@code float} values with X, Y, Z
     * order.
     *
     * @return This vector as an array
     * @since 1.0.0
     */
    public float @NotNull [] toArray() {
        return new float[]{x, y, z};
    }

    public float @NotNull [] toArray(Function<Float, Float> block) {
        return new float[]{block.apply(x), block.apply(y), block.apply(z), block.apply(w)};
    }

    @NotNull
    @Override
    public Iterator<Float> iterator() {
        return Arrays.asList(x, y, z, w).iterator();
    }

    @Override
    public @NotNull Stream<? extends ExaminableProperty> examinableProperties() {
        return Stream.of(
                ExaminableProperty.of("x", x),
                ExaminableProperty.of("y", y),
                ExaminableProperty.of("z", z),
                ExaminableProperty.of("w", w)
        );
    }

    @Override
    public @NotNull String toString() {
        return examine(StringExaminer.simpleEscaping());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        QuaternionFloat that = (QuaternionFloat) o;
        return x == that.x
                && y == that.y
                && z == that.z
                && w == that.w;
    }

}
