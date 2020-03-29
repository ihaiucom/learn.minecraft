package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import java.util.Optional;
import java.util.stream.Stream;
import net.minecraft.util.datafix.TypeReferences;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

public class ItemLoreComponentizeFix extends DataFix {
   public ItemLoreComponentizeFix(Schema p_i50426_1_, boolean p_i50426_2_) {
      super(p_i50426_1_, p_i50426_2_);
   }

   protected TypeRewriteRule makeRule() {
      Type<?> lvt_1_1_ = this.getInputSchema().getType(TypeReferences.ITEM_STACK);
      OpticFinder<?> lvt_2_1_ = lvt_1_1_.findField("tag");
      return this.fixTypeEverywhereTyped("Item Lore componentize", lvt_1_1_, (p_219832_1_) -> {
         return p_219832_1_.updateTyped(lvt_2_1_, (p_219836_0_) -> {
            return p_219836_0_.update(DSL.remainderFinder(), (p_219835_0_) -> {
               return p_219835_0_.update("display", (p_219833_0_) -> {
                  return p_219833_0_.update("Lore", (p_219834_0_) -> {
                     Optional var10000 = p_219834_0_.asStreamOpt().map(ItemLoreComponentizeFix::func_219830_a);
                     p_219834_0_.getClass();
                     return (Dynamic)DataFixUtils.orElse(var10000.map(p_219834_0_::createList), p_219834_0_);
                  });
               });
            });
         });
      });
   }

   private static <T> Stream<Dynamic<T>> func_219830_a(Stream<Dynamic<T>> p_219830_0_) {
      return p_219830_0_.map((p_219831_0_) -> {
         Optional var10000 = p_219831_0_.asString().map(ItemLoreComponentizeFix::func_219837_a);
         p_219831_0_.getClass();
         return (Dynamic)DataFixUtils.orElse(var10000.map(p_219831_0_::createString), p_219831_0_);
      });
   }

   private static String func_219837_a(String p_219837_0_) {
      return ITextComponent.Serializer.toJson(new StringTextComponent(p_219837_0_));
   }
}
