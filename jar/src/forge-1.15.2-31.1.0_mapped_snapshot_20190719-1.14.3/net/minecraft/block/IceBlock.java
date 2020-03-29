package net.minecraft.block;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.PushReaction;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class IceBlock extends BreakableBlock {
   public IceBlock(Block.Properties p_i48375_1_) {
      super(p_i48375_1_);
   }

   public void harvestBlock(World p_180657_1_, PlayerEntity p_180657_2_, BlockPos p_180657_3_, BlockState p_180657_4_, @Nullable TileEntity p_180657_5_, ItemStack p_180657_6_) {
      super.harvestBlock(p_180657_1_, p_180657_2_, p_180657_3_, p_180657_4_, p_180657_5_, p_180657_6_);
      if (EnchantmentHelper.getEnchantmentLevel(Enchantments.SILK_TOUCH, p_180657_6_) == 0) {
         if (p_180657_1_.dimension.doesWaterVaporize()) {
            p_180657_1_.removeBlock(p_180657_3_, false);
            return;
         }

         Material lvt_7_1_ = p_180657_1_.getBlockState(p_180657_3_.down()).getMaterial();
         if (lvt_7_1_.blocksMovement() || lvt_7_1_.isLiquid()) {
            p_180657_1_.setBlockState(p_180657_3_, Blocks.WATER.getDefaultState());
         }
      }

   }

   public void func_225534_a_(BlockState p_225534_1_, ServerWorld p_225534_2_, BlockPos p_225534_3_, Random p_225534_4_) {
      if (p_225534_2_.func_226658_a_(LightType.BLOCK, p_225534_3_) > 11 - p_225534_1_.getOpacity(p_225534_2_, p_225534_3_)) {
         this.turnIntoWater(p_225534_1_, p_225534_2_, p_225534_3_);
      }

   }

   protected void turnIntoWater(BlockState p_196454_1_, World p_196454_2_, BlockPos p_196454_3_) {
      if (p_196454_2_.dimension.doesWaterVaporize()) {
         p_196454_2_.removeBlock(p_196454_3_, false);
      } else {
         p_196454_2_.setBlockState(p_196454_3_, Blocks.WATER.getDefaultState());
         p_196454_2_.neighborChanged(p_196454_3_, Blocks.WATER, p_196454_3_);
      }
   }

   public PushReaction getPushReaction(BlockState p_149656_1_) {
      return PushReaction.NORMAL;
   }

   public boolean canEntitySpawn(BlockState p_220067_1_, IBlockReader p_220067_2_, BlockPos p_220067_3_, EntityType<?> p_220067_4_) {
      return p_220067_4_ == EntityType.POLAR_BEAR;
   }
}
