package net.minecraft.item.crafting;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.client.resources.JsonReloadListener;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.crafting.CraftingHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RecipeManager extends JsonReloadListener {
   private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();
   private static final Logger LOGGER = LogManager.getLogger();
   private Map<IRecipeType<?>, Map<ResourceLocation, IRecipe<?>>> recipes = ImmutableMap.of();
   private boolean someRecipesErrored;

   public RecipeManager() {
      super(GSON, "recipes");
   }

   protected void apply(Map<ResourceLocation, JsonObject> p_212853_1_, IResourceManager p_212853_2_, IProfiler p_212853_3_) {
      this.someRecipesErrored = false;
      Map<IRecipeType<?>, Builder<ResourceLocation, IRecipe<?>>> map = Maps.newHashMap();
      Iterator var5 = p_212853_1_.entrySet().iterator();

      while(var5.hasNext()) {
         Entry<ResourceLocation, JsonObject> entry = (Entry)var5.next();
         ResourceLocation resourcelocation = (ResourceLocation)entry.getKey();
         if (!resourcelocation.getPath().startsWith("_")) {
            try {
               if (!CraftingHelper.processConditions((JsonObject)entry.getValue(), "conditions")) {
                  LOGGER.info("Skipping loading recipe {} as it's conditions were not met", resourcelocation);
               } else {
                  IRecipe<?> irecipe = deserializeRecipe(resourcelocation, (JsonObject)entry.getValue());
                  if (irecipe == null) {
                     LOGGER.info("Skipping loading recipe {} as it's serializer returned null", resourcelocation);
                  } else {
                     ((Builder)map.computeIfAbsent(irecipe.getType(), (p_lambda$apply$0_0_) -> {
                        return ImmutableMap.builder();
                     })).put(resourcelocation, irecipe);
                  }
               }
            } catch (JsonParseException | IllegalArgumentException var9) {
               LOGGER.error("Parsing error loading recipe {}", resourcelocation, var9);
            }
         }
      }

      this.recipes = (Map)map.entrySet().stream().collect(ImmutableMap.toImmutableMap(Entry::getKey, (p_lambda$apply$1_0_) -> {
         return ((Builder)p_lambda$apply$1_0_.getValue()).build();
      }));
      LOGGER.info("Loaded {} recipes", map.size());
   }

   public <C extends IInventory, T extends IRecipe<C>> Optional<T> getRecipe(IRecipeType<T> p_215371_1_, C p_215371_2_, World p_215371_3_) {
      return this.getRecipes(p_215371_1_).values().stream().flatMap((p_lambda$getRecipe$2_3_) -> {
         return Util.streamOptional(p_215371_1_.matches(p_lambda$getRecipe$2_3_, p_215371_3_, p_215371_2_));
      }).findFirst();
   }

   public <C extends IInventory, T extends IRecipe<C>> List<T> getRecipes(IRecipeType<T> p_215370_1_, C p_215370_2_, World p_215370_3_) {
      return (List)this.getRecipes(p_215370_1_).values().stream().flatMap((p_lambda$getRecipes$3_3_) -> {
         return Util.streamOptional(p_215370_1_.matches(p_lambda$getRecipes$3_3_, p_215370_3_, p_215370_2_));
      }).sorted(Comparator.comparing((p_lambda$getRecipes$4_0_) -> {
         return p_lambda$getRecipes$4_0_.getRecipeOutput().getTranslationKey();
      })).collect(Collectors.toList());
   }

   private <C extends IInventory, T extends IRecipe<C>> Map<ResourceLocation, IRecipe<C>> getRecipes(IRecipeType<T> p_215366_1_) {
      return (Map)this.recipes.getOrDefault(p_215366_1_, Collections.emptyMap());
   }

   public <C extends IInventory, T extends IRecipe<C>> NonNullList<ItemStack> getRecipeNonNull(IRecipeType<T> p_215369_1_, C p_215369_2_, World p_215369_3_) {
      Optional<T> optional = this.getRecipe(p_215369_1_, p_215369_2_, p_215369_3_);
      if (optional.isPresent()) {
         return ((IRecipe)optional.get()).getRemainingItems(p_215369_2_);
      } else {
         NonNullList<ItemStack> nonnulllist = NonNullList.withSize(p_215369_2_.getSizeInventory(), ItemStack.EMPTY);

         for(int i = 0; i < nonnulllist.size(); ++i) {
            nonnulllist.set(i, p_215369_2_.getStackInSlot(i));
         }

         return nonnulllist;
      }
   }

   public Optional<? extends IRecipe<?>> getRecipe(ResourceLocation p_215367_1_) {
      return this.recipes.values().stream().map((p_lambda$getRecipe$5_1_) -> {
         return (IRecipe)p_lambda$getRecipe$5_1_.get(p_215367_1_);
      }).filter(Objects::nonNull).findFirst();
   }

   public Collection<IRecipe<?>> getRecipes() {
      return (Collection)this.recipes.values().stream().flatMap((p_lambda$getRecipes$6_0_) -> {
         return p_lambda$getRecipes$6_0_.values().stream();
      }).collect(Collectors.toSet());
   }

   public Stream<ResourceLocation> getKeys() {
      return this.recipes.values().stream().flatMap((p_lambda$getKeys$7_0_) -> {
         return p_lambda$getKeys$7_0_.keySet().stream();
      });
   }

   public static IRecipe<?> deserializeRecipe(ResourceLocation p_215377_0_, JsonObject p_215377_1_) {
      String s = JSONUtils.getString(p_215377_1_, "type");
      return ((IRecipeSerializer)Registry.RECIPE_SERIALIZER.getValue(new ResourceLocation(s)).orElseThrow(() -> {
         return new JsonSyntaxException("Invalid or unsupported recipe type '" + s + "'");
      })).read(p_215377_0_, p_215377_1_);
   }

   @OnlyIn(Dist.CLIENT)
   public void func_223389_a(Iterable<IRecipe<?>> p_223389_1_) {
      this.someRecipesErrored = false;
      Map<IRecipeType<?>, Map<ResourceLocation, IRecipe<?>>> map = Maps.newHashMap();
      p_223389_1_.forEach((p_lambda$func_223389_a$10_1_) -> {
         Map<ResourceLocation, IRecipe<?>> map1 = (Map)map.computeIfAbsent(p_lambda$func_223389_a$10_1_.getType(), (p_lambda$null$9_0_) -> {
            return Maps.newHashMap();
         });
         IRecipe<?> irecipe = (IRecipe)map1.put(p_lambda$func_223389_a$10_1_.getId(), p_lambda$func_223389_a$10_1_);
         if (irecipe != null) {
            throw new IllegalStateException("Duplicate recipe ignored with ID " + p_lambda$func_223389_a$10_1_.getId());
         }
      });
      this.recipes = ImmutableMap.copyOf(map);
   }
}
