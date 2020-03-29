package net.minecraft.item;

import com.google.common.collect.Lists;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.stats.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class KnowledgeBookItem extends Item {
   private static final Logger LOGGER = LogManager.getLogger();

   public KnowledgeBookItem(Item.Properties p_i48485_1_) {
      super(p_i48485_1_);
   }

   public ActionResult<ItemStack> onItemRightClick(World p_77659_1_, PlayerEntity p_77659_2_, Hand p_77659_3_) {
      ItemStack lvt_4_1_ = p_77659_2_.getHeldItem(p_77659_3_);
      CompoundNBT lvt_5_1_ = lvt_4_1_.getTag();
      if (!p_77659_2_.abilities.isCreativeMode) {
         p_77659_2_.setHeldItem(p_77659_3_, ItemStack.EMPTY);
      }

      if (lvt_5_1_ != null && lvt_5_1_.contains("Recipes", 9)) {
         if (!p_77659_1_.isRemote) {
            ListNBT lvt_6_1_ = lvt_5_1_.getList("Recipes", 8);
            List<IRecipe<?>> lvt_7_1_ = Lists.newArrayList();
            RecipeManager lvt_8_1_ = p_77659_1_.getServer().getRecipeManager();

            for(int lvt_9_1_ = 0; lvt_9_1_ < lvt_6_1_.size(); ++lvt_9_1_) {
               String lvt_10_1_ = lvt_6_1_.getString(lvt_9_1_);
               Optional<? extends IRecipe<?>> lvt_11_1_ = lvt_8_1_.getRecipe(new ResourceLocation(lvt_10_1_));
               if (!lvt_11_1_.isPresent()) {
                  LOGGER.error("Invalid recipe: {}", lvt_10_1_);
                  return ActionResult.func_226251_d_(lvt_4_1_);
               }

               lvt_7_1_.add(lvt_11_1_.get());
            }

            p_77659_2_.unlockRecipes((Collection)lvt_7_1_);
            p_77659_2_.addStat(Stats.ITEM_USED.get(this));
         }

         return ActionResult.func_226248_a_(lvt_4_1_);
      } else {
         LOGGER.error("Tag not valid: {}", lvt_5_1_);
         return ActionResult.func_226251_d_(lvt_4_1_);
      }
   }
}
