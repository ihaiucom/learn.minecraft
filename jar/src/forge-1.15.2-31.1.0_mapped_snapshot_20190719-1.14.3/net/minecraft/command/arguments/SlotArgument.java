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
import net.minecraft.command.CommandSource;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.util.Util;
import net.minecraft.util.text.TranslationTextComponent;

public class SlotArgument implements ArgumentType<Integer> {
   private static final Collection<String> EXAMPLES = Arrays.asList("container.5", "12", "weapon");
   private static final DynamicCommandExceptionType SLOT_UNKNOWN = new DynamicCommandExceptionType((p_208679_0_) -> {
      return new TranslationTextComponent("slot.unknown", new Object[]{p_208679_0_});
   });
   private static final Map<String, Integer> KNOWN_SLOTS = (Map)Util.make(Maps.newHashMap(), (p_209386_0_) -> {
      int lvt_1_6_;
      for(lvt_1_6_ = 0; lvt_1_6_ < 54; ++lvt_1_6_) {
         p_209386_0_.put("container." + lvt_1_6_, lvt_1_6_);
      }

      for(lvt_1_6_ = 0; lvt_1_6_ < 9; ++lvt_1_6_) {
         p_209386_0_.put("hotbar." + lvt_1_6_, lvt_1_6_);
      }

      for(lvt_1_6_ = 0; lvt_1_6_ < 27; ++lvt_1_6_) {
         p_209386_0_.put("inventory." + lvt_1_6_, 9 + lvt_1_6_);
      }

      for(lvt_1_6_ = 0; lvt_1_6_ < 27; ++lvt_1_6_) {
         p_209386_0_.put("enderchest." + lvt_1_6_, 200 + lvt_1_6_);
      }

      for(lvt_1_6_ = 0; lvt_1_6_ < 8; ++lvt_1_6_) {
         p_209386_0_.put("villager." + lvt_1_6_, 300 + lvt_1_6_);
      }

      for(lvt_1_6_ = 0; lvt_1_6_ < 15; ++lvt_1_6_) {
         p_209386_0_.put("horse." + lvt_1_6_, 500 + lvt_1_6_);
      }

      p_209386_0_.put("weapon", 98);
      p_209386_0_.put("weapon.mainhand", 98);
      p_209386_0_.put("weapon.offhand", 99);
      p_209386_0_.put("armor.head", 100 + EquipmentSlotType.HEAD.getIndex());
      p_209386_0_.put("armor.chest", 100 + EquipmentSlotType.CHEST.getIndex());
      p_209386_0_.put("armor.legs", 100 + EquipmentSlotType.LEGS.getIndex());
      p_209386_0_.put("armor.feet", 100 + EquipmentSlotType.FEET.getIndex());
      p_209386_0_.put("horse.saddle", 400);
      p_209386_0_.put("horse.armor", 401);
      p_209386_0_.put("horse.chest", 499);
   });

   public static SlotArgument slot() {
      return new SlotArgument();
   }

   public static int getSlot(CommandContext<CommandSource> p_197221_0_, String p_197221_1_) {
      return (Integer)p_197221_0_.getArgument(p_197221_1_, Integer.class);
   }

   public Integer parse(StringReader p_parse_1_) throws CommandSyntaxException {
      String lvt_2_1_ = p_parse_1_.readUnquotedString();
      if (!KNOWN_SLOTS.containsKey(lvt_2_1_)) {
         throw SLOT_UNKNOWN.create(lvt_2_1_);
      } else {
         return (Integer)KNOWN_SLOTS.get(lvt_2_1_);
      }
   }

   public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> p_listSuggestions_1_, SuggestionsBuilder p_listSuggestions_2_) {
      return ISuggestionProvider.suggest((Iterable)KNOWN_SLOTS.keySet(), p_listSuggestions_2_);
   }

   public Collection<String> getExamples() {
      return EXAMPLES;
   }

   // $FF: synthetic method
   public Object parse(StringReader p_parse_1_) throws CommandSyntaxException {
      return this.parse(p_parse_1_);
   }
}
