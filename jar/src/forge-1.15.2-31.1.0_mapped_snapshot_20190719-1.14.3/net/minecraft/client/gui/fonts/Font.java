package net.minecraft.client.gui.fonts;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import it.unimi.dsi.fastutil.chars.Char2ObjectMap;
import it.unimi.dsi.fastutil.chars.Char2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.chars.CharArrayList;
import it.unimi.dsi.fastutil.chars.CharList;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Stream;
import net.minecraft.client.gui.fonts.providers.IGlyphProvider;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class Font implements AutoCloseable {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final EmptyGlyph field_212460_b = new EmptyGlyph();
   private static final IGlyph field_212461_c = () -> {
      return 4.0F;
   };
   private static final Random RANDOM = new Random();
   private final TextureManager textureManager;
   private final ResourceLocation id;
   private TexturedGlyph fallbackGlyph;
   private TexturedGlyph field_228156_h_;
   private final List<IGlyphProvider> glyphProviders = Lists.newArrayList();
   private final Char2ObjectMap<TexturedGlyph> field_212463_j = new Char2ObjectOpenHashMap();
   private final Char2ObjectMap<IGlyph> glyphs = new Char2ObjectOpenHashMap();
   private final Int2ObjectMap<CharList> glyphsByWidth = new Int2ObjectOpenHashMap();
   private final List<FontTexture> textures = Lists.newArrayList();

   public Font(TextureManager p_i49771_1_, ResourceLocation p_i49771_2_) {
      this.textureManager = p_i49771_1_;
      this.id = p_i49771_2_;
   }

   public void setGlyphProviders(List<IGlyphProvider> p_211570_1_) {
      this.func_230154_b_();
      this.deleteTextures();
      this.field_212463_j.clear();
      this.glyphs.clear();
      this.glyphsByWidth.clear();
      this.fallbackGlyph = this.createTexturedGlyph(DefaultGlyph.INSTANCE);
      this.field_228156_h_ = this.createTexturedGlyph(WhiteGlyph.INSTANCE);
      Set<IGlyphProvider> lvt_2_1_ = Sets.newHashSet();

      for(char lvt_3_1_ = 0; lvt_3_1_ < '\uffff'; ++lvt_3_1_) {
         Iterator var4 = p_211570_1_.iterator();

         while(var4.hasNext()) {
            IGlyphProvider lvt_5_1_ = (IGlyphProvider)var4.next();
            IGlyph lvt_6_1_ = lvt_3_1_ == ' ' ? field_212461_c : lvt_5_1_.func_212248_a(lvt_3_1_);
            if (lvt_6_1_ != null) {
               lvt_2_1_.add(lvt_5_1_);
               if (lvt_6_1_ != DefaultGlyph.INSTANCE) {
                  ((CharList)this.glyphsByWidth.computeIfAbsent(MathHelper.ceil(((IGlyph)lvt_6_1_).getAdvance(false)), (p_212456_0_) -> {
                     return new CharArrayList();
                  })).add(lvt_3_1_);
               }
               break;
            }
         }
      }

      Stream var10000 = p_211570_1_.stream();
      lvt_2_1_.getClass();
      var10000 = var10000.filter(lvt_2_1_::contains);
      List var10001 = this.glyphProviders;
      var10000.forEach(var10001::add);
   }

   public void close() {
      this.func_230154_b_();
      this.deleteTextures();
   }

   private void func_230154_b_() {
      Iterator var1 = this.glyphProviders.iterator();

      while(var1.hasNext()) {
         IGlyphProvider lvt_2_1_ = (IGlyphProvider)var1.next();
         lvt_2_1_.close();
      }

      this.glyphProviders.clear();
   }

   private void deleteTextures() {
      Iterator var1 = this.textures.iterator();

      while(var1.hasNext()) {
         FontTexture lvt_2_1_ = (FontTexture)var1.next();
         lvt_2_1_.close();
      }

      this.textures.clear();
   }

   public IGlyph findGlyph(char p_211184_1_) {
      return (IGlyph)this.glyphs.computeIfAbsent(p_211184_1_, (p_212457_1_) -> {
         return (IGlyph)(p_212457_1_ == 32 ? field_212461_c : this.func_212455_c((char)p_212457_1_));
      });
   }

   private IGlyphInfo func_212455_c(char p_212455_1_) {
      Iterator var2 = this.glyphProviders.iterator();

      IGlyphInfo lvt_4_1_;
      do {
         if (!var2.hasNext()) {
            return DefaultGlyph.INSTANCE;
         }

         IGlyphProvider lvt_3_1_ = (IGlyphProvider)var2.next();
         lvt_4_1_ = lvt_3_1_.func_212248_a(p_212455_1_);
      } while(lvt_4_1_ == null);

      return lvt_4_1_;
   }

   public TexturedGlyph getGlyph(char p_211187_1_) {
      return (TexturedGlyph)this.field_212463_j.computeIfAbsent(p_211187_1_, (p_212458_1_) -> {
         return (TexturedGlyph)(p_212458_1_ == 32 ? field_212460_b : this.createTexturedGlyph(this.func_212455_c((char)p_212458_1_)));
      });
   }

   private TexturedGlyph createTexturedGlyph(IGlyphInfo p_211185_1_) {
      Iterator var2 = this.textures.iterator();

      TexturedGlyph lvt_4_1_;
      do {
         if (!var2.hasNext()) {
            FontTexture lvt_2_1_ = new FontTexture(new ResourceLocation(this.id.getNamespace(), this.id.getPath() + "/" + this.textures.size()), p_211185_1_.isColored());
            this.textures.add(lvt_2_1_);
            this.textureManager.func_229263_a_(lvt_2_1_.getTextureLocation(), lvt_2_1_);
            TexturedGlyph lvt_3_2_ = lvt_2_1_.createTexturedGlyph(p_211185_1_);
            return lvt_3_2_ == null ? this.fallbackGlyph : lvt_3_2_;
         }

         FontTexture lvt_3_1_ = (FontTexture)var2.next();
         lvt_4_1_ = lvt_3_1_.createTexturedGlyph(p_211185_1_);
      } while(lvt_4_1_ == null);

      return lvt_4_1_;
   }

   public TexturedGlyph obfuscate(IGlyph p_211188_1_) {
      CharList lvt_2_1_ = (CharList)this.glyphsByWidth.get(MathHelper.ceil(p_211188_1_.getAdvance(false)));
      return lvt_2_1_ != null && !lvt_2_1_.isEmpty() ? this.getGlyph(lvt_2_1_.get(RANDOM.nextInt(lvt_2_1_.size()))) : this.fallbackGlyph;
   }

   public TexturedGlyph func_228157_b_() {
      return this.field_228156_h_;
   }
}
