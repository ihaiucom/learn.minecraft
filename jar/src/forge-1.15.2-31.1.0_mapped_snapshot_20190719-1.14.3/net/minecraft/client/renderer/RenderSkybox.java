package net.minecraft.client.renderer;

import net.minecraft.client.Minecraft;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderSkybox {
   private final Minecraft mc;
   private final RenderSkyboxCube renderer;
   private float time;

   public RenderSkybox(RenderSkyboxCube p_i49377_1_) {
      this.renderer = p_i49377_1_;
      this.mc = Minecraft.getInstance();
   }

   public void render(float p_217623_1_, float p_217623_2_) {
      this.time += p_217623_1_;
      this.renderer.render(this.mc, MathHelper.sin(this.time * 0.001F) * 5.0F + 25.0F, -this.time * 0.1F, p_217623_2_);
   }
}
