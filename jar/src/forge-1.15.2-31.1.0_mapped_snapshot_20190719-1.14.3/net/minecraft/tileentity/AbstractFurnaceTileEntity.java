package net.minecraft.tileentity;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.block.AbstractFurnaceBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.item.ExperienceOrbEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IRecipeHelperPopulator;
import net.minecraft.inventory.IRecipeHolder;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.AbstractCookingRecipe;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.RecipeItemHelper;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.Direction;
import net.minecraft.util.IIntArray;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.SidedInvWrapper;

public abstract class AbstractFurnaceTileEntity extends LockableTileEntity implements ISidedInventory, IRecipeHolder, IRecipeHelperPopulator, ITickableTileEntity {
   private static final int[] SLOTS_UP = new int[]{0};
   private static final int[] SLOTS_DOWN = new int[]{2, 1};
   private static final int[] SLOTS_HORIZONTAL = new int[]{1};
   protected NonNullList<ItemStack> items;
   private int burnTime;
   private int recipesUsed;
   private int cookTime;
   private int cookTimeTotal;
   protected final IIntArray furnaceData;
   private final Map<ResourceLocation, Integer> field_214022_n;
   protected final IRecipeType<? extends AbstractCookingRecipe> recipeType;
   LazyOptional<? extends IItemHandler>[] handlers;

   protected AbstractFurnaceTileEntity(TileEntityType<?> p_i49964_1_, IRecipeType<? extends AbstractCookingRecipe> p_i49964_2_) {
      super(p_i49964_1_);
      this.items = NonNullList.withSize(3, ItemStack.EMPTY);
      this.furnaceData = new IIntArray() {
         public int get(int p_221476_1_) {
            switch(p_221476_1_) {
            case 0:
               return AbstractFurnaceTileEntity.this.burnTime;
            case 1:
               return AbstractFurnaceTileEntity.this.recipesUsed;
            case 2:
               return AbstractFurnaceTileEntity.this.cookTime;
            case 3:
               return AbstractFurnaceTileEntity.this.cookTimeTotal;
            default:
               return 0;
            }
         }

         public void set(int p_221477_1_, int p_221477_2_) {
            switch(p_221477_1_) {
            case 0:
               AbstractFurnaceTileEntity.this.burnTime = p_221477_2_;
               break;
            case 1:
               AbstractFurnaceTileEntity.this.recipesUsed = p_221477_2_;
               break;
            case 2:
               AbstractFurnaceTileEntity.this.cookTime = p_221477_2_;
               break;
            case 3:
               AbstractFurnaceTileEntity.this.cookTimeTotal = p_221477_2_;
            }

         }

         public int size() {
            return 4;
         }
      };
      this.field_214022_n = Maps.newHashMap();
      this.handlers = SidedInvWrapper.create(this, Direction.UP, Direction.DOWN, Direction.NORTH);
      this.recipeType = p_i49964_2_;
   }

   /** @deprecated */
   @Deprecated
   public static Map<Item, Integer> getBurnTimes() {
      Map<Item, Integer> map = Maps.newLinkedHashMap();
      addItemBurnTime(map, Items.LAVA_BUCKET, 20000);
      addItemBurnTime(map, Blocks.COAL_BLOCK, 16000);
      addItemBurnTime(map, Items.BLAZE_ROD, 2400);
      addItemBurnTime(map, Items.COAL, 1600);
      addItemBurnTime(map, Items.CHARCOAL, 1600);
      addItemTagBurnTime(map, ItemTags.LOGS, 300);
      addItemTagBurnTime(map, ItemTags.PLANKS, 300);
      addItemTagBurnTime(map, ItemTags.WOODEN_STAIRS, 300);
      addItemTagBurnTime(map, ItemTags.WOODEN_SLABS, 150);
      addItemTagBurnTime(map, ItemTags.WOODEN_TRAPDOORS, 300);
      addItemTagBurnTime(map, ItemTags.WOODEN_PRESSURE_PLATES, 300);
      addItemTagBurnTime(map, Tags.Items.FENCES_WOODEN, 300);
      addItemTagBurnTime(map, Tags.Items.FENCE_GATES_WOODEN, 300);
      addItemBurnTime(map, Blocks.NOTE_BLOCK, 300);
      addItemBurnTime(map, Blocks.BOOKSHELF, 300);
      addItemBurnTime(map, Blocks.LECTERN, 300);
      addItemBurnTime(map, Blocks.JUKEBOX, 300);
      addItemBurnTime(map, Blocks.CHEST, 300);
      addItemBurnTime(map, Blocks.TRAPPED_CHEST, 300);
      addItemBurnTime(map, Blocks.CRAFTING_TABLE, 300);
      addItemBurnTime(map, Blocks.DAYLIGHT_DETECTOR, 300);
      addItemTagBurnTime(map, ItemTags.BANNERS, 300);
      addItemBurnTime(map, Items.BOW, 300);
      addItemBurnTime(map, Items.FISHING_ROD, 300);
      addItemBurnTime(map, Blocks.LADDER, 300);
      addItemTagBurnTime(map, ItemTags.SIGNS, 200);
      addItemBurnTime(map, Items.WOODEN_SHOVEL, 200);
      addItemBurnTime(map, Items.WOODEN_SWORD, 200);
      addItemBurnTime(map, Items.WOODEN_HOE, 200);
      addItemBurnTime(map, Items.WOODEN_AXE, 200);
      addItemBurnTime(map, Items.WOODEN_PICKAXE, 200);
      addItemTagBurnTime(map, ItemTags.WOODEN_DOORS, 200);
      addItemTagBurnTime(map, ItemTags.BOATS, 1200);
      addItemTagBurnTime(map, ItemTags.WOOL, 100);
      addItemTagBurnTime(map, ItemTags.WOODEN_BUTTONS, 100);
      addItemBurnTime(map, Items.STICK, 100);
      addItemTagBurnTime(map, ItemTags.SAPLINGS, 100);
      addItemBurnTime(map, Items.BOWL, 100);
      addItemTagBurnTime(map, ItemTags.CARPETS, 67);
      addItemBurnTime(map, Blocks.DRIED_KELP_BLOCK, 4001);
      addItemBurnTime(map, Items.CROSSBOW, 300);
      addItemBurnTime(map, Blocks.BAMBOO, 50);
      addItemBurnTime(map, Blocks.DEAD_BUSH, 100);
      addItemBurnTime(map, Blocks.SCAFFOLDING, 400);
      addItemBurnTime(map, Blocks.LOOM, 300);
      addItemBurnTime(map, Blocks.BARREL, 300);
      addItemBurnTime(map, Blocks.CARTOGRAPHY_TABLE, 300);
      addItemBurnTime(map, Blocks.FLETCHING_TABLE, 300);
      addItemBurnTime(map, Blocks.SMITHING_TABLE, 300);
      addItemBurnTime(map, Blocks.COMPOSTER, 300);
      return map;
   }

   private static void addItemTagBurnTime(Map<Item, Integer> p_213992_0_, Tag<Item> p_213992_1_, int p_213992_2_) {
      Iterator var3 = p_213992_1_.getAllElements().iterator();

      while(var3.hasNext()) {
         Item item = (Item)var3.next();
         p_213992_0_.put(item, p_213992_2_);
      }

   }

   private static void addItemBurnTime(Map<Item, Integer> p_213996_0_, IItemProvider p_213996_1_, int p_213996_2_) {
      p_213996_0_.put(p_213996_1_.asItem(), p_213996_2_);
   }

   private boolean isBurning() {
      return this.burnTime > 0;
   }

   public void read(CompoundNBT p_145839_1_) {
      super.read(p_145839_1_);
      this.items = NonNullList.withSize(this.getSizeInventory(), ItemStack.EMPTY);
      ItemStackHelper.loadAllItems(p_145839_1_, this.items);
      this.burnTime = p_145839_1_.getInt("BurnTime");
      this.cookTime = p_145839_1_.getInt("CookTime");
      this.cookTimeTotal = p_145839_1_.getInt("CookTimeTotal");
      this.recipesUsed = this.getBurnTime((ItemStack)this.items.get(1));
      int i = p_145839_1_.getShort("RecipesUsedSize");

      for(int j = 0; j < i; ++j) {
         ResourceLocation resourcelocation = new ResourceLocation(p_145839_1_.getString("RecipeLocation" + j));
         int k = p_145839_1_.getInt("RecipeAmount" + j);
         this.field_214022_n.put(resourcelocation, k);
      }

   }

   public CompoundNBT write(CompoundNBT p_189515_1_) {
      super.write(p_189515_1_);
      p_189515_1_.putInt("BurnTime", this.burnTime);
      p_189515_1_.putInt("CookTime", this.cookTime);
      p_189515_1_.putInt("CookTimeTotal", this.cookTimeTotal);
      ItemStackHelper.saveAllItems(p_189515_1_, this.items);
      p_189515_1_.putShort("RecipesUsedSize", (short)this.field_214022_n.size());
      int i = 0;

      for(Iterator var3 = this.field_214022_n.entrySet().iterator(); var3.hasNext(); ++i) {
         Entry<ResourceLocation, Integer> entry = (Entry)var3.next();
         p_189515_1_.putString("RecipeLocation" + i, ((ResourceLocation)entry.getKey()).toString());
         p_189515_1_.putInt("RecipeAmount" + i, (Integer)entry.getValue());
      }

      return p_189515_1_;
   }

   public void tick() {
      boolean flag = this.isBurning();
      boolean flag1 = false;
      if (this.isBurning()) {
         --this.burnTime;
      }

      if (!this.world.isRemote) {
         ItemStack itemstack = (ItemStack)this.items.get(1);
         if (!this.isBurning() && (itemstack.isEmpty() || ((ItemStack)this.items.get(0)).isEmpty())) {
            if (!this.isBurning() && this.cookTime > 0) {
               this.cookTime = MathHelper.clamp(this.cookTime - 2, 0, this.cookTimeTotal);
            }
         } else {
            IRecipe<?> irecipe = (IRecipe)this.world.getRecipeManager().getRecipe(this.recipeType, this, this.world).orElse((Object)null);
            if (!this.isBurning() && this.canSmelt(irecipe)) {
               this.burnTime = this.getBurnTime(itemstack);
               this.recipesUsed = this.burnTime;
               if (this.isBurning()) {
                  flag1 = true;
                  if (itemstack.hasContainerItem()) {
                     this.items.set(1, itemstack.getContainerItem());
                  } else if (!itemstack.isEmpty()) {
                     Item item = itemstack.getItem();
                     itemstack.shrink(1);
                     if (itemstack.isEmpty()) {
                        this.items.set(1, itemstack.getContainerItem());
                     }
                  }
               }
            }

            if (this.isBurning() && this.canSmelt(irecipe)) {
               ++this.cookTime;
               if (this.cookTime == this.cookTimeTotal) {
                  this.cookTime = 0;
                  this.cookTimeTotal = this.func_214005_h();
                  this.func_214007_c(irecipe);
                  flag1 = true;
               }
            } else {
               this.cookTime = 0;
            }
         }

         if (flag != this.isBurning()) {
            flag1 = true;
            this.world.setBlockState(this.pos, (BlockState)this.world.getBlockState(this.pos).with(AbstractFurnaceBlock.LIT, this.isBurning()), 3);
         }
      }

      if (flag1) {
         this.markDirty();
      }

   }

   protected boolean canSmelt(@Nullable IRecipe<?> p_214008_1_) {
      if (!((ItemStack)this.items.get(0)).isEmpty() && p_214008_1_ != null) {
         ItemStack itemstack = p_214008_1_.getRecipeOutput();
         if (itemstack.isEmpty()) {
            return false;
         } else {
            ItemStack itemstack1 = (ItemStack)this.items.get(2);
            if (itemstack1.isEmpty()) {
               return true;
            } else if (!itemstack1.isItemEqual(itemstack)) {
               return false;
            } else if (itemstack1.getCount() + itemstack.getCount() <= this.getInventoryStackLimit() && itemstack1.getCount() + itemstack.getCount() <= itemstack1.getMaxStackSize()) {
               return true;
            } else {
               return itemstack1.getCount() + itemstack.getCount() <= itemstack.getMaxStackSize();
            }
         }
      } else {
         return false;
      }
   }

   private void func_214007_c(@Nullable IRecipe<?> p_214007_1_) {
      if (p_214007_1_ != null && this.canSmelt(p_214007_1_)) {
         ItemStack itemstack = (ItemStack)this.items.get(0);
         ItemStack itemstack1 = p_214007_1_.getRecipeOutput();
         ItemStack itemstack2 = (ItemStack)this.items.get(2);
         if (itemstack2.isEmpty()) {
            this.items.set(2, itemstack1.copy());
         } else if (itemstack2.getItem() == itemstack1.getItem()) {
            itemstack2.grow(itemstack1.getCount());
         }

         if (!this.world.isRemote) {
            this.setRecipeUsed(p_214007_1_);
         }

         if (itemstack.getItem() == Blocks.WET_SPONGE.asItem() && !((ItemStack)this.items.get(1)).isEmpty() && ((ItemStack)this.items.get(1)).getItem() == Items.BUCKET) {
            this.items.set(1, new ItemStack(Items.WATER_BUCKET));
         }

         itemstack.shrink(1);
      }

   }

   protected int getBurnTime(ItemStack p_213997_1_) {
      if (p_213997_1_.isEmpty()) {
         return 0;
      } else {
         Item item = p_213997_1_.getItem();
         return ForgeHooks.getBurnTime(p_213997_1_);
      }
   }

   protected int func_214005_h() {
      return (Integer)this.world.getRecipeManager().getRecipe(this.recipeType, this, this.world).map(AbstractCookingRecipe::getCookTime).orElse(200);
   }

   public static boolean isFuel(ItemStack p_213991_0_) {
      return ForgeHooks.getBurnTime(p_213991_0_) > 0;
   }

   public int[] getSlotsForFace(Direction p_180463_1_) {
      if (p_180463_1_ == Direction.DOWN) {
         return SLOTS_DOWN;
      } else {
         return p_180463_1_ == Direction.UP ? SLOTS_UP : SLOTS_HORIZONTAL;
      }
   }

   public boolean canInsertItem(int p_180462_1_, ItemStack p_180462_2_, @Nullable Direction p_180462_3_) {
      return this.isItemValidForSlot(p_180462_1_, p_180462_2_);
   }

   public boolean canExtractItem(int p_180461_1_, ItemStack p_180461_2_, Direction p_180461_3_) {
      if (p_180461_3_ == Direction.DOWN && p_180461_1_ == 1) {
         Item item = p_180461_2_.getItem();
         if (item != Items.WATER_BUCKET && item != Items.BUCKET) {
            return false;
         }
      }

      return true;
   }

   public int getSizeInventory() {
      return this.items.size();
   }

   public boolean isEmpty() {
      Iterator var1 = this.items.iterator();

      ItemStack itemstack;
      do {
         if (!var1.hasNext()) {
            return true;
         }

         itemstack = (ItemStack)var1.next();
      } while(itemstack.isEmpty());

      return false;
   }

   public ItemStack getStackInSlot(int p_70301_1_) {
      return (ItemStack)this.items.get(p_70301_1_);
   }

   public ItemStack decrStackSize(int p_70298_1_, int p_70298_2_) {
      return ItemStackHelper.getAndSplit(this.items, p_70298_1_, p_70298_2_);
   }

   public ItemStack removeStackFromSlot(int p_70304_1_) {
      return ItemStackHelper.getAndRemove(this.items, p_70304_1_);
   }

   public void setInventorySlotContents(int p_70299_1_, ItemStack p_70299_2_) {
      ItemStack itemstack = (ItemStack)this.items.get(p_70299_1_);
      boolean flag = !p_70299_2_.isEmpty() && p_70299_2_.isItemEqual(itemstack) && ItemStack.areItemStackTagsEqual(p_70299_2_, itemstack);
      this.items.set(p_70299_1_, p_70299_2_);
      if (p_70299_2_.getCount() > this.getInventoryStackLimit()) {
         p_70299_2_.setCount(this.getInventoryStackLimit());
      }

      if (p_70299_1_ == 0 && !flag) {
         this.cookTimeTotal = this.func_214005_h();
         this.cookTime = 0;
         this.markDirty();
      }

   }

   public boolean isUsableByPlayer(PlayerEntity p_70300_1_) {
      if (this.world.getTileEntity(this.pos) != this) {
         return false;
      } else {
         return p_70300_1_.getDistanceSq((double)this.pos.getX() + 0.5D, (double)this.pos.getY() + 0.5D, (double)this.pos.getZ() + 0.5D) <= 64.0D;
      }
   }

   public boolean isItemValidForSlot(int p_94041_1_, ItemStack p_94041_2_) {
      if (p_94041_1_ == 2) {
         return false;
      } else if (p_94041_1_ != 1) {
         return true;
      } else {
         ItemStack itemstack = (ItemStack)this.items.get(1);
         return isFuel(p_94041_2_) || p_94041_2_.getItem() == Items.BUCKET && itemstack.getItem() != Items.BUCKET;
      }
   }

   public void clear() {
      this.items.clear();
   }

   public void setRecipeUsed(@Nullable IRecipe<?> p_193056_1_) {
      if (p_193056_1_ != null) {
         this.field_214022_n.compute(p_193056_1_.getId(), (p_lambda$setRecipeUsed$0_0_, p_lambda$setRecipeUsed$0_1_) -> {
            return 1 + (p_lambda$setRecipeUsed$0_1_ == null ? 0 : p_lambda$setRecipeUsed$0_1_);
         });
      }

   }

   @Nullable
   public IRecipe<?> getRecipeUsed() {
      return null;
   }

   public void onCrafting(PlayerEntity p_201560_1_) {
   }

   public void func_213995_d(PlayerEntity p_213995_1_) {
      List<IRecipe<?>> list = Lists.newArrayList();
      Iterator var3 = this.field_214022_n.entrySet().iterator();

      while(var3.hasNext()) {
         Entry<ResourceLocation, Integer> entry = (Entry)var3.next();
         p_213995_1_.world.getRecipeManager().getRecipe((ResourceLocation)entry.getKey()).ifPresent((p_lambda$func_213995_d$1_3_) -> {
            list.add(p_lambda$func_213995_d$1_3_);
            func_214003_a(p_213995_1_, (Integer)entry.getValue(), ((AbstractCookingRecipe)p_lambda$func_213995_d$1_3_).getExperience());
         });
      }

      p_213995_1_.unlockRecipes((Collection)list);
      this.field_214022_n.clear();
   }

   private static void func_214003_a(PlayerEntity p_214003_0_, int p_214003_1_, float p_214003_2_) {
      int i;
      if (p_214003_2_ == 0.0F) {
         p_214003_1_ = 0;
      } else if (p_214003_2_ < 1.0F) {
         i = MathHelper.floor((float)p_214003_1_ * p_214003_2_);
         if (i < MathHelper.ceil((float)p_214003_1_ * p_214003_2_) && Math.random() < (double)((float)p_214003_1_ * p_214003_2_ - (float)i)) {
            ++i;
         }

         p_214003_1_ = i;
      }

      while(p_214003_1_ > 0) {
         i = ExperienceOrbEntity.getXPSplit(p_214003_1_);
         p_214003_1_ -= i;
         p_214003_0_.world.addEntity(new ExperienceOrbEntity(p_214003_0_.world, p_214003_0_.func_226277_ct_(), p_214003_0_.func_226278_cu_() + 0.5D, p_214003_0_.func_226281_cx_() + 0.5D, i));
      }

   }

   public void fillStackedContents(RecipeItemHelper p_194018_1_) {
      Iterator var2 = this.items.iterator();

      while(var2.hasNext()) {
         ItemStack itemstack = (ItemStack)var2.next();
         p_194018_1_.accountStack(itemstack);
      }

   }

   public <T> LazyOptional<T> getCapability(Capability<T> p_getCapability_1_, @Nullable Direction p_getCapability_2_) {
      if (!this.removed && p_getCapability_2_ != null && p_getCapability_1_ == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
         if (p_getCapability_2_ == Direction.UP) {
            return this.handlers[0].cast();
         } else {
            return p_getCapability_2_ == Direction.DOWN ? this.handlers[1].cast() : this.handlers[2].cast();
         }
      } else {
         return super.getCapability(p_getCapability_1_, p_getCapability_2_);
      }
   }

   public void remove() {
      super.remove();

      for(int x = 0; x < this.handlers.length; ++x) {
         this.handlers[x].invalidate();
      }

   }
}
