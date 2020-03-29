package net.minecraft.item;

import net.minecraft.block.DispenserBlock;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class ElytraItem extends Item {
   public ElytraItem(Item.Properties p_i48507_1_) {
      super(p_i48507_1_);
      this.addPropertyOverride(new ResourceLocation("broken"), (p_210312_0_, p_210312_1_, p_210312_2_) -> {
         return isUsable(p_210312_0_) ? 0.0F : 1.0F;
      });
      DispenserBlock.registerDispenseBehavior(this, ArmorItem.DISPENSER_BEHAVIOR);
   }

   public static boolean isUsable(ItemStack p_185069_0_) {
      return p_185069_0_.getDamage() < p_185069_0_.getMaxDamage() - 1;
   }

   public boolean getIsRepairable(ItemStack p_82789_1_, ItemStack p_82789_2_) {
      return p_82789_2_.getItem() == Items.PHANTOM_MEMBRANE;
   }

   public ActionResult<ItemStack> onItemRightClick(World p_77659_1_, PlayerEntity p_77659_2_, Hand p_77659_3_) {
      ItemStack lvt_4_1_ = p_77659_2_.getHeldItem(p_77659_3_);
      EquipmentSlotType lvt_5_1_ = MobEntity.getSlotForItemStack(lvt_4_1_);
      ItemStack lvt_6_1_ = p_77659_2_.getItemStackFromSlot(lvt_5_1_);
      if (lvt_6_1_.isEmpty()) {
         p_77659_2_.setItemStackToSlot(lvt_5_1_, lvt_4_1_.copy());
         lvt_4_1_.setCount(0);
         return ActionResult.func_226248_a_(lvt_4_1_);
      } else {
         return ActionResult.func_226251_d_(lvt_4_1_);
      }
   }
}
