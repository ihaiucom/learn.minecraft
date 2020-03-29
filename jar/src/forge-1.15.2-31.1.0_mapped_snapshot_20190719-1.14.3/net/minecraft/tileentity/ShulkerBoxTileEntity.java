package net.minecraft.tileentity;

import java.util.List;
import java.util.stream.IntStream;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.block.material.PushReaction;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ShulkerBoxContainer;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.SidedInvWrapper;

public class ShulkerBoxTileEntity extends LockableLootTileEntity implements ISidedInventory, ITickableTileEntity {
   private static final int[] SLOTS = IntStream.range(0, 27).toArray();
   private NonNullList<ItemStack> items;
   private int openCount;
   private ShulkerBoxTileEntity.AnimationStatus animationStatus;
   private float progress;
   private float progressOld;
   @Nullable
   private DyeColor color;
   private boolean needsColorFromWorld;

   public ShulkerBoxTileEntity(@Nullable DyeColor p_i47242_1_) {
      super(TileEntityType.SHULKER_BOX);
      this.items = NonNullList.withSize(27, ItemStack.EMPTY);
      this.animationStatus = ShulkerBoxTileEntity.AnimationStatus.CLOSED;
      this.color = p_i47242_1_;
   }

   public ShulkerBoxTileEntity() {
      this((DyeColor)null);
      this.needsColorFromWorld = true;
   }

   public void tick() {
      this.updateAnimation();
      if (this.animationStatus == ShulkerBoxTileEntity.AnimationStatus.OPENING || this.animationStatus == ShulkerBoxTileEntity.AnimationStatus.CLOSING) {
         this.moveCollidedEntities();
      }

   }

   protected void updateAnimation() {
      this.progressOld = this.progress;
      switch(this.animationStatus) {
      case CLOSED:
         this.progress = 0.0F;
         break;
      case OPENING:
         this.progress += 0.1F;
         if (this.progress >= 1.0F) {
            this.moveCollidedEntities();
            this.animationStatus = ShulkerBoxTileEntity.AnimationStatus.OPENED;
            this.progress = 1.0F;
            this.func_213975_v();
         }
         break;
      case CLOSING:
         this.progress -= 0.1F;
         if (this.progress <= 0.0F) {
            this.animationStatus = ShulkerBoxTileEntity.AnimationStatus.CLOSED;
            this.progress = 0.0F;
            this.func_213975_v();
         }
         break;
      case OPENED:
         this.progress = 1.0F;
      }

   }

   public ShulkerBoxTileEntity.AnimationStatus getAnimationStatus() {
      return this.animationStatus;
   }

   public AxisAlignedBB getBoundingBox(BlockState p_190584_1_) {
      return this.getBoundingBox((Direction)p_190584_1_.get(ShulkerBoxBlock.FACING));
   }

   public AxisAlignedBB getBoundingBox(Direction p_190587_1_) {
      float f = this.getProgress(1.0F);
      return VoxelShapes.fullCube().getBoundingBox().expand((double)(0.5F * f * (float)p_190587_1_.getXOffset()), (double)(0.5F * f * (float)p_190587_1_.getYOffset()), (double)(0.5F * f * (float)p_190587_1_.getZOffset()));
   }

   private AxisAlignedBB getTopBoundingBox(Direction p_190588_1_) {
      Direction direction = p_190588_1_.getOpposite();
      return this.getBoundingBox(p_190588_1_).contract((double)direction.getXOffset(), (double)direction.getYOffset(), (double)direction.getZOffset());
   }

   private void moveCollidedEntities() {
      BlockState blockstate = this.world.getBlockState(this.getPos());
      if (blockstate.getBlock() instanceof ShulkerBoxBlock) {
         Direction direction = (Direction)blockstate.get(ShulkerBoxBlock.FACING);
         AxisAlignedBB axisalignedbb = this.getTopBoundingBox(direction).offset(this.pos);
         List<Entity> list = this.world.getEntitiesWithinAABBExcludingEntity((Entity)null, axisalignedbb);
         if (!list.isEmpty()) {
            for(int i = 0; i < list.size(); ++i) {
               Entity entity = (Entity)list.get(i);
               if (entity.getPushReaction() != PushReaction.IGNORE) {
                  double d0 = 0.0D;
                  double d1 = 0.0D;
                  double d2 = 0.0D;
                  AxisAlignedBB axisalignedbb1 = entity.getBoundingBox();
                  switch(direction.getAxis()) {
                  case X:
                     if (direction.getAxisDirection() == Direction.AxisDirection.POSITIVE) {
                        d0 = axisalignedbb.maxX - axisalignedbb1.minX;
                     } else {
                        d0 = axisalignedbb1.maxX - axisalignedbb.minX;
                     }

                     d0 += 0.01D;
                     break;
                  case Y:
                     if (direction.getAxisDirection() == Direction.AxisDirection.POSITIVE) {
                        d1 = axisalignedbb.maxY - axisalignedbb1.minY;
                     } else {
                        d1 = axisalignedbb1.maxY - axisalignedbb.minY;
                     }

                     d1 += 0.01D;
                     break;
                  case Z:
                     if (direction.getAxisDirection() == Direction.AxisDirection.POSITIVE) {
                        d2 = axisalignedbb.maxZ - axisalignedbb1.minZ;
                     } else {
                        d2 = axisalignedbb1.maxZ - axisalignedbb.minZ;
                     }

                     d2 += 0.01D;
                  }

                  entity.move(MoverType.SHULKER_BOX, new Vec3d(d0 * (double)direction.getXOffset(), d1 * (double)direction.getYOffset(), d2 * (double)direction.getZOffset()));
               }
            }
         }
      }

   }

   public int getSizeInventory() {
      return this.items.size();
   }

   public boolean receiveClientEvent(int p_145842_1_, int p_145842_2_) {
      if (p_145842_1_ == 1) {
         this.openCount = p_145842_2_;
         if (p_145842_2_ == 0) {
            this.animationStatus = ShulkerBoxTileEntity.AnimationStatus.CLOSING;
            this.func_213975_v();
         }

         if (p_145842_2_ == 1) {
            this.animationStatus = ShulkerBoxTileEntity.AnimationStatus.OPENING;
            this.func_213975_v();
         }

         return true;
      } else {
         return super.receiveClientEvent(p_145842_1_, p_145842_2_);
      }
   }

   private void func_213975_v() {
      this.getBlockState().updateNeighbors(this.getWorld(), this.getPos(), 3);
   }

   public void openInventory(PlayerEntity p_174889_1_) {
      if (!p_174889_1_.isSpectator()) {
         if (this.openCount < 0) {
            this.openCount = 0;
         }

         ++this.openCount;
         this.world.addBlockEvent(this.pos, this.getBlockState().getBlock(), 1, this.openCount);
         if (this.openCount == 1) {
            this.world.playSound((PlayerEntity)null, this.pos, SoundEvents.BLOCK_SHULKER_BOX_OPEN, SoundCategory.BLOCKS, 0.5F, this.world.rand.nextFloat() * 0.1F + 0.9F);
         }
      }

   }

   public void closeInventory(PlayerEntity p_174886_1_) {
      if (!p_174886_1_.isSpectator()) {
         --this.openCount;
         this.world.addBlockEvent(this.pos, this.getBlockState().getBlock(), 1, this.openCount);
         if (this.openCount <= 0) {
            this.world.playSound((PlayerEntity)null, this.pos, SoundEvents.BLOCK_SHULKER_BOX_CLOSE, SoundCategory.BLOCKS, 0.5F, this.world.rand.nextFloat() * 0.1F + 0.9F);
         }
      }

   }

   protected ITextComponent getDefaultName() {
      return new TranslationTextComponent("container.shulkerBox", new Object[0]);
   }

   public void read(CompoundNBT p_145839_1_) {
      super.read(p_145839_1_);
      this.loadFromNbt(p_145839_1_);
   }

   public CompoundNBT write(CompoundNBT p_189515_1_) {
      super.write(p_189515_1_);
      return this.saveToNbt(p_189515_1_);
   }

   public void loadFromNbt(CompoundNBT p_190586_1_) {
      this.items = NonNullList.withSize(this.getSizeInventory(), ItemStack.EMPTY);
      if (!this.checkLootAndRead(p_190586_1_) && p_190586_1_.contains("Items", 9)) {
         ItemStackHelper.loadAllItems(p_190586_1_, this.items);
      }

   }

   public CompoundNBT saveToNbt(CompoundNBT p_190580_1_) {
      if (!this.checkLootAndWrite(p_190580_1_)) {
         ItemStackHelper.saveAllItems(p_190580_1_, this.items, false);
      }

      return p_190580_1_;
   }

   protected NonNullList<ItemStack> getItems() {
      return this.items;
   }

   protected void setItems(NonNullList<ItemStack> p_199721_1_) {
      this.items = p_199721_1_;
   }

   public int[] getSlotsForFace(Direction p_180463_1_) {
      return SLOTS;
   }

   public boolean canInsertItem(int p_180462_1_, ItemStack p_180462_2_, @Nullable Direction p_180462_3_) {
      return !(Block.getBlockFromItem(p_180462_2_.getItem()) instanceof ShulkerBoxBlock);
   }

   public boolean canExtractItem(int p_180461_1_, ItemStack p_180461_2_, Direction p_180461_3_) {
      return true;
   }

   public float getProgress(float p_190585_1_) {
      return MathHelper.lerp(p_190585_1_, this.progressOld, this.progress);
   }

   @Nullable
   @OnlyIn(Dist.CLIENT)
   public DyeColor getColor() {
      if (this.needsColorFromWorld) {
         this.color = ShulkerBoxBlock.getColorFromBlock(this.getBlockState().getBlock());
         this.needsColorFromWorld = false;
      }

      return this.color;
   }

   protected Container createMenu(int p_213906_1_, PlayerInventory p_213906_2_) {
      return new ShulkerBoxContainer(p_213906_1_, p_213906_2_, this);
   }

   protected IItemHandler createUnSidedHandler() {
      return new SidedInvWrapper(this, Direction.UP);
   }

   public static enum AnimationStatus {
      CLOSED,
      OPENING,
      OPENED,
      CLOSING;
   }
}
