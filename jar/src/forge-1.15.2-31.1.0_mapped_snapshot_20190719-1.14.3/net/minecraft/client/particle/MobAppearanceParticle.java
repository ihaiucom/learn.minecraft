package net.minecraft.client.particle;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.entity.ElderGuardianRenderer;
import net.minecraft.client.renderer.entity.model.GuardianModel;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class MobAppearanceParticle extends Particle {
   private final Model field_228342_a_;
   private final RenderType field_228341_A_;

   private MobAppearanceParticle(World p_i46283_1_, double p_i46283_2_, double p_i46283_4_, double p_i46283_6_) {
      super(p_i46283_1_, p_i46283_2_, p_i46283_4_, p_i46283_6_);
      this.field_228342_a_ = new GuardianModel();
      this.field_228341_A_ = RenderType.func_228644_e_(ElderGuardianRenderer.GUARDIAN_ELDER_TEXTURE);
      this.particleGravity = 0.0F;
      this.maxAge = 30;
   }

   public IParticleRenderType getRenderType() {
      return IParticleRenderType.CUSTOM;
   }

   public void func_225606_a_(IVertexBuilder p_225606_1_, ActiveRenderInfo p_225606_2_, float p_225606_3_) {
      float lvt_4_1_ = ((float)this.age + p_225606_3_) / (float)this.maxAge;
      float lvt_5_1_ = 0.05F + 0.5F * MathHelper.sin(lvt_4_1_ * 3.1415927F);
      MatrixStack lvt_6_1_ = new MatrixStack();
      lvt_6_1_.func_227863_a_(p_225606_2_.func_227995_f_());
      lvt_6_1_.func_227863_a_(Vector3f.field_229179_b_.func_229187_a_(150.0F * lvt_4_1_ - 60.0F));
      lvt_6_1_.func_227862_a_(-1.0F, -1.0F, 1.0F);
      lvt_6_1_.func_227861_a_(0.0D, -1.1009999513626099D, 1.5D);
      IRenderTypeBuffer.Impl lvt_7_1_ = Minecraft.getInstance().func_228019_au_().func_228487_b_();
      IVertexBuilder lvt_8_1_ = lvt_7_1_.getBuffer(this.field_228341_A_);
      this.field_228342_a_.func_225598_a_(lvt_6_1_, lvt_8_1_, 15728880, OverlayTexture.field_229196_a_, 1.0F, 1.0F, 1.0F, lvt_5_1_);
      lvt_7_1_.func_228461_a_();
   }

   // $FF: synthetic method
   MobAppearanceParticle(World p_i51022_1_, double p_i51022_2_, double p_i51022_4_, double p_i51022_6_, Object p_i51022_8_) {
      this(p_i51022_1_, p_i51022_2_, p_i51022_4_, p_i51022_6_);
   }

   @OnlyIn(Dist.CLIENT)
   public static class Factory implements IParticleFactory<BasicParticleType> {
      public Particle makeParticle(BasicParticleType p_199234_1_, World p_199234_2_, double p_199234_3_, double p_199234_5_, double p_199234_7_, double p_199234_9_, double p_199234_11_, double p_199234_13_) {
         return new MobAppearanceParticle(p_199234_2_, p_199234_3_, p_199234_5_, p_199234_7_);
      }
   }
}
