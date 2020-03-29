package net.minecraft.advancements.criterion;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Items;
import net.minecraft.util.JSONUtils;
import net.minecraft.world.raid.Raid;

public class EntityEquipmentPredicate {
   public static final EntityEquipmentPredicate ANY;
   public static final EntityEquipmentPredicate WEARING_ILLAGER_BANNER;
   private final ItemPredicate head;
   private final ItemPredicate chest;
   private final ItemPredicate legs;
   private final ItemPredicate feet;
   private final ItemPredicate mainHand;
   private final ItemPredicate offHand;

   public EntityEquipmentPredicate(ItemPredicate p_i50809_1_, ItemPredicate p_i50809_2_, ItemPredicate p_i50809_3_, ItemPredicate p_i50809_4_, ItemPredicate p_i50809_5_, ItemPredicate p_i50809_6_) {
      this.head = p_i50809_1_;
      this.chest = p_i50809_2_;
      this.legs = p_i50809_3_;
      this.feet = p_i50809_4_;
      this.mainHand = p_i50809_5_;
      this.offHand = p_i50809_6_;
   }

   public boolean test(@Nullable Entity p_217955_1_) {
      if (this == ANY) {
         return true;
      } else if (!(p_217955_1_ instanceof LivingEntity)) {
         return false;
      } else {
         LivingEntity lvt_2_1_ = (LivingEntity)p_217955_1_;
         if (!this.head.test(lvt_2_1_.getItemStackFromSlot(EquipmentSlotType.HEAD))) {
            return false;
         } else if (!this.chest.test(lvt_2_1_.getItemStackFromSlot(EquipmentSlotType.CHEST))) {
            return false;
         } else if (!this.legs.test(lvt_2_1_.getItemStackFromSlot(EquipmentSlotType.LEGS))) {
            return false;
         } else if (!this.feet.test(lvt_2_1_.getItemStackFromSlot(EquipmentSlotType.FEET))) {
            return false;
         } else if (!this.mainHand.test(lvt_2_1_.getItemStackFromSlot(EquipmentSlotType.MAINHAND))) {
            return false;
         } else {
            return this.offHand.test(lvt_2_1_.getItemStackFromSlot(EquipmentSlotType.OFFHAND));
         }
      }
   }

   public static EntityEquipmentPredicate deserialize(@Nullable JsonElement p_217956_0_) {
      if (p_217956_0_ != null && !p_217956_0_.isJsonNull()) {
         JsonObject lvt_1_1_ = JSONUtils.getJsonObject(p_217956_0_, "equipment");
         ItemPredicate lvt_2_1_ = ItemPredicate.deserialize(lvt_1_1_.get("head"));
         ItemPredicate lvt_3_1_ = ItemPredicate.deserialize(lvt_1_1_.get("chest"));
         ItemPredicate lvt_4_1_ = ItemPredicate.deserialize(lvt_1_1_.get("legs"));
         ItemPredicate lvt_5_1_ = ItemPredicate.deserialize(lvt_1_1_.get("feet"));
         ItemPredicate lvt_6_1_ = ItemPredicate.deserialize(lvt_1_1_.get("mainhand"));
         ItemPredicate lvt_7_1_ = ItemPredicate.deserialize(lvt_1_1_.get("offhand"));
         return new EntityEquipmentPredicate(lvt_2_1_, lvt_3_1_, lvt_4_1_, lvt_5_1_, lvt_6_1_, lvt_7_1_);
      } else {
         return ANY;
      }
   }

   public JsonElement serialize() {
      if (this == ANY) {
         return JsonNull.INSTANCE;
      } else {
         JsonObject lvt_1_1_ = new JsonObject();
         lvt_1_1_.add("head", this.head.serialize());
         lvt_1_1_.add("chest", this.chest.serialize());
         lvt_1_1_.add("legs", this.legs.serialize());
         lvt_1_1_.add("feet", this.feet.serialize());
         lvt_1_1_.add("mainhand", this.mainHand.serialize());
         lvt_1_1_.add("offhand", this.offHand.serialize());
         return lvt_1_1_;
      }
   }

   static {
      ANY = new EntityEquipmentPredicate(ItemPredicate.ANY, ItemPredicate.ANY, ItemPredicate.ANY, ItemPredicate.ANY, ItemPredicate.ANY, ItemPredicate.ANY);
      WEARING_ILLAGER_BANNER = new EntityEquipmentPredicate(ItemPredicate.Builder.create().item(Items.WHITE_BANNER).nbt(Raid.createIllagerBanner().getTag()).build(), ItemPredicate.ANY, ItemPredicate.ANY, ItemPredicate.ANY, ItemPredicate.ANY, ItemPredicate.ANY);
   }
}
