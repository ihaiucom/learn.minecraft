package net.minecraftforge.client.event;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

@Cancelable
public class RenderBlockOverlayEvent extends Event {
   private final PlayerEntity player;
   private final MatrixStack mat;
   private final RenderBlockOverlayEvent.OverlayType overlayType;
   private final BlockState blockForOverlay;
   private final BlockPos blockPos;

   public RenderBlockOverlayEvent(PlayerEntity player, MatrixStack mat, RenderBlockOverlayEvent.OverlayType type, BlockState block, BlockPos blockPos) {
      this.player = player;
      this.mat = mat;
      this.overlayType = type;
      this.blockForOverlay = block;
      this.blockPos = blockPos;
   }

   public PlayerEntity getPlayer() {
      return this.player;
   }

   public MatrixStack getMatrixStack() {
      return this.mat;
   }

   public RenderBlockOverlayEvent.OverlayType getOverlayType() {
      return this.overlayType;
   }

   public BlockState getBlockForOverlay() {
      return this.blockForOverlay;
   }

   public BlockPos getBlockPos() {
      return this.blockPos;
   }

   public static enum OverlayType {
      FIRE,
      BLOCK,
      WATER;
   }
}
