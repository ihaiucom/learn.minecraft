package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleType;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SSpawnParticlePacket implements IPacket<IClientPlayNetHandler> {
   private double xCoord;
   private double yCoord;
   private double zCoord;
   private float xOffset;
   private float yOffset;
   private float zOffset;
   private float particleSpeed;
   private int particleCount;
   private boolean longDistance;
   private IParticleData particle;

   public SSpawnParticlePacket() {
   }

   public <T extends IParticleData> SSpawnParticlePacket(T p_i229960_1_, boolean p_i229960_2_, double p_i229960_3_, double p_i229960_5_, double p_i229960_7_, float p_i229960_9_, float p_i229960_10_, float p_i229960_11_, float p_i229960_12_, int p_i229960_13_) {
      this.particle = p_i229960_1_;
      this.longDistance = p_i229960_2_;
      this.xCoord = p_i229960_3_;
      this.yCoord = p_i229960_5_;
      this.zCoord = p_i229960_7_;
      this.xOffset = p_i229960_9_;
      this.yOffset = p_i229960_10_;
      this.zOffset = p_i229960_11_;
      this.particleSpeed = p_i229960_12_;
      this.particleCount = p_i229960_13_;
   }

   public void readPacketData(PacketBuffer p_148837_1_) throws IOException {
      ParticleType<?> lvt_2_1_ = (ParticleType)Registry.PARTICLE_TYPE.getByValue(p_148837_1_.readInt());
      if (lvt_2_1_ == null) {
         lvt_2_1_ = ParticleTypes.BARRIER;
      }

      this.longDistance = p_148837_1_.readBoolean();
      this.xCoord = p_148837_1_.readDouble();
      this.yCoord = p_148837_1_.readDouble();
      this.zCoord = p_148837_1_.readDouble();
      this.xOffset = p_148837_1_.readFloat();
      this.yOffset = p_148837_1_.readFloat();
      this.zOffset = p_148837_1_.readFloat();
      this.particleSpeed = p_148837_1_.readFloat();
      this.particleCount = p_148837_1_.readInt();
      this.particle = this.readParticle(p_148837_1_, (ParticleType)lvt_2_1_);
   }

   private <T extends IParticleData> T readParticle(PacketBuffer p_199855_1_, ParticleType<T> p_199855_2_) {
      return p_199855_2_.getDeserializer().read(p_199855_2_, p_199855_1_);
   }

   public void writePacketData(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeInt(Registry.PARTICLE_TYPE.getId(this.particle.getType()));
      p_148840_1_.writeBoolean(this.longDistance);
      p_148840_1_.writeDouble(this.xCoord);
      p_148840_1_.writeDouble(this.yCoord);
      p_148840_1_.writeDouble(this.zCoord);
      p_148840_1_.writeFloat(this.xOffset);
      p_148840_1_.writeFloat(this.yOffset);
      p_148840_1_.writeFloat(this.zOffset);
      p_148840_1_.writeFloat(this.particleSpeed);
      p_148840_1_.writeInt(this.particleCount);
      this.particle.write(p_148840_1_);
   }

   @OnlyIn(Dist.CLIENT)
   public boolean isLongDistance() {
      return this.longDistance;
   }

   @OnlyIn(Dist.CLIENT)
   public double getXCoordinate() {
      return this.xCoord;
   }

   @OnlyIn(Dist.CLIENT)
   public double getYCoordinate() {
      return this.yCoord;
   }

   @OnlyIn(Dist.CLIENT)
   public double getZCoordinate() {
      return this.zCoord;
   }

   @OnlyIn(Dist.CLIENT)
   public float getXOffset() {
      return this.xOffset;
   }

   @OnlyIn(Dist.CLIENT)
   public float getYOffset() {
      return this.yOffset;
   }

   @OnlyIn(Dist.CLIENT)
   public float getZOffset() {
      return this.zOffset;
   }

   @OnlyIn(Dist.CLIENT)
   public float getParticleSpeed() {
      return this.particleSpeed;
   }

   @OnlyIn(Dist.CLIENT)
   public int getParticleCount() {
      return this.particleCount;
   }

   @OnlyIn(Dist.CLIENT)
   public IParticleData getParticle() {
      return this.particle;
   }

   public void processPacket(IClientPlayNetHandler p_148833_1_) {
      p_148833_1_.handleParticles(this);
   }
}
