package net.minecraftforge.event.entity.player;

import javax.annotation.Nonnull;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.eventbus.api.Cancelable;

@Cancelable
public class ArrowLooseEvent extends PlayerEvent {
   private final ItemStack bow;
   private final World world;
   private final boolean hasAmmo;
   private int charge;

   public ArrowLooseEvent(PlayerEntity player, @Nonnull ItemStack bow, World world, int charge, boolean hasAmmo) {
      super(player);
      this.bow = bow;
      this.world = world;
      this.charge = charge;
      this.hasAmmo = hasAmmo;
   }

   @Nonnull
   public ItemStack getBow() {
      return this.bow;
   }

   public World getWorld() {
      return this.world;
   }

   public boolean hasAmmo() {
      return this.hasAmmo;
   }

   public int getCharge() {
      return this.charge;
   }

   public void setCharge(int charge) {
      this.charge = charge;
   }
}
