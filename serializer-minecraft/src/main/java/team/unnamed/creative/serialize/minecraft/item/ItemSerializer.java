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
import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;
import team.unnamed.creative.base.CubeFace;
import team.unnamed.creative.base.DyeColor;
import team.unnamed.creative.base.HeadType;
import team.unnamed.creative.base.Pose;
import team.unnamed.creative.base.WoodType;
import team.unnamed.creative.item.*;
import team.unnamed.creative.item.property.*;
import team.unnamed.creative.item.special.BannerSpecialRender;
import team.unnamed.creative.item.special.BedSpecialRender;
import team.unnamed.creative.item.special.ChestSpecialRender;
import team.unnamed.creative.item.special.CopperGolemStatueSpecialRender;
import team.unnamed.creative.item.special.HeadSpecialRender;
import team.unnamed.creative.item.special.NoFieldSpecialRender;
import team.unnamed.creative.item.special.ShulkerBoxSpecialRender;
import team.unnamed.creative.item.special.SignSpecialRender;
import team.unnamed.creative.item.special.SpecialRender;
import team.unnamed.creative.item.tint.ConstantTintSource;
import team.unnamed.creative.item.tint.CustomModelDataTintSource;
import team.unnamed.creative.item.tint.GrassTintSource;
import team.unnamed.creative.item.tint.KeyedAndBackedTintSource;
import team.unnamed.creative.item.tint.TintSource;
import team.unnamed.creative.overlay.ResourceContainer;
import team.unnamed.creative.serialize.minecraft.GsonUtil;
import team.unnamed.creative.serialize.minecraft.ResourceCategoryImpl;
import team.unnamed.creative.serialize.minecraft.base.KeySerializer;
import team.unnamed.creative.serialize.minecraft.io.JsonResourceDeserializer;
import team.unnamed.creative.serialize.minecraft.io.JsonResourceSerializer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class ItemSerializer implements JsonResourceSerializer<Item>, JsonResourceDeserializer<Item> {
    public static final ItemSerializer INSTANCE;
    public static final ResourceCategoryImpl<Item> CATEGORY;

    static {
        INSTANCE = new ItemSerializer();
        CATEGORY = new ResourceCategoryImpl<>(
                "items",
                ".json",
                ResourceContainer::items,
                INSTANCE
        );
    }

    private ItemSerializer() {
    }

    private void serializeItemModel(ItemModel model, JsonWriter writer, int targetPackFormat) throws IOException {
        writer.beginObject();
        switch (model) {
            case EmptyItemModel ignored -> writer.name("type").value("empty");
            case ReferenceItemModel referenceItemModel -> writeReference(writer, referenceItemModel);
            case SpecialItemModel specialItemModel -> writeSpecial(writer, specialItemModel);
            case CompositeItemModel compositeItemModel -> writeComposite(writer, compositeItemModel, targetPackFormat);
            case ConditionItemModel conditionItemModel -> writeCondition(writer, conditionItemModel, targetPackFormat);
            case SelectItemModel selectItemModel -> writeSelect(writer, selectItemModel, targetPackFormat);
            case RangeDispatchItemModel rangeDispatchItemModel ->
                    writeRangeDispatch(writer, rangeDispatchItemModel, targetPackFormat);
            case BundleSelectedItemModel ignored -> writer.name("type").value("bundle/selected_item");
            default -> throw new IllegalArgumentException("Unknown item model type: " + model.getClass());
        }
        writer.endObject();
    }

    private @NotNull ItemModel deserializeItemModel(JsonElement unknownNode) throws IOException {
        final JsonObject node = unknownNode.getAsJsonObject();
        final Key type = Key.key(node.get("type").getAsString());
        if (!type.namespace().equals(Key.MINECRAFT_NAMESPACE)) {
            throw new IllegalArgumentException("Unknown item model type: " + type);
        }
        return switch (type.value()) {
            case "empty" -> ItemModel.empty();
            case "model" -> readReference(node);
            case "special" -> readSpecial(node);
            case "composite" -> readComposite(node);
            case "condition" -> readCondition(node);
            case "select" -> readSelect(node);
            case "range_dispatch" -> readRangeDispatch(node);
            case "bundle/selected_item" -> ItemModel.bundleSelectedItem();
            default -> throw new IllegalArgumentException("Unknown item model type: " + type);
        };
    }

    @Override
    public void serializeToJson(Item item, JsonWriter writer, int targetPackFormat) throws IOException {
        writer.beginObject();
        writer.name("model");
        serializeItemModel(item.model(), writer, targetPackFormat);

        boolean handAnimationOnSwap = item.handAnimationOnSwap();
        if (handAnimationOnSwap != Item.DEFAULT_HAND_ANIMATION_ON_SWAP) {
            writer.name("hand_animation_on_swap").value(handAnimationOnSwap);
        }

        boolean oversizedInGui = item.oversizedInGui();
        if (oversizedInGui != Item.DEFAULT_OVERSIZED_IN_GUI) {
            writer.name("oversized_in_gui").value(oversizedInGui);
        }

        float swapAnimationScale = item.swapAnimationScale();
        if (swapAnimationScale != Item.DEFAULT_SWAP_ANIMATION_SCALE) {
            writer.name("swap_animation_scale").value(swapAnimationScale);
        }

        writer.endObject();
    }

    @Override
    public Item deserializeFromJson(JsonElement node, Key key) throws IOException {
        JsonObject jsonObject = node.getAsJsonObject();
        ItemModel model = deserializeItemModel(jsonObject.get("model"));
        boolean handAnimationOnSwap = jsonObject.has("hand_animation_on_swap")
                ? jsonObject.get("hand_animation_on_swap").getAsBoolean()
                : Item.DEFAULT_HAND_ANIMATION_ON_SWAP;
        boolean oversizedInGui = jsonObject.has("oversized_in_gui")
                ? jsonObject.get("oversized_in_gui").getAsBoolean()
                : Item.DEFAULT_OVERSIZED_IN_GUI;
        float swapAnimationScale = jsonObject.has("swap_animation_scale")
                ? jsonObject.get("swap_animation_scale").getAsFloat()
                : Item.DEFAULT_SWAP_ANIMATION_SCALE;
        return Item.item(key, model, handAnimationOnSwap, oversizedInGui, swapAnimationScale);
    }

    private void writeReference(final @NotNull JsonWriter writer, final @NotNull ReferenceItemModel model) throws IOException {
        writer.name("type").value("model");
        writer.name("model").value(KeySerializer.toString(model.model()));
        writer.name("tints").beginArray();
        for (final TintSource tint : model.tints()) {
            writer.beginObject();
            switch (tint) {
                case ConstantTintSource constantTintSource -> {
                    writer.name("type").value("constant");
                    writer.name("value").value(constantTintSource.tint());
                }
                case CustomModelDataTintSource customModelDataTintSource -> {
                    writer.name("type").value("custom_model_data");

                    int index = customModelDataTintSource.index();
                    if (index != CustomModelDataTintSource.DEFAULT_INDEX) {
                        writer.name("index").value(index);
                    }

                    writer.name("default").value(customModelDataTintSource.defaultTint());
                }
                case GrassTintSource grassTintSource -> {
                    writer.name("type").value("grass");
                    writer.name("temperature").value(grassTintSource.temperature());
                    writer.name("downfall").value(grassTintSource.downfall());
                }
                case KeyedAndBackedTintSource keyedAndBackedTintSource -> {
                    writer.name("type").value(KeySerializer.toString(keyedAndBackedTintSource.key()));
                    writer.name("default").value(keyedAndBackedTintSource.defaultTint());
                }
                default -> throw new IllegalArgumentException("Unknown tint source type: " + tint.getClass());
            }
            writer.endObject();
        }
        writer.endArray();
    }

    private @NotNull ReferenceItemModel readReference(final @NotNull JsonObject node) {
        final Key model = Key.key(node.get("model").getAsString());
        final List<TintSource> tints = new ArrayList<>();

        if (node.has("tints")) for (JsonElement tintElement : node.getAsJsonArray("tints")) {
            final JsonObject tintObject = tintElement.getAsJsonObject();
            final Key type = Key.key(tintObject.get("type").getAsString());
            if (!type.namespace().equals(Key.MINECRAFT_NAMESPACE)) {
                throw new IllegalArgumentException("Unknown tint source type: " + type);
            }
            switch (type.value()) {
                case "constant":
                    tints.add(TintSource.constant(tintObject.get("value").getAsInt()));
                    break;
                case "custom_model_data":
                    int index = tintObject.has("index")
                            ? tintObject.get("index").getAsInt()
                            : CustomModelDataTintSource.DEFAULT_INDEX;
                    int defaultTint = tintObject.get("default").getAsInt();
                    tints.add(TintSource.customModelData(index, defaultTint));
                    break;
                case "grass":
                    float temperature = tintObject.get("temperature").getAsFloat();
                    float downfall = tintObject.get("downfall").getAsFloat();
                    tints.add(TintSource.grass(temperature, downfall));
                    break;
                case "dye":
                    tints.add(TintSource.dye(GsonUtil.parseColor(tintObject.get("default"))));
                    break;
                case "firework":
                    tints.add(TintSource.firework(GsonUtil.parseColor(tintObject.get("default"))));
                    break;
                case "map_color":
                    tints.add(TintSource.mapColor(GsonUtil.parseColor(tintObject.get("default"))));
                    break;
                case "potion":
                    tints.add(TintSource.potion(GsonUtil.parseColor(tintObject.get("default"))));
                    break;
                default:
                    throw new IllegalArgumentException("Unknown tint source type: " + type);
            }
        }
        return ItemModel.reference(model, tints);
    }

    private void writeSpecial(final @NotNull JsonWriter writer, final @NotNull SpecialItemModel model) throws IOException {
        writer.name("type").value("special");
        writer.name("model").beginObject();
        final SpecialRender render = model.render();
        switch (render) {
            case BannerSpecialRender bannerRender -> {
                writer.name("type").value("banner");
                writer.name("color").value(bannerRender.color().name().toLowerCase(Locale.ENGLISH));
            }
            case BedSpecialRender bedRender -> {
                writer.name("type").value("bed");
                writer.name("texture").value(KeySerializer.toString(bedRender.texture()));
            }
            case ChestSpecialRender chestRender -> {
                writer.name("type").value("chest");
                writer.name("texture").value(KeySerializer.toString(chestRender.texture()));
                final float openness = chestRender.openness();
                if (openness != ChestSpecialRender.DEFAULT_OPENNESS) {
                    writer.name("openness").value(openness);
                }
            }
            case SignSpecialRender signRender -> { // covers both hanging & standing sign types
                writer.name("type").value(signRender.hanging() ? "hanging_sign" : "standing_sign");
                writer.name("wood_type").value(signRender.woodType().name().toLowerCase(Locale.ENGLISH));
                final Key texture = signRender.texture();
                if (texture != null) {
                    writer.name("texture").value(KeySerializer.toString(texture));
                }
            }
            case HeadSpecialRender headRender -> {
                writer.name("type").value("head");
                writer.name("kind").value(headRender.kind().name().toLowerCase(Locale.ENGLISH));

                final Key texture = headRender.texture();
                if (texture != null) {
                    writer.name("texture").value(KeySerializer.toString(texture));
                }

                final float animation = headRender.animation();
                if (animation != HeadSpecialRender.DEFAULT_ANIMATION) {
                    writer.name("animation").value(animation);
                }
            }
            case ShulkerBoxSpecialRender shulkerBoxRender -> {
                writer.name("type").value("shulker_box");
                writer.name("texture").value(KeySerializer.toString(shulkerBoxRender.texture()));

                final float openness = shulkerBoxRender.openness();
                if (openness != ShulkerBoxSpecialRender.DEFAULT_OPENNESS) {
                    writer.name("openness").value(openness);
                }

                final CubeFace orientation = shulkerBoxRender.orientation();
                if (orientation != ShulkerBoxSpecialRender.DEFAULT_ORIENTATION) {
                    writer.name("orientation").value(orientation.name().toLowerCase(Locale.ENGLISH));
                }
            }
            case CopperGolemStatueSpecialRender copperGolemStatueRender -> {
                writer.name("type").value("copper_golem_statue");
                writer.name("texture").value(KeySerializer.toString(copperGolemStatueRender.texture()));
                writer.name("pose").value(copperGolemStatueRender.pose().name().toLowerCase(Locale.ENGLISH));
            }
            case NoFieldSpecialRender noFieldRender ->
                    writer.name("type").value(KeySerializer.toString(noFieldRender.key()));
            default -> throw new IllegalArgumentException("Unknown special render type: " + render.getClass());
        }
        writer.endObject();
        writer.name("base").value(KeySerializer.toString(model.base()));
    }

    private @NotNull SpecialItemModel readSpecial(final @NotNull JsonObject node) {
        final JsonObject modelNode = node.getAsJsonObject("model");
        final SpecialRender render;
        final Key type = Key.key(modelNode.get("type").getAsString());
        if (!type.namespace().equals(Key.MINECRAFT_NAMESPACE)) {
            throw new IllegalArgumentException("Unknown special render type: " + type);
        }
        switch (type.value()) {
            case "banner":
                render = SpecialRender.banner(DyeColor.valueOf(modelNode.get("color").getAsString().toUpperCase(Locale.ENGLISH)));
                break;
            case "bed":
                render = SpecialRender.bed(Key.key(modelNode.get("texture").getAsString()));
                break;
            case "chest":
                final Key chestTexture = Key.key(modelNode.get("texture").getAsString());
                final float openness = modelNode.has("openness")
                        ? modelNode.get("openness").getAsFloat()
                        : ChestSpecialRender.DEFAULT_OPENNESS;
                render = SpecialRender.chest(chestTexture, openness);
                break;
            case "hanging_sign":
            case "standing_sign":
                final boolean hanging = type.asMinimalString().equals("hanging_sign");
                final WoodType woodType = WoodType.valueOf(modelNode.get("wood_type").getAsString().toUpperCase(Locale.ENGLISH));
                final Key signTexture = modelNode.has("texture")
                        ? Key.key(modelNode.get("texture").getAsString())
                        : null;
                render = hanging ? SpecialRender.hangingSign(woodType, signTexture) : SpecialRender.standingSign(woodType, signTexture);
                break;
            case "head":
                final HeadType kind = HeadType.valueOf(modelNode.get("kind").getAsString().toUpperCase(Locale.ENGLISH));
                final Key headTexture = modelNode.has("texture")
                        ? Key.key(modelNode.get("texture").getAsString())
                        : null;
                final float animation = modelNode.has("animation")
                        ? modelNode.get("animation").getAsFloat()
                        : HeadSpecialRender.DEFAULT_ANIMATION;
                render = SpecialRender.head(kind, headTexture, animation);
                break;
            case "player_head":
                render = SpecialRender.playerHead();
                break;
            case "shulker_box":
                final Key shulkerBoxTexture = Key.key(modelNode.get("texture").getAsString());
                final float shulkerBoxOpenness = modelNode.has("openness")
                        ? modelNode.get("openness").getAsFloat()
                        : ShulkerBoxSpecialRender.DEFAULT_OPENNESS;
                final CubeFace orientation = modelNode.has("orientation")
                        ? CubeFace.valueOf(modelNode.get("orientation").getAsString().toUpperCase(Locale.ENGLISH))
                        : ShulkerBoxSpecialRender.DEFAULT_ORIENTATION;
                render = SpecialRender.shulkerBox(shulkerBoxTexture, shulkerBoxOpenness, orientation);
                break;
            case "conduit":
                render = SpecialRender.conduit();
                break;
            case "decorated_pot":
                render = SpecialRender.decoratedPot();
                break;
            case "shield":
                render = SpecialRender.shield();
                break;
            case "trident":
                render = SpecialRender.trident();
                break;
            case "copper_golem_statue":
                final Pose pose = Pose.valueOf(modelNode.get("pose").getAsString().toUpperCase(Locale.ENGLISH));
                final Key texture = Key.key(modelNode.get("texture").getAsString());
                render = SpecialRender.copperGolemStatue(pose, texture);
                break;
            default:
                throw new IllegalArgumentException("Unknown special render type: " + type);
        }
        return ItemModel.special(render, Key.key(node.get("base").getAsString()));
    }

    private void writeComposite(final @NotNull JsonWriter writer, final @NotNull CompositeItemModel model, final int targetPackFormat) throws IOException {
        writer.name("type").value("composite");
        writer.name("models").beginArray();
        for (ItemModel child : model.models()) {
            serializeItemModel(child, writer, targetPackFormat);
        }
        writer.endArray();
    }

    private @NotNull CompositeItemModel readComposite(final @NotNull JsonObject node) throws IOException {
        final List<ItemModel> models = new ArrayList<>();
        for (JsonElement childElement : node.getAsJsonArray("models")) {
            models.add(deserializeItemModel(childElement));
        }
        return ItemModel.composite(models);
    }

    private void writeCondition(final @NotNull JsonWriter writer, final @NotNull ConditionItemModel model, final int targetPackFormat) throws IOException {
        writer.name("type").value("condition");
        final ItemBooleanProperty condition = model.condition();
        switch (condition) {
            case CustomModelDataItemBooleanProperty customModelDataItemBooleanProperty -> {
                writer.name("property").value("custom_model_data");
                final int index = customModelDataItemBooleanProperty.index();
                if (index != CustomModelDataItemBooleanProperty.DEFAULT_INDEX) {
                    writer.name("index").value(index);
                }
            }
            case HasComponentItemBooleanProperty hasComponent -> {
                writer.name("property").value("has_component");
                writer.name("component").value(hasComponent.component());

                final boolean ignoreDefault = hasComponent.ignoreDefault();
                if (ignoreDefault != HasComponentItemBooleanProperty.DEFAULT_IGNORE_DEFAULT) {
                    writer.name("ignore_default").value(ignoreDefault);
                }
            }
            case KeybindDownItemBooleanProperty keybindDownItemBooleanProperty -> {
                writer.name("property").value("keybind_down");
                writer.name("keybind").value(keybindDownItemBooleanProperty.key());
            }
            case ComponentItemBooleanProperty component -> {
                writer.name("property").value("component");
                writer.name("predicate").value(component.predicate());
                writer.name("value").jsonValue(component.value().toString());
            }
            case NoFieldItemBooleanProperty noFieldItemBooleanProperty ->
                    writer.name("property").value(KeySerializer.toString(noFieldItemBooleanProperty.key()));
            default -> throw new IllegalArgumentException("Unknown condition type: " + condition.getClass());
        }
        writer.name("on_true");
        serializeItemModel(model.onTrue(), writer, targetPackFormat);
        writer.name("on_false");
        serializeItemModel(model.onFalse(), writer, targetPackFormat);
    }

    private @NotNull ConditionItemModel readCondition(final @NotNull JsonObject node) throws IOException {
        final ItemBooleanProperty condition;
        final Key property = Key.key(node.get("property").getAsString());
        if (!property.namespace().equals(Key.MINECRAFT_NAMESPACE)) {
            throw new IllegalArgumentException("Unknown condition property: " + property);
        }
        switch (property.value()) {
            case "custom_model_data":
                final int index = node.has("index")
                        ? node.get("index").getAsInt()
                        : CustomModelDataItemBooleanProperty.DEFAULT_INDEX;
                condition = ItemBooleanProperty.customModelData(index);
                break;
            case "has_component":
                final String component = node.get("component").getAsString();
                final boolean ignoreDefault = node.has("ignore_default")
                        ? node.get("ignore_default").getAsBoolean()
                        : HasComponentItemBooleanProperty.DEFAULT_IGNORE_DEFAULT;
                condition = ItemBooleanProperty.hasComponent(component, ignoreDefault);
                break;
            case "keybind_down":
                condition = ItemBooleanProperty.keybindDown(node.get("keybind").getAsString());
                break;
            case "broken":
                condition = ItemBooleanProperty.broken();
                break;
            case "bundle/has_selected_item":
                condition = ItemBooleanProperty.bundleHasSelectedItem();
                break;
            case "carried":
                condition = ItemBooleanProperty.carried();
                break;
            case "damaged":
                condition = ItemBooleanProperty.damaged();
                break;
            case "extended_view":
                condition = ItemBooleanProperty.extendedView();
                break;
            case "fishing_rod/cast":
                condition = ItemBooleanProperty.fishingRodCast();
                break;
            case "selected":
                condition = ItemBooleanProperty.selected();
                break;
            case "using_item":
                condition = ItemBooleanProperty.usingItem();
                break;
            case "view_entity":
                condition = ItemBooleanProperty.viewEntity();
                break;
            case "component":
                final String predicate = node.get("predicate").getAsString();
                final JsonElement value = node.get("value");
                condition = ItemBooleanProperty.component(predicate, value);
                break;
            default:
                throw new IllegalArgumentException("Unknown condition property: " + property);
        }
        return ItemModel.conditional(
                condition,
                deserializeItemModel(node.get("on_true")),
                deserializeItemModel(node.get("on_false"))
        );
    }

    private void writeSelect(final @NotNull JsonWriter writer, final @NotNull SelectItemModel model, final int targetPackFormat) throws IOException {
        writer.name("type").value("select");

        final ItemStringProperty property = model.property();
        switch (property) {
            case BlockStateItemStringProperty blockStateItemStringProperty -> {
                writer.name("property").value("block_state");
                writer.name("block_state_property").value(blockStateItemStringProperty.property());
            }
            case CustomModelDataItemStringProperty customModelDataItemStringProperty -> {
                writer.name("property").value("custom_model_data");
                final int index = customModelDataItemStringProperty.index();
                if (index != CustomModelDataItemStringProperty.DEFAULT_INDEX) {
                    writer.name("index").value(index);
                }
            }
            case LocalTimeItemStringProperty localTimeProperty -> {
                writer.name("property").value("local_time");
                writer.name("pattern").value(localTimeProperty.pattern());

                final String locale = localTimeProperty.locale();
                if (!locale.equals(LocalTimeItemStringProperty.DEFAULT_LOCALE)) {
                    writer.name("locale").value(locale);
                }

                final String timezone = localTimeProperty.timezone();
                if (timezone != null) {
                    writer.name("timezone").value(timezone);
                }
            }
            case ComponentItemStringProperty component -> writer.name("property").value("component").name("component").value(component.component());
            case NoFieldItemStringProperty noFieldItemStringProperty ->
                    writer.name("property").value(KeySerializer.toString(noFieldItemStringProperty.key()));
            default -> throw new IllegalArgumentException("Unknown select property type: " + property.getClass());
        }

        writer.name("cases").beginArray();
        for (final SelectItemModel.Case _case : model.cases()) {
            writer.beginObject();
            writer.name("when");
            final List<JsonElement> when = _case.when();
            if (when.size() == 1 && when.getFirst().isJsonPrimitive()) {
                writer.jsonValue(when.getFirst().toString());
            } else {
                writer.beginArray();
                for (JsonElement value : when) {
                    writer.jsonValue(value.toString());
                }
                writer.endArray();
            }
            writer.name("model");
            serializeItemModel(_case.model(), writer, targetPackFormat);
            writer.endObject();
        }
        writer.endArray();

        final ItemModel fallback = model.fallback();
        if (fallback != null) {
            writer.name("fallback");
            serializeItemModel(fallback, writer, targetPackFormat);
        }
    }

    private @NotNull SelectItemModel readSelect(final @NotNull JsonObject node) throws IOException {
        final ItemStringProperty property;
        final Key propertyType = Key.key(node.get("property").getAsString());
        if (!propertyType.namespace().equals(Key.MINECRAFT_NAMESPACE)) {
            throw new IllegalArgumentException("Unknown select property type: " + propertyType);
        }
        switch (propertyType.value()) {
            case "block_state":
                property = ItemStringProperty.blockState(node.get("block_state_property").getAsString());
                break;
            case "custom_model_data":
                final int index = node.has("index")
                        ? node.get("index").getAsInt()
                        : CustomModelDataItemStringProperty.DEFAULT_INDEX;
                property = ItemStringProperty.customModelData(index);
                break;
            case "local_time":
                final String pattern = node.get("pattern").getAsString();
                final String locale = node.has("locale")
                        ? node.get("locale").getAsString()
                        : LocalTimeItemStringProperty.DEFAULT_LOCALE;
                final String timezone = node.has("timezone")
                        ? node.get("timezone").getAsString()
                        : null;
                property = ItemStringProperty.localTime(locale, timezone, pattern);
                break;
            case "charge_type":
                property = ItemStringProperty.chargeType();
                break;
            case "context_dimension":
                property = ItemStringProperty.contextDimension();
                break;
            case "context_entity_type":
                property = ItemStringProperty.contextEntityType();
                break;
            case "display_context":
                property = ItemStringProperty.displayContext();
                break;
            case "main_hand":
                property = ItemStringProperty.mainHand();
                break;
            case "trim_material":
                property = ItemStringProperty.trimMaterial();
                break;
            case "component":
                property = ItemStringProperty.component(node.get("component").getAsString());
                break;
            default:
                throw new IllegalArgumentException("Unknown select property type: " + propertyType);
        }

        final List<SelectItemModel.Case> cases = new ArrayList<>();
        for (JsonElement caseElement : node.getAsJsonArray("cases")) {
            final JsonObject caseObject = caseElement.getAsJsonObject();
            final List<JsonElement> when = new ArrayList<>();

            JsonElement whenNode = caseObject.get("when");
            if (whenNode instanceof JsonArray whenArray) for (JsonElement whenElement : whenArray) {
                when.add(whenElement);
            } else {
                when.add(whenNode);
            }
            cases.add(SelectItemModel.Case._case(deserializeItemModel(caseObject.get("model")), when));
        }

        final ItemModel fallback = node.has("fallback")
                ? deserializeItemModel(node.get("fallback"))
                : null;

        return ItemModel.select(property, cases, fallback);
    }

    private void writeRangeDispatch(final @NotNull JsonWriter writer, final @NotNull RangeDispatchItemModel model, final int targetPackFormat) throws IOException {
        writer.name("type").value("range_dispatch");

        final ItemNumericProperty property = model.property();
        switch (property) {
            case CompassItemNumericProperty compassProperty -> {
                writer.name("property").value("compass");
                writer.name("target").value(compassProperty.target().name().toLowerCase(Locale.ENGLISH));

                final boolean wobble = compassProperty.wobble();
                if (wobble != CompassItemNumericProperty.DEFAULT_WOBBLE) {
                    writer.name("wobble").value(wobble);
                }
            }
            case CountItemNumericProperty countItemNumericProperty -> {
                writer.name("property").value("count");
                final boolean normalize = countItemNumericProperty.normalize();
                if (normalize != CountItemNumericProperty.DEFAULT_NORMALIZE) {
                    writer.name("normalize").value(normalize);
                }
            }
            case CustomModelDataItemNumericProperty customModelDataItemNumericProperty -> {
                writer.name("property").value("custom_model_data");
                final int index = customModelDataItemNumericProperty.index();
                if (index != CustomModelDataItemNumericProperty.DEFAULT_INDEX) {
                    writer.name("index").value(index);
                }
            }
            case DamageItemNumericProperty damageItemNumericProperty -> {
                writer.name("property").value("damage");
                final boolean normalize = damageItemNumericProperty.normalize();
                if (normalize != DamageItemNumericProperty.DEFAULT_NORMALIZE) {
                    writer.name("normalize").value(normalize);
                }
            }
            case TimeItemNumericProperty timeProperty -> {
                writer.name("property").value("time");
                final boolean wobble = timeProperty.wobble();
                if (wobble != TimeItemNumericProperty.DEFAULT_WOBBLE) {
                    writer.name("wobble").value(wobble);
                }
                writer.name("source").value(timeProperty.source().name().toLowerCase(Locale.ENGLISH));
            }
            case UseCycleItemNumericProperty useCycleItemNumericProperty -> {
                writer.name("property").value("use_cycle");
                final float period = useCycleItemNumericProperty.period();
                if (period != UseCycleItemNumericProperty.DEFAULT_PERIOD) {
                    writer.name("period").value(period);
                }
            }
            case UseDurationItemNumericProperty useDurationItemNumericProperty -> {
                writer.name("property").value("use_duration");
                final boolean remaining = useDurationItemNumericProperty.remaining();
                if (remaining != UseDurationItemNumericProperty.DEFAULT_REMAINING) {
                    writer.name("remaining").value(remaining);
                }
            }
            case NoFieldItemNumericProperty noFieldItemNumericProperty ->
                    writer.name("property").value(KeySerializer.toString(noFieldItemNumericProperty.key()));
            default ->
                    throw new IllegalArgumentException("Unknown range dispatch property type: " + property.getClass());
        }

        final float scale = model.scale();
        if (scale != RangeDispatchItemModel.DEFAULT_SCALE) {
            writer.name("scale").value(scale);
        }

        writer.name("entries").beginArray();
        for (final RangeDispatchItemModel.Entry entry : model.entries()) {
            writer.beginObject();
            writer.name("threshold").value(entry.threshold());
            writer.name("model");
            serializeItemModel(entry.model(), writer, targetPackFormat);
            writer.endObject();
        }
        writer.endArray();

        final ItemModel fallback = model.fallback();
        if (fallback != null) {
            writer.name("fallback");
            serializeItemModel(fallback, writer, targetPackFormat);
        }
    }

    private @NotNull RangeDispatchItemModel readRangeDispatch(final @NotNull JsonObject node) throws IOException {
        final ItemNumericProperty property;
        final Key propertyType = Key.key(node.get("property").getAsString());
        if (!propertyType.namespace().equals(Key.MINECRAFT_NAMESPACE)) {
            throw new IllegalArgumentException("Unknown range dispatch property type: " + propertyType);
        }
        switch (propertyType.value()) {
            case "compass":
                final CompassItemNumericProperty.Target target = CompassItemNumericProperty.Target.valueOf(node.get("target").getAsString().toUpperCase(Locale.ENGLISH));
                final boolean wobble = node.has("wobble")
                        ? node.get("wobble").getAsBoolean()
                        : CompassItemNumericProperty.DEFAULT_WOBBLE;
                property = ItemNumericProperty.compass(target, wobble);
                break;
            case "count":
                final boolean normalize = node.has("normalize")
                        ? node.get("normalize").getAsBoolean()
                        : CountItemNumericProperty.DEFAULT_NORMALIZE;
                property = ItemNumericProperty.count(normalize);
                break;
            case "custom_model_data":
                final int index = node.has("index")
                        ? node.get("index").getAsInt()
                        : CustomModelDataItemNumericProperty.DEFAULT_INDEX;
                property = ItemNumericProperty.customModelData(index);
                break;
            case "damage":
                final boolean damageNormalize = node.has("normalize")
                        ? node.get("normalize").getAsBoolean()
                        : DamageItemNumericProperty.DEFAULT_NORMALIZE;
                property = ItemNumericProperty.damage(damageNormalize);
                break;
            case "time":
                final boolean timeWobble = node.has("wobble")
                        ? node.get("wobble").getAsBoolean()
                        : TimeItemNumericProperty.DEFAULT_WOBBLE;
                final TimeItemNumericProperty.Source source = TimeItemNumericProperty.Source.valueOf(node.get("source").getAsString().toUpperCase(Locale.ENGLISH));
                property = ItemNumericProperty.time(timeWobble, source);
                break;
            case "use_cycle":
                final float period = node.has("period")
                        ? node.get("period").getAsFloat()
                        : UseCycleItemNumericProperty.DEFAULT_PERIOD;
                property = ItemNumericProperty.useCycle(period);
                break;
            case "use_duration":
                final boolean remaining = node.has("remaining")
                        ? node.get("remaining").getAsBoolean()
                        : UseDurationItemNumericProperty.DEFAULT_REMAINING;
                property = ItemNumericProperty.useDuration(remaining);
                break;
            case "bundle/fullness":
                property = ItemNumericProperty.bundleFullness();
                break;
            case "cooldown":
                property = ItemNumericProperty.cooldown();
                break;
            case "crossbow/pull":
                property = ItemNumericProperty.crossbowPull();
                break;
            default:
                throw new IllegalArgumentException("Unknown range dispatch property type: " + propertyType);
        }

        final float scale = node.has("scale")
                ? node.get("scale").getAsFloat()
                : RangeDispatchItemModel.DEFAULT_SCALE;

        final List<RangeDispatchItemModel.Entry> entries = new ArrayList<>();
        for (JsonElement entryElement : node.getAsJsonArray("entries")) {
            final JsonObject entryObject = entryElement.getAsJsonObject();
            entries.add(RangeDispatchItemModel.Entry.entry(
                    entryObject.get("threshold").getAsFloat(),
                    deserializeItemModel(entryObject.get("model"))
            ));
        }

        final ItemModel fallback = node.has("fallback")
                ? deserializeItemModel(node.get("fallback"))
                : null;

        return ItemModel.rangeDispatch(property, scale, entries, fallback);
    }
}
