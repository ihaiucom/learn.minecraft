package net.minecraft.command.impl.data;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import java.util.Locale;
import java.util.function.Function;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.command.arguments.NBTPathArgument;
import net.minecraft.command.arguments.ResourceLocationArgument;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.storage.CommandStorage;

public class StorageAccessor implements IDataAccessor {
   private static final SuggestionProvider<CommandSource> field_229834_b_ = (p_229838_0_, p_229838_1_) -> {
      return ISuggestionProvider.func_212476_a(func_229840_b_(p_229838_0_).func_227484_a_(), p_229838_1_);
   };
   public static final Function<String, DataCommand.IDataProvider> field_229833_a_ = (p_229839_0_) -> {
      return new DataCommand.IDataProvider() {
         public IDataAccessor createAccessor(CommandContext<CommandSource> p_198919_1_) {
            return new StorageAccessor(StorageAccessor.func_229840_b_(p_198919_1_), ResourceLocationArgument.getResourceLocation(p_198919_1_, p_229839_0_));
         }

         public ArgumentBuilder<CommandSource, ?> createArgument(ArgumentBuilder<CommandSource, ?> p_198920_1_, Function<ArgumentBuilder<CommandSource, ?>, ArgumentBuilder<CommandSource, ?>> p_198920_2_) {
            return p_198920_1_.then(Commands.literal("storage").then((ArgumentBuilder)p_198920_2_.apply(Commands.argument(p_229839_0_, ResourceLocationArgument.resourceLocation()).suggests(StorageAccessor.field_229834_b_))));
         }
      };
   };
   private final CommandStorage field_229835_c_;
   private final ResourceLocation field_229836_d_;

   private static CommandStorage func_229840_b_(CommandContext<CommandSource> p_229840_0_) {
      return ((CommandSource)p_229840_0_.getSource()).getServer().func_229735_aN_();
   }

   private StorageAccessor(CommandStorage p_i226092_1_, ResourceLocation p_i226092_2_) {
      this.field_229835_c_ = p_i226092_1_;
      this.field_229836_d_ = p_i226092_2_;
   }

   public void mergeData(CompoundNBT p_198925_1_) {
      this.field_229835_c_.func_227489_a_(this.field_229836_d_, p_198925_1_);
   }

   public CompoundNBT getData() {
      return this.field_229835_c_.func_227488_a_(this.field_229836_d_);
   }

   public ITextComponent getModifiedMessage() {
      return new TranslationTextComponent("commands.data.storage.modified", new Object[]{this.field_229836_d_});
   }

   public ITextComponent getQueryMessage(INBT p_198924_1_) {
      return new TranslationTextComponent("commands.data.storage.query", new Object[]{this.field_229836_d_, p_198924_1_.toFormattedComponent()});
   }

   public ITextComponent getGetMessage(NBTPathArgument.NBTPath p_198922_1_, double p_198922_2_, int p_198922_4_) {
      return new TranslationTextComponent("commands.data.storage.get", new Object[]{p_198922_1_, this.field_229836_d_, String.format(Locale.ROOT, "%.2f", p_198922_2_), p_198922_4_});
   }

   // $FF: synthetic method
   StorageAccessor(CommandStorage p_i226093_1_, ResourceLocation p_i226093_2_, Object p_i226093_3_) {
      this(p_i226093_1_, p_i226093_2_);
   }
}
