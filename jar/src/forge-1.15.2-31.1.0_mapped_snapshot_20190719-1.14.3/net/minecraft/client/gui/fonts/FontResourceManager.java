package net.minecraft.client.gui.fonts;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.fonts.providers.DefaultGlyphProvider;
import net.minecraft.client.gui.fonts.providers.GlyphProviderTypes;
import net.minecraft.client.gui.fonts.providers.IGlyphProvider;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.ReloadListener;
import net.minecraft.profiler.EmptyProfiler;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IFutureReloadListener;
import net.minecraft.resources.IResource;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class FontResourceManager implements AutoCloseable {
   private static final Logger LOGGER = LogManager.getLogger();
   private final Map<ResourceLocation, FontRenderer> fontRenderers = Maps.newHashMap();
   private final TextureManager textureManager;
   private boolean forceUnicodeFont;
   private final IFutureReloadListener field_216889_f = new ReloadListener<Map<ResourceLocation, List<IGlyphProvider>>>() {
      protected Map<ResourceLocation, List<IGlyphProvider>> prepare(IResourceManager p_212854_1_, IProfiler p_212854_2_) {
         p_212854_2_.startTick();
         Gson lvt_3_1_ = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();
         Map<ResourceLocation, List<IGlyphProvider>> lvt_4_1_ = Maps.newHashMap();
         Iterator var5 = p_212854_1_.getAllResourceLocations("font", (p_215274_0_) -> {
            return p_215274_0_.endsWith(".json");
         }).iterator();

         while(var5.hasNext()) {
            ResourceLocation lvt_6_1_ = (ResourceLocation)var5.next();
            String lvt_7_1_ = lvt_6_1_.getPath();
            ResourceLocation lvt_8_1_ = new ResourceLocation(lvt_6_1_.getNamespace(), lvt_7_1_.substring("font/".length(), lvt_7_1_.length() - ".json".length()));
            List<IGlyphProvider> lvt_9_1_ = (List)lvt_4_1_.computeIfAbsent(lvt_8_1_, (p_215272_0_) -> {
               return Lists.newArrayList(new IGlyphProvider[]{new DefaultGlyphProvider()});
            });
            p_212854_2_.startSection(lvt_8_1_::toString);

            try {
               for(Iterator var10 = p_212854_1_.getAllResources(lvt_6_1_).iterator(); var10.hasNext(); p_212854_2_.endSection()) {
                  IResource lvt_11_1_ = (IResource)var10.next();
                  p_212854_2_.startSection(lvt_11_1_::getPackName);

                  try {
                     InputStream lvt_12_1_ = lvt_11_1_.getInputStream();
                     Throwable var13 = null;

                     try {
                        Reader lvt_14_1_ = new BufferedReader(new InputStreamReader(lvt_12_1_, StandardCharsets.UTF_8));
                        Throwable var15 = null;

                        try {
                           p_212854_2_.startSection("reading");
                           JsonArray lvt_16_1_ = JSONUtils.getJsonArray((JsonObject)JSONUtils.fromJson(lvt_3_1_, (Reader)lvt_14_1_, (Class)JsonObject.class), "providers");
                           p_212854_2_.endStartSection("parsing");

                           for(int lvt_17_1_ = lvt_16_1_.size() - 1; lvt_17_1_ >= 0; --lvt_17_1_) {
                              JsonObject lvt_18_1_ = JSONUtils.getJsonObject(lvt_16_1_.get(lvt_17_1_), "providers[" + lvt_17_1_ + "]");

                              try {
                                 String lvt_19_1_ = JSONUtils.getString(lvt_18_1_, "type");
                                 GlyphProviderTypes lvt_20_1_ = GlyphProviderTypes.byName(lvt_19_1_);
                                 if (!FontResourceManager.this.forceUnicodeFont || lvt_20_1_ == GlyphProviderTypes.LEGACY_UNICODE || !lvt_8_1_.equals(Minecraft.DEFAULT_FONT_RENDERER_NAME)) {
                                    p_212854_2_.startSection(lvt_19_1_);
                                    lvt_9_1_.add(lvt_20_1_.getFactory(lvt_18_1_).create(p_212854_1_));
                                    p_212854_2_.endSection();
                                 }
                              } catch (RuntimeException var48) {
                                 FontResourceManager.LOGGER.warn("Unable to read definition '{}' in fonts.json in resourcepack: '{}': {}", lvt_8_1_, lvt_11_1_.getPackName(), var48.getMessage());
                              }
                           }

                           p_212854_2_.endSection();
                        } catch (Throwable var49) {
                           var15 = var49;
                           throw var49;
                        } finally {
                           if (lvt_14_1_ != null) {
                              if (var15 != null) {
                                 try {
                                    lvt_14_1_.close();
                                 } catch (Throwable var47) {
                                    var15.addSuppressed(var47);
                                 }
                              } else {
                                 lvt_14_1_.close();
                              }
                           }

                        }
                     } catch (Throwable var51) {
                        var13 = var51;
                        throw var51;
                     } finally {
                        if (lvt_12_1_ != null) {
                           if (var13 != null) {
                              try {
                                 lvt_12_1_.close();
                              } catch (Throwable var46) {
                                 var13.addSuppressed(var46);
                              }
                           } else {
                              lvt_12_1_.close();
                           }
                        }

                     }
                  } catch (RuntimeException var53) {
                     FontResourceManager.LOGGER.warn("Unable to load font '{}' in fonts.json in resourcepack: '{}': {}", lvt_8_1_, lvt_11_1_.getPackName(), var53.getMessage());
                  }
               }
            } catch (IOException var54) {
               FontResourceManager.LOGGER.warn("Unable to load font '{}' in fonts.json: {}", lvt_8_1_, var54.getMessage());
            }

            p_212854_2_.startSection("caching");

            for(char lvt_10_2_ = 0; lvt_10_2_ < '\uffff'; ++lvt_10_2_) {
               if (lvt_10_2_ != ' ') {
                  Iterator var56 = Lists.reverse(lvt_9_1_).iterator();

                  while(var56.hasNext()) {
                     IGlyphProvider lvt_12_3_ = (IGlyphProvider)var56.next();
                     if (lvt_12_3_.func_212248_a(lvt_10_2_) != null) {
                        break;
                     }
                  }
               }
            }

            p_212854_2_.endSection();
            p_212854_2_.endSection();
         }

         p_212854_2_.endTick();
         return lvt_4_1_;
      }

      protected void apply(Map<ResourceLocation, List<IGlyphProvider>> p_212853_1_, IResourceManager p_212853_2_, IProfiler p_212853_3_) {
         p_212853_3_.startTick();
         p_212853_3_.startSection("reloading");
         Stream.concat(FontResourceManager.this.fontRenderers.keySet().stream(), p_212853_1_.keySet().stream()).distinct().forEach((p_215271_2_) -> {
            List<IGlyphProvider> lvt_3_1_ = (List)p_212853_1_.getOrDefault(p_215271_2_, Collections.emptyList());
            Collections.reverse(lvt_3_1_);
            ((FontRenderer)FontResourceManager.this.fontRenderers.computeIfAbsent(p_215271_2_, (p_215273_1_) -> {
               return new FontRenderer(FontResourceManager.this.textureManager, new Font(FontResourceManager.this.textureManager, p_215273_1_));
            })).setGlyphProviders(lvt_3_1_);
         });
         p_212853_3_.endSection();
         p_212853_3_.endTick();
      }

      public String func_225594_i_() {
         return "FontManager";
      }

      // $FF: synthetic method
      protected Object prepare(IResourceManager p_212854_1_, IProfiler p_212854_2_) {
         return this.prepare(p_212854_1_, p_212854_2_);
      }
   };

   public FontResourceManager(TextureManager p_i49787_1_, boolean p_i49787_2_) {
      this.textureManager = p_i49787_1_;
      this.forceUnicodeFont = p_i49787_2_;
   }

   @Nullable
   public FontRenderer getFontRenderer(ResourceLocation p_211504_1_) {
      return (FontRenderer)this.fontRenderers.computeIfAbsent(p_211504_1_, (p_212318_1_) -> {
         FontRenderer lvt_2_1_ = new FontRenderer(this.textureManager, new Font(this.textureManager, p_212318_1_));
         lvt_2_1_.setGlyphProviders(Lists.newArrayList(new IGlyphProvider[]{new DefaultGlyphProvider()}));
         return lvt_2_1_;
      });
   }

   public void func_216883_a(boolean p_216883_1_, Executor p_216883_2_, Executor p_216883_3_) {
      if (p_216883_1_ != this.forceUnicodeFont) {
         this.forceUnicodeFont = p_216883_1_;
         IResourceManager lvt_4_1_ = Minecraft.getInstance().getResourceManager();
         IFutureReloadListener.IStage lvt_5_1_ = new IFutureReloadListener.IStage() {
            public <T> CompletableFuture<T> markCompleteAwaitingOthers(T p_216872_1_) {
               return CompletableFuture.completedFuture(p_216872_1_);
            }
         };
         this.field_216889_f.reload(lvt_5_1_, lvt_4_1_, EmptyProfiler.INSTANCE, EmptyProfiler.INSTANCE, p_216883_2_, p_216883_3_);
      }
   }

   public IFutureReloadListener func_216884_a() {
      return this.field_216889_f;
   }

   public void close() {
      this.fontRenderers.values().forEach(FontRenderer::close);
   }
}
