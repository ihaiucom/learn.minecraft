package net.minecraft.item;

import java.util.function.Predicate;
import net.minecraft.entity.LivingEntity;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Hand;

public abstract class ShootableItem extends Item {
   public static final Predicate<ItemStack> ARROWS = (p_220002_0_) -> {
      return p_220002_0_.getItem().isIn(ItemTags.ARROWS);
   };
   public static final Predicate<ItemStack> ARROWS_OR_FIREWORKS;

   public ShootableItem(Item.Properties p_i50040_1_) {
      super(p_i50040_1_);
   }

   public Predicate<ItemStack> getAmmoPredicate() {
      return this.getInventoryAmmoPredicate();
   }

   public abstract Predicate<ItemStack> getInventoryAmmoPredicate();

   public static ItemStack getHeldAmmo(LivingEntity p_220005_0_, Predicate<ItemStack> p_220005_1_) {
      if (p_220005_1_.test(p_220005_0_.getHeldItem(Hand.OFF_HAND))) {
         return p_220005_0_.getHeldItem(Hand.OFF_HAND);
      } else {
         return p_220005_1_.test(p_220005_0_.getHeldItem(Hand.MAIN_HAND)) ? p_220005_0_.getHeldItem(Hand.MAIN_HAND) : ItemStack.EMPTY;
      }
   }

   public int getItemEnchantability() {
      return 1;
   }

   static {
      ARROWS_OR_FIREWORKS = ARROWS.or((p_220003_0_) -> {
         return p_220003_0_.getItem() == Items.FIREWORK_ROCKET;
      });
   }
}
