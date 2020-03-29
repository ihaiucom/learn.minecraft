package net.minecraft.world.spawner;

import java.util.Iterator;
import java.util.Optional;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.entity.EntitySpawnPlacementRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.merchant.villager.WanderingTraderEntity;
import net.minecraft.entity.passive.horse.TraderLlamaEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.village.PointOfInterestManager;
import net.minecraft.village.PointOfInterestType;
import net.minecraft.world.GameRules;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.WorldInfo;

public class WanderingTraderSpawner {
   private final Random random = new Random();
   private final ServerWorld world;
   private int field_221248_c;
   private int field_221249_d;
   private int field_221250_e;

   public WanderingTraderSpawner(ServerWorld p_i50177_1_) {
      this.world = p_i50177_1_;
      this.field_221248_c = 1200;
      WorldInfo lvt_2_1_ = p_i50177_1_.getWorldInfo();
      this.field_221249_d = lvt_2_1_.getWanderingTraderSpawnDelay();
      this.field_221250_e = lvt_2_1_.getWanderingTraderSpawnChance();
      if (this.field_221249_d == 0 && this.field_221250_e == 0) {
         this.field_221249_d = 24000;
         lvt_2_1_.setWanderingTraderSpawnDelay(this.field_221249_d);
         this.field_221250_e = 25;
         lvt_2_1_.setWanderingTraderSpawnChance(this.field_221250_e);
      }

   }

   public void tick() {
      if (this.world.getGameRules().getBoolean(GameRules.field_230128_E_)) {
         if (--this.field_221248_c <= 0) {
            this.field_221248_c = 1200;
            WorldInfo lvt_1_1_ = this.world.getWorldInfo();
            this.field_221249_d -= 1200;
            lvt_1_1_.setWanderingTraderSpawnDelay(this.field_221249_d);
            if (this.field_221249_d <= 0) {
               this.field_221249_d = 24000;
               if (this.world.getGameRules().getBoolean(GameRules.DO_MOB_SPAWNING)) {
                  int lvt_2_1_ = this.field_221250_e;
                  this.field_221250_e = MathHelper.clamp(this.field_221250_e + 25, 25, 75);
                  lvt_1_1_.setWanderingTraderSpawnChance(this.field_221250_e);
                  if (this.random.nextInt(100) <= lvt_2_1_) {
                     if (this.func_221245_b()) {
                        this.field_221250_e = 25;
                     }

                  }
               }
            }
         }
      }
   }

   private boolean func_221245_b() {
      PlayerEntity lvt_1_1_ = this.world.getRandomPlayer();
      if (lvt_1_1_ == null) {
         return true;
      } else if (this.random.nextInt(10) != 0) {
         return false;
      } else {
         BlockPos lvt_2_1_ = lvt_1_1_.getPosition();
         int lvt_3_1_ = true;
         PointOfInterestManager lvt_4_1_ = this.world.func_217443_B();
         Optional<BlockPos> lvt_5_1_ = lvt_4_1_.func_219127_a(PointOfInterestType.MEETING.func_221045_c(), (p_221241_0_) -> {
            return true;
         }, lvt_2_1_, 48, PointOfInterestManager.Status.ANY);
         BlockPos lvt_6_1_ = (BlockPos)lvt_5_1_.orElse(lvt_2_1_);
         BlockPos lvt_7_1_ = this.func_221244_a(lvt_6_1_, 48);
         if (lvt_7_1_ != null && this.func_226559_a_(lvt_7_1_)) {
            if (this.world.func_226691_t_(lvt_7_1_) == Biomes.THE_VOID) {
               return false;
            }

            WanderingTraderEntity lvt_8_1_ = (WanderingTraderEntity)EntityType.WANDERING_TRADER.spawn(this.world, (CompoundNBT)null, (ITextComponent)null, (PlayerEntity)null, lvt_7_1_, SpawnReason.EVENT, false, false);
            if (lvt_8_1_ != null) {
               for(int lvt_9_1_ = 0; lvt_9_1_ < 2; ++lvt_9_1_) {
                  this.func_221243_a(lvt_8_1_, 4);
               }

               this.world.getWorldInfo().setWanderingTraderId(lvt_8_1_.getUniqueID());
               lvt_8_1_.func_213728_s(48000);
               lvt_8_1_.func_213726_g(lvt_6_1_);
               lvt_8_1_.setHomePosAndDistance(lvt_6_1_, 16);
               return true;
            }
         }

         return false;
      }
   }

   private void func_221243_a(WanderingTraderEntity p_221243_1_, int p_221243_2_) {
      BlockPos lvt_3_1_ = this.func_221244_a(new BlockPos(p_221243_1_), p_221243_2_);
      if (lvt_3_1_ != null) {
         TraderLlamaEntity lvt_4_1_ = (TraderLlamaEntity)EntityType.TRADER_LLAMA.spawn(this.world, (CompoundNBT)null, (ITextComponent)null, (PlayerEntity)null, lvt_3_1_, SpawnReason.EVENT, false, false);
         if (lvt_4_1_ != null) {
            lvt_4_1_.setLeashHolder(p_221243_1_, true);
         }
      }
   }

   @Nullable
   private BlockPos func_221244_a(BlockPos p_221244_1_, int p_221244_2_) {
      BlockPos lvt_3_1_ = null;

      for(int lvt_4_1_ = 0; lvt_4_1_ < 10; ++lvt_4_1_) {
         int lvt_5_1_ = p_221244_1_.getX() + this.random.nextInt(p_221244_2_ * 2) - p_221244_2_;
         int lvt_6_1_ = p_221244_1_.getZ() + this.random.nextInt(p_221244_2_ * 2) - p_221244_2_;
         int lvt_7_1_ = this.world.getHeight(Heightmap.Type.WORLD_SURFACE, lvt_5_1_, lvt_6_1_);
         BlockPos lvt_8_1_ = new BlockPos(lvt_5_1_, lvt_7_1_, lvt_6_1_);
         if (WorldEntitySpawner.canCreatureTypeSpawnAtLocation(EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, this.world, lvt_8_1_, EntityType.WANDERING_TRADER)) {
            lvt_3_1_ = lvt_8_1_;
            break;
         }
      }

      return lvt_3_1_;
   }

   private boolean func_226559_a_(BlockPos p_226559_1_) {
      Iterator var2 = BlockPos.getAllInBoxMutable(p_226559_1_, p_226559_1_.add(1, 2, 1)).iterator();

      BlockPos lvt_3_1_;
      do {
         if (!var2.hasNext()) {
            return true;
         }

         lvt_3_1_ = (BlockPos)var2.next();
      } while(this.world.getBlockState(lvt_3_1_).getCollisionShape(this.world, lvt_3_1_).isEmpty());

      return false;
   }
}
