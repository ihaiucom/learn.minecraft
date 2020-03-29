package net.minecraft.world.storage.loot.functions;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Arrays;
import java.util.List;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootEntry;
import net.minecraft.world.storage.loot.LootFunction;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraft.world.storage.loot.ValidationTracker;
import net.minecraft.world.storage.loot.conditions.ILootCondition;

public class SetContents extends LootFunction {
   private final List<LootEntry> field_215924_a;

   private SetContents(ILootCondition[] p_i51226_1_, List<LootEntry> p_i51226_2_) {
      super(p_i51226_1_);
      this.field_215924_a = ImmutableList.copyOf(p_i51226_2_);
   }

   public ItemStack doApply(ItemStack p_215859_1_, LootContext p_215859_2_) {
      if (p_215859_1_.isEmpty()) {
         return p_215859_1_;
      } else {
         NonNullList<ItemStack> lvt_3_1_ = NonNullList.create();
         this.field_215924_a.forEach((p_215921_2_) -> {
            p_215921_2_.expand(p_215859_2_, (p_215922_2_) -> {
               lvt_3_1_.getClass();
               p_215922_2_.func_216188_a(LootTable.capStackSizes(lvt_3_1_::add), p_215859_2_);
            });
         });
         CompoundNBT lvt_4_1_ = new CompoundNBT();
         ItemStackHelper.saveAllItems(lvt_4_1_, lvt_3_1_);
         CompoundNBT lvt_5_1_ = p_215859_1_.getOrCreateTag();
         lvt_5_1_.put("BlockEntityTag", lvt_4_1_.merge(lvt_5_1_.getCompound("BlockEntityTag")));
         return p_215859_1_;
      }
   }

   public void func_225580_a_(ValidationTracker p_225580_1_) {
      super.func_225580_a_(p_225580_1_);

      for(int lvt_2_1_ = 0; lvt_2_1_ < this.field_215924_a.size(); ++lvt_2_1_) {
         ((LootEntry)this.field_215924_a.get(lvt_2_1_)).func_225579_a_(p_225580_1_.func_227534_b_(".entry[" + lvt_2_1_ + "]"));
      }

   }

   public static SetContents.Builder func_215920_b() {
      return new SetContents.Builder();
   }

   // $FF: synthetic method
   SetContents(ILootCondition[] p_i51227_1_, List p_i51227_2_, Object p_i51227_3_) {
      this(p_i51227_1_, p_i51227_2_);
   }

   public static class Serializer extends LootFunction.Serializer<SetContents> {
      protected Serializer() {
         super(new ResourceLocation("set_contents"), SetContents.class);
      }

      public void serialize(JsonObject p_186532_1_, SetContents p_186532_2_, JsonSerializationContext p_186532_3_) {
         super.serialize(p_186532_1_, (LootFunction)p_186532_2_, p_186532_3_);
         p_186532_1_.add("entries", p_186532_3_.serialize(p_186532_2_.field_215924_a));
      }

      public SetContents deserialize(JsonObject p_186530_1_, JsonDeserializationContext p_186530_2_, ILootCondition[] p_186530_3_) {
         LootEntry[] lvt_4_1_ = (LootEntry[])JSONUtils.deserializeClass(p_186530_1_, "entries", p_186530_2_, LootEntry[].class);
         return new SetContents(p_186530_3_, Arrays.asList(lvt_4_1_));
      }

      // $FF: synthetic method
      public LootFunction deserialize(JsonObject p_186530_1_, JsonDeserializationContext p_186530_2_, ILootCondition[] p_186530_3_) {
         return this.deserialize(p_186530_1_, p_186530_2_, p_186530_3_);
      }
   }

   public static class Builder extends LootFunction.Builder<SetContents.Builder> {
      private final List<LootEntry> field_216076_a = Lists.newArrayList();

      protected SetContents.Builder doCast() {
         return this;
      }

      public SetContents.Builder func_216075_a(LootEntry.Builder<?> p_216075_1_) {
         this.field_216076_a.add(p_216075_1_.func_216081_b());
         return this;
      }

      public ILootFunction build() {
         return new SetContents(this.getConditions(), this.field_216076_a);
      }

      // $FF: synthetic method
      protected LootFunction.Builder doCast() {
         return this.doCast();
      }
   }
}
