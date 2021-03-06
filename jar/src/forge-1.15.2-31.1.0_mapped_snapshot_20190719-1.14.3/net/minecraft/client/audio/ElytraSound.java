package net.minecraft.client.audio;

import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ElytraSound extends TickableSound {
   private final ClientPlayerEntity player;
   private int time;

   public ElytraSound(ClientPlayerEntity p_i47113_1_) {
      super(SoundEvents.ITEM_ELYTRA_FLYING, SoundCategory.PLAYERS);
      this.player = p_i47113_1_;
      this.repeat = true;
      this.repeatDelay = 0;
      this.volume = 0.1F;
   }

   public void tick() {
      ++this.time;
      if (!this.player.removed && (this.time <= 20 || this.player.isElytraFlying())) {
         this.x = (float)this.player.func_226277_ct_();
         this.y = (float)this.player.func_226278_cu_();
         this.z = (float)this.player.func_226281_cx_();
         float lvt_1_1_ = (float)this.player.getMotion().lengthSquared();
         if ((double)lvt_1_1_ >= 1.0E-7D) {
            this.volume = MathHelper.clamp(lvt_1_1_ / 4.0F, 0.0F, 1.0F);
         } else {
            this.volume = 0.0F;
         }

         if (this.time < 20) {
            this.volume = 0.0F;
         } else if (this.time < 40) {
            this.volume = (float)((double)this.volume * ((double)(this.time - 20) / 20.0D));
         }

         float lvt_2_1_ = 0.8F;
         if (this.volume > 0.8F) {
            this.pitch = 1.0F + (this.volume - 0.8F);
         } else {
            this.pitch = 1.0F;
         }

      } else {
         this.donePlaying = true;
      }
   }
}
