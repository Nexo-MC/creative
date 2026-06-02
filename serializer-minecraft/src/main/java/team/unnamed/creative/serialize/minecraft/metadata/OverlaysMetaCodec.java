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
package team.unnamed.creative.serialize.minecraft.metadata;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonWriter;
import org.intellij.lang.annotations.Subst;
import org.jetbrains.annotations.NotNull;
import team.unnamed.creative.metadata.overlays.OverlayEntry;
import team.unnamed.creative.metadata.overlays.OverlaysMeta;
import team.unnamed.creative.metadata.pack.FormatVersion;
import team.unnamed.creative.metadata.pack.PackFormat;
import team.unnamed.creative.serialize.minecraft.base.PackFormatSerializer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

final class OverlaysMetaCodec implements MetadataPartCodec<OverlaysMeta> {

    static final MetadataPartCodec<OverlaysMeta> INSTANCE = new OverlaysMetaCodec();

    private OverlaysMetaCodec() {
    }

    @Override
    public @NotNull Class<OverlaysMeta> type() {
        return OverlaysMeta.class;
    }

    @Override
    public @NotNull String name() {
        return "overlays";
    }

    @Override
    public @NotNull OverlaysMeta read(final @NotNull JsonObject node) {
        final JsonArray entries = node.getAsJsonArray("entries");
        final List<OverlayEntry> overlays = new ArrayList<>();
        for (final JsonElement entryNode : entries) {
            final JsonObject entryObject = entryNode.getAsJsonObject();
            final PackFormat formats;
            if (entryObject.has("formats")) {
                formats = PackFormatSerializer.deserialize(entryObject.get("formats"));
            } else {
                // pack format 65+ deprecates "formats" in favor of min_format/max_format
                final int min = entryObject.get("min_format").getAsInt();
                final int max = entryObject.get("max_format").getAsInt();
                formats = PackFormat.format(FormatVersion.of(min), FormatVersion.of(max));
            }
            @Subst("dir") final String directory = entryObject.get("directory").getAsString();
            overlays.add(OverlayEntry.of(formats, directory));
        }
        return OverlaysMeta.of(overlays);
    }

    @Override
    public void write(final @NotNull JsonWriter writer, final @NotNull OverlaysMeta overlays) throws IOException {
        // No target pack format known here (e.g. direct toJson) - keep "formats" so
        // the output stays readable by pre-65 clients. UNKNOWN's min major is 0 (< 65).
        write(writer, overlays, PackFormat.UNKNOWN);
    }

    @Override
    public void write(final @NotNull JsonWriter writer, final @NotNull OverlaysMeta overlays, final @NotNull PackFormat targetFormat) throws IOException {
        // Minecraft reads the whole pack.mcmeta with a single overlay schema, chosen
        // for all entries at once:
        //   - "formats" is understood by every version but is deprecated (warns) on 65+
        //   - "min_format"/"max_format" only exist on pack format 65+
        // The legacy "formats" schema must be used for EVERY entry if a pre-65 client
        // can read the pack (main format starts below 65) or if any overlay entry itself
        // targets below 65. Only when the main format and all entries are 65+ can the new
        // "min_format"/"max_format" schema be used exclusively (and stay warning-free).
        boolean useLegacyFormats = targetFormat.min().major() < 65;
        if (!useLegacyFormats) {
            for (final OverlayEntry overlay : overlays.entries()) {
                if (overlay.formats().min().major() < 65) {
                    useLegacyFormats = true;
                    break;
                }
            }
        }

        writer.beginObject();
        writer.name("entries");
        writer.beginArray();
        for (final OverlayEntry overlay : overlays.entries()) {
            writer.beginObject();
            final int minMajor = overlay.formats().min().major();
            final int maxMajor = overlay.formats().max().major();

            // "formats" is only written when the legacy schema is in use (some pre-65
            // client could read this pack), so newer-only packs stay warning-free.
            if (useLegacyFormats) {
                writer.name("formats");
                PackFormatSerializer.serialize(overlay.formats(), writer);
            }
            writer.name("directory").value(overlay.directory());
            // min_format/max_format are always written for 65+ entries so newer clients
            // get the explicit fields even within a cross-version (legacy schema) pack.
            if (maxMajor > 64) {
                writer.name("min_format").value(minMajor);
                writer.name("max_format").value(maxMajor);
            }

            writer.endObject();
        }
        writer.endArray();
        writer.endObject();
    }

}