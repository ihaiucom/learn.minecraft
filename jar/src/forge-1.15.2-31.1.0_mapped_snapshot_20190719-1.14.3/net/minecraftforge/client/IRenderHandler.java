package net.minecraftforge.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.world.ClientWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@FunctionalInterface
public interface IRenderHandler {
   @OnlyIn(Dist.CLIENT)
   void render(int var1, float var2, ClientWorld var3, Minecraft var4);
}
