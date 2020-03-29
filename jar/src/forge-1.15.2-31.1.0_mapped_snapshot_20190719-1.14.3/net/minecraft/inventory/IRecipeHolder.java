package net.minecraft.inventory;

import java.util.Collection;
import java.util.Collections;
import javax.annotation.Nullable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;

public interface IRecipeHolder {
   void setRecipeUsed(@Nullable IRecipe<?> var1);

   @Nullable
   IRecipe<?> getRecipeUsed();

   default void onCrafting(PlayerEntity p_201560_1_) {
      IRecipe<?> lvt_2_1_ = this.getRecipeUsed();
      if (lvt_2_1_ != null && !lvt_2_1_.isDynamic()) {
         p_201560_1_.unlockRecipes((Collection)Collections.singleton(lvt_2_1_));
         this.setRecipeUsed((IRecipe)null);
      }

   }

   default boolean canUseRecipe(World p_201561_1_, ServerPlayerEntity p_201561_2_, IRecipe<?> p_201561_3_) {
      if (!p_201561_3_.isDynamic() && p_201561_1_.getGameRules().getBoolean(GameRules.DO_LIMITED_CRAFTING) && !p_201561_2_.getRecipeBook().isUnlocked(p_201561_3_)) {
         return false;
      } else {
         this.setRecipeUsed(p_201561_3_);
         return true;
      }
   }
}
