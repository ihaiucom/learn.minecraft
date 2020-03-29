package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import java.util.Optional;
import net.minecraft.util.datafix.TypeReferences;

public class OminousBannerTileEntityRenameFix extends NamedEntityFix {
   public OminousBannerTileEntityRenameFix(Schema p_i51509_1_, boolean p_i51509_2_) {
      super(p_i51509_1_, p_i51509_2_, "OminousBannerBlockEntityRenameFix", TypeReferences.BLOCK_ENTITY, "minecraft:banner");
   }

   protected Typed<?> fix(Typed<?> p_207419_1_) {
      return p_207419_1_.update(DSL.remainderFinder(), this::func_222992_a);
   }

   private Dynamic<?> func_222992_a(Dynamic<?> p_222992_1_) {
      Optional<String> lvt_2_1_ = p_222992_1_.get("CustomName").asString();
      if (lvt_2_1_.isPresent()) {
         String lvt_3_1_ = (String)lvt_2_1_.get();
         lvt_3_1_ = lvt_3_1_.replace("\"translate\":\"block.minecraft.illager_banner\"", "\"translate\":\"block.minecraft.ominous_banner\"");
         return p_222992_1_.set("CustomName", p_222992_1_.createString(lvt_3_1_));
      } else {
         return p_222992_1_;
      }
   }
}
