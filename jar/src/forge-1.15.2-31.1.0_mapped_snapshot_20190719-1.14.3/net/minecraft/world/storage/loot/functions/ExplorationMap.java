package net.minecraft.world.storage.loot.functions;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Locale;
import java.util.Set;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.MapData;
import net.minecraft.world.storage.MapDecoration;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootFunction;
import net.minecraft.world.storage.loot.LootParameter;
import net.minecraft.world.storage.loot.LootParameters;
import net.minecraft.world.storage.loot.conditions.ILootCondition;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ExplorationMap extends LootFunction {
   private static final Logger LOGGER = LogManager.getLogger();
   public static final MapDecoration.Type field_215910_a;
   private final String destination;
   private final MapDecoration.Type decoration;
   private final byte zoom;
   private final int searchRadius;
   private final boolean skipExistingChunks;

   private ExplorationMap(ILootCondition[] p_i48873_1_, String p_i48873_2_, MapDecoration.Type p_i48873_3_, byte p_i48873_4_, int p_i48873_5_, boolean p_i48873_6_) {
      super(p_i48873_1_);
      this.destination = p_i48873_2_;
      this.decoration = p_i48873_3_;
      this.zoom = p_i48873_4_;
      this.searchRadius = p_i48873_5_;
      this.skipExistingChunks = p_i48873_6_;
   }

   public Set<LootParameter<?>> getRequiredParameters() {
      return ImmutableSet.of(LootParameters.POSITION);
   }

   public ItemStack doApply(ItemStack p_215859_1_, LootContext p_215859_2_) {
      if (p_215859_1_.getItem() != Items.MAP) {
         return p_215859_1_;
      } else {
         BlockPos lvt_3_1_ = (BlockPos)p_215859_2_.get(LootParameters.POSITION);
         if (lvt_3_1_ != null) {
            ServerWorld lvt_4_1_ = p_215859_2_.getWorld();
            BlockPos lvt_5_1_ = lvt_4_1_.findNearestStructure(this.destination, lvt_3_1_, this.searchRadius, this.skipExistingChunks);
            if (lvt_5_1_ != null) {
               ItemStack lvt_6_1_ = FilledMapItem.setupNewMap(lvt_4_1_, lvt_5_1_.getX(), lvt_5_1_.getZ(), this.zoom, true, true);
               FilledMapItem.func_226642_a_(lvt_4_1_, lvt_6_1_);
               MapData.addTargetDecoration(lvt_6_1_, lvt_5_1_, "+", this.decoration);
               lvt_6_1_.setDisplayName(new TranslationTextComponent("filled_map." + this.destination.toLowerCase(Locale.ROOT), new Object[0]));
               return lvt_6_1_;
            }
         }

         return p_215859_1_;
      }
   }

   public static ExplorationMap.Builder func_215903_b() {
      return new ExplorationMap.Builder();
   }

   // $FF: synthetic method
   ExplorationMap(ILootCondition[] p_i51235_1_, String p_i51235_2_, MapDecoration.Type p_i51235_3_, byte p_i51235_4_, int p_i51235_5_, boolean p_i51235_6_, Object p_i51235_7_) {
      this(p_i51235_1_, p_i51235_2_, p_i51235_3_, p_i51235_4_, p_i51235_5_, p_i51235_6_);
   }

   static {
      field_215910_a = MapDecoration.Type.MANSION;
   }

   public static class Serializer extends LootFunction.Serializer<ExplorationMap> {
      protected Serializer() {
         super(new ResourceLocation("exploration_map"), ExplorationMap.class);
      }

      public void serialize(JsonObject p_186532_1_, ExplorationMap p_186532_2_, JsonSerializationContext p_186532_3_) {
         super.serialize(p_186532_1_, (LootFunction)p_186532_2_, p_186532_3_);
         if (!p_186532_2_.destination.equals("Buried_Treasure")) {
            p_186532_1_.add("destination", p_186532_3_.serialize(p_186532_2_.destination));
         }

         if (p_186532_2_.decoration != ExplorationMap.field_215910_a) {
            p_186532_1_.add("decoration", p_186532_3_.serialize(p_186532_2_.decoration.toString().toLowerCase(Locale.ROOT)));
         }

         if (p_186532_2_.zoom != 2) {
            p_186532_1_.addProperty("zoom", p_186532_2_.zoom);
         }

         if (p_186532_2_.searchRadius != 50) {
            p_186532_1_.addProperty("search_radius", p_186532_2_.searchRadius);
         }

         if (!p_186532_2_.skipExistingChunks) {
            p_186532_1_.addProperty("skip_existing_chunks", p_186532_2_.skipExistingChunks);
         }

      }

      public ExplorationMap deserialize(JsonObject p_186530_1_, JsonDeserializationContext p_186530_2_, ILootCondition[] p_186530_3_) {
         String lvt_4_1_ = p_186530_1_.has("destination") ? JSONUtils.getString(p_186530_1_, "destination") : "Buried_Treasure";
         lvt_4_1_ = Feature.STRUCTURES.containsKey(lvt_4_1_.toLowerCase(Locale.ROOT)) ? lvt_4_1_ : "Buried_Treasure";
         String lvt_5_1_ = p_186530_1_.has("decoration") ? JSONUtils.getString(p_186530_1_, "decoration") : "mansion";
         MapDecoration.Type lvt_6_1_ = ExplorationMap.field_215910_a;

         try {
            lvt_6_1_ = MapDecoration.Type.valueOf(lvt_5_1_.toUpperCase(Locale.ROOT));
         } catch (IllegalArgumentException var10) {
            ExplorationMap.LOGGER.error("Error while parsing loot table decoration entry. Found {}. Defaulting to " + ExplorationMap.field_215910_a, lvt_5_1_);
         }

         byte lvt_7_2_ = JSONUtils.func_219795_a(p_186530_1_, "zoom", (byte)2);
         int lvt_8_1_ = JSONUtils.getInt(p_186530_1_, "search_radius", 50);
         boolean lvt_9_1_ = JSONUtils.getBoolean(p_186530_1_, "skip_existing_chunks", true);
         return new ExplorationMap(p_186530_3_, lvt_4_1_, lvt_6_1_, lvt_7_2_, lvt_8_1_, lvt_9_1_);
      }

      // $FF: synthetic method
      public LootFunction deserialize(JsonObject p_186530_1_, JsonDeserializationContext p_186530_2_, ILootCondition[] p_186530_3_) {
         return this.deserialize(p_186530_1_, p_186530_2_, p_186530_3_);
      }
   }

   public static class Builder extends LootFunction.Builder<ExplorationMap.Builder> {
      private String field_216066_a = "Buried_Treasure";
      private MapDecoration.Type field_216067_b;
      private byte field_216068_c;
      private int field_216069_d;
      private boolean field_216070_e;

      public Builder() {
         this.field_216067_b = ExplorationMap.field_215910_a;
         this.field_216068_c = 2;
         this.field_216069_d = 50;
         this.field_216070_e = true;
      }

      protected ExplorationMap.Builder doCast() {
         return this;
      }

      public ExplorationMap.Builder func_216065_a(String p_216065_1_) {
         this.field_216066_a = p_216065_1_;
         return this;
      }

      public ExplorationMap.Builder func_216064_a(MapDecoration.Type p_216064_1_) {
         this.field_216067_b = p_216064_1_;
         return this;
      }

      public ExplorationMap.Builder func_216062_a(byte p_216062_1_) {
         this.field_216068_c = p_216062_1_;
         return this;
      }

      public ExplorationMap.Builder func_216063_a(boolean p_216063_1_) {
         this.field_216070_e = p_216063_1_;
         return this;
      }

      public ILootFunction build() {
         return new ExplorationMap(this.getConditions(), this.field_216066_a, this.field_216067_b, this.field_216068_c, this.field_216069_d, this.field_216070_e);
      }

      // $FF: synthetic method
      protected LootFunction.Builder doCast() {
         return this.doCast();
      }
   }
}
