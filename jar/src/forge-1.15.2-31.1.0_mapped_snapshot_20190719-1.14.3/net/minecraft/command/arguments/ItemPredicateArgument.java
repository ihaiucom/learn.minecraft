package net.minecraft.command.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.command.CommandSource;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tags.Tag;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;

public class ItemPredicateArgument implements ArgumentType<ItemPredicateArgument.IResult> {
   private static final Collection<String> EXAMPLES = Arrays.asList("stick", "minecraft:stick", "#stick", "#stick{foo=bar}");
   private static final DynamicCommandExceptionType UNKNOWN_TAG = new DynamicCommandExceptionType((p_208699_0_) -> {
      return new TranslationTextComponent("arguments.item.tag.unknown", new Object[]{p_208699_0_});
   });

   public static ItemPredicateArgument itemPredicate() {
      return new ItemPredicateArgument();
   }

   public ItemPredicateArgument.IResult parse(StringReader p_parse_1_) throws CommandSyntaxException {
      ItemParser lvt_2_1_ = (new ItemParser(p_parse_1_, true)).parse();
      if (lvt_2_1_.getItem() != null) {
         ItemPredicateArgument.ItemPredicate lvt_3_1_ = new ItemPredicateArgument.ItemPredicate(lvt_2_1_.getItem(), lvt_2_1_.getNbt());
         return (p_199848_1_) -> {
            return lvt_3_1_;
         };
      } else {
         ResourceLocation lvt_3_2_ = lvt_2_1_.getTag();
         return (p_199845_2_) -> {
            Tag<Item> lvt_3_1_ = ((CommandSource)p_199845_2_.getSource()).getServer().getNetworkTagManager().getItems().get(lvt_3_2_);
            if (lvt_3_1_ == null) {
               throw UNKNOWN_TAG.create(lvt_3_2_.toString());
            } else {
               return new ItemPredicateArgument.TagPredicate(lvt_3_1_, lvt_2_1_.getNbt());
            }
         };
      }
   }

   public static Predicate<ItemStack> getItemPredicate(CommandContext<CommandSource> p_199847_0_, String p_199847_1_) throws CommandSyntaxException {
      return ((ItemPredicateArgument.IResult)p_199847_0_.getArgument(p_199847_1_, ItemPredicateArgument.IResult.class)).create(p_199847_0_);
   }

   public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> p_listSuggestions_1_, SuggestionsBuilder p_listSuggestions_2_) {
      StringReader lvt_3_1_ = new StringReader(p_listSuggestions_2_.getInput());
      lvt_3_1_.setCursor(p_listSuggestions_2_.getStart());
      ItemParser lvt_4_1_ = new ItemParser(lvt_3_1_, true);

      try {
         lvt_4_1_.parse();
      } catch (CommandSyntaxException var6) {
      }

      return lvt_4_1_.func_197329_a(p_listSuggestions_2_);
   }

   public Collection<String> getExamples() {
      return EXAMPLES;
   }

   // $FF: synthetic method
   public Object parse(StringReader p_parse_1_) throws CommandSyntaxException {
      return this.parse(p_parse_1_);
   }

   static class TagPredicate implements Predicate<ItemStack> {
      private final Tag<Item> tag;
      @Nullable
      private final CompoundNBT nbt;

      public TagPredicate(Tag<Item> p_i48220_1_, @Nullable CompoundNBT p_i48220_2_) {
         this.tag = p_i48220_1_;
         this.nbt = p_i48220_2_;
      }

      public boolean test(ItemStack p_test_1_) {
         return this.tag.contains(p_test_1_.getItem()) && NBTUtil.areNBTEquals(this.nbt, p_test_1_.getTag(), true);
      }

      // $FF: synthetic method
      public boolean test(Object p_test_1_) {
         return this.test((ItemStack)p_test_1_);
      }
   }

   static class ItemPredicate implements Predicate<ItemStack> {
      private final Item item;
      @Nullable
      private final CompoundNBT nbt;

      public ItemPredicate(Item p_i48221_1_, @Nullable CompoundNBT p_i48221_2_) {
         this.item = p_i48221_1_;
         this.nbt = p_i48221_2_;
      }

      public boolean test(ItemStack p_test_1_) {
         return p_test_1_.getItem() == this.item && NBTUtil.areNBTEquals(this.nbt, p_test_1_.getTag(), true);
      }

      // $FF: synthetic method
      public boolean test(Object p_test_1_) {
         return this.test((ItemStack)p_test_1_);
      }
   }

   public interface IResult {
      Predicate<ItemStack> create(CommandContext<CommandSource> var1) throws CommandSyntaxException;
   }
}
