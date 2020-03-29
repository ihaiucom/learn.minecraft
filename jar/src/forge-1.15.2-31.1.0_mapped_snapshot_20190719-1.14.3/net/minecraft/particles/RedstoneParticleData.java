package net.minecraft.particles;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Locale;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class RedstoneParticleData implements IParticleData {
   public static final RedstoneParticleData REDSTONE_DUST = new RedstoneParticleData(1.0F, 0.0F, 0.0F, 1.0F);
   public static final IParticleData.IDeserializer<RedstoneParticleData> DESERIALIZER = new IParticleData.IDeserializer<RedstoneParticleData>() {
      public RedstoneParticleData deserialize(ParticleType<RedstoneParticleData> p_197544_1_, StringReader p_197544_2_) throws CommandSyntaxException {
         p_197544_2_.expect(' ');
         float lvt_3_1_ = (float)p_197544_2_.readDouble();
         p_197544_2_.expect(' ');
         float lvt_4_1_ = (float)p_197544_2_.readDouble();
         p_197544_2_.expect(' ');
         float lvt_5_1_ = (float)p_197544_2_.readDouble();
         p_197544_2_.expect(' ');
         float lvt_6_1_ = (float)p_197544_2_.readDouble();
         return new RedstoneParticleData(lvt_3_1_, lvt_4_1_, lvt_5_1_, lvt_6_1_);
      }

      public RedstoneParticleData read(ParticleType<RedstoneParticleData> p_197543_1_, PacketBuffer p_197543_2_) {
         return new RedstoneParticleData(p_197543_2_.readFloat(), p_197543_2_.readFloat(), p_197543_2_.readFloat(), p_197543_2_.readFloat());
      }

      // $FF: synthetic method
      public IParticleData read(ParticleType p_197543_1_, PacketBuffer p_197543_2_) {
         return this.read(p_197543_1_, p_197543_2_);
      }

      // $FF: synthetic method
      public IParticleData deserialize(ParticleType p_197544_1_, StringReader p_197544_2_) throws CommandSyntaxException {
         return this.deserialize(p_197544_1_, p_197544_2_);
      }
   };
   private final float red;
   private final float green;
   private final float blue;
   private final float alpha;

   public RedstoneParticleData(float p_i47950_1_, float p_i47950_2_, float p_i47950_3_, float p_i47950_4_) {
      this.red = p_i47950_1_;
      this.green = p_i47950_2_;
      this.blue = p_i47950_3_;
      this.alpha = MathHelper.clamp(p_i47950_4_, 0.01F, 4.0F);
   }

   public void write(PacketBuffer p_197553_1_) {
      p_197553_1_.writeFloat(this.red);
      p_197553_1_.writeFloat(this.green);
      p_197553_1_.writeFloat(this.blue);
      p_197553_1_.writeFloat(this.alpha);
   }

   public String getParameters() {
      return String.format(Locale.ROOT, "%s %.2f %.2f %.2f %.2f", Registry.PARTICLE_TYPE.getKey(this.getType()), this.red, this.green, this.blue, this.alpha);
   }

   public ParticleType<RedstoneParticleData> getType() {
      return ParticleTypes.DUST;
   }

   @OnlyIn(Dist.CLIENT)
   public float getRed() {
      return this.red;
   }

   @OnlyIn(Dist.CLIENT)
   public float getGreen() {
      return this.green;
   }

   @OnlyIn(Dist.CLIENT)
   public float getBlue() {
      return this.blue;
   }

   @OnlyIn(Dist.CLIENT)
   public float getAlpha() {
      return this.alpha;
   }
}
