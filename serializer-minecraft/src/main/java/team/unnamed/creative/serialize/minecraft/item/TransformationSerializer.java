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
package team.unnamed.creative.serialize.minecraft.item;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonWriter;
import org.jetbrains.annotations.NotNull;
import team.unnamed.creative.base.QuaternionFloat;
import team.unnamed.creative.base.Vector3Float;
import team.unnamed.creative.item.Transformation;

import java.io.IOException;

public class TransformationSerializer {
    
    public static void writeToWriter(final @NotNull JsonWriter writer, final @NotNull Transformation transformation) throws IOException {
        writer.name("transformation");
        writer.beginObject();
        writer.name("translation").jsonValue(Vector3FloatSerializer.INSTANCE.serializeToJsonString(transformation.translation()));
        writer.name("scale").jsonValue(Vector3FloatSerializer.INSTANCE.serializeToJsonString(transformation.scale()));
        writer.name("left_rotation").jsonValue(QuaternionSerializer.INSTANCE.serializeToJsonString(transformation.leftRotation()));
        writer.name("right_rotation").jsonValue(QuaternionSerializer.INSTANCE.serializeToJsonString(transformation.rightRotation()));
        writer.endObject();
    }

    public static Transformation readFromJson(final @NotNull JsonElement node) {
        Transformation transformation = Transformation.DEFAULT;
        float x,y,z,w;

        if (!(node instanceof JsonObject object) || object.isEmpty()) return transformation;

        JsonArray array = object.getAsJsonArray("translation");
        x = array.get(0).getAsNumber().floatValue();
        y = array.get(1).getAsNumber().floatValue();
        z = array.get(2).getAsNumber().floatValue();
        transformation = transformation.translation(new Vector3Float(x, y, z));

        array = object.getAsJsonArray("scale");
        x = array.get(0).getAsNumber().floatValue();
        y = array.get(1).getAsNumber().floatValue();
        z = array.get(2).getAsNumber().floatValue();
        transformation = transformation.scale(new Vector3Float(x, y, z));

        array = object.getAsJsonArray("left_rotation");
        x = array.get(0).getAsNumber().floatValue();
        y = array.get(1).getAsNumber().floatValue();
        z = array.get(2).getAsNumber().floatValue();
        w = array.get(3).getAsNumber().floatValue();
        transformation = transformation.leftRotation(new QuaternionFloat(x, y, z, w));

        array = object.getAsJsonArray("right_rotation");
        x = array.get(0).getAsNumber().floatValue();
        y = array.get(1).getAsNumber().floatValue();
        z = array.get(2).getAsNumber().floatValue();
        w = array.get(3).getAsNumber().floatValue();
        transformation = transformation.rightRotation(new QuaternionFloat(x, y, z, w));

        return transformation;
    }
}
