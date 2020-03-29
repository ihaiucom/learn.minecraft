package net.minecraft.client.renderer.tileentity;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.entity.model.BookModel;
import net.minecraft.client.renderer.model.Material;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.tileentity.EnchantingTableTileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class EnchantmentTableTileEntityRenderer extends TileEntityRenderer<EnchantingTableTileEntity> {
   public static final Material TEXTURE_BOOK;
   private final BookModel modelBook = new BookModel();

   public EnchantmentTableTileEntityRenderer(TileEntityRendererDispatcher p_i226010_1_) {
      super(p_i226010_1_);
   }

   public void func_225616_a_(EnchantingTableTileEntity p_225616_1_, float p_225616_2_, MatrixStack p_225616_3_, IRenderTypeBuffer p_225616_4_, int p_225616_5_, int p_225616_6_) {
      p_225616_3_.func_227860_a_();
      p_225616_3_.func_227861_a_(0.5D, 0.75D, 0.5D);
      float lvt_7_1_ = (float)p_225616_1_.field_195522_a + p_225616_2_;
      p_225616_3_.func_227861_a_(0.0D, (double)(0.1F + MathHelper.sin(lvt_7_1_ * 0.1F) * 0.01F), 0.0D);

      float lvt_8_1_;
      for(lvt_8_1_ = p_225616_1_.field_195529_l - p_225616_1_.field_195530_m; lvt_8_1_ >= 3.1415927F; lvt_8_1_ -= 6.2831855F) {
      }

      while(lvt_8_1_ < -3.1415927F) {
         lvt_8_1_ += 6.2831855F;
      }

      float lvt_9_1_ = p_225616_1_.field_195530_m + lvt_8_1_ * p_225616_2_;
      p_225616_3_.func_227863_a_(Vector3f.field_229181_d_.func_229193_c_(-lvt_9_1_));
      p_225616_3_.func_227863_a_(Vector3f.field_229183_f_.func_229187_a_(80.0F));
      float lvt_10_1_ = MathHelper.lerp(p_225616_2_, p_225616_1_.field_195524_g, p_225616_1_.field_195523_f);
      float lvt_11_1_ = MathHelper.func_226164_h_(lvt_10_1_ + 0.25F) * 1.6F - 0.3F;
      float lvt_12_1_ = MathHelper.func_226164_h_(lvt_10_1_ + 0.75F) * 1.6F - 0.3F;
      float lvt_13_1_ = MathHelper.lerp(p_225616_2_, p_225616_1_.field_195528_k, p_225616_1_.field_195527_j);
      this.modelBook.func_228247_a_(lvt_7_1_, MathHelper.clamp(lvt_11_1_, 0.0F, 1.0F), MathHelper.clamp(lvt_12_1_, 0.0F, 1.0F), lvt_13_1_);
      IVertexBuilder lvt_14_1_ = TEXTURE_BOOK.func_229311_a_(p_225616_4_, RenderType::func_228634_a_);
      this.modelBook.func_228249_b_(p_225616_3_, lvt_14_1_, p_225616_5_, p_225616_6_, 1.0F, 1.0F, 1.0F, 1.0F);
      p_225616_3_.func_227865_b_();
   }

   static {
      TEXTURE_BOOK = new Material(AtlasTexture.LOCATION_BLOCKS_TEXTURE, new ResourceLocation("entity/enchanting_table_book"));
   }
}
