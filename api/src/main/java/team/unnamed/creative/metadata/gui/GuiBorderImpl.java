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
package team.unnamed.creative.metadata.gui;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.stream.Stream;

record GuiBorderImpl(int top, int bottom, int left, int right) implements GuiBorder {
    GuiBorderImpl(final int top, final int bottom, final int left, final int right) {
        this.top = top;
        this.bottom = bottom;
        this.left = left;
        this.right = right;
        validate();
    }

    private void validate() {
        if (top < 0)
            throw new IllegalArgumentException("top border size must be greater than or equal to 0, got " + top);
        if (bottom < 0)
            throw new IllegalArgumentException("bottom border size must be greater than or equal to 0, got " + bottom);
        if (left < 0)
            throw new IllegalArgumentException("left border size must be greater than or equal to 0, got " + left);
        if (right < 0)
            throw new IllegalArgumentException("right border size must be greater than or equal to 0, got " + right);
    }

    @Override
    public @NotNull String toString() {
        return getClass().getSimpleName() + "{" + "top=" + top + ", bottom=" + bottom + ", left=" + left + ", right=" + right + "}";
    }

    @Override
    public boolean equals(final @Nullable Object o) {
        if (this == o) return true;
        if (!(o instanceof GuiBorderImpl guiBorder)) return false;
        return top == guiBorder.top && bottom == guiBorder.bottom && left == guiBorder.left && right == guiBorder.right;
    }

}
