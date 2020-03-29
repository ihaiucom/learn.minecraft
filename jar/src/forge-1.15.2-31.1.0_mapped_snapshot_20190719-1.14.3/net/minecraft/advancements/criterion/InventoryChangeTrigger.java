package net.minecraft.advancements.criterion;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.Iterator;
import java.util.List;
import net.minecraft.advancements.ICriterionInstance;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.tags.Tag;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;

public class InventoryChangeTrigger extends AbstractCriterionTrigger<InventoryChangeTrigger.Instance> {
   private static final ResourceLocation ID = new ResourceLocation("inventory_changed");

   public ResourceLocation getId() {
      return ID;
   }

   public InventoryChangeTrigger.Instance deserializeInstance(JsonObject p_192166_1_, JsonDeserializationContext p_192166_2_) {
      JsonObject lvt_3_1_ = JSONUtils.getJsonObject(p_192166_1_, "slots", new JsonObject());
      MinMaxBounds.IntBound lvt_4_1_ = MinMaxBounds.IntBound.fromJson(lvt_3_1_.get("occupied"));
      MinMaxBounds.IntBound lvt_5_1_ = MinMaxBounds.IntBound.fromJson(lvt_3_1_.get("full"));
      MinMaxBounds.IntBound lvt_6_1_ = MinMaxBounds.IntBound.fromJson(lvt_3_1_.get("empty"));
      ItemPredicate[] lvt_7_1_ = ItemPredicate.deserializeArray(p_192166_1_.get("items"));
      return new InventoryChangeTrigger.Instance(lvt_4_1_, lvt_5_1_, lvt_6_1_, lvt_7_1_);
   }

   public void trigger(ServerPlayerEntity p_192208_1_, PlayerInventory p_192208_2_) {
      this.func_227070_a_(p_192208_1_.getAdvancements(), (p_226650_1_) -> {
         return p_226650_1_.test(p_192208_2_);
      });
   }

   // $FF: synthetic method
   public ICriterionInstance deserializeInstance(JsonObject p_192166_1_, JsonDeserializationContext p_192166_2_) {
      return this.deserializeInstance(p_192166_1_, p_192166_2_);
   }

   public static class Instance extends CriterionInstance {
      private final MinMaxBounds.IntBound occupied;
      private final MinMaxBounds.IntBound full;
      private final MinMaxBounds.IntBound empty;
      private final ItemPredicate[] items;

      public Instance(MinMaxBounds.IntBound p_i49710_1_, MinMaxBounds.IntBound p_i49710_2_, MinMaxBounds.IntBound p_i49710_3_, ItemPredicate[] p_i49710_4_) {
         super(InventoryChangeTrigger.ID);
         this.occupied = p_i49710_1_;
         this.full = p_i49710_2_;
         this.empty = p_i49710_3_;
         this.items = p_i49710_4_;
      }

      public static InventoryChangeTrigger.Instance forItems(ItemPredicate... p_203923_0_) {
         return new InventoryChangeTrigger.Instance(MinMaxBounds.IntBound.UNBOUNDED, MinMaxBounds.IntBound.UNBOUNDED, MinMaxBounds.IntBound.UNBOUNDED, p_203923_0_);
      }

      public static InventoryChangeTrigger.Instance forItems(IItemProvider... p_203922_0_) {
         ItemPredicate[] lvt_1_1_ = new ItemPredicate[p_203922_0_.length];

         for(int lvt_2_1_ = 0; lvt_2_1_ < p_203922_0_.length; ++lvt_2_1_) {
            lvt_1_1_[lvt_2_1_] = new ItemPredicate((Tag)null, p_203922_0_[lvt_2_1_].asItem(), MinMaxBounds.IntBound.UNBOUNDED, MinMaxBounds.IntBound.UNBOUNDED, EnchantmentPredicate.field_226534_b_, EnchantmentPredicate.field_226534_b_, (Potion)null, NBTPredicate.ANY);
         }

         return forItems(lvt_1_1_);
      }

      public JsonElement serialize() {
         JsonObject lvt_1_1_ = new JsonObject();
         if (!this.occupied.isUnbounded() || !this.full.isUnbounded() || !this.empty.isUnbounded()) {
            JsonObject lvt_2_1_ = new JsonObject();
            lvt_2_1_.add("occupied", this.occupied.serialize());
            lvt_2_1_.add("full", this.full.serialize());
            lvt_2_1_.add("empty", this.empty.serialize());
            lvt_1_1_.add("slots", lvt_2_1_);
         }

         if (this.items.length > 0) {
            JsonArray lvt_2_2_ = new JsonArray();
            ItemPredicate[] var3 = this.items;
            int var4 = var3.length;

            for(int var5 = 0; var5 < var4; ++var5) {
               ItemPredicate lvt_6_1_ = var3[var5];
               lvt_2_2_.add(lvt_6_1_.serialize());
            }

            lvt_1_1_.add("items", lvt_2_2_);
         }

         return lvt_1_1_;
      }

      public boolean test(PlayerInventory p_192265_1_) {
         int lvt_2_1_ = 0;
         int lvt_3_1_ = 0;
         int lvt_4_1_ = 0;
         List<ItemPredicate> lvt_5_1_ = Lists.newArrayList(this.items);

         for(int lvt_6_1_ = 0; lvt_6_1_ < p_192265_1_.getSizeInventory(); ++lvt_6_1_) {
            ItemStack lvt_7_1_ = p_192265_1_.getStackInSlot(lvt_6_1_);
            if (lvt_7_1_.isEmpty()) {
               ++lvt_3_1_;
            } else {
               ++lvt_4_1_;
               if (lvt_7_1_.getCount() >= lvt_7_1_.getMaxStackSize()) {
                  ++lvt_2_1_;
               }

               Iterator lvt_8_1_ = lvt_5_1_.iterator();

               while(lvt_8_1_.hasNext()) {
                  ItemPredicate lvt_9_1_ = (ItemPredicate)lvt_8_1_.next();
                  if (lvt_9_1_.test(lvt_7_1_)) {
                     lvt_8_1_.remove();
                  }
               }
            }
         }

         if (!this.full.test(lvt_2_1_)) {
            return false;
         } else if (!this.empty.test(lvt_3_1_)) {
            return false;
         } else if (!this.occupied.test(lvt_4_1_)) {
            return false;
         } else if (!lvt_5_1_.isEmpty()) {
            return false;
         } else {
            return true;
         }
      }
   }
}
