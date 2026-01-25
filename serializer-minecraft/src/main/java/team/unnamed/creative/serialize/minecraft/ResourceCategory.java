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

import net.kyori.adventure.key.Key;
import net.kyori.adventure.key.Keyed;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import team.unnamed.creative.metadata.pack.PackFormat;
import team.unnamed.creative.overlay.ResourceContainer;
import team.unnamed.creative.part.ResourcePackPart;
import team.unnamed.creative.serialize.minecraft.io.ResourceDeserializer;
import team.unnamed.creative.serialize.minecraft.io.ResourceSerializer;

import java.util.Collection;
import java.util.function.Function;

/**
 * Generalization for all the kinds of resource-pack elements that
 * have keys specifying their location in the resource-pack zip,
 * for example: {@code assets/<namespace>/<category>/<key><extension>}.
 *
 * @param <T>
 */
@ApiStatus.Internal
public interface ResourceCategory<T extends Keyed & ResourcePackPart> {
    /**
     * Returns the folder name for this category,
     * based on the given pack format.
     *
     * @param packFormat The pack format
     * @return The folder name
     */
    @NotNull String folder(final PackFormat packFormat);

    /**
     * Returns the extension for this category,
     * based on the given pack format.
     *
     * @param packFormat The pack format
     * @return The extension
     */
    @NotNull String extension(final PackFormat packFormat);

    @NotNull ResourceDeserializer<T> deserializer();

    @NotNull Function<ResourceContainer, Collection<T>> lister();

    @NotNull ResourceSerializer<T> serializer();

    default @NotNull String pathOf(final @NotNull T resource, final PackFormat packFormat) {
        Key key = resource.key();
        // assets/<namespace>/<category>/<path><extension>
        return "assets/" + key.namespace() + "/" + folder(packFormat) + "/" + key.value() + extension(packFormat);
    }
}
