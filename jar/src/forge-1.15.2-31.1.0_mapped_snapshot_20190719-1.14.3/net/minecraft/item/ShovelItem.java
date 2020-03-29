package net.minecraft.item;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.Map;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CampfireBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;

public class ShovelItem extends ToolItem {
   private static final Set<Block> EFFECTIVE_ON;
   protected static final Map<Block, BlockState> field_195955_e;

   public ShovelItem(IItemTier p_i48469_1_, float p_i48469_2_, float p_i48469_3_, Item.Properties p_i48469_4_) {
      super(p_i48469_2_, p_i48469_3_, p_i48469_1_, EFFECTIVE_ON, p_i48469_4_.addToolType(ToolType.SHOVEL, p_i48469_1_.getHarvestLevel()));
   }

   public boolean canHarvestBlock(BlockState p_150897_1_) {
      Block block = p_150897_1_.getBlock();
      return block == Blocks.SNOW || block == Blocks.SNOW_BLOCK;
   }

   public ActionResultType onItemUse(ItemUseContext p_195939_1_) {
      World world = p_195939_1_.getWorld();
      BlockPos blockpos = p_195939_1_.getPos();
      BlockState blockstate = world.getBlockState(blockpos);
      if (p_195939_1_.getFace() == Direction.DOWN) {
         return ActionResultType.PASS;
      } else {
         PlayerEntity playerentity = p_195939_1_.getPlayer();
         BlockState blockstate1 = (BlockState)field_195955_e.get(blockstate.getBlock());
         BlockState blockstate2 = null;
         if (blockstate1 != null && world.isAirBlock(blockpos.up())) {
            world.playSound(playerentity, blockpos, SoundEvents.ITEM_SHOVEL_FLATTEN, SoundCategory.BLOCKS, 1.0F, 1.0F);
            blockstate2 = blockstate1;
         } else if (blockstate.getBlock() instanceof CampfireBlock && (Boolean)blockstate.get(CampfireBlock.LIT)) {
            world.playEvent((PlayerEntity)null, 1009, blockpos, 0);
            blockstate2 = (BlockState)blockstate.with(CampfireBlock.LIT, false);
         }

         if (blockstate2 != null) {
            if (!world.isRemote) {
               world.setBlockState(blockpos, blockstate2, 11);
               if (playerentity != null) {
                  p_195939_1_.getItem().damageItem(1, playerentity, (p_lambda$onItemUse$0_1_) -> {
                     p_lambda$onItemUse$0_1_.sendBreakAnimation(p_195939_1_.getHand());
                  });
               }
            }

            return ActionResultType.SUCCESS;
         } else {
            return ActionResultType.PASS;
         }
      }
   }

   static {
      EFFECTIVE_ON = Sets.newHashSet(new Block[]{Blocks.CLAY, Blocks.DIRT, Blocks.COARSE_DIRT, Blocks.PODZOL, Blocks.FARMLAND, Blocks.GRASS_BLOCK, Blocks.GRAVEL, Blocks.MYCELIUM, Blocks.SAND, Blocks.RED_SAND, Blocks.SNOW_BLOCK, Blocks.SNOW, Blocks.SOUL_SAND, Blocks.GRASS_PATH, Blocks.WHITE_CONCRETE_POWDER, Blocks.ORANGE_CONCRETE_POWDER, Blocks.MAGENTA_CONCRETE_POWDER, Blocks.LIGHT_BLUE_CONCRETE_POWDER, Blocks.YELLOW_CONCRETE_POWDER, Blocks.LIME_CONCRETE_POWDER, Blocks.PINK_CONCRETE_POWDER, Blocks.GRAY_CONCRETE_POWDER, Blocks.LIGHT_GRAY_CONCRETE_POWDER, Blocks.CYAN_CONCRETE_POWDER, Blocks.PURPLE_CONCRETE_POWDER, Blocks.BLUE_CONCRETE_POWDER, Blocks.BROWN_CONCRETE_POWDER, Blocks.GREEN_CONCRETE_POWDER, Blocks.RED_CONCRETE_POWDER, Blocks.BLACK_CONCRETE_POWDER});
      field_195955_e = Maps.newHashMap(ImmutableMap.of(Blocks.GRASS_BLOCK, Blocks.GRASS_PATH.getDefaultState()));
   }
}
