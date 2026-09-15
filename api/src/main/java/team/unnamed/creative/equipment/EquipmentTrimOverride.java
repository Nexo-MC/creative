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
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Swaps the texture or palette used by an equipment asset when it
 * is trimmed with a specific trim material or pattern.
 *
 * <p>Replaces the functionality previously provided by the
 * {@code override_armor_assets} field of trim materials, as well
 * as custom atlas overrides.</p>
 *
 * @since 1.15.0
 * @sinceMinecraft 26.3
 * @see Equipment
 */
public interface EquipmentTrimOverride {
    /**
     * Returns the trim material that must be applied for this
     * override to match, or null if any material matches.
     *
     * @return The matched trim material
     * @since 1.15.0
     * @sinceMinecraft 26.3
     */
    @Nullable Key material();

    /**
     * Returns the trim pattern that must be applied for this
     * override to match, or null if any pattern matches.
     *
     * @return The matched trim pattern
     * @since 1.15.0
     * @sinceMinecraft 26.3
     */
    @Nullable Key pattern();

    /**
     * Returns the replacement trim pattern asset used when this
     * override matches, specified in the same format as the
     * {@code asset_id} of a trim pattern.
     *
     * <p>If null, the default texture for the pattern is used.</p>
     *
     * @return The replacement texture
     * @since 1.15.0
     * @sinceMinecraft 26.3
     */
    @Nullable Key texture();

    /**
     * Returns the replacement palette used when this override
     * matches.
     *
     * <p>If null, no palette remapping is applied to the texture.</p>
     *
     * @return The replacement palette
     * @since 1.15.0
     * @sinceMinecraft 26.3
     */
    @Nullable Key palette();

    /**
     * Creates a new trim override.
     *
     * <p>At least one of {@code material} or {@code pattern} must be given,
     * and at least one of {@code texture} or {@code palette} must be given.
     * If both conditions are given, both must match.</p>
     *
     * @param material The matched trim material
     * @param pattern The matched trim pattern
     * @param texture The replacement texture
     * @param palette The replacement palette
     * @return The created trim override
     * @since 1.15.0
     * @sinceMinecraft 26.3
     */
    @Contract(value = "_, _, _, _ -> new", pure = true)
    static @NotNull EquipmentTrimOverride trimOverride(final @Nullable Key material, final @Nullable Key pattern, final @Nullable Key texture, final @Nullable Key palette) {
        return new EquipmentTrimOverrideImpl(material, pattern, texture, palette);
    }

    /**
     * Creates a new trim override matching the given trim material.
     *
     * @param material The matched trim material
     * @param texture The replacement texture
     * @param palette The replacement palette
     * @return The created trim override
     * @since 1.15.0
     * @sinceMinecraft 26.3
     */
    @Contract(value = "_, _, _ -> new", pure = true)
    static @NotNull EquipmentTrimOverride whenMaterial(final @NotNull Key material, final @Nullable Key texture, final @Nullable Key palette) {
        return new EquipmentTrimOverrideImpl(material, null, texture, palette);
    }

    /**
     * Creates a new trim override matching the given trim pattern.
     *
     * @param pattern The matched trim pattern
     * @param texture The replacement texture
     * @param palette The replacement palette
     * @return The created trim override
     * @since 1.15.0
     * @sinceMinecraft 26.3
     */
    @Contract(value = "_, _, _ -> new", pure = true)
    static @NotNull EquipmentTrimOverride whenPattern(final @NotNull Key pattern, final @Nullable Key texture, final @Nullable Key palette) {
        return new EquipmentTrimOverrideImpl(null, pattern, texture, palette);
    }
}
