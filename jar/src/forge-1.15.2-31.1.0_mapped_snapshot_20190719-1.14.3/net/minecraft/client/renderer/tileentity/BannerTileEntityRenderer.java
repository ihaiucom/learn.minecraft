package net.minecraft.client.renderer.tileentity;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.mojang.datafixers.util.Pair;
import java.util.List;
import net.minecraft.block.BannerBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.WallBannerBlock;
import net.minecraft.client.renderer.Atlases;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.model.Material;
import net.minecraft.client.renderer.model.ModelBakery;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.item.DyeColor;
import net.minecraft.tileentity.BannerPattern;
import net.minecraft.tileentity.BannerTileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BannerTileEntityRenderer extends TileEntityRenderer<BannerTileEntity> {
   private final ModelRenderer field_228833_a_ = func_228836_a_();
   private final ModelRenderer field_228834_c_ = new ModelRenderer(64, 64, 44, 0);
   private final ModelRenderer field_228835_d_;

   public BannerTileEntityRenderer(TileEntityRendererDispatcher p_i226002_1_) {
      super(p_i226002_1_);
      this.field_228834_c_.func_228301_a_(-1.0F, -30.0F, -1.0F, 2.0F, 42.0F, 2.0F, 0.0F);
      this.field_228835_d_ = new ModelRenderer(64, 64, 0, 42);
      this.field_228835_d_.func_228301_a_(-10.0F, -32.0F, -1.0F, 20.0F, 2.0F, 2.0F, 0.0F);
   }

   public static ModelRenderer func_228836_a_() {
      ModelRenderer lvt_0_1_ = new ModelRenderer(64, 64, 0, 0);
      lvt_0_1_.func_228301_a_(-10.0F, 0.0F, -2.0F, 20.0F, 40.0F, 1.0F, 0.0F);
      return lvt_0_1_;
   }

   public void func_225616_a_(BannerTileEntity p_225616_1_, float p_225616_2_, MatrixStack p_225616_3_, IRenderTypeBuffer p_225616_4_, int p_225616_5_, int p_225616_6_) {
      List<Pair<BannerPattern, DyeColor>> lvt_7_1_ = p_225616_1_.getPatternList();
      if (lvt_7_1_ != null) {
         float lvt_8_1_ = 0.6666667F;
         boolean lvt_9_1_ = p_225616_1_.getWorld() == null;
         p_225616_3_.func_227860_a_();
         long lvt_10_2_;
         if (lvt_9_1_) {
            lvt_10_2_ = 0L;
            p_225616_3_.func_227861_a_(0.5D, 0.5D, 0.5D);
            this.field_228834_c_.showModel = true;
         } else {
            lvt_10_2_ = p_225616_1_.getWorld().getGameTime();
            BlockState lvt_12_1_ = p_225616_1_.getBlockState();
            float lvt_13_1_;
            if (lvt_12_1_.getBlock() instanceof BannerBlock) {
               p_225616_3_.func_227861_a_(0.5D, 0.5D, 0.5D);
               lvt_13_1_ = (float)(-(Integer)lvt_12_1_.get(BannerBlock.ROTATION) * 360) / 16.0F;
               p_225616_3_.func_227863_a_(Vector3f.field_229181_d_.func_229187_a_(lvt_13_1_));
               this.field_228834_c_.showModel = true;
            } else {
               p_225616_3_.func_227861_a_(0.5D, -0.1666666716337204D, 0.5D);
               lvt_13_1_ = -((Direction)lvt_12_1_.get(WallBannerBlock.HORIZONTAL_FACING)).getHorizontalAngle();
               p_225616_3_.func_227863_a_(Vector3f.field_229181_d_.func_229187_a_(lvt_13_1_));
               p_225616_3_.func_227861_a_(0.0D, -0.3125D, -0.4375D);
               this.field_228834_c_.showModel = false;
            }
         }

         p_225616_3_.func_227860_a_();
         p_225616_3_.func_227862_a_(0.6666667F, -0.6666667F, -0.6666667F);
         IVertexBuilder lvt_12_2_ = ModelBakery.field_229315_f_.func_229311_a_(p_225616_4_, RenderType::func_228634_a_);
         this.field_228834_c_.func_228308_a_(p_225616_3_, lvt_12_2_, p_225616_5_, p_225616_6_);
         this.field_228835_d_.func_228308_a_(p_225616_3_, lvt_12_2_, p_225616_5_, p_225616_6_);
         BlockPos lvt_13_3_ = p_225616_1_.getPos();
         float lvt_14_1_ = ((float)Math.floorMod((long)(lvt_13_3_.getX() * 7 + lvt_13_3_.getY() * 9 + lvt_13_3_.getZ() * 13) + lvt_10_2_, 100L) + p_225616_2_) / 100.0F;
         this.field_228833_a_.rotateAngleX = (-0.0125F + 0.01F * MathHelper.cos(6.2831855F * lvt_14_1_)) * 3.1415927F;
         this.field_228833_a_.rotationPointY = -32.0F;
         func_230180_a_(p_225616_3_, p_225616_4_, p_225616_5_, p_225616_6_, this.field_228833_a_, ModelBakery.field_229315_f_, true, lvt_7_1_);
         p_225616_3_.func_227865_b_();
         p_225616_3_.func_227865_b_();
      }
   }

   public static void func_230180_a_(MatrixStack p_230180_0_, IRenderTypeBuffer p_230180_1_, int p_230180_2_, int p_230180_3_, ModelRenderer p_230180_4_, Material p_230180_5_, boolean p_230180_6_, List<Pair<BannerPattern, DyeColor>> p_230180_7_) {
      p_230180_4_.func_228308_a_(p_230180_0_, p_230180_5_.func_229311_a_(p_230180_1_, RenderType::func_228634_a_), p_230180_2_, p_230180_3_);

      for(int lvt_8_1_ = 0; lvt_8_1_ < 17 && lvt_8_1_ < p_230180_7_.size(); ++lvt_8_1_) {
         Pair<BannerPattern, DyeColor> lvt_9_1_ = (Pair)p_230180_7_.get(lvt_8_1_);
         float[] lvt_10_1_ = ((DyeColor)lvt_9_1_.getSecond()).getColorComponentValues();
         Material lvt_11_1_ = new Material(p_230180_6_ ? Atlases.field_228744_c_ : Atlases.field_228745_d_, ((BannerPattern)lvt_9_1_.getFirst()).func_226957_a_(p_230180_6_));
         p_230180_4_.func_228309_a_(p_230180_0_, lvt_11_1_.func_229311_a_(p_230180_1_, RenderType::func_228650_h_), p_230180_2_, p_230180_3_, lvt_10_1_[0], lvt_10_1_[1], lvt_10_1_[2], 1.0F);
      }

   }
}
