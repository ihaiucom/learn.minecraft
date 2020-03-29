package net.minecraft.command.impl.data;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.NBTCompoundTagArgument;
import net.minecraft.command.arguments.NBTPathArgument;
import net.minecraft.command.arguments.NBTTagArgument;
import net.minecraft.nbt.CollectionNBT;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.NumberNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TranslationTextComponent;

public class DataCommand {
   private static final SimpleCommandExceptionType NOTHING_CHANGED = new SimpleCommandExceptionType(new TranslationTextComponent("commands.data.merge.failed", new Object[0]));
   private static final DynamicCommandExceptionType GET_INVALID_EXCEPTION = new DynamicCommandExceptionType((p_208922_0_) -> {
      return new TranslationTextComponent("commands.data.get.invalid", new Object[]{p_208922_0_});
   });
   private static final DynamicCommandExceptionType GET_UNKNOWN_EXCEPTION = new DynamicCommandExceptionType((p_208919_0_) -> {
      return new TranslationTextComponent("commands.data.get.unknown", new Object[]{p_208919_0_});
   });
   private static final SimpleCommandExceptionType field_218957_g = new SimpleCommandExceptionType(new TranslationTextComponent("commands.data.get.multiple", new Object[0]));
   private static final DynamicCommandExceptionType field_218958_h = new DynamicCommandExceptionType((p_218931_0_) -> {
      return new TranslationTextComponent("commands.data.modify.expected_list", new Object[]{p_218931_0_});
   });
   private static final DynamicCommandExceptionType field_218959_i = new DynamicCommandExceptionType((p_218948_0_) -> {
      return new TranslationTextComponent("commands.data.modify.expected_object", new Object[]{p_218948_0_});
   });
   private static final DynamicCommandExceptionType field_218960_j = new DynamicCommandExceptionType((p_218943_0_) -> {
      return new TranslationTextComponent("commands.data.modify.invalid_index", new Object[]{p_218943_0_});
   });
   public static final List<Function<String, DataCommand.IDataProvider>> DATA_PROVIDERS;
   public static final List<DataCommand.IDataProvider> field_218955_b;
   public static final List<DataCommand.IDataProvider> field_218956_c;

   public static void register(CommandDispatcher<CommandSource> p_198937_0_) {
      LiteralArgumentBuilder<CommandSource> lvt_1_1_ = (LiteralArgumentBuilder)Commands.literal("data").requires((p_198939_0_) -> {
         return p_198939_0_.hasPermissionLevel(2);
      });
      Iterator var2 = field_218955_b.iterator();

      while(var2.hasNext()) {
         DataCommand.IDataProvider lvt_3_1_ = (DataCommand.IDataProvider)var2.next();
         ((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)lvt_1_1_.then(lvt_3_1_.createArgument(Commands.literal("merge"), (p_198943_1_) -> {
            return p_198943_1_.then(Commands.argument("nbt", NBTCompoundTagArgument.func_218043_a()).executes((p_198936_1_) -> {
               return merge((CommandSource)p_198936_1_.getSource(), lvt_3_1_.createAccessor(p_198936_1_), NBTCompoundTagArgument.func_218042_a(p_198936_1_, "nbt"));
            }));
         }))).then(lvt_3_1_.createArgument(Commands.literal("get"), (p_198940_1_) -> {
            return p_198940_1_.executes((p_198944_1_) -> {
               return get((CommandSource)p_198944_1_.getSource(), lvt_3_1_.createAccessor(p_198944_1_));
            }).then(((RequiredArgumentBuilder)Commands.argument("path", NBTPathArgument.nbtPath()).executes((p_198945_1_) -> {
               return get((CommandSource)p_198945_1_.getSource(), lvt_3_1_.createAccessor(p_198945_1_), NBTPathArgument.getNBTPath(p_198945_1_, "path"));
            })).then(Commands.argument("scale", DoubleArgumentType.doubleArg()).executes((p_198935_1_) -> {
               return getScaled((CommandSource)p_198935_1_.getSource(), lvt_3_1_.createAccessor(p_198935_1_), NBTPathArgument.getNBTPath(p_198935_1_, "path"), DoubleArgumentType.getDouble(p_198935_1_, "scale"));
            })));
         }))).then(lvt_3_1_.createArgument(Commands.literal("remove"), (p_198934_1_) -> {
            return p_198934_1_.then(Commands.argument("path", NBTPathArgument.nbtPath()).executes((p_198941_1_) -> {
               return remove((CommandSource)p_198941_1_.getSource(), lvt_3_1_.createAccessor(p_198941_1_), NBTPathArgument.getNBTPath(p_198941_1_, "path"));
            }));
         }))).then(func_218935_a((p_218924_0_, p_218924_1_) -> {
            p_218924_0_.then(Commands.literal("insert").then(Commands.argument("index", IntegerArgumentType.integer()).then(p_218924_1_.create((p_218930_0_, p_218930_1_, p_218930_2_, p_218930_3_) -> {
               int lvt_4_1_ = IntegerArgumentType.getInteger(p_218930_0_, "index");
               return func_218944_a(lvt_4_1_, p_218930_1_, p_218930_2_, p_218930_3_);
            })))).then(Commands.literal("prepend").then(p_218924_1_.create((p_218932_0_, p_218932_1_, p_218932_2_, p_218932_3_) -> {
               return func_218944_a(0, p_218932_1_, p_218932_2_, p_218932_3_);
            }))).then(Commands.literal("append").then(p_218924_1_.create((p_218941_0_, p_218941_1_, p_218941_2_, p_218941_3_) -> {
               return func_218944_a(-1, p_218941_1_, p_218941_2_, p_218941_3_);
            }))).then(Commands.literal("set").then(p_218924_1_.create((p_218954_0_, p_218954_1_, p_218954_2_, p_218954_3_) -> {
               INBT var10002 = (INBT)Iterables.getLast(p_218954_3_);
               var10002.getClass();
               return p_218954_2_.func_218076_b(p_218954_1_, var10002::copy);
            }))).then(Commands.literal("merge").then(p_218924_1_.create((p_218927_0_, p_218927_1_, p_218927_2_, p_218927_3_) -> {
               Collection<INBT> lvt_4_1_ = p_218927_2_.func_218073_a(p_218927_1_, CompoundNBT::new);
               int lvt_5_1_ = 0;

               CompoundNBT lvt_8_1_;
               CompoundNBT lvt_9_1_;
               for(Iterator var6 = lvt_4_1_.iterator(); var6.hasNext(); lvt_5_1_ += lvt_9_1_.equals(lvt_8_1_) ? 0 : 1) {
                  INBT lvt_7_1_ = (INBT)var6.next();
                  if (!(lvt_7_1_ instanceof CompoundNBT)) {
                     throw field_218959_i.create(lvt_7_1_);
                  }

                  lvt_8_1_ = (CompoundNBT)lvt_7_1_;
                  lvt_9_1_ = lvt_8_1_.copy();
                  Iterator var10 = p_218927_3_.iterator();

                  while(var10.hasNext()) {
                     INBT lvt_11_1_ = (INBT)var10.next();
                     if (!(lvt_11_1_ instanceof CompoundNBT)) {
                        throw field_218959_i.create(lvt_11_1_);
                     }

                     lvt_8_1_.merge((CompoundNBT)lvt_11_1_);
                  }
               }

               return lvt_5_1_;
            })));
         }));
      }

      p_198937_0_.register(lvt_1_1_);
   }

   private static int func_218944_a(int p_218944_0_, CompoundNBT p_218944_1_, NBTPathArgument.NBTPath p_218944_2_, List<INBT> p_218944_3_) throws CommandSyntaxException {
      Collection<INBT> lvt_4_1_ = p_218944_2_.func_218073_a(p_218944_1_, ListNBT::new);
      int lvt_5_1_ = 0;

      boolean lvt_8_1_;
      for(Iterator var6 = lvt_4_1_.iterator(); var6.hasNext(); lvt_5_1_ += lvt_8_1_ ? 1 : 0) {
         INBT lvt_7_1_ = (INBT)var6.next();
         if (!(lvt_7_1_ instanceof CollectionNBT)) {
            throw field_218958_h.create(lvt_7_1_);
         }

         lvt_8_1_ = false;
         CollectionNBT<?> lvt_9_1_ = (CollectionNBT)lvt_7_1_;
         int lvt_10_1_ = p_218944_0_ < 0 ? lvt_9_1_.size() + p_218944_0_ + 1 : p_218944_0_;
         Iterator var11 = p_218944_3_.iterator();

         while(var11.hasNext()) {
            INBT lvt_12_1_ = (INBT)var11.next();

            try {
               if (lvt_9_1_.func_218660_b(lvt_10_1_, lvt_12_1_.copy())) {
                  ++lvt_10_1_;
                  lvt_8_1_ = true;
               }
            } catch (IndexOutOfBoundsException var14) {
               throw field_218960_j.create(lvt_10_1_);
            }
         }
      }

      return lvt_5_1_;
   }

   private static ArgumentBuilder<CommandSource, ?> func_218935_a(BiConsumer<ArgumentBuilder<CommandSource, ?>, DataCommand.IModificationSourceArgumentBuilder> p_218935_0_) {
      LiteralArgumentBuilder<CommandSource> lvt_1_1_ = Commands.literal("modify");
      Iterator var2 = field_218955_b.iterator();

      while(var2.hasNext()) {
         DataCommand.IDataProvider lvt_3_1_ = (DataCommand.IDataProvider)var2.next();
         lvt_3_1_.createArgument(lvt_1_1_, (p_218940_2_) -> {
            ArgumentBuilder<CommandSource, ?> lvt_3_1_x = Commands.argument("targetPath", NBTPathArgument.nbtPath());
            Iterator var4 = field_218956_c.iterator();

            while(var4.hasNext()) {
               DataCommand.IDataProvider lvt_5_1_ = (DataCommand.IDataProvider)var4.next();
               p_218935_0_.accept(lvt_3_1_x, (p_218934_2_) -> {
                  return lvt_5_1_.createArgument(Commands.literal("from"), (p_218929_3_) -> {
                     return p_218929_3_.executes((p_218937_3_) -> {
                        List<INBT> lvt_4_1_ = Collections.singletonList(lvt_5_1_.createAccessor(p_218937_3_).getData());
                        return func_218933_a(p_218937_3_, lvt_3_1_, p_218934_2_, lvt_4_1_);
                     }).then(Commands.argument("sourcePath", NBTPathArgument.nbtPath()).executes((p_218936_3_) -> {
                        IDataAccessor lvt_4_1_ = lvt_5_1_.createAccessor(p_218936_3_);
                        NBTPathArgument.NBTPath lvt_5_1_x = NBTPathArgument.getNBTPath(p_218936_3_, "sourcePath");
                        List<INBT> lvt_6_1_ = lvt_5_1_x.func_218071_a(lvt_4_1_.getData());
                        return func_218933_a(p_218936_3_, lvt_3_1_, p_218934_2_, lvt_6_1_);
                     }));
                  });
               });
            }

            p_218935_0_.accept(lvt_3_1_x, (p_218949_1_) -> {
               return (LiteralArgumentBuilder)Commands.literal("value").then(Commands.argument("value", NBTTagArgument.func_218085_a()).executes((p_218952_2_) -> {
                  List<INBT> lvt_3_1_x = Collections.singletonList(NBTTagArgument.func_218086_a(p_218952_2_, "value"));
                  return func_218933_a(p_218952_2_, lvt_3_1_, p_218949_1_, lvt_3_1_x);
               }));
            });
            return p_218940_2_.then(lvt_3_1_x);
         });
      }

      return lvt_1_1_;
   }

   private static int func_218933_a(CommandContext<CommandSource> p_218933_0_, DataCommand.IDataProvider p_218933_1_, DataCommand.IModificationType p_218933_2_, List<INBT> p_218933_3_) throws CommandSyntaxException {
      IDataAccessor lvt_4_1_ = p_218933_1_.createAccessor(p_218933_0_);
      NBTPathArgument.NBTPath lvt_5_1_ = NBTPathArgument.getNBTPath(p_218933_0_, "targetPath");
      CompoundNBT lvt_6_1_ = lvt_4_1_.getData();
      int lvt_7_1_ = p_218933_2_.modify(p_218933_0_, lvt_6_1_, lvt_5_1_, p_218933_3_);
      if (lvt_7_1_ == 0) {
         throw NOTHING_CHANGED.create();
      } else {
         lvt_4_1_.mergeData(lvt_6_1_);
         ((CommandSource)p_218933_0_.getSource()).sendFeedback(lvt_4_1_.getModifiedMessage(), true);
         return lvt_7_1_;
      }
   }

   private static int remove(CommandSource p_198942_0_, IDataAccessor p_198942_1_, NBTPathArgument.NBTPath p_198942_2_) throws CommandSyntaxException {
      CompoundNBT lvt_3_1_ = p_198942_1_.getData();
      int lvt_4_1_ = p_198942_2_.func_218068_c(lvt_3_1_);
      if (lvt_4_1_ == 0) {
         throw NOTHING_CHANGED.create();
      } else {
         p_198942_1_.mergeData(lvt_3_1_);
         p_198942_0_.sendFeedback(p_198942_1_.getModifiedMessage(), true);
         return lvt_4_1_;
      }
   }

   private static INBT func_218928_a(NBTPathArgument.NBTPath p_218928_0_, IDataAccessor p_218928_1_) throws CommandSyntaxException {
      Collection<INBT> lvt_2_1_ = p_218928_0_.func_218071_a(p_218928_1_.getData());
      Iterator<INBT> lvt_3_1_ = lvt_2_1_.iterator();
      INBT lvt_4_1_ = (INBT)lvt_3_1_.next();
      if (lvt_3_1_.hasNext()) {
         throw field_218957_g.create();
      } else {
         return lvt_4_1_;
      }
   }

   private static int get(CommandSource p_201228_0_, IDataAccessor p_201228_1_, NBTPathArgument.NBTPath p_201228_2_) throws CommandSyntaxException {
      INBT lvt_3_1_ = func_218928_a(p_201228_2_, p_201228_1_);
      int lvt_4_5_;
      if (lvt_3_1_ instanceof NumberNBT) {
         lvt_4_5_ = MathHelper.floor(((NumberNBT)lvt_3_1_).getDouble());
      } else if (lvt_3_1_ instanceof CollectionNBT) {
         lvt_4_5_ = ((CollectionNBT)lvt_3_1_).size();
      } else if (lvt_3_1_ instanceof CompoundNBT) {
         lvt_4_5_ = ((CompoundNBT)lvt_3_1_).size();
      } else {
         if (!(lvt_3_1_ instanceof StringNBT)) {
            throw GET_UNKNOWN_EXCEPTION.create(p_201228_2_.toString());
         }

         lvt_4_5_ = lvt_3_1_.getString().length();
      }

      p_201228_0_.sendFeedback(p_201228_1_.getQueryMessage(lvt_3_1_), false);
      return lvt_4_5_;
   }

   private static int getScaled(CommandSource p_198938_0_, IDataAccessor p_198938_1_, NBTPathArgument.NBTPath p_198938_2_, double p_198938_3_) throws CommandSyntaxException {
      INBT lvt_5_1_ = func_218928_a(p_198938_2_, p_198938_1_);
      if (!(lvt_5_1_ instanceof NumberNBT)) {
         throw GET_INVALID_EXCEPTION.create(p_198938_2_.toString());
      } else {
         int lvt_6_1_ = MathHelper.floor(((NumberNBT)lvt_5_1_).getDouble() * p_198938_3_);
         p_198938_0_.sendFeedback(p_198938_1_.getGetMessage(p_198938_2_, p_198938_3_, lvt_6_1_), false);
         return lvt_6_1_;
      }
   }

   private static int get(CommandSource p_198947_0_, IDataAccessor p_198947_1_) throws CommandSyntaxException {
      p_198947_0_.sendFeedback(p_198947_1_.getQueryMessage(p_198947_1_.getData()), false);
      return 1;
   }

   private static int merge(CommandSource p_198946_0_, IDataAccessor p_198946_1_, CompoundNBT p_198946_2_) throws CommandSyntaxException {
      CompoundNBT lvt_3_1_ = p_198946_1_.getData();
      CompoundNBT lvt_4_1_ = lvt_3_1_.copy().merge(p_198946_2_);
      if (lvt_3_1_.equals(lvt_4_1_)) {
         throw NOTHING_CHANGED.create();
      } else {
         p_198946_1_.mergeData(lvt_4_1_);
         p_198946_0_.sendFeedback(p_198946_1_.getModifiedMessage(), true);
         return 1;
      }
   }

   static {
      DATA_PROVIDERS = ImmutableList.of(EntityDataAccessor.DATA_PROVIDER, BlockDataAccessor.DATA_PROVIDER, StorageAccessor.field_229833_a_);
      field_218955_b = (List)DATA_PROVIDERS.stream().map((p_218925_0_) -> {
         return (DataCommand.IDataProvider)p_218925_0_.apply("target");
      }).collect(ImmutableList.toImmutableList());
      field_218956_c = (List)DATA_PROVIDERS.stream().map((p_218947_0_) -> {
         return (DataCommand.IDataProvider)p_218947_0_.apply("source");
      }).collect(ImmutableList.toImmutableList());
   }

   public interface IDataProvider {
      IDataAccessor createAccessor(CommandContext<CommandSource> var1) throws CommandSyntaxException;

      ArgumentBuilder<CommandSource, ?> createArgument(ArgumentBuilder<CommandSource, ?> var1, Function<ArgumentBuilder<CommandSource, ?>, ArgumentBuilder<CommandSource, ?>> var2);
   }

   interface IModificationSourceArgumentBuilder {
      ArgumentBuilder<CommandSource, ?> create(DataCommand.IModificationType var1);
   }

   interface IModificationType {
      int modify(CommandContext<CommandSource> var1, CompoundNBT var2, NBTPathArgument.NBTPath var3, List<INBT> var4) throws CommandSyntaxException;
   }
}
