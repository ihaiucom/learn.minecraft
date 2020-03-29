package net.minecraftforge.event.entity.living;

import net.minecraft.entity.MobEntity;
import net.minecraftforge.eventbus.api.Event.HasResult;

@HasResult
public class LivingPackSizeEvent extends LivingEvent {
   private int maxPackSize;

   public LivingPackSizeEvent(MobEntity entity) {
      super(entity);
   }

   public int getMaxPackSize() {
      return this.maxPackSize;
   }

   public void setMaxPackSize(int maxPackSize) {
      this.maxPackSize = maxPackSize;
   }
}
