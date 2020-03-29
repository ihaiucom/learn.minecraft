package net.minecraft.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PotionEntity;
import net.minecraft.stats.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class ThrowablePotionItem extends PotionItem {
   public ThrowablePotionItem(Item.Properties p_i225739_1_) {
      super(p_i225739_1_);
   }

   public ActionResult<ItemStack> onItemRightClick(World p_77659_1_, PlayerEntity p_77659_2_, Hand p_77659_3_) {
      ItemStack lvt_4_1_ = p_77659_2_.getHeldItem(p_77659_3_);
      if (!p_77659_1_.isRemote) {
         PotionEntity lvt_5_1_ = new PotionEntity(p_77659_1_, p_77659_2_);
         lvt_5_1_.setItem(lvt_4_1_);
         lvt_5_1_.shoot(p_77659_2_, p_77659_2_.rotationPitch, p_77659_2_.rotationYaw, -20.0F, 0.5F, 1.0F);
         p_77659_1_.addEntity(lvt_5_1_);
      }

      p_77659_2_.addStat(Stats.ITEM_USED.get(this));
      if (!p_77659_2_.abilities.isCreativeMode) {
         lvt_4_1_.shrink(1);
      }

      return ActionResult.func_226248_a_(lvt_4_1_);
   }
}
