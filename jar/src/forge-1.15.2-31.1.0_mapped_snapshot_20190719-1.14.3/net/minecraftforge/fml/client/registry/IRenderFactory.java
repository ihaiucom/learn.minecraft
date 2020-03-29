package net.minecraftforge.fml.client.registry;

import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.entity.Entity;

public interface IRenderFactory<T extends Entity> {
   EntityRenderer<? super T> createRenderFor(EntityRendererManager var1);
}
