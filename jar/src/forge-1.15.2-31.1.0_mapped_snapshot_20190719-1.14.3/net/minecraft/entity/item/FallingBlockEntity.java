package net.minecraft.entity.item;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import net.minecraft.block.AnvilBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ConcretePowderBlock;
import net.minecraft.block.FallingBlock;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MoverType;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.DirectionalPlaceContext;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.play.server.SSpawnObjectPacket;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class FallingBlockEntity extends Entity {
   private BlockState fallTile;
   public int fallTime;
   public boolean shouldDropItem;
   private boolean dontSetBlock;
   private boolean hurtEntities;
   private int fallHurtMax;
   private float fallHurtAmount;
   public CompoundNBT tileEntityData;
   protected static final DataParameter<BlockPos> ORIGIN;

   public FallingBlockEntity(EntityType<? extends FallingBlockEntity> p_i50218_1_, World p_i50218_2_) {
      super(p_i50218_1_, p_i50218_2_);
      this.fallTile = Blocks.SAND.getDefaultState();
      this.shouldDropItem = true;
      this.fallHurtMax = 40;
      this.fallHurtAmount = 2.0F;
   }

   public FallingBlockEntity(World p_i45848_1_, double p_i45848_2_, double p_i45848_4_, double p_i45848_6_, BlockState p_i45848_8_) {
      this(EntityType.FALLING_BLOCK, p_i45848_1_);
      this.fallTile = p_i45848_8_;
      this.preventEntitySpawning = true;
      this.setPosition(p_i45848_2_, p_i45848_4_ + (double)((1.0F - this.getHeight()) / 2.0F), p_i45848_6_);
      this.setMotion(Vec3d.ZERO);
      this.prevPosX = p_i45848_2_;
      this.prevPosY = p_i45848_4_;
      this.prevPosZ = p_i45848_6_;
      this.setOrigin(new BlockPos(this));
   }

   public boolean canBeAttackedWithItem() {
      return false;
   }

   public void setOrigin(BlockPos p_184530_1_) {
      this.dataManager.set(ORIGIN, p_184530_1_);
   }

   @OnlyIn(Dist.CLIENT)
   public BlockPos getOrigin() {
      return (BlockPos)this.dataManager.get(ORIGIN);
   }

   protected boolean func_225502_at_() {
      return false;
   }

   protected void registerData() {
      this.dataManager.register(ORIGIN, BlockPos.ZERO);
   }

   public boolean canBeCollidedWith() {
      return !this.removed;
   }

   public void tick() {
      if (this.fallTile.isAir()) {
         this.remove();
      } else {
         Block block = this.fallTile.getBlock();
         BlockPos blockpos1;
         if (this.fallTime++ == 0) {
            blockpos1 = new BlockPos(this);
            if (this.world.getBlockState(blockpos1).getBlock() == block) {
               this.world.removeBlock(blockpos1, false);
            } else if (!this.world.isRemote) {
               this.remove();
               return;
            }
         }

         if (!this.hasNoGravity()) {
            this.setMotion(this.getMotion().add(0.0D, -0.04D, 0.0D));
         }

         this.move(MoverType.SELF, this.getMotion());
         if (!this.world.isRemote) {
            blockpos1 = new BlockPos(this);
            boolean flag = this.fallTile.getBlock() instanceof ConcretePowderBlock;
            boolean flag1 = flag && this.world.getFluidState(blockpos1).isTagged(FluidTags.WATER);
            double d0 = this.getMotion().lengthSquared();
            if (flag && d0 > 1.0D) {
               BlockRayTraceResult blockraytraceresult = this.world.rayTraceBlocks(new RayTraceContext(new Vec3d(this.prevPosX, this.prevPosY, this.prevPosZ), this.getPositionVec(), RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.SOURCE_ONLY, this));
               if (blockraytraceresult.getType() != RayTraceResult.Type.MISS && this.world.getFluidState(blockraytraceresult.getPos()).isTagged(FluidTags.WATER)) {
                  blockpos1 = blockraytraceresult.getPos();
                  flag1 = true;
               }
            }

            if (!this.onGround && !flag1) {
               if (!this.world.isRemote && (this.fallTime > 100 && (blockpos1.getY() < 1 || blockpos1.getY() > 256) || this.fallTime > 600)) {
                  if (this.shouldDropItem && this.world.getGameRules().getBoolean(GameRules.DO_ENTITY_DROPS)) {
                     this.entityDropItem(block);
                  }

                  this.remove();
               }
            } else {
               BlockState blockstate = this.world.getBlockState(blockpos1);
               this.setMotion(this.getMotion().mul(0.7D, -0.5D, 0.7D));
               if (blockstate.getBlock() != Blocks.MOVING_PISTON) {
                  this.remove();
                  if (this.dontSetBlock) {
                     if (block instanceof FallingBlock) {
                        ((FallingBlock)block).onBroken(this.world, blockpos1);
                     }
                  } else {
                     boolean flag2 = blockstate.isReplaceable(new DirectionalPlaceContext(this.world, blockpos1, Direction.DOWN, ItemStack.EMPTY, Direction.UP));
                     boolean flag3 = FallingBlock.canFallThrough(this.world.getBlockState(blockpos1.down())) && (!flag || !flag1);
                     boolean flag4 = this.fallTile.isValidPosition(this.world, blockpos1) && !flag3;
                     if (flag2 && flag4) {
                        if (this.fallTile.has(BlockStateProperties.WATERLOGGED) && this.world.getFluidState(blockpos1).getFluid() == Fluids.WATER) {
                           this.fallTile = (BlockState)this.fallTile.with(BlockStateProperties.WATERLOGGED, true);
                        }

                        if (!this.world.setBlockState(blockpos1, this.fallTile, 3)) {
                           if (this.shouldDropItem && this.world.getGameRules().getBoolean(GameRules.DO_ENTITY_DROPS)) {
                              this.entityDropItem(block);
                           }
                        } else {
                           if (block instanceof FallingBlock) {
                              ((FallingBlock)block).onEndFalling(this.world, blockpos1, this.fallTile, blockstate);
                           }

                           if (this.tileEntityData != null && this.fallTile.hasTileEntity()) {
                              TileEntity tileentity = this.world.getTileEntity(blockpos1);
                              if (tileentity != null) {
                                 CompoundNBT compoundnbt = tileentity.write(new CompoundNBT());
                                 Iterator var13 = this.tileEntityData.keySet().iterator();

                                 while(var13.hasNext()) {
                                    String s = (String)var13.next();
                                    INBT inbt = this.tileEntityData.get(s);
                                    if (!"x".equals(s) && !"y".equals(s) && !"z".equals(s)) {
                                       compoundnbt.put(s, inbt.copy());
                                    }
                                 }

                                 tileentity.read(compoundnbt);
                                 tileentity.markDirty();
                              }
                           }
                        }
                     } else if (this.shouldDropItem && this.world.getGameRules().getBoolean(GameRules.DO_ENTITY_DROPS)) {
                        this.entityDropItem(block);
                     }
                  }
               }
            }
         }

         this.setMotion(this.getMotion().scale(0.98D));
      }

   }

   public boolean func_225503_b_(float p_225503_1_, float p_225503_2_) {
      if (this.hurtEntities) {
         int i = MathHelper.ceil(p_225503_1_ - 1.0F);
         if (i > 0) {
            List<Entity> list = Lists.newArrayList(this.world.getEntitiesWithinAABBExcludingEntity(this, this.getBoundingBox()));
            boolean flag = this.fallTile.isIn(BlockTags.ANVIL);
            DamageSource damagesource = flag ? DamageSource.ANVIL : DamageSource.FALLING_BLOCK;
            Iterator var7 = list.iterator();

            while(var7.hasNext()) {
               Entity entity = (Entity)var7.next();
               entity.attackEntityFrom(damagesource, (float)Math.min(MathHelper.floor((float)i * this.fallHurtAmount), this.fallHurtMax));
            }

            if (flag && (double)this.rand.nextFloat() < 0.05000000074505806D + (double)i * 0.05D) {
               BlockState blockstate = AnvilBlock.damage(this.fallTile);
               if (blockstate == null) {
                  this.dontSetBlock = true;
               } else {
                  this.fallTile = blockstate;
               }
            }
         }
      }

      return false;
   }

   protected void writeAdditional(CompoundNBT p_213281_1_) {
      p_213281_1_.put("BlockState", NBTUtil.writeBlockState(this.fallTile));
      p_213281_1_.putInt("Time", this.fallTime);
      p_213281_1_.putBoolean("DropItem", this.shouldDropItem);
      p_213281_1_.putBoolean("HurtEntities", this.hurtEntities);
      p_213281_1_.putFloat("FallHurtAmount", this.fallHurtAmount);
      p_213281_1_.putInt("FallHurtMax", this.fallHurtMax);
      if (this.tileEntityData != null) {
         p_213281_1_.put("TileEntityData", this.tileEntityData);
      }

   }

   protected void readAdditional(CompoundNBT p_70037_1_) {
      this.fallTile = NBTUtil.readBlockState(p_70037_1_.getCompound("BlockState"));
      this.fallTime = p_70037_1_.getInt("Time");
      if (p_70037_1_.contains("HurtEntities", 99)) {
         this.hurtEntities = p_70037_1_.getBoolean("HurtEntities");
         this.fallHurtAmount = p_70037_1_.getFloat("FallHurtAmount");
         this.fallHurtMax = p_70037_1_.getInt("FallHurtMax");
      } else if (this.fallTile.isIn(BlockTags.ANVIL)) {
         this.hurtEntities = true;
      }

      if (p_70037_1_.contains("DropItem", 99)) {
         this.shouldDropItem = p_70037_1_.getBoolean("DropItem");
      }

      if (p_70037_1_.contains("TileEntityData", 10)) {
         this.tileEntityData = p_70037_1_.getCompound("TileEntityData");
      }

      if (this.fallTile.isAir()) {
         this.fallTile = Blocks.SAND.getDefaultState();
      }

   }

   @OnlyIn(Dist.CLIENT)
   public World getWorldObj() {
      return this.world;
   }

   public void setHurtEntities(boolean p_145806_1_) {
      this.hurtEntities = p_145806_1_;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean canRenderOnFire() {
      return false;
   }

   public void fillCrashReport(CrashReportCategory p_85029_1_) {
      super.fillCrashReport(p_85029_1_);
      p_85029_1_.addDetail("Immitating BlockState", (Object)this.fallTile.toString());
   }

   public BlockState getBlockState() {
      return this.fallTile;
   }

   public boolean ignoreItemEntityData() {
      return true;
   }

   public IPacket<?> createSpawnPacket() {
      return new SSpawnObjectPacket(this, Block.getStateId(this.getBlockState()));
   }

   static {
      ORIGIN = EntityDataManager.createKey(FallingBlockEntity.class, DataSerializers.BLOCK_POS);
   }
}
