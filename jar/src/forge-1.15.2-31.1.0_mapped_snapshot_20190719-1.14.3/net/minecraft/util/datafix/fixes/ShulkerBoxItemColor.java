package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Pair;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.util.datafix.TypeReferences;

public class ShulkerBoxItemColor extends DataFix {
   public static final String[] NAMES_BY_COLOR = new String[]{"minecraft:white_shulker_box", "minecraft:orange_shulker_box", "minecraft:magenta_shulker_box", "minecraft:light_blue_shulker_box", "minecraft:yellow_shulker_box", "minecraft:lime_shulker_box", "minecraft:pink_shulker_box", "minecraft:gray_shulker_box", "minecraft:silver_shulker_box", "minecraft:cyan_shulker_box", "minecraft:purple_shulker_box", "minecraft:blue_shulker_box", "minecraft:brown_shulker_box", "minecraft:green_shulker_box", "minecraft:red_shulker_box", "minecraft:black_shulker_box"};

   public ShulkerBoxItemColor(Schema p_i49640_1_, boolean p_i49640_2_) {
      super(p_i49640_1_, p_i49640_2_);
   }

   public TypeRewriteRule makeRule() {
      Type<?> lvt_1_1_ = this.getInputSchema().getType(TypeReferences.ITEM_STACK);
      OpticFinder<Pair<String, String>> lvt_2_1_ = DSL.fieldFinder("id", DSL.named(TypeReferences.ITEM_NAME.typeName(), DSL.namespacedString()));
      OpticFinder<?> lvt_3_1_ = lvt_1_1_.findField("tag");
      OpticFinder<?> lvt_4_1_ = lvt_3_1_.type().findField("BlockEntityTag");
      return this.fixTypeEverywhereTyped("ItemShulkerBoxColorFix", lvt_1_1_, (p_206358_3_) -> {
         Optional<Pair<String, String>> lvt_4_1_x = p_206358_3_.getOptional(lvt_2_1_);
         if (lvt_4_1_x.isPresent() && Objects.equals(((Pair)lvt_4_1_x.get()).getSecond(), "minecraft:shulker_box")) {
            Optional<? extends Typed<?>> lvt_5_1_ = p_206358_3_.getOptionalTyped(lvt_3_1_);
            if (lvt_5_1_.isPresent()) {
               Typed<?> lvt_6_1_ = (Typed)lvt_5_1_.get();
               Optional<? extends Typed<?>> lvt_7_1_ = lvt_6_1_.getOptionalTyped(lvt_4_1_);
               if (lvt_7_1_.isPresent()) {
                  Typed<?> lvt_8_1_ = (Typed)lvt_7_1_.get();
                  Dynamic<?> lvt_9_1_ = (Dynamic)lvt_8_1_.get(DSL.remainderFinder());
                  int lvt_10_1_ = lvt_9_1_.get("Color").asInt(0);
                  lvt_9_1_.remove("Color");
                  return p_206358_3_.set(lvt_3_1_, lvt_6_1_.set(lvt_4_1_, lvt_8_1_.set(DSL.remainderFinder(), lvt_9_1_))).set(lvt_2_1_, Pair.of(TypeReferences.ITEM_NAME.typeName(), NAMES_BY_COLOR[lvt_10_1_ % 16]));
               }
            }
         }

         return p_206358_3_;
      });
   }
}
