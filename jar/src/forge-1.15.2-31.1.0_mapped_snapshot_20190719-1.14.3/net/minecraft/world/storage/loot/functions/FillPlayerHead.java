package net.minecraft.world.storage.loot.functions;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.mojang.authlib.GameProfile;
import java.util.Set;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootFunction;
import net.minecraft.world.storage.loot.LootParameter;
import net.minecraft.world.storage.loot.conditions.ILootCondition;

public class FillPlayerHead extends LootFunction {
   private final LootContext.EntityTarget field_215902_a;

   public FillPlayerHead(ILootCondition[] p_i51234_1_, LootContext.EntityTarget p_i51234_2_) {
      super(p_i51234_1_);
      this.field_215902_a = p_i51234_2_;
   }

   public Set<LootParameter<?>> getRequiredParameters() {
      return ImmutableSet.of(this.field_215902_a.getParameter());
   }

   public ItemStack doApply(ItemStack p_215859_1_, LootContext p_215859_2_) {
      if (p_215859_1_.getItem() == Items.PLAYER_HEAD) {
         Entity lvt_3_1_ = (Entity)p_215859_2_.get(this.field_215902_a.getParameter());
         if (lvt_3_1_ instanceof PlayerEntity) {
            GameProfile lvt_4_1_ = ((PlayerEntity)lvt_3_1_).getGameProfile();
            p_215859_1_.getOrCreateTag().put("SkullOwner", NBTUtil.writeGameProfile(new CompoundNBT(), lvt_4_1_));
         }
      }

      return p_215859_1_;
   }

   public static class Serializer extends LootFunction.Serializer<FillPlayerHead> {
      public Serializer() {
         super(new ResourceLocation("fill_player_head"), FillPlayerHead.class);
      }

      public void serialize(JsonObject p_186532_1_, FillPlayerHead p_186532_2_, JsonSerializationContext p_186532_3_) {
         super.serialize(p_186532_1_, (LootFunction)p_186532_2_, p_186532_3_);
         p_186532_1_.add("entity", p_186532_3_.serialize(p_186532_2_.field_215902_a));
      }

      public FillPlayerHead deserialize(JsonObject p_186530_1_, JsonDeserializationContext p_186530_2_, ILootCondition[] p_186530_3_) {
         LootContext.EntityTarget lvt_4_1_ = (LootContext.EntityTarget)JSONUtils.deserializeClass(p_186530_1_, "entity", p_186530_2_, LootContext.EntityTarget.class);
         return new FillPlayerHead(p_186530_3_, lvt_4_1_);
      }

      // $FF: synthetic method
      public LootFunction deserialize(JsonObject p_186530_1_, JsonDeserializationContext p_186530_2_, ILootCondition[] p_186530_3_) {
         return this.deserialize(p_186530_1_, p_186530_2_, p_186530_3_);
      }
   }
}
