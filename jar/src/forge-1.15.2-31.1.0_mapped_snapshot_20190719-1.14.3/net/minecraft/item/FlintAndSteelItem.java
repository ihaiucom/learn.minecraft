package net.minecraft.item;

import java.util.Iterator;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FireBlock;
import net.minecraft.block.NetherPortalBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;

public class FlintAndSteelItem extends Item {
   public FlintAndSteelItem(Item.Properties p_i48493_1_) {
      super(p_i48493_1_);
   }

   public ActionResultType onItemUse(ItemUseContext p_195939_1_) {
      PlayerEntity playerentity = p_195939_1_.getPlayer();
      IWorld iworld = p_195939_1_.getWorld();
      BlockPos blockpos = p_195939_1_.getPos();
      BlockState blockstate = iworld.getBlockState(blockpos);
      if (isUnlitCampfire(blockstate)) {
         iworld.playSound(playerentity, blockpos, SoundEvents.ITEM_FLINTANDSTEEL_USE, SoundCategory.BLOCKS, 1.0F, random.nextFloat() * 0.4F + 0.8F);
         iworld.setBlockState(blockpos, (BlockState)blockstate.with(BlockStateProperties.LIT, true), 11);
         if (playerentity != null) {
            p_195939_1_.getItem().damageItem(1, playerentity, (p_lambda$onItemUse$0_1_) -> {
               p_lambda$onItemUse$0_1_.sendBreakAnimation(p_195939_1_.getHand());
            });
         }

         return ActionResultType.SUCCESS;
      } else {
         BlockPos blockpos1 = blockpos.offset(p_195939_1_.getFace());
         if (func_219996_a(iworld.getBlockState(blockpos1), iworld, blockpos1)) {
            iworld.playSound(playerentity, blockpos1, SoundEvents.ITEM_FLINTANDSTEEL_USE, SoundCategory.BLOCKS, 1.0F, random.nextFloat() * 0.4F + 0.8F);
            BlockState blockstate1 = ((FireBlock)Blocks.FIRE).getStateForPlacement(iworld, blockpos1);
            iworld.setBlockState(blockpos1, blockstate1, 11);
            ItemStack itemstack = p_195939_1_.getItem();
            if (playerentity instanceof ServerPlayerEntity) {
               CriteriaTriggers.PLACED_BLOCK.trigger((ServerPlayerEntity)playerentity, blockpos1, itemstack);
               itemstack.damageItem(1, playerentity, (p_lambda$onItemUse$1_1_) -> {
                  p_lambda$onItemUse$1_1_.sendBreakAnimation(p_195939_1_.getHand());
               });
            }

            return ActionResultType.SUCCESS;
         } else {
            return ActionResultType.FAIL;
         }
      }
   }

   public static boolean isUnlitCampfire(BlockState p_219997_0_) {
      return p_219997_0_.getBlock() == Blocks.CAMPFIRE && !(Boolean)p_219997_0_.get(BlockStateProperties.WATERLOGGED) && !(Boolean)p_219997_0_.get(BlockStateProperties.LIT);
   }

   public static boolean func_219996_a(BlockState p_219996_0_, IWorld p_219996_1_, BlockPos p_219996_2_) {
      BlockState blockstate = ((FireBlock)Blocks.FIRE).getStateForPlacement(p_219996_1_, p_219996_2_);
      boolean flag = false;
      Iterator var5 = Direction.Plane.HORIZONTAL.iterator();

      while(var5.hasNext()) {
         Direction direction = (Direction)var5.next();
         BlockPos framePos = p_219996_2_.offset(direction);
         if (p_219996_1_.getBlockState(framePos).isPortalFrame(p_219996_1_, framePos) && ((NetherPortalBlock)Blocks.NETHER_PORTAL).isPortal(p_219996_1_, p_219996_2_) != null) {
            flag = true;
         }
      }

      return p_219996_0_.isAir() && (blockstate.isValidPosition(p_219996_1_, p_219996_2_) || flag);
   }
}
