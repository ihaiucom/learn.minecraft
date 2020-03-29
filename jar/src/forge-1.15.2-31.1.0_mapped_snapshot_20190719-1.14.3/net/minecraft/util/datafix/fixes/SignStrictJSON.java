package net.minecraft.util.datafix.fixes;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import java.lang.reflect.Type;
import java.util.Iterator;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.datafix.TypeReferences;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import org.apache.commons.lang3.StringUtils;

public class SignStrictJSON extends NamedEntityFix {
   public static final Gson GSON = (new GsonBuilder()).registerTypeAdapter(ITextComponent.class, new JsonDeserializer<ITextComponent>() {
      public ITextComponent deserialize(JsonElement p_deserialize_1_, Type p_deserialize_2_, JsonDeserializationContext p_deserialize_3_) throws JsonParseException {
         if (p_deserialize_1_.isJsonPrimitive()) {
            return new StringTextComponent(p_deserialize_1_.getAsString());
         } else if (p_deserialize_1_.isJsonArray()) {
            JsonArray lvt_4_1_ = p_deserialize_1_.getAsJsonArray();
            ITextComponent lvt_5_1_ = null;
            Iterator var6 = lvt_4_1_.iterator();

            while(var6.hasNext()) {
               JsonElement lvt_7_1_ = (JsonElement)var6.next();
               ITextComponent lvt_8_1_ = this.deserialize(lvt_7_1_, lvt_7_1_.getClass(), p_deserialize_3_);
               if (lvt_5_1_ == null) {
                  lvt_5_1_ = lvt_8_1_;
               } else {
                  lvt_5_1_.appendSibling(lvt_8_1_);
               }
            }

            return lvt_5_1_;
         } else {
            throw new JsonParseException("Don't know how to turn " + p_deserialize_1_ + " into a Component");
         }
      }

      // $FF: synthetic method
      public Object deserialize(JsonElement p_deserialize_1_, Type p_deserialize_2_, JsonDeserializationContext p_deserialize_3_) throws JsonParseException {
         return this.deserialize(p_deserialize_1_, p_deserialize_2_, p_deserialize_3_);
      }
   }).create();

   public SignStrictJSON(Schema p_i49680_1_, boolean p_i49680_2_) {
      super(p_i49680_1_, p_i49680_2_, "BlockEntitySignTextStrictJsonFix", TypeReferences.BLOCK_ENTITY, "Sign");
   }

   private Dynamic<?> updateLine(Dynamic<?> p_209647_1_, String p_209647_2_) {
      String lvt_3_1_ = p_209647_1_.get(p_209647_2_).asString("");
      ITextComponent lvt_4_1_ = null;
      if (!"null".equals(lvt_3_1_) && !StringUtils.isEmpty(lvt_3_1_)) {
         if (lvt_3_1_.charAt(0) == '"' && lvt_3_1_.charAt(lvt_3_1_.length() - 1) == '"' || lvt_3_1_.charAt(0) == '{' && lvt_3_1_.charAt(lvt_3_1_.length() - 1) == '}') {
            try {
               lvt_4_1_ = (ITextComponent)JSONUtils.fromJson(GSON, lvt_3_1_, ITextComponent.class, true);
               if (lvt_4_1_ == null) {
                  lvt_4_1_ = new StringTextComponent("");
               }
            } catch (JsonParseException var8) {
            }

            if (lvt_4_1_ == null) {
               try {
                  lvt_4_1_ = ITextComponent.Serializer.fromJson(lvt_3_1_);
               } catch (JsonParseException var7) {
               }
            }

            if (lvt_4_1_ == null) {
               try {
                  lvt_4_1_ = ITextComponent.Serializer.fromJsonLenient(lvt_3_1_);
               } catch (JsonParseException var6) {
               }
            }

            if (lvt_4_1_ == null) {
               lvt_4_1_ = new StringTextComponent(lvt_3_1_);
            }
         } else {
            lvt_4_1_ = new StringTextComponent(lvt_3_1_);
         }
      } else {
         lvt_4_1_ = new StringTextComponent("");
      }

      return p_209647_1_.set(p_209647_2_, p_209647_1_.createString(ITextComponent.Serializer.toJson((ITextComponent)lvt_4_1_)));
   }

   protected Typed<?> fix(Typed<?> p_207419_1_) {
      return p_207419_1_.update(DSL.remainderFinder(), (p_206380_1_) -> {
         p_206380_1_ = this.updateLine(p_206380_1_, "Text1");
         p_206380_1_ = this.updateLine(p_206380_1_, "Text2");
         p_206380_1_ = this.updateLine(p_206380_1_, "Text3");
         p_206380_1_ = this.updateLine(p_206380_1_, "Text4");
         return p_206380_1_;
      });
   }
}
