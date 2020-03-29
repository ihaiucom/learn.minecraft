package net.minecraft.command.arguments;

import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.tree.ArgumentCommandNode;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.mojang.brigadier.tree.RootCommandNode;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.command.arguments.serializers.BrigadierSerializers;
import net.minecraft.network.PacketBuffer;
import net.minecraft.test.TestArgArgument;
import net.minecraft.test.TestTypeArgument;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SharedConstants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ArgumentTypes {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final Map<Class<?>, ArgumentTypes.Entry<?>> CLASS_TYPE_MAP = Maps.newHashMap();
   private static final Map<ResourceLocation, ArgumentTypes.Entry<?>> ID_TYPE_MAP = Maps.newHashMap();

   public static <T extends ArgumentType<?>> void register(String p_218136_0_, Class<T> p_218136_1_, IArgumentSerializer<T> p_218136_2_) {
      ResourceLocation lvt_3_1_ = new ResourceLocation(p_218136_0_);
      if (CLASS_TYPE_MAP.containsKey(p_218136_1_)) {
         throw new IllegalArgumentException("Class " + p_218136_1_.getName() + " already has a serializer!");
      } else if (ID_TYPE_MAP.containsKey(lvt_3_1_)) {
         throw new IllegalArgumentException("'" + lvt_3_1_ + "' is already a registered serializer!");
      } else {
         ArgumentTypes.Entry<T> lvt_4_1_ = new ArgumentTypes.Entry(p_218136_1_, p_218136_2_, lvt_3_1_);
         CLASS_TYPE_MAP.put(p_218136_1_, lvt_4_1_);
         ID_TYPE_MAP.put(lvt_3_1_, lvt_4_1_);
      }
   }

   public static void registerArgumentTypes() {
      BrigadierSerializers.registerArgumentTypes();
      register("entity", EntityArgument.class, new EntityArgument.Serializer());
      register("game_profile", GameProfileArgument.class, new ArgumentSerializer(GameProfileArgument::gameProfile));
      register("block_pos", BlockPosArgument.class, new ArgumentSerializer(BlockPosArgument::blockPos));
      register("column_pos", ColumnPosArgument.class, new ArgumentSerializer(ColumnPosArgument::columnPos));
      register("vec3", Vec3Argument.class, new ArgumentSerializer(Vec3Argument::vec3));
      register("vec2", Vec2Argument.class, new ArgumentSerializer(Vec2Argument::vec2));
      register("block_state", BlockStateArgument.class, new ArgumentSerializer(BlockStateArgument::blockState));
      register("block_predicate", BlockPredicateArgument.class, new ArgumentSerializer(BlockPredicateArgument::blockPredicate));
      register("item_stack", ItemArgument.class, new ArgumentSerializer(ItemArgument::item));
      register("item_predicate", ItemPredicateArgument.class, new ArgumentSerializer(ItemPredicateArgument::itemPredicate));
      register("color", ColorArgument.class, new ArgumentSerializer(ColorArgument::color));
      register("component", ComponentArgument.class, new ArgumentSerializer(ComponentArgument::component));
      register("message", MessageArgument.class, new ArgumentSerializer(MessageArgument::message));
      register("nbt_compound_tag", NBTCompoundTagArgument.class, new ArgumentSerializer(NBTCompoundTagArgument::func_218043_a));
      register("nbt_tag", NBTTagArgument.class, new ArgumentSerializer(NBTTagArgument::func_218085_a));
      register("nbt_path", NBTPathArgument.class, new ArgumentSerializer(NBTPathArgument::nbtPath));
      register("objective", ObjectiveArgument.class, new ArgumentSerializer(ObjectiveArgument::objective));
      register("objective_criteria", ObjectiveCriteriaArgument.class, new ArgumentSerializer(ObjectiveCriteriaArgument::objectiveCriteria));
      register("operation", OperationArgument.class, new ArgumentSerializer(OperationArgument::operation));
      register("particle", ParticleArgument.class, new ArgumentSerializer(ParticleArgument::particle));
      register("rotation", RotationArgument.class, new ArgumentSerializer(RotationArgument::rotation));
      register("scoreboard_slot", ScoreboardSlotArgument.class, new ArgumentSerializer(ScoreboardSlotArgument::scoreboardSlot));
      register("score_holder", ScoreHolderArgument.class, new ScoreHolderArgument.Serializer());
      register("swizzle", SwizzleArgument.class, new ArgumentSerializer(SwizzleArgument::swizzle));
      register("team", TeamArgument.class, new ArgumentSerializer(TeamArgument::team));
      register("item_slot", SlotArgument.class, new ArgumentSerializer(SlotArgument::slot));
      register("resource_location", ResourceLocationArgument.class, new ArgumentSerializer(ResourceLocationArgument::resourceLocation));
      register("mob_effect", PotionArgument.class, new ArgumentSerializer(PotionArgument::mobEffect));
      register("function", FunctionArgument.class, new ArgumentSerializer(FunctionArgument::func_200021_a));
      register("entity_anchor", EntityAnchorArgument.class, new ArgumentSerializer(EntityAnchorArgument::entityAnchor));
      register("int_range", IRangeArgument.IntRange.class, new IRangeArgument.IntRange.Serializer());
      register("float_range", IRangeArgument.FloatRange.class, new IRangeArgument.FloatRange.Serializer());
      register("item_enchantment", EnchantmentArgument.class, new ArgumentSerializer(EnchantmentArgument::enchantment));
      register("entity_summon", EntitySummonArgument.class, new ArgumentSerializer(EntitySummonArgument::entitySummon));
      register("dimension", DimensionArgument.class, new ArgumentSerializer(DimensionArgument::getDimension));
      register("time", TimeArgument.class, new ArgumentSerializer(TimeArgument::func_218091_a));
      if (SharedConstants.developmentMode) {
         register("test_argument", TestArgArgument.class, new ArgumentSerializer(TestArgArgument::func_229665_a_));
         register("test_class", TestTypeArgument.class, new ArgumentSerializer(TestTypeArgument::func_229611_a_));
      }

   }

   @Nullable
   private static ArgumentTypes.Entry<?> get(ResourceLocation p_197482_0_) {
      return (ArgumentTypes.Entry)ID_TYPE_MAP.get(p_197482_0_);
   }

   @Nullable
   private static ArgumentTypes.Entry<?> get(ArgumentType<?> p_201040_0_) {
      return (ArgumentTypes.Entry)CLASS_TYPE_MAP.get(p_201040_0_.getClass());
   }

   public static <T extends ArgumentType<?>> void serialize(PacketBuffer p_197484_0_, T p_197484_1_) {
      ArgumentTypes.Entry<T> lvt_2_1_ = get(p_197484_1_);
      if (lvt_2_1_ == null) {
         LOGGER.error("Could not serialize {} ({}) - will not be sent to client!", p_197484_1_, p_197484_1_.getClass());
         p_197484_0_.writeResourceLocation(new ResourceLocation(""));
      } else {
         p_197484_0_.writeResourceLocation(lvt_2_1_.id);
         lvt_2_1_.serializer.write(p_197484_1_, p_197484_0_);
      }
   }

   @Nullable
   public static ArgumentType<?> deserialize(PacketBuffer p_197486_0_) {
      ResourceLocation lvt_1_1_ = p_197486_0_.readResourceLocation();
      ArgumentTypes.Entry<?> lvt_2_1_ = get(lvt_1_1_);
      if (lvt_2_1_ == null) {
         LOGGER.error("Could not deserialize {}", lvt_1_1_);
         return null;
      } else {
         return lvt_2_1_.serializer.read(p_197486_0_);
      }
   }

   private static <T extends ArgumentType<?>> void serialize(JsonObject p_201042_0_, T p_201042_1_) {
      ArgumentTypes.Entry<T> lvt_2_1_ = get(p_201042_1_);
      if (lvt_2_1_ == null) {
         LOGGER.error("Could not serialize argument {} ({})!", p_201042_1_, p_201042_1_.getClass());
         p_201042_0_.addProperty("type", "unknown");
      } else {
         p_201042_0_.addProperty("type", "argument");
         p_201042_0_.addProperty("parser", lvt_2_1_.id.toString());
         JsonObject lvt_3_1_ = new JsonObject();
         lvt_2_1_.serializer.write(p_201042_1_, lvt_3_1_);
         if (lvt_3_1_.size() > 0) {
            p_201042_0_.add("properties", lvt_3_1_);
         }
      }

   }

   public static <S> JsonObject serialize(CommandDispatcher<S> p_200388_0_, CommandNode<S> p_200388_1_) {
      JsonObject lvt_2_1_ = new JsonObject();
      if (p_200388_1_ instanceof RootCommandNode) {
         lvt_2_1_.addProperty("type", "root");
      } else if (p_200388_1_ instanceof LiteralCommandNode) {
         lvt_2_1_.addProperty("type", "literal");
      } else if (p_200388_1_ instanceof ArgumentCommandNode) {
         serialize(lvt_2_1_, ((ArgumentCommandNode)p_200388_1_).getType());
      } else {
         LOGGER.error("Could not serialize node {} ({})!", p_200388_1_, p_200388_1_.getClass());
         lvt_2_1_.addProperty("type", "unknown");
      }

      JsonObject lvt_3_1_ = new JsonObject();
      Iterator var4 = p_200388_1_.getChildren().iterator();

      while(var4.hasNext()) {
         CommandNode<S> lvt_5_1_ = (CommandNode)var4.next();
         lvt_3_1_.add(lvt_5_1_.getName(), serialize(p_200388_0_, lvt_5_1_));
      }

      if (lvt_3_1_.size() > 0) {
         lvt_2_1_.add("children", lvt_3_1_);
      }

      if (p_200388_1_.getCommand() != null) {
         lvt_2_1_.addProperty("executable", true);
      }

      if (p_200388_1_.getRedirect() != null) {
         Collection<String> lvt_4_1_ = p_200388_0_.getPath(p_200388_1_.getRedirect());
         if (!lvt_4_1_.isEmpty()) {
            JsonArray lvt_5_2_ = new JsonArray();
            Iterator var6 = lvt_4_1_.iterator();

            while(var6.hasNext()) {
               String lvt_7_1_ = (String)var6.next();
               lvt_5_2_.add(lvt_7_1_);
            }

            lvt_2_1_.add("redirect", lvt_5_2_);
         }
      }

      return lvt_2_1_;
   }

   static class Entry<T extends ArgumentType<?>> {
      public final Class<T> argumentClass;
      public final IArgumentSerializer<T> serializer;
      public final ResourceLocation id;

      private Entry(Class<T> p_i48088_1_, IArgumentSerializer<T> p_i48088_2_, ResourceLocation p_i48088_3_) {
         this.argumentClass = p_i48088_1_;
         this.serializer = p_i48088_2_;
         this.id = p_i48088_3_;
      }

      // $FF: synthetic method
      Entry(Class p_i48089_1_, IArgumentSerializer p_i48089_2_, ResourceLocation p_i48089_3_, Object p_i48089_4_) {
         this(p_i48089_1_, p_i48089_2_, p_i48089_3_);
      }
   }
}
