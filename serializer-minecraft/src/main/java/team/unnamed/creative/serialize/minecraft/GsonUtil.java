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
package team.unnamed.creative.serialize.minecraft;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import team.unnamed.creative.base.Vector2Float;
import team.unnamed.creative.base.Vector3Float;

import java.io.IOException;
import java.io.Reader;

@ApiStatus.Internal
public final class GsonUtil {
    // we use deprecated methods to ensure backwards compatibility with
    // old versions of Gson, since some Minecraft server implementations
    // use them
    @SuppressWarnings("deprecation")
    private static final JsonParser JSON_PARSER = new JsonParser();

    private static final float FLOAT_CACHE_SCALE = 16F;
    private static final int FLOAT_CACHE_RANGE = 1024;
    private static final String[] FLOAT_CACHE = new String[FLOAT_CACHE_RANGE * 2 + 1];

    static {
        for (int i = -FLOAT_CACHE_RANGE; i <= FLOAT_CACHE_RANGE; i++) {
            FLOAT_CACHE[i + FLOAT_CACHE_RANGE] = Float.toString(i / FLOAT_CACHE_SCALE);
        }
    }

    @SuppressWarnings("deprecation")
    public static @NotNull JsonElement parseString(final @NotNull String string) {
        return JSON_PARSER.parse(string);
    }

    @SuppressWarnings("deprecation")
    public static @NotNull JsonElement parseReader(final @NotNull Reader reader) {
        return JSON_PARSER.parse(reader);
    }

    @SuppressWarnings("deprecation")
    public static @NotNull JsonElement parseReader(final @NotNull JsonReader reader) {
        return JSON_PARSER.parse(reader);
    }

    /*
     * Determines if a property with the given name
     * exists in the specified object and it's
     * not null
     */
    public static boolean isNullOrAbsent(JsonObject object, String name) {
        return !object.has(name) || object.get(name).isJsonNull();
    }

    public static boolean getBoolean(JsonObject object, String key, boolean def) {
        if (!object.has(key)) {
            // no value, return default
            return def;
        }
        JsonElement element = object.get(key);
        if (element.isJsonPrimitive() && (((JsonPrimitive) element).isBoolean() || ((JsonPrimitive) element).isString())) {
            // valid value
            return element.getAsBoolean();
        } else {
            // invalid value!
            throw new IllegalStateException("Field '" + key + "' must be a boolean");
        }
    }

    public static boolean isInt(JsonObject object, String key) {
        if (!object.has(key)) {
            // no value, return default
            return false;
        }
        JsonElement element = object.get(key);
        return element.isJsonPrimitive() && ((JsonPrimitive) element).isNumber();
    }

    public static int getInt(JsonObject object, String key, int def) {
        if (!object.has(key)) {
            // no value, return default
            return def;
        }
        JsonElement element = object.get(key);
        if (element.isJsonPrimitive() && ((JsonPrimitive) element).isNumber()) {
            return element.getAsInt();
        } else {
            throw new IllegalStateException("Field '" + key + "' must be an integer");
        }
    }

    public static float getFloat(JsonObject object, String key, float def) {
        if (!object.has(key)) {
            // no value, return default
            return def;
        }
        JsonElement element = object.get(key);
        if (element.isJsonPrimitive() && ((JsonPrimitive) element).isNumber()) {
            return (float) element.getAsDouble();
        } else {
            throw new IllegalStateException("Field '" + key + "' must be a float");
        }
    }

    /**
     * Writes the same text as {@link JsonWriter#value(float)}.
     * Multiples of 1/16 within [-64, 64] cover most model coordinates and come from a precomputed table.
     * Only a plain JsonWriter takes the table path, since subclasses like Gson's JsonTreeWriter reject {@link JsonWriter#jsonValue(String)}
     */
    public static void writeFloat(JsonWriter writer, float value) throws IOException {
        float scaled = value * FLOAT_CACHE_SCALE;
        int index = (int) scaled;
        if (index == scaled && index >= -FLOAT_CACHE_RANGE && index <= FLOAT_CACHE_RANGE
                && (index != 0 || Float.floatToRawIntBits(value) == 0)
                && writer.getClass() == JsonWriter.class) {
            writer.jsonValue(FLOAT_CACHE[index + FLOAT_CACHE_RANGE]);
        } else {
            writer.value(value);
        }
    }

    public static void writeVector3Float(JsonWriter writer, Vector3Float vector) throws IOException {
        writer.beginArray();
        writeFloat(writer, vector.x());
        writeFloat(writer, vector.y());
        writeFloat(writer, vector.z());
        writer.endArray();
    }

    public static void writeVector2Float(JsonWriter writer, Vector2Float vector) throws IOException {
        writer.beginArray();
        writeFloat(writer, vector.x());
        writeFloat(writer, vector.y());
        writer.endArray();
    }

    public static Vector3Float readVector3Float(JsonElement element) {
        JsonArray array = element.getAsJsonArray();
        return new Vector3Float(
                (float) array.get(0).getAsDouble(),
                (float) array.get(1).getAsDouble(),
                (float) array.get(2).getAsDouble()
        );
    }

    public static int parseColor(JsonElement element) {
        if (element.isJsonPrimitive() && element.getAsJsonPrimitive().isNumber()) {
            // Already a single integer
            return element.getAsInt();
        }

        if (element.isJsonArray()) {
            JsonArray array = element.getAsJsonArray();
            if (array.size() != 3) {
                throw new IllegalArgumentException("RGB array must have exactly 3 elements");
            }

            int r = (int) (clamp(array.get(0).getAsFloat()) * 255.0f);
            int g = (int) (clamp(array.get(1).getAsFloat()) * 255.0f);
            int b = (int) (clamp(array.get(2).getAsFloat()) * 255.0f);

            // Compose ARGB with full alpha (0xFF000000)
            return (0xFF << 24) | (r << 16) | (g << 8) | b;
        }

        throw new IllegalArgumentException("Invalid color format");
    }

    private static float clamp(float value) {
        return Math.max(0.0f, Math.min(1.0f, value));
    }

}