package net.minecraft.client.gui.toasts;

import com.mojang.blaze3d.systems.RenderSystem;
import javax.annotation.Nullable;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SystemToast implements IToast {
   private final SystemToast.Type type;
   private String title;
   private String subtitle;
   private long firstDrawTime;
   private boolean newDisplay;

   public SystemToast(SystemToast.Type p_i47488_1_, ITextComponent p_i47488_2_, @Nullable ITextComponent p_i47488_3_) {
      this.type = p_i47488_1_;
      this.title = p_i47488_2_.getString();
      this.subtitle = p_i47488_3_ == null ? null : p_i47488_3_.getString();
   }

   public IToast.Visibility draw(ToastGui p_193653_1_, long p_193653_2_) {
      if (this.newDisplay) {
         this.firstDrawTime = p_193653_2_;
         this.newDisplay = false;
      }

      p_193653_1_.getMinecraft().getTextureManager().bindTexture(TEXTURE_TOASTS);
      RenderSystem.color3f(1.0F, 1.0F, 1.0F);
      p_193653_1_.blit(0, 0, 0, 64, 160, 32);
      if (this.subtitle == null) {
         p_193653_1_.getMinecraft().fontRenderer.drawString(this.title, 18.0F, 12.0F, -256);
      } else {
         p_193653_1_.getMinecraft().fontRenderer.drawString(this.title, 18.0F, 7.0F, -256);
         p_193653_1_.getMinecraft().fontRenderer.drawString(this.subtitle, 18.0F, 18.0F, -1);
      }

      return p_193653_2_ - this.firstDrawTime < 5000L ? IToast.Visibility.SHOW : IToast.Visibility.HIDE;
   }

   public void setDisplayedText(ITextComponent p_193656_1_, @Nullable ITextComponent p_193656_2_) {
      this.title = p_193656_1_.getString();
      this.subtitle = p_193656_2_ == null ? null : p_193656_2_.getString();
      this.newDisplay = true;
   }

   public SystemToast.Type getType() {
      return this.type;
   }

   public static void addOrUpdate(ToastGui p_193657_0_, SystemToast.Type p_193657_1_, ITextComponent p_193657_2_, @Nullable ITextComponent p_193657_3_) {
      SystemToast lvt_4_1_ = (SystemToast)p_193657_0_.getToast(SystemToast.class, p_193657_1_);
      if (lvt_4_1_ == null) {
         p_193657_0_.add(new SystemToast(p_193657_1_, p_193657_2_, p_193657_3_));
      } else {
         lvt_4_1_.setDisplayedText(p_193657_2_, p_193657_3_);
      }

   }

   // $FF: synthetic method
   public Object getType() {
      return this.getType();
   }

   @OnlyIn(Dist.CLIENT)
   public static enum Type {
      TUTORIAL_HINT,
      NARRATOR_TOGGLE,
      WORLD_BACKUP,
      PACK_LOAD_FAILURE;
   }
}
