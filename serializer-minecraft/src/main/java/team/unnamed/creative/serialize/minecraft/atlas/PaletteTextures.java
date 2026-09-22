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
package team.unnamed.creative.serialize.minecraft.atlas;

import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import team.unnamed.creative.ResourcePack;
import team.unnamed.creative.atlas.Atlas;
import team.unnamed.creative.atlas.AtlasSource;
import team.unnamed.creative.atlas.PalettedPermutationsAtlasSource;
import team.unnamed.creative.metadata.pack.FormatVersion;
import team.unnamed.creative.metadata.pack.PackFormat;
import team.unnamed.creative.metadata.pack.PackMeta;
import team.unnamed.creative.overlay.Overlay;
import team.unnamed.creative.overlay.ResourceContainer;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import static team.unnamed.creative.serialize.minecraft.MinecraftResourcePackStructure.TEXTURE_EXTENSION;

/**
 * The palette identifiers named by the {@link PalettedPermutationsAtlasSource}s of a pack,
 * resolving where the textures behind them must be written for a given pack format.
 *
 * <p>Palette identifiers resolved to {@code <namespace>:textures/<path>.png} before pack
 * format {@link FormatVersion#FORMAT_26_3} and to {@code <namespace>:textures/palettes/<path>.png}
 * from it onwards, so a pack whose format range spans the change has to ship both copies.</p>
 *
 * <p>Identifiers are collected pack-wide because atlas sources concatenate across the pack
 * and its overlays, so the container naming a palette is often not the one holding it.</p>
 */
@ApiStatus.Internal
public final class PaletteTextures {

    private static final String PALETTES_PREFIX = "palettes/";

    private final Set<Key> ids;
    private final ResourcePack resourcePack;
    private final PackFormat declaredFormats;

    private PaletteTextures(final @NotNull Set<Key> ids, final @NotNull ResourcePack resourcePack, final @NotNull PackFormat declaredFormats) {
        this.ids = ids;
        this.resourcePack = resourcePack;
        this.declaredFormats = declaredFormats;
    }

    public static @NotNull PaletteTextures of(final @NotNull ResourcePack resourcePack) {
        final Set<Key> ids = new HashSet<>();
        collectPaletteIds(resourcePack, ids);
        for (final Overlay overlay : resourcePack.overlays()) {
            collectPaletteIds(overlay, ids);
        }
        final PackMeta packMeta = resourcePack.metadata().meta(PackMeta.class);
        return new PaletteTextures(ids, resourcePack, packMeta == null ? PackFormat.UNKNOWN : packMeta.formats());
    }

    /**
     * Maps every palette texture held by the given container to the texture keys it has to be
     * written as. An empty collection marks a texture as a copy that the given format does not
     * need, textures absent from the map are written as-is.
     */
    public @NotNull Map<Key, Collection<Key>> placements(final @NotNull ResourceContainer container, final @NotNull PackFormat format) {
        if (ids.isEmpty()) {
            return Collections.emptyMap();
        }

        final PackFormat reach = reachOf(container, format);
        final boolean unknownFormat = PackFormat.UNKNOWN.equals(reach);
        final boolean legacy = unknownFormat || reach.min().major() < FormatVersion.FORMAT_26_3;
        final boolean modern = unknownFormat || reach.max().major() >= FormatVersion.FORMAT_26_3;

        final Map<Key, Collection<Key>> placements = new HashMap<>();

        for (final Key id : ids) {
            final Key legacyKey = Key.key(id.namespace(), id.value() + TEXTURE_EXTENSION);
            final Key modernKey = Key.key(id.namespace(), PALETTES_PREFIX + id.value() + TEXTURE_EXTENSION);
            final boolean hasLegacy = container.texture(legacyKey) != null;
            final boolean hasModern = container.texture(modernKey) != null;

            if (hasLegacy && hasModern) {
                // both copies are shipped, each one stays where it is and the unneeded one is dropped
                place(placements, legacyKey, legacy ? legacyKey : null);
                place(placements, modernKey, modern ? modernKey : null);
            } else if (hasLegacy || hasModern) {
                final Key source = hasLegacy ? legacyKey : modernKey;
                place(placements, source, legacy ? legacyKey : null);
                place(placements, source, modern ? modernKey : null);
            }
            // otherwise the palette lives in another container, or the pack does not ship it at all
        }
        return placements;
    }

    /**
     * The formats the files of the given container are read by. An overlay is read only by the
     * clients its entry covers, while the pack-root is read by every client the pack declares
     * support for, whichever format the writer targets.
     */
    private @NotNull PackFormat reachOf(final @NotNull ResourceContainer container, final @NotNull PackFormat format) {
        if (container != resourcePack || PackFormat.UNKNOWN.equals(declaredFormats)) {
            return format;
        }
        return PackFormat.UNKNOWN.equals(format) ? declaredFormats : declaredFormats.union(format);
    }

    /**
     * Adds a target for the given palette texture, a null target only marks it as one this
     * class decides on, so that a texture left without any target is dropped.
     */
    private static void place(final @NotNull Map<Key, Collection<Key>> placements, final @NotNull Key source, final Key target) {
        final Collection<Key> targets = placements.computeIfAbsent(source, key -> new LinkedHashSet<>());
        if (target != null) {
            targets.add(target);
        }
    }

    private static void collectPaletteIds(final @NotNull ResourceContainer container, final @NotNull Set<Key> ids) {
        for (final Atlas atlas : container.atlases()) {
            for (final AtlasSource source : atlas.sources()) {
                if (source instanceof PalettedPermutationsAtlasSource paletted) {
                    ids.add(paletted.paletteKey());
                    ids.addAll(paletted.permutations().values());
                }
            }
        }
    }

}
