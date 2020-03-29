package net.minecraft.item.crafting;

import com.google.gson.JsonObject;
import javax.annotation.Nullable;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.registries.IForgeRegistryEntry;

public interface IRecipeSerializer<T extends IRecipe<?>> extends IForgeRegistryEntry<IRecipeSerializer<?>> {
   IRecipeSerializer<ShapedRecipe> CRAFTING_SHAPED = register("crafting_shaped", new ShapedRecipe.Serializer());
   IRecipeSerializer<ShapelessRecipe> CRAFTING_SHAPELESS = register("crafting_shapeless", new ShapelessRecipe.Serializer());
   SpecialRecipeSerializer<ArmorDyeRecipe> CRAFTING_SPECIAL_ARMORDYE = (SpecialRecipeSerializer)register("crafting_special_armordye", new SpecialRecipeSerializer(ArmorDyeRecipe::new));
   SpecialRecipeSerializer<BookCloningRecipe> CRAFTING_SPECIAL_BOOKCLONING = (SpecialRecipeSerializer)register("crafting_special_bookcloning", new SpecialRecipeSerializer(BookCloningRecipe::new));
   SpecialRecipeSerializer<MapCloningRecipe> CRAFTING_SPECIAL_MAPCLONING = (SpecialRecipeSerializer)register("crafting_special_mapcloning", new SpecialRecipeSerializer(MapCloningRecipe::new));
   SpecialRecipeSerializer<MapExtendingRecipe> CRAFTING_SPECIAL_MAPEXTENDING = (SpecialRecipeSerializer)register("crafting_special_mapextending", new SpecialRecipeSerializer(MapExtendingRecipe::new));
   SpecialRecipeSerializer<FireworkRocketRecipe> CRAFTING_SPECIAL_FIREWORK_ROCKET = (SpecialRecipeSerializer)register("crafting_special_firework_rocket", new SpecialRecipeSerializer(FireworkRocketRecipe::new));
   SpecialRecipeSerializer<FireworkStarRecipe> CRAFTING_SPECIAL_FIREWORK_STAR = (SpecialRecipeSerializer)register("crafting_special_firework_star", new SpecialRecipeSerializer(FireworkStarRecipe::new));
   SpecialRecipeSerializer<FireworkStarFadeRecipe> CRAFTING_SPECIAL_FIREWORK_STAR_FADE = (SpecialRecipeSerializer)register("crafting_special_firework_star_fade", new SpecialRecipeSerializer(FireworkStarFadeRecipe::new));
   SpecialRecipeSerializer<TippedArrowRecipe> CRAFTING_SPECIAL_TIPPEDARROW = (SpecialRecipeSerializer)register("crafting_special_tippedarrow", new SpecialRecipeSerializer(TippedArrowRecipe::new));
   SpecialRecipeSerializer<BannerDuplicateRecipe> CRAFTING_SPECIAL_BANNERDUPLICATE = (SpecialRecipeSerializer)register("crafting_special_bannerduplicate", new SpecialRecipeSerializer(BannerDuplicateRecipe::new));
   SpecialRecipeSerializer<ShieldRecipes> CRAFTING_SPECIAL_SHIELD = (SpecialRecipeSerializer)register("crafting_special_shielddecoration", new SpecialRecipeSerializer(ShieldRecipes::new));
   SpecialRecipeSerializer<ShulkerBoxColoringRecipe> CRAFTING_SPECIAL_SHULKERBOXCOLORING = (SpecialRecipeSerializer)register("crafting_special_shulkerboxcoloring", new SpecialRecipeSerializer(ShulkerBoxColoringRecipe::new));
   SpecialRecipeSerializer<SuspiciousStewRecipe> CRAFTING_SPECIAL_SUSPICIOUSSTEW = (SpecialRecipeSerializer)register("crafting_special_suspiciousstew", new SpecialRecipeSerializer(SuspiciousStewRecipe::new));
   SpecialRecipeSerializer<RepairItemRecipe> field_223550_o = (SpecialRecipeSerializer)register("crafting_special_repairitem", new SpecialRecipeSerializer(RepairItemRecipe::new));
   CookingRecipeSerializer<FurnaceRecipe> SMELTING = (CookingRecipeSerializer)register("smelting", new CookingRecipeSerializer(FurnaceRecipe::new, 200));
   CookingRecipeSerializer<BlastingRecipe> BLASTING = (CookingRecipeSerializer)register("blasting", new CookingRecipeSerializer(BlastingRecipe::new, 100));
   CookingRecipeSerializer<SmokingRecipe> SMOKING = (CookingRecipeSerializer)register("smoking", new CookingRecipeSerializer(SmokingRecipe::new, 100));
   CookingRecipeSerializer<CampfireCookingRecipe> CAMPFIRE_COOKING = (CookingRecipeSerializer)register("campfire_cooking", new CookingRecipeSerializer(CampfireCookingRecipe::new, 100));
   IRecipeSerializer<StonecuttingRecipe> STONECUTTING = register("stonecutting", new SingleItemRecipe.Serializer(StonecuttingRecipe::new));

   T read(ResourceLocation var1, JsonObject var2);

   @Nullable
   T read(ResourceLocation var1, PacketBuffer var2);

   void write(PacketBuffer var1, T var2);

   static <S extends IRecipeSerializer<T>, T extends IRecipe<?>> S register(String p_222156_0_, S p_222156_1_) {
      return (IRecipeSerializer)Registry.register((Registry)Registry.RECIPE_SERIALIZER, (String)p_222156_0_, (Object)p_222156_1_);
   }
}
