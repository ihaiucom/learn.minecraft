package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Iterator;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityType;
import net.minecraft.tileentity.LockableLootTileEntity;
import net.minecraft.tileentity.MobSpawnerTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationSettings;
import net.minecraft.world.gen.feature.structure.StructurePiece;
import net.minecraft.world.storage.loot.LootTables;
import net.minecraftforge.common.DungeonHooks;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DungeonsFeature extends Feature<NoFeatureConfig> {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final EntityType<?>[] SPAWNERTYPES;
   private static final BlockState CAVE_AIR;

   public DungeonsFeature(Function<Dynamic<?>, ? extends NoFeatureConfig> p_i51477_1_) {
      super(p_i51477_1_);
   }

   public boolean place(IWorld p_212245_1_, ChunkGenerator<? extends GenerationSettings> p_212245_2_, Random p_212245_3_, BlockPos p_212245_4_, NoFeatureConfig p_212245_5_) {
      int i = true;
      int j = p_212245_3_.nextInt(2) + 2;
      int k = -j - 1;
      int l = j + 1;
      int i1 = true;
      int j1 = true;
      int k1 = p_212245_3_.nextInt(2) + 2;
      int l1 = -k1 - 1;
      int i2 = k1 + 1;
      int j2 = 0;

      int k3;
      int i4;
      int k4;
      BlockPos blockpos1;
      for(k3 = k; k3 <= l; ++k3) {
         for(i4 = -1; i4 <= 4; ++i4) {
            for(k4 = l1; k4 <= i2; ++k4) {
               blockpos1 = p_212245_4_.add(k3, i4, k4);
               Material material = p_212245_1_.getBlockState(blockpos1).getMaterial();
               boolean flag = material.isSolid();
               if (i4 == -1 && !flag) {
                  return false;
               }

               if (i4 == 4 && !flag) {
                  return false;
               }

               if ((k3 == k || k3 == l || k4 == l1 || k4 == i2) && i4 == 0 && p_212245_1_.isAirBlock(blockpos1) && p_212245_1_.isAirBlock(blockpos1.up())) {
                  ++j2;
               }
            }
         }
      }

      if (j2 >= 1 && j2 <= 5) {
         for(k3 = k; k3 <= l; ++k3) {
            for(i4 = 3; i4 >= -1; --i4) {
               for(k4 = l1; k4 <= i2; ++k4) {
                  blockpos1 = p_212245_4_.add(k3, i4, k4);
                  if (k3 != k && i4 != -1 && k4 != l1 && k3 != l && i4 != 4 && k4 != i2) {
                     if (p_212245_1_.getBlockState(blockpos1).getBlock() != Blocks.CHEST) {
                        p_212245_1_.setBlockState(blockpos1, CAVE_AIR, 2);
                     }
                  } else if (blockpos1.getY() >= 0 && !p_212245_1_.getBlockState(blockpos1.down()).getMaterial().isSolid()) {
                     p_212245_1_.setBlockState(blockpos1, CAVE_AIR, 2);
                  } else if (p_212245_1_.getBlockState(blockpos1).getMaterial().isSolid() && p_212245_1_.getBlockState(blockpos1).getBlock() != Blocks.CHEST) {
                     if (i4 == -1 && p_212245_3_.nextInt(4) != 0) {
                        p_212245_1_.setBlockState(blockpos1, Blocks.MOSSY_COBBLESTONE.getDefaultState(), 2);
                     } else {
                        p_212245_1_.setBlockState(blockpos1, Blocks.COBBLESTONE.getDefaultState(), 2);
                     }
                  }
               }
            }
         }

         for(k3 = 0; k3 < 2; ++k3) {
            for(i4 = 0; i4 < 3; ++i4) {
               k4 = p_212245_4_.getX() + p_212245_3_.nextInt(j * 2 + 1) - j;
               int i5 = p_212245_4_.getY();
               int j5 = p_212245_4_.getZ() + p_212245_3_.nextInt(k1 * 2 + 1) - k1;
               BlockPos blockpos2 = new BlockPos(k4, i5, j5);
               if (p_212245_1_.isAirBlock(blockpos2)) {
                  int j3 = 0;
                  Iterator var23 = Direction.Plane.HORIZONTAL.iterator();

                  while(var23.hasNext()) {
                     Direction direction = (Direction)var23.next();
                     if (p_212245_1_.getBlockState(blockpos2.offset(direction)).getMaterial().isSolid()) {
                        ++j3;
                     }
                  }

                  if (j3 == 1) {
                     p_212245_1_.setBlockState(blockpos2, StructurePiece.func_197528_a(p_212245_1_, blockpos2, Blocks.CHEST.getDefaultState()), 2);
                     LockableLootTileEntity.setLootTable(p_212245_1_, p_212245_3_, blockpos2, LootTables.CHESTS_SIMPLE_DUNGEON);
                     break;
                  }
               }
            }
         }

         p_212245_1_.setBlockState(p_212245_4_, Blocks.SPAWNER.getDefaultState(), 2);
         TileEntity tileentity = p_212245_1_.getTileEntity(p_212245_4_);
         if (tileentity instanceof MobSpawnerTileEntity) {
            ((MobSpawnerTileEntity)tileentity).getSpawnerBaseLogic().setEntityType(this.func_201043_a(p_212245_3_));
         } else {
            LOGGER.error("Failed to fetch mob spawner entity at ({}, {}, {})", p_212245_4_.getX(), p_212245_4_.getY(), p_212245_4_.getZ());
         }

         return true;
      } else {
         return false;
      }
   }

   private EntityType<?> func_201043_a(Random p_201043_1_) {
      return DungeonHooks.getRandomDungeonMob(p_201043_1_);
   }

   static {
      SPAWNERTYPES = new EntityType[]{EntityType.SKELETON, EntityType.ZOMBIE, EntityType.ZOMBIE, EntityType.SPIDER};
      CAVE_AIR = Blocks.CAVE_AIR.getDefaultState();
   }
}
