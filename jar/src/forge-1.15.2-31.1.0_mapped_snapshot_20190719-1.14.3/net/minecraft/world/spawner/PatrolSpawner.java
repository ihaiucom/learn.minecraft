package net.minecraft.world.spawner;

import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.monster.PatrollerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.server.ServerWorld;

public class PatrolSpawner {
   private int field_222698_b;

   public int tick(ServerWorld p_222696_1_, boolean p_222696_2_, boolean p_222696_3_) {
      if (!p_222696_2_) {
         return 0;
      } else if (!p_222696_1_.getGameRules().getBoolean(GameRules.field_230127_D_)) {
         return 0;
      } else {
         Random lvt_4_1_ = p_222696_1_.rand;
         --this.field_222698_b;
         if (this.field_222698_b > 0) {
            return 0;
         } else {
            this.field_222698_b += 12000 + lvt_4_1_.nextInt(1200);
            long lvt_5_1_ = p_222696_1_.getDayTime() / 24000L;
            if (lvt_5_1_ >= 5L && p_222696_1_.isDaytime()) {
               if (lvt_4_1_.nextInt(5) != 0) {
                  return 0;
               } else {
                  int lvt_7_1_ = p_222696_1_.getPlayers().size();
                  if (lvt_7_1_ < 1) {
                     return 0;
                  } else {
                     PlayerEntity lvt_8_1_ = (PlayerEntity)p_222696_1_.getPlayers().get(lvt_4_1_.nextInt(lvt_7_1_));
                     if (lvt_8_1_.isSpectator()) {
                        return 0;
                     } else if (p_222696_1_.func_217483_b_(lvt_8_1_.getPosition())) {
                        return 0;
                     } else {
                        int lvt_9_1_ = (24 + lvt_4_1_.nextInt(24)) * (lvt_4_1_.nextBoolean() ? -1 : 1);
                        int lvt_10_1_ = (24 + lvt_4_1_.nextInt(24)) * (lvt_4_1_.nextBoolean() ? -1 : 1);
                        BlockPos.Mutable lvt_11_1_ = (new BlockPos.Mutable(lvt_8_1_)).move(lvt_9_1_, 0, lvt_10_1_);
                        if (!p_222696_1_.isAreaLoaded(lvt_11_1_.getX() - 10, lvt_11_1_.getY() - 10, lvt_11_1_.getZ() - 10, lvt_11_1_.getX() + 10, lvt_11_1_.getY() + 10, lvt_11_1_.getZ() + 10)) {
                           return 0;
                        } else {
                           Biome lvt_12_1_ = p_222696_1_.func_226691_t_(lvt_11_1_);
                           Biome.Category lvt_13_1_ = lvt_12_1_.getCategory();
                           if (lvt_13_1_ == Biome.Category.MUSHROOM) {
                              return 0;
                           } else {
                              int lvt_14_1_ = 0;
                              int lvt_15_1_ = (int)Math.ceil((double)p_222696_1_.getDifficultyForLocation(lvt_11_1_).getAdditionalDifficulty()) + 1;

                              for(int lvt_16_1_ = 0; lvt_16_1_ < lvt_15_1_; ++lvt_16_1_) {
                                 ++lvt_14_1_;
                                 lvt_11_1_.setY(p_222696_1_.getHeight(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, lvt_11_1_).getY());
                                 if (lvt_16_1_ == 0) {
                                    if (!this.func_222695_a(p_222696_1_, lvt_11_1_, lvt_4_1_, true)) {
                                       break;
                                    }
                                 } else {
                                    this.func_222695_a(p_222696_1_, lvt_11_1_, lvt_4_1_, false);
                                 }

                                 lvt_11_1_.func_223471_o(lvt_11_1_.getX() + lvt_4_1_.nextInt(5) - lvt_4_1_.nextInt(5));
                                 lvt_11_1_.func_223472_q(lvt_11_1_.getZ() + lvt_4_1_.nextInt(5) - lvt_4_1_.nextInt(5));
                              }

                              return lvt_14_1_;
                           }
                        }
                     }
                  }
               }
            } else {
               return 0;
            }
         }
      }
   }

   private boolean func_222695_a(World p_222695_1_, BlockPos p_222695_2_, Random p_222695_3_, boolean p_222695_4_) {
      BlockState lvt_5_1_ = p_222695_1_.getBlockState(p_222695_2_);
      if (!WorldEntitySpawner.isSpawnableSpace(p_222695_1_, p_222695_2_, lvt_5_1_, lvt_5_1_.getFluidState())) {
         return false;
      } else if (!PatrollerEntity.func_223330_b(EntityType.PILLAGER, p_222695_1_, SpawnReason.PATROL, p_222695_2_, p_222695_3_)) {
         return false;
      } else {
         PatrollerEntity lvt_6_1_ = (PatrollerEntity)EntityType.PILLAGER.create(p_222695_1_);
         if (lvt_6_1_ != null) {
            if (p_222695_4_) {
               lvt_6_1_.setLeader(true);
               lvt_6_1_.resetPatrolTarget();
            }

            lvt_6_1_.setPosition((double)p_222695_2_.getX(), (double)p_222695_2_.getY(), (double)p_222695_2_.getZ());
            lvt_6_1_.onInitialSpawn(p_222695_1_, p_222695_1_.getDifficultyForLocation(p_222695_2_), SpawnReason.PATROL, (ILivingEntityData)null, (CompoundNBT)null);
            p_222695_1_.addEntity(lvt_6_1_);
            return true;
         } else {
            return false;
         }
      }
   }
}
