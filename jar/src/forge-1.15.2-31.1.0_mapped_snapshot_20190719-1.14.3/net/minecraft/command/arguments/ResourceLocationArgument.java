package net.minecraft.command.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import java.util.Arrays;
import java.util.Collection;
import net.minecraft.advancements.Advancement;
import net.minecraft.command.CommandSource;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.storage.loot.LootPredicateManager;
import net.minecraft.world.storage.loot.conditions.ILootCondition;

public class ResourceLocationArgument implements ArgumentType<ResourceLocation> {
   private static final Collection<String> EXAMPLES = Arrays.asList("foo", "foo:bar", "012");
   private static final DynamicCommandExceptionType ADVANCEMENT_NOT_FOUND = new DynamicCommandExceptionType((p_208676_0_) -> {
      return new TranslationTextComponent("advancement.advancementNotFound", new Object[]{p_208676_0_});
   });
   private static final DynamicCommandExceptionType RECIPE_NOT_FOUND = new DynamicCommandExceptionType((p_208677_0_) -> {
      return new TranslationTextComponent("recipe.notFound", new Object[]{p_208677_0_});
   });
   private static final DynamicCommandExceptionType field_228258_d_ = new DynamicCommandExceptionType((p_208674_0_) -> {
      return new TranslationTextComponent("predicate.unknown", new Object[]{p_208674_0_});
   });

   public static ResourceLocationArgument resourceLocation() {
      return new ResourceLocationArgument();
   }

   public static Advancement getAdvancement(CommandContext<CommandSource> p_197198_0_, String p_197198_1_) throws CommandSyntaxException {
      ResourceLocation lvt_2_1_ = (ResourceLocation)p_197198_0_.getArgument(p_197198_1_, ResourceLocation.class);
      Advancement lvt_3_1_ = ((CommandSource)p_197198_0_.getSource()).getServer().getAdvancementManager().getAdvancement(lvt_2_1_);
      if (lvt_3_1_ == null) {
         throw ADVANCEMENT_NOT_FOUND.create(lvt_2_1_);
      } else {
         return lvt_3_1_;
      }
   }

   public static IRecipe<?> getRecipe(CommandContext<CommandSource> p_197194_0_, String p_197194_1_) throws CommandSyntaxException {
      RecipeManager lvt_2_1_ = ((CommandSource)p_197194_0_.getSource()).getServer().getRecipeManager();
      ResourceLocation lvt_3_1_ = (ResourceLocation)p_197194_0_.getArgument(p_197194_1_, ResourceLocation.class);
      return (IRecipe)lvt_2_1_.getRecipe(lvt_3_1_).orElseThrow(() -> {
         return RECIPE_NOT_FOUND.create(lvt_3_1_);
      });
   }

   public static ILootCondition func_228259_c_(CommandContext<CommandSource> p_228259_0_, String p_228259_1_) throws CommandSyntaxException {
      ResourceLocation lvt_2_1_ = (ResourceLocation)p_228259_0_.getArgument(p_228259_1_, ResourceLocation.class);
      LootPredicateManager lvt_3_1_ = ((CommandSource)p_228259_0_.getSource()).getServer().func_229736_aP_();
      ILootCondition lvt_4_1_ = lvt_3_1_.func_227517_a_(lvt_2_1_);
      if (lvt_4_1_ == null) {
         throw field_228258_d_.create(lvt_2_1_);
      } else {
         return lvt_4_1_;
      }
   }

   public static ResourceLocation getResourceLocation(CommandContext<CommandSource> p_197195_0_, String p_197195_1_) {
      return (ResourceLocation)p_197195_0_.getArgument(p_197195_1_, ResourceLocation.class);
   }

   public ResourceLocation parse(StringReader p_parse_1_) throws CommandSyntaxException {
      return ResourceLocation.read(p_parse_1_);
   }

   public Collection<String> getExamples() {
      return EXAMPLES;
   }

   // $FF: synthetic method
   public Object parse(StringReader p_parse_1_) throws CommandSyntaxException {
      return this.parse(p_parse_1_);
   }
}
