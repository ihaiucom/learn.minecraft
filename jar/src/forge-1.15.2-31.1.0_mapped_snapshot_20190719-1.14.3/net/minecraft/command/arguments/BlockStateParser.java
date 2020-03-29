package net.minecraft.command.arguments;

import com.google.common.collect.Maps;
import com.google.common.collect.UnmodifiableIterator;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.Dynamic3CommandExceptionType;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.state.IProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.TranslationTextComponent;

public class BlockStateParser {
   public static final SimpleCommandExceptionType STATE_TAGS_NOT_ALLOWED = new SimpleCommandExceptionType(new TranslationTextComponent("argument.block.tag.disallowed", new Object[0]));
   public static final DynamicCommandExceptionType STATE_BAD_ID = new DynamicCommandExceptionType((p_lambda$static$0_0_) -> {
      return new TranslationTextComponent("argument.block.id.invalid", new Object[]{p_lambda$static$0_0_});
   });
   public static final Dynamic2CommandExceptionType STATE_UNKNOWN_PROPERTY = new Dynamic2CommandExceptionType((p_lambda$static$1_0_, p_lambda$static$1_1_) -> {
      return new TranslationTextComponent("argument.block.property.unknown", new Object[]{p_lambda$static$1_0_, p_lambda$static$1_1_});
   });
   public static final Dynamic2CommandExceptionType STATE_DUPLICATE_PROPERTY = new Dynamic2CommandExceptionType((p_lambda$static$2_0_, p_lambda$static$2_1_) -> {
      return new TranslationTextComponent("argument.block.property.duplicate", new Object[]{p_lambda$static$2_1_, p_lambda$static$2_0_});
   });
   public static final Dynamic3CommandExceptionType STATE_INVALID_PROPERTY_VALUE = new Dynamic3CommandExceptionType((p_lambda$static$3_0_, p_lambda$static$3_1_, p_lambda$static$3_2_) -> {
      return new TranslationTextComponent("argument.block.property.invalid", new Object[]{p_lambda$static$3_0_, p_lambda$static$3_2_, p_lambda$static$3_1_});
   });
   public static final Dynamic2CommandExceptionType STATE_NO_VALUE = new Dynamic2CommandExceptionType((p_lambda$static$4_0_, p_lambda$static$4_1_) -> {
      return new TranslationTextComponent("argument.block.property.novalue", new Object[]{p_lambda$static$4_0_, p_lambda$static$4_1_});
   });
   public static final SimpleCommandExceptionType STATE_UNCLOSED = new SimpleCommandExceptionType(new TranslationTextComponent("argument.block.property.unclosed", new Object[0]));
   private static final Function<SuggestionsBuilder, CompletableFuture<Suggestions>> SUGGEST_NONE = SuggestionsBuilder::buildFuture;
   private final StringReader reader;
   private final boolean tagsAllowed;
   private final Map<IProperty<?>, Comparable<?>> properties = Maps.newHashMap();
   private final Map<String, String> stringProperties = Maps.newHashMap();
   private ResourceLocation blockID = new ResourceLocation("");
   private StateContainer<Block, BlockState> blockStateContainer;
   private BlockState state;
   @Nullable
   private CompoundNBT nbt;
   private ResourceLocation tag = new ResourceLocation("");
   private int cursorPos;
   private Function<SuggestionsBuilder, CompletableFuture<Suggestions>> suggestor;

   public BlockStateParser(StringReader p_i48214_1_, boolean p_i48214_2_) {
      this.suggestor = SUGGEST_NONE;
      this.reader = p_i48214_1_;
      this.tagsAllowed = p_i48214_2_;
   }

   public Map<IProperty<?>, Comparable<?>> getProperties() {
      return this.properties;
   }

   @Nullable
   public BlockState getState() {
      return this.state;
   }

   @Nullable
   public CompoundNBT getNbt() {
      return this.nbt;
   }

   @Nullable
   public ResourceLocation getTag() {
      return this.tag;
   }

   public BlockStateParser parse(boolean p_197243_1_) throws CommandSyntaxException {
      this.suggestor = this::suggestTagOrBlock;
      if (this.reader.canRead() && this.reader.peek() == '#') {
         this.readTag();
         this.suggestor = this::func_212599_i;
         if (this.reader.canRead() && this.reader.peek() == '[') {
            this.readStringProperties();
            this.suggestor = this::suggestNbt;
         }
      } else {
         this.readBlock();
         this.suggestor = this::suggestPropertyOrNbt;
         if (this.reader.canRead() && this.reader.peek() == '[') {
            this.readProperties();
            this.suggestor = this::suggestNbt;
         }
      }

      if (p_197243_1_ && this.reader.canRead() && this.reader.peek() == '{') {
         this.suggestor = SUGGEST_NONE;
         this.readNBT();
      }

      return this;
   }

   private CompletableFuture<Suggestions> suggestPropertyOrEnd(SuggestionsBuilder p_197252_1_) {
      if (p_197252_1_.getRemaining().isEmpty()) {
         p_197252_1_.suggest(String.valueOf(']'));
      }

      return this.suggestProperty(p_197252_1_);
   }

   private CompletableFuture<Suggestions> suggestStringPropertyOrEnd(SuggestionsBuilder p_200136_1_) {
      if (p_200136_1_.getRemaining().isEmpty()) {
         p_200136_1_.suggest(String.valueOf(']'));
      }

      return this.suggestStringProperty(p_200136_1_);
   }

   private CompletableFuture<Suggestions> suggestProperty(SuggestionsBuilder p_197256_1_) {
      String s = p_197256_1_.getRemaining().toLowerCase(Locale.ROOT);
      Iterator var3 = this.state.getProperties().iterator();

      while(var3.hasNext()) {
         IProperty<?> iproperty = (IProperty)var3.next();
         if (!this.properties.containsKey(iproperty) && iproperty.getName().startsWith(s)) {
            p_197256_1_.suggest(iproperty.getName() + '=');
         }
      }

      return p_197256_1_.buildFuture();
   }

   private CompletableFuture<Suggestions> suggestStringProperty(SuggestionsBuilder p_200134_1_) {
      String s = p_200134_1_.getRemaining().toLowerCase(Locale.ROOT);
      if (this.tag != null && !this.tag.getPath().isEmpty()) {
         Tag<Block> tag = BlockTags.getCollection().get(this.tag);
         if (tag != null) {
            Iterator var4 = tag.getAllElements().iterator();

            while(var4.hasNext()) {
               Block block = (Block)var4.next();
               Iterator var6 = block.getStateContainer().getProperties().iterator();

               while(var6.hasNext()) {
                  IProperty<?> iproperty = (IProperty)var6.next();
                  if (!this.stringProperties.containsKey(iproperty.getName()) && iproperty.getName().startsWith(s)) {
                     p_200134_1_.suggest(iproperty.getName() + '=');
                  }
               }
            }
         }
      }

      return p_200134_1_.buildFuture();
   }

   private CompletableFuture<Suggestions> suggestNbt(SuggestionsBuilder p_197244_1_) {
      if (p_197244_1_.getRemaining().isEmpty() && this.func_212598_k()) {
         p_197244_1_.suggest(String.valueOf('{'));
      }

      return p_197244_1_.buildFuture();
   }

   private boolean func_212598_k() {
      if (this.state != null) {
         return this.state.hasTileEntity();
      } else {
         if (this.tag != null) {
            Tag<Block> tag = BlockTags.getCollection().get(this.tag);
            if (tag != null) {
               Iterator var2 = tag.getAllElements().iterator();

               while(var2.hasNext()) {
                  Block block = (Block)var2.next();
                  if (block.getDefaultState().hasTileEntity()) {
                     return true;
                  }
               }
            }
         }

         return false;
      }
   }

   private CompletableFuture<Suggestions> suggestEquals(SuggestionsBuilder p_197246_1_) {
      if (p_197246_1_.getRemaining().isEmpty()) {
         p_197246_1_.suggest(String.valueOf('='));
      }

      return p_197246_1_.buildFuture();
   }

   private CompletableFuture<Suggestions> suggestPropertyEndOrContinue(SuggestionsBuilder p_197248_1_) {
      if (p_197248_1_.getRemaining().isEmpty()) {
         p_197248_1_.suggest(String.valueOf(']'));
      }

      if (p_197248_1_.getRemaining().isEmpty() && this.properties.size() < this.state.getProperties().size()) {
         p_197248_1_.suggest(String.valueOf(','));
      }

      return p_197248_1_.buildFuture();
   }

   private static <T extends Comparable<T>> SuggestionsBuilder suggestValue(SuggestionsBuilder p_201037_0_, IProperty<T> p_201037_1_) {
      Iterator var2 = p_201037_1_.getAllowedValues().iterator();

      while(var2.hasNext()) {
         T t = (Comparable)var2.next();
         if (t instanceof Integer) {
            p_201037_0_.suggest((Integer)t);
         } else {
            p_201037_0_.suggest(p_201037_1_.getName(t));
         }
      }

      return p_201037_0_;
   }

   private CompletableFuture<Suggestions> suggestTagProperties(SuggestionsBuilder p_200140_1_, String p_200140_2_) {
      boolean flag = false;
      if (this.tag != null && !this.tag.getPath().isEmpty()) {
         Tag<Block> tag = BlockTags.getCollection().get(this.tag);
         if (tag != null) {
            Iterator var5 = tag.getAllElements().iterator();

            label42:
            while(true) {
               while(true) {
                  Block block;
                  do {
                     if (!var5.hasNext()) {
                        break label42;
                     }

                     block = (Block)var5.next();
                     IProperty<?> iproperty = block.getStateContainer().getProperty(p_200140_2_);
                     if (iproperty != null) {
                        suggestValue(p_200140_1_, iproperty);
                     }
                  } while(flag);

                  Iterator iterator = block.getStateContainer().getProperties().iterator();

                  while(iterator.hasNext()) {
                     IProperty<?> iproperty1 = (IProperty)iterator.next();
                     if (!this.stringProperties.containsKey(iproperty1.getName())) {
                        flag = true;
                        break;
                     }
                  }
               }
            }
         }
      }

      if (flag) {
         p_200140_1_.suggest(String.valueOf(','));
      }

      p_200140_1_.suggest(String.valueOf(']'));
      return p_200140_1_.buildFuture();
   }

   private CompletableFuture<Suggestions> func_212599_i(SuggestionsBuilder p_212599_1_) {
      if (p_212599_1_.getRemaining().isEmpty()) {
         Tag<Block> tag = BlockTags.getCollection().get(this.tag);
         if (tag != null) {
            boolean flag = false;
            boolean flag1 = false;
            Iterator var5 = tag.getAllElements().iterator();

            while(var5.hasNext()) {
               Block block = (Block)var5.next();
               flag |= !block.getStateContainer().getProperties().isEmpty();
               flag1 |= block.hasTileEntity();
               if (flag && flag1) {
                  break;
               }
            }

            if (flag) {
               p_212599_1_.suggest(String.valueOf('['));
            }

            if (flag1) {
               p_212599_1_.suggest(String.valueOf('{'));
            }
         }
      }

      return this.suggestTag(p_212599_1_);
   }

   private CompletableFuture<Suggestions> suggestPropertyOrNbt(SuggestionsBuilder p_197255_1_) {
      if (p_197255_1_.getRemaining().isEmpty()) {
         if (!this.state.getBlock().getStateContainer().getProperties().isEmpty()) {
            p_197255_1_.suggest(String.valueOf('['));
         }

         if (this.state.hasTileEntity()) {
            p_197255_1_.suggest(String.valueOf('{'));
         }
      }

      return p_197255_1_.buildFuture();
   }

   private CompletableFuture<Suggestions> suggestTag(SuggestionsBuilder p_201953_1_) {
      return ISuggestionProvider.suggestIterable(BlockTags.getCollection().getRegisteredTags(), p_201953_1_.createOffset(this.cursorPos).add(p_201953_1_));
   }

   private CompletableFuture<Suggestions> suggestTagOrBlock(SuggestionsBuilder p_197250_1_) {
      if (this.tagsAllowed) {
         ISuggestionProvider.suggestIterable(BlockTags.getCollection().getRegisteredTags(), p_197250_1_, String.valueOf('#'));
      }

      ISuggestionProvider.suggestIterable(Registry.BLOCK.keySet(), p_197250_1_);
      return p_197250_1_.buildFuture();
   }

   public void readBlock() throws CommandSyntaxException {
      int i = this.reader.getCursor();
      this.blockID = ResourceLocation.read(this.reader);
      Block block = (Block)Registry.BLOCK.getValue(this.blockID).orElseThrow(() -> {
         this.reader.setCursor(i);
         return STATE_BAD_ID.createWithContext(this.reader, this.blockID.toString());
      });
      this.blockStateContainer = block.getStateContainer();
      this.state = block.getDefaultState();
   }

   public void readTag() throws CommandSyntaxException {
      if (!this.tagsAllowed) {
         throw STATE_TAGS_NOT_ALLOWED.create();
      } else {
         this.suggestor = this::suggestTag;
         this.reader.expect('#');
         this.cursorPos = this.reader.getCursor();
         this.tag = ResourceLocation.read(this.reader);
      }
   }

   public void readProperties() throws CommandSyntaxException {
      this.reader.skip();
      this.suggestor = this::suggestPropertyOrEnd;
      this.reader.skipWhitespace();

      while(this.reader.canRead() && this.reader.peek() != ']') {
         this.reader.skipWhitespace();
         int i = this.reader.getCursor();
         String s = this.reader.readString();
         IProperty<?> iproperty = this.blockStateContainer.getProperty(s);
         if (iproperty == null) {
            this.reader.setCursor(i);
            throw STATE_UNKNOWN_PROPERTY.createWithContext(this.reader, this.blockID.toString(), s);
         }

         if (this.properties.containsKey(iproperty)) {
            this.reader.setCursor(i);
            throw STATE_DUPLICATE_PROPERTY.createWithContext(this.reader, this.blockID.toString(), s);
         }

         this.reader.skipWhitespace();
         this.suggestor = this::suggestEquals;
         if (this.reader.canRead() && this.reader.peek() == '=') {
            this.reader.skip();
            this.reader.skipWhitespace();
            this.suggestor = (p_lambda$readProperties$6_1_) -> {
               return suggestValue(p_lambda$readProperties$6_1_, iproperty).buildFuture();
            };
            int j = this.reader.getCursor();
            this.parseValue(iproperty, this.reader.readString(), j);
            this.suggestor = this::suggestPropertyEndOrContinue;
            this.reader.skipWhitespace();
            if (!this.reader.canRead()) {
               continue;
            }

            if (this.reader.peek() == ',') {
               this.reader.skip();
               this.suggestor = this::suggestProperty;
               continue;
            }

            if (this.reader.peek() != ']') {
               throw STATE_UNCLOSED.createWithContext(this.reader);
            }
            break;
         }

         throw STATE_NO_VALUE.createWithContext(this.reader, this.blockID.toString(), s);
      }

      if (this.reader.canRead()) {
         this.reader.skip();
      } else {
         throw STATE_UNCLOSED.createWithContext(this.reader);
      }
   }

   public void readStringProperties() throws CommandSyntaxException {
      this.reader.skip();
      this.suggestor = this::suggestStringPropertyOrEnd;
      int i = -1;
      this.reader.skipWhitespace();

      while(true) {
         if (this.reader.canRead() && this.reader.peek() != ']') {
            this.reader.skipWhitespace();
            int j = this.reader.getCursor();
            String s = this.reader.readString();
            if (this.stringProperties.containsKey(s)) {
               this.reader.setCursor(j);
               throw STATE_DUPLICATE_PROPERTY.createWithContext(this.reader, this.blockID.toString(), s);
            }

            this.reader.skipWhitespace();
            if (!this.reader.canRead() || this.reader.peek() != '=') {
               this.reader.setCursor(j);
               throw STATE_NO_VALUE.createWithContext(this.reader, this.blockID.toString(), s);
            }

            this.reader.skip();
            this.reader.skipWhitespace();
            this.suggestor = (p_lambda$readStringProperties$7_2_) -> {
               return this.suggestTagProperties(p_lambda$readStringProperties$7_2_, s);
            };
            i = this.reader.getCursor();
            String s1 = this.reader.readString();
            this.stringProperties.put(s, s1);
            this.reader.skipWhitespace();
            if (!this.reader.canRead()) {
               continue;
            }

            i = -1;
            if (this.reader.peek() == ',') {
               this.reader.skip();
               this.suggestor = this::suggestStringProperty;
               continue;
            }

            if (this.reader.peek() != ']') {
               throw STATE_UNCLOSED.createWithContext(this.reader);
            }
         }

         if (this.reader.canRead()) {
            this.reader.skip();
            return;
         }

         if (i >= 0) {
            this.reader.setCursor(i);
         }

         throw STATE_UNCLOSED.createWithContext(this.reader);
      }
   }

   public void readNBT() throws CommandSyntaxException {
      this.nbt = (new JsonToNBT(this.reader)).readStruct();
   }

   private <T extends Comparable<T>> void parseValue(IProperty<T> p_197253_1_, String p_197253_2_, int p_197253_3_) throws CommandSyntaxException {
      Optional<T> optional = p_197253_1_.parseValue(p_197253_2_);
      if (optional.isPresent()) {
         this.state = (BlockState)this.state.with(p_197253_1_, (Comparable)optional.get());
         this.properties.put(p_197253_1_, optional.get());
      } else {
         this.reader.setCursor(p_197253_3_);
         throw STATE_INVALID_PROPERTY_VALUE.createWithContext(this.reader, this.blockID.toString(), p_197253_1_.getName(), p_197253_2_);
      }
   }

   public static String toString(BlockState p_197247_0_) {
      StringBuilder stringbuilder = new StringBuilder(Registry.BLOCK.getKey(p_197247_0_.getBlock()).toString());
      if (!p_197247_0_.getProperties().isEmpty()) {
         stringbuilder.append('[');
         boolean flag = false;

         for(UnmodifiableIterator var3 = p_197247_0_.getValues().entrySet().iterator(); var3.hasNext(); flag = true) {
            Entry<IProperty<?>, Comparable<?>> entry = (Entry)var3.next();
            if (flag) {
               stringbuilder.append(',');
            }

            propValToString(stringbuilder, (IProperty)entry.getKey(), (Comparable)entry.getValue());
         }

         stringbuilder.append(']');
      }

      return stringbuilder.toString();
   }

   private static <T extends Comparable<T>> void propValToString(StringBuilder p_211375_0_, IProperty<T> p_211375_1_, Comparable<?> p_211375_2_) {
      p_211375_0_.append(p_211375_1_.getName());
      p_211375_0_.append('=');
      p_211375_0_.append(p_211375_1_.getName(p_211375_2_));
   }

   public CompletableFuture<Suggestions> getSuggestions(SuggestionsBuilder p_197245_1_) {
      return (CompletableFuture)this.suggestor.apply(p_197245_1_.createOffset(this.reader.getCursor()));
   }

   public Map<String, String> getStringProperties() {
      return this.stringProperties;
   }
}
