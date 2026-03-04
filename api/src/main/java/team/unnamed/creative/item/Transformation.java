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
package team.unnamed.creative.item;

import net.kyori.examination.Examinable;
import org.jetbrains.annotations.NotNull;
import team.unnamed.creative.base.QuaternionFloat;
import team.unnamed.creative.base.Vector3Float;

public interface Transformation extends Examinable {

    Transformation DEFAULT = TransformationImpl.DEFAULT;

    @NotNull Vector3Float translation();
    @NotNull Transformation translation(Vector3Float translation);

    @NotNull Vector3Float scale();
    @NotNull Transformation scale(Vector3Float scale);

    @NotNull QuaternionFloat leftRotation();
    @NotNull Transformation leftRotation(QuaternionFloat leftRotation);

    @NotNull QuaternionFloat rightRotation();
    @NotNull Transformation rightRotation(QuaternionFloat rightRotation);
}
