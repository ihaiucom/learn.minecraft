package net.minecraft.world.gen.feature.structure;

import com.google.common.collect.ImmutableSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.fluid.IFluidState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ChestTileEntity;
import net.minecraft.tileentity.DispenserTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Mirror;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.Heightmap;

public abstract class StructurePiece {
   protected static final BlockState CAVE_AIR;
   protected MutableBoundingBox boundingBox;
   @Nullable
   private Direction field_74885_f;
   private Mirror mirror;
   private Rotation rotation;
   protected int componentType;
   private final IStructurePieceType field_214811_d;
   private static final Set<Block> BLOCKS_NEEDING_POSTPROCESSING;

   protected StructurePiece(IStructurePieceType p_i51342_1_, int p_i51342_2_) {
      this.field_214811_d = p_i51342_1_;
      this.componentType = p_i51342_2_;
   }

   public StructurePiece(IStructurePieceType p_i51343_1_, CompoundNBT p_i51343_2_) {
      this(p_i51343_1_, p_i51343_2_.getInt("GD"));
      if (p_i51343_2_.contains("BB")) {
         this.boundingBox = new MutableBoundingBox(p_i51343_2_.getIntArray("BB"));
      }

      int i = p_i51343_2_.getInt("O");
      this.setCoordBaseMode(i == -1 ? null : Direction.byHorizontalIndex(i));
   }

   public final CompoundNBT write() {
      if (Registry.STRUCTURE_PIECE.getKey(this.func_214807_k()) == null) {
         throw new RuntimeException("StructurePiece \"" + this.getClass().getName() + "\": \"" + this.func_214807_k() + "\" missing ID Mapping, Modder see MapGenStructureIO");
      } else {
         CompoundNBT compoundnbt = new CompoundNBT();
         compoundnbt.putString("id", Registry.STRUCTURE_PIECE.getKey(this.func_214807_k()).toString());
         compoundnbt.put("BB", this.boundingBox.toNBTTagIntArray());
         Direction direction = this.getCoordBaseMode();
         compoundnbt.putInt("O", direction == null ? -1 : direction.getHorizontalIndex());
         compoundnbt.putInt("GD", this.componentType);
         this.readAdditional(compoundnbt);
         return compoundnbt;
      }
   }

   protected abstract void readAdditional(CompoundNBT var1);

   public void buildComponent(StructurePiece p_74861_1_, List<StructurePiece> p_74861_2_, Random p_74861_3_) {
   }

   public abstract boolean func_225577_a_(IWorld var1, ChunkGenerator<?> var2, Random var3, MutableBoundingBox var4, ChunkPos var5);

   public MutableBoundingBox getBoundingBox() {
      return this.boundingBox;
   }

   public int getComponentType() {
      return this.componentType;
   }

   public boolean func_214810_a(ChunkPos p_214810_1_, int p_214810_2_) {
      int i = p_214810_1_.x << 4;
      int j = p_214810_1_.z << 4;
      return this.boundingBox.intersectsWith(i - p_214810_2_, j - p_214810_2_, i + 15 + p_214810_2_, j + 15 + p_214810_2_);
   }

   public static StructurePiece findIntersecting(List<StructurePiece> p_74883_0_, MutableBoundingBox p_74883_1_) {
      Iterator var2 = p_74883_0_.iterator();

      StructurePiece structurepiece;
      do {
         if (!var2.hasNext()) {
            return null;
         }

         structurepiece = (StructurePiece)var2.next();
      } while(structurepiece.getBoundingBox() == null || !structurepiece.getBoundingBox().intersectsWith(p_74883_1_));

      return structurepiece;
   }

   protected boolean isLiquidInStructureBoundingBox(IBlockReader p_74860_1_, MutableBoundingBox p_74860_2_) {
      int i = Math.max(this.boundingBox.minX - 1, p_74860_2_.minX);
      int j = Math.max(this.boundingBox.minY - 1, p_74860_2_.minY);
      int k = Math.max(this.boundingBox.minZ - 1, p_74860_2_.minZ);
      int l = Math.min(this.boundingBox.maxX + 1, p_74860_2_.maxX);
      int i1 = Math.min(this.boundingBox.maxY + 1, p_74860_2_.maxY);
      int j1 = Math.min(this.boundingBox.maxZ + 1, p_74860_2_.maxZ);
      BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();

      int j2;
      int l2;
      for(j2 = i; j2 <= l; ++j2) {
         for(l2 = k; l2 <= j1; ++l2) {
            if (p_74860_1_.getBlockState(blockpos$mutable.setPos(j2, j, l2)).getMaterial().isLiquid()) {
               return true;
            }

            if (p_74860_1_.getBlockState(blockpos$mutable.setPos(j2, i1, l2)).getMaterial().isLiquid()) {
               return true;
            }
         }
      }

      for(j2 = i; j2 <= l; ++j2) {
         for(l2 = j; l2 <= i1; ++l2) {
            if (p_74860_1_.getBlockState(blockpos$mutable.setPos(j2, l2, k)).getMaterial().isLiquid()) {
               return true;
            }

            if (p_74860_1_.getBlockState(blockpos$mutable.setPos(j2, l2, j1)).getMaterial().isLiquid()) {
               return true;
            }
         }
      }

      for(j2 = k; j2 <= j1; ++j2) {
         for(l2 = j; l2 <= i1; ++l2) {
            if (p_74860_1_.getBlockState(blockpos$mutable.setPos(i, l2, j2)).getMaterial().isLiquid()) {
               return true;
            }

            if (p_74860_1_.getBlockState(blockpos$mutable.setPos(l, l2, j2)).getMaterial().isLiquid()) {
               return true;
            }
         }
      }

      return false;
   }

   protected int getXWithOffset(int p_74865_1_, int p_74865_2_) {
      Direction direction = this.getCoordBaseMode();
      if (direction == null) {
         return p_74865_1_;
      } else {
         switch(direction) {
         case NORTH:
         case SOUTH:
            return this.boundingBox.minX + p_74865_1_;
         case WEST:
            return this.boundingBox.maxX - p_74865_2_;
         case EAST:
            return this.boundingBox.minX + p_74865_2_;
         default:
            return p_74865_1_;
         }
      }
   }

   protected int getYWithOffset(int p_74862_1_) {
      return this.getCoordBaseMode() == null ? p_74862_1_ : p_74862_1_ + this.boundingBox.minY;
   }

   protected int getZWithOffset(int p_74873_1_, int p_74873_2_) {
      Direction direction = this.getCoordBaseMode();
      if (direction == null) {
         return p_74873_2_;
      } else {
         switch(direction) {
         case NORTH:
            return this.boundingBox.maxZ - p_74873_2_;
         case SOUTH:
            return this.boundingBox.minZ + p_74873_2_;
         case WEST:
         case EAST:
            return this.boundingBox.minZ + p_74873_1_;
         default:
            return p_74873_2_;
         }
      }
   }

   protected void setBlockState(IWorld p_175811_1_, BlockState p_175811_2_, int p_175811_3_, int p_175811_4_, int p_175811_5_, MutableBoundingBox p_175811_6_) {
      BlockPos blockpos = new BlockPos(this.getXWithOffset(p_175811_3_, p_175811_5_), this.getYWithOffset(p_175811_4_), this.getZWithOffset(p_175811_3_, p_175811_5_));
      if (p_175811_6_.isVecInside(blockpos)) {
         if (this.mirror != Mirror.NONE) {
            p_175811_2_ = p_175811_2_.mirror(this.mirror);
         }

         if (this.rotation != Rotation.NONE) {
            p_175811_2_ = p_175811_2_.rotate(this.rotation);
         }

         p_175811_1_.setBlockState(blockpos, p_175811_2_, 2);
         IFluidState ifluidstate = p_175811_1_.getFluidState(blockpos);
         if (!ifluidstate.isEmpty()) {
            p_175811_1_.getPendingFluidTicks().scheduleTick(blockpos, ifluidstate.getFluid(), 0);
         }

         if (BLOCKS_NEEDING_POSTPROCESSING.contains(p_175811_2_.getBlock())) {
            p_175811_1_.getChunk(blockpos).markBlockForPostprocessing(blockpos);
         }
      }

   }

   protected BlockState getBlockStateFromPos(IBlockReader p_175807_1_, int p_175807_2_, int p_175807_3_, int p_175807_4_, MutableBoundingBox p_175807_5_) {
      int i = this.getXWithOffset(p_175807_2_, p_175807_4_);
      int j = this.getYWithOffset(p_175807_3_);
      int k = this.getZWithOffset(p_175807_2_, p_175807_4_);
      BlockPos blockpos = new BlockPos(i, j, k);
      return !p_175807_5_.isVecInside(blockpos) ? Blocks.AIR.getDefaultState() : p_175807_1_.getBlockState(blockpos);
   }

   protected boolean getSkyBrightness(IWorldReader p_189916_1_, int p_189916_2_, int p_189916_3_, int p_189916_4_, MutableBoundingBox p_189916_5_) {
      int i = this.getXWithOffset(p_189916_2_, p_189916_4_);
      int j = this.getYWithOffset(p_189916_3_ + 1);
      int k = this.getZWithOffset(p_189916_2_, p_189916_4_);
      BlockPos blockpos = new BlockPos(i, j, k);
      if (!p_189916_5_.isVecInside(blockpos)) {
         return false;
      } else {
         return j < p_189916_1_.getHeight(Heightmap.Type.OCEAN_FLOOR_WG, i, k);
      }
   }

   protected void fillWithAir(IWorld p_74878_1_, MutableBoundingBox p_74878_2_, int p_74878_3_, int p_74878_4_, int p_74878_5_, int p_74878_6_, int p_74878_7_, int p_74878_8_) {
      for(int i = p_74878_4_; i <= p_74878_7_; ++i) {
         for(int j = p_74878_3_; j <= p_74878_6_; ++j) {
            for(int k = p_74878_5_; k <= p_74878_8_; ++k) {
               this.setBlockState(p_74878_1_, Blocks.AIR.getDefaultState(), j, i, k, p_74878_2_);
            }
         }
      }

   }

   protected void fillWithBlocks(IWorld p_175804_1_, MutableBoundingBox p_175804_2_, int p_175804_3_, int p_175804_4_, int p_175804_5_, int p_175804_6_, int p_175804_7_, int p_175804_8_, BlockState p_175804_9_, BlockState p_175804_10_, boolean p_175804_11_) {
      for(int i = p_175804_4_; i <= p_175804_7_; ++i) {
         for(int j = p_175804_3_; j <= p_175804_6_; ++j) {
            for(int k = p_175804_5_; k <= p_175804_8_; ++k) {
               if (!p_175804_11_ || !this.getBlockStateFromPos(p_175804_1_, j, i, k, p_175804_2_).isAir()) {
                  if (i != p_175804_4_ && i != p_175804_7_ && j != p_175804_3_ && j != p_175804_6_ && k != p_175804_5_ && k != p_175804_8_) {
                     this.setBlockState(p_175804_1_, p_175804_10_, j, i, k, p_175804_2_);
                  } else {
                     this.setBlockState(p_175804_1_, p_175804_9_, j, i, k, p_175804_2_);
                  }
               }
            }
         }
      }

   }

   protected void fillWithRandomizedBlocks(IWorld p_74882_1_, MutableBoundingBox p_74882_2_, int p_74882_3_, int p_74882_4_, int p_74882_5_, int p_74882_6_, int p_74882_7_, int p_74882_8_, boolean p_74882_9_, Random p_74882_10_, StructurePiece.BlockSelector p_74882_11_) {
      for(int i = p_74882_4_; i <= p_74882_7_; ++i) {
         for(int j = p_74882_3_; j <= p_74882_6_; ++j) {
            for(int k = p_74882_5_; k <= p_74882_8_; ++k) {
               if (!p_74882_9_ || !this.getBlockStateFromPos(p_74882_1_, j, i, k, p_74882_2_).isAir()) {
                  p_74882_11_.selectBlocks(p_74882_10_, j, i, k, i == p_74882_4_ || i == p_74882_7_ || j == p_74882_3_ || j == p_74882_6_ || k == p_74882_5_ || k == p_74882_8_);
                  this.setBlockState(p_74882_1_, p_74882_11_.getBlockState(), j, i, k, p_74882_2_);
               }
            }
         }
      }

   }

   protected void generateMaybeBox(IWorld p_189914_1_, MutableBoundingBox p_189914_2_, Random p_189914_3_, float p_189914_4_, int p_189914_5_, int p_189914_6_, int p_189914_7_, int p_189914_8_, int p_189914_9_, int p_189914_10_, BlockState p_189914_11_, BlockState p_189914_12_, boolean p_189914_13_, boolean p_189914_14_) {
      for(int i = p_189914_6_; i <= p_189914_9_; ++i) {
         for(int j = p_189914_5_; j <= p_189914_8_; ++j) {
            for(int k = p_189914_7_; k <= p_189914_10_; ++k) {
               if (p_189914_3_.nextFloat() <= p_189914_4_ && (!p_189914_13_ || !this.getBlockStateFromPos(p_189914_1_, j, i, k, p_189914_2_).isAir()) && (!p_189914_14_ || this.getSkyBrightness(p_189914_1_, j, i, k, p_189914_2_))) {
                  if (i != p_189914_6_ && i != p_189914_9_ && j != p_189914_5_ && j != p_189914_8_ && k != p_189914_7_ && k != p_189914_10_) {
                     this.setBlockState(p_189914_1_, p_189914_12_, j, i, k, p_189914_2_);
                  } else {
                     this.setBlockState(p_189914_1_, p_189914_11_, j, i, k, p_189914_2_);
                  }
               }
            }
         }
      }

   }

   protected void randomlyPlaceBlock(IWorld p_175809_1_, MutableBoundingBox p_175809_2_, Random p_175809_3_, float p_175809_4_, int p_175809_5_, int p_175809_6_, int p_175809_7_, BlockState p_175809_8_) {
      if (p_175809_3_.nextFloat() < p_175809_4_) {
         this.setBlockState(p_175809_1_, p_175809_8_, p_175809_5_, p_175809_6_, p_175809_7_, p_175809_2_);
      }

   }

   protected void randomlyRareFillWithBlocks(IWorld p_180777_1_, MutableBoundingBox p_180777_2_, int p_180777_3_, int p_180777_4_, int p_180777_5_, int p_180777_6_, int p_180777_7_, int p_180777_8_, BlockState p_180777_9_, boolean p_180777_10_) {
      float f = (float)(p_180777_6_ - p_180777_3_ + 1);
      float f1 = (float)(p_180777_7_ - p_180777_4_ + 1);
      float f2 = (float)(p_180777_8_ - p_180777_5_ + 1);
      float f3 = (float)p_180777_3_ + f / 2.0F;
      float f4 = (float)p_180777_5_ + f2 / 2.0F;

      for(int i = p_180777_4_; i <= p_180777_7_; ++i) {
         float f5 = (float)(i - p_180777_4_) / f1;

         for(int j = p_180777_3_; j <= p_180777_6_; ++j) {
            float f6 = ((float)j - f3) / (f * 0.5F);

            for(int k = p_180777_5_; k <= p_180777_8_; ++k) {
               float f7 = ((float)k - f4) / (f2 * 0.5F);
               if (!p_180777_10_ || !this.getBlockStateFromPos(p_180777_1_, j, i, k, p_180777_2_).isAir()) {
                  float f8 = f6 * f6 + f5 * f5 + f7 * f7;
                  if (f8 <= 1.05F) {
                     this.setBlockState(p_180777_1_, p_180777_9_, j, i, k, p_180777_2_);
                  }
               }
            }
         }
      }

   }

   protected void replaceAirAndLiquidDownwards(IWorld p_175808_1_, BlockState p_175808_2_, int p_175808_3_, int p_175808_4_, int p_175808_5_, MutableBoundingBox p_175808_6_) {
      int i = this.getXWithOffset(p_175808_3_, p_175808_5_);
      int j = this.getYWithOffset(p_175808_4_);
      int k = this.getZWithOffset(p_175808_3_, p_175808_5_);
      if (p_175808_6_.isVecInside(new BlockPos(i, j, k))) {
         while((p_175808_1_.isAirBlock(new BlockPos(i, j, k)) || p_175808_1_.getBlockState(new BlockPos(i, j, k)).getMaterial().isLiquid()) && j > 1) {
            p_175808_1_.setBlockState(new BlockPos(i, j, k), p_175808_2_, 2);
            --j;
         }
      }

   }

   protected boolean generateChest(IWorld p_186167_1_, MutableBoundingBox p_186167_2_, Random p_186167_3_, int p_186167_4_, int p_186167_5_, int p_186167_6_, ResourceLocation p_186167_7_) {
      BlockPos blockpos = new BlockPos(this.getXWithOffset(p_186167_4_, p_186167_6_), this.getYWithOffset(p_186167_5_), this.getZWithOffset(p_186167_4_, p_186167_6_));
      return this.generateChest(p_186167_1_, p_186167_2_, p_186167_3_, blockpos, p_186167_7_, (BlockState)null);
   }

   public static BlockState func_197528_a(IBlockReader p_197528_0_, BlockPos p_197528_1_, BlockState p_197528_2_) {
      Direction direction = null;
      Iterator var4 = Direction.Plane.HORIZONTAL.iterator();

      while(var4.hasNext()) {
         Direction direction1 = (Direction)var4.next();
         BlockPos blockpos = p_197528_1_.offset(direction1);
         BlockState blockstate = p_197528_0_.getBlockState(blockpos);
         if (blockstate.getBlock() == Blocks.CHEST) {
            return p_197528_2_;
         }

         if (blockstate.isOpaqueCube(p_197528_0_, blockpos)) {
            if (direction != null) {
               direction = null;
               break;
            }

            direction = direction1;
         }
      }

      if (direction != null) {
         return (BlockState)p_197528_2_.with(HorizontalBlock.HORIZONTAL_FACING, direction.getOpposite());
      } else {
         Direction direction2 = (Direction)p_197528_2_.get(HorizontalBlock.HORIZONTAL_FACING);
         BlockPos blockpos1 = p_197528_1_.offset(direction2);
         if (p_197528_0_.getBlockState(blockpos1).isOpaqueCube(p_197528_0_, blockpos1)) {
            direction2 = direction2.getOpposite();
            blockpos1 = p_197528_1_.offset(direction2);
         }

         if (p_197528_0_.getBlockState(blockpos1).isOpaqueCube(p_197528_0_, blockpos1)) {
            direction2 = direction2.rotateY();
            blockpos1 = p_197528_1_.offset(direction2);
         }

         if (p_197528_0_.getBlockState(blockpos1).isOpaqueCube(p_197528_0_, blockpos1)) {
            direction2 = direction2.getOpposite();
            p_197528_1_.offset(direction2);
         }

         return (BlockState)p_197528_2_.with(HorizontalBlock.HORIZONTAL_FACING, direction2);
      }
   }

   protected boolean generateChest(IWorld p_191080_1_, MutableBoundingBox p_191080_2_, Random p_191080_3_, BlockPos p_191080_4_, ResourceLocation p_191080_5_, @Nullable BlockState p_191080_6_) {
      if (p_191080_2_.isVecInside(p_191080_4_) && p_191080_1_.getBlockState(p_191080_4_).getBlock() != Blocks.CHEST) {
         if (p_191080_6_ == null) {
            p_191080_6_ = func_197528_a(p_191080_1_, p_191080_4_, Blocks.CHEST.getDefaultState());
         }

         p_191080_1_.setBlockState(p_191080_4_, p_191080_6_, 2);
         TileEntity tileentity = p_191080_1_.getTileEntity(p_191080_4_);
         if (tileentity instanceof ChestTileEntity) {
            ((ChestTileEntity)tileentity).setLootTable(p_191080_5_, p_191080_3_.nextLong());
         }

         return true;
      } else {
         return false;
      }
   }

   protected boolean createDispenser(IWorld p_189419_1_, MutableBoundingBox p_189419_2_, Random p_189419_3_, int p_189419_4_, int p_189419_5_, int p_189419_6_, Direction p_189419_7_, ResourceLocation p_189419_8_) {
      BlockPos blockpos = new BlockPos(this.getXWithOffset(p_189419_4_, p_189419_6_), this.getYWithOffset(p_189419_5_), this.getZWithOffset(p_189419_4_, p_189419_6_));
      if (p_189419_2_.isVecInside(blockpos) && p_189419_1_.getBlockState(blockpos).getBlock() != Blocks.DISPENSER) {
         this.setBlockState(p_189419_1_, (BlockState)Blocks.DISPENSER.getDefaultState().with(DispenserBlock.FACING, p_189419_7_), p_189419_4_, p_189419_5_, p_189419_6_, p_189419_2_);
         TileEntity tileentity = p_189419_1_.getTileEntity(blockpos);
         if (tileentity instanceof DispenserTileEntity) {
            ((DispenserTileEntity)tileentity).setLootTable(p_189419_8_, p_189419_3_.nextLong());
         }

         return true;
      } else {
         return false;
      }
   }

   public void offset(int p_181138_1_, int p_181138_2_, int p_181138_3_) {
      this.boundingBox.offset(p_181138_1_, p_181138_2_, p_181138_3_);
   }

   @Nullable
   public Direction getCoordBaseMode() {
      return this.field_74885_f;
   }

   public void setCoordBaseMode(@Nullable Direction p_186164_1_) {
      this.field_74885_f = p_186164_1_;
      if (p_186164_1_ == null) {
         this.rotation = Rotation.NONE;
         this.mirror = Mirror.NONE;
      } else {
         switch(p_186164_1_) {
         case SOUTH:
            this.mirror = Mirror.LEFT_RIGHT;
            this.rotation = Rotation.NONE;
            break;
         case WEST:
            this.mirror = Mirror.LEFT_RIGHT;
            this.rotation = Rotation.CLOCKWISE_90;
            break;
         case EAST:
            this.mirror = Mirror.NONE;
            this.rotation = Rotation.CLOCKWISE_90;
            break;
         default:
            this.mirror = Mirror.NONE;
            this.rotation = Rotation.NONE;
         }
      }

   }

   public Rotation getRotation() {
      return this.rotation;
   }

   public IStructurePieceType func_214807_k() {
      return this.field_214811_d;
   }

   static {
      CAVE_AIR = Blocks.CAVE_AIR.getDefaultState();
      BLOCKS_NEEDING_POSTPROCESSING = ImmutableSet.builder().add(Blocks.NETHER_BRICK_FENCE).add(Blocks.TORCH).add(Blocks.WALL_TORCH).add(Blocks.OAK_FENCE).add(Blocks.SPRUCE_FENCE).add(Blocks.DARK_OAK_FENCE).add(Blocks.ACACIA_FENCE).add(Blocks.BIRCH_FENCE).add(Blocks.JUNGLE_FENCE).add(Blocks.LADDER).add(Blocks.IRON_BARS).build();
   }

   public abstract static class BlockSelector {
      protected BlockState blockstate;

      public BlockSelector() {
         this.blockstate = Blocks.AIR.getDefaultState();
      }

      public abstract void selectBlocks(Random var1, int var2, int var3, int var4, boolean var5);

      public BlockState getBlockState() {
         return this.blockstate;
      }
   }
}
