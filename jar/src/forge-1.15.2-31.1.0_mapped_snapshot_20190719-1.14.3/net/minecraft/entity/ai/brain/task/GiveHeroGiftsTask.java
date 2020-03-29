package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.BrainUtil;
import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.merchant.villager.VillagerProfession;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.Effects;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootParameterSets;
import net.minecraft.world.storage.loot.LootParameters;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraft.world.storage.loot.LootTables;

public class GiveHeroGiftsTask extends Task<VillagerEntity> {
   private static final Map<VillagerProfession, ResourceLocation> GIFTS = (Map)Util.make(Maps.newHashMap(), (p_220395_0_) -> {
      p_220395_0_.put(VillagerProfession.ARMORER, LootTables.GAMEPLAY_HERO_OF_THE_VILLAGE_ARMORER_GIFT);
      p_220395_0_.put(VillagerProfession.BUTCHER, LootTables.GAMEPLAY_HERO_OF_THE_VILLAGE_BUTCHER_GIFT);
      p_220395_0_.put(VillagerProfession.CARTOGRAPHER, LootTables.GAMEPLAY_HERO_OF_THE_VILLAGE_CARTOGRAPHER_GIFT);
      p_220395_0_.put(VillagerProfession.CLERIC, LootTables.GAMEPLAY_HERO_OF_THE_VILLAGE_CLERIC_GIFT);
      p_220395_0_.put(VillagerProfession.FARMER, LootTables.GAMEPLAY_HERO_OF_THE_VILLAGE_FARMER_GIFT);
      p_220395_0_.put(VillagerProfession.FISHERMAN, LootTables.GAMEPLAY_HERO_OF_THE_VILLAGE_FISHERMAN_GIFT);
      p_220395_0_.put(VillagerProfession.FLETCHER, LootTables.GAMEPLAY_HERO_OF_THE_VILLAGE_FLETCHER_GIFT);
      p_220395_0_.put(VillagerProfession.LEATHERWORKER, LootTables.GAMEPLAY_HERO_OF_THE_VILLAGE_LEATHERWORKER_GIFT);
      p_220395_0_.put(VillagerProfession.LIBRARIAN, LootTables.GAMEPLAY_HERO_OF_THE_VILLAGE_LIBRARIAN_GIFT);
      p_220395_0_.put(VillagerProfession.MASON, LootTables.GAMEPLAY_HERO_OF_THE_VILLAGE_MASON_GIFT);
      p_220395_0_.put(VillagerProfession.SHEPHERD, LootTables.GAMEPLAY_HERO_OF_THE_VILLAGE_SHEPHERD_GIFT);
      p_220395_0_.put(VillagerProfession.TOOLSMITH, LootTables.GAMEPLAY_HERO_OF_THE_VILLAGE_TOOLSMITH_GIFT);
      p_220395_0_.put(VillagerProfession.WEAPONSMITH, LootTables.GAMEPLAY_HERO_OF_THE_VILLAGE_WEAPONSMITH_GIFT);
   });
   private int cooldown = 600;
   private boolean done;
   private long startTime;

   public GiveHeroGiftsTask(int p_i50366_1_) {
      super(ImmutableMap.of(MemoryModuleType.WALK_TARGET, MemoryModuleStatus.REGISTERED, MemoryModuleType.LOOK_TARGET, MemoryModuleStatus.REGISTERED, MemoryModuleType.INTERACTION_TARGET, MemoryModuleStatus.REGISTERED, MemoryModuleType.NEAREST_VISIBLE_PLAYER, MemoryModuleStatus.VALUE_PRESENT), p_i50366_1_);
   }

   protected boolean shouldExecute(ServerWorld p_212832_1_, VillagerEntity p_212832_2_) {
      if (!this.hasNearestPlayer(p_212832_2_)) {
         return false;
      } else if (this.cooldown > 0) {
         --this.cooldown;
         return false;
      } else {
         return true;
      }
   }

   protected void startExecuting(ServerWorld p_212831_1_, VillagerEntity p_212831_2_, long p_212831_3_) {
      this.done = false;
      this.startTime = p_212831_3_;
      PlayerEntity lvt_5_1_ = (PlayerEntity)this.getNearestPlayer(p_212831_2_).get();
      p_212831_2_.getBrain().setMemory(MemoryModuleType.INTERACTION_TARGET, (Object)lvt_5_1_);
      BrainUtil.lookAt(p_212831_2_, lvt_5_1_);
   }

   protected boolean shouldContinueExecuting(ServerWorld p_212834_1_, VillagerEntity p_212834_2_, long p_212834_3_) {
      return this.hasNearestPlayer(p_212834_2_) && !this.done;
   }

   protected void updateTask(ServerWorld p_212833_1_, VillagerEntity p_212833_2_, long p_212833_3_) {
      PlayerEntity lvt_5_1_ = (PlayerEntity)this.getNearestPlayer(p_212833_2_).get();
      BrainUtil.lookAt(p_212833_2_, lvt_5_1_);
      if (this.isCloseEnough(p_212833_2_, lvt_5_1_)) {
         if (p_212833_3_ - this.startTime > 20L) {
            this.giveGifts(p_212833_2_, lvt_5_1_);
            this.done = true;
         }
      } else {
         BrainUtil.approach(p_212833_2_, lvt_5_1_, 5);
      }

   }

   protected void resetTask(ServerWorld p_212835_1_, VillagerEntity p_212835_2_, long p_212835_3_) {
      this.cooldown = getNextCooldown(p_212835_1_);
      p_212835_2_.getBrain().removeMemory(MemoryModuleType.INTERACTION_TARGET);
      p_212835_2_.getBrain().removeMemory(MemoryModuleType.WALK_TARGET);
      p_212835_2_.getBrain().removeMemory(MemoryModuleType.LOOK_TARGET);
   }

   private void giveGifts(VillagerEntity p_220398_1_, LivingEntity p_220398_2_) {
      List<ItemStack> lvt_3_1_ = this.getGifts(p_220398_1_);
      Iterator var4 = lvt_3_1_.iterator();

      while(var4.hasNext()) {
         ItemStack lvt_5_1_ = (ItemStack)var4.next();
         BrainUtil.throwItemAt(p_220398_1_, lvt_5_1_, p_220398_2_);
      }

   }

   private List<ItemStack> getGifts(VillagerEntity p_220399_1_) {
      if (p_220399_1_.isChild()) {
         return ImmutableList.of(new ItemStack(Items.POPPY));
      } else {
         VillagerProfession lvt_2_1_ = p_220399_1_.getVillagerData().getProfession();
         if (GIFTS.containsKey(lvt_2_1_)) {
            LootTable lvt_3_1_ = p_220399_1_.world.getServer().getLootTableManager().getLootTableFromLocation((ResourceLocation)GIFTS.get(lvt_2_1_));
            LootContext.Builder lvt_4_1_ = (new LootContext.Builder((ServerWorld)p_220399_1_.world)).withParameter(LootParameters.POSITION, new BlockPos(p_220399_1_)).withParameter(LootParameters.THIS_ENTITY, p_220399_1_).withRandom(p_220399_1_.getRNG());
            return lvt_3_1_.generate(lvt_4_1_.build(LootParameterSets.GIFT));
         } else {
            return ImmutableList.of(new ItemStack(Items.WHEAT_SEEDS));
         }
      }
   }

   private boolean hasNearestPlayer(VillagerEntity p_220396_1_) {
      return this.getNearestPlayer(p_220396_1_).isPresent();
   }

   private Optional<PlayerEntity> getNearestPlayer(VillagerEntity p_220400_1_) {
      return p_220400_1_.getBrain().getMemory(MemoryModuleType.NEAREST_VISIBLE_PLAYER).filter(this::isHero);
   }

   private boolean isHero(PlayerEntity p_220402_1_) {
      return p_220402_1_.isPotionActive(Effects.HERO_OF_THE_VILLAGE);
   }

   private boolean isCloseEnough(VillagerEntity p_220401_1_, PlayerEntity p_220401_2_) {
      BlockPos lvt_3_1_ = new BlockPos(p_220401_2_);
      BlockPos lvt_4_1_ = new BlockPos(p_220401_1_);
      return lvt_4_1_.withinDistance(lvt_3_1_, 5.0D);
   }

   private static int getNextCooldown(ServerWorld p_220397_0_) {
      return 600 + p_220397_0_.rand.nextInt(6001);
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
