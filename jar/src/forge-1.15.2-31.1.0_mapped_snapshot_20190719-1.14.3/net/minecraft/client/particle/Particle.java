package net.minecraft.client.particle;

import com.mojang.blaze3d.vertex.IVertexBuilder;
import java.util.Random;
import java.util.stream.Stream;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.ReuseableStream;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class Particle {
   private static final AxisAlignedBB EMPTY_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D);
   protected final World world;
   protected double prevPosX;
   protected double prevPosY;
   protected double prevPosZ;
   protected double posX;
   protected double posY;
   protected double posZ;
   protected double motionX;
   protected double motionY;
   protected double motionZ;
   private AxisAlignedBB boundingBox;
   protected boolean onGround;
   protected boolean canCollide;
   private boolean field_228343_B_;
   protected boolean isExpired;
   protected float width;
   protected float height;
   protected final Random rand;
   protected int age;
   protected int maxAge;
   protected float particleGravity;
   protected float particleRed;
   protected float particleGreen;
   protected float particleBlue;
   protected float particleAlpha;
   protected float particleAngle;
   protected float prevParticleAngle;

   protected Particle(World p_i46352_1_, double p_i46352_2_, double p_i46352_4_, double p_i46352_6_) {
      this.boundingBox = EMPTY_AABB;
      this.canCollide = true;
      this.width = 0.6F;
      this.height = 1.8F;
      this.rand = new Random();
      this.particleRed = 1.0F;
      this.particleGreen = 1.0F;
      this.particleBlue = 1.0F;
      this.particleAlpha = 1.0F;
      this.world = p_i46352_1_;
      this.setSize(0.2F, 0.2F);
      this.setPosition(p_i46352_2_, p_i46352_4_, p_i46352_6_);
      this.prevPosX = p_i46352_2_;
      this.prevPosY = p_i46352_4_;
      this.prevPosZ = p_i46352_6_;
      this.maxAge = (int)(4.0F / (this.rand.nextFloat() * 0.9F + 0.1F));
   }

   public Particle(World p_i1219_1_, double p_i1219_2_, double p_i1219_4_, double p_i1219_6_, double p_i1219_8_, double p_i1219_10_, double p_i1219_12_) {
      this(p_i1219_1_, p_i1219_2_, p_i1219_4_, p_i1219_6_);
      this.motionX = p_i1219_8_ + (Math.random() * 2.0D - 1.0D) * 0.4000000059604645D;
      this.motionY = p_i1219_10_ + (Math.random() * 2.0D - 1.0D) * 0.4000000059604645D;
      this.motionZ = p_i1219_12_ + (Math.random() * 2.0D - 1.0D) * 0.4000000059604645D;
      float lvt_14_1_ = (float)(Math.random() + Math.random() + 1.0D) * 0.15F;
      float lvt_15_1_ = MathHelper.sqrt(this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ);
      this.motionX = this.motionX / (double)lvt_15_1_ * (double)lvt_14_1_ * 0.4000000059604645D;
      this.motionY = this.motionY / (double)lvt_15_1_ * (double)lvt_14_1_ * 0.4000000059604645D + 0.10000000149011612D;
      this.motionZ = this.motionZ / (double)lvt_15_1_ * (double)lvt_14_1_ * 0.4000000059604645D;
   }

   public Particle multiplyVelocity(float p_70543_1_) {
      this.motionX *= (double)p_70543_1_;
      this.motionY = (this.motionY - 0.10000000149011612D) * (double)p_70543_1_ + 0.10000000149011612D;
      this.motionZ *= (double)p_70543_1_;
      return this;
   }

   public Particle multipleParticleScaleBy(float p_70541_1_) {
      this.setSize(0.2F * p_70541_1_, 0.2F * p_70541_1_);
      return this;
   }

   public void setColor(float p_70538_1_, float p_70538_2_, float p_70538_3_) {
      this.particleRed = p_70538_1_;
      this.particleGreen = p_70538_2_;
      this.particleBlue = p_70538_3_;
   }

   protected void setAlphaF(float p_82338_1_) {
      this.particleAlpha = p_82338_1_;
   }

   public void setMaxAge(int p_187114_1_) {
      this.maxAge = p_187114_1_;
   }

   public int getMaxAge() {
      return this.maxAge;
   }

   public void tick() {
      this.prevPosX = this.posX;
      this.prevPosY = this.posY;
      this.prevPosZ = this.posZ;
      if (this.age++ >= this.maxAge) {
         this.setExpired();
      } else {
         this.motionY -= 0.04D * (double)this.particleGravity;
         this.move(this.motionX, this.motionY, this.motionZ);
         this.motionX *= 0.9800000190734863D;
         this.motionY *= 0.9800000190734863D;
         this.motionZ *= 0.9800000190734863D;
         if (this.onGround) {
            this.motionX *= 0.699999988079071D;
            this.motionZ *= 0.699999988079071D;
         }

      }
   }

   public abstract void func_225606_a_(IVertexBuilder var1, ActiveRenderInfo var2, float var3);

   public abstract IParticleRenderType getRenderType();

   public String toString() {
      return this.getClass().getSimpleName() + ", Pos (" + this.posX + "," + this.posY + "," + this.posZ + "), RGBA (" + this.particleRed + "," + this.particleGreen + "," + this.particleBlue + "," + this.particleAlpha + "), Age " + this.age;
   }

   public void setExpired() {
      this.isExpired = true;
   }

   protected void setSize(float p_187115_1_, float p_187115_2_) {
      if (p_187115_1_ != this.width || p_187115_2_ != this.height) {
         this.width = p_187115_1_;
         this.height = p_187115_2_;
         AxisAlignedBB lvt_3_1_ = this.getBoundingBox();
         double lvt_4_1_ = (lvt_3_1_.minX + lvt_3_1_.maxX - (double)p_187115_1_) / 2.0D;
         double lvt_6_1_ = (lvt_3_1_.minZ + lvt_3_1_.maxZ - (double)p_187115_1_) / 2.0D;
         this.setBoundingBox(new AxisAlignedBB(lvt_4_1_, lvt_3_1_.minY, lvt_6_1_, lvt_4_1_ + (double)this.width, lvt_3_1_.minY + (double)this.height, lvt_6_1_ + (double)this.width));
      }

   }

   public void setPosition(double p_187109_1_, double p_187109_3_, double p_187109_5_) {
      this.posX = p_187109_1_;
      this.posY = p_187109_3_;
      this.posZ = p_187109_5_;
      float lvt_7_1_ = this.width / 2.0F;
      float lvt_8_1_ = this.height;
      this.setBoundingBox(new AxisAlignedBB(p_187109_1_ - (double)lvt_7_1_, p_187109_3_, p_187109_5_ - (double)lvt_7_1_, p_187109_1_ + (double)lvt_7_1_, p_187109_3_ + (double)lvt_8_1_, p_187109_5_ + (double)lvt_7_1_));
   }

   public void move(double p_187110_1_, double p_187110_3_, double p_187110_5_) {
      if (!this.field_228343_B_) {
         double lvt_7_1_ = p_187110_1_;
         double lvt_9_1_ = p_187110_3_;
         if (this.canCollide && (p_187110_1_ != 0.0D || p_187110_3_ != 0.0D || p_187110_5_ != 0.0D)) {
            Vec3d lvt_13_1_ = Entity.func_223307_a((Entity)null, new Vec3d(p_187110_1_, p_187110_3_, p_187110_5_), this.getBoundingBox(), this.world, ISelectionContext.dummy(), new ReuseableStream(Stream.empty()));
            p_187110_1_ = lvt_13_1_.x;
            p_187110_3_ = lvt_13_1_.y;
            p_187110_5_ = lvt_13_1_.z;
         }

         if (p_187110_1_ != 0.0D || p_187110_3_ != 0.0D || p_187110_5_ != 0.0D) {
            this.setBoundingBox(this.getBoundingBox().offset(p_187110_1_, p_187110_3_, p_187110_5_));
            this.resetPositionToBB();
         }

         if (Math.abs(p_187110_3_) >= 9.999999747378752E-6D && Math.abs(p_187110_3_) < 9.999999747378752E-6D) {
            this.field_228343_B_ = true;
         }

         this.onGround = p_187110_3_ != p_187110_3_ && lvt_9_1_ < 0.0D;
         if (lvt_7_1_ != p_187110_1_) {
            this.motionX = 0.0D;
         }

         if (p_187110_5_ != p_187110_5_) {
            this.motionZ = 0.0D;
         }

      }
   }

   protected void resetPositionToBB() {
      AxisAlignedBB lvt_1_1_ = this.getBoundingBox();
      this.posX = (lvt_1_1_.minX + lvt_1_1_.maxX) / 2.0D;
      this.posY = lvt_1_1_.minY;
      this.posZ = (lvt_1_1_.minZ + lvt_1_1_.maxZ) / 2.0D;
   }

   protected int getBrightnessForRender(float p_189214_1_) {
      BlockPos lvt_2_1_ = new BlockPos(this.posX, this.posY, this.posZ);
      return this.world.isBlockLoaded(lvt_2_1_) ? WorldRenderer.func_228421_a_(this.world, lvt_2_1_) : 0;
   }

   public boolean isAlive() {
      return !this.isExpired;
   }

   public AxisAlignedBB getBoundingBox() {
      return this.boundingBox;
   }

   public void setBoundingBox(AxisAlignedBB p_187108_1_) {
      this.boundingBox = p_187108_1_;
   }
}
