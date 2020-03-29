package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Pair;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import net.minecraft.util.datafix.TypeReferences;

public abstract class BlockRename extends DataFix {
   private final String name;

   public BlockRename(Schema p_i49678_1_, String p_i49678_2_) {
      super(p_i49678_1_, false);
      this.name = p_i49678_2_;
   }

   public TypeRewriteRule makeRule() {
      Type<?> lvt_1_1_ = this.getInputSchema().getType(TypeReferences.BLOCK_NAME);
      Type<Pair<String, String>> lvt_2_1_ = DSL.named(TypeReferences.BLOCK_NAME.typeName(), DSL.namespacedString());
      if (!Objects.equals(lvt_1_1_, lvt_2_1_)) {
         throw new IllegalStateException("block type is not what was expected.");
      } else {
         TypeRewriteRule lvt_3_1_ = this.fixTypeEverywhere(this.name + " for block", lvt_2_1_, (p_209705_1_) -> {
            return (p_206308_1_) -> {
               return p_206308_1_.mapSecond(this::fixBlock);
            };
         });
         TypeRewriteRule lvt_4_1_ = this.fixTypeEverywhereTyped(this.name + " for block_state", this.getInputSchema().getType(TypeReferences.BLOCK_STATE), (p_209704_1_) -> {
            return p_209704_1_.update(DSL.remainderFinder(), (p_207439_1_) -> {
               Optional<String> lvt_2_1_ = p_207439_1_.get("Name").asString();
               return lvt_2_1_.isPresent() ? p_207439_1_.set("Name", p_207439_1_.createString(this.fixBlock((String)lvt_2_1_.get()))) : p_207439_1_;
            });
         });
         return TypeRewriteRule.seq(lvt_3_1_, lvt_4_1_);
      }
   }

   protected abstract String fixBlock(String var1);

   public static DataFix create(Schema p_207437_0_, String p_207437_1_, final Function<String, String> p_207437_2_) {
      return new BlockRename(p_207437_0_, p_207437_1_) {
         protected String fixBlock(String p_206309_1_) {
            return (String)p_207437_2_.apply(p_206309_1_);
         }
      };
   }
}
