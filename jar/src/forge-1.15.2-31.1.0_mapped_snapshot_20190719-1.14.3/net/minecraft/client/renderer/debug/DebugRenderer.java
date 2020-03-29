package net.minecraft.client.renderer.debug;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Optional;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Matrix4f;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.TransformationMatrix;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.ProjectileHelper;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class DebugRenderer {
   public final PathfindingDebugRenderer pathfinding = new PathfindingDebugRenderer();
   public final DebugRenderer.IDebugRenderer water;
   public final DebugRenderer.IDebugRenderer chunkBorder;
   public final DebugRenderer.IDebugRenderer heightMap;
   public final DebugRenderer.IDebugRenderer collisionBox;
   public final DebugRenderer.IDebugRenderer neighborsUpdate;
   public final CaveDebugRenderer cave;
   public final StructureDebugRenderer structure;
   public final DebugRenderer.IDebugRenderer light;
   public final DebugRenderer.IDebugRenderer worldGenAttempts;
   public final DebugRenderer.IDebugRenderer solidFace;
   public final DebugRenderer.IDebugRenderer field_217740_l;
   public final PointOfInterestDebugRenderer field_217741_m;
   public final BeeDebugRenderer field_229017_n_;
   public final RaidDebugRenderer field_222927_n;
   public final EntityAIDebugRenderer field_217742_n;
   public final GameTestDebugRenderer field_229018_q_;
   private boolean chunkBorderEnabled;

   public DebugRenderer(Minecraft p_i46557_1_) {
      this.water = new WaterDebugRenderer(p_i46557_1_);
      this.chunkBorder = new ChunkBorderDebugRenderer(p_i46557_1_);
      this.heightMap = new HeightMapDebugRenderer(p_i46557_1_);
      this.collisionBox = new CollisionBoxDebugRenderer(p_i46557_1_);
      this.neighborsUpdate = new NeighborsUpdateDebugRenderer(p_i46557_1_);
      this.cave = new CaveDebugRenderer();
      this.structure = new StructureDebugRenderer(p_i46557_1_);
      this.light = new LightDebugRenderer(p_i46557_1_);
      this.worldGenAttempts = new WorldGenAttemptsDebugRenderer();
      this.solidFace = new SolidFaceDebugRenderer(p_i46557_1_);
      this.field_217740_l = new ChunkInfoDebugRenderer(p_i46557_1_);
      this.field_217741_m = new PointOfInterestDebugRenderer(p_i46557_1_);
      this.field_229017_n_ = new BeeDebugRenderer(p_i46557_1_);
      this.field_222927_n = new RaidDebugRenderer(p_i46557_1_);
      this.field_217742_n = new EntityAIDebugRenderer(p_i46557_1_);
      this.field_229018_q_ = new GameTestDebugRenderer();
   }

   public void func_217737_a() {
      this.pathfinding.func_217675_a();
      this.water.func_217675_a();
      this.chunkBorder.func_217675_a();
      this.heightMap.func_217675_a();
      this.collisionBox.func_217675_a();
      this.neighborsUpdate.func_217675_a();
      this.cave.func_217675_a();
      this.structure.func_217675_a();
      this.light.func_217675_a();
      this.worldGenAttempts.func_217675_a();
      this.solidFace.func_217675_a();
      this.field_217740_l.func_217675_a();
      this.field_217741_m.func_217675_a();
      this.field_229017_n_.func_217675_a();
      this.field_222927_n.func_217675_a();
      this.field_217742_n.func_217675_a();
      this.field_229018_q_.func_217675_a();
   }

   public boolean toggleChunkBorders() {
      this.chunkBorderEnabled = !this.chunkBorderEnabled;
      return this.chunkBorderEnabled;
   }

   public void func_229019_a_(MatrixStack p_229019_1_, IRenderTypeBuffer.Impl p_229019_2_, double p_229019_3_, double p_229019_5_, double p_229019_7_) {
      if (this.chunkBorderEnabled && !Minecraft.getInstance().isReducedDebug()) {
         this.chunkBorder.func_225619_a_(p_229019_1_, p_229019_2_, p_229019_3_, p_229019_5_, p_229019_7_);
      }

      this.field_229018_q_.func_225619_a_(p_229019_1_, p_229019_2_, p_229019_3_, p_229019_5_, p_229019_7_);
   }

   public static Optional<Entity> func_217728_a(@Nullable Entity p_217728_0_, int p_217728_1_) {
      if (p_217728_0_ == null) {
         return Optional.empty();
      } else {
         Vec3d lvt_2_1_ = p_217728_0_.getEyePosition(1.0F);
         Vec3d lvt_3_1_ = p_217728_0_.getLook(1.0F).scale((double)p_217728_1_);
         Vec3d lvt_4_1_ = lvt_2_1_.add(lvt_3_1_);
         AxisAlignedBB lvt_5_1_ = p_217728_0_.getBoundingBox().expand(lvt_3_1_).grow(1.0D);
         int lvt_6_1_ = p_217728_1_ * p_217728_1_;
         Predicate<Entity> lvt_7_1_ = (p_217727_0_) -> {
            return !p_217727_0_.isSpectator() && p_217727_0_.canBeCollidedWith();
         };
         EntityRayTraceResult lvt_8_1_ = ProjectileHelper.func_221273_a(p_217728_0_, lvt_2_1_, lvt_4_1_, lvt_5_1_, lvt_7_1_, (double)lvt_6_1_);
         if (lvt_8_1_ == null) {
            return Optional.empty();
         } else {
            return lvt_2_1_.squareDistanceTo(lvt_8_1_.getHitVec()) > (double)lvt_6_1_ ? Optional.empty() : Optional.of(lvt_8_1_.getEntity());
         }
      }
   }

   public static void func_217735_a(BlockPos p_217735_0_, BlockPos p_217735_1_, float p_217735_2_, float p_217735_3_, float p_217735_4_, float p_217735_5_) {
      ActiveRenderInfo lvt_6_1_ = Minecraft.getInstance().gameRenderer.getActiveRenderInfo();
      if (lvt_6_1_.isValid()) {
         Vec3d lvt_7_1_ = lvt_6_1_.getProjectedView().func_216371_e();
         AxisAlignedBB lvt_8_1_ = (new AxisAlignedBB(p_217735_0_, p_217735_1_)).offset(lvt_7_1_);
         func_217730_a(lvt_8_1_, p_217735_2_, p_217735_3_, p_217735_4_, p_217735_5_);
      }
   }

   public static void func_217736_a(BlockPos p_217736_0_, float p_217736_1_, float p_217736_2_, float p_217736_3_, float p_217736_4_, float p_217736_5_) {
      ActiveRenderInfo lvt_6_1_ = Minecraft.getInstance().gameRenderer.getActiveRenderInfo();
      if (lvt_6_1_.isValid()) {
         Vec3d lvt_7_1_ = lvt_6_1_.getProjectedView().func_216371_e();
         AxisAlignedBB lvt_8_1_ = (new AxisAlignedBB(p_217736_0_)).offset(lvt_7_1_).grow((double)p_217736_1_);
         func_217730_a(lvt_8_1_, p_217736_2_, p_217736_3_, p_217736_4_, p_217736_5_);
      }
   }

   public static void func_217730_a(AxisAlignedBB p_217730_0_, float p_217730_1_, float p_217730_2_, float p_217730_3_, float p_217730_4_) {
      func_217733_a(p_217730_0_.minX, p_217730_0_.minY, p_217730_0_.minZ, p_217730_0_.maxX, p_217730_0_.maxY, p_217730_0_.maxZ, p_217730_1_, p_217730_2_, p_217730_3_, p_217730_4_);
   }

   public static void func_217733_a(double p_217733_0_, double p_217733_2_, double p_217733_4_, double p_217733_6_, double p_217733_8_, double p_217733_10_, float p_217733_12_, float p_217733_13_, float p_217733_14_, float p_217733_15_) {
      Tessellator lvt_16_1_ = Tessellator.getInstance();
      BufferBuilder lvt_17_1_ = lvt_16_1_.getBuffer();
      lvt_17_1_.begin(5, DefaultVertexFormats.POSITION_COLOR);
      WorldRenderer.addChainedFilledBoxVertices(lvt_17_1_, p_217733_0_, p_217733_2_, p_217733_4_, p_217733_6_, p_217733_8_, p_217733_10_, p_217733_12_, p_217733_13_, p_217733_14_, p_217733_15_);
      lvt_16_1_.draw();
   }

   public static void func_217731_a(String p_217731_0_, int p_217731_1_, int p_217731_2_, int p_217731_3_, int p_217731_4_) {
      func_217732_a(p_217731_0_, (double)p_217731_1_ + 0.5D, (double)p_217731_2_ + 0.5D, (double)p_217731_3_ + 0.5D, p_217731_4_);
   }

   public static void func_217732_a(String p_217732_0_, double p_217732_1_, double p_217732_3_, double p_217732_5_, int p_217732_7_) {
      func_217729_a(p_217732_0_, p_217732_1_, p_217732_3_, p_217732_5_, p_217732_7_, 0.02F);
   }

   public static void func_217729_a(String p_217729_0_, double p_217729_1_, double p_217729_3_, double p_217729_5_, int p_217729_7_, float p_217729_8_) {
      func_217734_a(p_217729_0_, p_217729_1_, p_217729_3_, p_217729_5_, p_217729_7_, p_217729_8_, true, 0.0F, false);
   }

   public static void func_217734_a(String p_217734_0_, double p_217734_1_, double p_217734_3_, double p_217734_5_, int p_217734_7_, float p_217734_8_, boolean p_217734_9_, float p_217734_10_, boolean p_217734_11_) {
      Minecraft lvt_12_1_ = Minecraft.getInstance();
      ActiveRenderInfo lvt_13_1_ = lvt_12_1_.gameRenderer.getActiveRenderInfo();
      if (lvt_13_1_.isValid() && lvt_12_1_.getRenderManager().options != null) {
         FontRenderer lvt_14_1_ = lvt_12_1_.fontRenderer;
         double lvt_15_1_ = lvt_13_1_.getProjectedView().x;
         double lvt_17_1_ = lvt_13_1_.getProjectedView().y;
         double lvt_19_1_ = lvt_13_1_.getProjectedView().z;
         RenderSystem.pushMatrix();
         RenderSystem.translatef((float)(p_217734_1_ - lvt_15_1_), (float)(p_217734_3_ - lvt_17_1_) + 0.07F, (float)(p_217734_5_ - lvt_19_1_));
         RenderSystem.normal3f(0.0F, 1.0F, 0.0F);
         RenderSystem.multMatrix(new Matrix4f(lvt_13_1_.func_227995_f_()));
         RenderSystem.scalef(p_217734_8_, -p_217734_8_, p_217734_8_);
         RenderSystem.enableTexture();
         if (p_217734_11_) {
            RenderSystem.disableDepthTest();
         } else {
            RenderSystem.enableDepthTest();
         }

         RenderSystem.depthMask(true);
         RenderSystem.scalef(-1.0F, 1.0F, 1.0F);
         float lvt_21_1_ = p_217734_9_ ? (float)(-lvt_14_1_.getStringWidth(p_217734_0_)) / 2.0F : 0.0F;
         lvt_21_1_ -= p_217734_10_ / p_217734_8_;
         RenderSystem.enableAlphaTest();
         IRenderTypeBuffer.Impl lvt_22_1_ = IRenderTypeBuffer.func_228455_a_(Tessellator.getInstance().getBuffer());
         lvt_14_1_.func_228079_a_(p_217734_0_, lvt_21_1_, 0.0F, p_217734_7_, false, TransformationMatrix.func_227983_a_().func_227988_c_(), lvt_22_1_, p_217734_11_, 0, 15728880);
         lvt_22_1_.func_228461_a_();
         RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
         RenderSystem.enableDepthTest();
         RenderSystem.popMatrix();
      }
   }

   @OnlyIn(Dist.CLIENT)
   public interface IDebugRenderer {
      void func_225619_a_(MatrixStack var1, IRenderTypeBuffer var2, double var3, double var5, double var7);

      default void func_217675_a() {
      }
   }
}
