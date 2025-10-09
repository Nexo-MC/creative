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

import java.util.stream.Stream;

import static java.util.Objects.requireNonNull;

record ItemImpl(Key key, ItemModel model, boolean handAnimationOnSwap, boolean oversizedInGui, float swapAnimationScale) implements Item {
    ItemImpl(final @NotNull Key key, final @NotNull ItemModel model, final boolean handAnimationOnSwap, final boolean oversizedInGui, final float swapAnimationScale) {
        this.key = requireNonNull(key, "key");
        this.model = requireNonNull(model, "model");
        this.handAnimationOnSwap = handAnimationOnSwap;
        this.oversizedInGui = oversizedInGui;
        this.swapAnimationScale = swapAnimationScale;
    }

    @Override
    public @NotNull Key key() {
        return key;
    }

    @Override
    public @NotNull ItemModel model() {
        return model;
    }

    @Override
    public @NotNull Stream<? extends ExaminableProperty> examinableProperties() {
        return Stream.of(
                ExaminableProperty.of("key", key),
                ExaminableProperty.of("model", model),
                ExaminableProperty.of("handAnimationOnSwap", handAnimationOnSwap),
                ExaminableProperty.of("oversized_in_gui", oversizedInGui),
                ExaminableProperty.of("swap_animation_scale", swapAnimationScale)
        );
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ItemImpl item = (ItemImpl) o;
        return key.equals(item.key)
                && model.equals(item.model)
                && handAnimationOnSwap == item.handAnimationOnSwap
                && oversizedInGui == item.oversizedInGui
                && swapAnimationScale == item.swapAnimationScale;
    }

    @Override
    public @NotNull String toString() {
        return examine(StringExaminer.simpleEscaping());
    }
}
