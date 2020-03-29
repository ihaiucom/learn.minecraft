package net.minecraft.realms;

import net.minecraft.client.gui.widget.list.ExtendedList;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class RealmListEntry extends ExtendedList.AbstractListEntry<RealmListEntry> {
   public abstract void render(int var1, int var2, int var3, int var4, int var5, int var6, int var7, boolean var8, float var9);

   public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_) {
      return false;
   }
}
