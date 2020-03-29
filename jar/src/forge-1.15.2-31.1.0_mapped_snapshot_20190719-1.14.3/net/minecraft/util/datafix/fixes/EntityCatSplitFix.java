package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.util.Pair;
import java.util.Objects;

public class EntityCatSplitFix extends EntityRenameHelper {
   public EntityCatSplitFix(Schema p_i50428_1_, boolean p_i50428_2_) {
      super("EntityCatSplitFix", p_i50428_1_, p_i50428_2_);
   }

   protected Pair<String, Dynamic<?>> getNewNameAndTag(String p_209758_1_, Dynamic<?> p_209758_2_) {
      if (Objects.equals("minecraft:ocelot", p_209758_1_)) {
         int lvt_3_1_ = p_209758_2_.get("CatType").asInt(0);
         if (lvt_3_1_ == 0) {
            String lvt_4_1_ = p_209758_2_.get("Owner").asString("");
            String lvt_5_1_ = p_209758_2_.get("OwnerUUID").asString("");
            if (lvt_4_1_.length() > 0 || lvt_5_1_.length() > 0) {
               p_209758_2_.set("Trusting", p_209758_2_.createBoolean(true));
            }
         } else if (lvt_3_1_ > 0 && lvt_3_1_ < 4) {
            p_209758_2_ = p_209758_2_.set("CatType", p_209758_2_.createInt(lvt_3_1_));
            p_209758_2_ = p_209758_2_.set("OwnerUUID", p_209758_2_.createString(p_209758_2_.get("OwnerUUID").asString("")));
            return Pair.of("minecraft:cat", p_209758_2_);
         }
      }

      return Pair.of(p_209758_1_, p_209758_2_);
   }
}
