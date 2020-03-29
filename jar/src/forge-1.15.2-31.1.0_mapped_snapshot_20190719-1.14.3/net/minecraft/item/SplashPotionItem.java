package net.minecraft.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.World;

public class SplashPotionItem extends ThrowablePotionItem {
   public SplashPotionItem(Item.Properties p_i48463_1_) {
      super(p_i48463_1_);
   }

   public ActionResult<ItemStack> onItemRightClick(World p_77659_1_, PlayerEntity p_77659_2_, Hand p_77659_3_) {
      p_77659_1_.playSound((PlayerEntity)null, p_77659_2_.func_226277_ct_(), p_77659_2_.func_226278_cu_(), p_77659_2_.func_226281_cx_(), SoundEvents.ENTITY_SPLASH_POTION_THROW, SoundCategory.PLAYERS, 0.5F, 0.4F / (random.nextFloat() * 0.4F + 0.8F));
      return super.onItemRightClick(p_77659_1_, p_77659_2_, p_77659_3_);
   }
}
