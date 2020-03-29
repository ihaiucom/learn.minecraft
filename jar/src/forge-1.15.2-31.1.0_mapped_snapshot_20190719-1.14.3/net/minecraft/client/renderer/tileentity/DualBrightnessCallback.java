package net.minecraft.client.renderer.tileentity;

import it.unimi.dsi.fastutil.ints.Int2IntFunction;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityMerger;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class DualBrightnessCallback<S extends TileEntity> implements TileEntityMerger.ICallback<S, Int2IntFunction> {
   public Int2IntFunction func_225539_a_(S p_225539_1_, S p_225539_2_) {
      return (p_228860_2_) -> {
         int lvt_3_1_ = WorldRenderer.func_228421_a_(p_225539_1_.getWorld(), p_225539_1_.getPos());
         int lvt_4_1_ = WorldRenderer.func_228421_a_(p_225539_2_.getWorld(), p_225539_2_.getPos());
         int lvt_5_1_ = LightTexture.func_228450_a_(lvt_3_1_);
         int lvt_6_1_ = LightTexture.func_228450_a_(lvt_4_1_);
         int lvt_7_1_ = LightTexture.func_228454_b_(lvt_3_1_);
         int lvt_8_1_ = LightTexture.func_228454_b_(lvt_4_1_);
         return LightTexture.func_228451_a_(Math.max(lvt_5_1_, lvt_6_1_), Math.max(lvt_7_1_, lvt_8_1_));
      };
   }

   public Int2IntFunction func_225538_a_(S p_225538_1_) {
      return (p_228861_0_) -> {
         return p_228861_0_;
      };
   }

   public Int2IntFunction func_225537_b_() {
      return (p_228859_0_) -> {
         return p_228859_0_;
      };
   }

   // $FF: synthetic method
   public Object func_225537_b_() {
      return this.func_225537_b_();
   }
}
