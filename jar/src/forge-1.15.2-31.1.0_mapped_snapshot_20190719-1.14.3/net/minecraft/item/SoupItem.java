package net.minecraft.item;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

public class SoupItem extends Item {
   public SoupItem(Item.Properties p_i50054_1_) {
      super(p_i50054_1_);
   }

   public ItemStack onItemUseFinish(ItemStack p_77654_1_, World p_77654_2_, LivingEntity p_77654_3_) {
      ItemStack lvt_4_1_ = super.onItemUseFinish(p_77654_1_, p_77654_2_, p_77654_3_);
      return p_77654_3_ instanceof PlayerEntity && ((PlayerEntity)p_77654_3_).abilities.isCreativeMode ? lvt_4_1_ : new ItemStack(Items.BOWL);
   }
}
