package net.minecraft.client.gui.toasts;

import com.google.common.collect.Queues;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Arrays;
import java.util.Deque;
import java.util.Iterator;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ToastGui extends AbstractGui {
   private final Minecraft mc;
   private final ToastGui.ToastInstance<?>[] visible = new ToastGui.ToastInstance[5];
   private final Deque<IToast> toastsQueue = Queues.newArrayDeque();

   public ToastGui(Minecraft p_i47388_1_) {
      this.mc = p_i47388_1_;
   }

   public void render() {
      if (!this.mc.gameSettings.hideGUI) {
         for(int lvt_1_1_ = 0; lvt_1_1_ < this.visible.length; ++lvt_1_1_) {
            ToastGui.ToastInstance<?> lvt_2_1_ = this.visible[lvt_1_1_];
            if (lvt_2_1_ != null && lvt_2_1_.render(this.mc.func_228018_at_().getScaledWidth(), lvt_1_1_)) {
               this.visible[lvt_1_1_] = null;
            }

            if (this.visible[lvt_1_1_] == null && !this.toastsQueue.isEmpty()) {
               this.visible[lvt_1_1_] = new ToastGui.ToastInstance((IToast)this.toastsQueue.removeFirst());
            }
         }

      }
   }

   @Nullable
   public <T extends IToast> T getToast(Class<? extends T> p_192990_1_, Object p_192990_2_) {
      ToastGui.ToastInstance[] var3 = this.visible;
      int var4 = var3.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         ToastGui.ToastInstance<?> lvt_6_1_ = var3[var5];
         if (lvt_6_1_ != null && p_192990_1_.isAssignableFrom(lvt_6_1_.getToast().getClass()) && lvt_6_1_.getToast().getType().equals(p_192990_2_)) {
            return lvt_6_1_.getToast();
         }
      }

      Iterator var7 = this.toastsQueue.iterator();

      IToast lvt_4_1_;
      do {
         if (!var7.hasNext()) {
            return null;
         }

         lvt_4_1_ = (IToast)var7.next();
      } while(!p_192990_1_.isAssignableFrom(lvt_4_1_.getClass()) || !lvt_4_1_.getType().equals(p_192990_2_));

      return lvt_4_1_;
   }

   public void clear() {
      Arrays.fill(this.visible, (Object)null);
      this.toastsQueue.clear();
   }

   public void add(IToast p_192988_1_) {
      this.toastsQueue.add(p_192988_1_);
   }

   public Minecraft getMinecraft() {
      return this.mc;
   }

   @OnlyIn(Dist.CLIENT)
   class ToastInstance<T extends IToast> {
      private final T toast;
      private long animationTime;
      private long visibleTime;
      private IToast.Visibility visibility;

      private ToastInstance(T p_i47483_2_) {
         this.animationTime = -1L;
         this.visibleTime = -1L;
         this.visibility = IToast.Visibility.SHOW;
         this.toast = p_i47483_2_;
      }

      public T getToast() {
         return this.toast;
      }

      private float getVisibility(long p_193686_1_) {
         float lvt_3_1_ = MathHelper.clamp((float)(p_193686_1_ - this.animationTime) / 600.0F, 0.0F, 1.0F);
         lvt_3_1_ *= lvt_3_1_;
         return this.visibility == IToast.Visibility.HIDE ? 1.0F - lvt_3_1_ : lvt_3_1_;
      }

      public boolean render(int p_193684_1_, int p_193684_2_) {
         long lvt_3_1_ = Util.milliTime();
         if (this.animationTime == -1L) {
            this.animationTime = lvt_3_1_;
            this.visibility.playSound(ToastGui.this.mc.getSoundHandler());
         }

         if (this.visibility == IToast.Visibility.SHOW && lvt_3_1_ - this.animationTime <= 600L) {
            this.visibleTime = lvt_3_1_;
         }

         RenderSystem.pushMatrix();
         RenderSystem.translatef((float)p_193684_1_ - 160.0F * this.getVisibility(lvt_3_1_), (float)(p_193684_2_ * 32), (float)(800 + p_193684_2_));
         IToast.Visibility lvt_5_1_ = this.toast.draw(ToastGui.this, lvt_3_1_ - this.visibleTime);
         RenderSystem.popMatrix();
         if (lvt_5_1_ != this.visibility) {
            this.animationTime = lvt_3_1_ - (long)((int)((1.0F - this.getVisibility(lvt_3_1_)) * 600.0F));
            this.visibility = lvt_5_1_;
            this.visibility.playSound(ToastGui.this.mc.getSoundHandler());
         }

         return this.visibility == IToast.Visibility.HIDE && lvt_3_1_ - this.animationTime > 600L;
      }

      // $FF: synthetic method
      ToastInstance(IToast p_i47484_2_, Object p_i47484_3_) {
         this(p_i47484_2_);
      }
   }
}
