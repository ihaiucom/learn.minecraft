package net.minecraftforge.common.extensions;

import javax.annotation.Nonnull;
import net.minecraft.block.AbstractSignBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.EnderChestBlock;
import net.minecraft.block.SkullBlock;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.ModelDataManager;
import net.minecraftforge.client.model.data.EmptyModelData;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

public interface IForgeTileEntity extends ICapabilitySerializable<CompoundNBT> {
   AxisAlignedBB INFINITE_EXTENT_AABB = new AxisAlignedBB(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);

   default TileEntity getTileEntity() {
      return (TileEntity)this;
   }

   default void deserializeNBT(CompoundNBT nbt) {
      this.getTileEntity().read(nbt);
   }

   default CompoundNBT serializeNBT() {
      CompoundNBT ret = new CompoundNBT();
      this.getTileEntity().write(ret);
      return ret;
   }

   default void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
   }

   default void handleUpdateTag(CompoundNBT tag) {
      this.getTileEntity().read(tag);
   }

   CompoundNBT getTileData();

   default void onChunkUnloaded() {
   }

   default void onLoad() {
      this.requestModelDataUpdate();
   }

   @OnlyIn(Dist.CLIENT)
   default AxisAlignedBB getRenderBoundingBox() {
      AxisAlignedBB bb = INFINITE_EXTENT_AABB;
      BlockState state = this.getTileEntity().getBlockState();
      Block block = state.getBlock();
      BlockPos pos = this.getTileEntity().getPos();
      if (block == Blocks.ENCHANTING_TABLE) {
         bb = new AxisAlignedBB(pos, pos.add(1, 1, 1));
      } else if (block != Blocks.CHEST && block != Blocks.TRAPPED_CHEST) {
         if (block == Blocks.STRUCTURE_BLOCK) {
            bb = INFINITE_EXTENT_AABB;
         } else if (block != null && block != Blocks.BEACON) {
            AxisAlignedBB cbb = null;

            try {
               cbb = state.getCollisionShape(this.getTileEntity().getWorld(), pos).getBoundingBox().offset(pos);
            } catch (Exception var7) {
               cbb = new AxisAlignedBB(pos.add(-1, 0, -1), pos.add(1, 1, 1));
            }

            if (cbb != null) {
               bb = cbb;
            }
         }
      } else {
         bb = new AxisAlignedBB(pos.add(-1, 0, -1), pos.add(2, 2, 2));
      }

      return bb;
   }

   default boolean canRenderBreaking() {
      Block block = this.getTileEntity().getBlockState().getBlock();
      return block instanceof ChestBlock || block instanceof EnderChestBlock || block instanceof AbstractSignBlock || block instanceof SkullBlock;
   }

   default boolean hasFastRenderer() {
      return false;
   }

   default void requestModelDataUpdate() {
      TileEntity te = this.getTileEntity();
      World world = te.getWorld();
      if (world != null && world.isRemote) {
         ModelDataManager.requestModelDataRefresh(te);
      }

   }

   @Nonnull
   default IModelData getModelData() {
      return EmptyModelData.INSTANCE;
   }
}
