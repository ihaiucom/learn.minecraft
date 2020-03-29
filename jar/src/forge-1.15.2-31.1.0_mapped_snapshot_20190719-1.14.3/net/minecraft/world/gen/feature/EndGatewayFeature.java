package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Iterator;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.block.Blocks;
import net.minecraft.tileentity.EndGatewayTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationSettings;

public class EndGatewayFeature extends Feature<EndGatewayConfig> {
   public EndGatewayFeature(Function<Dynamic<?>, ? extends EndGatewayConfig> p_i49881_1_) {
      super(p_i49881_1_);
   }

   public boolean place(IWorld p_212245_1_, ChunkGenerator<? extends GenerationSettings> p_212245_2_, Random p_212245_3_, BlockPos p_212245_4_, EndGatewayConfig p_212245_5_) {
      Iterator var6 = BlockPos.getAllInBoxMutable(p_212245_4_.add(-1, -2, -1), p_212245_4_.add(1, 2, 1)).iterator();

      while(true) {
         while(var6.hasNext()) {
            BlockPos lvt_7_1_ = (BlockPos)var6.next();
            boolean lvt_8_1_ = lvt_7_1_.getX() == p_212245_4_.getX();
            boolean lvt_9_1_ = lvt_7_1_.getY() == p_212245_4_.getY();
            boolean lvt_10_1_ = lvt_7_1_.getZ() == p_212245_4_.getZ();
            boolean lvt_11_1_ = Math.abs(lvt_7_1_.getY() - p_212245_4_.getY()) == 2;
            if (lvt_8_1_ && lvt_9_1_ && lvt_10_1_) {
               BlockPos lvt_12_1_ = lvt_7_1_.toImmutable();
               this.setBlockState(p_212245_1_, lvt_12_1_, Blocks.END_GATEWAY.getDefaultState());
               p_212245_5_.func_214700_b().ifPresent((p_214624_3_) -> {
                  TileEntity lvt_4_1_ = p_212245_1_.getTileEntity(lvt_12_1_);
                  if (lvt_4_1_ instanceof EndGatewayTileEntity) {
                     EndGatewayTileEntity lvt_5_1_ = (EndGatewayTileEntity)lvt_4_1_;
                     lvt_5_1_.setExitPortal(p_214624_3_, p_212245_5_.func_214701_c());
                     lvt_4_1_.markDirty();
                  }

               });
            } else if (lvt_9_1_) {
               this.setBlockState(p_212245_1_, lvt_7_1_, Blocks.AIR.getDefaultState());
            } else if (lvt_11_1_ && lvt_8_1_ && lvt_10_1_) {
               this.setBlockState(p_212245_1_, lvt_7_1_, Blocks.BEDROCK.getDefaultState());
            } else if ((lvt_8_1_ || lvt_10_1_) && !lvt_11_1_) {
               this.setBlockState(p_212245_1_, lvt_7_1_, Blocks.BEDROCK.getDefaultState());
            } else {
               this.setBlockState(p_212245_1_, lvt_7_1_, Blocks.AIR.getDefaultState());
            }
         }

         return true;
      }
   }
}
