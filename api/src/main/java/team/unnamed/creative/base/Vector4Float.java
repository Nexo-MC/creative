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

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Iterator;
import java.util.stream.Stream;

/**
 * Represents a fixed-size vector of four 32-bit
 * floating numbers, immutable
 *
 * <p>Note: this is a vector and not a matrix, but
 * it has two "X" and two "Y" values (just names)</p>
 *
 * @since 1.0.0
 * @deprecated Bad design. Use two {@link Vector2Float} instead
 */
@Deprecated
@ApiStatus.ScheduledForRemoval(inVersion = "2.0.0")
public record Vector4Float(float x, float y, float x2, float y2) implements Iterable<Float> {

    /**
     * Constant for {@link Vector2Float} value with
     * all four components with {@code 0} zero as
     * value
     */
    public static final Vector4Float ZERO = new Vector4Float(0, 0, 0, 0);

    /**
     * Gets the first "X" value for this
     * vector
     *
     * @return The first X component
     */
    @Override
    public float x() {
        return x;
    }

    /**
     * Gets the first "Y" value for this
     * vector
     *
     * @return The first Y component
     */
    @Override
    public float y() {
        return y;
    }

    /**
     * Gets the second "X" value for
     * this vector
     *
     * @return The second X component
     */
    @Override
    public float x2() {
        return x2;
    }

    /**
     * Gets the second "Y" value for
     * this vector
     *
     * @return The second Y component
     */
    @Override
    public float y2() {
        return y2;
    }

    public float[] toArray() {
        return new float[]{x, y, x2, y2};
    }

    /**
     * Multiplies this vector components by
     * the given scalar, returns a new vector
     * with the resulting values
     *
     * @param scalar The multiplicand
     * @return The resulting vector
     */
    public Vector4Float multiply(float scalar) {
        return new Vector4Float(x * scalar, y * scalar, x2 * scalar, y2 * scalar);
    }

    @NotNull
    @Override
    public Iterator<Float> iterator() {
        return Arrays.asList(x, y, x2, y2).iterator();
    }

    @Override
    public @NotNull String toString() {
        return getClass().getSimpleName() + "{" + "x=" + x + ", y=" + y + ", x2=" + x2 + ", y2=" + y2 + "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vector4Float that = (Vector4Float) o;
        return x == that.x
                && y == that.y
                && x2 == that.x2
                && y2 == that.y2;
    }

}
