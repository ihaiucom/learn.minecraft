package net.minecraftforge.server.permission.context;

import com.google.common.base.Preconditions;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;

public class BlockPosContext extends PlayerContext {
   private final BlockPos blockPos;
   private BlockState blockState;
   private Direction facing;

   public BlockPosContext(PlayerEntity ep, BlockPos pos, @Nullable BlockState state, @Nullable Direction f) {
      super(ep);
      this.blockPos = (BlockPos)Preconditions.checkNotNull(pos, "BlockPos can't be null in BlockPosContext!");
      this.blockState = state;
      this.facing = f;
   }

   public BlockPosContext(PlayerEntity ep, ChunkPos pos) {
      this(ep, new BlockPos(pos.getXStart() + 8, 0, pos.getZStart() + 8), (BlockState)null, (Direction)null);
   }

   @Nullable
   public <T> T get(ContextKey<T> key) {
      if (key.equals(ContextKeys.POS)) {
         return this.blockPos;
      } else if (key.equals(ContextKeys.BLOCK_STATE)) {
         if (this.blockState == null) {
            this.blockState = this.getWorld().getBlockState(this.blockPos);
         }

         return this.blockState;
      } else {
         return key.equals(ContextKeys.FACING) ? this.facing : super.get(key);
      }
   }

   protected boolean covers(ContextKey<?> key) {
      return key.equals(ContextKeys.POS) || key.equals(ContextKeys.BLOCK_STATE) || this.facing != null && key.equals(ContextKeys.FACING);
   }
}
