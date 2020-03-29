package net.minecraft.client.renderer.model;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.multipart.Multipart;
import net.minecraft.client.renderer.model.multipart.Selector;
import net.minecraft.state.StateContainer;
import net.minecraft.util.JSONUtils;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BlockModelDefinition {
   private final Map<String, VariantList> mapVariants = Maps.newLinkedHashMap();
   private Multipart multipart;

   public static BlockModelDefinition fromJson(BlockModelDefinition.ContainerHolder p_209577_0_, Reader p_209577_1_) {
      return (BlockModelDefinition)JSONUtils.fromJson(p_209577_0_.gson, p_209577_1_, BlockModelDefinition.class);
   }

   public BlockModelDefinition(Map<String, VariantList> p_i46572_1_, Multipart p_i46572_2_) {
      this.multipart = p_i46572_2_;
      this.mapVariants.putAll(p_i46572_1_);
   }

   public BlockModelDefinition(List<BlockModelDefinition> p_i46222_1_) {
      BlockModelDefinition lvt_2_1_ = null;

      BlockModelDefinition lvt_4_1_;
      for(Iterator var3 = p_i46222_1_.iterator(); var3.hasNext(); this.mapVariants.putAll(lvt_4_1_.mapVariants)) {
         lvt_4_1_ = (BlockModelDefinition)var3.next();
         if (lvt_4_1_.hasMultipartData()) {
            this.mapVariants.clear();
            lvt_2_1_ = lvt_4_1_;
         }
      }

      if (lvt_2_1_ != null) {
         this.multipart = lvt_2_1_.multipart;
      }

   }

   public boolean equals(Object p_equals_1_) {
      if (this == p_equals_1_) {
         return true;
      } else {
         if (p_equals_1_ instanceof BlockModelDefinition) {
            BlockModelDefinition lvt_2_1_ = (BlockModelDefinition)p_equals_1_;
            if (this.mapVariants.equals(lvt_2_1_.mapVariants)) {
               return this.hasMultipartData() ? this.multipart.equals(lvt_2_1_.multipart) : !lvt_2_1_.hasMultipartData();
            }
         }

         return false;
      }
   }

   public int hashCode() {
      return 31 * this.mapVariants.hashCode() + (this.hasMultipartData() ? this.multipart.hashCode() : 0);
   }

   public Map<String, VariantList> getVariants() {
      return this.mapVariants;
   }

   public boolean hasMultipartData() {
      return this.multipart != null;
   }

   public Multipart getMultipartData() {
      return this.multipart;
   }

   @OnlyIn(Dist.CLIENT)
   public static class Deserializer implements JsonDeserializer<BlockModelDefinition> {
      public BlockModelDefinition deserialize(JsonElement p_deserialize_1_, Type p_deserialize_2_, JsonDeserializationContext p_deserialize_3_) throws JsonParseException {
         JsonObject lvt_4_1_ = p_deserialize_1_.getAsJsonObject();
         Map<String, VariantList> lvt_5_1_ = this.parseMapVariants(p_deserialize_3_, lvt_4_1_);
         Multipart lvt_6_1_ = this.parseMultipart(p_deserialize_3_, lvt_4_1_);
         if (!lvt_5_1_.isEmpty() || lvt_6_1_ != null && !lvt_6_1_.getVariants().isEmpty()) {
            return new BlockModelDefinition(lvt_5_1_, lvt_6_1_);
         } else {
            throw new JsonParseException("Neither 'variants' nor 'multipart' found");
         }
      }

      protected Map<String, VariantList> parseMapVariants(JsonDeserializationContext p_187999_1_, JsonObject p_187999_2_) {
         Map<String, VariantList> lvt_3_1_ = Maps.newHashMap();
         if (p_187999_2_.has("variants")) {
            JsonObject lvt_4_1_ = JSONUtils.getJsonObject(p_187999_2_, "variants");
            Iterator var5 = lvt_4_1_.entrySet().iterator();

            while(var5.hasNext()) {
               Entry<String, JsonElement> lvt_6_1_ = (Entry)var5.next();
               lvt_3_1_.put(lvt_6_1_.getKey(), p_187999_1_.deserialize((JsonElement)lvt_6_1_.getValue(), VariantList.class));
            }
         }

         return lvt_3_1_;
      }

      @Nullable
      protected Multipart parseMultipart(JsonDeserializationContext p_187998_1_, JsonObject p_187998_2_) {
         if (!p_187998_2_.has("multipart")) {
            return null;
         } else {
            JsonArray lvt_3_1_ = JSONUtils.getJsonArray(p_187998_2_, "multipart");
            return (Multipart)p_187998_1_.deserialize(lvt_3_1_, Multipart.class);
         }
      }

      // $FF: synthetic method
      public Object deserialize(JsonElement p_deserialize_1_, Type p_deserialize_2_, JsonDeserializationContext p_deserialize_3_) throws JsonParseException {
         return this.deserialize(p_deserialize_1_, p_deserialize_2_, p_deserialize_3_);
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static final class ContainerHolder {
      protected final Gson gson = (new GsonBuilder()).registerTypeAdapter(BlockModelDefinition.class, new BlockModelDefinition.Deserializer()).registerTypeAdapter(Variant.class, new Variant.Deserializer()).registerTypeAdapter(VariantList.class, new VariantList.Deserializer()).registerTypeAdapter(Multipart.class, new Multipart.Deserializer(this)).registerTypeAdapter(Selector.class, new Selector.Deserializer()).create();
      private StateContainer<Block, BlockState> stateContainer;

      public StateContainer<Block, BlockState> getStateContainer() {
         return this.stateContainer;
      }

      public void setStateContainer(StateContainer<Block, BlockState> p_209573_1_) {
         this.stateContainer = p_209573_1_;
      }
   }
}
