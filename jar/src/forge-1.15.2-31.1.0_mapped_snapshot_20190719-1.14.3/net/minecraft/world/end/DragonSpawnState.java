package net.minecraft.world.end;

import com.google.common.collect.ImmutableList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EnderCrystalEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.gen.feature.EndSpikeFeature;
import net.minecraft.world.gen.feature.EndSpikeFeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.server.ServerWorld;

public enum DragonSpawnState {
   START {
      public void process(ServerWorld p_186079_1_, DragonFightManager p_186079_2_, List<EnderCrystalEntity> p_186079_3_, int p_186079_4_, BlockPos p_186079_5_) {
         BlockPos lvt_6_1_ = new BlockPos(0, 128, 0);
         Iterator var7 = p_186079_3_.iterator();

         while(var7.hasNext()) {
            EnderCrystalEntity lvt_8_1_ = (EnderCrystalEntity)var7.next();
            lvt_8_1_.setBeamTarget(lvt_6_1_);
         }

         p_186079_2_.setRespawnState(PREPARING_TO_SUMMON_PILLARS);
      }
   },
   PREPARING_TO_SUMMON_PILLARS {
      public void process(ServerWorld p_186079_1_, DragonFightManager p_186079_2_, List<EnderCrystalEntity> p_186079_3_, int p_186079_4_, BlockPos p_186079_5_) {
         if (p_186079_4_ < 100) {
            if (p_186079_4_ == 0 || p_186079_4_ == 50 || p_186079_4_ == 51 || p_186079_4_ == 52 || p_186079_4_ >= 95) {
               p_186079_1_.playEvent(3001, new BlockPos(0, 128, 0), 0);
            }
         } else {
            p_186079_2_.setRespawnState(SUMMONING_PILLARS);
         }

      }
   },
   SUMMONING_PILLARS {
      public void process(ServerWorld p_186079_1_, DragonFightManager p_186079_2_, List<EnderCrystalEntity> p_186079_3_, int p_186079_4_, BlockPos p_186079_5_) {
         int lvt_6_1_ = true;
         boolean lvt_7_1_ = p_186079_4_ % 40 == 0;
         boolean lvt_8_1_ = p_186079_4_ % 40 == 39;
         if (lvt_7_1_ || lvt_8_1_) {
            List<EndSpikeFeature.EndSpike> lvt_9_1_ = EndSpikeFeature.func_214554_a(p_186079_1_);
            int lvt_10_1_ = p_186079_4_ / 40;
            if (lvt_10_1_ < lvt_9_1_.size()) {
               EndSpikeFeature.EndSpike lvt_11_1_ = (EndSpikeFeature.EndSpike)lvt_9_1_.get(lvt_10_1_);
               if (lvt_7_1_) {
                  Iterator var12 = p_186079_3_.iterator();

                  while(var12.hasNext()) {
                     EnderCrystalEntity lvt_13_1_ = (EnderCrystalEntity)var12.next();
                     lvt_13_1_.setBeamTarget(new BlockPos(lvt_11_1_.getCenterX(), lvt_11_1_.getHeight() + 1, lvt_11_1_.getCenterZ()));
                  }
               } else {
                  int lvt_12_1_ = true;
                  Iterator var16 = BlockPos.getAllInBoxMutable(new BlockPos(lvt_11_1_.getCenterX() - 10, lvt_11_1_.getHeight() - 10, lvt_11_1_.getCenterZ() - 10), new BlockPos(lvt_11_1_.getCenterX() + 10, lvt_11_1_.getHeight() + 10, lvt_11_1_.getCenterZ() + 10)).iterator();

                  while(var16.hasNext()) {
                     BlockPos lvt_14_1_ = (BlockPos)var16.next();
                     p_186079_1_.removeBlock(lvt_14_1_, false);
                  }

                  p_186079_1_.createExplosion((Entity)null, (double)((float)lvt_11_1_.getCenterX() + 0.5F), (double)lvt_11_1_.getHeight(), (double)((float)lvt_11_1_.getCenterZ() + 0.5F), 5.0F, Explosion.Mode.DESTROY);
                  EndSpikeFeatureConfig lvt_13_2_ = new EndSpikeFeatureConfig(true, ImmutableList.of(lvt_11_1_), new BlockPos(0, 128, 0));
                  Feature.END_SPIKE.func_225566_b_(lvt_13_2_).place(p_186079_1_, p_186079_1_.getChunkProvider().getChunkGenerator(), new Random(), new BlockPos(lvt_11_1_.getCenterX(), 45, lvt_11_1_.getCenterZ()));
               }
            } else if (lvt_7_1_) {
               p_186079_2_.setRespawnState(SUMMONING_DRAGON);
            }
         }

      }
   },
   SUMMONING_DRAGON {
      public void process(ServerWorld p_186079_1_, DragonFightManager p_186079_2_, List<EnderCrystalEntity> p_186079_3_, int p_186079_4_, BlockPos p_186079_5_) {
         Iterator var6;
         EnderCrystalEntity lvt_7_2_;
         if (p_186079_4_ >= 100) {
            p_186079_2_.setRespawnState(END);
            p_186079_2_.resetSpikeCrystals();
            var6 = p_186079_3_.iterator();

            while(var6.hasNext()) {
               lvt_7_2_ = (EnderCrystalEntity)var6.next();
               lvt_7_2_.setBeamTarget((BlockPos)null);
               p_186079_1_.createExplosion(lvt_7_2_, lvt_7_2_.func_226277_ct_(), lvt_7_2_.func_226278_cu_(), lvt_7_2_.func_226281_cx_(), 6.0F, Explosion.Mode.NONE);
               lvt_7_2_.remove();
            }
         } else if (p_186079_4_ >= 80) {
            p_186079_1_.playEvent(3001, new BlockPos(0, 128, 0), 0);
         } else if (p_186079_4_ == 0) {
            var6 = p_186079_3_.iterator();

            while(var6.hasNext()) {
               lvt_7_2_ = (EnderCrystalEntity)var6.next();
               lvt_7_2_.setBeamTarget(new BlockPos(0, 128, 0));
            }
         } else if (p_186079_4_ < 5) {
            p_186079_1_.playEvent(3001, new BlockPos(0, 128, 0), 0);
         }

      }
   },
   END {
      public void process(ServerWorld p_186079_1_, DragonFightManager p_186079_2_, List<EnderCrystalEntity> p_186079_3_, int p_186079_4_, BlockPos p_186079_5_) {
      }
   };

   private DragonSpawnState() {
   }

   public abstract void process(ServerWorld var1, DragonFightManager var2, List<EnderCrystalEntity> var3, int var4, BlockPos var5);

   // $FF: synthetic method
   DragonSpawnState(Object p_i46671_3_) {
      this();
   }
}
