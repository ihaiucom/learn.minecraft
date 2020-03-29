package net.minecraft.world.storage.loot.functions;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Set;
import java.util.function.UnaryOperator;
import javax.annotation.Nullable;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentUtils;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootFunction;
import net.minecraft.world.storage.loot.LootParameter;
import net.minecraft.world.storage.loot.conditions.ILootCondition;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SetName extends LootFunction {
   private static final Logger LOGGER = LogManager.getLogger();
   private final ITextComponent name;
   @Nullable
   private final LootContext.EntityTarget field_215940_d;

   private SetName(ILootCondition[] p_i51218_1_, @Nullable ITextComponent p_i51218_2_, @Nullable LootContext.EntityTarget p_i51218_3_) {
      super(p_i51218_1_);
      this.name = p_i51218_2_;
      this.field_215940_d = p_i51218_3_;
   }

   public Set<LootParameter<?>> getRequiredParameters() {
      return this.field_215940_d != null ? ImmutableSet.of(this.field_215940_d.getParameter()) : ImmutableSet.of();
   }

   public static UnaryOperator<ITextComponent> func_215936_a(LootContext p_215936_0_, @Nullable LootContext.EntityTarget p_215936_1_) {
      if (p_215936_1_ != null) {
         Entity lvt_2_1_ = (Entity)p_215936_0_.get(p_215936_1_.getParameter());
         if (lvt_2_1_ != null) {
            CommandSource lvt_3_1_ = lvt_2_1_.getCommandSource().withPermissionLevel(2);
            return (p_215937_2_) -> {
               try {
                  return TextComponentUtils.updateForEntity(lvt_3_1_, p_215937_2_, lvt_2_1_, 0);
               } catch (CommandSyntaxException var4) {
                  LOGGER.warn("Failed to resolve text component", var4);
                  return p_215937_2_;
               }
            };
         }
      }

      return (p_215938_0_) -> {
         return p_215938_0_;
      };
   }

   public ItemStack doApply(ItemStack p_215859_1_, LootContext p_215859_2_) {
      if (this.name != null) {
         p_215859_1_.setDisplayName((ITextComponent)func_215936_a(p_215859_2_, this.field_215940_d).apply(this.name));
      }

      return p_215859_1_;
   }

   // $FF: synthetic method
   SetName(ILootCondition[] p_i51219_1_, ITextComponent p_i51219_2_, LootContext.EntityTarget p_i51219_3_, Object p_i51219_4_) {
      this(p_i51219_1_, p_i51219_2_, p_i51219_3_);
   }

   public static class Serializer extends LootFunction.Serializer<SetName> {
      public Serializer() {
         super(new ResourceLocation("set_name"), SetName.class);
      }

      public void serialize(JsonObject p_186532_1_, SetName p_186532_2_, JsonSerializationContext p_186532_3_) {
         super.serialize(p_186532_1_, (LootFunction)p_186532_2_, p_186532_3_);
         if (p_186532_2_.name != null) {
            p_186532_1_.add("name", ITextComponent.Serializer.toJsonTree(p_186532_2_.name));
         }

         if (p_186532_2_.field_215940_d != null) {
            p_186532_1_.add("entity", p_186532_3_.serialize(p_186532_2_.field_215940_d));
         }

      }

      public SetName deserialize(JsonObject p_186530_1_, JsonDeserializationContext p_186530_2_, ILootCondition[] p_186530_3_) {
         ITextComponent lvt_4_1_ = ITextComponent.Serializer.fromJson(p_186530_1_.get("name"));
         LootContext.EntityTarget lvt_5_1_ = (LootContext.EntityTarget)JSONUtils.deserializeClass(p_186530_1_, "entity", (Object)null, p_186530_2_, LootContext.EntityTarget.class);
         return new SetName(p_186530_3_, lvt_4_1_, lvt_5_1_);
      }

      // $FF: synthetic method
      public LootFunction deserialize(JsonObject p_186530_1_, JsonDeserializationContext p_186530_2_, ILootCondition[] p_186530_3_) {
         return this.deserialize(p_186530_1_, p_186530_2_, p_186530_3_);
      }
   }
}
