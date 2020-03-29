package net.minecraft.client.gui.toasts;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Iterator;
import java.util.List;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.advancements.FrameType;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class AdvancementToast implements IToast {
   private final Advancement advancement;
   private boolean hasPlayedSound;

   public AdvancementToast(Advancement p_i47490_1_) {
      this.advancement = p_i47490_1_;
   }

   public IToast.Visibility draw(ToastGui p_193653_1_, long p_193653_2_) {
      p_193653_1_.getMinecraft().getTextureManager().bindTexture(TEXTURE_TOASTS);
      RenderSystem.color3f(1.0F, 1.0F, 1.0F);
      DisplayInfo lvt_4_1_ = this.advancement.getDisplay();
      p_193653_1_.blit(0, 0, 0, 0, 160, 32);
      if (lvt_4_1_ != null) {
         List<String> lvt_5_1_ = p_193653_1_.getMinecraft().fontRenderer.listFormattedStringToWidth(lvt_4_1_.getTitle().getFormattedText(), 125);
         int lvt_6_1_ = lvt_4_1_.getFrame() == FrameType.CHALLENGE ? 16746751 : 16776960;
         if (lvt_5_1_.size() == 1) {
            p_193653_1_.getMinecraft().fontRenderer.drawString(I18n.format("advancements.toast." + lvt_4_1_.getFrame().getName()), 30.0F, 7.0F, lvt_6_1_ | -16777216);
            p_193653_1_.getMinecraft().fontRenderer.drawString(lvt_4_1_.getTitle().getFormattedText(), 30.0F, 18.0F, -1);
         } else {
            int lvt_7_1_ = true;
            float lvt_8_1_ = 300.0F;
            int lvt_9_2_;
            if (p_193653_2_ < 1500L) {
               lvt_9_2_ = MathHelper.floor(MathHelper.clamp((float)(1500L - p_193653_2_) / 300.0F, 0.0F, 1.0F) * 255.0F) << 24 | 67108864;
               p_193653_1_.getMinecraft().fontRenderer.drawString(I18n.format("advancements.toast." + lvt_4_1_.getFrame().getName()), 30.0F, 11.0F, lvt_6_1_ | lvt_9_2_);
            } else {
               lvt_9_2_ = MathHelper.floor(MathHelper.clamp((float)(p_193653_2_ - 1500L) / 300.0F, 0.0F, 1.0F) * 252.0F) << 24 | 67108864;
               int var10001 = lvt_5_1_.size();
               p_193653_1_.getMinecraft().fontRenderer.getClass();
               int lvt_10_1_ = 16 - var10001 * 9 / 2;

               for(Iterator var11 = lvt_5_1_.iterator(); var11.hasNext(); lvt_10_1_ += 9) {
                  String lvt_12_1_ = (String)var11.next();
                  p_193653_1_.getMinecraft().fontRenderer.drawString(lvt_12_1_, 30.0F, (float)lvt_10_1_, 16777215 | lvt_9_2_);
                  p_193653_1_.getMinecraft().fontRenderer.getClass();
               }
            }
         }

         if (!this.hasPlayedSound && p_193653_2_ > 0L) {
            this.hasPlayedSound = true;
            if (lvt_4_1_.getFrame() == FrameType.CHALLENGE) {
               p_193653_1_.getMinecraft().getSoundHandler().play(SimpleSound.master(SoundEvents.UI_TOAST_CHALLENGE_COMPLETE, 1.0F, 1.0F));
            }
         }

         p_193653_1_.getMinecraft().getItemRenderer().renderItemAndEffectIntoGUI((LivingEntity)null, lvt_4_1_.getIcon(), 8, 8);
         return p_193653_2_ >= 5000L ? IToast.Visibility.HIDE : IToast.Visibility.SHOW;
      } else {
         return IToast.Visibility.HIDE;
      }
   }
}
