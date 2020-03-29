package net.minecraft.command.impl.data;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Locale;
import java.util.UUID;
import java.util.function.Function;
import net.minecraft.advancements.criterion.NBTPredicate;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.command.arguments.NBTPathArgument;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class EntityDataAccessor implements IDataAccessor {
   private static final SimpleCommandExceptionType DATA_ENTITY_INVALID = new SimpleCommandExceptionType(new TranslationTextComponent("commands.data.entity.invalid", new Object[0]));
   public static final Function<String, DataCommand.IDataProvider> DATA_PROVIDER = (p_218922_0_) -> {
      return new DataCommand.IDataProvider() {
         public IDataAccessor createAccessor(CommandContext<CommandSource> p_198919_1_) throws CommandSyntaxException {
            return new EntityDataAccessor(EntityArgument.getEntity(p_198919_1_, p_218922_0_));
         }

         public ArgumentBuilder<CommandSource, ?> createArgument(ArgumentBuilder<CommandSource, ?> p_198920_1_, Function<ArgumentBuilder<CommandSource, ?>, ArgumentBuilder<CommandSource, ?>> p_198920_2_) {
            return p_198920_1_.then(Commands.literal("entity").then((ArgumentBuilder)p_198920_2_.apply(Commands.argument(p_218922_0_, EntityArgument.entity()))));
         }
      };
   };
   private final Entity entity;

   public EntityDataAccessor(Entity p_i47917_1_) {
      this.entity = p_i47917_1_;
   }

   public void mergeData(CompoundNBT p_198925_1_) throws CommandSyntaxException {
      if (this.entity instanceof PlayerEntity) {
         throw DATA_ENTITY_INVALID.create();
      } else {
         UUID lvt_2_1_ = this.entity.getUniqueID();
         this.entity.read(p_198925_1_);
         this.entity.setUniqueId(lvt_2_1_);
      }
   }

   public CompoundNBT getData() {
      return NBTPredicate.writeToNBTWithSelectedItem(this.entity);
   }

   public ITextComponent getModifiedMessage() {
      return new TranslationTextComponent("commands.data.entity.modified", new Object[]{this.entity.getDisplayName()});
   }

   public ITextComponent getQueryMessage(INBT p_198924_1_) {
      return new TranslationTextComponent("commands.data.entity.query", new Object[]{this.entity.getDisplayName(), p_198924_1_.toFormattedComponent()});
   }

   public ITextComponent getGetMessage(NBTPathArgument.NBTPath p_198922_1_, double p_198922_2_, int p_198922_4_) {
      return new TranslationTextComponent("commands.data.entity.get", new Object[]{p_198922_1_, this.entity.getDisplayName(), String.format(Locale.ROOT, "%.2f", p_198922_2_), p_198922_4_});
   }
}
