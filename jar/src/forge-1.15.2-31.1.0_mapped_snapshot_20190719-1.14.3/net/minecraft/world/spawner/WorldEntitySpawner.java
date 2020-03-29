package net.minecraft.world.spawner;

import java.util.List;
import java.util.Objects;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntitySpawnPlacementRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.IFluidState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.pathfinding.PathType;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.WeightedRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.event.ForgeEventFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class WorldEntitySpawner {
   private static final Logger LOGGER = LogManager.getLogger();

   public static void func_226701_a_(EntityClassification p_226701_0_, ServerWorld p_226701_1_, Chunk p_226701_2_, BlockPos p_226701_3_) {
      ChunkGenerator<?> chunkgenerator = p_226701_1_.getChunkProvider().getChunkGenerator();
      int i = 0;
      BlockPos blockpos = getRandomHeight(p_226701_1_, p_226701_2_);
      int j = blockpos.getX();
      int k = blockpos.getY();
      int l = blockpos.getZ();
      if (k >= 1) {
         BlockState blockstate = p_226701_2_.getBlockState(blockpos);
         if (!blockstate.isNormalCube(p_226701_2_, blockpos)) {
            BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();

            for(int i1 = 0; i1 < 3; ++i1) {
               int j1 = j;
               int k1 = l;
               int l1 = true;
               Biome.SpawnListEntry biome$spawnlistentry = null;
               ILivingEntityData ilivingentitydata = null;
               int i2 = MathHelper.ceil(Math.random() * 4.0D);
               int j2 = 0;

               for(int k2 = 0; k2 < i2; ++k2) {
                  j1 += p_226701_1_.rand.nextInt(6) - p_226701_1_.rand.nextInt(6);
                  k1 += p_226701_1_.rand.nextInt(6) - p_226701_1_.rand.nextInt(6);
                  blockpos$mutable.setPos(j1, k, k1);
                  float f = (float)j1 + 0.5F;
                  float f1 = (float)k1 + 0.5F;
                  PlayerEntity playerentity = p_226701_1_.getClosestPlayer((double)f, (double)f1, -1.0D);
                  if (playerentity != null) {
                     double d0 = playerentity.getDistanceSq((double)f, (double)k, (double)f1);
                     if (d0 > 576.0D && !p_226701_3_.withinDistance(new Vec3d((double)f, (double)k, (double)f1), 24.0D)) {
                        ChunkPos chunkpos = new ChunkPos(blockpos$mutable);
                        if (Objects.equals(chunkpos, p_226701_2_.getPos()) || p_226701_1_.getChunkProvider().isChunkLoaded(chunkpos)) {
                           if (biome$spawnlistentry == null) {
                              biome$spawnlistentry = getSpawnList(chunkgenerator, p_226701_0_, (Random)p_226701_1_.rand, blockpos$mutable, p_226701_1_);
                              if (biome$spawnlistentry == null) {
                                 break;
                              }

                              i2 = biome$spawnlistentry.minGroupCount + p_226701_1_.rand.nextInt(1 + biome$spawnlistentry.maxGroupCount - biome$spawnlistentry.minGroupCount);
                           }

                           if (biome$spawnlistentry.entityType.getClassification() != EntityClassification.MISC && (biome$spawnlistentry.entityType.func_225437_d() || d0 <= 16384.0D)) {
                              EntityType<?> entitytype = biome$spawnlistentry.entityType;
                              if (entitytype.isSummonable() && getSpawnList(chunkgenerator, p_226701_0_, (Biome.SpawnListEntry)biome$spawnlistentry, blockpos$mutable, p_226701_1_)) {
                                 EntitySpawnPlacementRegistry.PlacementType entityspawnplacementregistry$placementtype = EntitySpawnPlacementRegistry.getPlacementType(entitytype);
                                 if (canCreatureTypeSpawnAtLocation(entityspawnplacementregistry$placementtype, p_226701_1_, blockpos$mutable, entitytype) && EntitySpawnPlacementRegistry.func_223515_a(entitytype, p_226701_1_, SpawnReason.NATURAL, blockpos$mutable, p_226701_1_.rand) && p_226701_1_.func_226664_a_(entitytype.func_220328_a((double)f, (double)k, (double)f1))) {
                                    MobEntity mobentity;
                                    try {
                                       Entity entity = entitytype.create(p_226701_1_);
                                       if (!(entity instanceof MobEntity)) {
                                          throw new IllegalStateException("Trying to spawn a non-mob: " + Registry.ENTITY_TYPE.getKey(entitytype));
                                       }

                                       mobentity = (MobEntity)entity;
                                    } catch (Exception var31) {
                                       LOGGER.warn("Failed to create mob", var31);
                                       return;
                                    }

                                    mobentity.setLocationAndAngles((double)f, (double)k, (double)f1, p_226701_1_.rand.nextFloat() * 360.0F, 0.0F);
                                    int canSpawn = ForgeHooks.canEntitySpawn(mobentity, p_226701_1_, (double)f, (double)k, (double)f1, (AbstractSpawner)null, SpawnReason.NATURAL);
                                    if (canSpawn != -1 && (canSpawn != 0 || (d0 <= 16384.0D || !mobentity.canDespawn(d0)) && mobentity.canSpawn(p_226701_1_, SpawnReason.NATURAL) && mobentity.isNotColliding(p_226701_1_))) {
                                       if (!ForgeEventFactory.doSpecialSpawn(mobentity, p_226701_1_, f, (float)k, f1, (AbstractSpawner)null, SpawnReason.NATURAL)) {
                                          ilivingentitydata = mobentity.onInitialSpawn(p_226701_1_, p_226701_1_.getDifficultyForLocation(new BlockPos(mobentity)), SpawnReason.NATURAL, ilivingentitydata, (CompoundNBT)null);
                                       }

                                       ++i;
                                       ++j2;
                                       p_226701_1_.addEntity(mobentity);
                                       if (i >= ForgeEventFactory.getMaxSpawnPackSize(mobentity)) {
                                          return;
                                       }

                                       if (mobentity.func_204209_c(j2)) {
                                          break;
                                       }
                                    }
                                 }
                              }
                           }
                        }
                     }
                  }
               }
            }
         }
      }

   }

   @Nullable
   private static Biome.SpawnListEntry getSpawnList(ChunkGenerator<?> p_getSpawnList_0_, EntityClassification p_getSpawnList_1_, Random p_getSpawnList_2_, BlockPos p_getSpawnList_3_, World p_getSpawnList_4_) {
      List<Biome.SpawnListEntry> list = p_getSpawnList_0_.getPossibleCreatures(p_getSpawnList_1_, p_getSpawnList_3_);
      list = ForgeEventFactory.getPotentialSpawns(p_getSpawnList_4_, p_getSpawnList_1_, p_getSpawnList_3_, list);
      return list.isEmpty() ? null : (Biome.SpawnListEntry)WeightedRandom.getRandomItem(p_getSpawnList_2_, list);
   }

   private static boolean getSpawnList(ChunkGenerator<?> p_getSpawnList_0_, EntityClassification p_getSpawnList_1_, Biome.SpawnListEntry p_getSpawnList_2_, BlockPos p_getSpawnList_3_, World p_getSpawnList_4_) {
      List<Biome.SpawnListEntry> list = p_getSpawnList_0_.getPossibleCreatures(p_getSpawnList_1_, p_getSpawnList_3_);
      list = ForgeEventFactory.getPotentialSpawns(p_getSpawnList_4_, p_getSpawnList_1_, p_getSpawnList_3_, list);
      return list.isEmpty() ? false : list.contains(p_getSpawnList_2_);
   }

   private static BlockPos getRandomHeight(World p_222262_0_, Chunk p_222262_1_) {
      ChunkPos chunkpos = p_222262_1_.getPos();
      int i = chunkpos.getXStart() + p_222262_0_.rand.nextInt(16);
      int j = chunkpos.getZStart() + p_222262_0_.rand.nextInt(16);
      int k = p_222262_1_.getTopBlockY(Heightmap.Type.WORLD_SURFACE, i, j) + 1;
      int l = p_222262_0_.rand.nextInt(k + 1);
      return new BlockPos(i, l, j);
   }

   public static boolean isSpawnableSpace(IBlockReader p_222266_0_, BlockPos p_222266_1_, BlockState p_222266_2_, IFluidState p_222266_3_) {
      if (p_222266_2_.func_224756_o(p_222266_0_, p_222266_1_)) {
         return false;
      } else if (p_222266_2_.canProvidePower()) {
         return false;
      } else if (!p_222266_3_.isEmpty()) {
         return false;
      } else {
         return !p_222266_2_.isIn(BlockTags.RAILS);
      }
   }

   public static boolean canCreatureTypeSpawnAtLocation(EntitySpawnPlacementRegistry.PlacementType p_209382_0_, IWorldReader p_209382_1_, BlockPos p_209382_2_, @Nullable EntityType<?> p_209382_3_) {
      if (p_209382_0_ == EntitySpawnPlacementRegistry.PlacementType.NO_RESTRICTIONS) {
         return true;
      } else {
         return p_209382_3_ != null && p_209382_1_.getWorldBorder().contains(p_209382_2_) ? p_209382_0_.canSpawnAt(p_209382_1_, p_209382_2_, p_209382_3_) : false;
      }
   }

   public static boolean canSpawnAtBody(EntitySpawnPlacementRegistry.PlacementType p_canSpawnAtBody_0_, IWorldReader p_canSpawnAtBody_1_, BlockPos p_canSpawnAtBody_2_, @Nullable EntityType<?> p_canSpawnAtBody_3_) {
      BlockState blockstate = p_canSpawnAtBody_1_.getBlockState(p_canSpawnAtBody_2_);
      IFluidState ifluidstate = p_canSpawnAtBody_1_.getFluidState(p_canSpawnAtBody_2_);
      BlockPos blockpos = p_canSpawnAtBody_2_.up();
      BlockPos blockpos1 = p_canSpawnAtBody_2_.down();
      switch(p_canSpawnAtBody_0_) {
      case IN_WATER:
         return ifluidstate.isTagged(FluidTags.WATER) && p_canSpawnAtBody_1_.getFluidState(blockpos1).isTagged(FluidTags.WATER) && !p_canSpawnAtBody_1_.getBlockState(blockpos).isNormalCube(p_canSpawnAtBody_1_, blockpos);
      case ON_GROUND:
      default:
         BlockState blockstate1 = p_canSpawnAtBody_1_.getBlockState(blockpos1);
         if (!blockstate1.canCreatureSpawn(p_canSpawnAtBody_1_, blockpos1, p_canSpawnAtBody_0_, p_canSpawnAtBody_3_)) {
            return false;
         } else {
            return isSpawnableSpace(p_canSpawnAtBody_1_, p_canSpawnAtBody_2_, blockstate, ifluidstate) && isSpawnableSpace(p_canSpawnAtBody_1_, blockpos, p_canSpawnAtBody_1_.getBlockState(blockpos), p_canSpawnAtBody_1_.getFluidState(blockpos));
         }
      }
   }

   public static void performWorldGenSpawning(IWorld p_77191_0_, Biome p_77191_1_, int p_77191_2_, int p_77191_3_, Random p_77191_4_) {
      List<Biome.SpawnListEntry> list = p_77191_1_.getSpawns(EntityClassification.CREATURE);
      if (!list.isEmpty()) {
         int i = p_77191_2_ << 4;
         int j = p_77191_3_ << 4;

         while(p_77191_4_.nextFloat() < p_77191_1_.getSpawningChance()) {
            Biome.SpawnListEntry biome$spawnlistentry = (Biome.SpawnListEntry)WeightedRandom.getRandomItem(p_77191_4_, list);
            int k = biome$spawnlistentry.minGroupCount + p_77191_4_.nextInt(1 + biome$spawnlistentry.maxGroupCount - biome$spawnlistentry.minGroupCount);
            ILivingEntityData ilivingentitydata = null;
            int l = i + p_77191_4_.nextInt(16);
            int i1 = j + p_77191_4_.nextInt(16);
            int j1 = l;
            int k1 = i1;

            for(int l1 = 0; l1 < k; ++l1) {
               boolean flag = false;

               for(int i2 = 0; !flag && i2 < 4; ++i2) {
                  BlockPos blockpos = getTopSolidOrLiquidBlock(p_77191_0_, biome$spawnlistentry.entityType, l, i1);
                  if (biome$spawnlistentry.entityType.isSummonable() && canCreatureTypeSpawnAtLocation(EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, p_77191_0_, blockpos, biome$spawnlistentry.entityType)) {
                     float f = biome$spawnlistentry.entityType.getWidth();
                     double d0 = MathHelper.clamp((double)l, (double)i + (double)f, (double)i + 16.0D - (double)f);
                     double d1 = MathHelper.clamp((double)i1, (double)j + (double)f, (double)j + 16.0D - (double)f);
                     if (!p_77191_0_.func_226664_a_(biome$spawnlistentry.entityType.func_220328_a(d0, (double)blockpos.getY(), d1)) || !EntitySpawnPlacementRegistry.func_223515_a(biome$spawnlistentry.entityType, p_77191_0_, SpawnReason.CHUNK_GENERATION, new BlockPos(d0, (double)blockpos.getY(), d1), p_77191_0_.getRandom())) {
                        continue;
                     }

                     Entity entity;
                     try {
                        entity = biome$spawnlistentry.entityType.create(p_77191_0_.getWorld());
                     } catch (Exception var26) {
                        LOGGER.warn("Failed to create mob", var26);
                        continue;
                     }

                     entity.setLocationAndAngles(d0, (double)blockpos.getY(), d1, p_77191_4_.nextFloat() * 360.0F, 0.0F);
                     if (entity instanceof MobEntity) {
                        MobEntity mobentity = (MobEntity)entity;
                        if (ForgeHooks.canEntitySpawn(mobentity, p_77191_0_, d0, (double)blockpos.getY(), d1, (AbstractSpawner)null, SpawnReason.CHUNK_GENERATION) == -1) {
                           continue;
                        }

                        if (mobentity.canSpawn(p_77191_0_, SpawnReason.CHUNK_GENERATION) && mobentity.isNotColliding(p_77191_0_)) {
                           ilivingentitydata = mobentity.onInitialSpawn(p_77191_0_, p_77191_0_.getDifficultyForLocation(new BlockPos(mobentity)), SpawnReason.CHUNK_GENERATION, ilivingentitydata, (CompoundNBT)null);
                           p_77191_0_.addEntity(mobentity);
                           flag = true;
                        }
                     }
                  }

                  l += p_77191_4_.nextInt(5) - p_77191_4_.nextInt(5);

                  for(i1 += p_77191_4_.nextInt(5) - p_77191_4_.nextInt(5); l < i || l >= i + 16 || i1 < j || i1 >= j + 16; i1 = k1 + p_77191_4_.nextInt(5) - p_77191_4_.nextInt(5)) {
                     l = j1 + p_77191_4_.nextInt(5) - p_77191_4_.nextInt(5);
                  }
               }
            }
         }
      }

   }

   private static BlockPos getTopSolidOrLiquidBlock(IWorldReader p_208498_0_, @Nullable EntityType<?> p_208498_1_, int p_208498_2_, int p_208498_3_) {
      BlockPos blockpos = new BlockPos(p_208498_2_, p_208498_0_.getHeight(EntitySpawnPlacementRegistry.func_209342_b(p_208498_1_), p_208498_2_, p_208498_3_), p_208498_3_);
      BlockPos blockpos1 = blockpos.down();
      return p_208498_0_.getBlockState(blockpos1).allowsMovement(p_208498_0_, blockpos1, PathType.LAND) ? blockpos1 : blockpos;
   }
}
