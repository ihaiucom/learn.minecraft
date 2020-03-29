package net.minecraft.client.particle;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderTypeBuffers;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ItemPickupParticle extends Particle {
   private final RenderTypeBuffers field_228340_a_;
   private final Entity item;
   private final Entity target;
   private int age;
   private final EntityRendererManager renderManager;

   public ItemPickupParticle(EntityRendererManager p_i225963_1_, RenderTypeBuffers p_i225963_2_, World p_i225963_3_, Entity p_i225963_4_, Entity p_i225963_5_) {
      this(p_i225963_1_, p_i225963_2_, p_i225963_3_, p_i225963_4_, p_i225963_5_, p_i225963_4_.getMotion());
   }

   private ItemPickupParticle(EntityRendererManager p_i225964_1_, RenderTypeBuffers p_i225964_2_, World p_i225964_3_, Entity p_i225964_4_, Entity p_i225964_5_, Vec3d p_i225964_6_) {
      super(p_i225964_3_, p_i225964_4_.func_226277_ct_(), p_i225964_4_.func_226278_cu_(), p_i225964_4_.func_226281_cx_(), p_i225964_6_.x, p_i225964_6_.y, p_i225964_6_.z);
      this.field_228340_a_ = p_i225964_2_;
      this.item = p_i225964_4_;
      this.target = p_i225964_5_;
      this.renderManager = p_i225964_1_;
   }

   public IParticleRenderType getRenderType() {
      return IParticleRenderType.CUSTOM;
   }

   public void func_225606_a_(IVertexBuilder p_225606_1_, ActiveRenderInfo p_225606_2_, float p_225606_3_) {
      float lvt_4_1_ = ((float)this.age + p_225606_3_) / 3.0F;
      lvt_4_1_ *= lvt_4_1_;
      double lvt_5_1_ = MathHelper.lerp((double)p_225606_3_, this.target.lastTickPosX, this.target.func_226277_ct_());
      double lvt_7_1_ = MathHelper.lerp((double)p_225606_3_, this.target.lastTickPosY, this.target.func_226278_cu_()) + 0.5D;
      double lvt_9_1_ = MathHelper.lerp((double)p_225606_3_, this.target.lastTickPosZ, this.target.func_226281_cx_());
      double lvt_11_1_ = MathHelper.lerp((double)lvt_4_1_, this.item.func_226277_ct_(), lvt_5_1_);
      double lvt_13_1_ = MathHelper.lerp((double)lvt_4_1_, this.item.func_226278_cu_(), lvt_7_1_);
      double lvt_15_1_ = MathHelper.lerp((double)lvt_4_1_, this.item.func_226281_cx_(), lvt_9_1_);
      IRenderTypeBuffer.Impl lvt_17_1_ = this.field_228340_a_.func_228487_b_();
      Vec3d lvt_18_1_ = p_225606_2_.getProjectedView();
      this.renderManager.func_229084_a_(this.item, lvt_11_1_ - lvt_18_1_.getX(), lvt_13_1_ - lvt_18_1_.getY(), lvt_15_1_ - lvt_18_1_.getZ(), this.item.rotationYaw, p_225606_3_, new MatrixStack(), lvt_17_1_, this.renderManager.func_229085_a_(this.item, p_225606_3_));
      lvt_17_1_.func_228461_a_();
   }

   public void tick() {
      ++this.age;
      if (this.age == 3) {
         this.setExpired();
      }

   }
}
