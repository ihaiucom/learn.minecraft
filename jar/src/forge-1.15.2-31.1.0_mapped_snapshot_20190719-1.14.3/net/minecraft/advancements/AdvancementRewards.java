package net.minecraft.advancements;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.command.FunctionObject;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootParameterSets;
import net.minecraft.world.storage.loot.LootParameters;

public class AdvancementRewards {
   public static final AdvancementRewards EMPTY;
   private final int experience;
   private final ResourceLocation[] loot;
   private final ResourceLocation[] recipes;
   private final FunctionObject.CacheableFunction function;

   public AdvancementRewards(int p_i47587_1_, ResourceLocation[] p_i47587_2_, ResourceLocation[] p_i47587_3_, FunctionObject.CacheableFunction p_i47587_4_) {
      this.experience = p_i47587_1_;
      this.loot = p_i47587_2_;
      this.recipes = p_i47587_3_;
      this.function = p_i47587_4_;
   }

   public void apply(ServerPlayerEntity p_192113_1_) {
      p_192113_1_.giveExperiencePoints(this.experience);
      LootContext lootcontext = (new LootContext.Builder(p_192113_1_.getServerWorld())).withParameter(LootParameters.THIS_ENTITY, p_192113_1_).withParameter(LootParameters.POSITION, new BlockPos(p_192113_1_)).withRandom(p_192113_1_.getRNG()).withLuck(p_192113_1_.getLuck()).build(LootParameterSets.ADVANCEMENT);
      boolean flag = false;
      ResourceLocation[] var4 = this.loot;
      int var5 = var4.length;

      for(int var6 = 0; var6 < var5; ++var6) {
         ResourceLocation resourcelocation = var4[var6];
         Iterator var8 = p_192113_1_.server.getLootTableManager().getLootTableFromLocation(resourcelocation).generate(lootcontext).iterator();

         while(var8.hasNext()) {
            ItemStack itemstack = (ItemStack)var8.next();
            if (p_192113_1_.addItemStackToInventory(itemstack)) {
               p_192113_1_.world.playSound((PlayerEntity)null, p_192113_1_.func_226277_ct_(), p_192113_1_.func_226278_cu_(), p_192113_1_.func_226281_cx_(), SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.PLAYERS, 0.2F, ((p_192113_1_.getRNG().nextFloat() - p_192113_1_.getRNG().nextFloat()) * 0.7F + 1.0F) * 2.0F);
               flag = true;
            } else {
               ItemEntity itementity = p_192113_1_.dropItem(itemstack, false);
               if (itementity != null) {
                  itementity.setNoPickupDelay();
                  itementity.setOwnerId(p_192113_1_.getUniqueID());
               }
            }
         }
      }

      if (flag) {
         p_192113_1_.container.detectAndSendChanges();
      }

      if (this.recipes.length > 0) {
         p_192113_1_.unlockRecipes(this.recipes);
      }

      MinecraftServer minecraftserver = p_192113_1_.server;
      this.function.func_218039_a(minecraftserver.getFunctionManager()).ifPresent((p_lambda$apply$0_2_) -> {
         minecraftserver.getFunctionManager().execute(p_lambda$apply$0_2_, p_192113_1_.getCommandSource().withFeedbackDisabled().withPermissionLevel(2));
      });
   }

   public String toString() {
      return "AdvancementRewards{experience=" + this.experience + ", loot=" + Arrays.toString((Object[])this.loot) + ", recipes=" + Arrays.toString((Object[])this.recipes) + ", function=" + this.function + '}';
   }

   public JsonElement serialize() {
      if (this == EMPTY) {
         return JsonNull.INSTANCE;
      } else {
         JsonObject jsonobject = new JsonObject();
         if (this.experience != 0) {
            jsonobject.addProperty("experience", this.experience);
         }

         JsonArray jsonarray1;
         ResourceLocation[] var3;
         int var4;
         int var5;
         ResourceLocation resourcelocation1;
         if (this.loot.length > 0) {
            jsonarray1 = new JsonArray();
            var3 = this.loot;
            var4 = var3.length;

            for(var5 = 0; var5 < var4; ++var5) {
               resourcelocation1 = var3[var5];
               jsonarray1.add(resourcelocation1.toString());
            }

            jsonobject.add("loot", jsonarray1);
         }

         if (this.recipes.length > 0) {
            jsonarray1 = new JsonArray();
            var3 = this.recipes;
            var4 = var3.length;

            for(var5 = 0; var5 < var4; ++var5) {
               resourcelocation1 = var3[var5];
               jsonarray1.add(resourcelocation1.toString());
            }

            jsonobject.add("recipes", jsonarray1);
         }

         if (this.function.getId() != null) {
            jsonobject.addProperty("function", this.function.getId().toString());
         }

         return jsonobject;
      }
   }

   static {
      EMPTY = new AdvancementRewards(0, new ResourceLocation[0], new ResourceLocation[0], FunctionObject.CacheableFunction.EMPTY);
   }

   public static class Deserializer implements JsonDeserializer<AdvancementRewards> {
      public AdvancementRewards deserialize(JsonElement p_deserialize_1_, Type p_deserialize_2_, JsonDeserializationContext p_deserialize_3_) throws JsonParseException {
         JsonObject jsonobject = JSONUtils.getJsonObject(p_deserialize_1_, "rewards");
         int i = JSONUtils.getInt(jsonobject, "experience", 0);
         JsonArray jsonarray = JSONUtils.getJsonArray(jsonobject, "loot", new JsonArray());
         ResourceLocation[] aresourcelocation = new ResourceLocation[jsonarray.size()];

         for(int j = 0; j < aresourcelocation.length; ++j) {
            aresourcelocation[j] = new ResourceLocation(JSONUtils.getString(jsonarray.get(j), "loot[" + j + "]"));
         }

         JsonArray jsonarray1 = JSONUtils.getJsonArray(jsonobject, "recipes", new JsonArray());
         ResourceLocation[] aresourcelocation1 = new ResourceLocation[jsonarray1.size()];

         for(int k = 0; k < aresourcelocation1.length; ++k) {
            aresourcelocation1[k] = new ResourceLocation(JSONUtils.getString(jsonarray1.get(k), "recipes[" + k + "]"));
         }

         FunctionObject.CacheableFunction functionobject$cacheablefunction;
         if (jsonobject.has("function")) {
            functionobject$cacheablefunction = new FunctionObject.CacheableFunction(new ResourceLocation(JSONUtils.getString(jsonobject, "function")));
         } else {
            functionobject$cacheablefunction = FunctionObject.CacheableFunction.EMPTY;
         }

         return new AdvancementRewards(i, aresourcelocation, aresourcelocation1, functionobject$cacheablefunction);
      }
   }

   public static class Builder {
      private int experience;
      private final List<ResourceLocation> loot = Lists.newArrayList();
      private final List<ResourceLocation> recipes = Lists.newArrayList();
      @Nullable
      private ResourceLocation function;

      public static AdvancementRewards.Builder experience(int p_203907_0_) {
         return (new AdvancementRewards.Builder()).addExperience(p_203907_0_);
      }

      public AdvancementRewards.Builder addExperience(int p_203906_1_) {
         this.experience += p_203906_1_;
         return this;
      }

      public static AdvancementRewards.Builder recipe(ResourceLocation p_200280_0_) {
         return (new AdvancementRewards.Builder()).addRecipe(p_200280_0_);
      }

      public AdvancementRewards.Builder addRecipe(ResourceLocation p_200279_1_) {
         this.recipes.add(p_200279_1_);
         return this;
      }

      public AdvancementRewards build() {
         return new AdvancementRewards(this.experience, (ResourceLocation[])this.loot.toArray(new ResourceLocation[0]), (ResourceLocation[])this.recipes.toArray(new ResourceLocation[0]), this.function == null ? FunctionObject.CacheableFunction.EMPTY : new FunctionObject.CacheableFunction(this.function));
      }
   }
}
