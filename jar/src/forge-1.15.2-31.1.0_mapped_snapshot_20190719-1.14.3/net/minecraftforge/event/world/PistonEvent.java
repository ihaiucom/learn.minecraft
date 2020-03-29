package net.minecraftforge.event.world;

import javax.annotation.Nullable;
import net.minecraft.block.PistonBlockStructureHelper;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.eventbus.api.Cancelable;

public abstract class PistonEvent extends BlockEvent {
   private final Direction direction;
   private final PistonEvent.PistonMoveType moveType;

   public PistonEvent(World world, BlockPos pos, Direction direction, PistonEvent.PistonMoveType moveType) {
      super(world, pos, world.getBlockState(pos));
      this.direction = direction;
      this.moveType = moveType;
   }

   public Direction getDirection() {
      return this.direction;
   }

   public BlockPos getFaceOffsetPos() {
      return this.getPos().offset(this.direction);
   }

   public PistonEvent.PistonMoveType getPistonMoveType() {
      return this.moveType;
   }

   @Nullable
   public PistonBlockStructureHelper getStructureHelper() {
      return this.getWorld() instanceof World ? new PistonBlockStructureHelper((World)this.getWorld(), this.getPos(), this.getDirection(), this.getPistonMoveType().isExtend) : null;
   }

   public static enum PistonMoveType {
      EXTEND(true),
      RETRACT(false);

      public final boolean isExtend;

      private PistonMoveType(boolean isExtend) {
         this.isExtend = isExtend;
      }
   }

   @Cancelable
   public static class Pre extends PistonEvent {
      public Pre(World world, BlockPos pos, Direction direction, PistonEvent.PistonMoveType moveType) {
         super(world, pos, direction, moveType);
      }
   }

   public static class Post extends PistonEvent {
      public Post(World world, BlockPos pos, Direction direction, PistonEvent.PistonMoveType moveType) {
         super(world, pos, direction, moveType);
      }
   }
}
