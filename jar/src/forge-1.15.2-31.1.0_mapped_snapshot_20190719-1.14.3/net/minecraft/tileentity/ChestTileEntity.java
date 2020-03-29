package net.minecraft.tileentity;

import java.util.Iterator;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.DoubleSidedInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.inventory.container.ChestContainer;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.properties.ChestType;
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;
import net.minecraftforge.items.wrapper.InvWrapper;

@OnlyIn(
   value = Dist.CLIENT,
   _interface = IChestLid.class
)
public class ChestTileEntity extends LockableLootTileEntity implements IChestLid, ITickableTileEntity {
   private NonNullList<ItemStack> chestContents;
   protected float lidAngle;
   protected float prevLidAngle;
   protected int numPlayersUsing;
   private int ticksSinceSync;
   private LazyOptional<IItemHandlerModifiable> chestHandler;

   protected ChestTileEntity(TileEntityType<?> p_i48287_1_) {
      super(p_i48287_1_);
      this.chestContents = NonNullList.withSize(27, ItemStack.EMPTY);
   }

   public ChestTileEntity() {
      this(TileEntityType.CHEST);
   }

   public int getSizeInventory() {
      return 27;
   }

   protected ITextComponent getDefaultName() {
      return new TranslationTextComponent("container.chest", new Object[0]);
   }

   public void read(CompoundNBT p_145839_1_) {
      super.read(p_145839_1_);
      this.chestContents = NonNullList.withSize(this.getSizeInventory(), ItemStack.EMPTY);
      if (!this.checkLootAndRead(p_145839_1_)) {
         ItemStackHelper.loadAllItems(p_145839_1_, this.chestContents);
      }

   }

   public CompoundNBT write(CompoundNBT p_189515_1_) {
      super.write(p_189515_1_);
      if (!this.checkLootAndWrite(p_189515_1_)) {
         ItemStackHelper.saveAllItems(p_189515_1_, this.chestContents);
      }

      return p_189515_1_;
   }

   public void tick() {
      int i = this.pos.getX();
      int j = this.pos.getY();
      int k = this.pos.getZ();
      ++this.ticksSinceSync;
      this.numPlayersUsing = func_213977_a(this.world, this, this.ticksSinceSync, i, j, k, this.numPlayersUsing);
      this.prevLidAngle = this.lidAngle;
      float f = 0.1F;
      if (this.numPlayersUsing > 0 && this.lidAngle == 0.0F) {
         this.playSound(SoundEvents.BLOCK_CHEST_OPEN);
      }

      if (this.numPlayersUsing == 0 && this.lidAngle > 0.0F || this.numPlayersUsing > 0 && this.lidAngle < 1.0F) {
         float f1 = this.lidAngle;
         if (this.numPlayersUsing > 0) {
            this.lidAngle += 0.1F;
         } else {
            this.lidAngle -= 0.1F;
         }

         if (this.lidAngle > 1.0F) {
            this.lidAngle = 1.0F;
         }

         float f2 = 0.5F;
         if (this.lidAngle < 0.5F && f1 >= 0.5F) {
            this.playSound(SoundEvents.BLOCK_CHEST_CLOSE);
         }

         if (this.lidAngle < 0.0F) {
            this.lidAngle = 0.0F;
         }
      }

   }

   public static int func_213977_a(World p_213977_0_, LockableTileEntity p_213977_1_, int p_213977_2_, int p_213977_3_, int p_213977_4_, int p_213977_5_, int p_213977_6_) {
      if (!p_213977_0_.isRemote && p_213977_6_ != 0 && (p_213977_2_ + p_213977_3_ + p_213977_4_ + p_213977_5_) % 200 == 0) {
         p_213977_6_ = func_213976_a(p_213977_0_, p_213977_1_, p_213977_3_, p_213977_4_, p_213977_5_);
      }

      return p_213977_6_;
   }

   public static int func_213976_a(World p_213976_0_, LockableTileEntity p_213976_1_, int p_213976_2_, int p_213976_3_, int p_213976_4_) {
      int i = 0;
      float f = 5.0F;
      Iterator var7 = p_213976_0_.getEntitiesWithinAABB(PlayerEntity.class, new AxisAlignedBB((double)((float)p_213976_2_ - 5.0F), (double)((float)p_213976_3_ - 5.0F), (double)((float)p_213976_4_ - 5.0F), (double)((float)(p_213976_2_ + 1) + 5.0F), (double)((float)(p_213976_3_ + 1) + 5.0F), (double)((float)(p_213976_4_ + 1) + 5.0F))).iterator();

      while(true) {
         IInventory iinventory;
         do {
            PlayerEntity playerentity;
            do {
               if (!var7.hasNext()) {
                  return i;
               }

               playerentity = (PlayerEntity)var7.next();
            } while(!(playerentity.openContainer instanceof ChestContainer));

            iinventory = ((ChestContainer)playerentity.openContainer).getLowerChestInventory();
         } while(iinventory != p_213976_1_ && (!(iinventory instanceof DoubleSidedInventory) || !((DoubleSidedInventory)iinventory).isPartOfLargeChest(p_213976_1_)));

         ++i;
      }
   }

   private void playSound(SoundEvent p_195483_1_) {
      ChestType chesttype = (ChestType)this.getBlockState().get(ChestBlock.TYPE);
      if (chesttype != ChestType.LEFT) {
         double d0 = (double)this.pos.getX() + 0.5D;
         double d1 = (double)this.pos.getY() + 0.5D;
         double d2 = (double)this.pos.getZ() + 0.5D;
         if (chesttype == ChestType.RIGHT) {
            Direction direction = ChestBlock.getDirectionToAttached(this.getBlockState());
            d0 += (double)direction.getXOffset() * 0.5D;
            d2 += (double)direction.getZOffset() * 0.5D;
         }

         this.world.playSound((PlayerEntity)null, d0, d1, d2, p_195483_1_, SoundCategory.BLOCKS, 0.5F, this.world.rand.nextFloat() * 0.1F + 0.9F);
      }

   }

   public boolean receiveClientEvent(int p_145842_1_, int p_145842_2_) {
      if (p_145842_1_ == 1) {
         this.numPlayersUsing = p_145842_2_;
         return true;
      } else {
         return super.receiveClientEvent(p_145842_1_, p_145842_2_);
      }
   }

   public void openInventory(PlayerEntity p_174889_1_) {
      if (!p_174889_1_.isSpectator()) {
         if (this.numPlayersUsing < 0) {
            this.numPlayersUsing = 0;
         }

         ++this.numPlayersUsing;
         this.onOpenOrClose();
      }

   }

   public void closeInventory(PlayerEntity p_174886_1_) {
      if (!p_174886_1_.isSpectator()) {
         --this.numPlayersUsing;
         this.onOpenOrClose();
      }

   }

   protected void onOpenOrClose() {
      Block block = this.getBlockState().getBlock();
      if (block instanceof ChestBlock) {
         this.world.addBlockEvent(this.pos, block, 1, this.numPlayersUsing);
         this.world.notifyNeighborsOfStateChange(this.pos, block);
      }

   }

   protected NonNullList<ItemStack> getItems() {
      return this.chestContents;
   }

   protected void setItems(NonNullList<ItemStack> p_199721_1_) {
      this.chestContents = p_199721_1_;
   }

   @OnlyIn(Dist.CLIENT)
   public float getLidAngle(float p_195480_1_) {
      return MathHelper.lerp(p_195480_1_, this.prevLidAngle, this.lidAngle);
   }

   public static int getPlayersUsing(IBlockReader p_195481_0_, BlockPos p_195481_1_) {
      BlockState blockstate = p_195481_0_.getBlockState(p_195481_1_);
      if (blockstate.hasTileEntity()) {
         TileEntity tileentity = p_195481_0_.getTileEntity(p_195481_1_);
         if (tileentity instanceof ChestTileEntity) {
            return ((ChestTileEntity)tileentity).numPlayersUsing;
         }
      }

      return 0;
   }

   public static void swapContents(ChestTileEntity p_199722_0_, ChestTileEntity p_199722_1_) {
      NonNullList<ItemStack> nonnulllist = p_199722_0_.getItems();
      p_199722_0_.setItems(p_199722_1_.getItems());
      p_199722_1_.setItems(nonnulllist);
   }

   protected Container createMenu(int p_213906_1_, PlayerInventory p_213906_2_) {
      return ChestContainer.createGeneric9X3(p_213906_1_, p_213906_2_, this);
   }

   public void updateContainingBlockInfo() {
      super.updateContainingBlockInfo();
      if (this.chestHandler != null) {
         this.chestHandler.invalidate();
         this.chestHandler = null;
      }

   }

   public <T> LazyOptional<T> getCapability(Capability<T> p_getCapability_1_, Direction p_getCapability_2_) {
      if (!this.removed && p_getCapability_1_ == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
         if (this.chestHandler == null) {
            this.chestHandler = LazyOptional.of(this::createHandler);
         }

         return this.chestHandler.cast();
      } else {
         return super.getCapability(p_getCapability_1_, p_getCapability_2_);
      }
   }

   private IItemHandlerModifiable createHandler() {
      BlockState state = this.getBlockState();
      if (!(state.getBlock() instanceof ChestBlock)) {
         return new InvWrapper(this);
      } else {
         ChestType type = (ChestType)state.get(ChestBlock.TYPE);
         if (type != ChestType.SINGLE) {
            BlockPos opos = this.getPos().offset(ChestBlock.getDirectionToAttached(state));
            BlockState ostate = this.getWorld().getBlockState(opos);
            if (state.getBlock() == ostate.getBlock()) {
               ChestType otype = (ChestType)ostate.get(ChestBlock.TYPE);
               if (otype != ChestType.SINGLE && type != otype && state.get(ChestBlock.FACING) == ostate.get(ChestBlock.FACING)) {
                  TileEntity ote = this.getWorld().getTileEntity(opos);
                  if (ote instanceof ChestTileEntity) {
                     IInventory top = type == ChestType.RIGHT ? this : (IInventory)ote;
                     IInventory bottom = type == ChestType.RIGHT ? (IInventory)ote : this;
                     return new CombinedInvWrapper(new IItemHandlerModifiable[]{new InvWrapper((IInventory)top), new InvWrapper((IInventory)bottom)});
                  }
               }
            }
         }

         return new InvWrapper(this);
      }
   }

   public void remove() {
      super.remove();
      if (this.chestHandler != null) {
         this.chestHandler.invalidate();
      }

   }
}
