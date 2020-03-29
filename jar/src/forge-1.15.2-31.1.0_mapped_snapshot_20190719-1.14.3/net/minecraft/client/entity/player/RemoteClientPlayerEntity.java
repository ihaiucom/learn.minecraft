package net.minecraft.client.entity.player;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ForgeHooks;

@OnlyIn(Dist.CLIENT)
public class RemoteClientPlayerEntity extends AbstractClientPlayerEntity {
   public RemoteClientPlayerEntity(ClientWorld p_i50989_1_, GameProfile p_i50989_2_) {
      super(p_i50989_1_, p_i50989_2_);
      this.stepHeight = 1.0F;
      this.noClip = true;
   }

   public boolean isInRangeToRenderDist(double p_70112_1_) {
      double d0 = this.getBoundingBox().getAverageEdgeLength() * 10.0D;
      if (Double.isNaN(d0)) {
         d0 = 1.0D;
      }

      d0 = d0 * 64.0D * getRenderDistanceWeight();
      return p_70112_1_ < d0 * d0;
   }

   public boolean attackEntityFrom(DamageSource p_70097_1_, float p_70097_2_) {
      ForgeHooks.onPlayerAttack(this, p_70097_1_, p_70097_2_);
      return true;
   }

   public void tick() {
      super.tick();
      this.prevLimbSwingAmount = this.limbSwingAmount;
      double d0 = this.func_226277_ct_() - this.prevPosX;
      double d1 = this.func_226281_cx_() - this.prevPosZ;
      float f = MathHelper.sqrt(d0 * d0 + d1 * d1) * 4.0F;
      if (f > 1.0F) {
         f = 1.0F;
      }

      this.limbSwingAmount += (f - this.limbSwingAmount) * 0.4F;
      this.limbSwing += this.limbSwingAmount;
   }

   public void livingTick() {
      if (this.newPosRotationIncrements > 0) {
         double d0 = this.func_226277_ct_() + (this.interpTargetX - this.func_226277_ct_()) / (double)this.newPosRotationIncrements;
         double d1 = this.func_226278_cu_() + (this.interpTargetY - this.func_226278_cu_()) / (double)this.newPosRotationIncrements;
         double d2 = this.func_226281_cx_() + (this.interpTargetZ - this.func_226281_cx_()) / (double)this.newPosRotationIncrements;
         this.rotationYaw = (float)((double)this.rotationYaw + MathHelper.wrapDegrees(this.interpTargetYaw - (double)this.rotationYaw) / (double)this.newPosRotationIncrements);
         this.rotationPitch = (float)((double)this.rotationPitch + (this.interpTargetPitch - (double)this.rotationPitch) / (double)this.newPosRotationIncrements);
         --this.newPosRotationIncrements;
         this.setPosition(d0, d1, d2);
         this.setRotation(this.rotationYaw, this.rotationPitch);
      }

      if (this.interpTicksHead > 0) {
         this.rotationYawHead = (float)((double)this.rotationYawHead + MathHelper.wrapDegrees(this.interpTargetHeadYaw - (double)this.rotationYawHead) / (double)this.interpTicksHead);
         --this.interpTicksHead;
      }

      this.prevCameraYaw = this.cameraYaw;
      this.updateArmSwingProgress();
      float f1;
      if (this.onGround && this.getHealth() > 0.0F) {
         f1 = Math.min(0.1F, MathHelper.sqrt(func_213296_b(this.getMotion())));
      } else {
         f1 = 0.0F;
      }

      float var2;
      if (!this.onGround && this.getHealth() > 0.0F) {
         var2 = (float)Math.atan(-this.getMotion().y * 0.20000000298023224D) * 15.0F;
      } else {
         var2 = 0.0F;
      }

      this.cameraYaw += (f1 - this.cameraYaw) * 0.4F;
      this.world.getProfiler().startSection("push");
      this.collideWithNearbyEntities();
      this.world.getProfiler().endSection();
   }

   protected void updatePose() {
   }

   public void sendMessage(ITextComponent p_145747_1_) {
      Minecraft.getInstance().ingameGUI.getChatGUI().printChatMessage(p_145747_1_);
   }
}
