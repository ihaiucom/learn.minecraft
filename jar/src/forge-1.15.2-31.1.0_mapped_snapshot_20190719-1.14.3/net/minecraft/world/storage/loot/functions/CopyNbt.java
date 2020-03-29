package net.minecraft.world.storage.loot.functions;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.advancements.criterion.NBTPredicate;
import net.minecraft.command.arguments.NBTPathArgument;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootFunction;
import net.minecraft.world.storage.loot.LootParameter;
import net.minecraft.world.storage.loot.LootParameters;
import net.minecraft.world.storage.loot.conditions.ILootCondition;

public class CopyNbt extends LootFunction {
   private final CopyNbt.Source field_215887_a;
   private final List<CopyNbt.Operation> field_215888_c;
   private static final Function<Entity, INBT> field_215889_d = NBTPredicate::writeToNBTWithSelectedItem;
   private static final Function<TileEntity, INBT> field_215890_e = (p_215882_0_) -> {
      return p_215882_0_.write(new CompoundNBT());
   };

   private CopyNbt(ILootCondition[] p_i51240_1_, CopyNbt.Source p_i51240_2_, List<CopyNbt.Operation> p_i51240_3_) {
      super(p_i51240_1_);
      this.field_215887_a = p_i51240_2_;
      this.field_215888_c = ImmutableList.copyOf(p_i51240_3_);
   }

   private static NBTPathArgument.NBTPath func_215880_b(String p_215880_0_) {
      try {
         return (new NBTPathArgument()).parse(new StringReader(p_215880_0_));
      } catch (CommandSyntaxException var2) {
         throw new IllegalArgumentException("Failed to parse path " + p_215880_0_, var2);
      }
   }

   public Set<LootParameter<?>> getRequiredParameters() {
      return ImmutableSet.of(this.field_215887_a.field_216225_f);
   }

   public ItemStack doApply(ItemStack p_215859_1_, LootContext p_215859_2_) {
      INBT lvt_3_1_ = (INBT)this.field_215887_a.field_216226_g.apply(p_215859_2_);
      if (lvt_3_1_ != null) {
         this.field_215888_c.forEach((p_215885_2_) -> {
            p_215885_2_.func_216216_a(p_215859_1_::getOrCreateTag, lvt_3_1_);
         });
      }

      return p_215859_1_;
   }

   public static CopyNbt.Builder func_215881_a(CopyNbt.Source p_215881_0_) {
      return new CopyNbt.Builder(p_215881_0_);
   }

   // $FF: synthetic method
   CopyNbt(ILootCondition[] p_i51241_1_, CopyNbt.Source p_i51241_2_, List p_i51241_3_, Object p_i51241_4_) {
      this(p_i51241_1_, p_i51241_2_, p_i51241_3_);
   }

   public static class Serializer extends LootFunction.Serializer<CopyNbt> {
      public Serializer() {
         super(new ResourceLocation("copy_nbt"), CopyNbt.class);
      }

      public void serialize(JsonObject p_186532_1_, CopyNbt p_186532_2_, JsonSerializationContext p_186532_3_) {
         super.serialize(p_186532_1_, (LootFunction)p_186532_2_, p_186532_3_);
         p_186532_1_.addProperty("source", p_186532_2_.field_215887_a.field_216224_e);
         JsonArray lvt_4_1_ = new JsonArray();
         p_186532_2_.field_215888_c.stream().map(CopyNbt.Operation::func_216214_a).forEach(lvt_4_1_::add);
         p_186532_1_.add("ops", lvt_4_1_);
      }

      public CopyNbt deserialize(JsonObject p_186530_1_, JsonDeserializationContext p_186530_2_, ILootCondition[] p_186530_3_) {
         CopyNbt.Source lvt_4_1_ = CopyNbt.Source.func_216223_a(JSONUtils.getString(p_186530_1_, "source"));
         List<CopyNbt.Operation> lvt_5_1_ = Lists.newArrayList();
         JsonArray lvt_6_1_ = JSONUtils.getJsonArray(p_186530_1_, "ops");
         Iterator var7 = lvt_6_1_.iterator();

         while(var7.hasNext()) {
            JsonElement lvt_8_1_ = (JsonElement)var7.next();
            JsonObject lvt_9_1_ = JSONUtils.getJsonObject(lvt_8_1_, "op");
            lvt_5_1_.add(CopyNbt.Operation.func_216215_a(lvt_9_1_));
         }

         return new CopyNbt(p_186530_3_, lvt_4_1_, lvt_5_1_);
      }

      // $FF: synthetic method
      public LootFunction deserialize(JsonObject p_186530_1_, JsonDeserializationContext p_186530_2_, ILootCondition[] p_186530_3_) {
         return this.deserialize(p_186530_1_, p_186530_2_, p_186530_3_);
      }
   }

   public static enum Source {
      THIS("this", LootParameters.THIS_ENTITY, CopyNbt.field_215889_d),
      KILLER("killer", LootParameters.KILLER_ENTITY, CopyNbt.field_215889_d),
      KILLER_PLAYER("killer_player", LootParameters.LAST_DAMAGE_PLAYER, CopyNbt.field_215889_d),
      BLOCK_ENTITY("block_entity", LootParameters.BLOCK_ENTITY, CopyNbt.field_215890_e);

      public final String field_216224_e;
      public final LootParameter<?> field_216225_f;
      public final Function<LootContext, INBT> field_216226_g;

      private <T> Source(String p_i50672_3_, LootParameter<T> p_i50672_4_, Function<? super T, INBT> p_i50672_5_) {
         this.field_216224_e = p_i50672_3_;
         this.field_216225_f = p_i50672_4_;
         this.field_216226_g = (p_216222_2_) -> {
            T lvt_3_1_ = p_216222_2_.get(p_i50672_4_);
            return lvt_3_1_ != null ? (INBT)p_i50672_5_.apply(lvt_3_1_) : null;
         };
      }

      public static CopyNbt.Source func_216223_a(String p_216223_0_) {
         CopyNbt.Source[] var1 = values();
         int var2 = var1.length;

         for(int var3 = 0; var3 < var2; ++var3) {
            CopyNbt.Source lvt_4_1_ = var1[var3];
            if (lvt_4_1_.field_216224_e.equals(p_216223_0_)) {
               return lvt_4_1_;
            }
         }

         throw new IllegalArgumentException("Invalid tag source " + p_216223_0_);
      }
   }

   public static enum Action {
      REPLACE("replace") {
         public void func_216227_a(INBT p_216227_1_, NBTPathArgument.NBTPath p_216227_2_, List<INBT> p_216227_3_) throws CommandSyntaxException {
            INBT var10002 = (INBT)Iterables.getLast(p_216227_3_);
            p_216227_2_.func_218076_b(p_216227_1_, var10002::copy);
         }
      },
      APPEND("append") {
         public void func_216227_a(INBT p_216227_1_, NBTPathArgument.NBTPath p_216227_2_, List<INBT> p_216227_3_) throws CommandSyntaxException {
            List<INBT> lvt_4_1_ = p_216227_2_.func_218073_a(p_216227_1_, ListNBT::new);
            lvt_4_1_.forEach((p_216232_1_) -> {
               if (p_216232_1_ instanceof ListNBT) {
                  p_216227_3_.forEach((p_216231_1_) -> {
                     ((ListNBT)p_216232_1_).add(p_216231_1_.copy());
                  });
               }

            });
         }
      },
      MERGE("merge") {
         public void func_216227_a(INBT p_216227_1_, NBTPathArgument.NBTPath p_216227_2_, List<INBT> p_216227_3_) throws CommandSyntaxException {
            List<INBT> lvt_4_1_ = p_216227_2_.func_218073_a(p_216227_1_, CompoundNBT::new);
            lvt_4_1_.forEach((p_216234_1_) -> {
               if (p_216234_1_ instanceof CompoundNBT) {
                  p_216227_3_.forEach((p_216233_1_) -> {
                     if (p_216233_1_ instanceof CompoundNBT) {
                        ((CompoundNBT)p_216234_1_).merge((CompoundNBT)p_216233_1_);
                     }

                  });
               }

            });
         }
      };

      private final String field_216230_d;

      public abstract void func_216227_a(INBT var1, NBTPathArgument.NBTPath var2, List<INBT> var3) throws CommandSyntaxException;

      private Action(String p_i50670_3_) {
         this.field_216230_d = p_i50670_3_;
      }

      public static CopyNbt.Action func_216229_a(String p_216229_0_) {
         CopyNbt.Action[] var1 = values();
         int var2 = var1.length;

         for(int var3 = 0; var3 < var2; ++var3) {
            CopyNbt.Action lvt_4_1_ = var1[var3];
            if (lvt_4_1_.field_216230_d.equals(p_216229_0_)) {
               return lvt_4_1_;
            }
         }

         throw new IllegalArgumentException("Invalid merge strategy" + p_216229_0_);
      }

      // $FF: synthetic method
      Action(String p_i50671_3_, Object p_i50671_4_) {
         this(p_i50671_3_);
      }
   }

   public static class Builder extends LootFunction.Builder<CopyNbt.Builder> {
      private final CopyNbt.Source field_216057_a;
      private final List<CopyNbt.Operation> field_216058_b;

      private Builder(CopyNbt.Source p_i50675_1_) {
         this.field_216058_b = Lists.newArrayList();
         this.field_216057_a = p_i50675_1_;
      }

      public CopyNbt.Builder func_216055_a(String p_216055_1_, String p_216055_2_, CopyNbt.Action p_216055_3_) {
         this.field_216058_b.add(new CopyNbt.Operation(p_216055_1_, p_216055_2_, p_216055_3_));
         return this;
      }

      public CopyNbt.Builder func_216056_a(String p_216056_1_, String p_216056_2_) {
         return this.func_216055_a(p_216056_1_, p_216056_2_, CopyNbt.Action.REPLACE);
      }

      protected CopyNbt.Builder doCast() {
         return this;
      }

      public ILootFunction build() {
         return new CopyNbt(this.getConditions(), this.field_216057_a, this.field_216058_b);
      }

      // $FF: synthetic method
      protected LootFunction.Builder doCast() {
         return this.doCast();
      }

      // $FF: synthetic method
      Builder(CopyNbt.Source p_i50676_1_, Object p_i50676_2_) {
         this(p_i50676_1_);
      }
   }

   static class Operation {
      private final String field_216217_a;
      private final NBTPathArgument.NBTPath field_216218_b;
      private final String field_216219_c;
      private final NBTPathArgument.NBTPath field_216220_d;
      private final CopyNbt.Action field_216221_e;

      private Operation(String p_i50673_1_, String p_i50673_2_, CopyNbt.Action p_i50673_3_) {
         this.field_216217_a = p_i50673_1_;
         this.field_216218_b = CopyNbt.func_215880_b(p_i50673_1_);
         this.field_216219_c = p_i50673_2_;
         this.field_216220_d = CopyNbt.func_215880_b(p_i50673_2_);
         this.field_216221_e = p_i50673_3_;
      }

      public void func_216216_a(Supplier<INBT> p_216216_1_, INBT p_216216_2_) {
         try {
            List<INBT> lvt_3_1_ = this.field_216218_b.func_218071_a(p_216216_2_);
            if (!lvt_3_1_.isEmpty()) {
               this.field_216221_e.func_216227_a((INBT)p_216216_1_.get(), this.field_216220_d, lvt_3_1_);
            }
         } catch (CommandSyntaxException var4) {
         }

      }

      public JsonObject func_216214_a() {
         JsonObject lvt_1_1_ = new JsonObject();
         lvt_1_1_.addProperty("source", this.field_216217_a);
         lvt_1_1_.addProperty("target", this.field_216219_c);
         lvt_1_1_.addProperty("op", this.field_216221_e.field_216230_d);
         return lvt_1_1_;
      }

      public static CopyNbt.Operation func_216215_a(JsonObject p_216215_0_) {
         String lvt_1_1_ = JSONUtils.getString(p_216215_0_, "source");
         String lvt_2_1_ = JSONUtils.getString(p_216215_0_, "target");
         CopyNbt.Action lvt_3_1_ = CopyNbt.Action.func_216229_a(JSONUtils.getString(p_216215_0_, "op"));
         return new CopyNbt.Operation(lvt_1_1_, lvt_2_1_, lvt_3_1_);
      }

      // $FF: synthetic method
      Operation(String p_i50674_1_, String p_i50674_2_, CopyNbt.Action p_i50674_3_, Object p_i50674_4_) {
         this(p_i50674_1_, p_i50674_2_, p_i50674_3_);
      }
   }
}
