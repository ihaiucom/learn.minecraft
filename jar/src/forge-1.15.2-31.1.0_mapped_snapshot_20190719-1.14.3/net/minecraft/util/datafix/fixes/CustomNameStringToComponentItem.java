package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import java.util.Optional;
import net.minecraft.util.datafix.TypeReferences;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class CustomNameStringToComponentItem extends DataFix {
   public CustomNameStringToComponentItem(Schema p_i49644_1_, boolean p_i49644_2_) {
      super(p_i49644_1_, p_i49644_2_);
   }

   private Dynamic<?> fixTag(Dynamic<?> p_209621_1_) {
      Optional<? extends Dynamic<?>> lvt_2_1_ = p_209621_1_.get("display").get();
      if (lvt_2_1_.isPresent()) {
         Dynamic<?> lvt_3_1_ = (Dynamic)lvt_2_1_.get();
         Optional<String> lvt_4_1_ = lvt_3_1_.get("Name").asString();
         if (lvt_4_1_.isPresent()) {
            lvt_3_1_ = lvt_3_1_.set("Name", lvt_3_1_.createString(ITextComponent.Serializer.toJson(new StringTextComponent((String)lvt_4_1_.get()))));
         } else {
            Optional<String> lvt_5_1_ = lvt_3_1_.get("LocName").asString();
            if (lvt_5_1_.isPresent()) {
               lvt_3_1_ = lvt_3_1_.set("Name", lvt_3_1_.createString(ITextComponent.Serializer.toJson(new TranslationTextComponent((String)lvt_5_1_.get(), new Object[0]))));
               lvt_3_1_ = lvt_3_1_.remove("LocName");
            }
         }

         return p_209621_1_.set("display", lvt_3_1_);
      } else {
         return p_209621_1_;
      }
   }

   public TypeRewriteRule makeRule() {
      Type<?> lvt_1_1_ = this.getInputSchema().getType(TypeReferences.ITEM_STACK);
      OpticFinder<?> lvt_2_1_ = lvt_1_1_.findField("tag");
      return this.fixTypeEverywhereTyped("ItemCustomNameToComponentFix", lvt_1_1_, (p_207467_2_) -> {
         return p_207467_2_.updateTyped(lvt_2_1_, (p_207469_1_) -> {
            return p_207469_1_.update(DSL.remainderFinder(), this::fixTag);
         });
      });
   }
}
