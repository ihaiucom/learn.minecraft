package net.minecraftforge.common.util;

import java.lang.ref.WeakReference;
import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import net.minecraftforge.registries.ForgeRegistries;

public class BlockSnapshot {
   private static final boolean DEBUG = Boolean.parseBoolean(System.getProperty("forge.debugBlockSnapshot", "false"));
   private final BlockPos pos;
   private final int dimId;
   @Nullable
   private BlockState replacedBlock;
   private int flag;
   @Nullable
   private final CompoundNBT nbt;
   @Nullable
   private WeakReference<IWorld> world;
   private final ResourceLocation registryName;
   private final int meta;

   public BlockSnapshot(IWorld world, BlockPos pos, BlockState state) {
      this(world, pos, state, getTileNBT(world.getTileEntity(pos)));
   }

   public BlockSnapshot(IWorld world, BlockPos pos, BlockState state, @Nullable CompoundNBT nbt) {
      this.meta = 0;
      this.setWorld(world);
      this.dimId = world.getDimension().getType().getId();
      this.pos = pos.toImmutable();
      this.setReplacedBlock(state);
      this.registryName = state.getBlock().getRegistryName();
      this.setFlag(3);
      this.nbt = nbt;
      if (DEBUG) {
         System.out.printf("Created BlockSnapshot - [World: %s ][Location: %d,%d,%d ][Block: %s ][Meta: %d ]", world.getWorldInfo().getWorldName(), pos.getX(), pos.getY(), pos.getZ(), this.getRegistryName(), this.getMeta());
      }

   }

   public BlockSnapshot(IWorld world, BlockPos pos, BlockState state, int flag) {
      this(world, pos, state);
      this.setFlag(flag);
   }

   public BlockSnapshot(int dimension, BlockPos pos, ResourceLocation registryName, int meta, int flag, @Nullable CompoundNBT nbt) {
      this.meta = 0;
      this.dimId = dimension;
      this.pos = pos.toImmutable();
      this.setFlag(flag);
      this.registryName = registryName;
      this.nbt = nbt;
   }

   public static BlockSnapshot getBlockSnapshot(IWorld world, BlockPos pos) {
      return new BlockSnapshot(world, pos, world.getBlockState(pos));
   }

   public static BlockSnapshot getBlockSnapshot(IWorld world, BlockPos pos, int flag) {
      return new BlockSnapshot(world, pos, world.getBlockState(pos), flag);
   }

   public static BlockSnapshot readFromNBT(CompoundNBT tag) {
      return new BlockSnapshot(tag.getInt("dimension"), new BlockPos(tag.getInt("posX"), tag.getInt("posY"), tag.getInt("posZ")), new ResourceLocation(tag.getString("blockMod"), tag.getString("blockName")), tag.getInt("metadata"), tag.getInt("flag"), tag.getBoolean("hasTE") ? tag.getCompound("tileEntity") : null);
   }

   @Nullable
   private static CompoundNBT getTileNBT(@Nullable TileEntity te) {
      if (te == null) {
         return null;
      } else {
         CompoundNBT nbt = new CompoundNBT();
         te.write(nbt);
         return nbt;
      }
   }

   public BlockState getCurrentBlock() {
      return this.getWorld().getBlockState(this.getPos());
   }

   public IWorld getWorld() {
      IWorld world = this.world != null ? (IWorld)this.world.get() : null;
      if (world == null) {
         world = ServerLifecycleHooks.getCurrentServer().getWorld(DimensionType.getById(this.getDimId()));
         this.world = new WeakReference(world);
      }

      return (IWorld)world;
   }

   public BlockState getReplacedBlock() {
      if (this.replacedBlock == null) {
         Block var10001 = (Block)ForgeRegistries.BLOCKS.getValue(this.getRegistryName());
         this.replacedBlock = Block.getStateById(this.getMeta());
      }

      return this.replacedBlock;
   }

   @Nullable
   public TileEntity getTileEntity() {
      return this.getNbt() != null ? TileEntity.create(this.getNbt()) : null;
   }

   public boolean restore() {
      return this.restore(false);
   }

   public boolean restore(boolean force) {
      return this.restore(force, true);
   }

   public boolean restore(boolean force, boolean notifyNeighbors) {
      return this.restoreToLocation(this.getWorld(), this.getPos(), force, notifyNeighbors);
   }

   public boolean restoreToLocation(IWorld world, BlockPos pos, boolean force, boolean notifyNeighbors) {
      BlockState current = this.getCurrentBlock();
      BlockState replaced = this.getReplacedBlock();
      int flags = notifyNeighbors ? 3 : 2;
      if (current != replaced) {
         if (!force) {
            return false;
         }

         world.setBlockState(pos, replaced, flags);
      }

      world.setBlockState(pos, replaced, flags);
      if (world instanceof World) {
         ((World)world).notifyBlockUpdate(pos, current, replaced, flags);
      }

      TileEntity te = null;
      if (this.getNbt() != null) {
         te = world.getTileEntity(pos);
         if (te != null) {
            te.read(this.getNbt());
            te.markDirty();
         }
      }

      if (DEBUG) {
         System.out.printf("Restored BlockSnapshot with data [World: %s ][Location: %d,%d,%d ][State: %s ][Block: %s ][TileEntity: %s ][force: %s ][notifyNeighbors: %s]", world.getWorldInfo().getWorldName(), pos.getX(), pos.getY(), pos.getZ(), replaced, replaced.getBlock().delegate.name(), te, force, notifyNeighbors);
      }

      return true;
   }

   public void writeToNBT(CompoundNBT compound) {
      compound.putString("blockMod", this.getRegistryName().getNamespace());
      compound.putString("blockName", this.getRegistryName().getPath());
      compound.putInt("posX", this.getPos().getX());
      compound.putInt("posY", this.getPos().getY());
      compound.putInt("posZ", this.getPos().getZ());
      compound.putInt("flag", this.getFlag());
      compound.putInt("dimension", this.getDimId());
      compound.putInt("metadata", this.getMeta());
      compound.putBoolean("hasTE", this.getNbt() != null);
      if (this.getNbt() != null) {
         compound.put("tileEntity", this.getNbt());
      }

   }

   public boolean equals(Object obj) {
      if (obj == this) {
         return true;
      } else if (obj != null && this.getClass() == obj.getClass()) {
         BlockSnapshot other = (BlockSnapshot)obj;
         return this.getMeta() == other.getMeta() && this.getDimId() == other.getDimId() && this.getPos().equals(other.getPos()) && this.getRegistryName().equals(other.getRegistryName()) && Objects.equals(this.getNbt(), other.getNbt());
      } else {
         return false;
      }
   }

   public int hashCode() {
      int hash = 7;
      int hash = 73 * hash + this.getMeta();
      hash = 73 * hash + this.getDimId();
      hash = 73 * hash + this.getPos().hashCode();
      hash = 73 * hash + this.getRegistryName().hashCode();
      hash = 73 * hash + Objects.hashCode(this.getNbt());
      return hash;
   }

   public BlockPos getPos() {
      return this.pos;
   }

   public int getDimId() {
      return this.dimId;
   }

   public void setReplacedBlock(BlockState replacedBlock) {
      this.replacedBlock = replacedBlock;
   }

   public int getFlag() {
      return this.flag;
   }

   public void setFlag(int flag) {
      this.flag = flag;
   }

   @Nullable
   public CompoundNBT getNbt() {
      return this.nbt;
   }

   public void setWorld(IWorld world) {
      this.world = new WeakReference(world);
   }

   public ResourceLocation getRegistryName() {
      return this.registryName;
   }

   public int getMeta() {
      return 0;
   }
}
