package net.minecraft.item.crafting;

import com.google.common.collect.Lists;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.network.play.server.SRecipeBookPacket;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.ResourceLocationException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServerRecipeBook extends RecipeBook {
   private static final Logger LOGGER = LogManager.getLogger();
   private final RecipeManager recipeManager;

   public ServerRecipeBook(RecipeManager p_i48175_1_) {
      this.recipeManager = p_i48175_1_;
   }

   public int add(Collection<IRecipe<?>> p_197926_1_, ServerPlayerEntity p_197926_2_) {
      List<ResourceLocation> lvt_3_1_ = Lists.newArrayList();
      int lvt_4_1_ = 0;
      Iterator var5 = p_197926_1_.iterator();

      while(var5.hasNext()) {
         IRecipe<?> lvt_6_1_ = (IRecipe)var5.next();
         ResourceLocation lvt_7_1_ = lvt_6_1_.getId();
         if (!this.recipes.contains(lvt_7_1_) && !lvt_6_1_.isDynamic()) {
            this.unlock(lvt_7_1_);
            this.markNew(lvt_7_1_);
            lvt_3_1_.add(lvt_7_1_);
            CriteriaTriggers.RECIPE_UNLOCKED.trigger(p_197926_2_, lvt_6_1_);
            ++lvt_4_1_;
         }
      }

      this.sendPacket(SRecipeBookPacket.State.ADD, p_197926_2_, lvt_3_1_);
      return lvt_4_1_;
   }

   public int remove(Collection<IRecipe<?>> p_197925_1_, ServerPlayerEntity p_197925_2_) {
      List<ResourceLocation> lvt_3_1_ = Lists.newArrayList();
      int lvt_4_1_ = 0;
      Iterator var5 = p_197925_1_.iterator();

      while(var5.hasNext()) {
         IRecipe<?> lvt_6_1_ = (IRecipe)var5.next();
         ResourceLocation lvt_7_1_ = lvt_6_1_.getId();
         if (this.recipes.contains(lvt_7_1_)) {
            this.lock(lvt_7_1_);
            lvt_3_1_.add(lvt_7_1_);
            ++lvt_4_1_;
         }
      }

      this.sendPacket(SRecipeBookPacket.State.REMOVE, p_197925_2_, lvt_3_1_);
      return lvt_4_1_;
   }

   private void sendPacket(SRecipeBookPacket.State p_194081_1_, ServerPlayerEntity p_194081_2_, List<ResourceLocation> p_194081_3_) {
      p_194081_2_.connection.sendPacket(new SRecipeBookPacket(p_194081_1_, p_194081_3_, Collections.emptyList(), this.isGuiOpen, this.isFilteringCraftable, this.isFurnaceGuiOpen, this.isFurnaceFilteringCraftable));
   }

   public CompoundNBT write() {
      CompoundNBT lvt_1_1_ = new CompoundNBT();
      lvt_1_1_.putBoolean("isGuiOpen", this.isGuiOpen);
      lvt_1_1_.putBoolean("isFilteringCraftable", this.isFilteringCraftable);
      lvt_1_1_.putBoolean("isFurnaceGuiOpen", this.isFurnaceGuiOpen);
      lvt_1_1_.putBoolean("isFurnaceFilteringCraftable", this.isFurnaceFilteringCraftable);
      ListNBT lvt_2_1_ = new ListNBT();
      Iterator var3 = this.recipes.iterator();

      while(var3.hasNext()) {
         ResourceLocation lvt_4_1_ = (ResourceLocation)var3.next();
         lvt_2_1_.add(StringNBT.func_229705_a_(lvt_4_1_.toString()));
      }

      lvt_1_1_.put("recipes", lvt_2_1_);
      ListNBT lvt_3_1_ = new ListNBT();
      Iterator var7 = this.newRecipes.iterator();

      while(var7.hasNext()) {
         ResourceLocation lvt_5_1_ = (ResourceLocation)var7.next();
         lvt_3_1_.add(StringNBT.func_229705_a_(lvt_5_1_.toString()));
      }

      lvt_1_1_.put("toBeDisplayed", lvt_3_1_);
      return lvt_1_1_;
   }

   public void read(CompoundNBT p_192825_1_) {
      this.isGuiOpen = p_192825_1_.getBoolean("isGuiOpen");
      this.isFilteringCraftable = p_192825_1_.getBoolean("isFilteringCraftable");
      this.isFurnaceGuiOpen = p_192825_1_.getBoolean("isFurnaceGuiOpen");
      this.isFurnaceFilteringCraftable = p_192825_1_.getBoolean("isFurnaceFilteringCraftable");
      ListNBT lvt_2_1_ = p_192825_1_.getList("recipes", 8);
      this.func_223417_a(lvt_2_1_, this::unlock);
      ListNBT lvt_3_1_ = p_192825_1_.getList("toBeDisplayed", 8);
      this.func_223417_a(lvt_3_1_, this::markNew);
   }

   private void func_223417_a(ListNBT p_223417_1_, Consumer<IRecipe<?>> p_223417_2_) {
      for(int lvt_3_1_ = 0; lvt_3_1_ < p_223417_1_.size(); ++lvt_3_1_) {
         String lvt_4_1_ = p_223417_1_.getString(lvt_3_1_);

         try {
            ResourceLocation lvt_5_1_ = new ResourceLocation(lvt_4_1_);
            Optional<? extends IRecipe<?>> lvt_6_1_ = this.recipeManager.getRecipe(lvt_5_1_);
            if (!lvt_6_1_.isPresent()) {
               LOGGER.error("Tried to load unrecognized recipe: {} removed now.", lvt_5_1_);
            } else {
               p_223417_2_.accept(lvt_6_1_.get());
            }
         } catch (ResourceLocationException var7) {
            LOGGER.error("Tried to load improperly formatted recipe: {} removed now.", lvt_4_1_);
         }
      }

   }

   public void init(ServerPlayerEntity p_192826_1_) {
      p_192826_1_.connection.sendPacket(new SRecipeBookPacket(SRecipeBookPacket.State.INIT, this.recipes, this.newRecipes, this.isGuiOpen, this.isFilteringCraftable, this.isFurnaceGuiOpen, this.isFurnaceFilteringCraftable));
   }
}
