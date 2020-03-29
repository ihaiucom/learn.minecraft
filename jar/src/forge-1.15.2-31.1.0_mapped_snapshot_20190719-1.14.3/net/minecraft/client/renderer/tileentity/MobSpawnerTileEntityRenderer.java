package net.minecraft.client.renderer.tileentity;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.MobSpawnerTileEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.spawner.AbstractSpawner;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class MobSpawnerTileEntityRenderer extends TileEntityRenderer<MobSpawnerTileEntity> {
   public MobSpawnerTileEntityRenderer(TileEntityRendererDispatcher p_i226016_1_) {
      super(p_i226016_1_);
   }

   public void func_225616_a_(MobSpawnerTileEntity p_225616_1_, float p_225616_2_, MatrixStack p_225616_3_, IRenderTypeBuffer p_225616_4_, int p_225616_5_, int p_225616_6_) {
      p_225616_3_.func_227860_a_();
      p_225616_3_.func_227861_a_(0.5D, 0.0D, 0.5D);
      AbstractSpawner lvt_7_1_ = p_225616_1_.getSpawnerBaseLogic();
      Entity lvt_8_1_ = lvt_7_1_.getCachedEntity();
      if (lvt_8_1_ != null) {
         float lvt_9_1_ = 0.53125F;
         float lvt_10_1_ = Math.max(lvt_8_1_.getWidth(), lvt_8_1_.getHeight());
         if ((double)lvt_10_1_ > 1.0D) {
            lvt_9_1_ /= lvt_10_1_;
         }

         p_225616_3_.func_227861_a_(0.0D, 0.4000000059604645D, 0.0D);
         p_225616_3_.func_227863_a_(Vector3f.field_229181_d_.func_229187_a_((float)MathHelper.lerp((double)p_225616_2_, lvt_7_1_.getPrevMobRotation(), lvt_7_1_.getMobRotation()) * 10.0F));
         p_225616_3_.func_227861_a_(0.0D, -0.20000000298023224D, 0.0D);
         p_225616_3_.func_227863_a_(Vector3f.field_229179_b_.func_229187_a_(-30.0F));
         p_225616_3_.func_227862_a_(lvt_9_1_, lvt_9_1_, lvt_9_1_);
         Minecraft.getInstance().getRenderManager().func_229084_a_(lvt_8_1_, 0.0D, 0.0D, 0.0D, 0.0F, p_225616_2_, p_225616_3_, p_225616_4_, p_225616_5_);
      }

      p_225616_3_.func_227865_b_();
   }
}
