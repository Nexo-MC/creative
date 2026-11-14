package team.unnamed.creative.resources;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.jetbrains.annotations.NotNull;
import team.unnamed.creative.ResourcePack;
import team.unnamed.creative.base.Writable;
import team.unnamed.creative.metadata.overlays.OverlayEntry;
import team.unnamed.creative.metadata.overlays.OverlaysMeta;
import team.unnamed.creative.metadata.pack.FormatVersion;
import team.unnamed.creative.metadata.pack.PackFormat;
import team.unnamed.creative.overlay.Overlay;
import team.unnamed.creative.overlay.ResourceContainer;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.WeakHashMap;
import java.util.function.Consumer;
import java.util.regex.Pattern;

import static java.util.Objects.requireNonNull;

public final class CoreShaderMerger {
    private static final Pattern CORE_SHADER = Pattern.compile("assets/[^/]+/shaders/core/.+\\.(vsh|fsh|json|glsl)");
    private static final Pattern SHADER_INCLUDE = Pattern.compile("assets/[^/]+/shaders/include/.+\\.glsl");
    private static final Map<ResourcePack, Sources> SOURCES = Collections.synchronizedMap(new WeakHashMap<>());

    private CoreShaderMerger() {
    }

    public static void clear(final @NotNull ResourcePack resourcePack) {
        SOURCES.remove(requireNonNull(resourcePack, "resourcePack"));
    }

    public static void record(final @NotNull ResourcePack resourcePack, final @NotNull ResourcePack imported) {
        requireNonNull(resourcePack, "resourcePack");
        requireNonNull(imported, "imported");
        final Sources state = SOURCES.computeIfAbsent(resourcePack, pack -> new Sources(snapshot(pack), shaderFiles(pack)));
        state.packs.add(snapshot(imported));
    }

    public static void finish(final @NotNull ResourcePack resourcePack) {
        final Sources state = SOURCES.get(requireNonNull(resourcePack, "resourcePack"));
        if (state != null) state.root = shaderFiles(resourcePack);
    }

    public static void merge(final @NotNull ResourceContainer container, final @NotNull ResourceContainer imported, final @NotNull Consumer<String> onConflict) {
        merge(container, imported, false, onConflict);
    }

    public static void merge(final @NotNull ResourceContainer container, final @NotNull ResourceContainer imported, final boolean generated, final @NotNull Consumer<String> onConflict) {
        requireNonNull(container, "container");
        requireNonNull(imported, "imported");
        requireNonNull(onConflict, "onConflict");
        final Map<String, Map<String, Writable>> groups = new LinkedHashMap<>();
        for (final Map.Entry<String, Writable> entry : imported.unknownFiles().entrySet()) {
            final String path = entry.getKey();
            final String group = CORE_SHADER.matcher(path).matches() ? path.substring(0, path.lastIndexOf('.')) : path;
            groups.computeIfAbsent(group, ignored -> new LinkedHashMap<>()).put(path, entry.getValue());
        }
        for (final Map<String, Writable> files : groups.values()) {
            Map<String, Writable> merged = new LinkedHashMap<>();
            try {
                for (final Map.Entry<String, Writable> entry : files.entrySet()) {
                    final String path = entry.getKey();
                    final Writable existing = container.unknownFile(path);
                    merged.put(path, existing == null || !isShader(path) ? entry.getValue() : merge(path, existing, entry.getValue(), generated));
                }
            } catch (final RuntimeException error) {
                onConflict.accept("Failed to merge core shader " + files.keySet().iterator().next() + ": " + error.getMessage() + ". Keeping the incoming shader");
                merged = files;
            }
            merged.forEach(container::unknownFile);
        }
    }

    public static void mergeOverlays(final @NotNull ResourcePack resourcePack, final @NotNull Collection<String> generatedDirectories, final @NotNull Consumer<String> onConflict) {
        requireNonNull(resourcePack, "resourcePack");
        requireNonNull(generatedDirectories, "generatedDirectories");
        requireNonNull(onConflict, "onConflict");
        final List<OverlayEntry> entries = resourcePack.overlaysMeta() == null ? List.of() : resourcePack.overlaysMeta().entries();
        final Set<String> directories = new HashSet<>();
        entries.forEach(entry -> directories.add(entry.directory()));
        final Sources state = SOURCES.get(resourcePack);
        final List<Source> inputs = new ArrayList<>();
        for (final Source source : state == null ? List.of(snapshot(resourcePack)) : state.packs) {
            inputs.add(new Source(source.root, source.overlays.stream().filter(overlay -> directories.contains(overlay.directory)).toList(), source.generated));
        }
        if (state != null) {
            final List<ShaderOverlay> generated = new ArrayList<>();
            for (final OverlayEntry entry : entries) {
                if (!generatedDirectories.contains(entry.directory())) continue;
                final Overlay overlay = resourcePack.overlay(entry.directory());
                if (overlay == null) continue;
                final Map<String, Writable> files = shaderFiles(overlay);
                if (!files.isEmpty()) generated.add(new ShaderOverlay(entry.directory(), entry.formats(), files));
            }
            inputs.add(new Source(Map.of(), generated, true));
        }
        final List<ShaderOverlay> layers = inputs.stream().flatMap(source -> source.overlays.stream()).toList();
        if (layers.isEmpty()) return;
        final Set<Long> boundaries = new TreeSet<>();
        for (final ShaderOverlay layer : layers) {
            boundaries.add(number(layer.formats.min()));
            boundaries.add(after(layer.formats.max()));
        }
        final Map<String, Writable> changed = new LinkedHashMap<>();
        if (state != null) shaderFiles(resourcePack).forEach((path, data) -> {
            final Writable previous = state.root.get(path);
            if (previous == null || !text(previous).equals(text(data))) changed.put(path, data);
        });
        final List<OverlayEntry> generatedEntries = new ArrayList<>();
        final List<Long> ranges = new ArrayList<>(boundaries);
        for (int i = 0; i + 1 < ranges.size(); i++) {
            final long start = ranges.get(i);
            final long end = ranges.get(i + 1);
            if (layers.stream().noneMatch(layer -> layer.formats.isInRange(version(start)))) continue;
            final List<Map<String, Writable>> active = new ArrayList<>();
            final Map<String, Integer> counts = new HashMap<>();
            for (final Source source : inputs) {
                final Map<String, Writable> files = new LinkedHashMap<>(source.root);
                source.overlays.stream().filter(layer -> layer.formats.isInRange(version(start))).forEach(layer -> files.putAll(layer.files));
                files.keySet().forEach(path -> counts.merge(path, 1, Integer::sum));
                active.add(files);
            }
            if (counts.values().stream().noneMatch(count -> count > 1)) continue;
            String directory = "creative_core_shaders_" + generatedEntries.size();
            while (resourcePack.overlay(directory) != null) directory += "_";
            final Overlay overlay = Overlay.overlay(directory);
            for (int sourceIndex = 0; sourceIndex < inputs.size(); sourceIndex++) {
                final Source source = inputs.get(sourceIndex);
                if (source.generated) changed.forEach(overlay::unknownFile);
                final Overlay shaders = Overlay.overlay("source");
                active.get(sourceIndex).forEach(shaders::unknownFile);
                merge(overlay, shaders, source.generated, onConflict);
            }
            resourcePack.overlay(overlay);
            generatedEntries.add(OverlayEntry.of(PackFormat.format(version(start), before(end)), directory));
        }
        if (!generatedEntries.isEmpty()) {
            final List<OverlayEntry> combined = new ArrayList<>(entries);
            combined.addAll(generatedEntries);
            resourcePack.overlaysMeta(OverlaysMeta.of(combined));
        }
    }

    private static Source snapshot(final ResourcePack resourcePack) {
        final List<ShaderOverlay> overlays = new ArrayList<>();
        if (resourcePack.overlaysMeta() != null) for (final OverlayEntry entry : resourcePack.overlaysMeta().entries()) {
            final Overlay overlay = resourcePack.overlay(entry.directory());
            if (overlay != null) overlays.add(new ShaderOverlay(entry.directory(), entry.formats(), shaderFiles(overlay)));
        }
        return new Source(shaderFiles(resourcePack), overlays, false);
    }

    private static Map<String, Writable> shaderFiles(final ResourceContainer container) {
        final Map<String, Writable> files = new LinkedHashMap<>();
        container.unknownFiles().forEach((path, data) -> {
            if (isShader(path)) files.put(path, data);
        });
        return files;
    }

    private static Writable merge(final String path, final Writable existing, final Writable incoming, final boolean generated) {
        if (generated && SHADER_INCLUDE.matcher(path).matches()) return incoming;
        final String left = text(existing);
        final String right = text(incoming);
        if (left.equals(right)) return incoming;
        final String merged = path.endsWith(".json") ? mergeJson(JsonParser.parseString(left), JsonParser.parseString(right)).toString() : CoreShaderSource.merge(left, right, generated);
        return Writable.stringUtf8(merged);
    }

    private static JsonElement mergeJson(final JsonElement existing, final JsonElement incoming) {
        if (existing.equals(incoming)) return incoming.deepCopy();
        if (existing instanceof JsonObject left && incoming instanceof JsonObject right) {
            final JsonObject result = left.deepCopy();
            right.entrySet().forEach(entry -> result.add(entry.getKey(), result.has(entry.getKey()) ? mergeJson(result.get(entry.getKey()), entry.getValue()) : entry.getValue().deepCopy()));
            return result;
        }
        if (existing instanceof JsonArray left && incoming instanceof JsonArray right) {
            final JsonArray result = left.deepCopy();
            for (final JsonElement value : left) validateParameter(value);
            for (final JsonElement value : right) validateParameter(value);
            for (final JsonElement value : right) {
                final JsonElement name = value instanceof JsonObject object ? object.get("name") : null;
                JsonElement previous = null;
                for (final JsonElement candidate : result) {
                    if (name == null ? candidate.equals(value) : candidate instanceof JsonObject object && Objects.equals(object.get("name"), name)) {
                        previous = candidate;
                        break;
                    }
                }
                if (previous == null) result.add(value.deepCopy());
                else if (!previous.equals(value)) throw new MergeException("conflicting shader parameter " + name);
            }
            return result;
        }
        throw new MergeException("conflicting shader program values " + existing + " and " + incoming);
    }

    private static void validateParameter(final JsonElement value) {
        if (!(value instanceof JsonObject object && object.has("name") || value.isJsonPrimitive() && value.getAsJsonPrimitive().isString())) throw new MergeException("conflicting shader arrays");
    }

    private static String text(final Writable writable) {
        try {
            return writable.toUTF8String();
        } catch (final IOException error) {
            throw new UncheckedIOException(error);
        }
    }

    private static boolean isShader(final String path) {
        return CORE_SHADER.matcher(path).matches() || SHADER_INCLUDE.matcher(path).matches();
    }

    private static long number(final FormatVersion version) {
        return ((long) version.major() << 32) + version.minor();
    }

    private static FormatVersion version(final long number) {
        return FormatVersion.of((int) (number >>> 32), (int) number);
    }

    private static long after(final FormatVersion version) {
        return version.minor() == Integer.MAX_VALUE ? ((long) version.major() + 1) << 32 : number(version) + 1;
    }

    private static FormatVersion before(final long number) {
        return (int) number == 0 ? FormatVersion.of((int) (number >>> 32) - 1, Integer.MAX_VALUE) : version(number - 1);
    }

    private record ShaderOverlay(String directory, PackFormat formats, Map<String, Writable> files) {
    }

    private record Source(Map<String, Writable> root, List<ShaderOverlay> overlays, boolean generated) {
    }

    private static final class Sources {
        private final List<Source> packs = new ArrayList<>();
        private Map<String, Writable> root;

        private Sources(final Source source, final Map<String, Writable> root) {
            packs.add(source);
            this.root = root;
        }
    }
}
