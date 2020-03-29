package net.minecraftforge.event.entity.player;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event.HasResult;

@Cancelable
@HasResult
public class FillBucketEvent extends PlayerEvent {
   private final ItemStack current;
   private final World world;
   @Nullable
   private final RayTraceResult target;
   private ItemStack result;

   public FillBucketEvent(PlayerEntity player, @Nonnull ItemStack current, World world, @Nullable RayTraceResult target) {
      super(player);
      this.current = current;
      this.world = world;
      this.target = target;
   }

   @Nonnull
   public ItemStack getEmptyBucket() {
      return this.current;
   }

   public World getWorld() {
      return this.world;
   }

   @Nullable
   public RayTraceResult getTarget() {
      return this.target;
   }

   @Nonnull
   public ItemStack getFilledBucket() {
      return this.result;
   }

   public void setFilledBucket(@Nonnull ItemStack bucket) {
      this.result = bucket;
   }
}
