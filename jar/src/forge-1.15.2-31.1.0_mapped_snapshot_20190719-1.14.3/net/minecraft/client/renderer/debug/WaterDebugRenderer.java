package net.minecraft.client.renderer.debug;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Iterator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.fluid.IFluidState;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class WaterDebugRenderer implements DebugRenderer.IDebugRenderer {
   private final Minecraft minecraft;

   public WaterDebugRenderer(Minecraft p_i46555_1_) {
      this.minecraft = p_i46555_1_;
   }

   public void func_225619_a_(MatrixStack p_225619_1_, IRenderTypeBuffer p_225619_2_, double p_225619_3_, double p_225619_5_, double p_225619_7_) {
      BlockPos lvt_9_1_ = this.minecraft.player.getPosition();
      IWorldReader lvt_10_1_ = this.minecraft.player.world;
      RenderSystem.enableBlend();
      RenderSystem.defaultBlendFunc();
      RenderSystem.color4f(0.0F, 1.0F, 0.0F, 0.75F);
      RenderSystem.disableTexture();
      RenderSystem.lineWidth(6.0F);
      Iterator var11 = BlockPos.getAllInBoxMutable(lvt_9_1_.add(-10, -10, -10), lvt_9_1_.add(10, 10, 10)).iterator();

      BlockPos lvt_12_2_;
      IFluidState lvt_13_2_;
      while(var11.hasNext()) {
         lvt_12_2_ = (BlockPos)var11.next();
         lvt_13_2_ = lvt_10_1_.getFluidState(lvt_12_2_);
         if (lvt_13_2_.isTagged(FluidTags.WATER)) {
            double lvt_14_1_ = (double)((float)lvt_12_2_.getY() + lvt_13_2_.func_215679_a(lvt_10_1_, lvt_12_2_));
            DebugRenderer.func_217730_a((new AxisAlignedBB((double)((float)lvt_12_2_.getX() + 0.01F), (double)((float)lvt_12_2_.getY() + 0.01F), (double)((float)lvt_12_2_.getZ() + 0.01F), (double)((float)lvt_12_2_.getX() + 0.99F), lvt_14_1_, (double)((float)lvt_12_2_.getZ() + 0.99F))).offset(-p_225619_3_, -p_225619_5_, -p_225619_7_), 1.0F, 1.0F, 1.0F, 0.2F);
         }
      }

      var11 = BlockPos.getAllInBoxMutable(lvt_9_1_.add(-10, -10, -10), lvt_9_1_.add(10, 10, 10)).iterator();

      while(var11.hasNext()) {
         lvt_12_2_ = (BlockPos)var11.next();
         lvt_13_2_ = lvt_10_1_.getFluidState(lvt_12_2_);
         if (lvt_13_2_.isTagged(FluidTags.WATER)) {
            DebugRenderer.func_217732_a(String.valueOf(lvt_13_2_.getLevel()), (double)lvt_12_2_.getX() + 0.5D, (double)((float)lvt_12_2_.getY() + lvt_13_2_.func_215679_a(lvt_10_1_, lvt_12_2_)), (double)lvt_12_2_.getZ() + 0.5D, -16777216);
         }
      }

      RenderSystem.enableTexture();
      RenderSystem.disableBlend();
   }
}
