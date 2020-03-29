package net.minecraft.entity.passive;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.World;

public abstract class ShoulderRidingEntity extends TameableEntity {
   private int rideCooldownCounter;

   protected ShoulderRidingEntity(EntityType<? extends ShoulderRidingEntity> p_i48566_1_, World p_i48566_2_) {
      super(p_i48566_1_, p_i48566_2_);
   }

   public boolean func_213439_d(ServerPlayerEntity p_213439_1_) {
      CompoundNBT lvt_2_1_ = new CompoundNBT();
      lvt_2_1_.putString("id", this.getEntityString());
      this.writeWithoutTypeId(lvt_2_1_);
      if (p_213439_1_.addShoulderEntity(lvt_2_1_)) {
         this.remove();
         return true;
      } else {
         return false;
      }
   }

   public void tick() {
      ++this.rideCooldownCounter;
      super.tick();
   }

   public boolean canSitOnShoulder() {
      return this.rideCooldownCounter > 100;
   }
}
