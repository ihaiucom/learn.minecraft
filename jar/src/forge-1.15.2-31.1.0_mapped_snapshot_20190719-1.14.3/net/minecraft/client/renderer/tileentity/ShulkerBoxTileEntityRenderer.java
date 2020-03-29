package net.minecraft.client.renderer.tileentity;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.client.renderer.Atlases;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.entity.model.ShulkerModel;
import net.minecraft.client.renderer.model.Material;
import net.minecraft.item.DyeColor;
import net.minecraft.tileentity.ShulkerBoxTileEntity;
import net.minecraft.util.Direction;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ShulkerBoxTileEntityRenderer extends TileEntityRenderer<ShulkerBoxTileEntity> {
   private final ShulkerModel<?> model;

   public ShulkerBoxTileEntityRenderer(ShulkerModel<?> p_i226013_1_, TileEntityRendererDispatcher p_i226013_2_) {
      super(p_i226013_2_);
      this.model = p_i226013_1_;
   }

   public void func_225616_a_(ShulkerBoxTileEntity p_225616_1_, float p_225616_2_, MatrixStack p_225616_3_, IRenderTypeBuffer p_225616_4_, int p_225616_5_, int p_225616_6_) {
      Direction lvt_7_1_ = Direction.UP;
      if (p_225616_1_.hasWorld()) {
         BlockState lvt_8_1_ = p_225616_1_.getWorld().getBlockState(p_225616_1_.getPos());
         if (lvt_8_1_.getBlock() instanceof ShulkerBoxBlock) {
            lvt_7_1_ = (Direction)lvt_8_1_.get(ShulkerBoxBlock.FACING);
         }
      }

      DyeColor lvt_9_1_ = p_225616_1_.getColor();
      Material lvt_8_3_;
      if (lvt_9_1_ == null) {
         lvt_8_3_ = Atlases.field_228748_g_;
      } else {
         lvt_8_3_ = (Material)Atlases.field_228749_h_.get(lvt_9_1_.getId());
      }

      p_225616_3_.func_227860_a_();
      p_225616_3_.func_227861_a_(0.5D, 0.5D, 0.5D);
      float lvt_10_1_ = 0.9995F;
      p_225616_3_.func_227862_a_(0.9995F, 0.9995F, 0.9995F);
      p_225616_3_.func_227863_a_(lvt_7_1_.func_229384_a_());
      p_225616_3_.func_227862_a_(1.0F, -1.0F, -1.0F);
      p_225616_3_.func_227861_a_(0.0D, -1.0D, 0.0D);
      IVertexBuilder lvt_11_1_ = lvt_8_3_.func_229311_a_(p_225616_4_, RenderType::func_228640_c_);
      this.model.getBase().func_228308_a_(p_225616_3_, lvt_11_1_, p_225616_5_, p_225616_6_);
      p_225616_3_.func_227861_a_(0.0D, (double)(-p_225616_1_.getProgress(p_225616_2_) * 0.5F), 0.0D);
      p_225616_3_.func_227863_a_(Vector3f.field_229181_d_.func_229187_a_(270.0F * p_225616_1_.getProgress(p_225616_2_)));
      this.model.getLid().func_228308_a_(p_225616_3_, lvt_11_1_, p_225616_5_, p_225616_6_);
      p_225616_3_.func_227865_b_();
   }
}
