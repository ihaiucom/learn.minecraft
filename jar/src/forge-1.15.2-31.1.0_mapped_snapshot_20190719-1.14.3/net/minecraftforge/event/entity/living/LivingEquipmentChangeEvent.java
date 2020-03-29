package net.minecraftforge.event.entity.living;

import javax.annotation.Nonnull;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;

public class LivingEquipmentChangeEvent extends LivingEvent {
   private final EquipmentSlotType slot;
   private final ItemStack from;
   private final ItemStack to;

   public LivingEquipmentChangeEvent(LivingEntity entity, EquipmentSlotType slot, @Nonnull ItemStack from, @Nonnull ItemStack to) {
      super(entity);
      this.slot = slot;
      this.from = from;
      this.to = to;
   }

   public EquipmentSlotType getSlot() {
      return this.slot;
   }

   @Nonnull
   public ItemStack getFrom() {
      return this.from;
   }

   @Nonnull
   public ItemStack getTo() {
      return this.to;
   }
}
