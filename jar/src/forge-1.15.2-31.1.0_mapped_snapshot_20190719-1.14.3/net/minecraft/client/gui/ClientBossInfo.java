package net.minecraft.client.gui;

import net.minecraft.network.play.server.SUpdateBossInfoPacket;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.BossInfo;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ClientBossInfo extends BossInfo {
   protected float rawPercent;
   protected long percentSetTime;

   public ClientBossInfo(SUpdateBossInfoPacket p_i46605_1_) {
      super(p_i46605_1_.getUniqueId(), p_i46605_1_.getName(), p_i46605_1_.getColor(), p_i46605_1_.getOverlay());
      this.rawPercent = p_i46605_1_.getPercent();
      this.percent = p_i46605_1_.getPercent();
      this.percentSetTime = Util.milliTime();
      this.setDarkenSky(p_i46605_1_.shouldDarkenSky());
      this.setPlayEndBossMusic(p_i46605_1_.shouldPlayEndBossMusic());
      this.setCreateFog(p_i46605_1_.shouldCreateFog());
   }

   public void setPercent(float p_186735_1_) {
      this.percent = this.getPercent();
      this.rawPercent = p_186735_1_;
      this.percentSetTime = Util.milliTime();
   }

   public float getPercent() {
      long lvt_1_1_ = Util.milliTime() - this.percentSetTime;
      float lvt_3_1_ = MathHelper.clamp((float)lvt_1_1_ / 100.0F, 0.0F, 1.0F);
      return MathHelper.lerp(lvt_3_1_, this.percent, this.rawPercent);
   }

   public void updateFromPacket(SUpdateBossInfoPacket p_186765_1_) {
      switch(p_186765_1_.getOperation()) {
      case UPDATE_NAME:
         this.setName(p_186765_1_.getName());
         break;
      case UPDATE_PCT:
         this.setPercent(p_186765_1_.getPercent());
         break;
      case UPDATE_STYLE:
         this.setColor(p_186765_1_.getColor());
         this.setOverlay(p_186765_1_.getOverlay());
         break;
      case UPDATE_PROPERTIES:
         this.setDarkenSky(p_186765_1_.shouldDarkenSky());
         this.setPlayEndBossMusic(p_186765_1_.shouldPlayEndBossMusic());
      }

   }
}
