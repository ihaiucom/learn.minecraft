package net.minecraft.client.gui.overlay;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Iterator;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.ISoundEventListener;
import net.minecraft.client.audio.SoundEventAccessor;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SubtitleOverlayGui extends AbstractGui implements ISoundEventListener {
   private final Minecraft client;
   private final List<SubtitleOverlayGui.Subtitle> subtitles = Lists.newArrayList();
   private boolean enabled;

   public SubtitleOverlayGui(Minecraft p_i46603_1_) {
      this.client = p_i46603_1_;
   }

   public void render() {
      if (!this.enabled && this.client.gameSettings.showSubtitles) {
         this.client.getSoundHandler().addListener(this);
         this.enabled = true;
      } else if (this.enabled && !this.client.gameSettings.showSubtitles) {
         this.client.getSoundHandler().removeListener(this);
         this.enabled = false;
      }

      if (this.enabled && !this.subtitles.isEmpty()) {
         RenderSystem.pushMatrix();
         RenderSystem.enableBlend();
         RenderSystem.defaultBlendFunc();
         Vec3d lvt_1_1_ = new Vec3d(this.client.player.func_226277_ct_(), this.client.player.func_226280_cw_(), this.client.player.func_226281_cx_());
         Vec3d lvt_2_1_ = (new Vec3d(0.0D, 0.0D, -1.0D)).rotatePitch(-this.client.player.rotationPitch * 0.017453292F).rotateYaw(-this.client.player.rotationYaw * 0.017453292F);
         Vec3d lvt_3_1_ = (new Vec3d(0.0D, 1.0D, 0.0D)).rotatePitch(-this.client.player.rotationPitch * 0.017453292F).rotateYaw(-this.client.player.rotationYaw * 0.017453292F);
         Vec3d lvt_4_1_ = lvt_2_1_.crossProduct(lvt_3_1_);
         int lvt_5_1_ = 0;
         int lvt_6_1_ = 0;
         Iterator lvt_7_1_ = this.subtitles.iterator();

         SubtitleOverlayGui.Subtitle lvt_8_2_;
         while(lvt_7_1_.hasNext()) {
            lvt_8_2_ = (SubtitleOverlayGui.Subtitle)lvt_7_1_.next();
            if (lvt_8_2_.getStartTime() + 3000L <= Util.milliTime()) {
               lvt_7_1_.remove();
            } else {
               lvt_6_1_ = Math.max(lvt_6_1_, this.client.fontRenderer.getStringWidth(lvt_8_2_.getString()));
            }
         }

         lvt_6_1_ += this.client.fontRenderer.getStringWidth("<") + this.client.fontRenderer.getStringWidth(" ") + this.client.fontRenderer.getStringWidth(">") + this.client.fontRenderer.getStringWidth(" ");

         for(lvt_7_1_ = this.subtitles.iterator(); lvt_7_1_.hasNext(); ++lvt_5_1_) {
            lvt_8_2_ = (SubtitleOverlayGui.Subtitle)lvt_7_1_.next();
            int lvt_9_1_ = true;
            String lvt_10_1_ = lvt_8_2_.getString();
            Vec3d lvt_11_1_ = lvt_8_2_.getLocation().subtract(lvt_1_1_).normalize();
            double lvt_12_1_ = -lvt_4_1_.dotProduct(lvt_11_1_);
            double lvt_14_1_ = -lvt_2_1_.dotProduct(lvt_11_1_);
            boolean lvt_16_1_ = lvt_14_1_ > 0.5D;
            int lvt_17_1_ = lvt_6_1_ / 2;
            this.client.fontRenderer.getClass();
            int lvt_18_1_ = 9;
            int lvt_19_1_ = lvt_18_1_ / 2;
            float lvt_20_1_ = 1.0F;
            int lvt_21_1_ = this.client.fontRenderer.getStringWidth(lvt_10_1_);
            int lvt_22_1_ = MathHelper.floor(MathHelper.clampedLerp(255.0D, 75.0D, (double)((float)(Util.milliTime() - lvt_8_2_.getStartTime()) / 3000.0F)));
            int lvt_23_1_ = lvt_22_1_ << 16 | lvt_22_1_ << 8 | lvt_22_1_;
            RenderSystem.pushMatrix();
            RenderSystem.translatef((float)this.client.func_228018_at_().getScaledWidth() - (float)lvt_17_1_ * 1.0F - 2.0F, (float)(this.client.func_228018_at_().getScaledHeight() - 30) - (float)(lvt_5_1_ * (lvt_18_1_ + 1)) * 1.0F, 0.0F);
            RenderSystem.scalef(1.0F, 1.0F, 1.0F);
            fill(-lvt_17_1_ - 1, -lvt_19_1_ - 1, lvt_17_1_ + 1, lvt_19_1_ + 1, this.client.gameSettings.func_216841_b(0.8F));
            RenderSystem.enableBlend();
            if (!lvt_16_1_) {
               if (lvt_12_1_ > 0.0D) {
                  this.client.fontRenderer.drawString(">", (float)(lvt_17_1_ - this.client.fontRenderer.getStringWidth(">")), (float)(-lvt_19_1_), lvt_23_1_ + -16777216);
               } else if (lvt_12_1_ < 0.0D) {
                  this.client.fontRenderer.drawString("<", (float)(-lvt_17_1_), (float)(-lvt_19_1_), lvt_23_1_ + -16777216);
               }
            }

            this.client.fontRenderer.drawString(lvt_10_1_, (float)(-lvt_21_1_ / 2), (float)(-lvt_19_1_), lvt_23_1_ + -16777216);
            RenderSystem.popMatrix();
         }

         RenderSystem.disableBlend();
         RenderSystem.popMatrix();
      }
   }

   public void onPlaySound(ISound p_184067_1_, SoundEventAccessor p_184067_2_) {
      if (p_184067_2_.getSubtitle() != null) {
         String lvt_3_1_ = p_184067_2_.getSubtitle().getFormattedText();
         if (!this.subtitles.isEmpty()) {
            Iterator var4 = this.subtitles.iterator();

            while(var4.hasNext()) {
               SubtitleOverlayGui.Subtitle lvt_5_1_ = (SubtitleOverlayGui.Subtitle)var4.next();
               if (lvt_5_1_.getString().equals(lvt_3_1_)) {
                  lvt_5_1_.refresh(new Vec3d((double)p_184067_1_.getX(), (double)p_184067_1_.getY(), (double)p_184067_1_.getZ()));
                  return;
               }
            }
         }

         this.subtitles.add(new SubtitleOverlayGui.Subtitle(lvt_3_1_, new Vec3d((double)p_184067_1_.getX(), (double)p_184067_1_.getY(), (double)p_184067_1_.getZ())));
      }
   }

   @OnlyIn(Dist.CLIENT)
   public class Subtitle {
      private final String subtitle;
      private long startTime;
      private Vec3d location;

      public Subtitle(String p_i47104_2_, Vec3d p_i47104_3_) {
         this.subtitle = p_i47104_2_;
         this.location = p_i47104_3_;
         this.startTime = Util.milliTime();
      }

      public String getString() {
         return this.subtitle;
      }

      public long getStartTime() {
         return this.startTime;
      }

      public Vec3d getLocation() {
         return this.location;
      }

      public void refresh(Vec3d p_186823_1_) {
         this.location = p_186823_1_;
         this.startTime = Util.milliTime();
      }
   }
}
