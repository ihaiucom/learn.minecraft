package net.minecraft.world.gen.surfacebuilders;

import com.mojang.datafixers.Dynamic;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;

public class SurfaceBuilderConfig implements ISurfaceBuilderConfig {
   private final BlockState topMaterial;
   private final BlockState underMaterial;
   private final BlockState underWaterMaterial;

   public SurfaceBuilderConfig(BlockState p_i48954_1_, BlockState p_i48954_2_, BlockState p_i48954_3_) {
      this.topMaterial = p_i48954_1_;
      this.underMaterial = p_i48954_2_;
      this.underWaterMaterial = p_i48954_3_;
   }

   public BlockState getTop() {
      return this.topMaterial;
   }

   public BlockState getUnder() {
      return this.underMaterial;
   }

   public BlockState getUnderWaterMaterial() {
      return this.underWaterMaterial;
   }

   public static SurfaceBuilderConfig deserialize(Dynamic<?> p_215455_0_) {
      BlockState lvt_1_1_ = (BlockState)p_215455_0_.get("top_material").map(BlockState::deserialize).orElse(Blocks.AIR.getDefaultState());
      BlockState lvt_2_1_ = (BlockState)p_215455_0_.get("under_material").map(BlockState::deserialize).orElse(Blocks.AIR.getDefaultState());
      BlockState lvt_3_1_ = (BlockState)p_215455_0_.get("underwater_material").map(BlockState::deserialize).orElse(Blocks.AIR.getDefaultState());
      return new SurfaceBuilderConfig(lvt_1_1_, lvt_2_1_, lvt_3_1_);
   }
}
