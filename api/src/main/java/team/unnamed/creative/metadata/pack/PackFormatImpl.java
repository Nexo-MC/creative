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
package team.unnamed.creative.metadata.pack;

import net.kyori.examination.ExaminableProperty;
import net.kyori.examination.string.StringExaminer;
import org.jetbrains.annotations.NotNull;

import java.util.stream.Stream;

record PackFormatImpl(@NotNull FormatVersion min, @NotNull FormatVersion max) implements PackFormat {

    PackFormatImpl(final @NotNull FormatVersion min, final @NotNull FormatVersion max) {
        this.min = min;
        this.max = max;

        // validate arguments
        if (min.compareTo(max) > 0) {
            throw new IllegalArgumentException("Minimum " + min + " is greater than maximum " + max);
        }
    }

    @Override
    public FormatVersion min() {
        return min;
    }

    @Override
    public FormatVersion max() {
        return max;
    }

    @Override
    public boolean isSingle() {
        return min.equals(max);
    }

    @Override
    public boolean isInRange(final @NotNull FormatVersion format) {
        return format.compareTo(min) >= 0 && format.compareTo(max) <= 0;
    }

    @Override
    @Deprecated
    public boolean isInRange(int format) {
        return isInRange(FormatVersion.of(format));
    }

    @Override
    public @NotNull Stream<? extends ExaminableProperty> examinableProperties() {
        return Stream.of(
                ExaminableProperty.of("min", min),
                ExaminableProperty.of("max", max)
        );
    }

    @Override
    public @NotNull String toString() {
        return examine(StringExaminer.simpleEscaping());
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PackFormatImpl that = (PackFormatImpl) o;
        if (!min.equals(that.min)) return false;
        return max.equals(that.max);
    }

}
