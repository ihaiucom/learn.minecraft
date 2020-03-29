package net.minecraft.client.gui.toasts;

import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public interface IToast {
   ResourceLocation TEXTURE_TOASTS = new ResourceLocation("textures/gui/toasts.png");
   Object NO_TOKEN = new Object();

   IToast.Visibility draw(ToastGui var1, long var2);

   default Object getType() {
      return NO_TOKEN;
   }

   @OnlyIn(Dist.CLIENT)
   public static enum Visibility {
      SHOW(SoundEvents.UI_TOAST_IN),
      HIDE(SoundEvents.UI_TOAST_OUT);

      private final SoundEvent sound;

      private Visibility(SoundEvent p_i47607_3_) {
         this.sound = p_i47607_3_;
      }

      public void playSound(SoundHandler p_194169_1_) {
         p_194169_1_.play(SimpleSound.master(this.sound, 1.0F, 1.0F));
      }
   }
}
