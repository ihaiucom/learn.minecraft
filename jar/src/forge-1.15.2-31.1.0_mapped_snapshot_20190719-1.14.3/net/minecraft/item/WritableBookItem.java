package net.minecraft.item;

import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.LecternBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.stats.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class WritableBookItem extends Item {
   public WritableBookItem(Item.Properties p_i48455_1_) {
      super(p_i48455_1_);
   }

   public ActionResultType onItemUse(ItemUseContext p_195939_1_) {
      World lvt_2_1_ = p_195939_1_.getWorld();
      BlockPos lvt_3_1_ = p_195939_1_.getPos();
      BlockState lvt_4_1_ = lvt_2_1_.getBlockState(lvt_3_1_);
      if (lvt_4_1_.getBlock() == Blocks.LECTERN) {
         return LecternBlock.tryPlaceBook(lvt_2_1_, lvt_3_1_, lvt_4_1_, p_195939_1_.getItem()) ? ActionResultType.SUCCESS : ActionResultType.PASS;
      } else {
         return ActionResultType.PASS;
      }
   }

   public ActionResult<ItemStack> onItemRightClick(World p_77659_1_, PlayerEntity p_77659_2_, Hand p_77659_3_) {
      ItemStack lvt_4_1_ = p_77659_2_.getHeldItem(p_77659_3_);
      p_77659_2_.openBook(lvt_4_1_, p_77659_3_);
      p_77659_2_.addStat(Stats.ITEM_USED.get(this));
      return ActionResult.func_226248_a_(lvt_4_1_);
   }

   public static boolean isNBTValid(@Nullable CompoundNBT p_150930_0_) {
      if (p_150930_0_ == null) {
         return false;
      } else if (!p_150930_0_.contains("pages", 9)) {
         return false;
      } else {
         ListNBT lvt_1_1_ = p_150930_0_.getList("pages", 8);

         for(int lvt_2_1_ = 0; lvt_2_1_ < lvt_1_1_.size(); ++lvt_2_1_) {
            String lvt_3_1_ = lvt_1_1_.getString(lvt_2_1_);
            if (lvt_3_1_.length() > 32767) {
               return false;
            }
         }

         return true;
      }
   }
}
