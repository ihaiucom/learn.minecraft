package net.minecraft.item;

import java.util.List;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.IShearable;

public class ShearsItem extends Item {
   public ShearsItem(Item.Properties p_i48471_1_) {
      super(p_i48471_1_);
   }

   public boolean onBlockDestroyed(ItemStack p_179218_1_, World p_179218_2_, BlockState p_179218_3_, BlockPos p_179218_4_, LivingEntity p_179218_5_) {
      if (!p_179218_2_.isRemote) {
         p_179218_1_.damageItem(1, p_179218_5_, (p_lambda$onBlockDestroyed$0_0_) -> {
            p_lambda$onBlockDestroyed$0_0_.sendBreakAnimation(EquipmentSlotType.MAINHAND);
         });
      }

      Block block = p_179218_3_.getBlock();
      return !p_179218_3_.isIn(BlockTags.LEAVES) && block != Blocks.COBWEB && block != Blocks.GRASS && block != Blocks.FERN && block != Blocks.DEAD_BUSH && block != Blocks.VINE && block != Blocks.TRIPWIRE && !block.isIn(BlockTags.WOOL) ? super.onBlockDestroyed(p_179218_1_, p_179218_2_, p_179218_3_, p_179218_4_, p_179218_5_) : true;
   }

   public boolean canHarvestBlock(BlockState p_150897_1_) {
      Block block = p_150897_1_.getBlock();
      return block == Blocks.COBWEB || block == Blocks.REDSTONE_WIRE || block == Blocks.TRIPWIRE;
   }

   public float getDestroySpeed(ItemStack p_150893_1_, BlockState p_150893_2_) {
      Block block = p_150893_2_.getBlock();
      if (block != Blocks.COBWEB && !p_150893_2_.isIn(BlockTags.LEAVES)) {
         return block.isIn(BlockTags.WOOL) ? 5.0F : super.getDestroySpeed(p_150893_1_, p_150893_2_);
      } else {
         return 15.0F;
      }
   }

   public boolean itemInteractionForEntity(ItemStack p_111207_1_, PlayerEntity p_111207_2_, LivingEntity p_111207_3_, Hand p_111207_4_) {
      if (p_111207_3_.world.isRemote) {
         return false;
      } else if (p_111207_3_ instanceof IShearable) {
         IShearable target = (IShearable)p_111207_3_;
         BlockPos pos = new BlockPos(p_111207_3_.func_226277_ct_(), p_111207_3_.func_226278_cu_(), p_111207_3_.func_226281_cx_());
         if (target.isShearable(p_111207_1_, p_111207_3_.world, pos)) {
            List<ItemStack> drops = target.onSheared(p_111207_1_, p_111207_3_.world, pos, EnchantmentHelper.getEnchantmentLevel(Enchantments.FORTUNE, p_111207_1_));
            Random rand = new Random();
            drops.forEach((p_lambda$itemInteractionForEntity$1_2_) -> {
               ItemEntity ent = p_111207_3_.entityDropItem(p_lambda$itemInteractionForEntity$1_2_, 1.0F);
               ent.setMotion(ent.getMotion().add((double)((rand.nextFloat() - rand.nextFloat()) * 0.1F), (double)(rand.nextFloat() * 0.05F), (double)((rand.nextFloat() - rand.nextFloat()) * 0.1F)));
            });
            p_111207_1_.damageItem(1, p_111207_3_, (p_lambda$itemInteractionForEntity$2_1_) -> {
               p_lambda$itemInteractionForEntity$2_1_.sendBreakAnimation(p_111207_4_);
            });
         }

         return true;
      } else {
         return false;
      }
   }
}
