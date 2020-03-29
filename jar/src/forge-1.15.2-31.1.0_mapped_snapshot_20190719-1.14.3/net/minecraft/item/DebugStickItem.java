package net.minecraft.item;

import java.util.Collection;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.IProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class DebugStickItem extends Item {
   public DebugStickItem(Item.Properties p_i48513_1_) {
      super(p_i48513_1_);
   }

   public boolean hasEffect(ItemStack p_77636_1_) {
      return true;
   }

   public boolean canPlayerBreakBlockWhileHolding(BlockState p_195938_1_, World p_195938_2_, BlockPos p_195938_3_, PlayerEntity p_195938_4_) {
      if (!p_195938_2_.isRemote) {
         this.handleClick(p_195938_4_, p_195938_1_, p_195938_2_, p_195938_3_, false, p_195938_4_.getHeldItem(Hand.MAIN_HAND));
      }

      return false;
   }

   public ActionResultType onItemUse(ItemUseContext p_195939_1_) {
      PlayerEntity lvt_2_1_ = p_195939_1_.getPlayer();
      World lvt_3_1_ = p_195939_1_.getWorld();
      if (!lvt_3_1_.isRemote && lvt_2_1_ != null) {
         BlockPos lvt_4_1_ = p_195939_1_.getPos();
         this.handleClick(lvt_2_1_, lvt_3_1_.getBlockState(lvt_4_1_), lvt_3_1_, lvt_4_1_, true, p_195939_1_.getItem());
      }

      return ActionResultType.SUCCESS;
   }

   private void handleClick(PlayerEntity p_195958_1_, BlockState p_195958_2_, IWorld p_195958_3_, BlockPos p_195958_4_, boolean p_195958_5_, ItemStack p_195958_6_) {
      if (p_195958_1_.canUseCommandBlock()) {
         Block lvt_7_1_ = p_195958_2_.getBlock();
         StateContainer<Block, BlockState> lvt_8_1_ = lvt_7_1_.getStateContainer();
         Collection<IProperty<?>> lvt_9_1_ = lvt_8_1_.getProperties();
         String lvt_10_1_ = Registry.BLOCK.getKey(lvt_7_1_).toString();
         if (lvt_9_1_.isEmpty()) {
            sendMessage(p_195958_1_, new TranslationTextComponent(this.getTranslationKey() + ".empty", new Object[]{lvt_10_1_}));
         } else {
            CompoundNBT lvt_11_1_ = p_195958_6_.getOrCreateChildTag("DebugProperty");
            String lvt_12_1_ = lvt_11_1_.getString(lvt_10_1_);
            IProperty<?> lvt_13_1_ = lvt_8_1_.getProperty(lvt_12_1_);
            if (p_195958_5_) {
               if (lvt_13_1_ == null) {
                  lvt_13_1_ = (IProperty)lvt_9_1_.iterator().next();
               }

               BlockState lvt_14_1_ = cycleProperty(p_195958_2_, lvt_13_1_, p_195958_1_.func_226563_dT_());
               p_195958_3_.setBlockState(p_195958_4_, lvt_14_1_, 18);
               sendMessage(p_195958_1_, new TranslationTextComponent(this.getTranslationKey() + ".update", new Object[]{lvt_13_1_.getName(), func_195957_a(lvt_14_1_, lvt_13_1_)}));
            } else {
               lvt_13_1_ = (IProperty)getAdjacentValue(lvt_9_1_, lvt_13_1_, p_195958_1_.func_226563_dT_());
               String lvt_14_2_ = lvt_13_1_.getName();
               lvt_11_1_.putString(lvt_10_1_, lvt_14_2_);
               sendMessage(p_195958_1_, new TranslationTextComponent(this.getTranslationKey() + ".select", new Object[]{lvt_14_2_, func_195957_a(p_195958_2_, lvt_13_1_)}));
            }

         }
      }
   }

   private static <T extends Comparable<T>> BlockState cycleProperty(BlockState p_195960_0_, IProperty<T> p_195960_1_, boolean p_195960_2_) {
      return (BlockState)p_195960_0_.with(p_195960_1_, (Comparable)getAdjacentValue(p_195960_1_.getAllowedValues(), p_195960_0_.get(p_195960_1_), p_195960_2_));
   }

   private static <T> T getAdjacentValue(Iterable<T> p_195959_0_, @Nullable T p_195959_1_, boolean p_195959_2_) {
      return p_195959_2_ ? Util.getElementBefore(p_195959_0_, p_195959_1_) : Util.getElementAfter(p_195959_0_, p_195959_1_);
   }

   private static void sendMessage(PlayerEntity p_195956_0_, ITextComponent p_195956_1_) {
      ((ServerPlayerEntity)p_195956_0_).sendMessage(p_195956_1_, ChatType.GAME_INFO);
   }

   private static <T extends Comparable<T>> String func_195957_a(BlockState p_195957_0_, IProperty<T> p_195957_1_) {
      return p_195957_1_.getName(p_195957_0_.get(p_195957_1_));
   }
}
