package net.minecraft.command;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.minecraft.util.ResourceLocation;

public interface ISuggestionProvider {
   Collection<String> getPlayerNames();

   default Collection<String> getTargetedEntity() {
      return Collections.emptyList();
   }

   Collection<String> getTeamNames();

   Collection<ResourceLocation> getSoundResourceLocations();

   Stream<ResourceLocation> getRecipeResourceLocations();

   CompletableFuture<Suggestions> getSuggestionsFromServer(CommandContext<ISuggestionProvider> var1, SuggestionsBuilder var2);

   default Collection<ISuggestionProvider.Coordinates> func_217294_q() {
      return Collections.singleton(ISuggestionProvider.Coordinates.DEFAULT_GLOBAL);
   }

   default Collection<ISuggestionProvider.Coordinates> func_217293_r() {
      return Collections.singleton(ISuggestionProvider.Coordinates.DEFAULT_GLOBAL);
   }

   boolean hasPermissionLevel(int var1);

   static <T> void func_210512_a(Iterable<T> p_210512_0_, String p_210512_1_, Function<T, ResourceLocation> p_210512_2_, Consumer<T> p_210512_3_) {
      boolean lvt_4_1_ = p_210512_1_.indexOf(58) > -1;
      Iterator var5 = p_210512_0_.iterator();

      while(true) {
         while(var5.hasNext()) {
            T lvt_6_1_ = var5.next();
            ResourceLocation lvt_7_1_ = (ResourceLocation)p_210512_2_.apply(lvt_6_1_);
            if (lvt_4_1_) {
               String lvt_8_1_ = lvt_7_1_.toString();
               if (lvt_8_1_.startsWith(p_210512_1_)) {
                  p_210512_3_.accept(lvt_6_1_);
               }
            } else if (lvt_7_1_.getNamespace().startsWith(p_210512_1_) || lvt_7_1_.getNamespace().equals("minecraft") && lvt_7_1_.getPath().startsWith(p_210512_1_)) {
               p_210512_3_.accept(lvt_6_1_);
            }
         }

         return;
      }
   }

   static <T> void func_210511_a(Iterable<T> p_210511_0_, String p_210511_1_, String p_210511_2_, Function<T, ResourceLocation> p_210511_3_, Consumer<T> p_210511_4_) {
      if (p_210511_1_.isEmpty()) {
         p_210511_0_.forEach(p_210511_4_);
      } else {
         String lvt_5_1_ = Strings.commonPrefix(p_210511_1_, p_210511_2_);
         if (!lvt_5_1_.isEmpty()) {
            String lvt_6_1_ = p_210511_1_.substring(lvt_5_1_.length());
            func_210512_a(p_210511_0_, lvt_6_1_, p_210511_3_, p_210511_4_);
         }
      }

   }

   static CompletableFuture<Suggestions> suggestIterable(Iterable<ResourceLocation> p_197006_0_, SuggestionsBuilder p_197006_1_, String p_197006_2_) {
      String lvt_3_1_ = p_197006_1_.getRemaining().toLowerCase(Locale.ROOT);
      func_210511_a(p_197006_0_, lvt_3_1_, p_197006_2_, (p_210519_0_) -> {
         return p_210519_0_;
      }, (p_210518_2_) -> {
         p_197006_1_.suggest(p_197006_2_ + p_210518_2_);
      });
      return p_197006_1_.buildFuture();
   }

   static CompletableFuture<Suggestions> suggestIterable(Iterable<ResourceLocation> p_197014_0_, SuggestionsBuilder p_197014_1_) {
      String lvt_2_1_ = p_197014_1_.getRemaining().toLowerCase(Locale.ROOT);
      func_210512_a(p_197014_0_, lvt_2_1_, (p_210517_0_) -> {
         return p_210517_0_;
      }, (p_210513_1_) -> {
         p_197014_1_.suggest(p_210513_1_.toString());
      });
      return p_197014_1_.buildFuture();
   }

   static <T> CompletableFuture<Suggestions> func_210514_a(Iterable<T> p_210514_0_, SuggestionsBuilder p_210514_1_, Function<T, ResourceLocation> p_210514_2_, Function<T, Message> p_210514_3_) {
      String lvt_4_1_ = p_210514_1_.getRemaining().toLowerCase(Locale.ROOT);
      func_210512_a(p_210514_0_, lvt_4_1_, p_210514_2_, (p_210515_3_) -> {
         p_210514_1_.suggest(((ResourceLocation)p_210514_2_.apply(p_210515_3_)).toString(), (Message)p_210514_3_.apply(p_210515_3_));
      });
      return p_210514_1_.buildFuture();
   }

   static CompletableFuture<Suggestions> func_212476_a(Stream<ResourceLocation> p_212476_0_, SuggestionsBuilder p_212476_1_) {
      return suggestIterable(p_212476_0_::iterator, p_212476_1_);
   }

   static <T> CompletableFuture<Suggestions> func_201725_a(Stream<T> p_201725_0_, SuggestionsBuilder p_201725_1_, Function<T, ResourceLocation> p_201725_2_, Function<T, Message> p_201725_3_) {
      return func_210514_a(p_201725_0_::iterator, p_201725_1_, p_201725_2_, p_201725_3_);
   }

   static CompletableFuture<Suggestions> func_209000_a(String p_209000_0_, Collection<ISuggestionProvider.Coordinates> p_209000_1_, SuggestionsBuilder p_209000_2_, Predicate<String> p_209000_3_) {
      List<String> lvt_4_1_ = Lists.newArrayList();
      if (Strings.isNullOrEmpty(p_209000_0_)) {
         Iterator var5 = p_209000_1_.iterator();

         while(var5.hasNext()) {
            ISuggestionProvider.Coordinates lvt_6_1_ = (ISuggestionProvider.Coordinates)var5.next();
            String lvt_7_1_ = lvt_6_1_.x + " " + lvt_6_1_.y + " " + lvt_6_1_.z;
            if (p_209000_3_.test(lvt_7_1_)) {
               lvt_4_1_.add(lvt_6_1_.x);
               lvt_4_1_.add(lvt_6_1_.x + " " + lvt_6_1_.y);
               lvt_4_1_.add(lvt_7_1_);
            }
         }
      } else {
         String[] lvt_5_1_ = p_209000_0_.split(" ");
         String lvt_8_2_;
         Iterator var10;
         ISuggestionProvider.Coordinates lvt_7_2_;
         if (lvt_5_1_.length == 1) {
            var10 = p_209000_1_.iterator();

            while(var10.hasNext()) {
               lvt_7_2_ = (ISuggestionProvider.Coordinates)var10.next();
               lvt_8_2_ = lvt_5_1_[0] + " " + lvt_7_2_.y + " " + lvt_7_2_.z;
               if (p_209000_3_.test(lvt_8_2_)) {
                  lvt_4_1_.add(lvt_5_1_[0] + " " + lvt_7_2_.y);
                  lvt_4_1_.add(lvt_8_2_);
               }
            }
         } else if (lvt_5_1_.length == 2) {
            var10 = p_209000_1_.iterator();

            while(var10.hasNext()) {
               lvt_7_2_ = (ISuggestionProvider.Coordinates)var10.next();
               lvt_8_2_ = lvt_5_1_[0] + " " + lvt_5_1_[1] + " " + lvt_7_2_.z;
               if (p_209000_3_.test(lvt_8_2_)) {
                  lvt_4_1_.add(lvt_8_2_);
               }
            }
         }
      }

      return suggest((Iterable)lvt_4_1_, p_209000_2_);
   }

   static CompletableFuture<Suggestions> func_211269_a(String p_211269_0_, Collection<ISuggestionProvider.Coordinates> p_211269_1_, SuggestionsBuilder p_211269_2_, Predicate<String> p_211269_3_) {
      List<String> lvt_4_1_ = Lists.newArrayList();
      if (Strings.isNullOrEmpty(p_211269_0_)) {
         Iterator var5 = p_211269_1_.iterator();

         while(var5.hasNext()) {
            ISuggestionProvider.Coordinates lvt_6_1_ = (ISuggestionProvider.Coordinates)var5.next();
            String lvt_7_1_ = lvt_6_1_.x + " " + lvt_6_1_.z;
            if (p_211269_3_.test(lvt_7_1_)) {
               lvt_4_1_.add(lvt_6_1_.x);
               lvt_4_1_.add(lvt_7_1_);
            }
         }
      } else {
         String[] lvt_5_1_ = p_211269_0_.split(" ");
         if (lvt_5_1_.length == 1) {
            Iterator var10 = p_211269_1_.iterator();

            while(var10.hasNext()) {
               ISuggestionProvider.Coordinates lvt_7_2_ = (ISuggestionProvider.Coordinates)var10.next();
               String lvt_8_1_ = lvt_5_1_[0] + " " + lvt_7_2_.z;
               if (p_211269_3_.test(lvt_8_1_)) {
                  lvt_4_1_.add(lvt_8_1_);
               }
            }
         }
      }

      return suggest((Iterable)lvt_4_1_, p_211269_2_);
   }

   static CompletableFuture<Suggestions> suggest(Iterable<String> p_197005_0_, SuggestionsBuilder p_197005_1_) {
      String lvt_2_1_ = p_197005_1_.getRemaining().toLowerCase(Locale.ROOT);
      Iterator var3 = p_197005_0_.iterator();

      while(var3.hasNext()) {
         String lvt_4_1_ = (String)var3.next();
         if (lvt_4_1_.toLowerCase(Locale.ROOT).startsWith(lvt_2_1_)) {
            p_197005_1_.suggest(lvt_4_1_);
         }
      }

      return p_197005_1_.buildFuture();
   }

   static CompletableFuture<Suggestions> suggest(Stream<String> p_197013_0_, SuggestionsBuilder p_197013_1_) {
      String lvt_2_1_ = p_197013_1_.getRemaining().toLowerCase(Locale.ROOT);
      p_197013_0_.filter((p_197007_1_) -> {
         return p_197007_1_.toLowerCase(Locale.ROOT).startsWith(lvt_2_1_);
      }).forEach(p_197013_1_::suggest);
      return p_197013_1_.buildFuture();
   }

   static CompletableFuture<Suggestions> suggest(String[] p_197008_0_, SuggestionsBuilder p_197008_1_) {
      String lvt_2_1_ = p_197008_1_.getRemaining().toLowerCase(Locale.ROOT);
      String[] var3 = p_197008_0_;
      int var4 = p_197008_0_.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         String lvt_6_1_ = var3[var5];
         if (lvt_6_1_.toLowerCase(Locale.ROOT).startsWith(lvt_2_1_)) {
            p_197008_1_.suggest(lvt_6_1_);
         }
      }

      return p_197008_1_.buildFuture();
   }

   public static class Coordinates {
      public static final ISuggestionProvider.Coordinates DEFAULT_LOCAL = new ISuggestionProvider.Coordinates("^", "^", "^");
      public static final ISuggestionProvider.Coordinates DEFAULT_GLOBAL = new ISuggestionProvider.Coordinates("~", "~", "~");
      public final String x;
      public final String y;
      public final String z;

      public Coordinates(String p_i49368_1_, String p_i49368_2_, String p_i49368_3_) {
         this.x = p_i49368_1_;
         this.y = p_i49368_2_;
         this.z = p_i49368_3_;
      }
   }
}
