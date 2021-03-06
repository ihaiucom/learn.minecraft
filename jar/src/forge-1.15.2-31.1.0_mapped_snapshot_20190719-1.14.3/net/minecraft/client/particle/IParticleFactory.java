package net.minecraft.client.particle;

import javax.annotation.Nullable;
import net.minecraft.particles.IParticleData;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public interface IParticleFactory<T extends IParticleData> {
   @Nullable
   Particle makeParticle(T var1, World var2, double var3, double var5, double var7, double var9, double var11, double var13);
}
