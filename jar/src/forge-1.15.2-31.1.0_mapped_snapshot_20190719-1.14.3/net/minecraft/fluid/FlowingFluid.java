package net.minecraft.fluid;

import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.Object2ByteLinkedOpenHashMap;
import it.unimi.dsi.fastutil.shorts.Short2BooleanMap;
import it.unimi.dsi.fastutil.shorts.Short2BooleanOpenHashMap;
import it.unimi.dsi.fastutil.shorts.Short2ObjectMap;
import it.unimi.dsi.fastutil.shorts.Short2ObjectOpenHashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.DoorBlock;
import net.minecraft.block.ILiquidContainer;
import net.minecraft.block.material.Material;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

public abstract class FlowingFluid extends Fluid {
   public static final BooleanProperty FALLING;
   public static final IntegerProperty LEVEL_1_8;
   private static final ThreadLocal<Object2ByteLinkedOpenHashMap<Block.RenderSideCacheKey>> field_212756_e;
   private final Map<IFluidState, VoxelShape> field_215669_f = Maps.newIdentityHashMap();

   protected void fillStateContainer(StateContainer.Builder<Fluid, IFluidState> p_207184_1_) {
      p_207184_1_.add(FALLING);
   }

   public Vec3d func_215663_a(IBlockReader p_215663_1_, BlockPos p_215663_2_, IFluidState p_215663_3_) {
      double lvt_4_1_ = 0.0D;
      double lvt_6_1_ = 0.0D;
      BlockPos.PooledMutable lvt_8_1_ = BlockPos.PooledMutable.retain();
      Throwable var9 = null;

      try {
         Iterator var10 = Direction.Plane.HORIZONTAL.iterator();

         while(var10.hasNext()) {
            Direction lvt_11_1_ = (Direction)var10.next();
            lvt_8_1_.setPos((Vec3i)p_215663_2_).move(lvt_11_1_);
            IFluidState lvt_12_1_ = p_215663_1_.getFluidState(lvt_8_1_);
            if (this.isSameOrEmpty(lvt_12_1_)) {
               float lvt_13_1_ = lvt_12_1_.func_223408_f();
               float lvt_14_1_ = 0.0F;
               if (lvt_13_1_ == 0.0F) {
                  if (!p_215663_1_.getBlockState(lvt_8_1_).getMaterial().blocksMovement()) {
                     BlockPos lvt_15_1_ = lvt_8_1_.down();
                     IFluidState lvt_16_1_ = p_215663_1_.getFluidState(lvt_15_1_);
                     if (this.isSameOrEmpty(lvt_16_1_)) {
                        lvt_13_1_ = lvt_16_1_.func_223408_f();
                        if (lvt_13_1_ > 0.0F) {
                           lvt_14_1_ = p_215663_3_.func_223408_f() - (lvt_13_1_ - 0.8888889F);
                        }
                     }
                  }
               } else if (lvt_13_1_ > 0.0F) {
                  lvt_14_1_ = p_215663_3_.func_223408_f() - lvt_13_1_;
               }

               if (lvt_14_1_ != 0.0F) {
                  lvt_4_1_ += (double)((float)lvt_11_1_.getXOffset() * lvt_14_1_);
                  lvt_6_1_ += (double)((float)lvt_11_1_.getZOffset() * lvt_14_1_);
               }
            }
         }

         Vec3d lvt_10_1_ = new Vec3d(lvt_4_1_, 0.0D, lvt_6_1_);
         if ((Boolean)p_215663_3_.get(FALLING)) {
            label164: {
               Iterator var27 = Direction.Plane.HORIZONTAL.iterator();

               Direction lvt_12_2_;
               do {
                  if (!var27.hasNext()) {
                     break label164;
                  }

                  lvt_12_2_ = (Direction)var27.next();
                  lvt_8_1_.setPos((Vec3i)p_215663_2_).move(lvt_12_2_);
               } while(!this.causesDownwardCurrent(p_215663_1_, lvt_8_1_, lvt_12_2_) && !this.causesDownwardCurrent(p_215663_1_, lvt_8_1_.up(), lvt_12_2_));

               lvt_10_1_ = lvt_10_1_.normalize().add(0.0D, -6.0D, 0.0D);
            }
         }

         Vec3d var28 = lvt_10_1_.normalize();
         return var28;
      } catch (Throwable var24) {
         var9 = var24;
         throw var24;
      } finally {
         if (lvt_8_1_ != null) {
            if (var9 != null) {
               try {
                  lvt_8_1_.close();
               } catch (Throwable var23) {
                  var9.addSuppressed(var23);
               }
            } else {
               lvt_8_1_.close();
            }
         }

      }
   }

   private boolean isSameOrEmpty(IFluidState p_212189_1_) {
      return p_212189_1_.isEmpty() || p_212189_1_.getFluid().isEquivalentTo(this);
   }

   protected boolean causesDownwardCurrent(IBlockReader p_205573_1_, BlockPos p_205573_2_, Direction p_205573_3_) {
      BlockState lvt_4_1_ = p_205573_1_.getBlockState(p_205573_2_);
      IFluidState lvt_5_1_ = p_205573_1_.getFluidState(p_205573_2_);
      if (lvt_5_1_.getFluid().isEquivalentTo(this)) {
         return false;
      } else if (p_205573_3_ == Direction.UP) {
         return true;
      } else {
         return lvt_4_1_.getMaterial() == Material.ICE ? false : lvt_4_1_.func_224755_d(p_205573_1_, p_205573_2_, p_205573_3_);
      }
   }

   protected void flowAround(IWorld p_205575_1_, BlockPos p_205575_2_, IFluidState p_205575_3_) {
      if (!p_205575_3_.isEmpty()) {
         BlockState lvt_4_1_ = p_205575_1_.getBlockState(p_205575_2_);
         BlockPos lvt_5_1_ = p_205575_2_.down();
         BlockState lvt_6_1_ = p_205575_1_.getBlockState(lvt_5_1_);
         IFluidState lvt_7_1_ = this.calculateCorrectFlowingState(p_205575_1_, lvt_5_1_, lvt_6_1_);
         if (this.canFlow(p_205575_1_, p_205575_2_, lvt_4_1_, Direction.DOWN, lvt_5_1_, lvt_6_1_, p_205575_1_.getFluidState(lvt_5_1_), lvt_7_1_.getFluid())) {
            this.flowInto(p_205575_1_, lvt_5_1_, lvt_6_1_, Direction.DOWN, lvt_7_1_);
            if (this.getNumHorizontallyAdjacentSources(p_205575_1_, p_205575_2_) >= 3) {
               this.func_207937_a(p_205575_1_, p_205575_2_, p_205575_3_, lvt_4_1_);
            }
         } else if (p_205575_3_.isSource() || !this.func_211759_a(p_205575_1_, lvt_7_1_.getFluid(), p_205575_2_, lvt_4_1_, lvt_5_1_, lvt_6_1_)) {
            this.func_207937_a(p_205575_1_, p_205575_2_, p_205575_3_, lvt_4_1_);
         }

      }
   }

   private void func_207937_a(IWorld p_207937_1_, BlockPos p_207937_2_, IFluidState p_207937_3_, BlockState p_207937_4_) {
      int lvt_5_1_ = p_207937_3_.getLevel() - this.getLevelDecreasePerBlock(p_207937_1_);
      if ((Boolean)p_207937_3_.get(FALLING)) {
         lvt_5_1_ = 7;
      }

      if (lvt_5_1_ > 0) {
         Map<Direction, IFluidState> lvt_6_1_ = this.func_205572_b(p_207937_1_, p_207937_2_, p_207937_4_);
         Iterator var7 = lvt_6_1_.entrySet().iterator();

         while(var7.hasNext()) {
            Entry<Direction, IFluidState> lvt_8_1_ = (Entry)var7.next();
            Direction lvt_9_1_ = (Direction)lvt_8_1_.getKey();
            IFluidState lvt_10_1_ = (IFluidState)lvt_8_1_.getValue();
            BlockPos lvt_11_1_ = p_207937_2_.offset(lvt_9_1_);
            BlockState lvt_12_1_ = p_207937_1_.getBlockState(lvt_11_1_);
            if (this.canFlow(p_207937_1_, p_207937_2_, p_207937_4_, lvt_9_1_, lvt_11_1_, lvt_12_1_, p_207937_1_.getFluidState(lvt_11_1_), lvt_10_1_.getFluid())) {
               this.flowInto(p_207937_1_, lvt_11_1_, lvt_12_1_, lvt_9_1_, lvt_10_1_);
            }
         }

      }
   }

   protected IFluidState calculateCorrectFlowingState(IWorldReader p_205576_1_, BlockPos p_205576_2_, BlockState p_205576_3_) {
      int lvt_4_1_ = 0;
      int lvt_5_1_ = 0;
      Iterator var6 = Direction.Plane.HORIZONTAL.iterator();

      while(var6.hasNext()) {
         Direction lvt_7_1_ = (Direction)var6.next();
         BlockPos lvt_8_1_ = p_205576_2_.offset(lvt_7_1_);
         BlockState lvt_9_1_ = p_205576_1_.getBlockState(lvt_8_1_);
         IFluidState lvt_10_1_ = lvt_9_1_.getFluidState();
         if (lvt_10_1_.getFluid().isEquivalentTo(this) && this.func_212751_a(lvt_7_1_, p_205576_1_, p_205576_2_, p_205576_3_, lvt_8_1_, lvt_9_1_)) {
            if (lvt_10_1_.isSource()) {
               ++lvt_5_1_;
            }

            lvt_4_1_ = Math.max(lvt_4_1_, lvt_10_1_.getLevel());
         }
      }

      if (this.canSourcesMultiply() && lvt_5_1_ >= 2) {
         BlockState lvt_6_1_ = p_205576_1_.getBlockState(p_205576_2_.down());
         IFluidState lvt_7_2_ = lvt_6_1_.getFluidState();
         if (lvt_6_1_.getMaterial().isSolid() || this.isSameAs(lvt_7_2_)) {
            return this.getStillFluidState(false);
         }
      }

      BlockPos lvt_6_2_ = p_205576_2_.up();
      BlockState lvt_7_3_ = p_205576_1_.getBlockState(lvt_6_2_);
      IFluidState lvt_8_2_ = lvt_7_3_.getFluidState();
      if (!lvt_8_2_.isEmpty() && lvt_8_2_.getFluid().isEquivalentTo(this) && this.func_212751_a(Direction.UP, p_205576_1_, p_205576_2_, p_205576_3_, lvt_6_2_, lvt_7_3_)) {
         return this.getFlowingFluidState(8, true);
      } else {
         int lvt_9_2_ = lvt_4_1_ - this.getLevelDecreasePerBlock(p_205576_1_);
         if (lvt_9_2_ <= 0) {
            return Fluids.EMPTY.getDefaultState();
         } else {
            return this.getFlowingFluidState(lvt_9_2_, false);
         }
      }
   }

   private boolean func_212751_a(Direction p_212751_1_, IBlockReader p_212751_2_, BlockPos p_212751_3_, BlockState p_212751_4_, BlockPos p_212751_5_, BlockState p_212751_6_) {
      Object2ByteLinkedOpenHashMap lvt_7_2_;
      if (!p_212751_4_.getBlock().isVariableOpacity() && !p_212751_6_.getBlock().isVariableOpacity()) {
         lvt_7_2_ = (Object2ByteLinkedOpenHashMap)field_212756_e.get();
      } else {
         lvt_7_2_ = null;
      }

      Block.RenderSideCacheKey lvt_8_2_;
      if (lvt_7_2_ != null) {
         lvt_8_2_ = new Block.RenderSideCacheKey(p_212751_4_, p_212751_6_, p_212751_1_);
         byte lvt_9_1_ = lvt_7_2_.getAndMoveToFirst(lvt_8_2_);
         if (lvt_9_1_ != 127) {
            return lvt_9_1_ != 0;
         }
      } else {
         lvt_8_2_ = null;
      }

      VoxelShape lvt_9_2_ = p_212751_4_.getCollisionShape(p_212751_2_, p_212751_3_);
      VoxelShape lvt_10_1_ = p_212751_6_.getCollisionShape(p_212751_2_, p_212751_5_);
      boolean lvt_11_1_ = !VoxelShapes.doAdjacentCubeSidesFillSquare(lvt_9_2_, lvt_10_1_, p_212751_1_);
      if (lvt_7_2_ != null) {
         if (lvt_7_2_.size() == 200) {
            lvt_7_2_.removeLastByte();
         }

         lvt_7_2_.putAndMoveToFirst(lvt_8_2_, (byte)(lvt_11_1_ ? 1 : 0));
      }

      return lvt_11_1_;
   }

   public abstract Fluid getFlowingFluid();

   public IFluidState getFlowingFluidState(int p_207207_1_, boolean p_207207_2_) {
      return (IFluidState)((IFluidState)this.getFlowingFluid().getDefaultState().with(LEVEL_1_8, p_207207_1_)).with(FALLING, p_207207_2_);
   }

   public abstract Fluid getStillFluid();

   public IFluidState getStillFluidState(boolean p_207204_1_) {
      return (IFluidState)this.getStillFluid().getDefaultState().with(FALLING, p_207204_1_);
   }

   protected abstract boolean canSourcesMultiply();

   protected void flowInto(IWorld p_205574_1_, BlockPos p_205574_2_, BlockState p_205574_3_, Direction p_205574_4_, IFluidState p_205574_5_) {
      if (p_205574_3_.getBlock() instanceof ILiquidContainer) {
         ((ILiquidContainer)p_205574_3_.getBlock()).receiveFluid(p_205574_1_, p_205574_2_, p_205574_3_, p_205574_5_);
      } else {
         if (!p_205574_3_.isAir()) {
            this.beforeReplacingBlock(p_205574_1_, p_205574_2_, p_205574_3_);
         }

         p_205574_1_.setBlockState(p_205574_2_, p_205574_5_.getBlockState(), 3);
      }

   }

   protected abstract void beforeReplacingBlock(IWorld var1, BlockPos var2, BlockState var3);

   private static short func_212752_a(BlockPos p_212752_0_, BlockPos p_212752_1_) {
      int lvt_2_1_ = p_212752_1_.getX() - p_212752_0_.getX();
      int lvt_3_1_ = p_212752_1_.getZ() - p_212752_0_.getZ();
      return (short)((lvt_2_1_ + 128 & 255) << 8 | lvt_3_1_ + 128 & 255);
   }

   protected int func_205571_a(IWorldReader p_205571_1_, BlockPos p_205571_2_, int p_205571_3_, Direction p_205571_4_, BlockState p_205571_5_, BlockPos p_205571_6_, Short2ObjectMap<Pair<BlockState, IFluidState>> p_205571_7_, Short2BooleanMap p_205571_8_) {
      int lvt_9_1_ = 1000;
      Iterator var10 = Direction.Plane.HORIZONTAL.iterator();

      while(var10.hasNext()) {
         Direction lvt_11_1_ = (Direction)var10.next();
         if (lvt_11_1_ != p_205571_4_) {
            BlockPos lvt_12_1_ = p_205571_2_.offset(lvt_11_1_);
            short lvt_13_1_ = func_212752_a(p_205571_6_, lvt_12_1_);
            Pair<BlockState, IFluidState> lvt_14_1_ = (Pair)p_205571_7_.computeIfAbsent(lvt_13_1_, (p_212748_2_) -> {
               BlockState lvt_3_1_ = p_205571_1_.getBlockState(lvt_12_1_);
               return Pair.of(lvt_3_1_, lvt_3_1_.getFluidState());
            });
            BlockState lvt_15_1_ = (BlockState)lvt_14_1_.getFirst();
            IFluidState lvt_16_1_ = (IFluidState)lvt_14_1_.getSecond();
            if (this.func_211760_a(p_205571_1_, this.getFlowingFluid(), p_205571_2_, p_205571_5_, lvt_11_1_, lvt_12_1_, lvt_15_1_, lvt_16_1_)) {
               boolean lvt_17_1_ = p_205571_8_.computeIfAbsent(lvt_13_1_, (p_212749_4_) -> {
                  BlockPos lvt_5_1_ = lvt_12_1_.down();
                  BlockState lvt_6_1_ = p_205571_1_.getBlockState(lvt_5_1_);
                  return this.func_211759_a(p_205571_1_, this.getFlowingFluid(), lvt_12_1_, lvt_15_1_, lvt_5_1_, lvt_6_1_);
               });
               if (lvt_17_1_) {
                  return p_205571_3_;
               }

               if (p_205571_3_ < this.getSlopeFindDistance(p_205571_1_)) {
                  int lvt_18_1_ = this.func_205571_a(p_205571_1_, lvt_12_1_, p_205571_3_ + 1, lvt_11_1_.getOpposite(), lvt_15_1_, p_205571_6_, p_205571_7_, p_205571_8_);
                  if (lvt_18_1_ < lvt_9_1_) {
                     lvt_9_1_ = lvt_18_1_;
                  }
               }
            }
         }
      }

      return lvt_9_1_;
   }

   private boolean func_211759_a(IBlockReader p_211759_1_, Fluid p_211759_2_, BlockPos p_211759_3_, BlockState p_211759_4_, BlockPos p_211759_5_, BlockState p_211759_6_) {
      if (!this.func_212751_a(Direction.DOWN, p_211759_1_, p_211759_3_, p_211759_4_, p_211759_5_, p_211759_6_)) {
         return false;
      } else {
         return p_211759_6_.getFluidState().getFluid().isEquivalentTo(this) ? true : this.isBlocked(p_211759_1_, p_211759_5_, p_211759_6_, p_211759_2_);
      }
   }

   private boolean func_211760_a(IBlockReader p_211760_1_, Fluid p_211760_2_, BlockPos p_211760_3_, BlockState p_211760_4_, Direction p_211760_5_, BlockPos p_211760_6_, BlockState p_211760_7_, IFluidState p_211760_8_) {
      return !this.isSameAs(p_211760_8_) && this.func_212751_a(p_211760_5_, p_211760_1_, p_211760_3_, p_211760_4_, p_211760_6_, p_211760_7_) && this.isBlocked(p_211760_1_, p_211760_6_, p_211760_7_, p_211760_2_);
   }

   private boolean isSameAs(IFluidState p_211758_1_) {
      return p_211758_1_.getFluid().isEquivalentTo(this) && p_211758_1_.isSource();
   }

   protected abstract int getSlopeFindDistance(IWorldReader var1);

   private int getNumHorizontallyAdjacentSources(IWorldReader p_207936_1_, BlockPos p_207936_2_) {
      int lvt_3_1_ = 0;
      Iterator var4 = Direction.Plane.HORIZONTAL.iterator();

      while(var4.hasNext()) {
         Direction lvt_5_1_ = (Direction)var4.next();
         BlockPos lvt_6_1_ = p_207936_2_.offset(lvt_5_1_);
         IFluidState lvt_7_1_ = p_207936_1_.getFluidState(lvt_6_1_);
         if (this.isSameAs(lvt_7_1_)) {
            ++lvt_3_1_;
         }
      }

      return lvt_3_1_;
   }

   protected Map<Direction, IFluidState> func_205572_b(IWorldReader p_205572_1_, BlockPos p_205572_2_, BlockState p_205572_3_) {
      int lvt_4_1_ = 1000;
      Map<Direction, IFluidState> lvt_5_1_ = Maps.newEnumMap(Direction.class);
      Short2ObjectMap<Pair<BlockState, IFluidState>> lvt_6_1_ = new Short2ObjectOpenHashMap();
      Short2BooleanMap lvt_7_1_ = new Short2BooleanOpenHashMap();
      Iterator var8 = Direction.Plane.HORIZONTAL.iterator();

      while(var8.hasNext()) {
         Direction lvt_9_1_ = (Direction)var8.next();
         BlockPos lvt_10_1_ = p_205572_2_.offset(lvt_9_1_);
         short lvt_11_1_ = func_212752_a(p_205572_2_, lvt_10_1_);
         Pair<BlockState, IFluidState> lvt_12_1_ = (Pair)lvt_6_1_.computeIfAbsent(lvt_11_1_, (p_212755_2_) -> {
            BlockState lvt_3_1_ = p_205572_1_.getBlockState(lvt_10_1_);
            return Pair.of(lvt_3_1_, lvt_3_1_.getFluidState());
         });
         BlockState lvt_13_1_ = (BlockState)lvt_12_1_.getFirst();
         IFluidState lvt_14_1_ = (IFluidState)lvt_12_1_.getSecond();
         IFluidState lvt_15_1_ = this.calculateCorrectFlowingState(p_205572_1_, lvt_10_1_, lvt_13_1_);
         if (this.func_211760_a(p_205572_1_, lvt_15_1_.getFluid(), p_205572_2_, p_205572_3_, lvt_9_1_, lvt_10_1_, lvt_13_1_, lvt_14_1_)) {
            BlockPos lvt_17_1_ = lvt_10_1_.down();
            boolean lvt_18_1_ = lvt_7_1_.computeIfAbsent(lvt_11_1_, (p_212753_5_) -> {
               BlockState lvt_6_1_ = p_205572_1_.getBlockState(lvt_17_1_);
               return this.func_211759_a(p_205572_1_, this.getFlowingFluid(), lvt_10_1_, lvt_13_1_, lvt_17_1_, lvt_6_1_);
            });
            int lvt_16_2_;
            if (lvt_18_1_) {
               lvt_16_2_ = 0;
            } else {
               lvt_16_2_ = this.func_205571_a(p_205572_1_, lvt_10_1_, 1, lvt_9_1_.getOpposite(), lvt_13_1_, p_205572_2_, lvt_6_1_, lvt_7_1_);
            }

            if (lvt_16_2_ < lvt_4_1_) {
               lvt_5_1_.clear();
            }

            if (lvt_16_2_ <= lvt_4_1_) {
               lvt_5_1_.put(lvt_9_1_, lvt_15_1_);
               lvt_4_1_ = lvt_16_2_;
            }
         }
      }

      return lvt_5_1_;
   }

   private boolean isBlocked(IBlockReader p_211761_1_, BlockPos p_211761_2_, BlockState p_211761_3_, Fluid p_211761_4_) {
      Block lvt_5_1_ = p_211761_3_.getBlock();
      if (lvt_5_1_ instanceof ILiquidContainer) {
         return ((ILiquidContainer)lvt_5_1_).canContainFluid(p_211761_1_, p_211761_2_, p_211761_3_, p_211761_4_);
      } else if (!(lvt_5_1_ instanceof DoorBlock) && !lvt_5_1_.isIn(BlockTags.SIGNS) && lvt_5_1_ != Blocks.LADDER && lvt_5_1_ != Blocks.SUGAR_CANE && lvt_5_1_ != Blocks.BUBBLE_COLUMN) {
         Material lvt_6_1_ = p_211761_3_.getMaterial();
         if (lvt_6_1_ != Material.PORTAL && lvt_6_1_ != Material.STRUCTURE_VOID && lvt_6_1_ != Material.OCEAN_PLANT && lvt_6_1_ != Material.SEA_GRASS) {
            return !lvt_6_1_.blocksMovement();
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   protected boolean canFlow(IBlockReader p_205570_1_, BlockPos p_205570_2_, BlockState p_205570_3_, Direction p_205570_4_, BlockPos p_205570_5_, BlockState p_205570_6_, IFluidState p_205570_7_, Fluid p_205570_8_) {
      return p_205570_7_.func_215677_a(p_205570_1_, p_205570_5_, p_205570_8_, p_205570_4_) && this.func_212751_a(p_205570_4_, p_205570_1_, p_205570_2_, p_205570_3_, p_205570_5_, p_205570_6_) && this.isBlocked(p_205570_1_, p_205570_5_, p_205570_6_, p_205570_8_);
   }

   protected abstract int getLevelDecreasePerBlock(IWorldReader var1);

   protected int func_215667_a(World p_215667_1_, BlockPos p_215667_2_, IFluidState p_215667_3_, IFluidState p_215667_4_) {
      return this.getTickRate(p_215667_1_);
   }

   public void tick(World p_207191_1_, BlockPos p_207191_2_, IFluidState p_207191_3_) {
      if (!p_207191_3_.isSource()) {
         IFluidState lvt_4_1_ = this.calculateCorrectFlowingState(p_207191_1_, p_207191_2_, p_207191_1_.getBlockState(p_207191_2_));
         int lvt_5_1_ = this.func_215667_a(p_207191_1_, p_207191_2_, p_207191_3_, lvt_4_1_);
         if (lvt_4_1_.isEmpty()) {
            p_207191_3_ = lvt_4_1_;
            p_207191_1_.setBlockState(p_207191_2_, Blocks.AIR.getDefaultState(), 3);
         } else if (!lvt_4_1_.equals(p_207191_3_)) {
            p_207191_3_ = lvt_4_1_;
            BlockState lvt_6_1_ = lvt_4_1_.getBlockState();
            p_207191_1_.setBlockState(p_207191_2_, lvt_6_1_, 2);
            p_207191_1_.getPendingFluidTicks().scheduleTick(p_207191_2_, lvt_4_1_.getFluid(), lvt_5_1_);
            p_207191_1_.notifyNeighborsOfStateChange(p_207191_2_, lvt_6_1_.getBlock());
         }
      }

      this.flowAround(p_207191_1_, p_207191_2_, p_207191_3_);
   }

   protected static int getLevelFromState(IFluidState p_207205_0_) {
      return p_207205_0_.isSource() ? 0 : 8 - Math.min(p_207205_0_.getLevel(), 8) + ((Boolean)p_207205_0_.get(FALLING) ? 8 : 0);
   }

   private static boolean func_215666_c(IFluidState p_215666_0_, IBlockReader p_215666_1_, BlockPos p_215666_2_) {
      return p_215666_0_.getFluid().isEquivalentTo(p_215666_1_.getFluidState(p_215666_2_.up()).getFluid());
   }

   public float func_215662_a(IFluidState p_215662_1_, IBlockReader p_215662_2_, BlockPos p_215662_3_) {
      return func_215666_c(p_215662_1_, p_215662_2_, p_215662_3_) ? 1.0F : p_215662_1_.func_223408_f();
   }

   public float func_223407_a(IFluidState p_223407_1_) {
      return (float)p_223407_1_.getLevel() / 9.0F;
   }

   public VoxelShape func_215664_b(IFluidState p_215664_1_, IBlockReader p_215664_2_, BlockPos p_215664_3_) {
      return p_215664_1_.getLevel() == 9 && func_215666_c(p_215664_1_, p_215664_2_, p_215664_3_) ? VoxelShapes.fullCube() : (VoxelShape)this.field_215669_f.computeIfAbsent(p_215664_1_, (p_215668_2_) -> {
         return VoxelShapes.create(0.0D, 0.0D, 0.0D, 1.0D, (double)p_215668_2_.func_215679_a(p_215664_2_, p_215664_3_), 1.0D);
      });
   }

   static {
      FALLING = BlockStateProperties.FALLING;
      LEVEL_1_8 = BlockStateProperties.LEVEL_1_8;
      field_212756_e = ThreadLocal.withInitial(() -> {
         Object2ByteLinkedOpenHashMap<Block.RenderSideCacheKey> lvt_0_1_ = new Object2ByteLinkedOpenHashMap<Block.RenderSideCacheKey>(200) {
            protected void rehash(int p_rehash_1_) {
            }
         };
         lvt_0_1_.defaultReturnValue((byte)127);
         return lvt_0_1_;
      });
   }
}
