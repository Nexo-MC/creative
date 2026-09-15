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
package team.unnamed.creative.serialize.minecraft.equipment;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonWriter;
import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.ApiStatus;
import team.unnamed.creative.equipment.Equipment;
import team.unnamed.creative.equipment.EquipmentLayer;
import team.unnamed.creative.equipment.EquipmentLayerDye;
import team.unnamed.creative.equipment.EquipmentLayerType;
import team.unnamed.creative.equipment.EquipmentTrimOverride;
import team.unnamed.creative.metadata.pack.PackFormat;
import team.unnamed.creative.serialize.minecraft.base.KeySerializer;
import team.unnamed.creative.serialize.minecraft.io.JsonResourceDeserializer;
import team.unnamed.creative.serialize.minecraft.io.JsonResourceSerializer;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@ApiStatus.Internal
public final class EquipmentSerializer implements JsonResourceSerializer<Equipment>, JsonResourceDeserializer<Equipment> {
    public static final EquipmentSerializer INSTANCE = new EquipmentSerializer();

    private EquipmentSerializer() {
    }

    @Override
    public void serializeToJson(Equipment equipment, JsonWriter writer, PackFormat packFormat) throws IOException {
        writer.beginObject()
            .name("layers")
            .beginObject();
        for (Map.Entry<EquipmentLayerType, List<EquipmentLayer>> entry : equipment.layers().entrySet()) {
            writer.name(entry.getKey().name().toLowerCase(Locale.ENGLISH))
                .beginArray();
            for (EquipmentLayer layer : entry.getValue()) {
                writer.beginObject()
                    .name("texture")
                    .value(KeySerializer.toString(layer.texture()));
                final boolean usePlayerTexture = layer.usePlayerTexture();
                if (usePlayerTexture != EquipmentLayer.DEFAULT_USE_PLAYER_TEXTURE) {
                    writer.name("use_player_texture").value(usePlayerTexture);
                }
                final EquipmentLayerDye dye = layer.dye();
                if (dye != null) {
                    writer.name("dyeable").beginObject();
                    final Integer colorWhenUndyed = dye.colorWhenUndyed();
                    if (colorWhenUndyed != null) {
                        writer.name("color_when_undyed").value(colorWhenUndyed);
                    }
                    writer.endObject();
                }
                writer.endObject();
            }
            writer.endArray();
        }
        writer.endObject();

        final List<EquipmentTrimOverride> trimOverrides = equipment.trimOverrides();
        if (!trimOverrides.isEmpty()) {
            writer.name("trim_overrides").beginArray();
            for (EquipmentTrimOverride trimOverride : trimOverrides) {
                writer.beginObject();
                final Key texture = trimOverride.texture();
                if (texture != null) writer.name("texture").value(KeySerializer.toString(texture));
                final Key palette = trimOverride.palette();
                if (palette != null) writer.name("palette").value(KeySerializer.toString(palette));

                writer.name("when").beginObject();
                final Key material = trimOverride.material();
                if (material != null) writer.name("material").value(KeySerializer.toString(material));
                final Key pattern = trimOverride.pattern();
                if (pattern != null) writer.name("pattern").value(KeySerializer.toString(pattern));
                writer.endObject().endObject();
            }
            writer.endArray();
        }
        writer.endObject();
    }

    @Override
    public Equipment deserializeFromJson(JsonElement node, Key key, PackFormat packFormat) {
        final JsonObject root = node.getAsJsonObject();
        final JsonObject layers = root.getAsJsonObject("layers");
        final Equipment.Builder builder = Equipment.equipment()
                .key(key);
        for (Map.Entry<String, JsonElement> entry : layers.entrySet()) {
            final EquipmentLayerType type = EquipmentLayerType.valueOf(entry.getKey().toUpperCase(Locale.ENGLISH));
            for (JsonElement element : entry.getValue().getAsJsonArray()) {
                final JsonObject object = element.getAsJsonObject();
                final Key texture = Key.key(object.get("texture").getAsString());
                final boolean usePlayerTexture = object.has("use_player_texture") && object.get("use_player_texture").getAsBoolean();
                final EquipmentLayerDye dye;
                if (object.has("dyeable")) {
                    final JsonObject dyeable = object.getAsJsonObject("dyeable");
                    final Integer colorWhenUndyed = dyeable.has("color_when_undyed") ? dyeable.get("color_when_undyed").getAsInt() : null;
                    dye = EquipmentLayerDye.equipmentLayerDye(colorWhenUndyed);
                } else {
                    dye = null;
                }
                builder.addLayer(type, EquipmentLayer.layer(texture, dye, usePlayerTexture));
            }
        }
        if (root.has("trim_overrides")) {
            for (JsonElement element : root.getAsJsonArray("trim_overrides")) {
                final JsonObject object = element.getAsJsonObject();
                final JsonObject when = object.getAsJsonObject("when");
                builder.addTrimOverride(EquipmentTrimOverride.trimOverride(
                        keyOrNull(when, "material"),
                        keyOrNull(when, "pattern"),
                        keyOrNull(object, "texture"),
                        keyOrNull(object, "palette")
                ));
            }
        }
        return builder.build();
    }

    private static Key keyOrNull(JsonObject object, String name) {
        return object.has(name) ? Key.key(object.get(name).getAsString()) : null;
    }
}
