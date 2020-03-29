package net.minecraftforge.event.entity.player;

import com.google.common.base.Preconditions;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event.Result;
import net.minecraftforge.fml.LogicalSide;

public class PlayerInteractEvent extends PlayerEvent {
   private final Hand hand;
   private final BlockPos pos;
   @Nullable
   private final Direction face;
   private ActionResultType cancellationResult;

   private PlayerInteractEvent(PlayerEntity player, Hand hand, BlockPos pos, @Nullable Direction face) {
      super((PlayerEntity)Preconditions.checkNotNull(player, "Null player in PlayerInteractEvent!"));
      this.cancellationResult = ActionResultType.PASS;
      this.hand = (Hand)Preconditions.checkNotNull(hand, "Null hand in PlayerInteractEvent!");
      this.pos = (BlockPos)Preconditions.checkNotNull(pos, "Null position in PlayerInteractEvent!");
      this.face = face;
   }

   @Nonnull
   public Hand getHand() {
      return this.hand;
   }

   @Nonnull
   public ItemStack getItemStack() {
      return this.getPlayer().getHeldItem(this.hand);
   }

   @Nonnull
   public BlockPos getPos() {
      return this.pos;
   }

   @Nullable
   public Direction getFace() {
      return this.face;
   }

   public World getWorld() {
      return this.getPlayer().getEntityWorld();
   }

   public LogicalSide getSide() {
      return this.getWorld().isRemote ? LogicalSide.CLIENT : LogicalSide.SERVER;
   }

   public ActionResultType getCancellationResult() {
      return this.cancellationResult;
   }

   public void setCancellationResult(ActionResultType result) {
      this.cancellationResult = result;
   }

   // $FF: synthetic method
   PlayerInteractEvent(PlayerEntity x0, Hand x1, BlockPos x2, Direction x3, Object x4) {
      this(x0, x1, x2, x3);
   }

   public static class LeftClickEmpty extends PlayerInteractEvent {
      public LeftClickEmpty(PlayerEntity player) {
         super(player, Hand.MAIN_HAND, new BlockPos(player), (Direction)null, null);
      }
   }

   @Cancelable
   public static class LeftClickBlock extends PlayerInteractEvent {
      private Result useBlock;
      private Result useItem;

      public LeftClickBlock(PlayerEntity player, BlockPos pos, Direction face) {
         super(player, Hand.MAIN_HAND, pos, face, null);
         this.useBlock = Result.DEFAULT;
         this.useItem = Result.DEFAULT;
      }

      public Result getUseBlock() {
         return this.useBlock;
      }

      public Result getUseItem() {
         return this.useItem;
      }

      public void setUseBlock(Result triggerBlock) {
         this.useBlock = triggerBlock;
      }

      public void setUseItem(Result triggerItem) {
         this.useItem = triggerItem;
      }

      public void setCanceled(boolean canceled) {
         super.setCanceled(canceled);
         if (canceled) {
            this.useBlock = Result.DENY;
            this.useItem = Result.DENY;
         }

      }
   }

   public static class RightClickEmpty extends PlayerInteractEvent {
      public RightClickEmpty(PlayerEntity player, Hand hand) {
         super(player, hand, new BlockPos(player), (Direction)null, null);
      }
   }

   @Cancelable
   public static class RightClickItem extends PlayerInteractEvent {
      public RightClickItem(PlayerEntity player, Hand hand) {
         super(player, hand, new BlockPos(player), (Direction)null, null);
      }
   }

   @Cancelable
   public static class RightClickBlock extends PlayerInteractEvent {
      private Result useBlock;
      private Result useItem;

      public RightClickBlock(PlayerEntity player, Hand hand, BlockPos pos, Direction face) {
         super(player, hand, pos, face, null);
         this.useBlock = Result.DEFAULT;
         this.useItem = Result.DEFAULT;
      }

      public Result getUseBlock() {
         return this.useBlock;
      }

      public Result getUseItem() {
         return this.useItem;
      }

      public void setUseBlock(Result triggerBlock) {
         this.useBlock = triggerBlock;
      }

      public void setUseItem(Result triggerItem) {
         this.useItem = triggerItem;
      }

      public void setCanceled(boolean canceled) {
         super.setCanceled(canceled);
         if (canceled) {
            this.useBlock = Result.DENY;
            this.useItem = Result.DENY;
         }

      }
   }

   @Cancelable
   public static class EntityInteract extends PlayerInteractEvent {
      private final Entity target;

      public EntityInteract(PlayerEntity player, Hand hand, Entity target) {
         super(player, hand, new BlockPos(target), (Direction)null, null);
         this.target = target;
      }

      public Entity getTarget() {
         return this.target;
      }
   }

   @Cancelable
   public static class EntityInteractSpecific extends PlayerInteractEvent {
      private final Vec3d localPos;
      private final Entity target;

      public EntityInteractSpecific(PlayerEntity player, Hand hand, Entity target, Vec3d localPos) {
         super(player, hand, new BlockPos(target), (Direction)null, null);
         this.localPos = localPos;
         this.target = target;
      }

      public Vec3d getLocalPos() {
         return this.localPos;
      }

      public Entity getTarget() {
         return this.target;
      }
   }
}
