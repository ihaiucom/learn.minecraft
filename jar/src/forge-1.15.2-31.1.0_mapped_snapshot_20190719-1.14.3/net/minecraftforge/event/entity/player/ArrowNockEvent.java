package net.minecraftforge.event.entity.player;

import javax.annotation.Nonnull;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class ArrowNockEvent extends PlayerEvent {
   private final ItemStack bow;
   private final Hand hand;
   private final World world;
   private final boolean hasAmmo;
   private ActionResult<ItemStack> action;

   public ArrowNockEvent(PlayerEntity player, @Nonnull ItemStack item, Hand hand, World world, boolean hasAmmo) {
      super(player);
      this.bow = item;
      this.hand = hand;
      this.world = world;
      this.hasAmmo = hasAmmo;
   }

   @Nonnull
   public ItemStack getBow() {
      return this.bow;
   }

   public World getWorld() {
      return this.world;
   }

   public Hand getHand() {
      return this.hand;
   }

   public boolean hasAmmo() {
      return this.hasAmmo;
   }

   public ActionResult<ItemStack> getAction() {
      return this.action;
   }

   public void setAction(ActionResult<ItemStack> action) {
      this.action = action;
   }
}
