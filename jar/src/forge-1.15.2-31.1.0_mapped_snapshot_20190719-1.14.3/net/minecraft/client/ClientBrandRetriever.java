package net.minecraft.client;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.BrandingControl;

@OnlyIn(Dist.CLIENT)
public class ClientBrandRetriever {
   public static String getClientModName() {
      return BrandingControl.getClientBranding();
   }
}
