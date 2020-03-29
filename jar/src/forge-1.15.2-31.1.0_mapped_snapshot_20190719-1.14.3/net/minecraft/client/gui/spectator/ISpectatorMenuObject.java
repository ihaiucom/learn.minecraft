package net.minecraft.client.gui.spectator;

import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public interface ISpectatorMenuObject {
   void selectItem(SpectatorMenu var1);

   ITextComponent getSpectatorName();

   void renderIcon(float var1, int var2);

   boolean isEnabled();
}
