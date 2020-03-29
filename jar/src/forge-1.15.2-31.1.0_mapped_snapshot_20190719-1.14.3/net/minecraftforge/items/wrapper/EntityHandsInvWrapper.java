package net.minecraftforge.items.wrapper;

import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;

public class EntityHandsInvWrapper extends EntityEquipmentInvWrapper {
   public EntityHandsInvWrapper(LivingEntity entity) {
      super(entity, EquipmentSlotType.Group.HAND);
   }
}
