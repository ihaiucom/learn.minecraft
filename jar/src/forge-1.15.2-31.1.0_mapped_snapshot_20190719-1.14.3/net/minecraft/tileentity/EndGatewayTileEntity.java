package net.minecraft.tileentity;

import java.util.Iterator;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.dimension.EndDimension;
import net.minecraft.world.gen.feature.EndGatewayConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EndGatewayTileEntity extends EndPortalTileEntity implements ITickableTileEntity {
   private static final Logger LOGGER = LogManager.getLogger();
   private long age;
   private int teleportCooldown;
   @Nullable
   private BlockPos exitPortal;
   private boolean exactTeleport;

   public EndGatewayTileEntity() {
      super(TileEntityType.END_GATEWAY);
   }

   public CompoundNBT write(CompoundNBT p_189515_1_) {
      super.write(p_189515_1_);
      p_189515_1_.putLong("Age", this.age);
      if (this.exitPortal != null) {
         p_189515_1_.put("ExitPortal", NBTUtil.writeBlockPos(this.exitPortal));
      }

      if (this.exactTeleport) {
         p_189515_1_.putBoolean("ExactTeleport", this.exactTeleport);
      }

      return p_189515_1_;
   }

   public void read(CompoundNBT p_145839_1_) {
      super.read(p_145839_1_);
      this.age = p_145839_1_.getLong("Age");
      if (p_145839_1_.contains("ExitPortal", 10)) {
         this.exitPortal = NBTUtil.readBlockPos(p_145839_1_.getCompound("ExitPortal"));
      }

      this.exactTeleport = p_145839_1_.getBoolean("ExactTeleport");
   }

   @OnlyIn(Dist.CLIENT)
   public double getMaxRenderDistanceSquared() {
      return 65536.0D;
   }

   public void tick() {
      boolean lvt_1_1_ = this.isSpawning();
      boolean lvt_2_1_ = this.isCoolingDown();
      ++this.age;
      if (lvt_2_1_) {
         --this.teleportCooldown;
      } else if (!this.world.isRemote) {
         List<Entity> lvt_3_1_ = this.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(this.getPos()));
         if (!lvt_3_1_.isEmpty()) {
            this.teleportEntity(((Entity)lvt_3_1_.get(0)).getLowestRidingEntity());
         }

         if (this.age % 2400L == 0L) {
            this.triggerCooldown();
         }
      }

      if (lvt_1_1_ != this.isSpawning() || lvt_2_1_ != this.isCoolingDown()) {
         this.markDirty();
      }

   }

   public boolean isSpawning() {
      return this.age < 200L;
   }

   public boolean isCoolingDown() {
      return this.teleportCooldown > 0;
   }

   @OnlyIn(Dist.CLIENT)
   public float getSpawnPercent(float p_195497_1_) {
      return MathHelper.clamp(((float)this.age + p_195497_1_) / 200.0F, 0.0F, 1.0F);
   }

   @OnlyIn(Dist.CLIENT)
   public float getCooldownPercent(float p_195491_1_) {
      return 1.0F - MathHelper.clamp(((float)this.teleportCooldown - p_195491_1_) / 40.0F, 0.0F, 1.0F);
   }

   @Nullable
   public SUpdateTileEntityPacket getUpdatePacket() {
      return new SUpdateTileEntityPacket(this.pos, 8, this.getUpdateTag());
   }

   public CompoundNBT getUpdateTag() {
      return this.write(new CompoundNBT());
   }

   public void triggerCooldown() {
      if (!this.world.isRemote) {
         this.teleportCooldown = 40;
         this.world.addBlockEvent(this.getPos(), this.getBlockState().getBlock(), 1, 0);
         this.markDirty();
      }

   }

   public boolean receiveClientEvent(int p_145842_1_, int p_145842_2_) {
      if (p_145842_1_ == 1) {
         this.teleportCooldown = 40;
         return true;
      } else {
         return super.receiveClientEvent(p_145842_1_, p_145842_2_);
      }
   }

   public void teleportEntity(Entity p_195496_1_) {
      if (this.world instanceof ServerWorld && !this.isCoolingDown()) {
         this.teleportCooldown = 100;
         if (this.exitPortal == null && this.world.dimension instanceof EndDimension) {
            this.func_227015_a_((ServerWorld)this.world);
         }

         if (this.exitPortal != null) {
            BlockPos lvt_2_1_ = this.exactTeleport ? this.exitPortal : this.findExitPosition();
            p_195496_1_.teleportKeepLoaded((double)lvt_2_1_.getX() + 0.5D, (double)lvt_2_1_.getY() + 0.5D, (double)lvt_2_1_.getZ() + 0.5D);
         }

         this.triggerCooldown();
      }
   }

   private BlockPos findExitPosition() {
      BlockPos lvt_1_1_ = findHighestBlock(this.world, this.exitPortal, 5, false);
      LOGGER.debug("Best exit position for portal at {} is {}", this.exitPortal, lvt_1_1_);
      return lvt_1_1_.up();
   }

   private void func_227015_a_(ServerWorld p_227015_1_) {
      Vec3d lvt_2_1_ = (new Vec3d((double)this.getPos().getX(), 0.0D, (double)this.getPos().getZ())).normalize();
      Vec3d lvt_3_1_ = lvt_2_1_.scale(1024.0D);

      int var4;
      for(var4 = 16; getChunk(p_227015_1_, lvt_3_1_).getTopFilledSegment() > 0 && var4-- > 0; lvt_3_1_ = lvt_3_1_.add(lvt_2_1_.scale(-16.0D))) {
         LOGGER.debug("Skipping backwards past nonempty chunk at {}", lvt_3_1_);
      }

      for(var4 = 16; getChunk(p_227015_1_, lvt_3_1_).getTopFilledSegment() == 0 && var4-- > 0; lvt_3_1_ = lvt_3_1_.add(lvt_2_1_.scale(16.0D))) {
         LOGGER.debug("Skipping forward past empty chunk at {}", lvt_3_1_);
      }

      LOGGER.debug("Found chunk at {}", lvt_3_1_);
      Chunk lvt_5_1_ = getChunk(p_227015_1_, lvt_3_1_);
      this.exitPortal = findSpawnpointInChunk(lvt_5_1_);
      if (this.exitPortal == null) {
         this.exitPortal = new BlockPos(lvt_3_1_.x + 0.5D, 75.0D, lvt_3_1_.z + 0.5D);
         LOGGER.debug("Failed to find suitable block, settling on {}", this.exitPortal);
         Feature.END_ISLAND.func_225566_b_(IFeatureConfig.NO_FEATURE_CONFIG).place(p_227015_1_, p_227015_1_.getChunkProvider().getChunkGenerator(), new Random(this.exitPortal.toLong()), this.exitPortal);
      } else {
         LOGGER.debug("Found block at {}", this.exitPortal);
      }

      this.exitPortal = findHighestBlock(p_227015_1_, this.exitPortal, 16, true);
      LOGGER.debug("Creating portal at {}", this.exitPortal);
      this.exitPortal = this.exitPortal.up(10);
      this.func_227016_a_(p_227015_1_, this.exitPortal);
      this.markDirty();
   }

   private static BlockPos findHighestBlock(IBlockReader p_195494_0_, BlockPos p_195494_1_, int p_195494_2_, boolean p_195494_3_) {
      BlockPos lvt_4_1_ = null;

      for(int lvt_5_1_ = -p_195494_2_; lvt_5_1_ <= p_195494_2_; ++lvt_5_1_) {
         for(int lvt_6_1_ = -p_195494_2_; lvt_6_1_ <= p_195494_2_; ++lvt_6_1_) {
            if (lvt_5_1_ != 0 || lvt_6_1_ != 0 || p_195494_3_) {
               for(int lvt_7_1_ = 255; lvt_7_1_ > (lvt_4_1_ == null ? 0 : lvt_4_1_.getY()); --lvt_7_1_) {
                  BlockPos lvt_8_1_ = new BlockPos(p_195494_1_.getX() + lvt_5_1_, lvt_7_1_, p_195494_1_.getZ() + lvt_6_1_);
                  BlockState lvt_9_1_ = p_195494_0_.getBlockState(lvt_8_1_);
                  if (lvt_9_1_.func_224756_o(p_195494_0_, lvt_8_1_) && (p_195494_3_ || lvt_9_1_.getBlock() != Blocks.BEDROCK)) {
                     lvt_4_1_ = lvt_8_1_;
                     break;
                  }
               }
            }
         }
      }

      return lvt_4_1_ == null ? p_195494_1_ : lvt_4_1_;
   }

   private static Chunk getChunk(World p_195495_0_, Vec3d p_195495_1_) {
      return p_195495_0_.getChunk(MathHelper.floor(p_195495_1_.x / 16.0D), MathHelper.floor(p_195495_1_.z / 16.0D));
   }

   @Nullable
   private static BlockPos findSpawnpointInChunk(Chunk p_195498_0_) {
      ChunkPos lvt_1_1_ = p_195498_0_.getPos();
      BlockPos lvt_2_1_ = new BlockPos(lvt_1_1_.getXStart(), 30, lvt_1_1_.getZStart());
      int lvt_3_1_ = p_195498_0_.getTopFilledSegment() + 16 - 1;
      BlockPos lvt_4_1_ = new BlockPos(lvt_1_1_.getXEnd(), lvt_3_1_, lvt_1_1_.getZEnd());
      BlockPos lvt_5_1_ = null;
      double lvt_6_1_ = 0.0D;
      Iterator var8 = BlockPos.getAllInBoxMutable(lvt_2_1_, lvt_4_1_).iterator();

      while(true) {
         BlockPos lvt_9_1_;
         double lvt_13_1_;
         do {
            BlockPos lvt_11_1_;
            BlockPos lvt_12_1_;
            do {
               BlockState lvt_10_1_;
               do {
                  do {
                     if (!var8.hasNext()) {
                        return lvt_5_1_;
                     }

                     lvt_9_1_ = (BlockPos)var8.next();
                     lvt_10_1_ = p_195498_0_.getBlockState(lvt_9_1_);
                     lvt_11_1_ = lvt_9_1_.up();
                     lvt_12_1_ = lvt_9_1_.up(2);
                  } while(lvt_10_1_.getBlock() != Blocks.END_STONE);
               } while(p_195498_0_.getBlockState(lvt_11_1_).func_224756_o(p_195498_0_, lvt_11_1_));
            } while(p_195498_0_.getBlockState(lvt_12_1_).func_224756_o(p_195498_0_, lvt_12_1_));

            lvt_13_1_ = lvt_9_1_.distanceSq(0.0D, 0.0D, 0.0D, true);
         } while(lvt_5_1_ != null && lvt_13_1_ >= lvt_6_1_);

         lvt_5_1_ = lvt_9_1_;
         lvt_6_1_ = lvt_13_1_;
      }
   }

   private void func_227016_a_(ServerWorld p_227016_1_, BlockPos p_227016_2_) {
      Feature.END_GATEWAY.func_225566_b_(EndGatewayConfig.func_214702_a(this.getPos(), false)).place(p_227016_1_, p_227016_1_.getChunkProvider().getChunkGenerator(), new Random(), p_227016_2_);
   }

   @OnlyIn(Dist.CLIENT)
   public boolean shouldRenderFace(Direction p_184313_1_) {
      return Block.shouldSideBeRendered(this.getBlockState(), this.world, this.getPos(), p_184313_1_);
   }

   @OnlyIn(Dist.CLIENT)
   public int getParticleAmount() {
      int lvt_1_1_ = 0;
      Direction[] var2 = Direction.values();
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         Direction lvt_5_1_ = var2[var4];
         lvt_1_1_ += this.shouldRenderFace(lvt_5_1_) ? 1 : 0;
      }

      return lvt_1_1_;
   }

   public void setExitPortal(BlockPos p_195489_1_, boolean p_195489_2_) {
      this.exactTeleport = p_195489_2_;
      this.exitPortal = p_195489_1_;
   }
}
