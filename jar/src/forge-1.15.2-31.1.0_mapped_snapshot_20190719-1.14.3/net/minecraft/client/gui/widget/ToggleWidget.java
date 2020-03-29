package net.minecraft.client.gui.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ToggleWidget extends Widget {
   protected ResourceLocation resourceLocation;
   protected boolean stateTriggered;
   protected int xTexStart;
   protected int yTexStart;
   protected int xDiffTex;
   protected int yDiffTex;

   public ToggleWidget(int p_i51128_1_, int p_i51128_2_, int p_i51128_3_, int p_i51128_4_, boolean p_i51128_5_) {
      super(p_i51128_1_, p_i51128_2_, p_i51128_3_, p_i51128_4_, "");
      this.stateTriggered = p_i51128_5_;
   }

   public void initTextureValues(int p_191751_1_, int p_191751_2_, int p_191751_3_, int p_191751_4_, ResourceLocation p_191751_5_) {
      this.xTexStart = p_191751_1_;
      this.yTexStart = p_191751_2_;
      this.xDiffTex = p_191751_3_;
      this.yDiffTex = p_191751_4_;
      this.resourceLocation = p_191751_5_;
   }

   public void setStateTriggered(boolean p_191753_1_) {
      this.stateTriggered = p_191753_1_;
   }

   public boolean isStateTriggered() {
      return this.stateTriggered;
   }

   public void setPosition(int p_191752_1_, int p_191752_2_) {
      this.x = p_191752_1_;
      this.y = p_191752_2_;
   }

   public void renderButton(int p_renderButton_1_, int p_renderButton_2_, float p_renderButton_3_) {
      Minecraft lvt_4_1_ = Minecraft.getInstance();
      lvt_4_1_.getTextureManager().bindTexture(this.resourceLocation);
      RenderSystem.disableDepthTest();
      int lvt_5_1_ = this.xTexStart;
      int lvt_6_1_ = this.yTexStart;
      if (this.stateTriggered) {
         lvt_5_1_ += this.xDiffTex;
      }

      if (this.isHovered()) {
         lvt_6_1_ += this.yDiffTex;
      }

      this.blit(this.x, this.y, lvt_5_1_, lvt_6_1_, this.width, this.height);
      RenderSystem.enableDepthTest();
   }
}
