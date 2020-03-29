package net.minecraftforge.event.entity.living;

import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.eventbus.api.Cancelable;

@Cancelable
public class LivingDestroyBlockEvent extends LivingEvent {
   private final BlockPos pos;
   private final BlockState state;

   public LivingDestroyBlockEvent(LivingEntity entity, BlockPos pos, BlockState state) {
      super(entity);
      this.pos = pos;
      this.state = state;
   }

   public BlockState getState() {
      return this.state;
   }

   public BlockPos getPos() {
      return this.pos;
   }
}
