package net.minecraftforge.event.world;

import java.util.Random;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraftforge.eventbus.api.Event.HasResult;

@HasResult
public class SaplingGrowTreeEvent extends WorldEvent {
   private final BlockPos pos;
   private final Random rand;

   public SaplingGrowTreeEvent(IWorld world, Random rand, BlockPos pos) {
      super(world);
      this.rand = rand;
      this.pos = pos;
   }

   public BlockPos getPos() {
      return this.pos;
   }

   public Random getRand() {
      return this.rand;
   }
}
