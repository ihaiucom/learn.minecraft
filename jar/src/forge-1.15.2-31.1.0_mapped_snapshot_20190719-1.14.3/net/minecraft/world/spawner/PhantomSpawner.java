package net.minecraft.world.spawner;

import java.util.Iterator;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.monster.PhantomEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.fluid.IFluidState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.stats.ServerStatisticsManager;
import net.minecraft.stats.Stats;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.GameRules;
import net.minecraft.world.server.ServerWorld;

public class PhantomSpawner {
   private int ticksUntilSpawn;

   public int tick(ServerWorld p_203232_1_, boolean p_203232_2_, boolean p_203232_3_) {
      if (!p_203232_2_) {
         return 0;
      } else if (!p_203232_1_.getGameRules().getBoolean(GameRules.field_226682_y_)) {
         return 0;
      } else {
         Random lvt_4_1_ = p_203232_1_.rand;
         --this.ticksUntilSpawn;
         if (this.ticksUntilSpawn > 0) {
            return 0;
         } else {
            this.ticksUntilSpawn += (60 + lvt_4_1_.nextInt(60)) * 20;
            if (p_203232_1_.getSkylightSubtracted() < 5 && p_203232_1_.dimension.hasSkyLight()) {
               return 0;
            } else {
               int lvt_5_1_ = 0;
               Iterator var6 = p_203232_1_.getPlayers().iterator();

               while(true) {
                  DifficultyInstance lvt_9_1_;
                  BlockPos lvt_13_1_;
                  BlockState lvt_14_1_;
                  IFluidState lvt_15_1_;
                  do {
                     BlockPos lvt_8_1_;
                     int lvt_11_1_;
                     do {
                        PlayerEntity lvt_7_1_;
                        do {
                           do {
                              do {
                                 if (!var6.hasNext()) {
                                    return lvt_5_1_;
                                 }

                                 lvt_7_1_ = (PlayerEntity)var6.next();
                              } while(lvt_7_1_.isSpectator());

                              lvt_8_1_ = new BlockPos(lvt_7_1_);
                           } while(p_203232_1_.dimension.hasSkyLight() && (lvt_8_1_.getY() < p_203232_1_.getSeaLevel() || !p_203232_1_.func_226660_f_(lvt_8_1_)));

                           lvt_9_1_ = p_203232_1_.getDifficultyForLocation(lvt_8_1_);
                        } while(!lvt_9_1_.isHarderThan(lvt_4_1_.nextFloat() * 3.0F));

                        ServerStatisticsManager lvt_10_1_ = ((ServerPlayerEntity)lvt_7_1_).getStats();
                        lvt_11_1_ = MathHelper.clamp(lvt_10_1_.getValue(Stats.CUSTOM.get(Stats.TIME_SINCE_REST)), 1, Integer.MAX_VALUE);
                        int lvt_12_1_ = true;
                     } while(lvt_4_1_.nextInt(lvt_11_1_) < 72000);

                     lvt_13_1_ = lvt_8_1_.up(20 + lvt_4_1_.nextInt(15)).east(-10 + lvt_4_1_.nextInt(21)).south(-10 + lvt_4_1_.nextInt(21));
                     lvt_14_1_ = p_203232_1_.getBlockState(lvt_13_1_);
                     lvt_15_1_ = p_203232_1_.getFluidState(lvt_13_1_);
                  } while(!WorldEntitySpawner.isSpawnableSpace(p_203232_1_, lvt_13_1_, lvt_14_1_, lvt_15_1_));

                  ILivingEntityData lvt_16_1_ = null;
                  int lvt_17_1_ = 1 + lvt_4_1_.nextInt(lvt_9_1_.getDifficulty().getId() + 1);

                  for(int lvt_18_1_ = 0; lvt_18_1_ < lvt_17_1_; ++lvt_18_1_) {
                     PhantomEntity lvt_19_1_ = (PhantomEntity)EntityType.PHANTOM.create(p_203232_1_);
                     lvt_19_1_.moveToBlockPosAndAngles(lvt_13_1_, 0.0F, 0.0F);
                     lvt_16_1_ = lvt_19_1_.onInitialSpawn(p_203232_1_, lvt_9_1_, SpawnReason.NATURAL, lvt_16_1_, (CompoundNBT)null);
                     p_203232_1_.addEntity(lvt_19_1_);
                  }

                  lvt_5_1_ += lvt_17_1_;
               }
            }
         }
      }
   }
}
