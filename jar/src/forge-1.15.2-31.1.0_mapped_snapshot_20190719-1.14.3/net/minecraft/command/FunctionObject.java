package net.minecraft.command;

import com.google.common.collect.Lists;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.ArrayDeque;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.advancements.FunctionManager;
import net.minecraft.util.ResourceLocation;

public class FunctionObject {
   private final FunctionObject.IEntry[] entries;
   private final ResourceLocation id;

   public FunctionObject(ResourceLocation p_i47973_1_, FunctionObject.IEntry[] p_i47973_2_) {
      this.id = p_i47973_1_;
      this.entries = p_i47973_2_;
   }

   public ResourceLocation getId() {
      return this.id;
   }

   public FunctionObject.IEntry[] getEntries() {
      return this.entries;
   }

   public static FunctionObject create(ResourceLocation p_197000_0_, FunctionManager p_197000_1_, List<String> p_197000_2_) {
      List<FunctionObject.IEntry> lvt_3_1_ = Lists.newArrayListWithCapacity(p_197000_2_.size());

      for(int lvt_4_1_ = 0; lvt_4_1_ < p_197000_2_.size(); ++lvt_4_1_) {
         int lvt_5_1_ = lvt_4_1_ + 1;
         String lvt_6_1_ = ((String)p_197000_2_.get(lvt_4_1_)).trim();
         StringReader lvt_7_1_ = new StringReader(lvt_6_1_);
         if (lvt_7_1_.canRead() && lvt_7_1_.peek() != '#') {
            if (lvt_7_1_.peek() == '/') {
               lvt_7_1_.skip();
               if (lvt_7_1_.peek() == '/') {
                  throw new IllegalArgumentException("Unknown or invalid command '" + lvt_6_1_ + "' on line " + lvt_5_1_ + " (if you intended to make a comment, use '#' not '//')");
               }

               String lvt_8_1_ = lvt_7_1_.readUnquotedString();
               throw new IllegalArgumentException("Unknown or invalid command '" + lvt_6_1_ + "' on line " + lvt_5_1_ + " (did you mean '" + lvt_8_1_ + "'? Do not use a preceding forwards slash.)");
            }

            try {
               ParseResults<CommandSource> lvt_8_2_ = p_197000_1_.getServer().getCommandManager().getDispatcher().parse(lvt_7_1_, p_197000_1_.func_223402_g());
               if (lvt_8_2_.getReader().canRead()) {
                  throw Commands.func_227481_a_(lvt_8_2_);
               }

               lvt_3_1_.add(new FunctionObject.CommandEntry(lvt_8_2_));
            } catch (CommandSyntaxException var9) {
               throw new IllegalArgumentException("Whilst parsing command on line " + lvt_5_1_ + ": " + var9.getMessage());
            }
         }
      }

      return new FunctionObject(p_197000_0_, (FunctionObject.IEntry[])lvt_3_1_.toArray(new FunctionObject.IEntry[0]));
   }

   public static class CacheableFunction {
      public static final FunctionObject.CacheableFunction EMPTY = new FunctionObject.CacheableFunction((ResourceLocation)null);
      @Nullable
      private final ResourceLocation id;
      private boolean isValid;
      private Optional<FunctionObject> function = Optional.empty();

      public CacheableFunction(@Nullable ResourceLocation p_i47537_1_) {
         this.id = p_i47537_1_;
      }

      public CacheableFunction(FunctionObject p_i47602_1_) {
         this.isValid = true;
         this.id = null;
         this.function = Optional.of(p_i47602_1_);
      }

      public Optional<FunctionObject> func_218039_a(FunctionManager p_218039_1_) {
         if (!this.isValid) {
            if (this.id != null) {
               this.function = p_218039_1_.get(this.id);
            }

            this.isValid = true;
         }

         return this.function;
      }

      @Nullable
      public ResourceLocation getId() {
         return (ResourceLocation)this.function.map((p_218040_0_) -> {
            return p_218040_0_.id;
         }).orElse(this.id);
      }
   }

   public static class FunctionEntry implements FunctionObject.IEntry {
      private final FunctionObject.CacheableFunction function;

      public FunctionEntry(FunctionObject p_i47601_1_) {
         this.function = new FunctionObject.CacheableFunction(p_i47601_1_);
      }

      public void execute(FunctionManager p_196998_1_, CommandSource p_196998_2_, ArrayDeque<FunctionManager.QueuedCommand> p_196998_3_, int p_196998_4_) {
         this.function.func_218039_a(p_196998_1_).ifPresent((p_218041_4_) -> {
            FunctionObject.IEntry[] lvt_5_1_ = p_218041_4_.getEntries();
            int lvt_6_1_ = p_196998_4_ - p_196998_3_.size();
            int lvt_7_1_ = Math.min(lvt_5_1_.length, lvt_6_1_);

            for(int lvt_8_1_ = lvt_7_1_ - 1; lvt_8_1_ >= 0; --lvt_8_1_) {
               p_196998_3_.addFirst(new FunctionManager.QueuedCommand(p_196998_1_, p_196998_2_, lvt_5_1_[lvt_8_1_]));
            }

         });
      }

      public String toString() {
         return "function " + this.function.getId();
      }
   }

   public static class CommandEntry implements FunctionObject.IEntry {
      private final ParseResults<CommandSource> field_196999_a;

      public CommandEntry(ParseResults<CommandSource> p_i47816_1_) {
         this.field_196999_a = p_i47816_1_;
      }

      public void execute(FunctionManager p_196998_1_, CommandSource p_196998_2_, ArrayDeque<FunctionManager.QueuedCommand> p_196998_3_, int p_196998_4_) throws CommandSyntaxException {
         p_196998_1_.getCommandDispatcher().execute(new ParseResults(this.field_196999_a.getContext().withSource(p_196998_2_), this.field_196999_a.getReader(), this.field_196999_a.getExceptions()));
      }

      public String toString() {
         return this.field_196999_a.getReader().getString();
      }
   }

   public interface IEntry {
      void execute(FunctionManager var1, CommandSource var2, ArrayDeque<FunctionManager.QueuedCommand> var3, int var4) throws CommandSyntaxException;
   }
}
