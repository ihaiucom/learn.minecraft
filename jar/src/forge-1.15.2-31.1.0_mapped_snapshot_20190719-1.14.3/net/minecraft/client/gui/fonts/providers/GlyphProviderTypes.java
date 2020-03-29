package net.minecraft.client.gui.fonts.providers;

import com.google.common.collect.Maps;
import com.google.gson.JsonObject;
import java.util.Map;
import java.util.function.Function;
import net.minecraft.util.Util;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public enum GlyphProviderTypes {
   BITMAP("bitmap", TextureGlyphProvider.Factory::deserialize),
   TTF("ttf", TrueTypeGlyphProviderFactory::deserialize),
   LEGACY_UNICODE("legacy_unicode", UnicodeTextureGlyphProvider.Factory::deserialize);

   private static final Map<String, GlyphProviderTypes> TYPES_BY_NAME = (Map)Util.make(Maps.newHashMap(), (p_211639_0_) -> {
      GlyphProviderTypes[] var1 = values();
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         GlyphProviderTypes lvt_4_1_ = var1[var3];
         p_211639_0_.put(lvt_4_1_.name, lvt_4_1_);
      }

   });
   private final String name;
   private final Function<JsonObject, IGlyphProviderFactory> factoryDeserializer;

   private GlyphProviderTypes(String p_i49766_3_, Function<JsonObject, IGlyphProviderFactory> p_i49766_4_) {
      this.name = p_i49766_3_;
      this.factoryDeserializer = p_i49766_4_;
   }

   public static GlyphProviderTypes byName(String p_211638_0_) {
      GlyphProviderTypes lvt_1_1_ = (GlyphProviderTypes)TYPES_BY_NAME.get(p_211638_0_);
      if (lvt_1_1_ == null) {
         throw new IllegalArgumentException("Invalid type: " + p_211638_0_);
      } else {
         return lvt_1_1_;
      }
   }

   public IGlyphProviderFactory getFactory(JsonObject p_211637_1_) {
      return (IGlyphProviderFactory)this.factoryDeserializer.apply(p_211637_1_);
   }
}
