package net.minecraft.particles;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.registry.Registry;

public class BasicParticleType extends ParticleType<BasicParticleType> implements IParticleData {
   private static final IParticleData.IDeserializer<BasicParticleType> DESERIALIZER = new IParticleData.IDeserializer<BasicParticleType>() {
      public BasicParticleType deserialize(ParticleType<BasicParticleType> p_197544_1_, StringReader p_197544_2_) throws CommandSyntaxException {
         return (BasicParticleType)p_197544_1_;
      }

      public BasicParticleType read(ParticleType<BasicParticleType> p_197543_1_, PacketBuffer p_197543_2_) {
         return (BasicParticleType)p_197543_1_;
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

   public BasicParticleType(boolean p_i50791_1_) {
      super(p_i50791_1_, DESERIALIZER);
   }

   public ParticleType<BasicParticleType> getType() {
      return this;
   }

   public void write(PacketBuffer p_197553_1_) {
   }

   public String getParameters() {
      return Registry.PARTICLE_TYPE.getKey(this).toString();
   }
}
