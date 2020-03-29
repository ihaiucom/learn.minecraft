package net.minecraft.advancements.criterion;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSyntaxException;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.JSONUtils;

public class NBTPredicate {
   public static final NBTPredicate ANY = new NBTPredicate((CompoundNBT)null);
   @Nullable
   private final CompoundNBT tag;

   public NBTPredicate(@Nullable CompoundNBT p_i47536_1_) {
      this.tag = p_i47536_1_;
   }

   public boolean test(ItemStack p_193478_1_) {
      return this == ANY ? true : this.test((INBT)p_193478_1_.getTag());
   }

   public boolean test(Entity p_193475_1_) {
      return this == ANY ? true : this.test((INBT)writeToNBTWithSelectedItem(p_193475_1_));
   }

   public boolean test(@Nullable INBT p_193477_1_) {
      if (p_193477_1_ == null) {
         return this == ANY;
      } else {
         return this.tag == null || NBTUtil.areNBTEquals(this.tag, p_193477_1_, true);
      }
   }

   public JsonElement serialize() {
      return (JsonElement)(this != ANY && this.tag != null ? new JsonPrimitive(this.tag.toString()) : JsonNull.INSTANCE);
   }

   public static NBTPredicate deserialize(@Nullable JsonElement p_193476_0_) {
      if (p_193476_0_ != null && !p_193476_0_.isJsonNull()) {
         CompoundNBT lvt_1_2_;
         try {
            lvt_1_2_ = JsonToNBT.getTagFromJson(JSONUtils.getString(p_193476_0_, "nbt"));
         } catch (CommandSyntaxException var3) {
            throw new JsonSyntaxException("Invalid nbt tag: " + var3.getMessage());
         }

         return new NBTPredicate(lvt_1_2_);
      } else {
         return ANY;
      }
   }

   public static CompoundNBT writeToNBTWithSelectedItem(Entity p_196981_0_) {
      CompoundNBT lvt_1_1_ = p_196981_0_.writeWithoutTypeId(new CompoundNBT());
      if (p_196981_0_ instanceof PlayerEntity) {
         ItemStack lvt_2_1_ = ((PlayerEntity)p_196981_0_).inventory.getCurrentItem();
         if (!lvt_2_1_.isEmpty()) {
            lvt_1_1_.put("SelectedItem", lvt_2_1_.write(new CompoundNBT()));
         }
      }

      return lvt_1_1_;
   }
}
