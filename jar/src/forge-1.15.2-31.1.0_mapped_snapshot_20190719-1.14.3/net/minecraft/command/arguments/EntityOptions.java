package net.minecraft.command.arguments;

import com.google.common.collect.Maps;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementManager;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.advancements.CriterionProgress;
import net.minecraft.advancements.PlayerAdvancements;
import net.minecraft.advancements.criterion.MinMaxBounds;
import net.minecraft.advancements.criterion.MinMaxBoundsWrapped;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.Team;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.GameType;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootParameterSets;
import net.minecraft.world.storage.loot.LootParameters;
import net.minecraft.world.storage.loot.conditions.ILootCondition;

public class EntityOptions {
   private static final Map<String, EntityOptions.OptionHandler> REGISTRY = Maps.newHashMap();
   public static final DynamicCommandExceptionType UNKNOWN_ENTITY_OPTION = new DynamicCommandExceptionType((p_208752_0_) -> {
      return new TranslationTextComponent("argument.entity.options.unknown", new Object[]{p_208752_0_});
   });
   public static final DynamicCommandExceptionType INAPPLICABLE_ENTITY_OPTION = new DynamicCommandExceptionType((p_208726_0_) -> {
      return new TranslationTextComponent("argument.entity.options.inapplicable", new Object[]{p_208726_0_});
   });
   public static final SimpleCommandExceptionType NEGATIVE_DISTANCE = new SimpleCommandExceptionType(new TranslationTextComponent("argument.entity.options.distance.negative", new Object[0]));
   public static final SimpleCommandExceptionType NEGATIVE_LEVEL = new SimpleCommandExceptionType(new TranslationTextComponent("argument.entity.options.level.negative", new Object[0]));
   public static final SimpleCommandExceptionType NONPOSITIVE_LIMIT = new SimpleCommandExceptionType(new TranslationTextComponent("argument.entity.options.limit.toosmall", new Object[0]));
   public static final DynamicCommandExceptionType INVALID_SORT = new DynamicCommandExceptionType((p_208749_0_) -> {
      return new TranslationTextComponent("argument.entity.options.sort.irreversible", new Object[]{p_208749_0_});
   });
   public static final DynamicCommandExceptionType INVALID_GAME_MODE = new DynamicCommandExceptionType((p_208740_0_) -> {
      return new TranslationTextComponent("argument.entity.options.mode.invalid", new Object[]{p_208740_0_});
   });
   public static final DynamicCommandExceptionType INVALID_ENTITY_TYPE = new DynamicCommandExceptionType((p_208758_0_) -> {
      return new TranslationTextComponent("argument.entity.options.type.invalid", new Object[]{p_208758_0_});
   });

   public static void register(String p_202024_0_, EntityOptions.IFilter p_202024_1_, Predicate<EntitySelectorParser> p_202024_2_, ITextComponent p_202024_3_) {
      REGISTRY.put(p_202024_0_, new EntityOptions.OptionHandler(p_202024_1_, p_202024_2_, p_202024_3_));
   }

   public static void registerOptions() {
      if (REGISTRY.isEmpty()) {
         register("name", (p_197440_0_) -> {
            int lvt_1_1_ = p_197440_0_.getReader().getCursor();
            boolean lvt_2_1_ = p_197440_0_.shouldInvertValue();
            String lvt_3_1_ = p_197440_0_.getReader().readString();
            if (p_197440_0_.hasNameNotEquals() && !lvt_2_1_) {
               p_197440_0_.getReader().setCursor(lvt_1_1_);
               throw INAPPLICABLE_ENTITY_OPTION.createWithContext(p_197440_0_.getReader(), "name");
            } else {
               if (lvt_2_1_) {
                  p_197440_0_.setHasNameNotEquals(true);
               } else {
                  p_197440_0_.setHasNameEquals(true);
               }

               p_197440_0_.addFilter((p_197446_2_) -> {
                  return p_197446_2_.getName().getUnformattedComponentText().equals(lvt_3_1_) != lvt_2_1_;
               });
            }
         }, (p_202016_0_) -> {
            return !p_202016_0_.hasNameEquals();
         }, new TranslationTextComponent("argument.entity.options.name.description", new Object[0]));
         register("distance", (p_197439_0_) -> {
            int lvt_1_1_ = p_197439_0_.getReader().getCursor();
            MinMaxBounds.FloatBound lvt_2_1_ = MinMaxBounds.FloatBound.fromReader(p_197439_0_.getReader());
            if ((lvt_2_1_.getMin() == null || (Float)lvt_2_1_.getMin() >= 0.0F) && (lvt_2_1_.getMax() == null || (Float)lvt_2_1_.getMax() >= 0.0F)) {
               p_197439_0_.setDistance(lvt_2_1_);
               p_197439_0_.setCurrentWorldOnly();
            } else {
               p_197439_0_.getReader().setCursor(lvt_1_1_);
               throw NEGATIVE_DISTANCE.createWithContext(p_197439_0_.getReader());
            }
         }, (p_202020_0_) -> {
            return p_202020_0_.getDistance().isUnbounded();
         }, new TranslationTextComponent("argument.entity.options.distance.description", new Object[0]));
         register("level", (p_197438_0_) -> {
            int lvt_1_1_ = p_197438_0_.getReader().getCursor();
            MinMaxBounds.IntBound lvt_2_1_ = MinMaxBounds.IntBound.fromReader(p_197438_0_.getReader());
            if ((lvt_2_1_.getMin() == null || (Integer)lvt_2_1_.getMin() >= 0) && (lvt_2_1_.getMax() == null || (Integer)lvt_2_1_.getMax() >= 0)) {
               p_197438_0_.setLevel(lvt_2_1_);
               p_197438_0_.setIncludeNonPlayers(false);
            } else {
               p_197438_0_.getReader().setCursor(lvt_1_1_);
               throw NEGATIVE_LEVEL.createWithContext(p_197438_0_.getReader());
            }
         }, (p_202019_0_) -> {
            return p_202019_0_.getLevel().isUnbounded();
         }, new TranslationTextComponent("argument.entity.options.level.description", new Object[0]));
         register("x", (p_197437_0_) -> {
            p_197437_0_.setCurrentWorldOnly();
            p_197437_0_.setX(p_197437_0_.getReader().readDouble());
         }, (p_202022_0_) -> {
            return p_202022_0_.getX() == null;
         }, new TranslationTextComponent("argument.entity.options.x.description", new Object[0]));
         register("y", (p_197442_0_) -> {
            p_197442_0_.setCurrentWorldOnly();
            p_197442_0_.setY(p_197442_0_.getReader().readDouble());
         }, (p_202021_0_) -> {
            return p_202021_0_.getY() == null;
         }, new TranslationTextComponent("argument.entity.options.y.description", new Object[0]));
         register("z", (p_197464_0_) -> {
            p_197464_0_.setCurrentWorldOnly();
            p_197464_0_.setZ(p_197464_0_.getReader().readDouble());
         }, (p_202029_0_) -> {
            return p_202029_0_.getZ() == null;
         }, new TranslationTextComponent("argument.entity.options.z.description", new Object[0]));
         register("dx", (p_197460_0_) -> {
            p_197460_0_.setCurrentWorldOnly();
            p_197460_0_.setDx(p_197460_0_.getReader().readDouble());
         }, (p_202027_0_) -> {
            return p_202027_0_.getDx() == null;
         }, new TranslationTextComponent("argument.entity.options.dx.description", new Object[0]));
         register("dy", (p_197463_0_) -> {
            p_197463_0_.setCurrentWorldOnly();
            p_197463_0_.setDy(p_197463_0_.getReader().readDouble());
         }, (p_202026_0_) -> {
            return p_202026_0_.getDy() == null;
         }, new TranslationTextComponent("argument.entity.options.dy.description", new Object[0]));
         register("dz", (p_197458_0_) -> {
            p_197458_0_.setCurrentWorldOnly();
            p_197458_0_.setDz(p_197458_0_.getReader().readDouble());
         }, (p_202030_0_) -> {
            return p_202030_0_.getDz() == null;
         }, new TranslationTextComponent("argument.entity.options.dz.description", new Object[0]));
         register("x_rotation", (p_197462_0_) -> {
            p_197462_0_.setXRotation(MinMaxBoundsWrapped.func_207921_a(p_197462_0_.getReader(), true, MathHelper::wrapDegrees));
         }, (p_202028_0_) -> {
            return p_202028_0_.getXRotation() == MinMaxBoundsWrapped.UNBOUNDED;
         }, new TranslationTextComponent("argument.entity.options.x_rotation.description", new Object[0]));
         register("y_rotation", (p_197461_0_) -> {
            p_197461_0_.setYRotation(MinMaxBoundsWrapped.func_207921_a(p_197461_0_.getReader(), true, MathHelper::wrapDegrees));
         }, (p_202036_0_) -> {
            return p_202036_0_.getYRotation() == MinMaxBoundsWrapped.UNBOUNDED;
         }, new TranslationTextComponent("argument.entity.options.y_rotation.description", new Object[0]));
         register("limit", (p_197456_0_) -> {
            int lvt_1_1_ = p_197456_0_.getReader().getCursor();
            int lvt_2_1_ = p_197456_0_.getReader().readInt();
            if (lvt_2_1_ < 1) {
               p_197456_0_.getReader().setCursor(lvt_1_1_);
               throw NONPOSITIVE_LIMIT.createWithContext(p_197456_0_.getReader());
            } else {
               p_197456_0_.setLimit(lvt_2_1_);
               p_197456_0_.setLimited(true);
            }
         }, (p_202035_0_) -> {
            return !p_202035_0_.isCurrentEntity() && !p_202035_0_.isLimited();
         }, new TranslationTextComponent("argument.entity.options.limit.description", new Object[0]));
         register("sort", (p_197455_0_) -> {
            int lvt_1_1_ = p_197455_0_.getReader().getCursor();
            String lvt_2_1_ = p_197455_0_.getReader().readUnquotedString();
            p_197455_0_.setSuggestionHandler((p_202056_0_, p_202056_1_) -> {
               return ISuggestionProvider.suggest((Iterable)Arrays.asList("nearest", "furthest", "random", "arbitrary"), p_202056_0_);
            });
            byte var5 = -1;
            switch(lvt_2_1_.hashCode()) {
            case -938285885:
               if (lvt_2_1_.equals("random")) {
                  var5 = 2;
               }
               break;
            case 1510793967:
               if (lvt_2_1_.equals("furthest")) {
                  var5 = 1;
               }
               break;
            case 1780188658:
               if (lvt_2_1_.equals("arbitrary")) {
                  var5 = 3;
               }
               break;
            case 1825779806:
               if (lvt_2_1_.equals("nearest")) {
                  var5 = 0;
               }
            }

            BiConsumer lvt_3_5_;
            switch(var5) {
            case 0:
               lvt_3_5_ = EntitySelectorParser.NEAREST;
               break;
            case 1:
               lvt_3_5_ = EntitySelectorParser.FURTHEST;
               break;
            case 2:
               lvt_3_5_ = EntitySelectorParser.RANDOM;
               break;
            case 3:
               lvt_3_5_ = EntitySelectorParser.ARBITRARY;
               break;
            default:
               p_197455_0_.getReader().setCursor(lvt_1_1_);
               throw INVALID_SORT.createWithContext(p_197455_0_.getReader(), lvt_2_1_);
            }

            p_197455_0_.setSorter(lvt_3_5_);
            p_197455_0_.setSorted(true);
         }, (p_202043_0_) -> {
            return !p_202043_0_.isCurrentEntity() && !p_202043_0_.isSorted();
         }, new TranslationTextComponent("argument.entity.options.sort.description", new Object[0]));
         register("gamemode", (p_197452_0_) -> {
            p_197452_0_.setSuggestionHandler((p_202018_1_, p_202018_2_) -> {
               String lvt_3_1_ = p_202018_1_.getRemaining().toLowerCase(Locale.ROOT);
               boolean lvt_4_1_ = !p_197452_0_.hasGamemodeNotEquals();
               boolean lvt_5_1_ = true;
               if (!lvt_3_1_.isEmpty()) {
                  if (lvt_3_1_.charAt(0) == '!') {
                     lvt_4_1_ = false;
                     lvt_3_1_ = lvt_3_1_.substring(1);
                  } else {
                     lvt_5_1_ = false;
                  }
               }

               GameType[] var6 = GameType.values();
               int var7 = var6.length;

               for(int var8 = 0; var8 < var7; ++var8) {
                  GameType lvt_9_1_ = var6[var8];
                  if (lvt_9_1_ != GameType.NOT_SET && lvt_9_1_.getName().toLowerCase(Locale.ROOT).startsWith(lvt_3_1_)) {
                     if (lvt_5_1_) {
                        p_202018_1_.suggest('!' + lvt_9_1_.getName());
                     }

                     if (lvt_4_1_) {
                        p_202018_1_.suggest(lvt_9_1_.getName());
                     }
                  }
               }

               return p_202018_1_.buildFuture();
            });
            int lvt_1_1_ = p_197452_0_.getReader().getCursor();
            boolean lvt_2_1_ = p_197452_0_.shouldInvertValue();
            if (p_197452_0_.hasGamemodeNotEquals() && !lvt_2_1_) {
               p_197452_0_.getReader().setCursor(lvt_1_1_);
               throw INAPPLICABLE_ENTITY_OPTION.createWithContext(p_197452_0_.getReader(), "gamemode");
            } else {
               String lvt_3_1_ = p_197452_0_.getReader().readUnquotedString();
               GameType lvt_4_1_ = GameType.parseGameTypeWithDefault(lvt_3_1_, GameType.NOT_SET);
               if (lvt_4_1_ == GameType.NOT_SET) {
                  p_197452_0_.getReader().setCursor(lvt_1_1_);
                  throw INVALID_GAME_MODE.createWithContext(p_197452_0_.getReader(), lvt_3_1_);
               } else {
                  p_197452_0_.setIncludeNonPlayers(false);
                  p_197452_0_.addFilter((p_202055_2_) -> {
                     if (!(p_202055_2_ instanceof ServerPlayerEntity)) {
                        return false;
                     } else {
                        GameType lvt_3_1_ = ((ServerPlayerEntity)p_202055_2_).interactionManager.getGameType();
                        return lvt_2_1_ ? lvt_3_1_ != lvt_4_1_ : lvt_3_1_ == lvt_4_1_;
                     }
                  });
                  if (lvt_2_1_) {
                     p_197452_0_.setHasGamemodeNotEquals(true);
                  } else {
                     p_197452_0_.setHasGamemodeEquals(true);
                  }

               }
            }
         }, (p_202048_0_) -> {
            return !p_202048_0_.hasGamemodeEquals();
         }, new TranslationTextComponent("argument.entity.options.gamemode.description", new Object[0]));
         register("team", (p_197449_0_) -> {
            boolean lvt_1_1_ = p_197449_0_.shouldInvertValue();
            String lvt_2_1_ = p_197449_0_.getReader().readUnquotedString();
            p_197449_0_.addFilter((p_197454_2_) -> {
               if (!(p_197454_2_ instanceof LivingEntity)) {
                  return false;
               } else {
                  Team lvt_3_1_ = p_197454_2_.getTeam();
                  String lvt_4_1_ = lvt_3_1_ == null ? "" : lvt_3_1_.getName();
                  return lvt_4_1_.equals(lvt_2_1_) != lvt_1_1_;
               }
            });
            if (lvt_1_1_) {
               p_197449_0_.setHasTeamNotEquals(true);
            } else {
               p_197449_0_.setHasTeamEquals(true);
            }

         }, (p_202038_0_) -> {
            return !p_202038_0_.hasTeamEquals();
         }, new TranslationTextComponent("argument.entity.options.team.description", new Object[0]));
         register("type", (p_197447_0_) -> {
            p_197447_0_.setSuggestionHandler((p_202052_1_, p_202052_2_) -> {
               ISuggestionProvider.suggestIterable(Registry.ENTITY_TYPE.keySet(), p_202052_1_, String.valueOf('!'));
               ISuggestionProvider.suggestIterable(EntityTypeTags.getCollection().getRegisteredTags(), p_202052_1_, "!#");
               if (!p_197447_0_.isTypeLimitedInversely()) {
                  ISuggestionProvider.suggestIterable(Registry.ENTITY_TYPE.keySet(), p_202052_1_);
                  ISuggestionProvider.suggestIterable(EntityTypeTags.getCollection().getRegisteredTags(), p_202052_1_, String.valueOf('#'));
               }

               return p_202052_1_.buildFuture();
            });
            int lvt_1_1_ = p_197447_0_.getReader().getCursor();
            boolean lvt_2_1_ = p_197447_0_.shouldInvertValue();
            if (p_197447_0_.isTypeLimitedInversely() && !lvt_2_1_) {
               p_197447_0_.getReader().setCursor(lvt_1_1_);
               throw INAPPLICABLE_ENTITY_OPTION.createWithContext(p_197447_0_.getReader(), "type");
            } else {
               if (lvt_2_1_) {
                  p_197447_0_.setTypeLimitedInversely();
               }

               ResourceLocation lvt_3_1_;
               if (p_197447_0_.func_218115_f()) {
                  lvt_3_1_ = ResourceLocation.read(p_197447_0_.getReader());
                  Tag<EntityType<?>> lvt_4_1_ = EntityTypeTags.getCollection().get(lvt_3_1_);
                  if (lvt_4_1_ == null) {
                     p_197447_0_.getReader().setCursor(lvt_1_1_);
                     throw INVALID_ENTITY_TYPE.createWithContext(p_197447_0_.getReader(), lvt_3_1_.toString());
                  }

                  p_197447_0_.addFilter((p_218127_2_) -> {
                     return lvt_4_1_.contains(p_218127_2_.getType()) != lvt_2_1_;
                  });
               } else {
                  lvt_3_1_ = ResourceLocation.read(p_197447_0_.getReader());
                  EntityType<?> lvt_4_2_ = (EntityType)Registry.ENTITY_TYPE.getValue(lvt_3_1_).orElseThrow(() -> {
                     p_197447_0_.getReader().setCursor(lvt_1_1_);
                     return INVALID_ENTITY_TYPE.createWithContext(p_197447_0_.getReader(), lvt_3_1_.toString());
                  });
                  if (Objects.equals(EntityType.PLAYER, lvt_4_2_) && !lvt_2_1_) {
                     p_197447_0_.setIncludeNonPlayers(false);
                  }

                  p_197447_0_.addFilter((p_202057_2_) -> {
                     return Objects.equals(lvt_4_2_, p_202057_2_.getType()) != lvt_2_1_;
                  });
                  if (!lvt_2_1_) {
                     p_197447_0_.func_218114_a(lvt_4_2_);
                  }
               }

            }
         }, (p_202047_0_) -> {
            return !p_202047_0_.isTypeLimited();
         }, new TranslationTextComponent("argument.entity.options.type.description", new Object[0]));
         register("tag", (p_197448_0_) -> {
            boolean lvt_1_1_ = p_197448_0_.shouldInvertValue();
            String lvt_2_1_ = p_197448_0_.getReader().readUnquotedString();
            p_197448_0_.addFilter((p_197466_2_) -> {
               if ("".equals(lvt_2_1_)) {
                  return p_197466_2_.getTags().isEmpty() != lvt_1_1_;
               } else {
                  return p_197466_2_.getTags().contains(lvt_2_1_) != lvt_1_1_;
               }
            });
         }, (p_202041_0_) -> {
            return true;
         }, new TranslationTextComponent("argument.entity.options.tag.description", new Object[0]));
         register("nbt", (p_197450_0_) -> {
            boolean lvt_1_1_ = p_197450_0_.shouldInvertValue();
            CompoundNBT lvt_2_1_ = (new JsonToNBT(p_197450_0_.getReader())).readStruct();
            p_197450_0_.addFilter((p_197443_2_) -> {
               CompoundNBT lvt_3_1_ = p_197443_2_.writeWithoutTypeId(new CompoundNBT());
               if (p_197443_2_ instanceof ServerPlayerEntity) {
                  ItemStack lvt_4_1_ = ((ServerPlayerEntity)p_197443_2_).inventory.getCurrentItem();
                  if (!lvt_4_1_.isEmpty()) {
                     lvt_3_1_.put("SelectedItem", lvt_4_1_.write(new CompoundNBT()));
                  }
               }

               return NBTUtil.areNBTEquals(lvt_2_1_, lvt_3_1_, true) != lvt_1_1_;
            });
         }, (p_202046_0_) -> {
            return true;
         }, new TranslationTextComponent("argument.entity.options.nbt.description", new Object[0]));
         register("scores", (p_197457_0_) -> {
            StringReader lvt_1_1_ = p_197457_0_.getReader();
            Map<String, MinMaxBounds.IntBound> lvt_2_1_ = Maps.newHashMap();
            lvt_1_1_.expect('{');
            lvt_1_1_.skipWhitespace();

            while(lvt_1_1_.canRead() && lvt_1_1_.peek() != '}') {
               lvt_1_1_.skipWhitespace();
               String lvt_3_1_ = lvt_1_1_.readUnquotedString();
               lvt_1_1_.skipWhitespace();
               lvt_1_1_.expect('=');
               lvt_1_1_.skipWhitespace();
               MinMaxBounds.IntBound lvt_4_1_ = MinMaxBounds.IntBound.fromReader(lvt_1_1_);
               lvt_2_1_.put(lvt_3_1_, lvt_4_1_);
               lvt_1_1_.skipWhitespace();
               if (lvt_1_1_.canRead() && lvt_1_1_.peek() == ',') {
                  lvt_1_1_.skip();
               }
            }

            lvt_1_1_.expect('}');
            if (!lvt_2_1_.isEmpty()) {
               p_197457_0_.addFilter((p_197465_1_) -> {
                  Scoreboard lvt_2_1_x = p_197465_1_.getServer().getScoreboard();
                  String lvt_3_1_ = p_197465_1_.getScoreboardName();
                  Iterator var4 = lvt_2_1_.entrySet().iterator();

                  Entry lvt_5_1_;
                  int lvt_8_1_;
                  do {
                     if (!var4.hasNext()) {
                        return true;
                     }

                     lvt_5_1_ = (Entry)var4.next();
                     ScoreObjective lvt_6_1_ = lvt_2_1_x.getObjective((String)lvt_5_1_.getKey());
                     if (lvt_6_1_ == null) {
                        return false;
                     }

                     if (!lvt_2_1_x.entityHasObjective(lvt_3_1_, lvt_6_1_)) {
                        return false;
                     }

                     Score lvt_7_1_ = lvt_2_1_x.getOrCreateScore(lvt_3_1_, lvt_6_1_);
                     lvt_8_1_ = lvt_7_1_.getScorePoints();
                  } while(((MinMaxBounds.IntBound)lvt_5_1_.getValue()).test(lvt_8_1_));

                  return false;
               });
            }

            p_197457_0_.setHasScores(true);
         }, (p_202033_0_) -> {
            return !p_202033_0_.hasScores();
         }, new TranslationTextComponent("argument.entity.options.scores.description", new Object[0]));
         register("advancements", (p_197453_0_) -> {
            StringReader lvt_1_1_ = p_197453_0_.getReader();
            Map<ResourceLocation, Predicate<AdvancementProgress>> lvt_2_1_ = Maps.newHashMap();
            lvt_1_1_.expect('{');
            lvt_1_1_.skipWhitespace();

            while(lvt_1_1_.canRead() && lvt_1_1_.peek() != '}') {
               lvt_1_1_.skipWhitespace();
               ResourceLocation lvt_3_1_ = ResourceLocation.read(lvt_1_1_);
               lvt_1_1_.skipWhitespace();
               lvt_1_1_.expect('=');
               lvt_1_1_.skipWhitespace();
               if (lvt_1_1_.canRead() && lvt_1_1_.peek() == '{') {
                  Map<String, Predicate<CriterionProgress>> lvt_4_1_ = Maps.newHashMap();
                  lvt_1_1_.skipWhitespace();
                  lvt_1_1_.expect('{');
                  lvt_1_1_.skipWhitespace();

                  while(lvt_1_1_.canRead() && lvt_1_1_.peek() != '}') {
                     lvt_1_1_.skipWhitespace();
                     String lvt_5_1_ = lvt_1_1_.readUnquotedString();
                     lvt_1_1_.skipWhitespace();
                     lvt_1_1_.expect('=');
                     lvt_1_1_.skipWhitespace();
                     boolean lvt_6_1_ = lvt_1_1_.readBoolean();
                     lvt_4_1_.put(lvt_5_1_, (p_197444_1_) -> {
                        return p_197444_1_.isObtained() == lvt_6_1_;
                     });
                     lvt_1_1_.skipWhitespace();
                     if (lvt_1_1_.canRead() && lvt_1_1_.peek() == ',') {
                        lvt_1_1_.skip();
                     }
                  }

                  lvt_1_1_.skipWhitespace();
                  lvt_1_1_.expect('}');
                  lvt_1_1_.skipWhitespace();
                  lvt_2_1_.put(lvt_3_1_, (p_197435_1_) -> {
                     Iterator var2 = lvt_4_1_.entrySet().iterator();

                     Entry lvt_3_1_;
                     CriterionProgress lvt_4_1_x;
                     do {
                        if (!var2.hasNext()) {
                           return true;
                        }

                        lvt_3_1_ = (Entry)var2.next();
                        lvt_4_1_x = p_197435_1_.getCriterionProgress((String)lvt_3_1_.getKey());
                     } while(lvt_4_1_x != null && ((Predicate)lvt_3_1_.getValue()).test(lvt_4_1_x));

                     return false;
                  });
               } else {
                  boolean lvt_4_2_ = lvt_1_1_.readBoolean();
                  lvt_2_1_.put(lvt_3_1_, (p_197451_1_) -> {
                     return p_197451_1_.isDone() == lvt_4_2_;
                  });
               }

               lvt_1_1_.skipWhitespace();
               if (lvt_1_1_.canRead() && lvt_1_1_.peek() == ',') {
                  lvt_1_1_.skip();
               }
            }

            lvt_1_1_.expect('}');
            if (!lvt_2_1_.isEmpty()) {
               p_197453_0_.addFilter((p_197441_1_) -> {
                  if (!(p_197441_1_ instanceof ServerPlayerEntity)) {
                     return false;
                  } else {
                     ServerPlayerEntity lvt_2_1_x = (ServerPlayerEntity)p_197441_1_;
                     PlayerAdvancements lvt_3_1_ = lvt_2_1_x.getAdvancements();
                     AdvancementManager lvt_4_1_ = lvt_2_1_x.getServer().getAdvancementManager();
                     Iterator var5 = lvt_2_1_.entrySet().iterator();

                     Entry lvt_6_1_;
                     Advancement lvt_7_1_;
                     do {
                        if (!var5.hasNext()) {
                           return true;
                        }

                        lvt_6_1_ = (Entry)var5.next();
                        lvt_7_1_ = lvt_4_1_.getAdvancement((ResourceLocation)lvt_6_1_.getKey());
                     } while(lvt_7_1_ != null && ((Predicate)lvt_6_1_.getValue()).test(lvt_3_1_.getProgress(lvt_7_1_)));

                     return false;
                  }
               });
               p_197453_0_.setIncludeNonPlayers(false);
            }

            p_197453_0_.setHasAdvancements(true);
         }, (p_202032_0_) -> {
            return !p_202032_0_.hasAdvancements();
         }, new TranslationTextComponent("argument.entity.options.advancements.description", new Object[0]));
         register("predicate", (p_229367_0_) -> {
            boolean lvt_1_1_ = p_229367_0_.shouldInvertValue();
            ResourceLocation lvt_2_1_ = ResourceLocation.read(p_229367_0_.getReader());
            p_229367_0_.addFilter((p_229366_2_) -> {
               if (!(p_229366_2_.world instanceof ServerWorld)) {
                  return false;
               } else {
                  ServerWorld lvt_3_1_ = (ServerWorld)p_229366_2_.world;
                  ILootCondition lvt_4_1_ = lvt_3_1_.getServer().func_229736_aP_().func_227517_a_(lvt_2_1_);
                  if (lvt_4_1_ == null) {
                     return false;
                  } else {
                     LootContext lvt_5_1_ = (new LootContext.Builder(lvt_3_1_)).withParameter(LootParameters.THIS_ENTITY, p_229366_2_).withParameter(LootParameters.POSITION, new BlockPos(p_229366_2_)).build(LootParameterSets.field_227558_d_);
                     return lvt_1_1_ ^ lvt_4_1_.test(lvt_5_1_);
                  }
               }
            });
         }, (p_229365_0_) -> {
            return true;
         }, new TranslationTextComponent("argument.entity.options.predicate.description", new Object[0]));
      }
   }

   public static EntityOptions.IFilter get(EntitySelectorParser p_202017_0_, String p_202017_1_, int p_202017_2_) throws CommandSyntaxException {
      EntityOptions.OptionHandler lvt_3_1_ = (EntityOptions.OptionHandler)REGISTRY.get(p_202017_1_);
      if (lvt_3_1_ != null) {
         if (lvt_3_1_.canHandle.test(p_202017_0_)) {
            return lvt_3_1_.handler;
         } else {
            throw INAPPLICABLE_ENTITY_OPTION.createWithContext(p_202017_0_.getReader(), p_202017_1_);
         }
      } else {
         p_202017_0_.getReader().setCursor(p_202017_2_);
         throw UNKNOWN_ENTITY_OPTION.createWithContext(p_202017_0_.getReader(), p_202017_1_);
      }
   }

   public static void suggestOptions(EntitySelectorParser p_202049_0_, SuggestionsBuilder p_202049_1_) {
      String lvt_2_1_ = p_202049_1_.getRemaining().toLowerCase(Locale.ROOT);
      Iterator var3 = REGISTRY.entrySet().iterator();

      while(var3.hasNext()) {
         Entry<String, EntityOptions.OptionHandler> lvt_4_1_ = (Entry)var3.next();
         if (((EntityOptions.OptionHandler)lvt_4_1_.getValue()).canHandle.test(p_202049_0_) && ((String)lvt_4_1_.getKey()).toLowerCase(Locale.ROOT).startsWith(lvt_2_1_)) {
            p_202049_1_.suggest((String)lvt_4_1_.getKey() + '=', ((EntityOptions.OptionHandler)lvt_4_1_.getValue()).tooltip);
         }
      }

   }

   static class OptionHandler {
      public final EntityOptions.IFilter handler;
      public final Predicate<EntitySelectorParser> canHandle;
      public final ITextComponent tooltip;

      private OptionHandler(EntityOptions.IFilter p_i48717_1_, Predicate<EntitySelectorParser> p_i48717_2_, ITextComponent p_i48717_3_) {
         this.handler = p_i48717_1_;
         this.canHandle = p_i48717_2_;
         this.tooltip = p_i48717_3_;
      }

      // $FF: synthetic method
      OptionHandler(EntityOptions.IFilter p_i48718_1_, Predicate p_i48718_2_, ITextComponent p_i48718_3_, Object p_i48718_4_) {
         this(p_i48718_1_, p_i48718_2_, p_i48718_3_);
      }
   }

   public interface IFilter {
      void handle(EntitySelectorParser var1) throws CommandSyntaxException;
   }
}
