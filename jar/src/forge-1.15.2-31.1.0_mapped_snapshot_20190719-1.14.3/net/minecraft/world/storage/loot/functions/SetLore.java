package net.minecraft.world.storage.loot.functions;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Streams;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.function.UnaryOperator;
import javax.annotation.Nullable;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootFunction;
import net.minecraft.world.storage.loot.LootParameter;
import net.minecraft.world.storage.loot.conditions.ILootCondition;

public class SetLore extends LootFunction {
   private final boolean replace;
   private final List<ITextComponent> lore;
   @Nullable
   private final LootContext.EntityTarget field_215947_d;

   public SetLore(ILootCondition[] p_i51220_1_, boolean p_i51220_2_, List<ITextComponent> p_i51220_3_, @Nullable LootContext.EntityTarget p_i51220_4_) {
      super(p_i51220_1_);
      this.replace = p_i51220_2_;
      this.lore = ImmutableList.copyOf(p_i51220_3_);
      this.field_215947_d = p_i51220_4_;
   }

   public Set<LootParameter<?>> getRequiredParameters() {
      return this.field_215947_d != null ? ImmutableSet.of(this.field_215947_d.getParameter()) : ImmutableSet.of();
   }

   public ItemStack doApply(ItemStack p_215859_1_, LootContext p_215859_2_) {
      ListNBT lvt_3_1_ = this.func_215942_a(p_215859_1_, !this.lore.isEmpty());
      if (lvt_3_1_ != null) {
         if (this.replace) {
            lvt_3_1_.clear();
         }

         UnaryOperator<ITextComponent> lvt_4_1_ = SetName.func_215936_a(p_215859_2_, this.field_215947_d);
         this.lore.stream().map(lvt_4_1_).map(ITextComponent.Serializer::toJson).map(StringNBT::func_229705_a_).forEach(lvt_3_1_::add);
      }

      return p_215859_1_;
   }

   @Nullable
   private ListNBT func_215942_a(ItemStack p_215942_1_, boolean p_215942_2_) {
      CompoundNBT lvt_3_3_;
      if (p_215942_1_.hasTag()) {
         lvt_3_3_ = p_215942_1_.getTag();
      } else {
         if (!p_215942_2_) {
            return null;
         }

         lvt_3_3_ = new CompoundNBT();
         p_215942_1_.setTag(lvt_3_3_);
      }

      CompoundNBT lvt_4_3_;
      if (lvt_3_3_.contains("display", 10)) {
         lvt_4_3_ = lvt_3_3_.getCompound("display");
      } else {
         if (!p_215942_2_) {
            return null;
         }

         lvt_4_3_ = new CompoundNBT();
         lvt_3_3_.put("display", lvt_4_3_);
      }

      if (lvt_4_3_.contains("Lore", 9)) {
         return lvt_4_3_.getList("Lore", 8);
      } else if (p_215942_2_) {
         ListNBT lvt_5_1_ = new ListNBT();
         lvt_4_3_.put("Lore", lvt_5_1_);
         return lvt_5_1_;
      } else {
         return null;
      }
   }

   public static class Serializer extends LootFunction.Serializer<SetLore> {
      public Serializer() {
         super(new ResourceLocation("set_lore"), SetLore.class);
      }

      public void serialize(JsonObject p_186532_1_, SetLore p_186532_2_, JsonSerializationContext p_186532_3_) {
         super.serialize(p_186532_1_, (LootFunction)p_186532_2_, p_186532_3_);
         p_186532_1_.addProperty("replace", p_186532_2_.replace);
         JsonArray lvt_4_1_ = new JsonArray();
         Iterator var5 = p_186532_2_.lore.iterator();

         while(var5.hasNext()) {
            ITextComponent lvt_6_1_ = (ITextComponent)var5.next();
            lvt_4_1_.add(ITextComponent.Serializer.toJsonTree(lvt_6_1_));
         }

         p_186532_1_.add("lore", lvt_4_1_);
         if (p_186532_2_.field_215947_d != null) {
            p_186532_1_.add("entity", p_186532_3_.serialize(p_186532_2_.field_215947_d));
         }

      }

      public SetLore deserialize(JsonObject p_186530_1_, JsonDeserializationContext p_186530_2_, ILootCondition[] p_186530_3_) {
         boolean lvt_4_1_ = JSONUtils.getBoolean(p_186530_1_, "replace", false);
         List<ITextComponent> lvt_5_1_ = (List)Streams.stream(JSONUtils.getJsonArray(p_186530_1_, "lore")).map(ITextComponent.Serializer::fromJson).collect(ImmutableList.toImmutableList());
         LootContext.EntityTarget lvt_6_1_ = (LootContext.EntityTarget)JSONUtils.deserializeClass(p_186530_1_, "entity", (Object)null, p_186530_2_, LootContext.EntityTarget.class);
         return new SetLore(p_186530_3_, lvt_4_1_, lvt_5_1_, lvt_6_1_);
      }

      // $FF: synthetic method
      public LootFunction deserialize(JsonObject p_186530_1_, JsonDeserializationContext p_186530_2_, ILootCondition[] p_186530_3_) {
         return this.deserialize(p_186530_1_, p_186530_2_, p_186530_3_);
      }
   }
}
