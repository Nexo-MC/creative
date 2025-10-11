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
package team.unnamed.creative.item.property;

import net.kyori.adventure.key.Key;
import net.kyori.examination.ExaminableProperty;
import net.kyori.examination.string.StringExaminer;
import org.intellij.lang.annotations.Subst;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.stream.Stream;

import static java.util.Objects.requireNonNull;

record NoFieldItemBooleanPropertyImpl(Key key) implements NoFieldItemBooleanProperty {
    static final NoFieldItemBooleanProperty BROKEN = new NoFieldItemBooleanPropertyImpl("broken");
    static final NoFieldItemBooleanProperty BUNDLE_HAS_SELECTED_ITEM = new NoFieldItemBooleanPropertyImpl("bundle/has_selected_item");
    static final NoFieldItemBooleanProperty CARRIED = new NoFieldItemBooleanPropertyImpl("carried");
    static final NoFieldItemBooleanProperty DAMAGED = new NoFieldItemBooleanPropertyImpl("damaged");
    static final NoFieldItemBooleanProperty EXTENDED_VIEW = new NoFieldItemBooleanPropertyImpl("extended_view");
    static final NoFieldItemBooleanProperty FISHING_ROD_CAST = new NoFieldItemBooleanPropertyImpl("fishing_rod/cast");
    static final NoFieldItemBooleanProperty SELECTED = new NoFieldItemBooleanPropertyImpl("selected");
    static final NoFieldItemBooleanProperty USING_ITEM = new NoFieldItemBooleanPropertyImpl("using_item");
    static final NoFieldItemBooleanProperty VIEW_ENTITY = new NoFieldItemBooleanPropertyImpl("view_entity");

    NoFieldItemBooleanPropertyImpl(final @NotNull Key key) {
        this.key = requireNonNull(key, "key");
    }

    NoFieldItemBooleanPropertyImpl(@Subst("minecraft:broken") final @NotNull String key) {
        this(Key.key(key));
    }

    @Override
    public @NotNull Key key() {
        return key;
    }

    @Override
    public @NotNull Stream<? extends ExaminableProperty> examinableProperties() {
        return Stream.of(ExaminableProperty.of("key", key));
    }

    @Override
    public boolean equals(final @Nullable Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        final NoFieldItemBooleanPropertyImpl that = (NoFieldItemBooleanPropertyImpl) o;
        return key.equals(that.key);
    }

    @Override
    public @NotNull String toString() {
        return examine(StringExaminer.simpleEscaping());
    }
}
