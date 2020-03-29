package net.minecraft.util.text;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.stream.JsonReader;
import com.mojang.brigadier.Message;
import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.util.EnumTypeAdapterFactory;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;

public interface ITextComponent extends Message, Iterable<ITextComponent> {
   ITextComponent setStyle(Style var1);

   Style getStyle();

   default ITextComponent appendText(String p_150258_1_) {
      return this.appendSibling(new StringTextComponent(p_150258_1_));
   }

   ITextComponent appendSibling(ITextComponent var1);

   String getUnformattedComponentText();

   default String getString() {
      StringBuilder lvt_1_1_ = new StringBuilder();
      this.stream().forEach((p_212635_1_) -> {
         lvt_1_1_.append(p_212635_1_.getUnformattedComponentText());
      });
      return lvt_1_1_.toString();
   }

   default String getStringTruncated(int p_212636_1_) {
      StringBuilder lvt_2_1_ = new StringBuilder();
      Iterator lvt_3_1_ = this.stream().iterator();

      while(lvt_3_1_.hasNext()) {
         int lvt_4_1_ = p_212636_1_ - lvt_2_1_.length();
         if (lvt_4_1_ <= 0) {
            break;
         }

         String lvt_5_1_ = ((ITextComponent)lvt_3_1_.next()).getUnformattedComponentText();
         lvt_2_1_.append(lvt_5_1_.length() <= lvt_4_1_ ? lvt_5_1_ : lvt_5_1_.substring(0, lvt_4_1_));
      }

      return lvt_2_1_.toString();
   }

   default String getFormattedText() {
      StringBuilder lvt_1_1_ = new StringBuilder();
      String lvt_2_1_ = "";
      Iterator lvt_3_1_ = this.stream().iterator();

      while(lvt_3_1_.hasNext()) {
         ITextComponent lvt_4_1_ = (ITextComponent)lvt_3_1_.next();
         String lvt_5_1_ = lvt_4_1_.getUnformattedComponentText();
         if (!lvt_5_1_.isEmpty()) {
            String lvt_6_1_ = lvt_4_1_.getStyle().getFormattingCode();
            if (!lvt_6_1_.equals(lvt_2_1_)) {
               if (!lvt_2_1_.isEmpty()) {
                  lvt_1_1_.append(TextFormatting.RESET);
               }

               lvt_1_1_.append(lvt_6_1_);
               lvt_2_1_ = lvt_6_1_;
            }

            lvt_1_1_.append(lvt_5_1_);
         }
      }

      if (!lvt_2_1_.isEmpty()) {
         lvt_1_1_.append(TextFormatting.RESET);
      }

      return lvt_1_1_.toString();
   }

   List<ITextComponent> getSiblings();

   Stream<ITextComponent> stream();

   default Stream<ITextComponent> func_212637_f() {
      return this.stream().map(ITextComponent::copyWithoutSiblings);
   }

   default Iterator<ITextComponent> iterator() {
      return this.func_212637_f().iterator();
   }

   ITextComponent shallowCopy();

   default ITextComponent deepCopy() {
      ITextComponent lvt_1_1_ = this.shallowCopy();
      lvt_1_1_.setStyle(this.getStyle().createShallowCopy());
      Iterator var2 = this.getSiblings().iterator();

      while(var2.hasNext()) {
         ITextComponent lvt_3_1_ = (ITextComponent)var2.next();
         lvt_1_1_.appendSibling(lvt_3_1_.deepCopy());
      }

      return lvt_1_1_;
   }

   default ITextComponent applyTextStyle(Consumer<Style> p_211710_1_) {
      p_211710_1_.accept(this.getStyle());
      return this;
   }

   default ITextComponent applyTextStyles(TextFormatting... p_211709_1_) {
      TextFormatting[] var2 = p_211709_1_;
      int var3 = p_211709_1_.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         TextFormatting lvt_5_1_ = var2[var4];
         this.applyTextStyle(lvt_5_1_);
      }

      return this;
   }

   default ITextComponent applyTextStyle(TextFormatting p_211708_1_) {
      Style lvt_2_1_ = this.getStyle();
      if (p_211708_1_.isColor()) {
         lvt_2_1_.setColor(p_211708_1_);
      }

      if (p_211708_1_.isFancyStyling()) {
         switch(p_211708_1_) {
         case OBFUSCATED:
            lvt_2_1_.setObfuscated(true);
            break;
         case BOLD:
            lvt_2_1_.setBold(true);
            break;
         case STRIKETHROUGH:
            lvt_2_1_.setStrikethrough(true);
            break;
         case UNDERLINE:
            lvt_2_1_.setUnderlined(true);
            break;
         case ITALIC:
            lvt_2_1_.setItalic(true);
         }
      }

      return this;
   }

   static ITextComponent copyWithoutSiblings(ITextComponent p_212639_0_) {
      ITextComponent lvt_1_1_ = p_212639_0_.shallowCopy();
      lvt_1_1_.setStyle(p_212639_0_.getStyle().createDeepCopy());
      return lvt_1_1_;
   }

   public static class Serializer implements JsonDeserializer<ITextComponent>, JsonSerializer<ITextComponent> {
      private static final Gson GSON = (Gson)Util.make(() -> {
         GsonBuilder lvt_0_1_ = new GsonBuilder();
         lvt_0_1_.disableHtmlEscaping();
         lvt_0_1_.registerTypeHierarchyAdapter(ITextComponent.class, new ITextComponent.Serializer());
         lvt_0_1_.registerTypeHierarchyAdapter(Style.class, new Style.Serializer());
         lvt_0_1_.registerTypeAdapterFactory(new EnumTypeAdapterFactory());
         return lvt_0_1_.create();
      });
      private static final Field JSON_READER_POS_FIELD = (Field)Util.make(() -> {
         try {
            new JsonReader(new StringReader(""));
            Field lvt_0_1_ = JsonReader.class.getDeclaredField("pos");
            lvt_0_1_.setAccessible(true);
            return lvt_0_1_;
         } catch (NoSuchFieldException var1) {
            throw new IllegalStateException("Couldn't get field 'pos' for JsonReader", var1);
         }
      });
      private static final Field JSON_READER_LINESTART_FIELD = (Field)Util.make(() -> {
         try {
            new JsonReader(new StringReader(""));
            Field lvt_0_1_ = JsonReader.class.getDeclaredField("lineStart");
            lvt_0_1_.setAccessible(true);
            return lvt_0_1_;
         } catch (NoSuchFieldException var1) {
            throw new IllegalStateException("Couldn't get field 'lineStart' for JsonReader", var1);
         }
      });

      public ITextComponent deserialize(JsonElement p_deserialize_1_, Type p_deserialize_2_, JsonDeserializationContext p_deserialize_3_) throws JsonParseException {
         if (p_deserialize_1_.isJsonPrimitive()) {
            return new StringTextComponent(p_deserialize_1_.getAsString());
         } else if (!p_deserialize_1_.isJsonObject()) {
            if (p_deserialize_1_.isJsonArray()) {
               JsonArray lvt_4_2_ = p_deserialize_1_.getAsJsonArray();
               ITextComponent lvt_5_13_ = null;
               Iterator var14 = lvt_4_2_.iterator();

               while(var14.hasNext()) {
                  JsonElement lvt_7_4_ = (JsonElement)var14.next();
                  ITextComponent lvt_8_2_ = this.deserialize(lvt_7_4_, lvt_7_4_.getClass(), p_deserialize_3_);
                  if (lvt_5_13_ == null) {
                     lvt_5_13_ = lvt_8_2_;
                  } else {
                     lvt_5_13_.appendSibling(lvt_8_2_);
                  }
               }

               return lvt_5_13_;
            } else {
               throw new JsonParseException("Don't know how to turn " + p_deserialize_1_ + " into a Component");
            }
         } else {
            JsonObject lvt_4_1_ = p_deserialize_1_.getAsJsonObject();
            Object lvt_5_4_;
            if (lvt_4_1_.has("text")) {
               lvt_5_4_ = new StringTextComponent(JSONUtils.getString(lvt_4_1_, "text"));
            } else {
               String lvt_6_3_;
               if (lvt_4_1_.has("translate")) {
                  lvt_6_3_ = JSONUtils.getString(lvt_4_1_, "translate");
                  if (lvt_4_1_.has("with")) {
                     JsonArray lvt_7_1_ = JSONUtils.getJsonArray(lvt_4_1_, "with");
                     Object[] lvt_8_1_ = new Object[lvt_7_1_.size()];

                     for(int lvt_9_1_ = 0; lvt_9_1_ < lvt_8_1_.length; ++lvt_9_1_) {
                        lvt_8_1_[lvt_9_1_] = this.deserialize(lvt_7_1_.get(lvt_9_1_), p_deserialize_2_, p_deserialize_3_);
                        if (lvt_8_1_[lvt_9_1_] instanceof StringTextComponent) {
                           StringTextComponent lvt_10_1_ = (StringTextComponent)lvt_8_1_[lvt_9_1_];
                           if (lvt_10_1_.getStyle().isEmpty() && lvt_10_1_.getSiblings().isEmpty()) {
                              lvt_8_1_[lvt_9_1_] = lvt_10_1_.getText();
                           }
                        }
                     }

                     lvt_5_4_ = new TranslationTextComponent(lvt_6_3_, lvt_8_1_);
                  } else {
                     lvt_5_4_ = new TranslationTextComponent(lvt_6_3_, new Object[0]);
                  }
               } else if (lvt_4_1_.has("score")) {
                  JsonObject lvt_6_2_ = JSONUtils.getJsonObject(lvt_4_1_, "score");
                  if (!lvt_6_2_.has("name") || !lvt_6_2_.has("objective")) {
                     throw new JsonParseException("A score component needs a least a name and an objective");
                  }

                  lvt_5_4_ = new ScoreTextComponent(JSONUtils.getString(lvt_6_2_, "name"), JSONUtils.getString(lvt_6_2_, "objective"));
                  if (lvt_6_2_.has("value")) {
                     ((ScoreTextComponent)lvt_5_4_).setValue(JSONUtils.getString(lvt_6_2_, "value"));
                  }
               } else if (lvt_4_1_.has("selector")) {
                  lvt_5_4_ = new SelectorTextComponent(JSONUtils.getString(lvt_4_1_, "selector"));
               } else if (lvt_4_1_.has("keybind")) {
                  lvt_5_4_ = new KeybindTextComponent(JSONUtils.getString(lvt_4_1_, "keybind"));
               } else {
                  if (!lvt_4_1_.has("nbt")) {
                     throw new JsonParseException("Don't know how to turn " + p_deserialize_1_ + " into a Component");
                  }

                  lvt_6_3_ = JSONUtils.getString(lvt_4_1_, "nbt");
                  boolean lvt_7_2_ = JSONUtils.getBoolean(lvt_4_1_, "interpret", false);
                  if (lvt_4_1_.has("block")) {
                     lvt_5_4_ = new NBTTextComponent.Block(lvt_6_3_, lvt_7_2_, JSONUtils.getString(lvt_4_1_, "block"));
                  } else if (lvt_4_1_.has("entity")) {
                     lvt_5_4_ = new NBTTextComponent.Entity(lvt_6_3_, lvt_7_2_, JSONUtils.getString(lvt_4_1_, "entity"));
                  } else {
                     if (!lvt_4_1_.has("storage")) {
                        throw new JsonParseException("Don't know how to turn " + p_deserialize_1_ + " into a Component");
                     }

                     lvt_5_4_ = new NBTTextComponent.Storage(lvt_6_3_, lvt_7_2_, new ResourceLocation(JSONUtils.getString(lvt_4_1_, "storage")));
                  }
               }
            }

            if (lvt_4_1_.has("extra")) {
               JsonArray lvt_6_4_ = JSONUtils.getJsonArray(lvt_4_1_, "extra");
               if (lvt_6_4_.size() <= 0) {
                  throw new JsonParseException("Unexpected empty array of components");
               }

               for(int lvt_7_3_ = 0; lvt_7_3_ < lvt_6_4_.size(); ++lvt_7_3_) {
                  ((ITextComponent)lvt_5_4_).appendSibling(this.deserialize(lvt_6_4_.get(lvt_7_3_), p_deserialize_2_, p_deserialize_3_));
               }
            }

            ((ITextComponent)lvt_5_4_).setStyle((Style)p_deserialize_3_.deserialize(p_deserialize_1_, Style.class));
            return (ITextComponent)lvt_5_4_;
         }
      }

      private void serializeChatStyle(Style p_150695_1_, JsonObject p_150695_2_, JsonSerializationContext p_150695_3_) {
         JsonElement lvt_4_1_ = p_150695_3_.serialize(p_150695_1_);
         if (lvt_4_1_.isJsonObject()) {
            JsonObject lvt_5_1_ = (JsonObject)lvt_4_1_;
            Iterator var6 = lvt_5_1_.entrySet().iterator();

            while(var6.hasNext()) {
               Entry<String, JsonElement> lvt_7_1_ = (Entry)var6.next();
               p_150695_2_.add((String)lvt_7_1_.getKey(), (JsonElement)lvt_7_1_.getValue());
            }
         }

      }

      public JsonElement serialize(ITextComponent p_serialize_1_, Type p_serialize_2_, JsonSerializationContext p_serialize_3_) {
         JsonObject lvt_4_1_ = new JsonObject();
         if (!p_serialize_1_.getStyle().isEmpty()) {
            this.serializeChatStyle(p_serialize_1_.getStyle(), lvt_4_1_, p_serialize_3_);
         }

         if (!p_serialize_1_.getSiblings().isEmpty()) {
            JsonArray lvt_5_1_ = new JsonArray();
            Iterator var6 = p_serialize_1_.getSiblings().iterator();

            while(var6.hasNext()) {
               ITextComponent lvt_7_1_ = (ITextComponent)var6.next();
               lvt_5_1_.add(this.serialize((ITextComponent)lvt_7_1_, lvt_7_1_.getClass(), p_serialize_3_));
            }

            lvt_4_1_.add("extra", lvt_5_1_);
         }

         if (p_serialize_1_ instanceof StringTextComponent) {
            lvt_4_1_.addProperty("text", ((StringTextComponent)p_serialize_1_).getText());
         } else if (p_serialize_1_ instanceof TranslationTextComponent) {
            TranslationTextComponent lvt_5_2_ = (TranslationTextComponent)p_serialize_1_;
            lvt_4_1_.addProperty("translate", lvt_5_2_.getKey());
            if (lvt_5_2_.getFormatArgs() != null && lvt_5_2_.getFormatArgs().length > 0) {
               JsonArray lvt_6_1_ = new JsonArray();
               Object[] var19 = lvt_5_2_.getFormatArgs();
               int var8 = var19.length;

               for(int var9 = 0; var9 < var8; ++var9) {
                  Object lvt_10_1_ = var19[var9];
                  if (lvt_10_1_ instanceof ITextComponent) {
                     lvt_6_1_.add(this.serialize((ITextComponent)((ITextComponent)lvt_10_1_), lvt_10_1_.getClass(), p_serialize_3_));
                  } else {
                     lvt_6_1_.add(new JsonPrimitive(String.valueOf(lvt_10_1_)));
                  }
               }

               lvt_4_1_.add("with", lvt_6_1_);
            }
         } else if (p_serialize_1_ instanceof ScoreTextComponent) {
            ScoreTextComponent lvt_5_3_ = (ScoreTextComponent)p_serialize_1_;
            JsonObject lvt_6_2_ = new JsonObject();
            lvt_6_2_.addProperty("name", lvt_5_3_.getName());
            lvt_6_2_.addProperty("objective", lvt_5_3_.getObjective());
            lvt_6_2_.addProperty("value", lvt_5_3_.getUnformattedComponentText());
            lvt_4_1_.add("score", lvt_6_2_);
         } else if (p_serialize_1_ instanceof SelectorTextComponent) {
            SelectorTextComponent lvt_5_4_ = (SelectorTextComponent)p_serialize_1_;
            lvt_4_1_.addProperty("selector", lvt_5_4_.getSelector());
         } else if (p_serialize_1_ instanceof KeybindTextComponent) {
            KeybindTextComponent lvt_5_5_ = (KeybindTextComponent)p_serialize_1_;
            lvt_4_1_.addProperty("keybind", lvt_5_5_.getKeybind());
         } else {
            if (!(p_serialize_1_ instanceof NBTTextComponent)) {
               throw new IllegalArgumentException("Don't know how to serialize " + p_serialize_1_ + " as a Component");
            }

            NBTTextComponent lvt_5_6_ = (NBTTextComponent)p_serialize_1_;
            lvt_4_1_.addProperty("nbt", lvt_5_6_.func_218676_i());
            lvt_4_1_.addProperty("interpret", lvt_5_6_.func_218677_j());
            if (p_serialize_1_ instanceof NBTTextComponent.Block) {
               NBTTextComponent.Block lvt_6_3_ = (NBTTextComponent.Block)p_serialize_1_;
               lvt_4_1_.addProperty("block", lvt_6_3_.func_218683_k());
            } else if (p_serialize_1_ instanceof NBTTextComponent.Entity) {
               NBTTextComponent.Entity lvt_6_4_ = (NBTTextComponent.Entity)p_serialize_1_;
               lvt_4_1_.addProperty("entity", lvt_6_4_.func_218687_k());
            } else {
               if (!(p_serialize_1_ instanceof NBTTextComponent.Storage)) {
                  throw new IllegalArgumentException("Don't know how to serialize " + p_serialize_1_ + " as a Component");
               }

               NBTTextComponent.Storage lvt_6_5_ = (NBTTextComponent.Storage)p_serialize_1_;
               lvt_4_1_.addProperty("storage", lvt_6_5_.func_229726_k_().toString());
            }
         }

         return lvt_4_1_;
      }

      public static String toJson(ITextComponent p_150696_0_) {
         return GSON.toJson(p_150696_0_);
      }

      public static JsonElement toJsonTree(ITextComponent p_200528_0_) {
         return GSON.toJsonTree(p_200528_0_);
      }

      @Nullable
      public static ITextComponent fromJson(String p_150699_0_) {
         return (ITextComponent)JSONUtils.fromJson(GSON, p_150699_0_, ITextComponent.class, false);
      }

      @Nullable
      public static ITextComponent fromJson(JsonElement p_197672_0_) {
         return (ITextComponent)GSON.fromJson(p_197672_0_, ITextComponent.class);
      }

      @Nullable
      public static ITextComponent fromJsonLenient(String p_186877_0_) {
         return (ITextComponent)JSONUtils.fromJson(GSON, p_186877_0_, ITextComponent.class, true);
      }

      public static ITextComponent fromJson(com.mojang.brigadier.StringReader p_197671_0_) {
         try {
            JsonReader lvt_1_1_ = new JsonReader(new StringReader(p_197671_0_.getRemaining()));
            lvt_1_1_.setLenient(false);
            ITextComponent lvt_2_1_ = (ITextComponent)GSON.getAdapter(ITextComponent.class).read(lvt_1_1_);
            p_197671_0_.setCursor(p_197671_0_.getCursor() + getPos(lvt_1_1_));
            return lvt_2_1_;
         } catch (StackOverflowError | IOException var3) {
            throw new JsonParseException(var3);
         }
      }

      private static int getPos(JsonReader p_197673_0_) {
         try {
            return JSON_READER_POS_FIELD.getInt(p_197673_0_) - JSON_READER_LINESTART_FIELD.getInt(p_197673_0_) + 1;
         } catch (IllegalAccessException var2) {
            throw new IllegalStateException("Couldn't read position of JsonReader", var2);
         }
      }

      // $FF: synthetic method
      public JsonElement serialize(Object p_serialize_1_, Type p_serialize_2_, JsonSerializationContext p_serialize_3_) {
         return this.serialize((ITextComponent)p_serialize_1_, p_serialize_2_, p_serialize_3_);
      }

      // $FF: synthetic method
      public Object deserialize(JsonElement p_deserialize_1_, Type p_deserialize_2_, JsonDeserializationContext p_deserialize_3_) throws JsonParseException {
         return this.deserialize(p_deserialize_1_, p_deserialize_2_, p_deserialize_3_);
      }
   }
}
