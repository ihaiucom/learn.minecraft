package net.minecraft.command.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.datafixers.util.Either;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import net.minecraft.command.CommandSource;
import net.minecraft.command.FunctionObject;
import net.minecraft.tags.Tag;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;

public class FunctionArgument implements ArgumentType<FunctionArgument.IResult> {
   private static final Collection<String> EXAMPLES = Arrays.asList("foo", "foo:bar", "#foo");
   private static final DynamicCommandExceptionType FUNCTION_UNKNOWN_TAG = new DynamicCommandExceptionType((p_208691_0_) -> {
      return new TranslationTextComponent("arguments.function.tag.unknown", new Object[]{p_208691_0_});
   });
   private static final DynamicCommandExceptionType FUNCTION_UNKNOWN = new DynamicCommandExceptionType((p_208694_0_) -> {
      return new TranslationTextComponent("arguments.function.unknown", new Object[]{p_208694_0_});
   });

   public static FunctionArgument func_200021_a() {
      return new FunctionArgument();
   }

   public FunctionArgument.IResult parse(StringReader p_parse_1_) throws CommandSyntaxException {
      final ResourceLocation lvt_2_2_;
      if (p_parse_1_.canRead() && p_parse_1_.peek() == '#') {
         p_parse_1_.skip();
         lvt_2_2_ = ResourceLocation.read(p_parse_1_);
         return new FunctionArgument.IResult() {
            public Collection<FunctionObject> create(CommandContext<CommandSource> p_223252_1_) throws CommandSyntaxException {
               Tag<FunctionObject> lvt_2_1_ = FunctionArgument.func_218111_d(p_223252_1_, lvt_2_2_);
               return lvt_2_1_.getAllElements();
            }

            public Either<FunctionObject, Tag<FunctionObject>> func_218102_b(CommandContext<CommandSource> p_218102_1_) throws CommandSyntaxException {
               return Either.right(FunctionArgument.func_218111_d(p_218102_1_, lvt_2_2_));
            }
         };
      } else {
         lvt_2_2_ = ResourceLocation.read(p_parse_1_);
         return new FunctionArgument.IResult() {
            public Collection<FunctionObject> create(CommandContext<CommandSource> p_223252_1_) throws CommandSyntaxException {
               return Collections.singleton(FunctionArgument.func_218108_c(p_223252_1_, lvt_2_2_));
            }

            public Either<FunctionObject, Tag<FunctionObject>> func_218102_b(CommandContext<CommandSource> p_218102_1_) throws CommandSyntaxException {
               return Either.left(FunctionArgument.func_218108_c(p_218102_1_, lvt_2_2_));
            }
         };
      }
   }

   private static FunctionObject func_218108_c(CommandContext<CommandSource> p_218108_0_, ResourceLocation p_218108_1_) throws CommandSyntaxException {
      return (FunctionObject)((CommandSource)p_218108_0_.getSource()).getServer().getFunctionManager().get(p_218108_1_).orElseThrow(() -> {
         return FUNCTION_UNKNOWN.create(p_218108_1_.toString());
      });
   }

   private static Tag<FunctionObject> func_218111_d(CommandContext<CommandSource> p_218111_0_, ResourceLocation p_218111_1_) throws CommandSyntaxException {
      Tag<FunctionObject> lvt_2_1_ = ((CommandSource)p_218111_0_.getSource()).getServer().getFunctionManager().getTagCollection().get(p_218111_1_);
      if (lvt_2_1_ == null) {
         throw FUNCTION_UNKNOWN_TAG.create(p_218111_1_.toString());
      } else {
         return lvt_2_1_;
      }
   }

   public static Collection<FunctionObject> getFunctions(CommandContext<CommandSource> p_200022_0_, String p_200022_1_) throws CommandSyntaxException {
      return ((FunctionArgument.IResult)p_200022_0_.getArgument(p_200022_1_, FunctionArgument.IResult.class)).create(p_200022_0_);
   }

   public static Either<FunctionObject, Tag<FunctionObject>> func_218110_b(CommandContext<CommandSource> p_218110_0_, String p_218110_1_) throws CommandSyntaxException {
      return ((FunctionArgument.IResult)p_218110_0_.getArgument(p_218110_1_, FunctionArgument.IResult.class)).func_218102_b(p_218110_0_);
   }

   public Collection<String> getExamples() {
      return EXAMPLES;
   }

   // $FF: synthetic method
   public Object parse(StringReader p_parse_1_) throws CommandSyntaxException {
      return this.parse(p_parse_1_);
   }

   public interface IResult {
      Collection<FunctionObject> create(CommandContext<CommandSource> var1) throws CommandSyntaxException;

      Either<FunctionObject, Tag<FunctionObject>> func_218102_b(CommandContext<CommandSource> var1) throws CommandSyntaxException;
   }
}
