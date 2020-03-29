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
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.command.CommandSource;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.state.IProperty;
import net.minecraft.tags.NetworkTagManager;
import net.minecraft.tags.Tag;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.CachedBlockInfo;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;

public class BlockPredicateArgument implements ArgumentType<BlockPredicateArgument.IResult> {
   private static final Collection<String> EXAMPLES = Arrays.asList("stone", "minecraft:stone", "stone[foo=bar]", "#stone", "#stone[foo=bar]{baz=nbt}");
   private static final DynamicCommandExceptionType UNKNOWN_TAG = new DynamicCommandExceptionType((p_208682_0_) -> {
      return new TranslationTextComponent("arguments.block.tag.unknown", new Object[]{p_208682_0_});
   });

   public static BlockPredicateArgument blockPredicate() {
      return new BlockPredicateArgument();
   }

   public BlockPredicateArgument.IResult parse(StringReader p_parse_1_) throws CommandSyntaxException {
      BlockStateParser lvt_2_1_ = (new BlockStateParser(p_parse_1_, true)).parse(true);
      if (lvt_2_1_.getState() != null) {
         BlockPredicateArgument.BlockPredicate lvt_3_1_ = new BlockPredicateArgument.BlockPredicate(lvt_2_1_.getState(), lvt_2_1_.getProperties().keySet(), lvt_2_1_.getNbt());
         return (p_199823_1_) -> {
            return lvt_3_1_;
         };
      } else {
         ResourceLocation lvt_3_2_ = lvt_2_1_.getTag();
         return (p_199822_2_) -> {
            Tag<Block> lvt_3_1_ = p_199822_2_.getBlocks().get(lvt_3_2_);
            if (lvt_3_1_ == null) {
               throw UNKNOWN_TAG.create(lvt_3_2_.toString());
            } else {
               return new BlockPredicateArgument.TagPredicate(lvt_3_1_, lvt_2_1_.getStringProperties(), lvt_2_1_.getNbt());
            }
         };
      }
   }

   public static Predicate<CachedBlockInfo> getBlockPredicate(CommandContext<CommandSource> p_199825_0_, String p_199825_1_) throws CommandSyntaxException {
      return ((BlockPredicateArgument.IResult)p_199825_0_.getArgument(p_199825_1_, BlockPredicateArgument.IResult.class)).create(((CommandSource)p_199825_0_.getSource()).getServer().getNetworkTagManager());
   }

   public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> p_listSuggestions_1_, SuggestionsBuilder p_listSuggestions_2_) {
      StringReader lvt_3_1_ = new StringReader(p_listSuggestions_2_.getInput());
      lvt_3_1_.setCursor(p_listSuggestions_2_.getStart());
      BlockStateParser lvt_4_1_ = new BlockStateParser(lvt_3_1_, true);

      try {
         lvt_4_1_.parse(true);
      } catch (CommandSyntaxException var6) {
      }

      return lvt_4_1_.getSuggestions(p_listSuggestions_2_);
   }

   public Collection<String> getExamples() {
      return EXAMPLES;
   }

   // $FF: synthetic method
   public Object parse(StringReader p_parse_1_) throws CommandSyntaxException {
      return this.parse(p_parse_1_);
   }

   static class TagPredicate implements Predicate<CachedBlockInfo> {
      private final Tag<Block> tag;
      @Nullable
      private final CompoundNBT nbt;
      private final Map<String, String> properties;

      private TagPredicate(Tag<Block> p_i48238_1_, Map<String, String> p_i48238_2_, @Nullable CompoundNBT p_i48238_3_) {
         this.tag = p_i48238_1_;
         this.properties = p_i48238_2_;
         this.nbt = p_i48238_3_;
      }

      public boolean test(CachedBlockInfo p_test_1_) {
         BlockState lvt_2_1_ = p_test_1_.getBlockState();
         if (!lvt_2_1_.isIn(this.tag)) {
            return false;
         } else {
            Iterator var3 = this.properties.entrySet().iterator();

            while(var3.hasNext()) {
               Entry<String, String> lvt_4_1_ = (Entry)var3.next();
               IProperty<?> lvt_5_1_ = lvt_2_1_.getBlock().getStateContainer().getProperty((String)lvt_4_1_.getKey());
               if (lvt_5_1_ == null) {
                  return false;
               }

               Comparable<?> lvt_6_1_ = (Comparable)lvt_5_1_.parseValue((String)lvt_4_1_.getValue()).orElse((Object)null);
               if (lvt_6_1_ == null) {
                  return false;
               }

               if (lvt_2_1_.get(lvt_5_1_) != lvt_6_1_) {
                  return false;
               }
            }

            if (this.nbt == null) {
               return true;
            } else {
               TileEntity lvt_3_1_ = p_test_1_.getTileEntity();
               return lvt_3_1_ != null && NBTUtil.areNBTEquals(this.nbt, lvt_3_1_.write(new CompoundNBT()), true);
            }
         }
      }

      // $FF: synthetic method
      public boolean test(Object p_test_1_) {
         return this.test((CachedBlockInfo)p_test_1_);
      }

      // $FF: synthetic method
      TagPredicate(Tag p_i48239_1_, Map p_i48239_2_, CompoundNBT p_i48239_3_, Object p_i48239_4_) {
         this(p_i48239_1_, p_i48239_2_, p_i48239_3_);
      }
   }

   static class BlockPredicate implements Predicate<CachedBlockInfo> {
      private final BlockState state;
      private final Set<IProperty<?>> properties;
      @Nullable
      private final CompoundNBT nbt;

      public BlockPredicate(BlockState p_i48210_1_, Set<IProperty<?>> p_i48210_2_, @Nullable CompoundNBT p_i48210_3_) {
         this.state = p_i48210_1_;
         this.properties = p_i48210_2_;
         this.nbt = p_i48210_3_;
      }

      public boolean test(CachedBlockInfo p_test_1_) {
         BlockState lvt_2_1_ = p_test_1_.getBlockState();
         if (lvt_2_1_.getBlock() != this.state.getBlock()) {
            return false;
         } else {
            Iterator var3 = this.properties.iterator();

            while(var3.hasNext()) {
               IProperty<?> lvt_4_1_ = (IProperty)var3.next();
               if (lvt_2_1_.get(lvt_4_1_) != this.state.get(lvt_4_1_)) {
                  return false;
               }
            }

            if (this.nbt == null) {
               return true;
            } else {
               TileEntity lvt_3_1_ = p_test_1_.getTileEntity();
               return lvt_3_1_ != null && NBTUtil.areNBTEquals(this.nbt, lvt_3_1_.write(new CompoundNBT()), true);
            }
         }
      }

      // $FF: synthetic method
      public boolean test(Object p_test_1_) {
         return this.test((CachedBlockInfo)p_test_1_);
      }
   }

   public interface IResult {
      Predicate<CachedBlockInfo> create(NetworkTagManager var1) throws CommandSyntaxException;
   }
}
