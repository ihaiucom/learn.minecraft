package net.minecraft.item.crafting;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntAVLTreeSet;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntCollection;
import it.unimi.dsi.fastutil.ints.IntIterator;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.ints.IntListIterator;
import java.util.BitSet;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.registry.Registry;

public class RecipeItemHelper {
   public final Int2IntMap itemToCount = new Int2IntOpenHashMap();

   public void accountPlainStack(ItemStack p_195932_1_) {
      if (!p_195932_1_.isDamaged() && !p_195932_1_.isEnchanted() && !p_195932_1_.hasDisplayName()) {
         this.accountStack(p_195932_1_);
      }

   }

   public void accountStack(ItemStack p_194112_1_) {
      this.func_221264_a(p_194112_1_, 64);
   }

   public void func_221264_a(ItemStack p_221264_1_, int p_221264_2_) {
      if (!p_221264_1_.isEmpty()) {
         int lvt_3_1_ = pack(p_221264_1_);
         int lvt_4_1_ = Math.min(p_221264_2_, p_221264_1_.getCount());
         this.increment(lvt_3_1_, lvt_4_1_);
      }

   }

   public static int pack(ItemStack p_194113_0_) {
      return Registry.ITEM.getId(p_194113_0_.getItem());
   }

   private boolean containsItem(int p_194120_1_) {
      return this.itemToCount.get(p_194120_1_) > 0;
   }

   private int tryTake(int p_194122_1_, int p_194122_2_) {
      int lvt_3_1_ = this.itemToCount.get(p_194122_1_);
      if (lvt_3_1_ >= p_194122_2_) {
         this.itemToCount.put(p_194122_1_, lvt_3_1_ - p_194122_2_);
         return p_194122_1_;
      } else {
         return 0;
      }
   }

   private void increment(int p_194117_1_, int p_194117_2_) {
      this.itemToCount.put(p_194117_1_, this.itemToCount.get(p_194117_1_) + p_194117_2_);
   }

   public boolean canCraft(IRecipe<?> p_194116_1_, @Nullable IntList p_194116_2_) {
      return this.canCraft(p_194116_1_, p_194116_2_, 1);
   }

   public boolean canCraft(IRecipe<?> p_194118_1_, @Nullable IntList p_194118_2_, int p_194118_3_) {
      return (new RecipeItemHelper.RecipePicker(p_194118_1_)).tryPick(p_194118_3_, p_194118_2_);
   }

   public int getBiggestCraftableStack(IRecipe<?> p_194114_1_, @Nullable IntList p_194114_2_) {
      return this.getBiggestCraftableStack(p_194114_1_, Integer.MAX_VALUE, p_194114_2_);
   }

   public int getBiggestCraftableStack(IRecipe<?> p_194121_1_, int p_194121_2_, @Nullable IntList p_194121_3_) {
      return (new RecipeItemHelper.RecipePicker(p_194121_1_)).tryPickAll(p_194121_2_, p_194121_3_);
   }

   public static ItemStack unpack(int p_194115_0_) {
      return p_194115_0_ == 0 ? ItemStack.EMPTY : new ItemStack(Item.getItemById(p_194115_0_));
   }

   public void clear() {
      this.itemToCount.clear();
   }

   class RecipePicker {
      private final IRecipe<?> recipe;
      private final List<Ingredient> ingredients = Lists.newArrayList();
      private final int ingredientCount;
      private final int[] possessedIngredientStacks;
      private final int possessedIngredientStackCount;
      private final BitSet data;
      private final IntList path = new IntArrayList();

      public RecipePicker(IRecipe<?> p_i47608_2_) {
         this.recipe = p_i47608_2_;
         this.ingredients.addAll(p_i47608_2_.getIngredients());
         this.ingredients.removeIf(Ingredient::hasNoMatchingItems);
         this.ingredientCount = this.ingredients.size();
         this.possessedIngredientStacks = this.getUniqueAvailIngredientItems();
         this.possessedIngredientStackCount = this.possessedIngredientStacks.length;
         this.data = new BitSet(this.ingredientCount + this.possessedIngredientStackCount + this.ingredientCount + this.ingredientCount * this.possessedIngredientStackCount);

         for(int lvt_3_1_ = 0; lvt_3_1_ < this.ingredients.size(); ++lvt_3_1_) {
            IntList lvt_4_1_ = ((Ingredient)this.ingredients.get(lvt_3_1_)).getValidItemStacksPacked();

            for(int lvt_5_1_ = 0; lvt_5_1_ < this.possessedIngredientStackCount; ++lvt_5_1_) {
               if (lvt_4_1_.contains(this.possessedIngredientStacks[lvt_5_1_])) {
                  this.data.set(this.getIndex(true, lvt_5_1_, lvt_3_1_));
               }
            }
         }

      }

      public boolean tryPick(int p_194092_1_, @Nullable IntList p_194092_2_) {
         if (p_194092_1_ <= 0) {
            return true;
         } else {
            int lvt_3_1_;
            for(lvt_3_1_ = 0; this.dfs(p_194092_1_); ++lvt_3_1_) {
               RecipeItemHelper.this.tryTake(this.possessedIngredientStacks[this.path.getInt(0)], p_194092_1_);
               int lvt_4_1_ = this.path.size() - 1;
               this.setSatisfied(this.path.getInt(lvt_4_1_));

               for(int lvt_5_1_ = 0; lvt_5_1_ < lvt_4_1_; ++lvt_5_1_) {
                  this.toggleResidual((lvt_5_1_ & 1) == 0, this.path.get(lvt_5_1_), this.path.get(lvt_5_1_ + 1));
               }

               this.path.clear();
               this.data.clear(0, this.ingredientCount + this.possessedIngredientStackCount);
            }

            boolean lvt_4_2_ = lvt_3_1_ == this.ingredientCount;
            boolean lvt_5_2_ = lvt_4_2_ && p_194092_2_ != null;
            if (lvt_5_2_) {
               p_194092_2_.clear();
            }

            this.data.clear(0, this.ingredientCount + this.possessedIngredientStackCount + this.ingredientCount);
            int lvt_6_1_ = 0;
            List<Ingredient> lvt_7_1_ = this.recipe.getIngredients();

            for(int lvt_8_1_ = 0; lvt_8_1_ < lvt_7_1_.size(); ++lvt_8_1_) {
               if (lvt_5_2_ && ((Ingredient)lvt_7_1_.get(lvt_8_1_)).hasNoMatchingItems()) {
                  p_194092_2_.add(0);
               } else {
                  for(int lvt_9_1_ = 0; lvt_9_1_ < this.possessedIngredientStackCount; ++lvt_9_1_) {
                     if (this.hasResidual(false, lvt_6_1_, lvt_9_1_)) {
                        this.toggleResidual(true, lvt_9_1_, lvt_6_1_);
                        RecipeItemHelper.this.increment(this.possessedIngredientStacks[lvt_9_1_], p_194092_1_);
                        if (lvt_5_2_) {
                           p_194092_2_.add(this.possessedIngredientStacks[lvt_9_1_]);
                        }
                     }
                  }

                  ++lvt_6_1_;
               }
            }

            return lvt_4_2_;
         }
      }

      private int[] getUniqueAvailIngredientItems() {
         IntCollection lvt_1_1_ = new IntAVLTreeSet();
         Iterator var2 = this.ingredients.iterator();

         while(var2.hasNext()) {
            Ingredient lvt_3_1_ = (Ingredient)var2.next();
            lvt_1_1_.addAll(lvt_3_1_.getValidItemStacksPacked());
         }

         IntIterator lvt_2_1_ = lvt_1_1_.iterator();

         while(lvt_2_1_.hasNext()) {
            if (!RecipeItemHelper.this.containsItem(lvt_2_1_.nextInt())) {
               lvt_2_1_.remove();
            }
         }

         return lvt_1_1_.toIntArray();
      }

      private boolean dfs(int p_194098_1_) {
         int lvt_2_1_ = this.possessedIngredientStackCount;

         for(int lvt_3_1_ = 0; lvt_3_1_ < lvt_2_1_; ++lvt_3_1_) {
            if (RecipeItemHelper.this.itemToCount.get(this.possessedIngredientStacks[lvt_3_1_]) >= p_194098_1_) {
               this.visit(false, lvt_3_1_);

               while(!this.path.isEmpty()) {
                  int lvt_4_1_ = this.path.size();
                  boolean lvt_5_1_ = (lvt_4_1_ & 1) == 1;
                  int lvt_6_1_ = this.path.getInt(lvt_4_1_ - 1);
                  if (!lvt_5_1_ && !this.isSatisfied(lvt_6_1_)) {
                     break;
                  }

                  int lvt_7_1_ = lvt_5_1_ ? this.ingredientCount : lvt_2_1_;

                  int lvt_8_2_;
                  for(lvt_8_2_ = 0; lvt_8_2_ < lvt_7_1_; ++lvt_8_2_) {
                     if (!this.hasVisited(lvt_5_1_, lvt_8_2_) && this.hasConnection(lvt_5_1_, lvt_6_1_, lvt_8_2_) && this.hasResidual(lvt_5_1_, lvt_6_1_, lvt_8_2_)) {
                        this.visit(lvt_5_1_, lvt_8_2_);
                        break;
                     }
                  }

                  lvt_8_2_ = this.path.size();
                  if (lvt_8_2_ == lvt_4_1_) {
                     this.path.removeInt(lvt_8_2_ - 1);
                  }
               }

               if (!this.path.isEmpty()) {
                  return true;
               }
            }
         }

         return false;
      }

      private boolean isSatisfied(int p_194091_1_) {
         return this.data.get(this.getSatisfiedIndex(p_194091_1_));
      }

      private void setSatisfied(int p_194096_1_) {
         this.data.set(this.getSatisfiedIndex(p_194096_1_));
      }

      private int getSatisfiedIndex(int p_194094_1_) {
         return this.ingredientCount + this.possessedIngredientStackCount + p_194094_1_;
      }

      private boolean hasConnection(boolean p_194093_1_, int p_194093_2_, int p_194093_3_) {
         return this.data.get(this.getIndex(p_194093_1_, p_194093_2_, p_194093_3_));
      }

      private boolean hasResidual(boolean p_194100_1_, int p_194100_2_, int p_194100_3_) {
         return p_194100_1_ != this.data.get(1 + this.getIndex(p_194100_1_, p_194100_2_, p_194100_3_));
      }

      private void toggleResidual(boolean p_194089_1_, int p_194089_2_, int p_194089_3_) {
         this.data.flip(1 + this.getIndex(p_194089_1_, p_194089_2_, p_194089_3_));
      }

      private int getIndex(boolean p_194095_1_, int p_194095_2_, int p_194095_3_) {
         int lvt_4_1_ = p_194095_1_ ? p_194095_2_ * this.ingredientCount + p_194095_3_ : p_194095_3_ * this.ingredientCount + p_194095_2_;
         return this.ingredientCount + this.possessedIngredientStackCount + this.ingredientCount + 2 * lvt_4_1_;
      }

      private void visit(boolean p_194088_1_, int p_194088_2_) {
         this.data.set(this.getVisitedIndex(p_194088_1_, p_194088_2_));
         this.path.add(p_194088_2_);
      }

      private boolean hasVisited(boolean p_194101_1_, int p_194101_2_) {
         return this.data.get(this.getVisitedIndex(p_194101_1_, p_194101_2_));
      }

      private int getVisitedIndex(boolean p_194099_1_, int p_194099_2_) {
         return (p_194099_1_ ? 0 : this.ingredientCount) + p_194099_2_;
      }

      public int tryPickAll(int p_194102_1_, @Nullable IntList p_194102_2_) {
         int lvt_3_1_ = 0;
         int lvt_4_1_ = Math.min(p_194102_1_, this.getMinIngredientCount()) + 1;

         while(true) {
            while(true) {
               int lvt_5_1_ = (lvt_3_1_ + lvt_4_1_) / 2;
               if (this.tryPick(lvt_5_1_, (IntList)null)) {
                  if (lvt_4_1_ - lvt_3_1_ <= 1) {
                     if (lvt_5_1_ > 0) {
                        this.tryPick(lvt_5_1_, p_194102_2_);
                     }

                     return lvt_5_1_;
                  }

                  lvt_3_1_ = lvt_5_1_;
               } else {
                  lvt_4_1_ = lvt_5_1_;
               }
            }
         }
      }

      private int getMinIngredientCount() {
         int lvt_1_1_ = Integer.MAX_VALUE;
         Iterator var2 = this.ingredients.iterator();

         while(var2.hasNext()) {
            Ingredient lvt_3_1_ = (Ingredient)var2.next();
            int lvt_4_1_ = 0;

            int lvt_6_1_;
            for(IntListIterator var5 = lvt_3_1_.getValidItemStacksPacked().iterator(); var5.hasNext(); lvt_4_1_ = Math.max(lvt_4_1_, RecipeItemHelper.this.itemToCount.get(lvt_6_1_))) {
               lvt_6_1_ = (Integer)var5.next();
            }

            if (lvt_1_1_ > 0) {
               lvt_1_1_ = Math.min(lvt_1_1_, lvt_4_1_);
            }
         }

         return lvt_1_1_;
      }
   }
}
