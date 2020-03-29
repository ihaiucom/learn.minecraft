package net.minecraft.util.datafix.fixes;

import com.google.common.collect.Lists;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.types.templates.TaggedChoice.TaggedChoiceType;
import com.mojang.datafixers.util.Pair;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.util.datafix.TypeReferences;

public class MinecartEntityTypes extends DataFix {
   private static final List<String> MINECART_TYPE_LIST = Lists.newArrayList(new String[]{"MinecartRideable", "MinecartChest", "MinecartFurnace"});

   public MinecartEntityTypes(Schema p_i49661_1_, boolean p_i49661_2_) {
      super(p_i49661_1_, p_i49661_2_);
   }

   public TypeRewriteRule makeRule() {
      TaggedChoiceType<String> lvt_1_1_ = this.getInputSchema().findChoiceType(TypeReferences.ENTITY);
      TaggedChoiceType<String> lvt_2_1_ = this.getOutputSchema().findChoiceType(TypeReferences.ENTITY);
      return this.fixTypeEverywhere("EntityMinecartIdentifiersFix", lvt_1_1_, lvt_2_1_, (p_209746_2_) -> {
         return (p_206328_3_) -> {
            if (!Objects.equals(p_206328_3_.getFirst(), "Minecart")) {
               return p_206328_3_;
            } else {
               Typed<? extends Pair<String, ?>> lvt_4_1_ = (Typed)lvt_1_1_.point(p_209746_2_, "Minecart", p_206328_3_.getSecond()).orElseThrow(IllegalStateException::new);
               Dynamic<?> lvt_5_1_ = (Dynamic)lvt_4_1_.getOrCreate(DSL.remainderFinder());
               int lvt_7_1_ = lvt_5_1_.get("Type").asInt(0);
               String lvt_6_2_;
               if (lvt_7_1_ > 0 && lvt_7_1_ < MINECART_TYPE_LIST.size()) {
                  lvt_6_2_ = (String)MINECART_TYPE_LIST.get(lvt_7_1_);
               } else {
                  lvt_6_2_ = "MinecartRideable";
               }

               return Pair.of(lvt_6_2_, ((Optional)((Type)lvt_2_1_.types().get(lvt_6_2_)).read(lvt_4_1_.write()).getSecond()).orElseThrow(() -> {
                  return new IllegalStateException("Could not read the new minecart.");
               }));
            }
         };
      });
   }
}
