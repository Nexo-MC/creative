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

import com.google.gson.JsonDeserializer;
import net.kyori.adventure.key.Key;
import org.intellij.lang.annotations.Language;
import org.junit.jupiter.api.Test;
import team.unnamed.creative.item.ConditionItemModel;
import team.unnamed.creative.item.Item;
import team.unnamed.creative.item.ItemModel;
import team.unnamed.creative.item.RangeDispatchItemModel;
import team.unnamed.creative.item.ReferenceItemModel;
import team.unnamed.creative.item.SelectItemModel;
import team.unnamed.creative.item.Transformation;
import team.unnamed.creative.item.property.ItemBooleanProperty;
import team.unnamed.creative.item.property.ItemNumericProperty;
import team.unnamed.creative.serialize.minecraft.GsonUtil;

import static org.junit.jupiter.api.Assertions.*;

class ItemSerializationTest {


    @Test
    void test_transformation_deserialization() throws Exception {
        @Language("JSON") String input = """
                {
                    "type": "minecraft:model",
                    "model": "minecraft:item/air",
                    "transformation": {
                      "translation": [0,0,0],
                      "scale": [1,1,1],
                      "left_rotation": [0,0,0,1],
                      "right_rotation": [0,0,0,1]
                    }
                }""";
        ItemModel itemModel = ItemSerializer.INSTANCE.deserializeItemModel(GsonUtil.parseString(input));
        assertEquals(Transformation.DEFAULT, itemModel.transformation());

        input = """
                {
                    "type": "minecraft:condition",
                    "property": "minecraft:broken",
                    "on_true": {
                      "type": "minecraft:model",
                      "model": "minecraft:item/air",
                      "transformation": {
                        "translation": [0,0,0],
                        "scale": [1,1,1],
                        "left_rotation": [0,0,0,1],
                        "right_rotation": [0,0,0,1]
                      }
                    },
                    "on_false": {
                      "type": "minecraft:model",
                      "model": "minecraft:item/air",
                      "transformation": {
                        "translation": [0,0,0],
                        "scale": [1,1,1],
                        "left_rotation": [0,0,0,1],
                        "right_rotation": [0,0,0,1]
                      }
                    },
                    "transformation": {
                      "translation": [0,0,0],
                      "scale": [1,1,1],
                      "left_rotation": [0,0,0,1],
                      "right_rotation": [0,0,0,1]
                    }
                }""";
        itemModel = ItemSerializer.INSTANCE.deserializeItemModel(GsonUtil.parseString(input));
        assertInstanceOf(ConditionItemModel.class, itemModel);
        assertEquals(Transformation.DEFAULT, itemModel.transformation());
        assertEquals(Transformation.DEFAULT, ((ConditionItemModel) itemModel).onTrue().transformation());
        assertEquals(Transformation.DEFAULT, ((ConditionItemModel) itemModel).onFalse().transformation());


        input = """
                {
                    "type": "minecraft:select",
                    "property": "minecraft:custom_model_data",
                    "cases": [
                      {
                        "when": "test",
                        "model": {
                          "type": "minecraft:model",
                          "model": "minecraft:item/air",
                          "transformation": {
                            "translation": [0,0,0],
                            "scale": [1,1,1],
                            "left_rotation": [0,0,0,1],
                            "right_rotation": [0,0,0,1]
                          }
                        },
                        "transformation": {
                          "translation": [0,0,0],
                          "scale": [1,1,1],
                          "left_rotation": [0,0,0,1],
                          "right_rotation": [0,0,0,1]
                        }
                      }
                    ],
                    "fallback": {
                      "type": "minecraft:model",
                      "model": "minecraft:item/air",
                      "transformation": {
                        "translation": [0,0,0],
                        "scale": [1,1,1],
                        "left_rotation": [0,0,0,1],
                        "right_rotation": [0,0,0,1]
                      }
                    },
                    "transformation": {
                      "translation": [0,0,0],
                      "scale": [1,1,1],
                      "left_rotation": [0,0,0,1],
                      "right_rotation": [0,0,0,1]
                    }
                }""";

        itemModel = ItemSerializer.INSTANCE.deserializeItemModel(GsonUtil.parseString(input));
        assertInstanceOf(SelectItemModel.class, itemModel);
        assertEquals(Transformation.DEFAULT, itemModel.transformation());
        assertEquals(Transformation.DEFAULT, ((SelectItemModel) itemModel).fallback().transformation());
        assertEquals(Transformation.DEFAULT, ((SelectItemModel) itemModel).cases().getFirst().transformation());
        assertEquals(Transformation.DEFAULT, ((SelectItemModel) itemModel).cases().getFirst().model().transformation());
    }


    @Test
    void test_air_deserialization() throws Exception {
        final @Language("JSON") String input = """
                {
                  "model": {
                    "type": "minecraft:model",
                    "model": "minecraft:item/air"
                  }
                }""";

        final Item item = ItemSerializer.INSTANCE.deserializeFromJsonString(input, Key.key("minecraft", "air"));

        assertEquals(Key.key("minecraft", "air"), item.key());
        assertEquals(Item.DEFAULT_HAND_ANIMATION_ON_SWAP, item.handAnimationOnSwap());
        assertEquals(Item.DEFAULT_OVERSIZED_IN_GUI, item.oversizedInGui());

        final ItemModel model = item.model();
        assertInstanceOf(ReferenceItemModel.class, model);

        final ReferenceItemModel reference = (ReferenceItemModel) model;
        assertEquals(Key.key("minecraft:item/air"), reference.model());
        assertEquals(0, reference.tints().size());
    }

    @Test
    void test_air_serialization() throws Exception {
        final Item item = Item.item(Key.key("minecraft", "air"), ItemModel.reference(Key.key("minecraft:item/air")), false, true);

        final String expected = "{\"model\":{\"type\":\"model\",\"model\":\"item/air\",\"tints\":[]},\"hand_animation_on_swap\":false,\"oversized_in_gui\":true}";

        assertEquals(
                expected,
                ItemSerializer.INSTANCE.serializeToJsonString(item)
        );
    }

    @Test
    void test_bow_deserialization() throws Exception {
        final @Language("JSON") String input = """
                {
                  "hand_animation_on_swap": false,
                  "oversized_in_gui": true,
                  "model": {
                    "type": "minecraft:condition",
                    "on_false": {
                      "type": "minecraft:model",
                      "model": "minecraft:item/bow"
                    },
                    "on_true": {
                      "type": "minecraft:range_dispatch",
                      "entries": [
                        {
                          "model": {
                            "type": "minecraft:model",
                            "model": "minecraft:item/bow_pulling_1"
                          },
                          "threshold": 0.65
                        },
                        {
                          "model": {
                            "type": "minecraft:model",
                            "model": "minecraft:item/bow_pulling_2"
                          },
                          "threshold": 0.9
                        }
                      ],
                      "fallback": {
                        "type": "minecraft:model",
                        "model": "minecraft:item/bow_pulling_0"
                      },
                      "property": "minecraft:use_duration",
                      "scale": 0.05
                    },
                    "property": "minecraft:using_item"
                  }
                }""";
        final Item item = ItemSerializer.INSTANCE.deserializeFromJsonString(input, Key.key("minecraft", "bow"));

        assertEquals(Key.key("minecraft", "bow"), item.key());
        assertFalse(item.handAnimationOnSwap());
        assertTrue(item.oversizedInGui());

        final ItemModel model = item.model();
        assertInstanceOf(ConditionItemModel.class, model);

        final ConditionItemModel condition = (ConditionItemModel) model;
        final ItemBooleanProperty property = condition.condition();

        assertEquals(ItemBooleanProperty.usingItem(), property);

        final ItemModel onFalse = condition.onFalse();
        assertInstanceOf(ReferenceItemModel.class, onFalse);

        final ReferenceItemModel reference = (ReferenceItemModel) onFalse;
        assertEquals(Key.key("minecraft:item/bow"), reference.model());
        assertEquals(0, reference.tints().size());

        final ItemModel onTrue = condition.onTrue();
        assertInstanceOf(RangeDispatchItemModel.class, onTrue);

        final RangeDispatchItemModel rangeDispatch = (RangeDispatchItemModel) onTrue;
        assertEquals(ItemNumericProperty.useDuration(), rangeDispatch.property());
        assertEquals(0.05f, rangeDispatch.scale());

        final ItemModel fallback = rangeDispatch.fallback();
        assertInstanceOf(ReferenceItemModel.class, fallback);

        final ReferenceItemModel fallbackReference = (ReferenceItemModel) fallback;
        assertEquals(Key.key("minecraft:item/bow_pulling_0"), fallbackReference.model());
        assertEquals(0, fallbackReference.tints().size());

        assertEquals(2, rangeDispatch.entries().size());

        final RangeDispatchItemModel.Entry entry1 = rangeDispatch.entries().getFirst();
        assertEquals(0.65f, entry1.threshold());

        final ItemModel model1 = entry1.model();
        assertInstanceOf(ReferenceItemModel.class, model1);

        final ReferenceItemModel reference1 = (ReferenceItemModel) model1;
        assertEquals(Key.key("minecraft:item/bow_pulling_1"), reference1.model());
        assertEquals(0, reference1.tints().size());

        final RangeDispatchItemModel.Entry entry2 = rangeDispatch.entries().get(1);
        assertEquals(0.9f, entry2.threshold());

        final ItemModel model2 = entry2.model();
        assertInstanceOf(ReferenceItemModel.class, model2);

        final ReferenceItemModel reference2 = (ReferenceItemModel) model2;
        assertEquals(Key.key("minecraft:item/bow_pulling_2"), reference2.model());
        assertEquals(0, reference2.tints().size());
    }


}
