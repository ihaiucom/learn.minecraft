package net.minecraft.client.renderer.model;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import javax.annotation.Nullable;
import net.minecraft.util.Direction;
import net.minecraft.util.JSONUtils;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BlockPartFace {
   public final Direction cullFace;
   public final int tintIndex;
   public final String texture;
   public final BlockFaceUV blockFaceUV;

   public BlockPartFace(@Nullable Direction p_i46230_1_, int p_i46230_2_, String p_i46230_3_, BlockFaceUV p_i46230_4_) {
      this.cullFace = p_i46230_1_;
      this.tintIndex = p_i46230_2_;
      this.texture = p_i46230_3_;
      this.blockFaceUV = p_i46230_4_;
   }

   @OnlyIn(Dist.CLIENT)
   public static class Deserializer implements JsonDeserializer<BlockPartFace> {
      public BlockPartFace deserialize(JsonElement p_deserialize_1_, Type p_deserialize_2_, JsonDeserializationContext p_deserialize_3_) throws JsonParseException {
         JsonObject lvt_4_1_ = p_deserialize_1_.getAsJsonObject();
         Direction lvt_5_1_ = this.parseCullFace(lvt_4_1_);
         int lvt_6_1_ = this.parseTintIndex(lvt_4_1_);
         String lvt_7_1_ = this.parseTexture(lvt_4_1_);
         BlockFaceUV lvt_8_1_ = (BlockFaceUV)p_deserialize_3_.deserialize(lvt_4_1_, BlockFaceUV.class);
         return new BlockPartFace(lvt_5_1_, lvt_6_1_, lvt_7_1_, lvt_8_1_);
      }

      protected int parseTintIndex(JsonObject p_178337_1_) {
         return JSONUtils.getInt(p_178337_1_, "tintindex", -1);
      }

      private String parseTexture(JsonObject p_178340_1_) {
         return JSONUtils.getString(p_178340_1_, "texture");
      }

      @Nullable
      private Direction parseCullFace(JsonObject p_178339_1_) {
         String lvt_2_1_ = JSONUtils.getString(p_178339_1_, "cullface", "");
         return Direction.byName(lvt_2_1_);
      }

      // $FF: synthetic method
      public Object deserialize(JsonElement p_deserialize_1_, Type p_deserialize_2_, JsonDeserializationContext p_deserialize_3_) throws JsonParseException {
         return this.deserialize(p_deserialize_1_, p_deserialize_2_, p_deserialize_3_);
      }
   }
}
