package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.FireworkRocketEntity;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.item.DyeColor;
import net.minecraft.item.FireworkRocketItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.raid.Raid;
import net.minecraft.world.server.ServerWorld;

public class CelebrateRaidVictoryTask extends Task<VillagerEntity> {
   @Nullable
   private Raid raid;

   public CelebrateRaidVictoryTask(int p_i50370_1_, int p_i50370_2_) {
      super(ImmutableMap.of(), p_i50370_1_, p_i50370_2_);
   }

   protected boolean shouldExecute(ServerWorld p_212832_1_, VillagerEntity p_212832_2_) {
      BlockPos lvt_3_1_ = new BlockPos(p_212832_2_);
      this.raid = p_212832_1_.findRaid(lvt_3_1_);
      return this.raid != null && this.raid.isVictory() && MoveToSkylightTask.func_226306_a_(p_212832_1_, p_212832_2_, lvt_3_1_);
   }

   protected boolean shouldContinueExecuting(ServerWorld p_212834_1_, VillagerEntity p_212834_2_, long p_212834_3_) {
      return this.raid != null && !this.raid.isStopped();
   }

   protected void resetTask(ServerWorld p_212835_1_, VillagerEntity p_212835_2_, long p_212835_3_) {
      this.raid = null;
      p_212835_2_.getBrain().updateActivity(p_212835_1_.getDayTime(), p_212835_1_.getGameTime());
   }

   protected void updateTask(ServerWorld p_212833_1_, VillagerEntity p_212833_2_, long p_212833_3_) {
      Random lvt_5_1_ = p_212833_2_.getRNG();
      if (lvt_5_1_.nextInt(100) == 0) {
         p_212833_2_.func_213711_eb();
      }

      if (lvt_5_1_.nextInt(200) == 0 && MoveToSkylightTask.func_226306_a_(p_212833_1_, p_212833_2_, new BlockPos(p_212833_2_))) {
         DyeColor lvt_6_1_ = DyeColor.values()[lvt_5_1_.nextInt(DyeColor.values().length)];
         int lvt_7_1_ = lvt_5_1_.nextInt(3);
         ItemStack lvt_8_1_ = this.makeFirework(lvt_6_1_, lvt_7_1_);
         FireworkRocketEntity lvt_9_1_ = new FireworkRocketEntity(p_212833_2_.world, p_212833_2_.func_226277_ct_(), p_212833_2_.func_226280_cw_(), p_212833_2_.func_226281_cx_(), lvt_8_1_);
         p_212833_2_.world.addEntity(lvt_9_1_);
      }

   }

   private ItemStack makeFirework(DyeColor p_220391_1_, int p_220391_2_) {
      ItemStack lvt_3_1_ = new ItemStack(Items.FIREWORK_ROCKET, 1);
      ItemStack lvt_4_1_ = new ItemStack(Items.FIREWORK_STAR);
      CompoundNBT lvt_5_1_ = lvt_4_1_.getOrCreateChildTag("Explosion");
      List<Integer> lvt_6_1_ = Lists.newArrayList();
      lvt_6_1_.add(p_220391_1_.getFireworkColor());
      lvt_5_1_.putIntArray("Colors", (List)lvt_6_1_);
      lvt_5_1_.putByte("Type", (byte)FireworkRocketItem.Shape.BURST.func_196071_a());
      CompoundNBT lvt_7_1_ = lvt_3_1_.getOrCreateChildTag("Fireworks");
      ListNBT lvt_8_1_ = new ListNBT();
      CompoundNBT lvt_9_1_ = lvt_4_1_.getChildTag("Explosion");
      if (lvt_9_1_ != null) {
         lvt_8_1_.add(lvt_9_1_);
      }

      lvt_7_1_.putByte("Flight", (byte)p_220391_2_);
      if (!lvt_8_1_.isEmpty()) {
         lvt_7_1_.put("Explosions", lvt_8_1_);
      }

      return lvt_3_1_;
   }

   // $FF: synthetic method
   protected boolean shouldContinueExecuting(ServerWorld p_212834_1_, LivingEntity p_212834_2_, long p_212834_3_) {
      return this.shouldContinueExecuting(p_212834_1_, (VillagerEntity)p_212834_2_, p_212834_3_);
   }

   // $FF: synthetic method
   protected void resetTask(ServerWorld p_212835_1_, LivingEntity p_212835_2_, long p_212835_3_) {
      this.resetTask(p_212835_1_, (VillagerEntity)p_212835_2_, p_212835_3_);
   }

   // $FF: synthetic method
   protected void updateTask(ServerWorld p_212833_1_, LivingEntity p_212833_2_, long p_212833_3_) {
      this.updateTask(p_212833_1_, (VillagerEntity)p_212833_2_, p_212833_3_);
   }
}
