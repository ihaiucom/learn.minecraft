package net.minecraft.command.arguments;

import com.google.common.collect.Maps;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;
import javax.annotation.Nullable;
import net.minecraft.command.CommandSource;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.entity.Entity;
import net.minecraft.util.Util;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TranslationTextComponent;

public class EntityAnchorArgument implements ArgumentType<EntityAnchorArgument.Type> {
   private static final Collection<String> EXMAPLES = Arrays.asList("eyes", "feet");
   private static final DynamicCommandExceptionType ANCHOR_INVALID = new DynamicCommandExceptionType((p_208661_0_) -> {
      return new TranslationTextComponent("argument.anchor.invalid", new Object[]{p_208661_0_});
   });

   public static EntityAnchorArgument.Type getEntityAnchor(CommandContext<CommandSource> p_201023_0_, String p_201023_1_) {
      return (EntityAnchorArgument.Type)p_201023_0_.getArgument(p_201023_1_, EntityAnchorArgument.Type.class);
   }

   public static EntityAnchorArgument entityAnchor() {
      return new EntityAnchorArgument();
   }

   public EntityAnchorArgument.Type parse(StringReader p_parse_1_) throws CommandSyntaxException {
      int lvt_2_1_ = p_parse_1_.getCursor();
      String lvt_3_1_ = p_parse_1_.readUnquotedString();
      EntityAnchorArgument.Type lvt_4_1_ = EntityAnchorArgument.Type.getByName(lvt_3_1_);
      if (lvt_4_1_ == null) {
         p_parse_1_.setCursor(lvt_2_1_);
         throw ANCHOR_INVALID.createWithContext(p_parse_1_, lvt_3_1_);
      } else {
         return lvt_4_1_;
      }
   }

   public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> p_listSuggestions_1_, SuggestionsBuilder p_listSuggestions_2_) {
      return ISuggestionProvider.suggest((Iterable)EntityAnchorArgument.Type.BY_NAME.keySet(), p_listSuggestions_2_);
   }

   public Collection<String> getExamples() {
      return EXMAPLES;
   }

   // $FF: synthetic method
   public Object parse(StringReader p_parse_1_) throws CommandSyntaxException {
      return this.parse(p_parse_1_);
   }

   public static enum Type {
      FEET("feet", (p_201019_0_, p_201019_1_) -> {
         return p_201019_0_;
      }),
      EYES("eyes", (p_201018_0_, p_201018_1_) -> {
         return new Vec3d(p_201018_0_.x, p_201018_0_.y + (double)p_201018_1_.getEyeHeight(), p_201018_0_.z);
      });

      private static final Map<String, EntityAnchorArgument.Type> BY_NAME = (Map)Util.make(Maps.newHashMap(), (p_209384_0_) -> {
         EntityAnchorArgument.Type[] var1 = values();
         int var2 = var1.length;

         for(int var3 = 0; var3 < var2; ++var3) {
            EntityAnchorArgument.Type lvt_4_1_ = var1[var3];
            p_209384_0_.put(lvt_4_1_.name, lvt_4_1_);
         }

      });
      private final String name;
      private final BiFunction<Vec3d, Entity, Vec3d> offsetFunc;

      private Type(String p_i48597_3_, BiFunction<Vec3d, Entity, Vec3d> p_i48597_4_) {
         this.name = p_i48597_3_;
         this.offsetFunc = p_i48597_4_;
      }

      @Nullable
      public static EntityAnchorArgument.Type getByName(String p_201016_0_) {
         return (EntityAnchorArgument.Type)BY_NAME.get(p_201016_0_);
      }

      public Vec3d apply(Entity p_201017_1_) {
         return (Vec3d)this.offsetFunc.apply(p_201017_1_.getPositionVec(), p_201017_1_);
      }

      public Vec3d apply(CommandSource p_201015_1_) {
         Entity lvt_2_1_ = p_201015_1_.getEntity();
         return lvt_2_1_ == null ? p_201015_1_.getPos() : (Vec3d)this.offsetFunc.apply(p_201015_1_.getPos(), lvt_2_1_);
      }
   }
}
