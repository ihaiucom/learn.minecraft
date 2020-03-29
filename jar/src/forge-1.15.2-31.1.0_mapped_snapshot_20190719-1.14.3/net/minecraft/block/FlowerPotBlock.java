package net.minecraft.block;

import com.google.common.collect.Maps;
import java.util.Collections;
import java.util.Map;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.Stats;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class FlowerPotBlock extends Block {
   private static final Map<Block, Block> field_196451_b = Maps.newHashMap();
   protected static final VoxelShape SHAPE = Block.makeCuboidShape(5.0D, 0.0D, 5.0D, 11.0D, 6.0D, 11.0D);
   private final Block flower;
   private final Map<ResourceLocation, Supplier<? extends Block>> fullPots;
   private final Supplier<FlowerPotBlock> emptyPot;
   private final Supplier<? extends Block> flowerDelegate;

   /** @deprecated */
   @Deprecated
   public FlowerPotBlock(Block p_i48395_1_, Block.Properties p_i48395_2_) {
      this(Blocks.FLOWER_POT == null ? null : () -> {
         return (FlowerPotBlock)Blocks.FLOWER_POT.delegate.get();
      }, () -> {
         return (Block)p_i48395_1_.delegate.get();
      }, p_i48395_2_);
      if (Blocks.FLOWER_POT != null) {
         ((FlowerPotBlock)Blocks.FLOWER_POT).addPlant(p_i48395_1_.getRegistryName(), () -> {
            return this;
         });
      }

   }

   public FlowerPotBlock(@Nullable Supplier<FlowerPotBlock> p_i230067_1_, Supplier<? extends Block> p_i230067_2_, Block.Properties p_i230067_3_) {
      super(p_i230067_3_);
      this.flower = null;
      this.flowerDelegate = p_i230067_2_;
      if (p_i230067_1_ == null) {
         this.fullPots = Maps.newHashMap();
         this.emptyPot = null;
      } else {
         this.fullPots = Collections.emptyMap();
         this.emptyPot = p_i230067_1_;
      }

   }

   public VoxelShape getShape(BlockState p_220053_1_, IBlockReader p_220053_2_, BlockPos p_220053_3_, ISelectionContext p_220053_4_) {
      return SHAPE;
   }

   public BlockRenderType getRenderType(BlockState p_149645_1_) {
      return BlockRenderType.MODEL;
   }

   public ActionResultType func_225533_a_(BlockState p_225533_1_, World p_225533_2_, BlockPos p_225533_3_, PlayerEntity p_225533_4_, Hand p_225533_5_, BlockRayTraceResult p_225533_6_) {
      ItemStack itemstack = p_225533_4_.getHeldItem(p_225533_5_);
      Item item = itemstack.getItem();
      Block block = item instanceof BlockItem ? (Block)((Supplier)this.getEmptyPot().fullPots.getOrDefault(((BlockItem)item).getBlock().getRegistryName(), Blocks.AIR.delegate)).get() : Blocks.AIR;
      boolean flag = block == Blocks.AIR;
      boolean flag1 = this.flower == Blocks.AIR;
      if (flag != flag1) {
         if (flag1) {
            p_225533_2_.setBlockState(p_225533_3_, block.getDefaultState(), 3);
            p_225533_4_.addStat(Stats.POT_FLOWER);
            if (!p_225533_4_.abilities.isCreativeMode) {
               itemstack.shrink(1);
            }
         } else {
            ItemStack itemstack1 = new ItemStack(this.flower);
            if (itemstack.isEmpty()) {
               p_225533_4_.setHeldItem(p_225533_5_, itemstack1);
            } else if (!p_225533_4_.addItemStackToInventory(itemstack1)) {
               p_225533_4_.dropItem(itemstack1, false);
            }

            p_225533_2_.setBlockState(p_225533_3_, this.getEmptyPot().getDefaultState(), 3);
         }

         return ActionResultType.SUCCESS;
      } else {
         return ActionResultType.CONSUME;
      }
   }

   public ItemStack getItem(IBlockReader p_185473_1_, BlockPos p_185473_2_, BlockState p_185473_3_) {
      return this.flower == Blocks.AIR ? super.getItem(p_185473_1_, p_185473_2_, p_185473_3_) : new ItemStack(this.flower);
   }

   public BlockState updatePostPlacement(BlockState p_196271_1_, Direction p_196271_2_, BlockState p_196271_3_, IWorld p_196271_4_, BlockPos p_196271_5_, BlockPos p_196271_6_) {
      return p_196271_2_ == Direction.DOWN && !p_196271_1_.isValidPosition(p_196271_4_, p_196271_5_) ? Blocks.AIR.getDefaultState() : super.updatePostPlacement(p_196271_1_, p_196271_2_, p_196271_3_, p_196271_4_, p_196271_5_, p_196271_6_);
   }

   public Block func_220276_d() {
      return (Block)this.flowerDelegate.get();
   }

   public FlowerPotBlock getEmptyPot() {
      return this.emptyPot == null ? this : (FlowerPotBlock)this.emptyPot.get();
   }

   public void addPlant(ResourceLocation p_addPlant_1_, Supplier<? extends Block> p_addPlant_2_) {
      if (this.getEmptyPot() != this) {
         throw new IllegalArgumentException("Cannot add plant to non-empty pot: " + this);
      } else {
         this.fullPots.put(p_addPlant_1_, p_addPlant_2_);
      }
   }
}
