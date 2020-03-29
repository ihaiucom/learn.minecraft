package net.minecraft.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.stats.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class MapItem extends AbstractMapItem {
   public MapItem(Item.Properties p_i48506_1_) {
      super(p_i48506_1_);
   }

   public ActionResult<ItemStack> onItemRightClick(World p_77659_1_, PlayerEntity p_77659_2_, Hand p_77659_3_) {
      ItemStack lvt_4_1_ = FilledMapItem.setupNewMap(p_77659_1_, MathHelper.floor(p_77659_2_.func_226277_ct_()), MathHelper.floor(p_77659_2_.func_226281_cx_()), (byte)0, true, false);
      ItemStack lvt_5_1_ = p_77659_2_.getHeldItem(p_77659_3_);
      if (!p_77659_2_.abilities.isCreativeMode) {
         lvt_5_1_.shrink(1);
      }

      if (lvt_5_1_.isEmpty()) {
         return ActionResult.func_226248_a_(lvt_4_1_);
      } else {
         if (!p_77659_2_.inventory.addItemStackToInventory(lvt_4_1_.copy())) {
            p_77659_2_.dropItem(lvt_4_1_, false);
         }

         p_77659_2_.addStat(Stats.ITEM_USED.get(this));
         return ActionResult.func_226248_a_(lvt_5_1_);
      }
   }
}
