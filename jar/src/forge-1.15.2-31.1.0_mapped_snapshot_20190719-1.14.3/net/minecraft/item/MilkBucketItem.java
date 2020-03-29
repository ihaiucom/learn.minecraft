package net.minecraft.item;

import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.stats.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.capability.wrappers.FluidBucketWrapper;

public class MilkBucketItem extends Item {
   public MilkBucketItem(Item.Properties p_i48481_1_) {
      super(p_i48481_1_);
   }

   public ItemStack onItemUseFinish(ItemStack p_77654_1_, World p_77654_2_, LivingEntity p_77654_3_) {
      if (!p_77654_2_.isRemote) {
         p_77654_3_.curePotionEffects(p_77654_1_);
      }

      if (p_77654_3_ instanceof ServerPlayerEntity) {
         ServerPlayerEntity serverplayerentity = (ServerPlayerEntity)p_77654_3_;
         CriteriaTriggers.CONSUME_ITEM.trigger(serverplayerentity, p_77654_1_);
         serverplayerentity.addStat(Stats.ITEM_USED.get(this));
      }

      if (p_77654_3_ instanceof PlayerEntity && !((PlayerEntity)p_77654_3_).abilities.isCreativeMode) {
         p_77654_1_.shrink(1);
      }

      if (!p_77654_2_.isRemote) {
         p_77654_3_.clearActivePotions();
      }

      return p_77654_1_.isEmpty() ? new ItemStack(Items.BUCKET) : p_77654_1_;
   }

   public int getUseDuration(ItemStack p_77626_1_) {
      return 32;
   }

   public UseAction getUseAction(ItemStack p_77661_1_) {
      return UseAction.DRINK;
   }

   public ActionResult<ItemStack> onItemRightClick(World p_77659_1_, PlayerEntity p_77659_2_, Hand p_77659_3_) {
      p_77659_2_.setActiveHand(p_77659_3_);
      return ActionResult.func_226248_a_(p_77659_2_.getHeldItem(p_77659_3_));
   }

   public ICapabilityProvider initCapabilities(ItemStack p_initCapabilities_1_, @Nullable CompoundNBT p_initCapabilities_2_) {
      return new FluidBucketWrapper(p_initCapabilities_1_);
   }
}
