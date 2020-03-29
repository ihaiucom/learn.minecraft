package net.minecraft.client.renderer.color;

import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public interface IItemColor {
   int getColor(ItemStack var1, int var2);
}
