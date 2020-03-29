package net.minecraft.item;

import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtils;
import net.minecraft.potion.Potions;
import net.minecraft.stats.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class PotionItem extends Item {
   public PotionItem(Item.Properties p_i48476_1_) {
      super(p_i48476_1_);
   }

   @OnlyIn(Dist.CLIENT)
   public ItemStack getDefaultInstance() {
      return PotionUtils.addPotionToItemStack(super.getDefaultInstance(), Potions.WATER);
   }

   public ItemStack onItemUseFinish(ItemStack p_77654_1_, World p_77654_2_, LivingEntity p_77654_3_) {
      PlayerEntity lvt_4_1_ = p_77654_3_ instanceof PlayerEntity ? (PlayerEntity)p_77654_3_ : null;
      if (lvt_4_1_ instanceof ServerPlayerEntity) {
         CriteriaTriggers.CONSUME_ITEM.trigger((ServerPlayerEntity)lvt_4_1_, p_77654_1_);
      }

      if (!p_77654_2_.isRemote) {
         List<EffectInstance> lvt_5_1_ = PotionUtils.getEffectsFromStack(p_77654_1_);
         Iterator var6 = lvt_5_1_.iterator();

         while(var6.hasNext()) {
            EffectInstance lvt_7_1_ = (EffectInstance)var6.next();
            if (lvt_7_1_.getPotion().isInstant()) {
               lvt_7_1_.getPotion().affectEntity(lvt_4_1_, lvt_4_1_, p_77654_3_, lvt_7_1_.getAmplifier(), 1.0D);
            } else {
               p_77654_3_.addPotionEffect(new EffectInstance(lvt_7_1_));
            }
         }
      }

      if (lvt_4_1_ != null) {
         lvt_4_1_.addStat(Stats.ITEM_USED.get(this));
         if (!lvt_4_1_.abilities.isCreativeMode) {
            p_77654_1_.shrink(1);
         }
      }

      if (lvt_4_1_ == null || !lvt_4_1_.abilities.isCreativeMode) {
         if (p_77654_1_.isEmpty()) {
            return new ItemStack(Items.GLASS_BOTTLE);
         }

         if (lvt_4_1_ != null) {
            lvt_4_1_.inventory.addItemStackToInventory(new ItemStack(Items.GLASS_BOTTLE));
         }
      }

      return p_77654_1_;
   }

   public int getUseDuration(ItemStack p_77626_1_) {
      return 32;
   }

   public UseAction getUseAction(ItemStack p_77661_1_) {
      return UseAction.DRINK;
   }

   public ActionResult<ItemStack> onItemRightClick(World p_77659_1_, PlayerEntity p_77659_2_, Hand p_77659_3_) {
      p_77659_2_.setActiveHand(p_77659_3_);
      return ActionResult.func_226248_a_(p_77659_2_.getHeldItem(p_77659_3_));
   }

   public String getTranslationKey(ItemStack p_77667_1_) {
      return PotionUtils.getPotionFromItem(p_77667_1_).getNamePrefixed(this.getTranslationKey() + ".effect.");
   }

   @OnlyIn(Dist.CLIENT)
   public void addInformation(ItemStack p_77624_1_, @Nullable World p_77624_2_, List<ITextComponent> p_77624_3_, ITooltipFlag p_77624_4_) {
      PotionUtils.addPotionTooltip(p_77624_1_, p_77624_3_, 1.0F);
   }

   public boolean hasEffect(ItemStack p_77636_1_) {
      return super.hasEffect(p_77636_1_) || !PotionUtils.getEffectsFromStack(p_77636_1_).isEmpty();
   }

   public void fillItemGroup(ItemGroup p_150895_1_, NonNullList<ItemStack> p_150895_2_) {
      if (this.isInGroup(p_150895_1_)) {
         Iterator var3 = Registry.POTION.iterator();

         while(var3.hasNext()) {
            Potion lvt_4_1_ = (Potion)var3.next();
            if (lvt_4_1_ != Potions.EMPTY) {
               p_150895_2_.add(PotionUtils.addPotionToItemStack(new ItemStack(this), lvt_4_1_));
            }
         }
      }

   }
}
