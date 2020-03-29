package net.minecraft.client.renderer.texture;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.datafixers.util.Pair;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.StitcherException;
import net.minecraft.client.resources.data.AnimationMetadataSection;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ReportedException;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IResource;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.ForgeHooksClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class AtlasTexture extends Texture implements ITickable {
   private static final Logger LOGGER = LogManager.getLogger();
   /** @deprecated */
   @Deprecated
   public static final ResourceLocation LOCATION_BLOCKS_TEXTURE;
   /** @deprecated */
   @Deprecated
   public static final ResourceLocation LOCATION_PARTICLES_TEXTURE;
   private final List<TextureAtlasSprite> listAnimatedSprites = Lists.newArrayList();
   private final Set<ResourceLocation> sprites = Sets.newHashSet();
   private final Map<ResourceLocation, TextureAtlasSprite> mapUploadedSprites = Maps.newHashMap();
   private final ResourceLocation field_229214_j_;
   private final int field_215265_o;

   public AtlasTexture(ResourceLocation p_i226047_1_) {
      this.field_229214_j_ = p_i226047_1_;
      this.field_215265_o = RenderSystem.maxSupportedTextureSize();
   }

   public void loadTexture(IResourceManager p_195413_1_) throws IOException {
   }

   public void upload(AtlasTexture.SheetData p_215260_1_) {
      this.sprites.clear();
      this.sprites.addAll(p_215260_1_.field_217805_a);
      LOGGER.info("Created: {}x{}x{} {}-atlas", p_215260_1_.width, p_215260_1_.height, p_215260_1_.field_229224_d_, this.field_229214_j_);
      TextureUtil.func_225681_a_(this.getGlTextureId(), p_215260_1_.field_229224_d_, p_215260_1_.width, p_215260_1_.height);
      this.clear();
      Iterator var2 = p_215260_1_.sprites.iterator();

      while(var2.hasNext()) {
         TextureAtlasSprite textureatlassprite = (TextureAtlasSprite)var2.next();
         this.mapUploadedSprites.put(textureatlassprite.getName(), textureatlassprite);

         try {
            textureatlassprite.uploadMipmaps();
         } catch (Throwable var7) {
            CrashReport crashreport = CrashReport.makeCrashReport(var7, "Stitching texture atlas");
            CrashReportCategory crashreportcategory = crashreport.makeCategory("Texture being stitched together");
            crashreportcategory.addDetail("Atlas path", (Object)this.field_229214_j_);
            crashreportcategory.addDetail("Sprite", (Object)textureatlassprite);
            throw new ReportedException(crashreport);
         }

         if (textureatlassprite.hasAnimationMetadata()) {
            this.listAnimatedSprites.add(textureatlassprite);
         }
      }

      ForgeHooksClient.onTextureStitchedPost(this);
   }

   public AtlasTexture.SheetData func_229220_a_(IResourceManager p_229220_1_, Stream<ResourceLocation> p_229220_2_, IProfiler p_229220_3_, int p_229220_4_) {
      p_229220_3_.startSection("preparing");
      Set<ResourceLocation> set = (Set)p_229220_2_.peek((p_lambda$func_229220_a_$0_0_) -> {
         if (p_lambda$func_229220_a_$0_0_ == null) {
            throw new IllegalArgumentException("Location cannot be null!");
         }
      }).collect(Collectors.toSet());
      int i = this.field_215265_o;
      Stitcher stitcher = new Stitcher(i, i, p_229220_4_);
      int j = Integer.MAX_VALUE;
      int k = 1 << p_229220_4_;
      p_229220_3_.endStartSection("extracting_frames");
      ForgeHooksClient.onTextureStitchedPre(this, set);

      TextureAtlasSprite.Info textureatlassprite$info;
      for(Iterator var10 = this.func_215256_a(p_229220_1_, set).iterator(); var10.hasNext(); stitcher.func_229211_a_(textureatlassprite$info)) {
         textureatlassprite$info = (TextureAtlasSprite.Info)var10.next();
         j = Math.min(j, Math.min(textureatlassprite$info.func_229250_b_(), textureatlassprite$info.func_229252_c_()));
         int l = Math.min(Integer.lowestOneBit(textureatlassprite$info.func_229250_b_()), Integer.lowestOneBit(textureatlassprite$info.func_229252_c_()));
         if (l < k) {
            LOGGER.warn("Texture {} with size {}x{} limits mip level from {} to {}", textureatlassprite$info.func_229248_a_(), textureatlassprite$info.func_229250_b_(), textureatlassprite$info.func_229252_c_(), MathHelper.log2(k), MathHelper.log2(l));
            k = l;
         }
      }

      int i1 = Math.min(j, k);
      int j1 = MathHelper.log2(i1);
      p_229220_3_.endStartSection("register");
      stitcher.func_229211_a_(MissingTextureSprite.func_229177_b_());
      p_229220_3_.endStartSection("stitching");

      try {
         stitcher.doStitch();
      } catch (StitcherException var16) {
         CrashReport crashreport = CrashReport.makeCrashReport(var16, "Stitching");
         CrashReportCategory crashreportcategory = crashreport.makeCategory("Stitcher");
         crashreportcategory.addDetail("Sprites", var16.func_225331_a().stream().map((p_lambda$func_229220_a_$1_0_) -> {
            return String.format("%s[%dx%d]", p_lambda$func_229220_a_$1_0_.func_229248_a_(), p_lambda$func_229220_a_$1_0_.func_229250_b_(), p_lambda$func_229220_a_$1_0_.func_229252_c_());
         }).collect(Collectors.joining(",")));
         crashreportcategory.addDetail("Max Texture Size", (Object)i);
         throw new ReportedException(crashreport);
      }

      p_229220_3_.endStartSection("loading");
      List<TextureAtlasSprite> list = this.func_229217_a_(p_229220_1_, stitcher, p_229220_4_);
      p_229220_3_.endSection();
      return new AtlasTexture.SheetData(set, stitcher.getCurrentWidth(), stitcher.getCurrentHeight(), p_229220_4_, list);
   }

   private Collection<TextureAtlasSprite.Info> func_215256_a(IResourceManager p_215256_1_, Set<ResourceLocation> p_215256_2_) {
      List<CompletableFuture<?>> list = Lists.newArrayList();
      ConcurrentLinkedQueue<TextureAtlasSprite.Info> concurrentlinkedqueue = new ConcurrentLinkedQueue();
      Iterator var5 = p_215256_2_.iterator();

      while(var5.hasNext()) {
         ResourceLocation resourcelocation = (ResourceLocation)var5.next();
         if (!MissingTextureSprite.getLocation().equals(resourcelocation)) {
            list.add(CompletableFuture.runAsync(() -> {
               ResourceLocation resourcelocation1 = this.getSpritePath(resourcelocation);

               TextureAtlasSprite.Info textureatlassprite$info;
               try {
                  IResource iresource = p_215256_1_.getResource(resourcelocation1);
                  Throwable var7 = null;

                  try {
                     PngSizeInfo pngsizeinfo = new PngSizeInfo(iresource.toString(), iresource.getInputStream());
                     AnimationMetadataSection animationmetadatasection = (AnimationMetadataSection)iresource.getMetadata(AnimationMetadataSection.SERIALIZER);
                     if (animationmetadatasection == null) {
                        animationmetadatasection = AnimationMetadataSection.field_229300_b_;
                     }

                     Pair<Integer, Integer> pair = animationmetadatasection.func_225641_a_(pngsizeinfo.width, pngsizeinfo.height);
                     textureatlassprite$info = new TextureAtlasSprite.Info(resourcelocation, (Integer)pair.getFirst(), (Integer)pair.getSecond(), animationmetadatasection);
                  } catch (Throwable var20) {
                     var7 = var20;
                     throw var20;
                  } finally {
                     if (iresource != null) {
                        if (var7 != null) {
                           try {
                              iresource.close();
                           } catch (Throwable var19) {
                              var7.addSuppressed(var19);
                           }
                        } else {
                           iresource.close();
                        }
                     }

                  }
               } catch (RuntimeException var22) {
                  LOGGER.error("Unable to parse metadata from {} : {}", resourcelocation1, var22);
                  return;
               } catch (IOException var23) {
                  LOGGER.error("Using missing texture, unable to load {} : {}", resourcelocation1, var23);
                  return;
               }

               concurrentlinkedqueue.add(textureatlassprite$info);
            }, Util.getServerExecutor()));
         }
      }

      CompletableFuture.allOf((CompletableFuture[])list.toArray(new CompletableFuture[0])).join();
      return concurrentlinkedqueue;
   }

   private List<TextureAtlasSprite> func_229217_a_(IResourceManager p_229217_1_, Stitcher p_229217_2_, int p_229217_3_) {
      ConcurrentLinkedQueue<TextureAtlasSprite> concurrentlinkedqueue = new ConcurrentLinkedQueue();
      List<CompletableFuture<?>> list = Lists.newArrayList();
      p_229217_2_.func_229209_a_((p_lambda$func_229217_a_$4_5_, p_lambda$func_229217_a_$4_6_, p_lambda$func_229217_a_$4_7_, p_lambda$func_229217_a_$4_8_, p_lambda$func_229217_a_$4_9_) -> {
         if (p_lambda$func_229217_a_$4_5_ == MissingTextureSprite.func_229177_b_()) {
            MissingTextureSprite missingtexturesprite = MissingTextureSprite.func_229176_a_(this, p_229217_3_, p_lambda$func_229217_a_$4_6_, p_lambda$func_229217_a_$4_7_, p_lambda$func_229217_a_$4_8_, p_lambda$func_229217_a_$4_9_);
            concurrentlinkedqueue.add(missingtexturesprite);
         } else {
            list.add(CompletableFuture.runAsync(() -> {
               TextureAtlasSprite textureatlassprite = this.func_229218_a_(p_229217_1_, p_lambda$func_229217_a_$4_5_, p_lambda$func_229217_a_$4_6_, p_lambda$func_229217_a_$4_7_, p_229217_3_, p_lambda$func_229217_a_$4_8_, p_lambda$func_229217_a_$4_9_);
               if (textureatlassprite != null) {
                  concurrentlinkedqueue.add(textureatlassprite);
               }

            }, Util.getServerExecutor()));
         }

      });
      CompletableFuture.allOf((CompletableFuture[])list.toArray(new CompletableFuture[0])).join();
      return Lists.newArrayList(concurrentlinkedqueue);
   }

   @Nullable
   private TextureAtlasSprite func_229218_a_(IResourceManager p_229218_1_, TextureAtlasSprite.Info p_229218_2_, int p_229218_3_, int p_229218_4_, int p_229218_5_, int p_229218_6_, int p_229218_7_) {
      ResourceLocation resourcelocation = this.getSpritePath(p_229218_2_.func_229248_a_());

      try {
         IResource iresource = p_229218_1_.getResource(resourcelocation);
         Throwable var10 = null;

         TextureAtlasSprite var13;
         try {
            NativeImage nativeimage = NativeImage.read(iresource.getInputStream());
            TextureAtlasSprite textureatlassprite = new TextureAtlasSprite(this, p_229218_2_, p_229218_5_, p_229218_3_, p_229218_4_, p_229218_6_, p_229218_7_, nativeimage);
            var13 = textureatlassprite;
         } catch (Throwable var24) {
            var10 = var24;
            throw var24;
         } finally {
            if (iresource != null) {
               if (var10 != null) {
                  try {
                     iresource.close();
                  } catch (Throwable var23) {
                     var10.addSuppressed(var23);
                  }
               } else {
                  iresource.close();
               }
            }

         }

         return var13;
      } catch (RuntimeException var26) {
         LOGGER.error("Unable to parse metadata from {}", resourcelocation, var26);
         return null;
      } catch (IOException var27) {
         LOGGER.error("Using missing texture, unable to load {}", resourcelocation, var27);
         return null;
      }
   }

   private ResourceLocation getSpritePath(ResourceLocation p_195420_1_) {
      return new ResourceLocation(p_195420_1_.getNamespace(), String.format("textures/%s%s", p_195420_1_.getPath(), ".png"));
   }

   public void updateAnimations() {
      this.func_229148_d_();
      Iterator var1 = this.listAnimatedSprites.iterator();

      while(var1.hasNext()) {
         TextureAtlasSprite textureatlassprite = (TextureAtlasSprite)var1.next();
         textureatlassprite.updateAnimation();
      }

   }

   public void tick() {
      if (!RenderSystem.isOnRenderThread()) {
         RenderSystem.recordRenderCall(this::updateAnimations);
      } else {
         this.updateAnimations();
      }

   }

   public TextureAtlasSprite getSprite(ResourceLocation p_195424_1_) {
      TextureAtlasSprite textureatlassprite = (TextureAtlasSprite)this.mapUploadedSprites.get(p_195424_1_);
      return textureatlassprite == null ? (TextureAtlasSprite)this.mapUploadedSprites.get(MissingTextureSprite.getLocation()) : textureatlassprite;
   }

   public void clear() {
      Iterator var1 = this.mapUploadedSprites.values().iterator();

      while(var1.hasNext()) {
         TextureAtlasSprite textureatlassprite = (TextureAtlasSprite)var1.next();
         textureatlassprite.close();
      }

      this.mapUploadedSprites.clear();
      this.listAnimatedSprites.clear();
   }

   public ResourceLocation func_229223_g_() {
      return this.field_229214_j_;
   }

   public void func_229221_b_(AtlasTexture.SheetData p_229221_1_) {
      this.setBlurMipmapDirect(false, p_229221_1_.field_229224_d_ > 0);
   }

   static {
      LOCATION_BLOCKS_TEXTURE = PlayerContainer.field_226615_c_;
      LOCATION_PARTICLES_TEXTURE = new ResourceLocation("textures/atlas/particles.png");
   }

   @OnlyIn(Dist.CLIENT)
   public static class SheetData {
      final Set<ResourceLocation> field_217805_a;
      final int width;
      final int height;
      final int field_229224_d_;
      final List<TextureAtlasSprite> sprites;

      public SheetData(Set<ResourceLocation> p_i226048_1_, int p_i226048_2_, int p_i226048_3_, int p_i226048_4_, List<TextureAtlasSprite> p_i226048_5_) {
         this.field_217805_a = p_i226048_1_;
         this.width = p_i226048_2_;
         this.height = p_i226048_3_;
         this.field_229224_d_ = p_i226048_4_;
         this.sprites = p_i226048_5_;
      }
   }
}
