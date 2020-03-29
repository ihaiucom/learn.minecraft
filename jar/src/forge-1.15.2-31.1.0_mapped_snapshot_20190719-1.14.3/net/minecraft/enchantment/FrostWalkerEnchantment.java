package net.minecraft.enchantment;

import java.util.Iterator;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.block.material.Material;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.world.World;
import net.minecraftforge.common.util.BlockSnapshot;
import net.minecraftforge.event.ForgeEventFactory;

public class FrostWalkerEnchantment extends Enchantment {
   public FrostWalkerEnchantment(Enchantment.Rarity p_i46728_1_, EquipmentSlotType... p_i46728_2_) {
      super(p_i46728_1_, EnchantmentType.ARMOR_FEET, p_i46728_2_);
   }

   public int getMinEnchantability(int p_77321_1_) {
      return p_77321_1_ * 10;
   }

   public int getMaxEnchantability(int p_223551_1_) {
      return this.getMinEnchantability(p_223551_1_) + 15;
   }

   public boolean isTreasureEnchantment() {
      return true;
   }

   public int getMaxLevel() {
      return 2;
   }

   public static void freezeNearby(LivingEntity p_185266_0_, World p_185266_1_, BlockPos p_185266_2_, int p_185266_3_) {
      if (p_185266_0_.onGround) {
         BlockState blockstate = Blocks.FROSTED_ICE.getDefaultState();
         float f = (float)Math.min(16, 2 + p_185266_3_);
         BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();
         Iterator var7 = BlockPos.getAllInBoxMutable(p_185266_2_.add((double)(-f), -1.0D, (double)(-f)), p_185266_2_.add((double)f, -1.0D, (double)f)).iterator();

         while(true) {
            BlockPos blockpos;
            BlockState blockstate1;
            do {
               do {
                  if (!var7.hasNext()) {
                     return;
                  }

                  blockpos = (BlockPos)var7.next();
               } while(!blockpos.withinDistance(p_185266_0_.getPositionVec(), (double)f));

               blockpos$mutable.setPos(blockpos.getX(), blockpos.getY() + 1, blockpos.getZ());
               blockstate1 = p_185266_1_.getBlockState(blockpos$mutable);
            } while(!blockstate1.isAir(p_185266_1_, blockpos$mutable));

            BlockState blockstate2 = p_185266_1_.getBlockState(blockpos);
            boolean isFull = blockstate2.getBlock() == Blocks.WATER && (Integer)blockstate2.get(FlowingFluidBlock.LEVEL) == 0;
            if (blockstate2.getMaterial() == Material.WATER && isFull && blockstate.isValidPosition(p_185266_1_, blockpos) && p_185266_1_.func_226663_a_(blockstate, blockpos, ISelectionContext.dummy()) && !ForgeEventFactory.onBlockPlace(p_185266_0_, new BlockSnapshot(p_185266_1_, blockpos, blockstate2), Direction.UP)) {
               p_185266_1_.setBlockState(blockpos, blockstate);
               p_185266_1_.getPendingBlockTicks().scheduleTick(blockpos, Blocks.FROSTED_ICE, MathHelper.nextInt(p_185266_0_.getRNG(), 60, 120));
            }
         }
      }
   }

   public boolean canApplyTogether(Enchantment p_77326_1_) {
      return super.canApplyTogether(p_77326_1_) && p_77326_1_ != Enchantments.DEPTH_STRIDER;
   }
}
