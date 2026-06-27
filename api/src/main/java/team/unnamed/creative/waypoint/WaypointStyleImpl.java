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
package team.unnamed.creative.waypoint;

import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

@ApiStatus.Internal
public record WaypointStyleImpl(@NotNull Key key, int nearDistance, int farDistance,
                                @NotNull List<Key> sprites) implements WaypointStyle {

    @Override
    public @NotNull List<Key> sprites() {
        return Collections.unmodifiableList(sprites);
    }

    @Override
    public @NotNull String toString() {
        return getClass().getSimpleName() + "{" + "key=" + key + ", nearDistance=" + nearDistance + ", farDistance=" + farDistance + ", sprites=" + sprites + "}";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof WaypointStyleImpl that)) return false;
        return nearDistance == that.nearDistance
                && farDistance == that.farDistance
                && key.equals(that.key)
                && sprites.equals(that.sprites);
    }

    static final class BuilderImpl implements Builder {
        private Key key;
        private int nearDistance = WaypointStyle.DEFAULT_NEAR_DISTANCE;
        private int farDistance = WaypointStyle.DEFAULT_FAR_DISTANCE;
        private final @NotNull ArrayList<Key> sprites = new ArrayList<>();

        @Override
        public @NotNull WaypointStyle.Builder key(@NotNull Key key) {
            this.key = key;
            return this;
        }

        @Override
        public @NotNull WaypointStyle.Builder nearDistance(int nearDistance) {
            this.nearDistance = nearDistance;
            return this;
        }

        @Override
        public @NotNull WaypointStyle.Builder farDistance(int farDistance) {
            this.farDistance = farDistance;
            return this;
        }

        @Override
        public @NotNull WaypointStyle.Builder sprite(@NotNull Key sprite) {
            this.sprites.add(sprite);
            return this;
        }

        @Override
        public @NotNull WaypointStyle build() {
            if (key == null) {
                throw new IllegalStateException("Key cannot be null");
            }
            if (sprites.isEmpty()) {
                throw new IllegalStateException("Sprites cannot be empty");
            }
            if (nearDistance < 0) {
                throw new IllegalStateException("Near distance cannot be negative");
            }
            if (farDistance < 0) {
                throw new IllegalStateException("Far distance cannot be negative");
            }
            if (nearDistance > farDistance) {
                throw new IllegalStateException("Near distance cannot be greater than far distance");
            }
            return new WaypointStyleImpl(key, nearDistance, farDistance, sprites);
        }
    }
}
