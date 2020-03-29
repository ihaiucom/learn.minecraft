package net.minecraft.tileentity;

import java.util.Optional;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.CampfireBlock;
import net.minecraft.inventory.IClearable;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CampfireCookingRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class CampfireTileEntity extends TileEntity implements IClearable, ITickableTileEntity {
   private final NonNullList<ItemStack> inventory;
   private final int[] cookingTimes;
   private final int[] cookingTotalTimes;

   public CampfireTileEntity() {
      super(TileEntityType.CAMPFIRE);
      this.inventory = NonNullList.withSize(4, ItemStack.EMPTY);
      this.cookingTimes = new int[4];
      this.cookingTotalTimes = new int[4];
   }

   public void tick() {
      boolean lvt_1_1_ = (Boolean)this.getBlockState().get(CampfireBlock.LIT);
      boolean lvt_2_1_ = this.world.isRemote;
      if (lvt_2_1_) {
         if (lvt_1_1_) {
            this.addParticles();
         }

      } else {
         if (lvt_1_1_) {
            this.cookAndDrop();
         } else {
            for(int lvt_3_1_ = 0; lvt_3_1_ < this.inventory.size(); ++lvt_3_1_) {
               if (this.cookingTimes[lvt_3_1_] > 0) {
                  this.cookingTimes[lvt_3_1_] = MathHelper.clamp(this.cookingTimes[lvt_3_1_] - 2, 0, this.cookingTotalTimes[lvt_3_1_]);
               }
            }
         }

      }
   }

   private void cookAndDrop() {
      for(int lvt_1_1_ = 0; lvt_1_1_ < this.inventory.size(); ++lvt_1_1_) {
         ItemStack lvt_2_1_ = (ItemStack)this.inventory.get(lvt_1_1_);
         if (!lvt_2_1_.isEmpty()) {
            int var10002 = this.cookingTimes[lvt_1_1_]++;
            if (this.cookingTimes[lvt_1_1_] >= this.cookingTotalTimes[lvt_1_1_]) {
               IInventory lvt_3_1_ = new Inventory(new ItemStack[]{lvt_2_1_});
               ItemStack lvt_4_1_ = (ItemStack)this.world.getRecipeManager().getRecipe(IRecipeType.CAMPFIRE_COOKING, lvt_3_1_, this.world).map((p_213979_1_) -> {
                  return p_213979_1_.getCraftingResult(lvt_3_1_);
               }).orElse(lvt_2_1_);
               BlockPos lvt_5_1_ = this.getPos();
               InventoryHelper.spawnItemStack(this.world, (double)lvt_5_1_.getX(), (double)lvt_5_1_.getY(), (double)lvt_5_1_.getZ(), lvt_4_1_);
               this.inventory.set(lvt_1_1_, ItemStack.EMPTY);
               this.func_213981_s();
            }
         }
      }

   }

   private void addParticles() {
      World lvt_1_1_ = this.getWorld();
      if (lvt_1_1_ != null) {
         BlockPos lvt_2_1_ = this.getPos();
         Random lvt_3_1_ = lvt_1_1_.rand;
         int lvt_4_2_;
         if (lvt_3_1_.nextFloat() < 0.11F) {
            for(lvt_4_2_ = 0; lvt_4_2_ < lvt_3_1_.nextInt(2) + 2; ++lvt_4_2_) {
               CampfireBlock.func_220098_a(lvt_1_1_, lvt_2_1_, (Boolean)this.getBlockState().get(CampfireBlock.SIGNAL_FIRE), false);
            }
         }

         lvt_4_2_ = ((Direction)this.getBlockState().get(CampfireBlock.FACING)).getHorizontalIndex();

         for(int lvt_5_1_ = 0; lvt_5_1_ < this.inventory.size(); ++lvt_5_1_) {
            if (!((ItemStack)this.inventory.get(lvt_5_1_)).isEmpty() && lvt_3_1_.nextFloat() < 0.2F) {
               Direction lvt_6_1_ = Direction.byHorizontalIndex(Math.floorMod(lvt_5_1_ + lvt_4_2_, 4));
               float lvt_7_1_ = 0.3125F;
               double lvt_8_1_ = (double)lvt_2_1_.getX() + 0.5D - (double)((float)lvt_6_1_.getXOffset() * 0.3125F) + (double)((float)lvt_6_1_.rotateY().getXOffset() * 0.3125F);
               double lvt_10_1_ = (double)lvt_2_1_.getY() + 0.5D;
               double lvt_12_1_ = (double)lvt_2_1_.getZ() + 0.5D - (double)((float)lvt_6_1_.getZOffset() * 0.3125F) + (double)((float)lvt_6_1_.rotateY().getZOffset() * 0.3125F);

               for(int lvt_14_1_ = 0; lvt_14_1_ < 4; ++lvt_14_1_) {
                  lvt_1_1_.addParticle(ParticleTypes.SMOKE, lvt_8_1_, lvt_10_1_, lvt_12_1_, 0.0D, 5.0E-4D, 0.0D);
               }
            }
         }

      }
   }

   public NonNullList<ItemStack> getInventory() {
      return this.inventory;
   }

   public void read(CompoundNBT p_145839_1_) {
      super.read(p_145839_1_);
      this.inventory.clear();
      ItemStackHelper.loadAllItems(p_145839_1_, this.inventory);
      int[] lvt_2_2_;
      if (p_145839_1_.contains("CookingTimes", 11)) {
         lvt_2_2_ = p_145839_1_.getIntArray("CookingTimes");
         System.arraycopy(lvt_2_2_, 0, this.cookingTimes, 0, Math.min(this.cookingTotalTimes.length, lvt_2_2_.length));
      }

      if (p_145839_1_.contains("CookingTotalTimes", 11)) {
         lvt_2_2_ = p_145839_1_.getIntArray("CookingTotalTimes");
         System.arraycopy(lvt_2_2_, 0, this.cookingTotalTimes, 0, Math.min(this.cookingTotalTimes.length, lvt_2_2_.length));
      }

   }

   public CompoundNBT write(CompoundNBT p_189515_1_) {
      this.writeItems(p_189515_1_);
      p_189515_1_.putIntArray("CookingTimes", this.cookingTimes);
      p_189515_1_.putIntArray("CookingTotalTimes", this.cookingTotalTimes);
      return p_189515_1_;
   }

   private CompoundNBT writeItems(CompoundNBT p_213983_1_) {
      super.write(p_213983_1_);
      ItemStackHelper.saveAllItems(p_213983_1_, this.inventory, true);
      return p_213983_1_;
   }

   @Nullable
   public SUpdateTileEntityPacket getUpdatePacket() {
      return new SUpdateTileEntityPacket(this.pos, 13, this.getUpdateTag());
   }

   public CompoundNBT getUpdateTag() {
      return this.writeItems(new CompoundNBT());
   }

   public Optional<CampfireCookingRecipe> findMatchingRecipe(ItemStack p_213980_1_) {
      return this.inventory.stream().noneMatch(ItemStack::isEmpty) ? Optional.empty() : this.world.getRecipeManager().getRecipe(IRecipeType.CAMPFIRE_COOKING, new Inventory(new ItemStack[]{p_213980_1_}), this.world);
   }

   public boolean addItem(ItemStack p_213984_1_, int p_213984_2_) {
      for(int lvt_3_1_ = 0; lvt_3_1_ < this.inventory.size(); ++lvt_3_1_) {
         ItemStack lvt_4_1_ = (ItemStack)this.inventory.get(lvt_3_1_);
         if (lvt_4_1_.isEmpty()) {
            this.cookingTotalTimes[lvt_3_1_] = p_213984_2_;
            this.cookingTimes[lvt_3_1_] = 0;
            this.inventory.set(lvt_3_1_, p_213984_1_.split(1));
            this.func_213981_s();
            return true;
         }
      }

      return false;
   }

   private void func_213981_s() {
      this.markDirty();
      this.getWorld().notifyBlockUpdate(this.getPos(), this.getBlockState(), this.getBlockState(), 3);
   }

   public void clear() {
      this.inventory.clear();
   }

   public void func_213986_d() {
      if (!this.getWorld().isRemote) {
         InventoryHelper.dropItems(this.getWorld(), this.getPos(), this.getInventory());
      }

      this.func_213981_s();
   }
}
