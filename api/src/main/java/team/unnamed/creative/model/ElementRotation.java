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
package team.unnamed.creative.model;

import net.kyori.examination.Examinable;
import net.kyori.examination.ExaminableProperty;
import net.kyori.examination.string.StringExaminer;
import org.jetbrains.annotations.NotNull;
import team.unnamed.creative.base.Axis3D;
import team.unnamed.creative.base.Vector3Float;

import java.util.stream.Stream;

import static java.util.Objects.requireNonNull;

/**
 * Class for representing {@link Element}
 * rotations in a single axis
 *
 * @since 1.0.0
 */
public record ElementRotation(Vector3Float origin, Vector3Float rotation, boolean rescale) implements Examinable {

    public static final boolean DEFAULT_RESCALE = false;

    public ElementRotation(
            Vector3Float origin,
            Vector3Float rotation,
            boolean rescale
    ) {
        this.origin = requireNonNull(origin, "origin");
        this.rotation = requireNonNull(rotation, "rotation");
        this.rescale = rescale;
        validate();
    }

    private void validate() {
        // Skip if using 1.21.11+ format where one can rotate beyond -45->45 and support multiple angles
        if (rotation.x() != 0f && (rotation.y() != 0f || rotation.z() != 0f)) return;
        if (rotation.y() != 0f && rotation.z() != 0f) return;

        float[] absAngles = rotation.toArray(Math::abs);
        for (float absAngle : absAngles) if (absAngle > 45.0f)
            throw new IllegalArgumentException("Angle must be between [-45.0, 45.0] (inclusive), but was " + absAngle);
    }

    /**
     * Returns the rotation origin, a.k.a.
     * rotation pivot
     *
     * @return The rotation origin
     */
    public Vector3Float origin() {
        return origin;
    }

    /**
     * Returns the axis of the element
     * rotation, because elements can only
     * use rotation in a single axis
     *
     * @return The rotation axis
     */
    @Deprecated
    public Axis3D axis() {
        if (rotation.x() != 0f) return Axis3D.X;
        else if (rotation.y() != 0f) return Axis3D.Y;
        else if (rotation.z() != 0f) return Axis3D.Z;
        else return Axis3D.X;
    }

    /**
     * Returns the actual rotation angle in the
     * range of [-45.0, 45.0]
     *
     * @return The rotation angle
     */
    @Deprecated
    public float angle() {
        if (rotation.x() != 0f) return rotation.x();
        else if (rotation.y() != 0f) return rotation.y();
        else return rotation.z();
    }

    /**
     * Returns true if faces will be
     * scaled across the whole block
     */
    public boolean rescale() {
        return rescale;
    }

    public ElementRotation origin(Vector3Float origin) {
        if (this.origin == origin) return this;
        return new ElementRotation(origin, this.rotation, this.rescale);
    }

    public ElementRotation rescale(boolean rescale) {
        if (this.rescale == rescale) return this;
        return new ElementRotation(this.origin, this.rotation, rescale);
    }

    public ElementRotation rotation(Vector3Float rotation) {
        if (this.rotation == rotation) return this;
        return new ElementRotation(this.origin, rotation, this.rescale);
    }

    public @NotNull ElementRotation.Builder toBuilder() {
        return ElementRotation.builder().origin(origin).rotation(this.rotation).rescale(rescale);
    }

    @Override
    public @NotNull Stream<? extends ExaminableProperty> examinableProperties() {
        return Stream.of(
                ExaminableProperty.of("origin", origin),
                ExaminableProperty.of("rotation", rotation),
                ExaminableProperty.of("rescale", rescale)
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
        ElementRotation that = (ElementRotation) o;
        return rescale == that.rescale && rotation.equals(that.rotation) && origin.equals(that.origin);
    }

    /**
     * Creates a new {@link ElementRotation} instance
     * from the provided values
     *
     * @param origin  The rotation origin or pivot
     * @param axis    The rotation axis
     * @param angle   The rotation angle (value)
     * @param rescale Whether to rescale the faces
     *                to the whole block
     * @return A new {@link ElementRotation} instance
     * @since 1.0.0
     */
    @Deprecated
    public static ElementRotation of(
            Vector3Float origin,
            Axis3D axis,
            float angle,
            boolean rescale
    ) {
        Vector3Float rotation = Vector3Float.ZERO;
        if (axis == Axis3D.Y) rotation = new Vector3Float(0f, angle, 0f);
        else if (axis == Axis3D.X) rotation = new Vector3Float(angle, 0f, 0f);
        else if (axis == Axis3D.Z) rotation = new Vector3Float(0f, 0f, angle);
        return new ElementRotation(origin, rotation, rescale);
    }

    /**
     * Static factory method for instantiating our
     * builder implementation
     *
     * @return A new builder for {@link ElementRotation}
     * instances
     * @since 1.0.0
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Builder implementation for creating
     * {@link ElementRotation} instances
     *
     * @since 1.0.0
     */
    public static class Builder {

        private Vector3Float origin;
        private Vector3Float rotation;
        private boolean rescale = DEFAULT_RESCALE;

        private Builder() {
        }

        public Builder origin(Vector3Float origin) {
            this.origin = requireNonNull(origin, "origin");
            return this;
        }

        public Builder rotation(Vector3Float rotation) {
            this.rotation = requireNonNull(rotation, "rotation");
            return this;
        }

        public Builder rescale(boolean rescale) {
            this.rescale = rescale;
            return this;
        }

        /**
         * Finishes building the {@link ElementRotation}
         * instance, this method can be invoked multiple
         * times, the builder is re-usable
         *
         * @return The element rotation
         */
        public ElementRotation build() {
            return new ElementRotation(origin, rotation, rescale);
        }

    }

}
