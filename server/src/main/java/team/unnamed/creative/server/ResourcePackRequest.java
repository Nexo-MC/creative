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
package team.unnamed.creative.server;

import net.kyori.examination.ExaminableProperty;
import net.kyori.examination.string.StringExaminer;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import team.unnamed.creative.server.request.ResourcePackDownloadRequest;

import java.util.UUID;
import java.util.stream.Stream;

import static java.util.Objects.requireNonNull;

/**
 * @deprecated Use {@link ResourcePackDownloadRequest} instead
 */
@Deprecated
@ApiStatus.ScheduledForRemoval(inVersion = "2.0.0")
public record ResourcePackRequest(UUID uuid, String username, String clientVersion, String clientVersionId,
                                  int packFormat) implements ResourcePackDownloadRequest {

    public ResourcePackRequest(
            UUID uuid,
            String username,
            String clientVersion,
            String clientVersionId,
            int packFormat
    ) {
        this.uuid = requireNonNull(uuid, "uuid");
        this.username = requireNonNull(username, "username");
        this.clientVersion = requireNonNull(clientVersion, "clientVersion");
        this.clientVersionId = requireNonNull(clientVersionId, "clientVersionId");
        this.packFormat = packFormat;
    }

    /**
     * Returns the uuid of the requester player,
     * provided by the {@code X-Minecraft-UUID}
     * HTTP header sent by the client
     *
     * @return The requester player UUID
     */
    @Override
    public @NotNull UUID uuid() {
        return uuid;
    }

    /**
     * Returns the requester player's username,
     * provided by the {@code X-Minecraft-Username}
     * HTTP header sent by the client
     *
     * @return The requester player username
     */
    @Override
    public @NotNull String username() {
        return username;
    }

    /**
     * Returns the requester player's client version
     * name, e.g. "1.18.1", provided by the
     * {@code X-Minecraft-Client-Version} HTTP header
     * sent by the client
     *
     * @return The requester client version name
     */
    @Override
    public @NotNull String clientVersion() {
        return clientVersion;
    }

    /**
     * Returns the requester player's client version
     * id, e.g. "1.18.1", provided by the
     * {@code X-Minecraft-Client-Version-Id} HTTP
     * header sent by the client, not to be confused
     * with {@link ResourcePackRequest#clientVersion()}
     *
     * @return The requester client version id
     */
    @Override
    public @NotNull String clientVersionId() {
        return clientVersionId;
    }

    /**
     * Returns the expected resource pack format
     * for the requester player's client, provided
     * by the {@code X-Minecraft-Pack-Format} HTTP
     * header sent by the client
     *
     * @return The expected pack format version
     */
    @Override
    public int packFormat() {
        return packFormat;
    }

    @Override
    public @NotNull Stream<? extends ExaminableProperty> examinableProperties() {
        return Stream.of(
                ExaminableProperty.of("uuid", uuid),
                ExaminableProperty.of("username", username),
                ExaminableProperty.of("clientVersion", clientVersion),
                ExaminableProperty.of("clientVersionId", clientVersionId),
                ExaminableProperty.of("packFormat", packFormat)
        );
    }

    @Override
    public @NotNull String toString() {
        return examine(StringExaminer.simpleEscaping());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ResourcePackRequest that = (ResourcePackRequest) o;
        return packFormat == that.packFormat
                && uuid.equals(that.uuid)
                && username.equals(that.username)
                && clientVersion.equals(that.clientVersion)
                && clientVersionId.equals(that.clientVersionId);
    }

}
