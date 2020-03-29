package net.minecraft.world.spawner;

import java.util.List;
import java.util.Random;
import net.minecraft.entity.EntitySpawnPlacementRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.village.PointOfInterestManager;
import net.minecraft.village.PointOfInterestType;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.server.ServerWorld;

public class CatSpawner {
   private int field_221125_a;

   public int tick(ServerWorld p_221124_1_, boolean p_221124_2_, boolean p_221124_3_) {
      if (p_221124_3_ && p_221124_1_.getGameRules().getBoolean(GameRules.DO_MOB_SPAWNING)) {
         --this.field_221125_a;
         if (this.field_221125_a > 0) {
            return 0;
         } else {
            this.field_221125_a = 1200;
            PlayerEntity lvt_4_1_ = p_221124_1_.getRandomPlayer();
            if (lvt_4_1_ == null) {
               return 0;
            } else {
               Random lvt_5_1_ = p_221124_1_.rand;
               int lvt_6_1_ = (8 + lvt_5_1_.nextInt(24)) * (lvt_5_1_.nextBoolean() ? -1 : 1);
               int lvt_7_1_ = (8 + lvt_5_1_.nextInt(24)) * (lvt_5_1_.nextBoolean() ? -1 : 1);
               BlockPos lvt_8_1_ = (new BlockPos(lvt_4_1_)).add(lvt_6_1_, 0, lvt_7_1_);
               if (!p_221124_1_.isAreaLoaded(lvt_8_1_.getX() - 10, lvt_8_1_.getY() - 10, lvt_8_1_.getZ() - 10, lvt_8_1_.getX() + 10, lvt_8_1_.getY() + 10, lvt_8_1_.getZ() + 10)) {
                  return 0;
               } else {
                  if (WorldEntitySpawner.canCreatureTypeSpawnAtLocation(EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, p_221124_1_, lvt_8_1_, EntityType.CAT)) {
                     if (p_221124_1_.func_217471_a(lvt_8_1_, 2)) {
                        return this.func_221121_a(p_221124_1_, lvt_8_1_);
                     }

                     if (Feature.SWAMP_HUT.isPositionInsideStructure(p_221124_1_, lvt_8_1_)) {
                        return this.func_221123_a(p_221124_1_, lvt_8_1_);
                     }
                  }

                  return 0;
               }
            }
         }
      } else {
         return 0;
      }
   }

   private int func_221121_a(ServerWorld p_221121_1_, BlockPos p_221121_2_) {
      int lvt_3_1_ = true;
      if (p_221121_1_.func_217443_B().func_219145_a(PointOfInterestType.HOME.func_221045_c(), p_221121_2_, 48, PointOfInterestManager.Status.IS_OCCUPIED) > 4L) {
         List<CatEntity> lvt_4_1_ = p_221121_1_.getEntitiesWithinAABB(CatEntity.class, (new AxisAlignedBB(p_221121_2_)).grow(48.0D, 8.0D, 48.0D));
         if (lvt_4_1_.size() < 5) {
            return this.func_221122_a(p_221121_2_, p_221121_1_);
         }
      }

      return 0;
   }

   private int func_221123_a(World p_221123_1_, BlockPos p_221123_2_) {
      int lvt_3_1_ = true;
      List<CatEntity> lvt_4_1_ = p_221123_1_.getEntitiesWithinAABB(CatEntity.class, (new AxisAlignedBB(p_221123_2_)).grow(16.0D, 8.0D, 16.0D));
      return lvt_4_1_.size() < 1 ? this.func_221122_a(p_221123_2_, p_221123_1_) : 0;
   }

   private int func_221122_a(BlockPos p_221122_1_, World p_221122_2_) {
      CatEntity lvt_3_1_ = (CatEntity)EntityType.CAT.create(p_221122_2_);
      if (lvt_3_1_ == null) {
         return 0;
      } else {
         lvt_3_1_.onInitialSpawn(p_221122_2_, p_221122_2_.getDifficultyForLocation(p_221122_1_), SpawnReason.NATURAL, (ILivingEntityData)null, (CompoundNBT)null);
         lvt_3_1_.moveToBlockPosAndAngles(p_221122_1_, 0.0F, 0.0F);
         p_221122_2_.addEntity(lvt_3_1_);
         return 1;
      }
   }
}
