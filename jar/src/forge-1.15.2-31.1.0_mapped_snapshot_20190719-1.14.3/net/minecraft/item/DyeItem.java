package net.minecraft.item;

import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;

public class DyeItem extends Item {
   private static final Map<DyeColor, DyeItem> COLOR_DYE_ITEM_MAP = Maps.newEnumMap(DyeColor.class);
   private final DyeColor dyeColor;

   public DyeItem(DyeColor p_i48510_1_, Item.Properties p_i48510_2_) {
      super(p_i48510_2_);
      this.dyeColor = p_i48510_1_;
      COLOR_DYE_ITEM_MAP.put(p_i48510_1_, this);
   }

   public boolean itemInteractionForEntity(ItemStack p_111207_1_, PlayerEntity p_111207_2_, LivingEntity p_111207_3_, Hand p_111207_4_) {
      if (p_111207_3_ instanceof SheepEntity) {
         SheepEntity lvt_5_1_ = (SheepEntity)p_111207_3_;
         if (lvt_5_1_.isAlive() && !lvt_5_1_.getSheared() && lvt_5_1_.getFleeceColor() != this.dyeColor) {
            lvt_5_1_.setFleeceColor(this.dyeColor);
            p_111207_1_.shrink(1);
         }

         return true;
      } else {
         return false;
      }
   }

   public DyeColor getDyeColor() {
      return this.dyeColor;
   }

   public static DyeItem getItem(DyeColor p_195961_0_) {
      return (DyeItem)COLOR_DYE_ITEM_MAP.get(p_195961_0_);
   }
}
