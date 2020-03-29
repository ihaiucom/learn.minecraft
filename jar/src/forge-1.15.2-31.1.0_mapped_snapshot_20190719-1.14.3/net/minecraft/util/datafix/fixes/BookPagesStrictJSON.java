package net.minecraft.util.datafix.fixes;

import com.google.gson.JsonParseException;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import java.util.Optional;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.datafix.TypeReferences;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import org.apache.commons.lang3.StringUtils;

public class BookPagesStrictJSON extends DataFix {
   public BookPagesStrictJSON(Schema p_i49630_1_, boolean p_i49630_2_) {
      super(p_i49630_1_, p_i49630_2_);
   }

   public Dynamic<?> fixTag(Dynamic<?> p_209633_1_) {
      return p_209633_1_.update("pages", (p_212821_1_) -> {
         Optional var10000 = p_212821_1_.asStreamOpt().map((p_209630_0_) -> {
            return p_209630_0_.map((p_209631_0_) -> {
               if (!p_209631_0_.asString().isPresent()) {
                  return p_209631_0_;
               } else {
                  String lvt_1_1_ = p_209631_0_.asString("");
                  ITextComponent lvt_2_1_ = null;
                  if (!"null".equals(lvt_1_1_) && !StringUtils.isEmpty(lvt_1_1_)) {
                     if (lvt_1_1_.charAt(0) == '"' && lvt_1_1_.charAt(lvt_1_1_.length() - 1) == '"' || lvt_1_1_.charAt(0) == '{' && lvt_1_1_.charAt(lvt_1_1_.length() - 1) == '}') {
                        try {
                           lvt_2_1_ = (ITextComponent)JSONUtils.fromJson(SignStrictJSON.GSON, lvt_1_1_, ITextComponent.class, true);
                           if (lvt_2_1_ == null) {
                              lvt_2_1_ = new StringTextComponent("");
                           }
                        } catch (JsonParseException var6) {
                        }

                        if (lvt_2_1_ == null) {
                           try {
                              lvt_2_1_ = ITextComponent.Serializer.fromJson(lvt_1_1_);
                           } catch (JsonParseException var5) {
                           }
                        }

                        if (lvt_2_1_ == null) {
                           try {
                              lvt_2_1_ = ITextComponent.Serializer.fromJsonLenient(lvt_1_1_);
                           } catch (JsonParseException var4) {
                           }
                        }

                        if (lvt_2_1_ == null) {
                           lvt_2_1_ = new StringTextComponent(lvt_1_1_);
                        }
                     } else {
                        lvt_2_1_ = new StringTextComponent(lvt_1_1_);
                     }
                  } else {
                     lvt_2_1_ = new StringTextComponent("");
                  }

                  return p_209631_0_.createString(ITextComponent.Serializer.toJson((ITextComponent)lvt_2_1_));
               }
            });
         });
         p_209633_1_.getClass();
         return (Dynamic)DataFixUtils.orElse(var10000.map(p_209633_1_::createList), p_209633_1_.emptyList());
      });
   }

   public TypeRewriteRule makeRule() {
      Type<?> lvt_1_1_ = this.getInputSchema().getType(TypeReferences.ITEM_STACK);
      OpticFinder<?> lvt_2_1_ = lvt_1_1_.findField("tag");
      return this.fixTypeEverywhereTyped("ItemWrittenBookPagesStrictJsonFix", lvt_1_1_, (p_207415_2_) -> {
         return p_207415_2_.updateTyped(lvt_2_1_, (p_207417_1_) -> {
            return p_207417_1_.update(DSL.remainderFinder(), this::fixTag);
         });
      });
   }
}
