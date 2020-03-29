package net.minecraft.network.play.server;

import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SExplosionPacket implements IPacket<IClientPlayNetHandler> {
   private double posX;
   private double posY;
   private double posZ;
   private float strength;
   private List<BlockPos> affectedBlockPositions;
   private float motionX;
   private float motionY;
   private float motionZ;

   public SExplosionPacket() {
   }

   public SExplosionPacket(double p_i47099_1_, double p_i47099_3_, double p_i47099_5_, float p_i47099_7_, List<BlockPos> p_i47099_8_, Vec3d p_i47099_9_) {
      this.posX = p_i47099_1_;
      this.posY = p_i47099_3_;
      this.posZ = p_i47099_5_;
      this.strength = p_i47099_7_;
      this.affectedBlockPositions = Lists.newArrayList(p_i47099_8_);
      if (p_i47099_9_ != null) {
         this.motionX = (float)p_i47099_9_.x;
         this.motionY = (float)p_i47099_9_.y;
         this.motionZ = (float)p_i47099_9_.z;
      }

   }

   public void readPacketData(PacketBuffer p_148837_1_) throws IOException {
      this.posX = (double)p_148837_1_.readFloat();
      this.posY = (double)p_148837_1_.readFloat();
      this.posZ = (double)p_148837_1_.readFloat();
      this.strength = p_148837_1_.readFloat();
      int lvt_2_1_ = p_148837_1_.readInt();
      this.affectedBlockPositions = Lists.newArrayListWithCapacity(lvt_2_1_);
      int lvt_3_1_ = MathHelper.floor(this.posX);
      int lvt_4_1_ = MathHelper.floor(this.posY);
      int lvt_5_1_ = MathHelper.floor(this.posZ);

      for(int lvt_6_1_ = 0; lvt_6_1_ < lvt_2_1_; ++lvt_6_1_) {
         int lvt_7_1_ = p_148837_1_.readByte() + lvt_3_1_;
         int lvt_8_1_ = p_148837_1_.readByte() + lvt_4_1_;
         int lvt_9_1_ = p_148837_1_.readByte() + lvt_5_1_;
         this.affectedBlockPositions.add(new BlockPos(lvt_7_1_, lvt_8_1_, lvt_9_1_));
      }

      this.motionX = p_148837_1_.readFloat();
      this.motionY = p_148837_1_.readFloat();
      this.motionZ = p_148837_1_.readFloat();
   }

   public void writePacketData(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeFloat((float)this.posX);
      p_148840_1_.writeFloat((float)this.posY);
      p_148840_1_.writeFloat((float)this.posZ);
      p_148840_1_.writeFloat(this.strength);
      p_148840_1_.writeInt(this.affectedBlockPositions.size());
      int lvt_2_1_ = MathHelper.floor(this.posX);
      int lvt_3_1_ = MathHelper.floor(this.posY);
      int lvt_4_1_ = MathHelper.floor(this.posZ);
      Iterator var5 = this.affectedBlockPositions.iterator();

      while(var5.hasNext()) {
         BlockPos lvt_6_1_ = (BlockPos)var5.next();
         int lvt_7_1_ = lvt_6_1_.getX() - lvt_2_1_;
         int lvt_8_1_ = lvt_6_1_.getY() - lvt_3_1_;
         int lvt_9_1_ = lvt_6_1_.getZ() - lvt_4_1_;
         p_148840_1_.writeByte(lvt_7_1_);
         p_148840_1_.writeByte(lvt_8_1_);
         p_148840_1_.writeByte(lvt_9_1_);
      }

      p_148840_1_.writeFloat(this.motionX);
      p_148840_1_.writeFloat(this.motionY);
      p_148840_1_.writeFloat(this.motionZ);
   }

   public void processPacket(IClientPlayNetHandler p_148833_1_) {
      p_148833_1_.handleExplosion(this);
   }

   @OnlyIn(Dist.CLIENT)
   public float getMotionX() {
      return this.motionX;
   }

   @OnlyIn(Dist.CLIENT)
   public float getMotionY() {
      return this.motionY;
   }

   @OnlyIn(Dist.CLIENT)
   public float getMotionZ() {
      return this.motionZ;
   }

   @OnlyIn(Dist.CLIENT)
   public double getX() {
      return this.posX;
   }

   @OnlyIn(Dist.CLIENT)
   public double getY() {
      return this.posY;
   }

   @OnlyIn(Dist.CLIENT)
   public double getZ() {
      return this.posZ;
   }

   @OnlyIn(Dist.CLIENT)
   public float getStrength() {
      return this.strength;
   }

   @OnlyIn(Dist.CLIENT)
   public List<BlockPos> getAffectedBlockPositions() {
      return this.affectedBlockPositions;
   }
}
