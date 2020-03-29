package net.minecraft.block;

import com.google.common.cache.LoadingCache;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.pattern.BlockPattern;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.CachedBlockInfo;
import net.minecraft.util.Direction;
import net.minecraft.util.Rotation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.GameRules;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.ForgeEventFactory;

public class NetherPortalBlock extends Block {
   public static final EnumProperty<Direction.Axis> AXIS;
   protected static final VoxelShape X_AABB;
   protected static final VoxelShape Z_AABB;

   public NetherPortalBlock(Block.Properties p_i48352_1_) {
      super(p_i48352_1_);
      this.setDefaultState((BlockState)((BlockState)this.stateContainer.getBaseState()).with(AXIS, Direction.Axis.X));
   }

   public VoxelShape getShape(BlockState p_220053_1_, IBlockReader p_220053_2_, BlockPos p_220053_3_, ISelectionContext p_220053_4_) {
      switch((Direction.Axis)p_220053_1_.get(AXIS)) {
      case Z:
         return Z_AABB;
      case X:
      default:
         return X_AABB;
      }
   }

   public void func_225534_a_(BlockState p_225534_1_, ServerWorld p_225534_2_, BlockPos p_225534_3_, Random p_225534_4_) {
      if (p_225534_2_.dimension.isSurfaceWorld() && p_225534_2_.getGameRules().getBoolean(GameRules.DO_MOB_SPAWNING) && p_225534_4_.nextInt(2000) < p_225534_2_.getDifficulty().getId()) {
         while(p_225534_2_.getBlockState(p_225534_3_).getBlock() == this) {
            p_225534_3_ = p_225534_3_.down();
         }

         if (p_225534_2_.getBlockState(p_225534_3_).canEntitySpawn(p_225534_2_, p_225534_3_, EntityType.ZOMBIE_PIGMAN)) {
            Entity entity = EntityType.ZOMBIE_PIGMAN.spawn(p_225534_2_, (CompoundNBT)null, (ITextComponent)null, (PlayerEntity)null, p_225534_3_.up(), SpawnReason.STRUCTURE, false, false);
            if (entity != null) {
               entity.timeUntilPortal = entity.getPortalCooldown();
            }
         }
      }

   }

   public boolean trySpawnPortal(IWorld p_176548_1_, BlockPos p_176548_2_) {
      NetherPortalBlock.Size netherportalblock$size = this.isPortal(p_176548_1_, p_176548_2_);
      if (netherportalblock$size != null && !ForgeEventFactory.onTrySpawnPortal(p_176548_1_, p_176548_2_, netherportalblock$size)) {
         netherportalblock$size.placePortalBlocks();
         return true;
      } else {
         return false;
      }
   }

   @Nullable
   public NetherPortalBlock.Size isPortal(IWorld p_201816_1_, BlockPos p_201816_2_) {
      NetherPortalBlock.Size netherportalblock$size = new NetherPortalBlock.Size(p_201816_1_, p_201816_2_, Direction.Axis.X);
      if (netherportalblock$size.isValid() && netherportalblock$size.portalBlockCount == 0) {
         return netherportalblock$size;
      } else {
         NetherPortalBlock.Size netherportalblock$size1 = new NetherPortalBlock.Size(p_201816_1_, p_201816_2_, Direction.Axis.Z);
         return netherportalblock$size1.isValid() && netherportalblock$size1.portalBlockCount == 0 ? netherportalblock$size1 : null;
      }
   }

   public BlockState updatePostPlacement(BlockState p_196271_1_, Direction p_196271_2_, BlockState p_196271_3_, IWorld p_196271_4_, BlockPos p_196271_5_, BlockPos p_196271_6_) {
      Direction.Axis direction$axis = p_196271_2_.getAxis();
      Direction.Axis direction$axis1 = (Direction.Axis)p_196271_1_.get(AXIS);
      boolean flag = direction$axis1 != direction$axis && direction$axis.isHorizontal();
      return !flag && p_196271_3_.getBlock() != this && !(new NetherPortalBlock.Size(p_196271_4_, p_196271_5_, direction$axis1)).func_208508_f() ? Blocks.AIR.getDefaultState() : super.updatePostPlacement(p_196271_1_, p_196271_2_, p_196271_3_, p_196271_4_, p_196271_5_, p_196271_6_);
   }

   public void onEntityCollision(BlockState p_196262_1_, World p_196262_2_, BlockPos p_196262_3_, Entity p_196262_4_) {
      if (!p_196262_4_.isPassenger() && !p_196262_4_.isBeingRidden() && p_196262_4_.isNonBoss()) {
         p_196262_4_.setPortal(p_196262_3_);
      }

   }

   @OnlyIn(Dist.CLIENT)
   public void animateTick(BlockState p_180655_1_, World p_180655_2_, BlockPos p_180655_3_, Random p_180655_4_) {
      if (p_180655_4_.nextInt(100) == 0) {
         p_180655_2_.playSound((double)p_180655_3_.getX() + 0.5D, (double)p_180655_3_.getY() + 0.5D, (double)p_180655_3_.getZ() + 0.5D, SoundEvents.BLOCK_PORTAL_AMBIENT, SoundCategory.BLOCKS, 0.5F, p_180655_4_.nextFloat() * 0.4F + 0.8F, false);
      }

      for(int i = 0; i < 4; ++i) {
         double d0 = (double)p_180655_3_.getX() + (double)p_180655_4_.nextFloat();
         double d1 = (double)p_180655_3_.getY() + (double)p_180655_4_.nextFloat();
         double d2 = (double)p_180655_3_.getZ() + (double)p_180655_4_.nextFloat();
         double d3 = ((double)p_180655_4_.nextFloat() - 0.5D) * 0.5D;
         double d4 = ((double)p_180655_4_.nextFloat() - 0.5D) * 0.5D;
         double d5 = ((double)p_180655_4_.nextFloat() - 0.5D) * 0.5D;
         int j = p_180655_4_.nextInt(2) * 2 - 1;
         if (p_180655_2_.getBlockState(p_180655_3_.west()).getBlock() != this && p_180655_2_.getBlockState(p_180655_3_.east()).getBlock() != this) {
            d0 = (double)p_180655_3_.getX() + 0.5D + 0.25D * (double)j;
            d3 = (double)(p_180655_4_.nextFloat() * 2.0F * (float)j);
         } else {
            d2 = (double)p_180655_3_.getZ() + 0.5D + 0.25D * (double)j;
            d5 = (double)(p_180655_4_.nextFloat() * 2.0F * (float)j);
         }

         p_180655_2_.addParticle(ParticleTypes.PORTAL, d0, d1, d2, d3, d4, d5);
      }

   }

   public ItemStack getItem(IBlockReader p_185473_1_, BlockPos p_185473_2_, BlockState p_185473_3_) {
      return ItemStack.EMPTY;
   }

   public BlockState rotate(BlockState p_185499_1_, Rotation p_185499_2_) {
      switch(p_185499_2_) {
      case COUNTERCLOCKWISE_90:
      case CLOCKWISE_90:
         switch((Direction.Axis)p_185499_1_.get(AXIS)) {
         case Z:
            return (BlockState)p_185499_1_.with(AXIS, Direction.Axis.X);
         case X:
            return (BlockState)p_185499_1_.with(AXIS, Direction.Axis.Z);
         default:
            return p_185499_1_;
         }
      default:
         return p_185499_1_;
      }
   }

   protected void fillStateContainer(StateContainer.Builder<Block, BlockState> p_206840_1_) {
      p_206840_1_.add(AXIS);
   }

   public static BlockPattern.PatternHelper createPatternHelper(IWorld p_181089_0_, BlockPos p_181089_1_) {
      Direction.Axis direction$axis = Direction.Axis.Z;
      NetherPortalBlock.Size netherportalblock$size = new NetherPortalBlock.Size(p_181089_0_, p_181089_1_, Direction.Axis.X);
      LoadingCache<BlockPos, CachedBlockInfo> loadingcache = BlockPattern.createLoadingCache(p_181089_0_, true);
      if (!netherportalblock$size.isValid()) {
         direction$axis = Direction.Axis.X;
         netherportalblock$size = new NetherPortalBlock.Size(p_181089_0_, p_181089_1_, Direction.Axis.Z);
      }

      if (!netherportalblock$size.isValid()) {
         return new BlockPattern.PatternHelper(p_181089_1_, Direction.NORTH, Direction.UP, loadingcache, 1, 1, 1);
      } else {
         int[] aint = new int[Direction.AxisDirection.values().length];
         Direction direction = netherportalblock$size.rightDir.rotateYCCW();
         BlockPos blockpos = netherportalblock$size.bottomLeft.up(netherportalblock$size.getHeight() - 1);
         Direction.AxisDirection[] var8 = Direction.AxisDirection.values();
         int var9 = var8.length;

         int var10;
         for(var10 = 0; var10 < var9; ++var10) {
            Direction.AxisDirection direction$axisdirection = var8[var10];
            BlockPattern.PatternHelper blockpattern$patternhelper = new BlockPattern.PatternHelper(direction.getAxisDirection() == direction$axisdirection ? blockpos : blockpos.offset(netherportalblock$size.rightDir, netherportalblock$size.getWidth() - 1), Direction.getFacingFromAxis(direction$axisdirection, direction$axis), Direction.UP, loadingcache, netherportalblock$size.getWidth(), netherportalblock$size.getHeight(), 1);

            for(int i = 0; i < netherportalblock$size.getWidth(); ++i) {
               for(int j = 0; j < netherportalblock$size.getHeight(); ++j) {
                  CachedBlockInfo cachedblockinfo = blockpattern$patternhelper.translateOffset(i, j, 1);
                  if (!cachedblockinfo.getBlockState().isAir()) {
                     ++aint[direction$axisdirection.ordinal()];
                  }
               }
            }
         }

         Direction.AxisDirection direction$axisdirection1 = Direction.AxisDirection.POSITIVE;
         Direction.AxisDirection[] var17 = Direction.AxisDirection.values();
         var10 = var17.length;

         for(int var18 = 0; var18 < var10; ++var18) {
            Direction.AxisDirection direction$axisdirection2 = var17[var18];
            if (aint[direction$axisdirection2.ordinal()] < aint[direction$axisdirection1.ordinal()]) {
               direction$axisdirection1 = direction$axisdirection2;
            }
         }

         return new BlockPattern.PatternHelper(direction.getAxisDirection() == direction$axisdirection1 ? blockpos : blockpos.offset(netherportalblock$size.rightDir, netherportalblock$size.getWidth() - 1), Direction.getFacingFromAxis(direction$axisdirection1, direction$axis), Direction.UP, loadingcache, netherportalblock$size.getWidth(), netherportalblock$size.getHeight(), 1);
      }
   }

   static {
      AXIS = BlockStateProperties.HORIZONTAL_AXIS;
      X_AABB = Block.makeCuboidShape(0.0D, 0.0D, 6.0D, 16.0D, 16.0D, 10.0D);
      Z_AABB = Block.makeCuboidShape(6.0D, 0.0D, 0.0D, 10.0D, 16.0D, 16.0D);
   }

   public static class Size {
      private final IWorld world;
      private final Direction.Axis axis;
      private final Direction rightDir;
      private final Direction leftDir;
      private int portalBlockCount;
      @Nullable
      private BlockPos bottomLeft;
      private int height;
      private int width;

      public Size(IWorld p_i48740_1_, BlockPos p_i48740_2_, Direction.Axis p_i48740_3_) {
         this.world = p_i48740_1_;
         this.axis = p_i48740_3_;
         if (p_i48740_3_ == Direction.Axis.X) {
            this.leftDir = Direction.EAST;
            this.rightDir = Direction.WEST;
         } else {
            this.leftDir = Direction.NORTH;
            this.rightDir = Direction.SOUTH;
         }

         for(BlockPos blockpos = p_i48740_2_; p_i48740_2_.getY() > blockpos.getY() - 21 && p_i48740_2_.getY() > 0 && this.func_196900_a(p_i48740_1_.getBlockState(p_i48740_2_.down())); p_i48740_2_ = p_i48740_2_.down()) {
         }

         int i = this.getDistanceUntilEdge(p_i48740_2_, this.leftDir) - 1;
         if (i >= 0) {
            this.bottomLeft = p_i48740_2_.offset(this.leftDir, i);
            this.width = this.getDistanceUntilEdge(this.bottomLeft, this.rightDir);
            if (this.width < 2 || this.width > 21) {
               this.bottomLeft = null;
               this.width = 0;
            }
         }

         if (this.bottomLeft != null) {
            this.height = this.calculatePortalHeight();
         }

      }

      protected int getDistanceUntilEdge(BlockPos p_180120_1_, Direction p_180120_2_) {
         int i;
         BlockPos blockpos;
         for(i = 0; i < 22; ++i) {
            blockpos = p_180120_1_.offset(p_180120_2_, i);
            if (!this.func_196900_a(this.world.getBlockState(blockpos)) || !this.world.getBlockState(blockpos.down()).isPortalFrame(this.world, blockpos.down())) {
               break;
            }
         }

         blockpos = p_180120_1_.offset(p_180120_2_, i);
         return this.world.getBlockState(blockpos).isPortalFrame(this.world, blockpos) ? i : 0;
      }

      public int getHeight() {
         return this.height;
      }

      public int getWidth() {
         return this.width;
      }

      protected int calculatePortalHeight() {
         int i;
         BlockPos blockpos;
         label57:
         for(this.height = 0; this.height < 21; ++this.height) {
            for(i = 0; i < this.width; ++i) {
               blockpos = this.bottomLeft.offset(this.rightDir, i).up(this.height);
               BlockState blockstate = this.world.getBlockState(blockpos);
               if (!this.func_196900_a(blockstate)) {
                  break label57;
               }

               Block block = blockstate.getBlock();
               if (block == Blocks.NETHER_PORTAL) {
                  ++this.portalBlockCount;
               }

               BlockPos framePos;
               if (i == 0) {
                  framePos = blockpos.offset(this.leftDir);
                  if (!this.world.getBlockState(framePos).isPortalFrame(this.world, framePos)) {
                     break label57;
                  }
               } else if (i == this.width - 1) {
                  framePos = blockpos.offset(this.rightDir);
                  if (!this.world.getBlockState(framePos).isPortalFrame(this.world, framePos)) {
                     break label57;
                  }
               }
            }
         }

         for(i = 0; i < this.width; ++i) {
            blockpos = this.bottomLeft.offset(this.rightDir, i).up(this.height);
            if (!this.world.getBlockState(blockpos).isPortalFrame(this.world, blockpos)) {
               this.height = 0;
               break;
            }
         }

         if (this.height <= 21 && this.height >= 3) {
            return this.height;
         } else {
            this.bottomLeft = null;
            this.width = 0;
            this.height = 0;
            return 0;
         }
      }

      protected boolean func_196900_a(BlockState p_196900_1_) {
         Block block = p_196900_1_.getBlock();
         return p_196900_1_.isAir() || block == Blocks.FIRE || block == Blocks.NETHER_PORTAL;
      }

      public boolean isValid() {
         return this.bottomLeft != null && this.width >= 2 && this.width <= 21 && this.height >= 3 && this.height <= 21;
      }

      public void placePortalBlocks() {
         for(int i = 0; i < this.width; ++i) {
            BlockPos blockpos = this.bottomLeft.offset(this.rightDir, i);

            for(int j = 0; j < this.height; ++j) {
               this.world.setBlockState(blockpos.up(j), (BlockState)Blocks.NETHER_PORTAL.getDefaultState().with(NetherPortalBlock.AXIS, this.axis), 18);
            }
         }

      }

      private boolean func_196899_f() {
         return this.portalBlockCount >= this.width * this.height;
      }

      public boolean func_208508_f() {
         return this.isValid() && this.func_196899_f();
      }
   }
}
