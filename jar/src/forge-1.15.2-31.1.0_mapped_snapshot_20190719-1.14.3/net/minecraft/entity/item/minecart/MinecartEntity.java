package net.minecraft.entity.item.minecart;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class MinecartEntity extends AbstractMinecartEntity {
   public MinecartEntity(EntityType<?> p_i50126_1_, World p_i50126_2_) {
      super(p_i50126_1_, p_i50126_2_);
   }

   public MinecartEntity(World p_i1723_1_, double p_i1723_2_, double p_i1723_4_, double p_i1723_6_) {
      super(EntityType.MINECART, p_i1723_1_, p_i1723_2_, p_i1723_4_, p_i1723_6_);
   }

   public boolean processInitialInteract(PlayerEntity p_184230_1_, Hand p_184230_2_) {
      if (super.processInitialInteract(p_184230_1_, p_184230_2_)) {
         return true;
      } else if (p_184230_1_.func_226563_dT_()) {
         return false;
      } else if (this.isBeingRidden()) {
         return true;
      } else {
         if (!this.world.isRemote) {
            p_184230_1_.startRiding(this);
         }

         return true;
      }
   }

   public void onActivatorRailPass(int p_96095_1_, int p_96095_2_, int p_96095_3_, boolean p_96095_4_) {
      if (p_96095_4_) {
         if (this.isBeingRidden()) {
            this.removePassengers();
         }

         if (this.getRollingAmplitude() == 0) {
            this.setRollingDirection(-this.getRollingDirection());
            this.setRollingAmplitude(10);
            this.setDamage(50.0F);
            this.markVelocityChanged();
         }
      }

   }

   public AbstractMinecartEntity.Type getMinecartType() {
      return AbstractMinecartEntity.Type.RIDEABLE;
   }
}
