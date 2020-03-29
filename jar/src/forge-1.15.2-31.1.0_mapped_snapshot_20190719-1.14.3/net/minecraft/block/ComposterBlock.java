package net.minecraft.block;

import it.unimi.dsi.fastutil.objects.Object2FloatMap;
import it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.ISidedInventoryProvider;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ComposterBlock extends Block implements ISidedInventoryProvider {
   public static final IntegerProperty field_220298_a;
   public static final Object2FloatMap<IItemProvider> CHANCES;
   public static final VoxelShape field_220300_c;
   private static final VoxelShape[] field_220301_d;

   public static void init() {
      CHANCES.defaultReturnValue(-1.0F);
      float lvt_0_1_ = 0.3F;
      float lvt_1_1_ = 0.5F;
      float lvt_2_1_ = 0.65F;
      float lvt_3_1_ = 0.85F;
      float lvt_4_1_ = 1.0F;
      registerCompostable(0.3F, Items.JUNGLE_LEAVES);
      registerCompostable(0.3F, Items.OAK_LEAVES);
      registerCompostable(0.3F, Items.SPRUCE_LEAVES);
      registerCompostable(0.3F, Items.DARK_OAK_LEAVES);
      registerCompostable(0.3F, Items.ACACIA_LEAVES);
      registerCompostable(0.3F, Items.BIRCH_LEAVES);
      registerCompostable(0.3F, Items.OAK_SAPLING);
      registerCompostable(0.3F, Items.SPRUCE_SAPLING);
      registerCompostable(0.3F, Items.BIRCH_SAPLING);
      registerCompostable(0.3F, Items.JUNGLE_SAPLING);
      registerCompostable(0.3F, Items.ACACIA_SAPLING);
      registerCompostable(0.3F, Items.DARK_OAK_SAPLING);
      registerCompostable(0.3F, Items.BEETROOT_SEEDS);
      registerCompostable(0.3F, Items.DRIED_KELP);
      registerCompostable(0.3F, Items.GRASS);
      registerCompostable(0.3F, Items.KELP);
      registerCompostable(0.3F, Items.MELON_SEEDS);
      registerCompostable(0.3F, Items.PUMPKIN_SEEDS);
      registerCompostable(0.3F, Items.SEAGRASS);
      registerCompostable(0.3F, Items.SWEET_BERRIES);
      registerCompostable(0.3F, Items.WHEAT_SEEDS);
      registerCompostable(0.5F, Items.DRIED_KELP_BLOCK);
      registerCompostable(0.5F, Items.TALL_GRASS);
      registerCompostable(0.5F, Items.CACTUS);
      registerCompostable(0.5F, Items.SUGAR_CANE);
      registerCompostable(0.5F, Items.VINE);
      registerCompostable(0.5F, Items.MELON_SLICE);
      registerCompostable(0.65F, Items.SEA_PICKLE);
      registerCompostable(0.65F, Items.LILY_PAD);
      registerCompostable(0.65F, Items.PUMPKIN);
      registerCompostable(0.65F, Items.CARVED_PUMPKIN);
      registerCompostable(0.65F, Items.MELON);
      registerCompostable(0.65F, Items.APPLE);
      registerCompostable(0.65F, Items.BEETROOT);
      registerCompostable(0.65F, Items.CARROT);
      registerCompostable(0.65F, Items.COCOA_BEANS);
      registerCompostable(0.65F, Items.POTATO);
      registerCompostable(0.65F, Items.WHEAT);
      registerCompostable(0.65F, Items.BROWN_MUSHROOM);
      registerCompostable(0.65F, Items.RED_MUSHROOM);
      registerCompostable(0.65F, Items.MUSHROOM_STEM);
      registerCompostable(0.65F, Items.DANDELION);
      registerCompostable(0.65F, Items.POPPY);
      registerCompostable(0.65F, Items.BLUE_ORCHID);
      registerCompostable(0.65F, Items.ALLIUM);
      registerCompostable(0.65F, Items.AZURE_BLUET);
      registerCompostable(0.65F, Items.RED_TULIP);
      registerCompostable(0.65F, Items.ORANGE_TULIP);
      registerCompostable(0.65F, Items.WHITE_TULIP);
      registerCompostable(0.65F, Items.PINK_TULIP);
      registerCompostable(0.65F, Items.OXEYE_DAISY);
      registerCompostable(0.65F, Items.CORNFLOWER);
      registerCompostable(0.65F, Items.LILY_OF_THE_VALLEY);
      registerCompostable(0.65F, Items.WITHER_ROSE);
      registerCompostable(0.65F, Items.FERN);
      registerCompostable(0.65F, Items.SUNFLOWER);
      registerCompostable(0.65F, Items.LILAC);
      registerCompostable(0.65F, Items.ROSE_BUSH);
      registerCompostable(0.65F, Items.PEONY);
      registerCompostable(0.65F, Items.LARGE_FERN);
      registerCompostable(0.85F, Items.HAY_BLOCK);
      registerCompostable(0.85F, Items.BROWN_MUSHROOM_BLOCK);
      registerCompostable(0.85F, Items.RED_MUSHROOM_BLOCK);
      registerCompostable(0.85F, Items.BREAD);
      registerCompostable(0.85F, Items.BAKED_POTATO);
      registerCompostable(0.85F, Items.COOKIE);
      registerCompostable(1.0F, Items.CAKE);
      registerCompostable(1.0F, Items.PUMPKIN_PIE);
   }

   private static void registerCompostable(float p_220290_0_, IItemProvider p_220290_1_) {
      CHANCES.put(p_220290_1_.asItem(), p_220290_0_);
   }

   public ComposterBlock(Block.Properties p_i49986_1_) {
      super(p_i49986_1_);
      this.setDefaultState((BlockState)((BlockState)this.stateContainer.getBaseState()).with(field_220298_a, 0));
   }

   @OnlyIn(Dist.CLIENT)
   public static void func_220292_a(World p_220292_0_, BlockPos p_220292_1_, boolean p_220292_2_) {
      BlockState lvt_3_1_ = p_220292_0_.getBlockState(p_220292_1_);
      p_220292_0_.playSound((double)p_220292_1_.getX(), (double)p_220292_1_.getY(), (double)p_220292_1_.getZ(), p_220292_2_ ? SoundEvents.BLOCK_COMPOSTER_FILL_SUCCESS : SoundEvents.BLOCK_COMPOSTER_FILL, SoundCategory.BLOCKS, 1.0F, 1.0F, false);
      double lvt_4_1_ = lvt_3_1_.getShape(p_220292_0_, p_220292_1_).max(Direction.Axis.Y, 0.5D, 0.5D) + 0.03125D;
      double lvt_6_1_ = 0.13124999403953552D;
      double lvt_8_1_ = 0.737500011920929D;
      Random lvt_10_1_ = p_220292_0_.getRandom();

      for(int lvt_11_1_ = 0; lvt_11_1_ < 10; ++lvt_11_1_) {
         double lvt_12_1_ = lvt_10_1_.nextGaussian() * 0.02D;
         double lvt_14_1_ = lvt_10_1_.nextGaussian() * 0.02D;
         double lvt_16_1_ = lvt_10_1_.nextGaussian() * 0.02D;
         p_220292_0_.addParticle(ParticleTypes.COMPOSTER, (double)p_220292_1_.getX() + 0.13124999403953552D + 0.737500011920929D * (double)lvt_10_1_.nextFloat(), (double)p_220292_1_.getY() + lvt_4_1_ + (double)lvt_10_1_.nextFloat() * (1.0D - lvt_4_1_), (double)p_220292_1_.getZ() + 0.13124999403953552D + 0.737500011920929D * (double)lvt_10_1_.nextFloat(), lvt_12_1_, lvt_14_1_, lvt_16_1_);
      }

   }

   public VoxelShape getShape(BlockState p_220053_1_, IBlockReader p_220053_2_, BlockPos p_220053_3_, ISelectionContext p_220053_4_) {
      return field_220301_d[(Integer)p_220053_1_.get(field_220298_a)];
   }

   public VoxelShape getRaytraceShape(BlockState p_199600_1_, IBlockReader p_199600_2_, BlockPos p_199600_3_) {
      return field_220300_c;
   }

   public VoxelShape getCollisionShape(BlockState p_220071_1_, IBlockReader p_220071_2_, BlockPos p_220071_3_, ISelectionContext p_220071_4_) {
      return field_220301_d[0];
   }

   public void onBlockAdded(BlockState p_220082_1_, World p_220082_2_, BlockPos p_220082_3_, BlockState p_220082_4_, boolean p_220082_5_) {
      if ((Integer)p_220082_1_.get(field_220298_a) == 7) {
         p_220082_2_.getPendingBlockTicks().scheduleTick(p_220082_3_, p_220082_1_.getBlock(), 20);
      }

   }

   public ActionResultType func_225533_a_(BlockState p_225533_1_, World p_225533_2_, BlockPos p_225533_3_, PlayerEntity p_225533_4_, Hand p_225533_5_, BlockRayTraceResult p_225533_6_) {
      int lvt_7_1_ = (Integer)p_225533_1_.get(field_220298_a);
      ItemStack lvt_8_1_ = p_225533_4_.getHeldItem(p_225533_5_);
      if (lvt_7_1_ < 8 && CHANCES.containsKey(lvt_8_1_.getItem())) {
         if (lvt_7_1_ < 7 && !p_225533_2_.isRemote) {
            boolean lvt_9_1_ = addItem(p_225533_1_, p_225533_2_, p_225533_3_, lvt_8_1_);
            p_225533_2_.playEvent(1500, p_225533_3_, lvt_9_1_ ? 1 : 0);
            if (!p_225533_4_.abilities.isCreativeMode) {
               lvt_8_1_.shrink(1);
            }
         }

         return ActionResultType.SUCCESS;
      } else if (lvt_7_1_ == 8) {
         if (!p_225533_2_.isRemote) {
            float lvt_9_2_ = 0.7F;
            double lvt_10_1_ = (double)(p_225533_2_.rand.nextFloat() * 0.7F) + 0.15000000596046448D;
            double lvt_12_1_ = (double)(p_225533_2_.rand.nextFloat() * 0.7F) + 0.06000000238418579D + 0.6D;
            double lvt_14_1_ = (double)(p_225533_2_.rand.nextFloat() * 0.7F) + 0.15000000596046448D;
            ItemEntity lvt_16_1_ = new ItemEntity(p_225533_2_, (double)p_225533_3_.getX() + lvt_10_1_, (double)p_225533_3_.getY() + lvt_12_1_, (double)p_225533_3_.getZ() + lvt_14_1_, new ItemStack(Items.BONE_MEAL));
            lvt_16_1_.setDefaultPickupDelay();
            p_225533_2_.addEntity(lvt_16_1_);
         }

         clear(p_225533_1_, p_225533_2_, p_225533_3_);
         p_225533_2_.playSound((PlayerEntity)null, p_225533_3_, SoundEvents.BLOCK_COMPOSTER_EMPTY, SoundCategory.BLOCKS, 1.0F, 1.0F);
         return ActionResultType.SUCCESS;
      } else {
         return ActionResultType.PASS;
      }
   }

   private static void clear(BlockState p_220294_0_, IWorld p_220294_1_, BlockPos p_220294_2_) {
      p_220294_1_.setBlockState(p_220294_2_, (BlockState)p_220294_0_.with(field_220298_a, 0), 3);
   }

   private static boolean addItem(BlockState p_220293_0_, IWorld p_220293_1_, BlockPos p_220293_2_, ItemStack p_220293_3_) {
      int lvt_4_1_ = (Integer)p_220293_0_.get(field_220298_a);
      float lvt_5_1_ = CHANCES.getFloat(p_220293_3_.getItem());
      if ((lvt_4_1_ != 0 || lvt_5_1_ <= 0.0F) && p_220293_1_.getRandom().nextDouble() >= (double)lvt_5_1_) {
         return false;
      } else {
         int lvt_6_1_ = lvt_4_1_ + 1;
         p_220293_1_.setBlockState(p_220293_2_, (BlockState)p_220293_0_.with(field_220298_a, lvt_6_1_), 3);
         if (lvt_6_1_ == 7) {
            p_220293_1_.getPendingBlockTicks().scheduleTick(p_220293_2_, p_220293_0_.getBlock(), 20);
         }

         return true;
      }
   }

   public void func_225534_a_(BlockState p_225534_1_, ServerWorld p_225534_2_, BlockPos p_225534_3_, Random p_225534_4_) {
      if ((Integer)p_225534_1_.get(field_220298_a) == 7) {
         p_225534_2_.setBlockState(p_225534_3_, (BlockState)p_225534_1_.cycle(field_220298_a), 3);
         p_225534_2_.playSound((PlayerEntity)null, p_225534_3_, SoundEvents.BLOCK_COMPOSTER_READY, SoundCategory.BLOCKS, 1.0F, 1.0F);
      }

      super.func_225534_a_(p_225534_1_, p_225534_2_, p_225534_3_, p_225534_4_);
   }

   public boolean hasComparatorInputOverride(BlockState p_149740_1_) {
      return true;
   }

   public int getComparatorInputOverride(BlockState p_180641_1_, World p_180641_2_, BlockPos p_180641_3_) {
      return (Integer)p_180641_1_.get(field_220298_a);
   }

   protected void fillStateContainer(StateContainer.Builder<Block, BlockState> p_206840_1_) {
      p_206840_1_.add(field_220298_a);
   }

   public boolean allowsMovement(BlockState p_196266_1_, IBlockReader p_196266_2_, BlockPos p_196266_3_, PathType p_196266_4_) {
      return false;
   }

   public ISidedInventory createInventory(BlockState p_219966_1_, IWorld p_219966_2_, BlockPos p_219966_3_) {
      int lvt_4_1_ = (Integer)p_219966_1_.get(field_220298_a);
      if (lvt_4_1_ == 8) {
         return new ComposterBlock.FullInventory(p_219966_1_, p_219966_2_, p_219966_3_, new ItemStack(Items.BONE_MEAL));
      } else {
         return (ISidedInventory)(lvt_4_1_ < 7 ? new ComposterBlock.PartialInventory(p_219966_1_, p_219966_2_, p_219966_3_) : new ComposterBlock.EmptyInventory());
      }
   }

   static {
      field_220298_a = BlockStateProperties.LEVEL_0_8;
      CHANCES = new Object2FloatOpenHashMap();
      field_220300_c = VoxelShapes.fullCube();
      field_220301_d = (VoxelShape[])Util.make(new VoxelShape[9], (p_220291_0_) -> {
         for(int lvt_1_1_ = 0; lvt_1_1_ < 8; ++lvt_1_1_) {
            p_220291_0_[lvt_1_1_] = VoxelShapes.combineAndSimplify(field_220300_c, Block.makeCuboidShape(2.0D, (double)Math.max(2, 1 + lvt_1_1_ * 2), 2.0D, 14.0D, 16.0D, 14.0D), IBooleanFunction.ONLY_FIRST);
         }

         p_220291_0_[8] = p_220291_0_[7];
      });
   }

   static class PartialInventory extends Inventory implements ISidedInventory {
      private final BlockState state;
      private final IWorld world;
      private final BlockPos pos;
      private boolean inserted;

      public PartialInventory(BlockState p_i50464_1_, IWorld p_i50464_2_, BlockPos p_i50464_3_) {
         super(1);
         this.state = p_i50464_1_;
         this.world = p_i50464_2_;
         this.pos = p_i50464_3_;
      }

      public int getInventoryStackLimit() {
         return 1;
      }

      public int[] getSlotsForFace(Direction p_180463_1_) {
         return p_180463_1_ == Direction.UP ? new int[]{0} : new int[0];
      }

      public boolean canInsertItem(int p_180462_1_, ItemStack p_180462_2_, @Nullable Direction p_180462_3_) {
         return !this.inserted && p_180462_3_ == Direction.UP && ComposterBlock.CHANCES.containsKey(p_180462_2_.getItem());
      }

      public boolean canExtractItem(int p_180461_1_, ItemStack p_180461_2_, Direction p_180461_3_) {
         return false;
      }

      public void markDirty() {
         ItemStack lvt_1_1_ = this.getStackInSlot(0);
         if (!lvt_1_1_.isEmpty()) {
            this.inserted = true;
            ComposterBlock.addItem(this.state, this.world, this.pos, lvt_1_1_);
            this.removeStackFromSlot(0);
         }

      }
   }

   static class FullInventory extends Inventory implements ISidedInventory {
      private final BlockState state;
      private final IWorld world;
      private final BlockPos pos;
      private boolean extracted;

      public FullInventory(BlockState p_i50463_1_, IWorld p_i50463_2_, BlockPos p_i50463_3_, ItemStack p_i50463_4_) {
         super(p_i50463_4_);
         this.state = p_i50463_1_;
         this.world = p_i50463_2_;
         this.pos = p_i50463_3_;
      }

      public int getInventoryStackLimit() {
         return 1;
      }

      public int[] getSlotsForFace(Direction p_180463_1_) {
         return p_180463_1_ == Direction.DOWN ? new int[]{0} : new int[0];
      }

      public boolean canInsertItem(int p_180462_1_, ItemStack p_180462_2_, @Nullable Direction p_180462_3_) {
         return false;
      }

      public boolean canExtractItem(int p_180461_1_, ItemStack p_180461_2_, Direction p_180461_3_) {
         return !this.extracted && p_180461_3_ == Direction.DOWN && p_180461_2_.getItem() == Items.BONE_MEAL;
      }

      public void markDirty() {
         ComposterBlock.clear(this.state, this.world, this.pos);
         this.extracted = true;
      }
   }

   static class EmptyInventory extends Inventory implements ISidedInventory {
      public EmptyInventory() {
         super(0);
      }

      public int[] getSlotsForFace(Direction p_180463_1_) {
         return new int[0];
      }

      public boolean canInsertItem(int p_180462_1_, ItemStack p_180462_2_, @Nullable Direction p_180462_3_) {
         return false;
      }

      public boolean canExtractItem(int p_180461_1_, ItemStack p_180461_2_, Direction p_180461_3_) {
         return false;
      }
   }
}
