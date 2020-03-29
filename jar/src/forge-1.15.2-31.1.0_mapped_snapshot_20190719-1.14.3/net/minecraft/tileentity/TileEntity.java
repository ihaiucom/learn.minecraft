package net.minecraft.tileentity;

import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.util.Mirror;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.CapabilityProvider;
import net.minecraftforge.common.extensions.IForgeTileEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.Supplier;

public abstract class TileEntity extends CapabilityProvider<TileEntity> implements IForgeTileEntity {
   private static final Logger LOGGER = LogManager.getLogger();
   private final TileEntityType<?> type;
   @Nullable
   protected World world;
   protected BlockPos pos;
   protected boolean removed;
   @Nullable
   private BlockState cachedBlockState;
   private boolean warnedInvalidBlock;
   private CompoundNBT customTileData;

   public TileEntity(TileEntityType<?> p_i48289_1_) {
      super(TileEntity.class);
      this.pos = BlockPos.ZERO;
      this.type = p_i48289_1_;
      this.gatherCapabilities();
   }

   @Nullable
   public World getWorld() {
      return this.world;
   }

   public void func_226984_a_(World p_226984_1_, BlockPos p_226984_2_) {
      this.world = p_226984_1_;
      this.pos = p_226984_2_.toImmutable();
   }

   public boolean hasWorld() {
      return this.world != null;
   }

   public void read(CompoundNBT p_145839_1_) {
      this.pos = new BlockPos(p_145839_1_.getInt("x"), p_145839_1_.getInt("y"), p_145839_1_.getInt("z"));
      if (p_145839_1_.contains("ForgeData")) {
         this.customTileData = p_145839_1_.getCompound("ForgeData");
      }

      if (this.getCapabilities() != null && p_145839_1_.contains("ForgeCaps")) {
         this.deserializeCaps(p_145839_1_.getCompound("ForgeCaps"));
      }

   }

   public CompoundNBT write(CompoundNBT p_189515_1_) {
      return this.writeInternal(p_189515_1_);
   }

   private CompoundNBT writeInternal(CompoundNBT p_189516_1_) {
      ResourceLocation resourcelocation = TileEntityType.getId(this.getType());
      if (resourcelocation == null) {
         throw new RuntimeException(this.getClass() + " is missing a mapping! This is a bug!");
      } else {
         p_189516_1_.putString("id", resourcelocation.toString());
         p_189516_1_.putInt("x", this.pos.getX());
         p_189516_1_.putInt("y", this.pos.getY());
         p_189516_1_.putInt("z", this.pos.getZ());
         if (this.customTileData != null) {
            p_189516_1_.put("ForgeData", this.customTileData);
         }

         if (this.getCapabilities() != null) {
            p_189516_1_.put("ForgeCaps", this.serializeCaps());
         }

         return p_189516_1_;
      }
   }

   @Nullable
   public static TileEntity create(CompoundNBT p_203403_0_) {
      String s = p_203403_0_.getString("id");
      return (TileEntity)Registry.BLOCK_ENTITY_TYPE.getValue(new ResourceLocation(s)).map((p_lambda$create$0_1_) -> {
         try {
            return p_lambda$create$0_1_.create();
         } catch (Throwable var3) {
            LOGGER.error("Failed to create block entity {}", s, var3);
            return null;
         }
      }).map((p_lambda$create$1_2_) -> {
         try {
            p_lambda$create$1_2_.read(p_203403_0_);
            return p_lambda$create$1_2_;
         } catch (Throwable var4) {
            LOGGER.error("Failed to load data for block entity {}", s, var4);
            return null;
         }
      }).orElseGet(() -> {
         LOGGER.warn("Skipping BlockEntity with id {}", s);
         return null;
      });
   }

   public void markDirty() {
      if (this.world != null) {
         this.cachedBlockState = this.world.getBlockState(this.pos);
         this.world.markChunkDirty(this.pos, this);
         if (!this.cachedBlockState.isAir(this.world, this.pos)) {
            this.world.updateComparatorOutputLevel(this.pos, this.cachedBlockState.getBlock());
         }
      }

   }

   public double getDistanceSq(double p_145835_1_, double p_145835_3_, double p_145835_5_) {
      double d0 = (double)this.pos.getX() + 0.5D - p_145835_1_;
      double d1 = (double)this.pos.getY() + 0.5D - p_145835_3_;
      double d2 = (double)this.pos.getZ() + 0.5D - p_145835_5_;
      return d0 * d0 + d1 * d1 + d2 * d2;
   }

   @OnlyIn(Dist.CLIENT)
   public double getMaxRenderDistanceSquared() {
      return 4096.0D;
   }

   public BlockPos getPos() {
      return this.pos;
   }

   public BlockState getBlockState() {
      if (this.cachedBlockState == null) {
         this.cachedBlockState = this.world.getBlockState(this.pos);
      }

      return this.cachedBlockState;
   }

   @Nullable
   public SUpdateTileEntityPacket getUpdatePacket() {
      return null;
   }

   public CompoundNBT getUpdateTag() {
      return this.writeInternal(new CompoundNBT());
   }

   public boolean isRemoved() {
      return this.removed;
   }

   public void remove() {
      this.removed = true;
      this.invalidateCaps();
      this.requestModelDataUpdate();
   }

   public void validate() {
      this.removed = false;
   }

   public boolean receiveClientEvent(int p_145842_1_, int p_145842_2_) {
      return false;
   }

   public void updateContainingBlockInfo() {
      this.cachedBlockState = null;
   }

   public void addInfoToCrashReport(CrashReportCategory p_145828_1_) {
      p_145828_1_.addDetail("Name", () -> {
         return Registry.BLOCK_ENTITY_TYPE.getKey(this.getType()) + " // " + this.getClass().getCanonicalName();
      });
      if (this.world != null) {
         CrashReportCategory.addBlockInfo(p_145828_1_, this.pos, this.getBlockState());
         CrashReportCategory.addBlockInfo(p_145828_1_, this.pos, this.world.getBlockState(this.pos));
      }

   }

   public void setPos(BlockPos p_174878_1_) {
      this.pos = p_174878_1_.toImmutable();
   }

   public boolean onlyOpsCanSetNbt() {
      return false;
   }

   public void rotate(Rotation p_189667_1_) {
   }

   public void mirror(Mirror p_189668_1_) {
   }

   public TileEntityType<?> getType() {
      return this.type;
   }

   public CompoundNBT getTileData() {
      if (this.customTileData == null) {
         this.customTileData = new CompoundNBT();
      }

      return this.customTileData;
   }

   public void warnInvalidBlock() {
      if (!this.warnedInvalidBlock) {
         this.warnedInvalidBlock = true;
         LOGGER.warn("Block entity invalid: {} @ {}", new Supplier[]{() -> {
            return Registry.BLOCK_ENTITY_TYPE.getKey(this.getType());
         }, this::getPos});
      }

   }
}
