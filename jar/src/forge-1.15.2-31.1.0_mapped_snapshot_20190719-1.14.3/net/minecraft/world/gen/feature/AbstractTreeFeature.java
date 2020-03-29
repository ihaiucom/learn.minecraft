package net.minecraft.world.gen.feature;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mojang.datafixers.Dynamic;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.shapes.BitSetVoxelShapePart;
import net.minecraft.util.math.shapes.VoxelShapePart;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.IWorldWriter;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationSettings;
import net.minecraft.world.gen.IWorldGenerationBaseReader;
import net.minecraft.world.gen.IWorldGenerationReader;
import net.minecraft.world.gen.feature.template.Template;
import net.minecraftforge.common.IPlantable;

public abstract class AbstractTreeFeature<T extends BaseTreeFeatureConfig> extends Feature<T> {
   public AbstractTreeFeature(Function<Dynamic<?>, ? extends T> p_i225797_1_) {
      super(p_i225797_1_);
   }

   protected static boolean func_214587_a(IWorldGenerationBaseReader p_214587_0_, BlockPos p_214587_1_) {
      return p_214587_0_ instanceof IWorldReader ? p_214587_0_.hasBlockState(p_214587_1_, (p_lambda$func_214587_a$0_2_) -> {
         return p_lambda$func_214587_a$0_2_.canBeReplacedByLogs((IWorldReader)p_214587_0_, p_214587_1_);
      }) : p_214587_0_.hasBlockState(p_214587_1_, (p_lambda$func_214587_a$1_0_) -> {
         Block block = p_lambda$func_214587_a$1_0_.getBlock();
         return p_lambda$func_214587_a$1_0_.isAir() || p_lambda$func_214587_a$1_0_.isIn(BlockTags.LEAVES) || func_227250_b_(block) || block.isIn(BlockTags.LOGS) || block.isIn(BlockTags.SAPLINGS) || block == Blocks.VINE;
      });
   }

   public static boolean isAir(IWorldGenerationBaseReader p_214574_0_, BlockPos p_214574_1_) {
      return p_214574_0_ instanceof IBlockReader ? p_214574_0_.hasBlockState(p_214574_1_, (p_lambda$isAir$2_2_) -> {
         return p_lambda$isAir$2_2_.isAir((IBlockReader)p_214574_0_, p_214574_1_);
      }) : p_214574_0_.hasBlockState(p_214574_1_, BlockState::isAir);
   }

   protected static boolean isDirt(IWorldGenerationBaseReader p_214578_0_, BlockPos p_214578_1_) {
      return p_214578_0_.hasBlockState(p_214578_1_, (p_lambda$isDirt$3_0_) -> {
         Block block = p_lambda$isDirt$3_0_.getBlock();
         return func_227250_b_(block) && block != Blocks.GRASS_BLOCK && block != Blocks.MYCELIUM;
      });
   }

   protected static boolean func_227222_d_(IWorldGenerationBaseReader p_227222_0_, BlockPos p_227222_1_) {
      return p_227222_0_.hasBlockState(p_227222_1_, (p_lambda$func_227222_d_$4_0_) -> {
         return p_lambda$func_227222_d_$4_0_.getBlock() == Blocks.VINE;
      });
   }

   public static boolean isWater(IWorldGenerationBaseReader p_214571_0_, BlockPos p_214571_1_) {
      return p_214571_0_.hasBlockState(p_214571_1_, (p_lambda$isWater$5_0_) -> {
         return p_lambda$isWater$5_0_.getBlock() == Blocks.WATER;
      });
   }

   public static boolean isAirOrLeaves(IWorldGenerationBaseReader p_214572_0_, BlockPos p_214572_1_) {
      return p_214572_0_ instanceof IWorldReader ? p_214572_0_.hasBlockState(p_214572_1_, (p_lambda$isAirOrLeaves$6_2_) -> {
         return p_lambda$isAirOrLeaves$6_2_.canBeReplacedByLeaves((IWorldReader)p_214572_0_, p_214572_1_);
      }) : p_214572_0_.hasBlockState(p_214572_1_, (p_lambda$isAirOrLeaves$7_0_) -> {
         return p_lambda$isAirOrLeaves$7_0_.isAir() || p_lambda$isAirOrLeaves$7_0_.isIn(BlockTags.LEAVES);
      });
   }

   /** @deprecated */
   @Deprecated
   public static boolean isDirtOrGrassBlock(IWorldGenerationBaseReader p_214589_0_, BlockPos p_214589_1_) {
      return p_214589_0_.hasBlockState(p_214589_1_, (p_lambda$isDirtOrGrassBlock$8_0_) -> {
         return func_227250_b_(p_lambda$isDirtOrGrassBlock$8_0_.getBlock());
      });
   }

   protected static boolean isSoil(IWorldGenerationBaseReader p_isSoil_0_, BlockPos p_isSoil_1_, IPlantable p_isSoil_2_) {
      return p_isSoil_0_ instanceof IBlockReader && p_isSoil_2_ != null ? p_isSoil_0_.hasBlockState(p_isSoil_1_, (p_lambda$isSoil$9_3_) -> {
         return p_lambda$isSoil$9_3_.canSustainPlant((IBlockReader)p_isSoil_0_, p_isSoil_1_, Direction.UP, p_isSoil_2_);
      }) : isDirtOrGrassBlock(p_isSoil_0_, p_isSoil_1_);
   }

   /** @deprecated */
   @Deprecated
   protected static boolean isDirtOrGrassBlockOrFarmland(IWorldGenerationBaseReader p_214585_0_, BlockPos p_214585_1_) {
      return p_214585_0_.hasBlockState(p_214585_1_, (p_lambda$isDirtOrGrassBlockOrFarmland$10_0_) -> {
         Block block = p_lambda$isDirtOrGrassBlockOrFarmland$10_0_.getBlock();
         return func_227250_b_(block) || block == Blocks.FARMLAND;
      });
   }

   protected static boolean isSoilOrFarm(IWorldGenerationBaseReader p_isSoilOrFarm_0_, BlockPos p_isSoilOrFarm_1_, IPlantable p_isSoilOrFarm_2_) {
      return p_isSoilOrFarm_0_ instanceof IBlockReader && p_isSoilOrFarm_2_ != null ? p_isSoilOrFarm_0_.hasBlockState(p_isSoilOrFarm_1_, (p_lambda$isSoilOrFarm$11_3_) -> {
         return p_lambda$isSoilOrFarm$11_3_.canSustainPlant((IBlockReader)p_isSoilOrFarm_0_, p_isSoilOrFarm_1_, Direction.UP, p_isSoilOrFarm_2_);
      }) : isDirtOrGrassBlockOrFarmland(p_isSoilOrFarm_0_, p_isSoilOrFarm_1_);
   }

   public static boolean func_214576_j(IWorldGenerationBaseReader p_214576_0_, BlockPos p_214576_1_) {
      return p_214576_0_.hasBlockState(p_214576_1_, (p_lambda$func_214576_j$12_0_) -> {
         Material material = p_lambda$func_214576_j$12_0_.getMaterial();
         return material == Material.TALL_PLANTS;
      });
   }

   /** @deprecated */
   @Deprecated
   protected void func_214584_a(IWorldGenerationReader p_214584_1_, BlockPos p_214584_2_) {
      if (!isDirt(p_214584_1_, p_214584_2_)) {
         this.setBlockState(p_214584_1_, p_214584_2_, Blocks.DIRT.getDefaultState());
      }

   }

   protected boolean func_227216_a_(IWorldGenerationReader p_227216_1_, Random p_227216_2_, BlockPos p_227216_3_, Set<BlockPos> p_227216_4_, MutableBoundingBox p_227216_5_, BaseTreeFeatureConfig p_227216_6_) {
      if (!isAirOrLeaves(p_227216_1_, p_227216_3_) && !func_214576_j(p_227216_1_, p_227216_3_) && !isWater(p_227216_1_, p_227216_3_)) {
         return false;
      } else {
         this.func_227217_a_(p_227216_1_, p_227216_3_, p_227216_6_.field_227368_m_.func_225574_a_(p_227216_2_, p_227216_3_), p_227216_5_);
         p_227216_4_.add(p_227216_3_.toImmutable());
         return true;
      }
   }

   protected boolean func_227219_b_(IWorldGenerationReader p_227219_1_, Random p_227219_2_, BlockPos p_227219_3_, Set<BlockPos> p_227219_4_, MutableBoundingBox p_227219_5_, BaseTreeFeatureConfig p_227219_6_) {
      if (!isAirOrLeaves(p_227219_1_, p_227219_3_) && !func_214576_j(p_227219_1_, p_227219_3_) && !isWater(p_227219_1_, p_227219_3_)) {
         return false;
      } else {
         this.func_227217_a_(p_227219_1_, p_227219_3_, p_227219_6_.field_227369_n_.func_225574_a_(p_227219_2_, p_227219_3_), p_227219_5_);
         p_227219_4_.add(p_227219_3_.toImmutable());
         return true;
      }
   }

   protected void setDirtAt(IWorldGenerationReader p_setDirtAt_1_, BlockPos p_setDirtAt_2_, BlockPos p_setDirtAt_3_) {
      if (!(p_setDirtAt_1_ instanceof IWorld)) {
         this.func_214584_a(p_setDirtAt_1_, p_setDirtAt_2_);
      } else {
         ((IWorld)p_setDirtAt_1_).getBlockState(p_setDirtAt_2_).onPlantGrow((IWorld)p_setDirtAt_1_, p_setDirtAt_2_, p_setDirtAt_3_);
      }
   }

   protected void setBlockState(IWorldWriter p_202278_1_, BlockPos p_202278_2_, BlockState p_202278_3_) {
      this.func_208521_b(p_202278_1_, p_202278_2_, p_202278_3_);
   }

   protected final void func_227217_a_(IWorldWriter p_227217_1_, BlockPos p_227217_2_, BlockState p_227217_3_, MutableBoundingBox p_227217_4_) {
      this.func_208521_b(p_227217_1_, p_227217_2_, p_227217_3_);
      p_227217_4_.expandTo(new MutableBoundingBox(p_227217_2_, p_227217_2_));
   }

   private void func_208521_b(IWorldWriter p_208521_1_, BlockPos p_208521_2_, BlockState p_208521_3_) {
      p_208521_1_.setBlockState(p_208521_2_, p_208521_3_, 19);
   }

   public final boolean place(IWorld p_212245_1_, ChunkGenerator<? extends GenerationSettings> p_212245_2_, Random p_212245_3_, BlockPos p_212245_4_, T p_212245_5_) {
      Set<BlockPos> set = Sets.newHashSet();
      Set<BlockPos> set1 = Sets.newHashSet();
      Set<BlockPos> set2 = Sets.newHashSet();
      MutableBoundingBox mutableboundingbox = MutableBoundingBox.getNewBoundingBox();
      boolean flag = this.func_225557_a_(p_212245_1_, p_212245_3_, p_212245_4_, set, set1, mutableboundingbox, p_212245_5_);
      if (mutableboundingbox.minX <= mutableboundingbox.maxX && flag && !set.isEmpty()) {
         if (!p_212245_5_.field_227370_o_.isEmpty()) {
            List<BlockPos> list = Lists.newArrayList(set);
            List<BlockPos> list1 = Lists.newArrayList(set1);
            list.sort(Comparator.comparingInt(Vec3i::getY));
            list1.sort(Comparator.comparingInt(Vec3i::getY));
            p_212245_5_.field_227370_o_.forEach((p_lambda$place$13_6_) -> {
               p_lambda$place$13_6_.func_225576_a_(p_212245_1_, p_212245_3_, list, list1, set2, mutableboundingbox);
            });
         }

         VoxelShapePart voxelshapepart = this.func_227214_a_(p_212245_1_, mutableboundingbox, set, set2);
         Template.func_222857_a(p_212245_1_, 3, voxelshapepart, mutableboundingbox.minX, mutableboundingbox.minY, mutableboundingbox.minZ);
         return true;
      } else {
         return false;
      }
   }

   private VoxelShapePart func_227214_a_(IWorld p_227214_1_, MutableBoundingBox p_227214_2_, Set<BlockPos> p_227214_3_, Set<BlockPos> p_227214_4_) {
      List<Set<BlockPos>> list = Lists.newArrayList();
      VoxelShapePart voxelshapepart = new BitSetVoxelShapePart(p_227214_2_.getXSize(), p_227214_2_.getYSize(), p_227214_2_.getZSize());
      int i = true;

      for(int j = 0; j < 6; ++j) {
         list.add(Sets.newHashSet());
      }

      BlockPos.PooledMutable blockpos$pooledmutable = BlockPos.PooledMutable.retain();
      Throwable var9 = null;

      try {
         Iterator var10 = Lists.newArrayList(p_227214_4_).iterator();

         BlockPos blockpos1;
         while(var10.hasNext()) {
            blockpos1 = (BlockPos)var10.next();
            if (p_227214_2_.isVecInside(blockpos1)) {
               voxelshapepart.setFilled(blockpos1.getX() - p_227214_2_.minX, blockpos1.getY() - p_227214_2_.minY, blockpos1.getZ() - p_227214_2_.minZ, true, true);
            }
         }

         var10 = Lists.newArrayList(p_227214_3_).iterator();

         label231:
         while(true) {
            if (var10.hasNext()) {
               blockpos1 = (BlockPos)var10.next();
               if (p_227214_2_.isVecInside(blockpos1)) {
                  voxelshapepart.setFilled(blockpos1.getX() - p_227214_2_.minX, blockpos1.getY() - p_227214_2_.minY, blockpos1.getZ() - p_227214_2_.minZ, true, true);
               }

               Direction[] var34 = Direction.values();
               int var35 = var34.length;
               int var36 = 0;

               while(true) {
                  if (var36 >= var35) {
                     continue label231;
                  }

                  Direction direction = var34[var36];
                  blockpos$pooledmutable.setPos((Vec3i)blockpos1).move(direction);
                  if (!p_227214_3_.contains(blockpos$pooledmutable)) {
                     BlockState blockstate = p_227214_1_.getBlockState(blockpos$pooledmutable);
                     if (blockstate.has(BlockStateProperties.DISTANCE_1_7)) {
                        ((Set)list.get(0)).add(blockpos$pooledmutable.toImmutable());
                        this.func_208521_b(p_227214_1_, blockpos$pooledmutable, (BlockState)blockstate.with(BlockStateProperties.DISTANCE_1_7, 1));
                        if (p_227214_2_.isVecInside(blockpos$pooledmutable)) {
                           voxelshapepart.setFilled(blockpos$pooledmutable.getX() - p_227214_2_.minX, blockpos$pooledmutable.getY() - p_227214_2_.minY, blockpos$pooledmutable.getZ() - p_227214_2_.minZ, true, true);
                        }
                     }
                  }

                  ++var36;
               }
            }

            for(int l = 1; l < 6; ++l) {
               Set<BlockPos> set = (Set)list.get(l - 1);
               Set<BlockPos> set1 = (Set)list.get(l);
               Iterator var13 = set.iterator();

               while(var13.hasNext()) {
                  BlockPos blockpos2 = (BlockPos)var13.next();
                  if (p_227214_2_.isVecInside(blockpos2)) {
                     voxelshapepart.setFilled(blockpos2.getX() - p_227214_2_.minX, blockpos2.getY() - p_227214_2_.minY, blockpos2.getZ() - p_227214_2_.minZ, true, true);
                  }

                  Direction[] var15 = Direction.values();
                  int var16 = var15.length;

                  for(int var17 = 0; var17 < var16; ++var17) {
                     Direction direction1 = var15[var17];
                     blockpos$pooledmutable.setPos((Vec3i)blockpos2).move(direction1);
                     if (!set.contains(blockpos$pooledmutable) && !set1.contains(blockpos$pooledmutable)) {
                        BlockState blockstate1 = p_227214_1_.getBlockState(blockpos$pooledmutable);
                        if (blockstate1.has(BlockStateProperties.DISTANCE_1_7)) {
                           int k = (Integer)blockstate1.get(BlockStateProperties.DISTANCE_1_7);
                           if (k > l + 1) {
                              BlockState blockstate2 = (BlockState)blockstate1.with(BlockStateProperties.DISTANCE_1_7, l + 1);
                              this.func_208521_b(p_227214_1_, blockpos$pooledmutable, blockstate2);
                              if (p_227214_2_.isVecInside(blockpos$pooledmutable)) {
                                 voxelshapepart.setFilled(blockpos$pooledmutable.getX() - p_227214_2_.minX, blockpos$pooledmutable.getY() - p_227214_2_.minY, blockpos$pooledmutable.getZ() - p_227214_2_.minZ, true, true);
                              }

                              set1.add(blockpos$pooledmutable.toImmutable());
                           }
                        }
                     }
                  }
               }
            }

            return voxelshapepart;
         }
      } catch (Throwable var29) {
         var9 = var29;
         throw var29;
      } finally {
         if (blockpos$pooledmutable != null) {
            if (var9 != null) {
               try {
                  blockpos$pooledmutable.close();
               } catch (Throwable var28) {
                  var9.addSuppressed(var28);
               }
            } else {
               blockpos$pooledmutable.close();
            }
         }

      }
   }

   protected abstract boolean func_225557_a_(IWorldGenerationReader var1, Random var2, BlockPos var3, Set<BlockPos> var4, Set<BlockPos> var5, MutableBoundingBox var6, T var7);
}
