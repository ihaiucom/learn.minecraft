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

public class HeightmapRenamingFix extends DataFix {
   public HeightmapRenamingFix(Schema p_i49646_1_, boolean p_i49646_2_) {
      super(p_i49646_1_, p_i49646_2_);
   }

   protected TypeRewriteRule makeRule() {
      Type<?> lvt_1_1_ = this.getInputSchema().getType(TypeReferences.CHUNK);
      OpticFinder<?> lvt_2_1_ = lvt_1_1_.findField("Level");
      return this.fixTypeEverywhereTyped("HeightmapRenamingFix", lvt_1_1_, (p_207306_2_) -> {
         return p_207306_2_.updateTyped(lvt_2_1_, (p_207307_1_) -> {
            return p_207307_1_.update(DSL.remainderFinder(), this::fix);
         });
      });
   }

   private Dynamic<?> fix(Dynamic<?> p_209766_1_) {
      Optional<? extends Dynamic<?>> lvt_2_1_ = p_209766_1_.get("Heightmaps").get();
      if (!lvt_2_1_.isPresent()) {
         return p_209766_1_;
      } else {
         Dynamic<?> lvt_3_1_ = (Dynamic)lvt_2_1_.get();
         Optional<? extends Dynamic<?>> lvt_4_1_ = lvt_3_1_.get("LIQUID").get();
         if (lvt_4_1_.isPresent()) {
            lvt_3_1_ = lvt_3_1_.remove("LIQUID");
            lvt_3_1_ = lvt_3_1_.set("WORLD_SURFACE_WG", (Dynamic)lvt_4_1_.get());
         }

         Optional<? extends Dynamic<?>> lvt_5_1_ = lvt_3_1_.get("SOLID").get();
         if (lvt_5_1_.isPresent()) {
            lvt_3_1_ = lvt_3_1_.remove("SOLID");
            lvt_3_1_ = lvt_3_1_.set("OCEAN_FLOOR_WG", (Dynamic)lvt_5_1_.get());
            lvt_3_1_ = lvt_3_1_.set("OCEAN_FLOOR", (Dynamic)lvt_5_1_.get());
         }

         Optional<? extends Dynamic<?>> lvt_6_1_ = lvt_3_1_.get("LIGHT").get();
         if (lvt_6_1_.isPresent()) {
            lvt_3_1_ = lvt_3_1_.remove("LIGHT");
            lvt_3_1_ = lvt_3_1_.set("LIGHT_BLOCKING", (Dynamic)lvt_6_1_.get());
         }

         Optional<? extends Dynamic<?>> lvt_7_1_ = lvt_3_1_.get("RAIN").get();
         if (lvt_7_1_.isPresent()) {
            lvt_3_1_ = lvt_3_1_.remove("RAIN");
            lvt_3_1_ = lvt_3_1_.set("MOTION_BLOCKING", (Dynamic)lvt_7_1_.get());
            lvt_3_1_ = lvt_3_1_.set("MOTION_BLOCKING_NO_LEAVES", (Dynamic)lvt_7_1_.get());
         }

         return p_209766_1_.set("Heightmaps", lvt_3_1_);
      }
   }
}
