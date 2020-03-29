package net.minecraft.item;

import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.DeadCoralWallFanBlock;
import net.minecraft.block.IGrowable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraftforge.event.ForgeEventFactory;

public class BoneMealItem extends Item {
   public BoneMealItem(Item.Properties p_i50055_1_) {
      super(p_i50055_1_);
   }

   public ActionResultType onItemUse(ItemUseContext p_195939_1_) {
      World world = p_195939_1_.getWorld();
      BlockPos blockpos = p_195939_1_.getPos();
      BlockPos blockpos1 = blockpos.offset(p_195939_1_.getFace());
      if (applyBonemeal(p_195939_1_.getItem(), world, blockpos, p_195939_1_.getPlayer())) {
         if (!world.isRemote) {
            world.playEvent(2005, blockpos, 0);
         }

         return ActionResultType.SUCCESS;
      } else {
         BlockState blockstate = world.getBlockState(blockpos);
         boolean flag = blockstate.func_224755_d(world, blockpos, p_195939_1_.getFace());
         if (flag && growSeagrass(p_195939_1_.getItem(), world, blockpos1, p_195939_1_.getFace())) {
            if (!world.isRemote) {
               world.playEvent(2005, blockpos1, 0);
            }

            return ActionResultType.SUCCESS;
         } else {
            return ActionResultType.PASS;
         }
      }
   }

   /** @deprecated */
   @Deprecated
   public static boolean applyBonemeal(ItemStack p_195966_0_, World p_195966_1_, BlockPos p_195966_2_) {
      return p_195966_1_ instanceof ServerWorld ? applyBonemeal(p_195966_0_, p_195966_1_, p_195966_2_, FakePlayerFactory.getMinecraft((ServerWorld)p_195966_1_)) : false;
   }

   public static boolean applyBonemeal(ItemStack p_applyBonemeal_0_, World p_applyBonemeal_1_, BlockPos p_applyBonemeal_2_, PlayerEntity p_applyBonemeal_3_) {
      BlockState blockstate = p_applyBonemeal_1_.getBlockState(p_applyBonemeal_2_);
      int hook = ForgeEventFactory.onApplyBonemeal(p_applyBonemeal_3_, p_applyBonemeal_1_, p_applyBonemeal_2_, blockstate, p_applyBonemeal_0_);
      if (hook != 0) {
         return hook > 0;
      } else {
         if (blockstate.getBlock() instanceof IGrowable) {
            IGrowable igrowable = (IGrowable)blockstate.getBlock();
            if (igrowable.canGrow(p_applyBonemeal_1_, p_applyBonemeal_2_, blockstate, p_applyBonemeal_1_.isRemote)) {
               if (p_applyBonemeal_1_ instanceof ServerWorld) {
                  if (igrowable.canUseBonemeal(p_applyBonemeal_1_, p_applyBonemeal_1_.rand, p_applyBonemeal_2_, blockstate)) {
                     igrowable.func_225535_a_((ServerWorld)p_applyBonemeal_1_, p_applyBonemeal_1_.rand, p_applyBonemeal_2_, blockstate);
                  }

                  p_applyBonemeal_0_.shrink(1);
               }

               return true;
            }
         }

         return false;
      }
   }

   public static boolean growSeagrass(ItemStack p_203173_0_, World p_203173_1_, BlockPos p_203173_2_, @Nullable Direction p_203173_3_) {
      if (p_203173_1_.getBlockState(p_203173_2_).getBlock() == Blocks.WATER && p_203173_1_.getFluidState(p_203173_2_).getLevel() == 8) {
         if (!(p_203173_1_ instanceof ServerWorld)) {
            return true;
         } else {
            label77:
            for(int i = 0; i < 128; ++i) {
               BlockPos blockpos = p_203173_2_;
               Biome biome = p_203173_1_.func_226691_t_(p_203173_2_);
               BlockState blockstate = Blocks.SEAGRASS.getDefaultState();

               int k;
               for(k = 0; k < i / 16; ++k) {
                  blockpos = blockpos.add(random.nextInt(3) - 1, (random.nextInt(3) - 1) * random.nextInt(3) / 2, random.nextInt(3) - 1);
                  biome = p_203173_1_.func_226691_t_(blockpos);
                  if (p_203173_1_.getBlockState(blockpos).func_224756_o(p_203173_1_, blockpos)) {
                     continue label77;
                  }
               }

               if (BiomeDictionary.hasType(biome, BiomeDictionary.Type.OCEAN) && BiomeDictionary.hasType(biome, BiomeDictionary.Type.HOT)) {
                  if (i == 0 && p_203173_3_ != null && p_203173_3_.getAxis().isHorizontal()) {
                     blockstate = (BlockState)((Block)BlockTags.WALL_CORALS.getRandomElement(p_203173_1_.rand)).getDefaultState().with(DeadCoralWallFanBlock.FACING, p_203173_3_);
                  } else if (random.nextInt(4) == 0) {
                     blockstate = ((Block)BlockTags.UNDERWATER_BONEMEALS.getRandomElement(random)).getDefaultState();
                  }
               }

               if (blockstate.getBlock().isIn(BlockTags.WALL_CORALS)) {
                  for(k = 0; !blockstate.isValidPosition(p_203173_1_, blockpos) && k < 4; ++k) {
                     blockstate = (BlockState)blockstate.with(DeadCoralWallFanBlock.FACING, Direction.Plane.HORIZONTAL.random(random));
                  }
               }

               if (blockstate.isValidPosition(p_203173_1_, blockpos)) {
                  BlockState blockstate1 = p_203173_1_.getBlockState(blockpos);
                  if (blockstate1.getBlock() == Blocks.WATER && p_203173_1_.getFluidState(blockpos).getLevel() == 8) {
                     p_203173_1_.setBlockState(blockpos, blockstate, 3);
                  } else if (blockstate1.getBlock() == Blocks.SEAGRASS && random.nextInt(10) == 0) {
                     ((IGrowable)Blocks.SEAGRASS).func_225535_a_((ServerWorld)p_203173_1_, random, blockpos, blockstate1);
                  }
               }
            }

            p_203173_0_.shrink(1);
            return true;
         }
      } else {
         return false;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static void spawnBonemealParticles(IWorld p_195965_0_, BlockPos p_195965_1_, int p_195965_2_) {
      if (p_195965_2_ == 0) {
         p_195965_2_ = 15;
      }

      BlockState blockstate = p_195965_0_.getBlockState(p_195965_1_);
      if (!blockstate.isAir(p_195965_0_, p_195965_1_)) {
         for(int i = 0; i < p_195965_2_; ++i) {
            double d0 = random.nextGaussian() * 0.02D;
            double d1 = random.nextGaussian() * 0.02D;
            double d2 = random.nextGaussian() * 0.02D;
            p_195965_0_.addParticle(ParticleTypes.HAPPY_VILLAGER, (double)((float)p_195965_1_.getX() + random.nextFloat()), (double)p_195965_1_.getY() + (double)random.nextFloat() * blockstate.getShape(p_195965_0_, p_195965_1_).getEnd(Direction.Axis.Y), (double)((float)p_195965_1_.getZ() + random.nextFloat()), d0, d1, d2);
         }
      }

   }
}
