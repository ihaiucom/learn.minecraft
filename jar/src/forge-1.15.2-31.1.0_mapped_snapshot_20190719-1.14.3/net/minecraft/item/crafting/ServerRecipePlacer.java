package net.minecraft.item.crafting;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.ints.IntListIterator;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.inventory.container.RecipeBookContainer;
import net.minecraft.inventory.container.Slot;
import net.minecraft.inventory.container.WorkbenchContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.SPlaceGhostRecipePacket;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServerRecipePlacer<C extends IInventory> implements IRecipePlacer<Integer> {
   protected static final Logger LOGGER = LogManager.getLogger();
   protected final RecipeItemHelper recipeItemHelper = new RecipeItemHelper();
   protected PlayerInventory playerInventory;
   protected RecipeBookContainer<C> recipeBookContainer;

   public ServerRecipePlacer(RecipeBookContainer<C> p_i50752_1_) {
      this.recipeBookContainer = p_i50752_1_;
   }

   public void place(ServerPlayerEntity p_194327_1_, @Nullable IRecipe<C> p_194327_2_, boolean p_194327_3_) {
      if (p_194327_2_ != null && p_194327_1_.getRecipeBook().isUnlocked(p_194327_2_)) {
         this.playerInventory = p_194327_1_.inventory;
         if (this.func_194328_c() || p_194327_1_.isCreative()) {
            this.recipeItemHelper.clear();
            p_194327_1_.inventory.func_201571_a(this.recipeItemHelper);
            this.recipeBookContainer.func_201771_a(this.recipeItemHelper);
            if (this.recipeItemHelper.canCraft(p_194327_2_, (IntList)null)) {
               this.tryPlaceRecipe(p_194327_2_, p_194327_3_);
            } else {
               this.clear();
               p_194327_1_.connection.sendPacket(new SPlaceGhostRecipePacket(p_194327_1_.openContainer.windowId, p_194327_2_));
            }

            p_194327_1_.inventory.markDirty();
         }
      }
   }

   protected void clear() {
      for(int lvt_1_1_ = 0; lvt_1_1_ < this.recipeBookContainer.getWidth() * this.recipeBookContainer.getHeight() + 1; ++lvt_1_1_) {
         if (lvt_1_1_ != this.recipeBookContainer.getOutputSlot() || !(this.recipeBookContainer instanceof WorkbenchContainer) && !(this.recipeBookContainer instanceof PlayerContainer)) {
            this.giveToPlayer(lvt_1_1_);
         }
      }

      this.recipeBookContainer.clear();
   }

   protected void giveToPlayer(int p_201510_1_) {
      ItemStack lvt_2_1_ = this.recipeBookContainer.getSlot(p_201510_1_).getStack();
      if (!lvt_2_1_.isEmpty()) {
         for(; lvt_2_1_.getCount() > 0; this.recipeBookContainer.getSlot(p_201510_1_).decrStackSize(1)) {
            int lvt_3_1_ = this.playerInventory.storeItemStack(lvt_2_1_);
            if (lvt_3_1_ == -1) {
               lvt_3_1_ = this.playerInventory.getFirstEmptyStack();
            }

            ItemStack lvt_4_1_ = lvt_2_1_.copy();
            lvt_4_1_.setCount(1);
            if (!this.playerInventory.add(lvt_3_1_, lvt_4_1_)) {
               LOGGER.error("Can't find any space for item in the inventory");
            }
         }

      }
   }

   protected void tryPlaceRecipe(IRecipe<C> p_201508_1_, boolean p_201508_2_) {
      boolean lvt_3_1_ = this.recipeBookContainer.matches(p_201508_1_);
      int lvt_4_1_ = this.recipeItemHelper.getBiggestCraftableStack(p_201508_1_, (IntList)null);
      int lvt_5_1_;
      if (lvt_3_1_) {
         for(lvt_5_1_ = 0; lvt_5_1_ < this.recipeBookContainer.getHeight() * this.recipeBookContainer.getWidth() + 1; ++lvt_5_1_) {
            if (lvt_5_1_ != this.recipeBookContainer.getOutputSlot()) {
               ItemStack lvt_6_1_ = this.recipeBookContainer.getSlot(lvt_5_1_).getStack();
               if (!lvt_6_1_.isEmpty() && Math.min(lvt_4_1_, lvt_6_1_.getMaxStackSize()) < lvt_6_1_.getCount() + 1) {
                  return;
               }
            }
         }
      }

      lvt_5_1_ = this.getMaxAmount(p_201508_2_, lvt_4_1_, lvt_3_1_);
      IntList lvt_6_2_ = new IntArrayList();
      if (this.recipeItemHelper.canCraft(p_201508_1_, lvt_6_2_, lvt_5_1_)) {
         int lvt_7_1_ = lvt_5_1_;
         IntListIterator var8 = lvt_6_2_.iterator();

         while(var8.hasNext()) {
            int lvt_9_1_ = (Integer)var8.next();
            int lvt_10_1_ = RecipeItemHelper.unpack(lvt_9_1_).getMaxStackSize();
            if (lvt_10_1_ < lvt_7_1_) {
               lvt_7_1_ = lvt_10_1_;
            }
         }

         if (this.recipeItemHelper.canCraft(p_201508_1_, lvt_6_2_, lvt_7_1_)) {
            this.clear();
            this.placeRecipe(this.recipeBookContainer.getWidth(), this.recipeBookContainer.getHeight(), this.recipeBookContainer.getOutputSlot(), p_201508_1_, lvt_6_2_.iterator(), lvt_7_1_);
         }
      }

   }

   public void setSlotContents(Iterator<Integer> p_201500_1_, int p_201500_2_, int p_201500_3_, int p_201500_4_, int p_201500_5_) {
      Slot lvt_6_1_ = this.recipeBookContainer.getSlot(p_201500_2_);
      ItemStack lvt_7_1_ = RecipeItemHelper.unpack((Integer)p_201500_1_.next());
      if (!lvt_7_1_.isEmpty()) {
         for(int lvt_8_1_ = 0; lvt_8_1_ < p_201500_3_; ++lvt_8_1_) {
            this.consumeIngredient(lvt_6_1_, lvt_7_1_);
         }
      }

   }

   protected int getMaxAmount(boolean p_201509_1_, int p_201509_2_, boolean p_201509_3_) {
      int lvt_4_1_ = 1;
      if (p_201509_1_) {
         lvt_4_1_ = p_201509_2_;
      } else if (p_201509_3_) {
         lvt_4_1_ = 64;

         for(int lvt_5_1_ = 0; lvt_5_1_ < this.recipeBookContainer.getWidth() * this.recipeBookContainer.getHeight() + 1; ++lvt_5_1_) {
            if (lvt_5_1_ != this.recipeBookContainer.getOutputSlot()) {
               ItemStack lvt_6_1_ = this.recipeBookContainer.getSlot(lvt_5_1_).getStack();
               if (!lvt_6_1_.isEmpty() && lvt_4_1_ > lvt_6_1_.getCount()) {
                  lvt_4_1_ = lvt_6_1_.getCount();
               }
            }
         }

         if (lvt_4_1_ < 64) {
            ++lvt_4_1_;
         }
      }

      return lvt_4_1_;
   }

   protected void consumeIngredient(Slot p_194325_1_, ItemStack p_194325_2_) {
      int lvt_3_1_ = this.playerInventory.findSlotMatchingUnusedItem(p_194325_2_);
      if (lvt_3_1_ != -1) {
         ItemStack lvt_4_1_ = this.playerInventory.getStackInSlot(lvt_3_1_).copy();
         if (!lvt_4_1_.isEmpty()) {
            if (lvt_4_1_.getCount() > 1) {
               this.playerInventory.decrStackSize(lvt_3_1_, 1);
            } else {
               this.playerInventory.removeStackFromSlot(lvt_3_1_);
            }

            lvt_4_1_.setCount(1);
            if (p_194325_1_.getStack().isEmpty()) {
               p_194325_1_.putStack(lvt_4_1_);
            } else {
               p_194325_1_.getStack().grow(1);
            }

         }
      }
   }

   private boolean func_194328_c() {
      List<ItemStack> lvt_1_1_ = Lists.newArrayList();
      int lvt_2_1_ = this.getEmptyPlayerSlots();

      for(int lvt_3_1_ = 0; lvt_3_1_ < this.recipeBookContainer.getWidth() * this.recipeBookContainer.getHeight() + 1; ++lvt_3_1_) {
         if (lvt_3_1_ != this.recipeBookContainer.getOutputSlot()) {
            ItemStack lvt_4_1_ = this.recipeBookContainer.getSlot(lvt_3_1_).getStack().copy();
            if (!lvt_4_1_.isEmpty()) {
               int lvt_5_1_ = this.playerInventory.storeItemStack(lvt_4_1_);
               if (lvt_5_1_ == -1 && lvt_1_1_.size() <= lvt_2_1_) {
                  Iterator var6 = lvt_1_1_.iterator();

                  while(var6.hasNext()) {
                     ItemStack lvt_7_1_ = (ItemStack)var6.next();
                     if (lvt_7_1_.isItemEqual(lvt_4_1_) && lvt_7_1_.getCount() != lvt_7_1_.getMaxStackSize() && lvt_7_1_.getCount() + lvt_4_1_.getCount() <= lvt_7_1_.getMaxStackSize()) {
                        lvt_7_1_.grow(lvt_4_1_.getCount());
                        lvt_4_1_.setCount(0);
                        break;
                     }
                  }

                  if (!lvt_4_1_.isEmpty()) {
                     if (lvt_1_1_.size() >= lvt_2_1_) {
                        return false;
                     }

                     lvt_1_1_.add(lvt_4_1_);
                  }
               } else if (lvt_5_1_ == -1) {
                  return false;
               }
            }
         }
      }

      return true;
   }

   private int getEmptyPlayerSlots() {
      int lvt_1_1_ = 0;
      Iterator var2 = this.playerInventory.mainInventory.iterator();

      while(var2.hasNext()) {
         ItemStack lvt_3_1_ = (ItemStack)var2.next();
         if (lvt_3_1_.isEmpty()) {
            ++lvt_1_1_;
         }
      }

      return lvt_1_1_;
   }
}
