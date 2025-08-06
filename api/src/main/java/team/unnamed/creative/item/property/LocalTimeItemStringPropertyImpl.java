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

import net.kyori.examination.ExaminableProperty;
import net.kyori.examination.string.StringExaminer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.stream.Stream;

import static java.util.Objects.requireNonNull;

record LocalTimeItemStringPropertyImpl(String locale, String timezone,
                                       String pattern) implements LocalTimeItemStringProperty {
    LocalTimeItemStringPropertyImpl(final @NotNull String locale, final @Nullable String timezone, final @NotNull String pattern) {
        this.locale = requireNonNull(locale, "locale");
        this.timezone = timezone;
        this.pattern = requireNonNull(pattern, "pattern");
    }

    @Override
    public @NotNull String locale() {
        return locale;
    }

    @Override
    public @Nullable String timezone() {
        return timezone;
    }

    @Override
    public @NotNull String pattern() {
        return pattern;
    }

    @Override
    public @NotNull Stream<? extends ExaminableProperty> examinableProperties() {
        return Stream.of(
                ExaminableProperty.of("locale", locale),
                ExaminableProperty.of("timezone", timezone),
                ExaminableProperty.of("pattern", pattern)
        );
    }

    @Override
    public boolean equals(final Object object) {
        if (this == object) return true;
        if (!(object instanceof LocalTimeItemStringPropertyImpl that)) return false;
        return locale.equals(that.locale) && pattern.equals(that.pattern) && Objects.equals(timezone, that.timezone);
    }

    @Override
    public @NotNull String toString() {
        return examine(StringExaminer.simpleEscaping());
    }
}
