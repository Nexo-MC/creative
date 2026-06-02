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

import com.google.gson.stream.JsonWriter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import team.unnamed.creative.metadata.overlays.OverlayEntry;
import team.unnamed.creative.metadata.overlays.OverlaysMeta;
import team.unnamed.creative.metadata.pack.PackFormat;
import team.unnamed.creative.metadata.pack.FormatVersion;

import java.io.IOException;
import java.io.StringWriter;
import java.io.UncheckedIOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class OverlaysMetaTest {

    private static String toJson(final OverlaysMeta meta, final PackFormat targetFormat) {
        final StringWriter stringWriter = new StringWriter();
        try {
            OverlaysMetaCodec.INSTANCE.write(new JsonWriter(stringWriter), meta, targetFormat);
        } catch (final IOException e) {
            throw new UncheckedIOException(e);
        }
        return stringWriter.toString();
    }

    @Test
    @DisplayName("Test Overlays meta serialization")
    void test_simple_serialization() {
        final OverlaysMeta overlaysMeta = OverlaysMeta.of(
                OverlayEntry.of(PackFormat.format(FormatVersion.of(18)), "v18"),
                OverlayEntry.of(PackFormat.format(FormatVersion.of(19)), "v19")
        );
        assertEquals(
                "{\"entries\":[{\"formats\":18,\"directory\":\"v18\"},{\"formats\":19,\"directory\":\"v19\"}]}",
                OverlaysMetaCodec.INSTANCE.toJson(overlaysMeta)
        );
    }

    @Test
    @DisplayName("Test overlays meta serialization with custom pack format range")
    void test_range_serialization() {
        final OverlaysMeta overlaysMeta = OverlaysMeta.of(
                OverlayEntry.of(PackFormat.format(FormatVersion.of(18), FormatVersion.of(20)), "v18-20"),
                OverlayEntry.of(PackFormat.format(FormatVersion.of(21), FormatVersion.of(24)), "v21-24")
        );
        assertEquals(
                "{\"entries\":[{\"formats\":[18,20],\"directory\":\"v18-20\"},{\"formats\":[21,24],\"directory\":\"v21-24\"}]}",
                OverlaysMetaCodec.INSTANCE.toJson(overlaysMeta)
        );
    }

    @Test
    @DisplayName("Test simple overlays meta deserialization")
    void test_simple_deserialization() {
        final OverlaysMeta overlaysMeta = OverlaysMetaCodec.INSTANCE.fromJson("{\"entries\":[{\"formats\":18,\"directory\":\"v18\"},{\"formats\":19,\"directory\":\"v19\"}]}");
        assertEquals(
                OverlaysMeta.of(
                        OverlayEntry.of(PackFormat.format(FormatVersion.of(18)), "v18"),
                        OverlayEntry.of(PackFormat.format(FormatVersion.of(19)), "v19")
                ),
                overlaysMeta
        );
    }

    @Test
    @DisplayName("Test overlays meta serialization falls back to legacy 'formats' when target format is unknown")
    void test_modern_format_serialization() {
        final OverlaysMeta overlaysMeta = OverlaysMeta.of(
                OverlayEntry.of(PackFormat.format(FormatVersion.of(75), FormatVersion.of(76)), "v75-76")
        );
        // Unknown target (main format 0) is treated as pre-65, so the legacy schema is used
        assertEquals(
                "{\"entries\":[{\"formats\":[75,76],\"directory\":\"v75-76\"}]}",
                OverlaysMetaCodec.INSTANCE.toJson(overlaysMeta)
        );
    }

    @Test
    @DisplayName("Test overlays meta uses min/max_format exclusively when main format and all entries are 65+")
    void test_modern_target_omits_formats() {
        final OverlaysMeta overlaysMeta = OverlaysMeta.of(
                OverlayEntry.of(PackFormat.format(FormatVersion.of(75), FormatVersion.of(76)), "v75-76"),
                OverlayEntry.of(PackFormat.format(FormatVersion.of(84)), "v84")
        );
        // Main pack format 75 and every entry is 65+, so the new schema is used for all
        assertEquals(
                "{\"entries\":[{\"directory\":\"v75-76\",\"min_format\":75,\"max_format\":76},{\"directory\":\"v84\",\"min_format\":84,\"max_format\":84}]}",
                toJson(overlaysMeta, PackFormat.format(FormatVersion.of(75)))
        );
    }

    @Test
    @DisplayName("Test overlays meta uses legacy 'formats' for all entries when the main pack format is pre-65")
    void test_legacy_target_keeps_formats() {
        final OverlaysMeta overlaysMeta = OverlaysMeta.of(
                OverlayEntry.of(PackFormat.format(FormatVersion.of(75), FormatVersion.of(76)), "v75-76")
        );
        // Main pack format 63 must stay parseable by pre-65 clients, so the legacy schema is used
        assertEquals(
                "{\"entries\":[{\"formats\":[75,76],\"directory\":\"v75-76\"}]}",
                toJson(overlaysMeta, PackFormat.format(FormatVersion.of(63)))
        );
    }

    @Test
    @DisplayName("Test a single pre-65 entry forces legacy 'formats' on all entries even with a 65+ main format")
    void test_one_legacy_entry_forces_legacy_for_all() {
        final OverlaysMeta overlaysMeta = OverlaysMeta.of(
                OverlayEntry.of(PackFormat.format(FormatVersion.of(34), FormatVersion.of(45)), "v34-45"),
                OverlayEntry.of(PackFormat.format(FormatVersion.of(75), FormatVersion.of(76)), "v75-76")
        );
        // The 34-45 entry targets pre-65 clients, so every entry must use the legacy schema
        assertEquals(
                "{\"entries\":[{\"formats\":[34,45],\"directory\":\"v34-45\"},{\"formats\":[75,76],\"directory\":\"v75-76\"}]}",
                toJson(overlaysMeta, PackFormat.format(FormatVersion.of(75)))
        );
    }

    @Test
    @DisplayName("Test overlays meta uses legacy 'formats' for ranges spanning format 64/65")
    void test_spanning_format_serialization() {
        final OverlaysMeta overlaysMeta = OverlaysMeta.of(
                OverlayEntry.of(PackFormat.format(FormatVersion.of(60), FormatVersion.of(70)), "v60-70")
        );
        // The entry dips below 65, so the legacy schema is used
        assertEquals(
                "{\"entries\":[{\"formats\":[60,70],\"directory\":\"v60-70\"}]}",
                OverlaysMetaCodec.INSTANCE.toJson(overlaysMeta)
        );
    }

    @Test
    @DisplayName("Test overlays meta deserialization for pack format 65+ without 'formats' field")
    void test_modern_format_deserialization() {
        final OverlaysMeta overlaysMeta = OverlaysMetaCodec.INSTANCE.fromJson("{\"entries\":[{\"directory\":\"v75-76\",\"min_format\":75,\"max_format\":76}]}");
        assertEquals(
                OverlaysMeta.of(
                        OverlayEntry.of(PackFormat.format(FormatVersion.of(75), FormatVersion.of(76)), "v75-76")
                ),
                overlaysMeta
        );
    }

    @Test
    @DisplayName("Test overlays meta deserialization with custom pack format range")
    void test_range_deserialization() {
        final OverlaysMeta overlaysMeta = OverlaysMetaCodec.INSTANCE.fromJson("{\"entries\":[{\"formats\":[18,20],\"directory\":\"v18-20\"},{\"formats\":[21,24],\"directory\":\"v21-24\"}]}");
        assertEquals(
                OverlaysMeta.of(
                        OverlayEntry.of(PackFormat.format(FormatVersion.of(18), FormatVersion.of(20)), "v18-20"),
                        OverlayEntry.of(PackFormat.format(FormatVersion.of(21), FormatVersion.of(24)), "v21-24")
                ),
                overlaysMeta
        );
    }

}
