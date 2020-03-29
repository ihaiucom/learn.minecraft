package net.minecraft.client.resources;

import java.io.IOException;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.GrassColors;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.resource.IResourceType;
import net.minecraftforge.resource.VanillaResourceType;

@OnlyIn(Dist.CLIENT)
public class GrassColorReloadListener extends ReloadListener<int[]> {
   private static final ResourceLocation GRASS_LOCATION = new ResourceLocation("textures/colormap/grass.png");

   protected int[] prepare(IResourceManager p_212854_1_, IProfiler p_212854_2_) {
      try {
         return ColorMapLoader.loadColors(p_212854_1_, GRASS_LOCATION);
      } catch (IOException var4) {
         throw new IllegalStateException("Failed to load grass color texture", var4);
      }
   }

   protected void apply(int[] p_212853_1_, IResourceManager p_212853_2_, IProfiler p_212853_3_) {
      GrassColors.setGrassBiomeColorizer(p_212853_1_);
   }

   public IResourceType getResourceType() {
      return VanillaResourceType.TEXTURES;
   }
}
