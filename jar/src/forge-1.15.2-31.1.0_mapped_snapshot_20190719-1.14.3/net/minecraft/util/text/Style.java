package net.minecraft.util.text;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;

public class Style {
   private Style parentStyle;
   private TextFormatting color;
   private Boolean bold;
   private Boolean italic;
   private Boolean underlined;
   private Boolean strikethrough;
   private Boolean obfuscated;
   private ClickEvent clickEvent;
   private HoverEvent hoverEvent;
   private String insertion;
   private static final Style ROOT = new Style() {
      @Nullable
      public TextFormatting getColor() {
         return null;
      }

      public boolean getBold() {
         return false;
      }

      public boolean getItalic() {
         return false;
      }

      public boolean getStrikethrough() {
         return false;
      }

      public boolean getUnderlined() {
         return false;
      }

      public boolean getObfuscated() {
         return false;
      }

      @Nullable
      public ClickEvent getClickEvent() {
         return null;
      }

      @Nullable
      public HoverEvent getHoverEvent() {
         return null;
      }

      @Nullable
      public String getInsertion() {
         return null;
      }

      public Style setColor(TextFormatting p_150238_1_) {
         throw new UnsupportedOperationException();
      }

      public Style setBold(Boolean p_150227_1_) {
         throw new UnsupportedOperationException();
      }

      public Style setItalic(Boolean p_150217_1_) {
         throw new UnsupportedOperationException();
      }

      public Style setStrikethrough(Boolean p_150225_1_) {
         throw new UnsupportedOperationException();
      }

      public Style setUnderlined(Boolean p_150228_1_) {
         throw new UnsupportedOperationException();
      }

      public Style setObfuscated(Boolean p_150237_1_) {
         throw new UnsupportedOperationException();
      }

      public Style setClickEvent(ClickEvent p_150241_1_) {
         throw new UnsupportedOperationException();
      }

      public Style setHoverEvent(HoverEvent p_150209_1_) {
         throw new UnsupportedOperationException();
      }

      public Style setParentStyle(Style p_150221_1_) {
         throw new UnsupportedOperationException();
      }

      public String toString() {
         return "Style.ROOT";
      }

      public Style createShallowCopy() {
         return this;
      }

      public Style createDeepCopy() {
         return this;
      }

      public String getFormattingCode() {
         return "";
      }
   };

   @Nullable
   public TextFormatting getColor() {
      return this.color == null ? this.getParent().getColor() : this.color;
   }

   public boolean getBold() {
      return this.bold == null ? this.getParent().getBold() : this.bold;
   }

   public boolean getItalic() {
      return this.italic == null ? this.getParent().getItalic() : this.italic;
   }

   public boolean getStrikethrough() {
      return this.strikethrough == null ? this.getParent().getStrikethrough() : this.strikethrough;
   }

   public boolean getUnderlined() {
      return this.underlined == null ? this.getParent().getUnderlined() : this.underlined;
   }

   public boolean getObfuscated() {
      return this.obfuscated == null ? this.getParent().getObfuscated() : this.obfuscated;
   }

   public boolean isEmpty() {
      return this.bold == null && this.italic == null && this.strikethrough == null && this.underlined == null && this.obfuscated == null && this.color == null && this.clickEvent == null && this.hoverEvent == null && this.insertion == null;
   }

   @Nullable
   public ClickEvent getClickEvent() {
      return this.clickEvent == null ? this.getParent().getClickEvent() : this.clickEvent;
   }

   @Nullable
   public HoverEvent getHoverEvent() {
      return this.hoverEvent == null ? this.getParent().getHoverEvent() : this.hoverEvent;
   }

   @Nullable
   public String getInsertion() {
      return this.insertion == null ? this.getParent().getInsertion() : this.insertion;
   }

   public Style setColor(TextFormatting p_150238_1_) {
      this.color = p_150238_1_;
      return this;
   }

   public Style setBold(Boolean p_150227_1_) {
      this.bold = p_150227_1_;
      return this;
   }

   public Style setItalic(Boolean p_150217_1_) {
      this.italic = p_150217_1_;
      return this;
   }

   public Style setStrikethrough(Boolean p_150225_1_) {
      this.strikethrough = p_150225_1_;
      return this;
   }

   public Style setUnderlined(Boolean p_150228_1_) {
      this.underlined = p_150228_1_;
      return this;
   }

   public Style setObfuscated(Boolean p_150237_1_) {
      this.obfuscated = p_150237_1_;
      return this;
   }

   public Style setClickEvent(ClickEvent p_150241_1_) {
      this.clickEvent = p_150241_1_;
      return this;
   }

   public Style setHoverEvent(HoverEvent p_150209_1_) {
      this.hoverEvent = p_150209_1_;
      return this;
   }

   public Style setInsertion(String p_179989_1_) {
      this.insertion = p_179989_1_;
      return this;
   }

   public Style setParentStyle(Style p_150221_1_) {
      this.parentStyle = p_150221_1_;
      return this;
   }

   public String getFormattingCode() {
      if (this.isEmpty()) {
         return this.parentStyle != null ? this.parentStyle.getFormattingCode() : "";
      } else {
         StringBuilder lvt_1_1_ = new StringBuilder();
         if (this.getColor() != null) {
            lvt_1_1_.append(this.getColor());
         }

         if (this.getBold()) {
            lvt_1_1_.append(TextFormatting.BOLD);
         }

         if (this.getItalic()) {
            lvt_1_1_.append(TextFormatting.ITALIC);
         }

         if (this.getUnderlined()) {
            lvt_1_1_.append(TextFormatting.UNDERLINE);
         }

         if (this.getObfuscated()) {
            lvt_1_1_.append(TextFormatting.OBFUSCATED);
         }

         if (this.getStrikethrough()) {
            lvt_1_1_.append(TextFormatting.STRIKETHROUGH);
         }

         return lvt_1_1_.toString();
      }
   }

   private Style getParent() {
      return this.parentStyle == null ? ROOT : this.parentStyle;
   }

   public String toString() {
      return "Style{hasParent=" + (this.parentStyle != null) + ", color=" + this.color + ", bold=" + this.bold + ", italic=" + this.italic + ", underlined=" + this.underlined + ", obfuscated=" + this.obfuscated + ", clickEvent=" + this.getClickEvent() + ", hoverEvent=" + this.getHoverEvent() + ", insertion=" + this.getInsertion() + '}';
   }

   public boolean equals(Object p_equals_1_) {
      if (this == p_equals_1_) {
         return true;
      } else if (!(p_equals_1_ instanceof Style)) {
         return false;
      } else {
         boolean var10000;
         label77: {
            Style lvt_2_1_ = (Style)p_equals_1_;
            if (this.getBold() == lvt_2_1_.getBold() && this.getColor() == lvt_2_1_.getColor() && this.getItalic() == lvt_2_1_.getItalic() && this.getObfuscated() == lvt_2_1_.getObfuscated() && this.getStrikethrough() == lvt_2_1_.getStrikethrough() && this.getUnderlined() == lvt_2_1_.getUnderlined()) {
               label71: {
                  if (this.getClickEvent() != null) {
                     if (!this.getClickEvent().equals(lvt_2_1_.getClickEvent())) {
                        break label71;
                     }
                  } else if (lvt_2_1_.getClickEvent() != null) {
                     break label71;
                  }

                  if (this.getHoverEvent() != null) {
                     if (!this.getHoverEvent().equals(lvt_2_1_.getHoverEvent())) {
                        break label71;
                     }
                  } else if (lvt_2_1_.getHoverEvent() != null) {
                     break label71;
                  }

                  if (this.getInsertion() != null) {
                     if (this.getInsertion().equals(lvt_2_1_.getInsertion())) {
                        break label77;
                     }
                  } else if (lvt_2_1_.getInsertion() == null) {
                     break label77;
                  }
               }
            }

            var10000 = false;
            return var10000;
         }

         var10000 = true;
         return var10000;
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.color, this.bold, this.italic, this.underlined, this.strikethrough, this.obfuscated, this.clickEvent, this.hoverEvent, this.insertion});
   }

   public Style createShallowCopy() {
      Style lvt_1_1_ = new Style();
      lvt_1_1_.bold = this.bold;
      lvt_1_1_.italic = this.italic;
      lvt_1_1_.strikethrough = this.strikethrough;
      lvt_1_1_.underlined = this.underlined;
      lvt_1_1_.obfuscated = this.obfuscated;
      lvt_1_1_.color = this.color;
      lvt_1_1_.clickEvent = this.clickEvent;
      lvt_1_1_.hoverEvent = this.hoverEvent;
      lvt_1_1_.parentStyle = this.parentStyle;
      lvt_1_1_.insertion = this.insertion;
      return lvt_1_1_;
   }

   public Style createDeepCopy() {
      Style lvt_1_1_ = new Style();
      lvt_1_1_.setBold(this.getBold());
      lvt_1_1_.setItalic(this.getItalic());
      lvt_1_1_.setStrikethrough(this.getStrikethrough());
      lvt_1_1_.setUnderlined(this.getUnderlined());
      lvt_1_1_.setObfuscated(this.getObfuscated());
      lvt_1_1_.setColor(this.getColor());
      lvt_1_1_.setClickEvent(this.getClickEvent());
      lvt_1_1_.setHoverEvent(this.getHoverEvent());
      lvt_1_1_.setInsertion(this.getInsertion());
      return lvt_1_1_;
   }

   public static class Serializer implements JsonDeserializer<Style>, JsonSerializer<Style> {
      @Nullable
      public Style deserialize(JsonElement p_deserialize_1_, Type p_deserialize_2_, JsonDeserializationContext p_deserialize_3_) throws JsonParseException {
         if (p_deserialize_1_.isJsonObject()) {
            Style lvt_4_1_ = new Style();
            JsonObject lvt_5_1_ = p_deserialize_1_.getAsJsonObject();
            if (lvt_5_1_ == null) {
               return null;
            } else {
               if (lvt_5_1_.has("bold")) {
                  lvt_4_1_.bold = lvt_5_1_.get("bold").getAsBoolean();
               }

               if (lvt_5_1_.has("italic")) {
                  lvt_4_1_.italic = lvt_5_1_.get("italic").getAsBoolean();
               }

               if (lvt_5_1_.has("underlined")) {
                  lvt_4_1_.underlined = lvt_5_1_.get("underlined").getAsBoolean();
               }

               if (lvt_5_1_.has("strikethrough")) {
                  lvt_4_1_.strikethrough = lvt_5_1_.get("strikethrough").getAsBoolean();
               }

               if (lvt_5_1_.has("obfuscated")) {
                  lvt_4_1_.obfuscated = lvt_5_1_.get("obfuscated").getAsBoolean();
               }

               if (lvt_5_1_.has("color")) {
                  lvt_4_1_.color = (TextFormatting)p_deserialize_3_.deserialize(lvt_5_1_.get("color"), TextFormatting.class);
               }

               if (lvt_5_1_.has("insertion")) {
                  lvt_4_1_.insertion = lvt_5_1_.get("insertion").getAsString();
               }

               JsonObject lvt_6_2_;
               String lvt_7_2_;
               if (lvt_5_1_.has("clickEvent")) {
                  lvt_6_2_ = JSONUtils.getJsonObject(lvt_5_1_, "clickEvent");
                  lvt_7_2_ = JSONUtils.getString(lvt_6_2_, "action", (String)null);
                  ClickEvent.Action lvt_8_1_ = lvt_7_2_ == null ? null : ClickEvent.Action.getValueByCanonicalName(lvt_7_2_);
                  String lvt_9_1_ = JSONUtils.getString(lvt_6_2_, "value", (String)null);
                  if (lvt_8_1_ != null && lvt_9_1_ != null && lvt_8_1_.shouldAllowInChat()) {
                     lvt_4_1_.clickEvent = new ClickEvent(lvt_8_1_, lvt_9_1_);
                  }
               }

               if (lvt_5_1_.has("hoverEvent")) {
                  lvt_6_2_ = JSONUtils.getJsonObject(lvt_5_1_, "hoverEvent");
                  lvt_7_2_ = JSONUtils.getString(lvt_6_2_, "action", (String)null);
                  HoverEvent.Action lvt_8_2_ = lvt_7_2_ == null ? null : HoverEvent.Action.getValueByCanonicalName(lvt_7_2_);
                  ITextComponent lvt_9_2_ = (ITextComponent)p_deserialize_3_.deserialize(lvt_6_2_.get("value"), ITextComponent.class);
                  if (lvt_8_2_ != null && lvt_9_2_ != null && lvt_8_2_.shouldAllowInChat()) {
                     lvt_4_1_.hoverEvent = new HoverEvent(lvt_8_2_, lvt_9_2_);
                  }
               }

               return lvt_4_1_;
            }
         } else {
            return null;
         }
      }

      @Nullable
      public JsonElement serialize(Style p_serialize_1_, Type p_serialize_2_, JsonSerializationContext p_serialize_3_) {
         if (p_serialize_1_.isEmpty()) {
            return null;
         } else {
            JsonObject lvt_4_1_ = new JsonObject();
            if (p_serialize_1_.bold != null) {
               lvt_4_1_.addProperty("bold", p_serialize_1_.bold);
            }

            if (p_serialize_1_.italic != null) {
               lvt_4_1_.addProperty("italic", p_serialize_1_.italic);
            }

            if (p_serialize_1_.underlined != null) {
               lvt_4_1_.addProperty("underlined", p_serialize_1_.underlined);
            }

            if (p_serialize_1_.strikethrough != null) {
               lvt_4_1_.addProperty("strikethrough", p_serialize_1_.strikethrough);
            }

            if (p_serialize_1_.obfuscated != null) {
               lvt_4_1_.addProperty("obfuscated", p_serialize_1_.obfuscated);
            }

            if (p_serialize_1_.color != null) {
               lvt_4_1_.add("color", p_serialize_3_.serialize(p_serialize_1_.color));
            }

            if (p_serialize_1_.insertion != null) {
               lvt_4_1_.add("insertion", p_serialize_3_.serialize(p_serialize_1_.insertion));
            }

            JsonObject lvt_5_2_;
            if (p_serialize_1_.clickEvent != null) {
               lvt_5_2_ = new JsonObject();
               lvt_5_2_.addProperty("action", p_serialize_1_.clickEvent.getAction().getCanonicalName());
               lvt_5_2_.addProperty("value", p_serialize_1_.clickEvent.getValue());
               lvt_4_1_.add("clickEvent", lvt_5_2_);
            }

            if (p_serialize_1_.hoverEvent != null) {
               lvt_5_2_ = new JsonObject();
               lvt_5_2_.addProperty("action", p_serialize_1_.hoverEvent.getAction().getCanonicalName());
               lvt_5_2_.add("value", p_serialize_3_.serialize(p_serialize_1_.hoverEvent.getValue()));
               lvt_4_1_.add("hoverEvent", lvt_5_2_);
            }

            return lvt_4_1_;
         }
      }

      // $FF: synthetic method
      @Nullable
      public JsonElement serialize(Object p_serialize_1_, Type p_serialize_2_, JsonSerializationContext p_serialize_3_) {
         return this.serialize((Style)p_serialize_1_, p_serialize_2_, p_serialize_3_);
      }

      // $FF: synthetic method
      @Nullable
      public Object deserialize(JsonElement p_deserialize_1_, Type p_deserialize_2_, JsonDeserializationContext p_deserialize_3_) throws JsonParseException {
         return this.deserialize(p_deserialize_1_, p_deserialize_2_, p_deserialize_3_);
      }
   }
}
