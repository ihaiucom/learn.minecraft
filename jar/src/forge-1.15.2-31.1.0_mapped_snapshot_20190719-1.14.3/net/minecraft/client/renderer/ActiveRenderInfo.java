package net.minecraft.client.renderer;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.fluid.IFluidState;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ActiveRenderInfo {
   private boolean valid;
   private IBlockReader world;
   private Entity renderViewEntity;
   private Vec3d pos;
   private final BlockPos.Mutable blockPos;
   private final Vector3f look;
   private final Vector3f up;
   private final Vector3f field_216796_h;
   private float pitch;
   private float yaw;
   private final Quaternion field_227994_k_;
   private boolean thirdPerson;
   private boolean thirdPersonReverse;
   private float height;
   private float previousHeight;

   public ActiveRenderInfo() {
      this.pos = Vec3d.ZERO;
      this.blockPos = new BlockPos.Mutable();
      this.look = new Vector3f(0.0F, 0.0F, 1.0F);
      this.up = new Vector3f(0.0F, 1.0F, 0.0F);
      this.field_216796_h = new Vector3f(1.0F, 0.0F, 0.0F);
      this.field_227994_k_ = new Quaternion(0.0F, 0.0F, 0.0F, 1.0F);
   }

   public void update(IBlockReader p_216772_1_, Entity p_216772_2_, boolean p_216772_3_, boolean p_216772_4_, float p_216772_5_) {
      this.valid = true;
      this.world = p_216772_1_;
      this.renderViewEntity = p_216772_2_;
      this.thirdPerson = p_216772_3_;
      this.thirdPersonReverse = p_216772_4_;
      this.setDirection(p_216772_2_.getYaw(p_216772_5_), p_216772_2_.getPitch(p_216772_5_));
      this.setPosition(MathHelper.lerp((double)p_216772_5_, p_216772_2_.prevPosX, p_216772_2_.func_226277_ct_()), MathHelper.lerp((double)p_216772_5_, p_216772_2_.prevPosY, p_216772_2_.func_226278_cu_()) + (double)MathHelper.lerp(p_216772_5_, this.previousHeight, this.height), MathHelper.lerp((double)p_216772_5_, p_216772_2_.prevPosZ, p_216772_2_.func_226281_cx_()));
      if (p_216772_3_) {
         if (p_216772_4_) {
            this.setDirection(this.yaw + 180.0F, -this.pitch);
         }

         this.movePosition(-this.calcCameraDistance(4.0D), 0.0D, 0.0D);
      } else if (p_216772_2_ instanceof LivingEntity && ((LivingEntity)p_216772_2_).isSleeping()) {
         Direction direction = ((LivingEntity)p_216772_2_).getBedDirection();
         this.setDirection(direction != null ? direction.getHorizontalAngle() - 180.0F : 0.0F, 0.0F);
         this.movePosition(0.0D, 0.3D, 0.0D);
      }

   }

   public void interpolateHeight() {
      if (this.renderViewEntity != null) {
         this.previousHeight = this.height;
         this.height += (this.renderViewEntity.getEyeHeight() - this.height) * 0.5F;
      }

   }

   private double calcCameraDistance(double p_216779_1_) {
      for(int i = 0; i < 8; ++i) {
         float f = (float)((i & 1) * 2 - 1);
         float f1 = (float)((i >> 1 & 1) * 2 - 1);
         float f2 = (float)((i >> 2 & 1) * 2 - 1);
         f *= 0.1F;
         f1 *= 0.1F;
         f2 *= 0.1F;
         Vec3d vec3d = this.pos.add((double)f, (double)f1, (double)f2);
         Vec3d vec3d1 = new Vec3d(this.pos.x - (double)this.look.getX() * p_216779_1_ + (double)f + (double)f2, this.pos.y - (double)this.look.getY() * p_216779_1_ + (double)f1, this.pos.z - (double)this.look.getZ() * p_216779_1_ + (double)f2);
         RayTraceResult raytraceresult = this.world.rayTraceBlocks(new RayTraceContext(vec3d, vec3d1, RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.NONE, this.renderViewEntity));
         if (raytraceresult.getType() != RayTraceResult.Type.MISS) {
            double d0 = raytraceresult.getHitVec().distanceTo(this.pos);
            if (d0 < p_216779_1_) {
               p_216779_1_ = d0;
            }
         }
      }

      return p_216779_1_;
   }

   protected void movePosition(double p_216782_1_, double p_216782_3_, double p_216782_5_) {
      double d0 = (double)this.look.getX() * p_216782_1_ + (double)this.up.getX() * p_216782_3_ + (double)this.field_216796_h.getX() * p_216782_5_;
      double d1 = (double)this.look.getY() * p_216782_1_ + (double)this.up.getY() * p_216782_3_ + (double)this.field_216796_h.getY() * p_216782_5_;
      double d2 = (double)this.look.getZ() * p_216782_1_ + (double)this.up.getZ() * p_216782_3_ + (double)this.field_216796_h.getZ() * p_216782_5_;
      this.setPostion(new Vec3d(this.pos.x + d0, this.pos.y + d1, this.pos.z + d2));
   }

   protected void setDirection(float p_216776_1_, float p_216776_2_) {
      this.pitch = p_216776_2_;
      this.yaw = p_216776_1_;
      this.field_227994_k_.func_227066_a_(0.0F, 0.0F, 0.0F, 1.0F);
      this.field_227994_k_.multiply(Vector3f.field_229181_d_.func_229187_a_(-p_216776_1_));
      this.field_227994_k_.multiply(Vector3f.field_229179_b_.func_229187_a_(p_216776_2_));
      this.look.set(0.0F, 0.0F, 1.0F);
      this.look.func_214905_a(this.field_227994_k_);
      this.up.set(0.0F, 1.0F, 0.0F);
      this.up.func_214905_a(this.field_227994_k_);
      this.field_216796_h.set(1.0F, 0.0F, 0.0F);
      this.field_216796_h.func_214905_a(this.field_227994_k_);
   }

   protected void setPosition(double p_216775_1_, double p_216775_3_, double p_216775_5_) {
      this.setPostion(new Vec3d(p_216775_1_, p_216775_3_, p_216775_5_));
   }

   protected void setPostion(Vec3d p_216774_1_) {
      this.pos = p_216774_1_;
      this.blockPos.setPos(p_216774_1_.x, p_216774_1_.y, p_216774_1_.z);
   }

   public Vec3d getProjectedView() {
      return this.pos;
   }

   public BlockPos getBlockPos() {
      return this.blockPos;
   }

   public float getPitch() {
      return this.pitch;
   }

   public float getYaw() {
      return this.yaw;
   }

   public Quaternion func_227995_f_() {
      return this.field_227994_k_;
   }

   public Entity getRenderViewEntity() {
      return this.renderViewEntity;
   }

   public boolean isValid() {
      return this.valid;
   }

   public boolean isThirdPerson() {
      return this.thirdPerson;
   }

   public IFluidState getFluidState() {
      if (!this.valid) {
         return Fluids.EMPTY.getDefaultState();
      } else {
         IFluidState ifluidstate = this.world.getFluidState(this.blockPos);
         return !ifluidstate.isEmpty() && this.pos.y >= (double)((float)this.blockPos.getY() + ifluidstate.func_215679_a(this.world, this.blockPos)) ? Fluids.EMPTY.getDefaultState() : ifluidstate;
      }
   }

   public final Vector3f func_227996_l_() {
      return this.look;
   }

   public final Vector3f func_227997_m_() {
      return this.up;
   }

   public void clear() {
      this.world = null;
      this.renderViewEntity = null;
      this.valid = false;
   }

   public void setAnglesInternal(float p_setAnglesInternal_1_, float p_setAnglesInternal_2_) {
      this.yaw = p_setAnglesInternal_1_;
      this.pitch = p_setAnglesInternal_2_;
   }

   public BlockState getBlockAtCamera() {
      return !this.valid ? Blocks.AIR.getDefaultState() : this.world.getBlockState(this.blockPos).getStateAtViewpoint(this.world, this.blockPos, this.pos);
   }
}
