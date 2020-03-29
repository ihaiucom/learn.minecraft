package net.minecraft.world.spawner;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntitySpawnPlacementRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.ResourceLocationException;
import net.minecraft.util.StringUtils;
import net.minecraft.util.WeightedRandom;
import net.minecraft.util.WeightedSpawnerEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.ForgeEventFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class AbstractSpawner {
   private static final Logger LOGGER = LogManager.getLogger();
   private int spawnDelay = 20;
   private final List<WeightedSpawnerEntity> potentialSpawns = Lists.newArrayList();
   private WeightedSpawnerEntity spawnData = new WeightedSpawnerEntity();
   private double mobRotation;
   private double prevMobRotation;
   private int minSpawnDelay = 200;
   private int maxSpawnDelay = 800;
   private int spawnCount = 4;
   @Nullable
   private Entity cachedEntity;
   private int maxNearbyEntities = 6;
   private int activatingRangeFromPlayer = 16;
   private int spawnRange = 4;

   @Nullable
   private ResourceLocation getEntityId() {
      String s = this.spawnData.getNbt().getString("id");

      try {
         return StringUtils.isNullOrEmpty(s) ? null : new ResourceLocation(s);
      } catch (ResourceLocationException var4) {
         BlockPos blockpos = this.getSpawnerPosition();
         LOGGER.warn("Invalid entity id '{}' at spawner {}:[{},{},{}]", s, this.getWorld().dimension.getType(), blockpos.getX(), blockpos.getY(), blockpos.getZ());
         return null;
      }
   }

   public void setEntityType(EntityType<?> p_200876_1_) {
      this.spawnData.getNbt().putString("id", Registry.ENTITY_TYPE.getKey(p_200876_1_).toString());
   }

   private boolean isActivated() {
      BlockPos blockpos = this.getSpawnerPosition();
      return this.getWorld().isPlayerWithin((double)blockpos.getX() + 0.5D, (double)blockpos.getY() + 0.5D, (double)blockpos.getZ() + 0.5D, (double)this.activatingRangeFromPlayer);
   }

   public void tick() {
      if (!this.isActivated()) {
         this.prevMobRotation = this.mobRotation;
      } else {
         World world = this.getWorld();
         BlockPos blockpos = this.getSpawnerPosition();
         if (world.isRemote) {
            double d3 = (double)blockpos.getX() + (double)world.rand.nextFloat();
            double d4 = (double)blockpos.getY() + (double)world.rand.nextFloat();
            double d5 = (double)blockpos.getZ() + (double)world.rand.nextFloat();
            world.addParticle(ParticleTypes.SMOKE, d3, d4, d5, 0.0D, 0.0D, 0.0D);
            world.addParticle(ParticleTypes.FLAME, d3, d4, d5, 0.0D, 0.0D, 0.0D);
            if (this.spawnDelay > 0) {
               --this.spawnDelay;
            }

            this.prevMobRotation = this.mobRotation;
            this.mobRotation = (this.mobRotation + (double)(1000.0F / ((float)this.spawnDelay + 200.0F))) % 360.0D;
         } else {
            if (this.spawnDelay == -1) {
               this.resetTimer();
            }

            if (this.spawnDelay > 0) {
               --this.spawnDelay;
               return;
            }

            boolean flag = false;
            int i = 0;

            while(true) {
               if (i >= this.spawnCount) {
                  if (flag) {
                     this.resetTimer();
                  }
                  break;
               }

               CompoundNBT compoundnbt = this.spawnData.getNbt();
               Optional<EntityType<?>> optional = EntityType.readEntityType(compoundnbt);
               if (!optional.isPresent()) {
                  this.resetTimer();
                  return;
               }

               ListNBT listnbt = compoundnbt.getList("Pos", 6);
               int j = listnbt.size();
               double d0 = j >= 1 ? listnbt.getDouble(0) : (double)blockpos.getX() + (world.rand.nextDouble() - world.rand.nextDouble()) * (double)this.spawnRange + 0.5D;
               double d1 = j >= 2 ? listnbt.getDouble(1) : (double)(blockpos.getY() + world.rand.nextInt(3) - 1);
               double d2 = j >= 3 ? listnbt.getDouble(2) : (double)blockpos.getZ() + (world.rand.nextDouble() - world.rand.nextDouble()) * (double)this.spawnRange + 0.5D;
               if (world.func_226664_a_(((EntityType)optional.get()).func_220328_a(d0, d1, d2)) && EntitySpawnPlacementRegistry.func_223515_a((EntityType)optional.get(), world.getWorld(), SpawnReason.SPAWNER, new BlockPos(d0, d1, d2), world.getRandom())) {
                  label90: {
                     Entity entity = EntityType.func_220335_a(compoundnbt, world, (p_lambda$tick$0_6_) -> {
                        p_lambda$tick$0_6_.setLocationAndAngles(d0, d1, d2, p_lambda$tick$0_6_.rotationYaw, p_lambda$tick$0_6_.rotationPitch);
                        return p_lambda$tick$0_6_;
                     });
                     if (entity == null) {
                        this.resetTimer();
                        return;
                     }

                     int k = world.getEntitiesWithinAABB(entity.getClass(), (new AxisAlignedBB((double)blockpos.getX(), (double)blockpos.getY(), (double)blockpos.getZ(), (double)(blockpos.getX() + 1), (double)(blockpos.getY() + 1), (double)(blockpos.getZ() + 1))).grow((double)this.spawnRange)).size();
                     if (k >= this.maxNearbyEntities) {
                        this.resetTimer();
                        return;
                     }

                     entity.setLocationAndAngles(entity.func_226277_ct_(), entity.func_226278_cu_(), entity.func_226281_cx_(), world.rand.nextFloat() * 360.0F, 0.0F);
                     if (entity instanceof MobEntity) {
                        MobEntity mobentity = (MobEntity)entity;
                        if (!ForgeEventFactory.canEntitySpawnSpawner(mobentity, world, (float)entity.func_226277_ct_(), (float)entity.func_226278_cu_(), (float)entity.func_226281_cx_(), this)) {
                           break label90;
                        }

                        if (this.spawnData.getNbt().size() == 1 && this.spawnData.getNbt().contains("id", 8)) {
                           ((MobEntity)entity).onInitialSpawn(world, world.getDifficultyForLocation(new BlockPos(entity)), SpawnReason.SPAWNER, (ILivingEntityData)null, (CompoundNBT)null);
                        }
                     }

                     this.func_221409_a(entity);
                     world.playEvent(2004, blockpos, 0);
                     if (entity instanceof MobEntity) {
                        ((MobEntity)entity).spawnExplosionParticle();
                     }

                     flag = true;
                  }
               }

               ++i;
            }
         }
      }

   }

   private void func_221409_a(Entity p_221409_1_) {
      if (this.getWorld().addEntity(p_221409_1_)) {
         Iterator var2 = p_221409_1_.getPassengers().iterator();

         while(var2.hasNext()) {
            Entity entity = (Entity)var2.next();
            this.func_221409_a(entity);
         }
      }

   }

   private void resetTimer() {
      if (this.maxSpawnDelay <= this.minSpawnDelay) {
         this.spawnDelay = this.minSpawnDelay;
      } else {
         int i = this.maxSpawnDelay - this.minSpawnDelay;
         this.spawnDelay = this.minSpawnDelay + this.getWorld().rand.nextInt(i);
      }

      if (!this.potentialSpawns.isEmpty()) {
         this.setNextSpawnData((WeightedSpawnerEntity)WeightedRandom.getRandomItem(this.getWorld().rand, this.potentialSpawns));
      }

      this.broadcastEvent(1);
   }

   public void read(CompoundNBT p_98270_1_) {
      this.spawnDelay = p_98270_1_.getShort("Delay");
      this.potentialSpawns.clear();
      if (p_98270_1_.contains("SpawnPotentials", 9)) {
         ListNBT listnbt = p_98270_1_.getList("SpawnPotentials", 10);

         for(int i = 0; i < listnbt.size(); ++i) {
            this.potentialSpawns.add(new WeightedSpawnerEntity(listnbt.getCompound(i)));
         }
      }

      if (p_98270_1_.contains("SpawnData", 10)) {
         this.setNextSpawnData(new WeightedSpawnerEntity(1, p_98270_1_.getCompound("SpawnData")));
      } else if (!this.potentialSpawns.isEmpty()) {
         this.setNextSpawnData((WeightedSpawnerEntity)WeightedRandom.getRandomItem(this.getWorld().rand, this.potentialSpawns));
      }

      if (p_98270_1_.contains("MinSpawnDelay", 99)) {
         this.minSpawnDelay = p_98270_1_.getShort("MinSpawnDelay");
         this.maxSpawnDelay = p_98270_1_.getShort("MaxSpawnDelay");
         this.spawnCount = p_98270_1_.getShort("SpawnCount");
      }

      if (p_98270_1_.contains("MaxNearbyEntities", 99)) {
         this.maxNearbyEntities = p_98270_1_.getShort("MaxNearbyEntities");
         this.activatingRangeFromPlayer = p_98270_1_.getShort("RequiredPlayerRange");
      }

      if (p_98270_1_.contains("SpawnRange", 99)) {
         this.spawnRange = p_98270_1_.getShort("SpawnRange");
      }

      if (this.getWorld() != null) {
         this.cachedEntity = null;
      }

   }

   public CompoundNBT write(CompoundNBT p_189530_1_) {
      ResourceLocation resourcelocation = this.getEntityId();
      if (resourcelocation == null) {
         return p_189530_1_;
      } else {
         p_189530_1_.putShort("Delay", (short)this.spawnDelay);
         p_189530_1_.putShort("MinSpawnDelay", (short)this.minSpawnDelay);
         p_189530_1_.putShort("MaxSpawnDelay", (short)this.maxSpawnDelay);
         p_189530_1_.putShort("SpawnCount", (short)this.spawnCount);
         p_189530_1_.putShort("MaxNearbyEntities", (short)this.maxNearbyEntities);
         p_189530_1_.putShort("RequiredPlayerRange", (short)this.activatingRangeFromPlayer);
         p_189530_1_.putShort("SpawnRange", (short)this.spawnRange);
         p_189530_1_.put("SpawnData", this.spawnData.getNbt().copy());
         ListNBT listnbt = new ListNBT();
         if (this.potentialSpawns.isEmpty()) {
            listnbt.add(this.spawnData.toCompoundTag());
         } else {
            Iterator var4 = this.potentialSpawns.iterator();

            while(var4.hasNext()) {
               WeightedSpawnerEntity weightedspawnerentity = (WeightedSpawnerEntity)var4.next();
               listnbt.add(weightedspawnerentity.toCompoundTag());
            }
         }

         p_189530_1_.put("SpawnPotentials", listnbt);
         return p_189530_1_;
      }
   }

   @Nullable
   @OnlyIn(Dist.CLIENT)
   public Entity getCachedEntity() {
      if (this.cachedEntity == null) {
         this.cachedEntity = EntityType.func_220335_a(this.spawnData.getNbt(), this.getWorld(), Function.identity());
         if (this.spawnData.getNbt().size() == 1 && this.spawnData.getNbt().contains("id", 8) && this.cachedEntity instanceof MobEntity) {
            ((MobEntity)this.cachedEntity).onInitialSpawn(this.getWorld(), this.getWorld().getDifficultyForLocation(new BlockPos(this.cachedEntity)), SpawnReason.SPAWNER, (ILivingEntityData)null, (CompoundNBT)null);
         }
      }

      return this.cachedEntity;
   }

   public boolean setDelayToMin(int p_98268_1_) {
      if (p_98268_1_ == 1 && this.getWorld().isRemote) {
         this.spawnDelay = this.minSpawnDelay;
         return true;
      } else {
         return false;
      }
   }

   public void setNextSpawnData(WeightedSpawnerEntity p_184993_1_) {
      this.spawnData = p_184993_1_;
   }

   public abstract void broadcastEvent(int var1);

   public abstract World getWorld();

   public abstract BlockPos getSpawnerPosition();

   @OnlyIn(Dist.CLIENT)
   public double getMobRotation() {
      return this.mobRotation;
   }

   @OnlyIn(Dist.CLIENT)
   public double getPrevMobRotation() {
      return this.prevMobRotation;
   }

   @Nullable
   public Entity getSpawnerEntity() {
      return null;
   }
}
