package net.minecraftforge.event.entity.player;

import javax.annotation.Nonnull;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event.HasResult;

@Cancelable
@HasResult
public class BonemealEvent extends PlayerEvent {
   private final World world;
   private final BlockPos pos;
   private final BlockState block;
   private final ItemStack stack;

   public BonemealEvent(@Nonnull PlayerEntity player, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull BlockState block, @Nonnull ItemStack stack) {
      super(player);
      this.world = world;
      this.pos = pos;
      this.block = block;
      this.stack = stack;
   }

   public World getWorld() {
      return this.world;
   }

   public BlockPos getPos() {
      return this.pos;
   }

   public BlockState getBlock() {
      return this.block;
   }

   @Nonnull
   public ItemStack getStack() {
      return this.stack;
   }
}
