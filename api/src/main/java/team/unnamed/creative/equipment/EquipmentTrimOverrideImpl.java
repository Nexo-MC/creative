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
package team.unnamed.creative.equipment;

import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.Nullable;

record EquipmentTrimOverrideImpl(Key material, Key pattern, Key texture, Key palette) implements EquipmentTrimOverride {
    EquipmentTrimOverrideImpl {
        if (material == null && pattern == null) {
            throw new IllegalArgumentException("At least one of 'material' or 'pattern' must be specified");
        }
        if (texture == null && palette == null) {
            throw new IllegalArgumentException("At least one of 'texture' or 'palette' must be specified");
        }
    }

    @Override
    public @Nullable Key material() {
        return material;
    }

    @Override
    public @Nullable Key pattern() {
        return pattern;
    }

    @Override
    public @Nullable Key texture() {
        return texture;
    }

    @Override
    public @Nullable Key palette() {
        return palette;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" + "material=" + material + ", pattern=" + pattern + ", texture=" + texture + ", palette=" + palette + "}";
    }
}
