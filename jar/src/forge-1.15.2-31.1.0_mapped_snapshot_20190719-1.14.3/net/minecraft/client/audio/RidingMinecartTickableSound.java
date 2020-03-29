package net.minecraft.client.audio;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.minecart.AbstractMinecartEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RidingMinecartTickableSound extends TickableSound {
   private final PlayerEntity player;
   private final AbstractMinecartEntity minecart;

   public RidingMinecartTickableSound(PlayerEntity p_i48613_1_, AbstractMinecartEntity p_i48613_2_) {
      super(SoundEvents.ENTITY_MINECART_INSIDE, SoundCategory.NEUTRAL);
      this.player = p_i48613_1_;
      this.minecart = p_i48613_2_;
      this.attenuationType = ISound.AttenuationType.NONE;
      this.repeat = true;
      this.repeatDelay = 0;
      this.volume = 0.0F;
   }

   public boolean canBeSilent() {
      return true;
   }

   public void tick() {
      if (!this.minecart.removed && this.player.isPassenger() && this.player.getRidingEntity() == this.minecart) {
         float lvt_1_1_ = MathHelper.sqrt(Entity.func_213296_b(this.minecart.getMotion()));
         if ((double)lvt_1_1_ >= 0.01D) {
            this.volume = 0.0F + MathHelper.clamp(lvt_1_1_, 0.0F, 1.0F) * 0.75F;
         } else {
            this.volume = 0.0F;
         }

      } else {
         this.donePlaying = true;
      }
   }
}
