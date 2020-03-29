package net.minecraftforge.items.wrapper;

import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;

public class EntityArmorInvWrapper extends EntityEquipmentInvWrapper {
   public EntityArmorInvWrapper(LivingEntity entity) {
      super(entity, EquipmentSlotType.Group.ARMOR);
   }
}
