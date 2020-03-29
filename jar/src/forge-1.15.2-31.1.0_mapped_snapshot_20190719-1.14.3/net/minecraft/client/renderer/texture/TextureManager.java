package net.minecraft.client.renderer.texture;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.realmsclient.RealmsMainScreen;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.MainMenuScreen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ReportedException;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IFutureReloadListener;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class TextureManager implements ITickable, AutoCloseable, IFutureReloadListener {
   private static final Logger LOGGER = LogManager.getLogger();
   public static final ResourceLocation RESOURCE_LOCATION_EMPTY = new ResourceLocation("");
   private final Map<ResourceLocation, Texture> mapTextureObjects = Maps.newHashMap();
   private final Set<ITickable> listTickables = Sets.newHashSet();
   private final Map<String, Integer> mapTextureCounters = Maps.newHashMap();
   private final IResourceManager resourceManager;

   public TextureManager(IResourceManager p_i1284_1_) {
      this.resourceManager = p_i1284_1_;
   }

   public void bindTexture(ResourceLocation p_110577_1_) {
      if (!RenderSystem.isOnRenderThread()) {
         RenderSystem.recordRenderCall(() -> {
            this.func_229269_d_(p_110577_1_);
         });
      } else {
         this.func_229269_d_(p_110577_1_);
      }

   }

   private void func_229269_d_(ResourceLocation p_229269_1_) {
      Texture texture = (Texture)this.mapTextureObjects.get(p_229269_1_);
      if (texture == null) {
         texture = new SimpleTexture(p_229269_1_);
         this.func_229263_a_(p_229269_1_, (Texture)texture);
      }

      ((Texture)texture).func_229148_d_();
   }

   public void func_229263_a_(ResourceLocation p_229263_1_, Texture p_229263_2_) {
      p_229263_2_ = this.func_230183_b_(p_229263_1_, p_229263_2_);
      Texture texture = (Texture)this.mapTextureObjects.put(p_229263_1_, p_229263_2_);
      if (texture != p_229263_2_) {
         if (texture != null && texture != MissingTextureSprite.getDynamicTexture()) {
            texture.deleteGlTexture();
            this.listTickables.remove(texture);
         }

         if (p_229263_2_ instanceof ITickable) {
            this.listTickables.add((ITickable)p_229263_2_);
         }
      }

   }

   private Texture func_230183_b_(ResourceLocation p_230183_1_, Texture p_230183_2_) {
      try {
         p_230183_2_.loadTexture(this.resourceManager);
         return p_230183_2_;
      } catch (IOException var6) {
         if (p_230183_1_ != RESOURCE_LOCATION_EMPTY) {
            LOGGER.warn("Failed to load texture: {}", p_230183_1_, var6);
         }

         return MissingTextureSprite.getDynamicTexture();
      } catch (Throwable var7) {
         CrashReport crashreport = CrashReport.makeCrashReport(var7, "Registering texture");
         CrashReportCategory crashreportcategory = crashreport.makeCategory("Resource location being registered");
         crashreportcategory.addDetail("Resource location", (Object)p_230183_1_);
         crashreportcategory.addDetail("Texture object class", () -> {
            return p_230183_2_.getClass().getName();
         });
         throw new ReportedException(crashreport);
      }
   }

   @Nullable
   public Texture func_229267_b_(ResourceLocation p_229267_1_) {
      return (Texture)this.mapTextureObjects.get(p_229267_1_);
   }

   public ResourceLocation getDynamicTextureLocation(String p_110578_1_, DynamicTexture p_110578_2_) {
      Integer integer = (Integer)this.mapTextureCounters.get(p_110578_1_);
      if (integer == null) {
         integer = 1;
      } else {
         integer = integer + 1;
      }

      this.mapTextureCounters.put(p_110578_1_, integer);
      ResourceLocation resourcelocation = new ResourceLocation(String.format("dynamic/%s_%d", p_110578_1_, integer));
      this.func_229263_a_(resourcelocation, p_110578_2_);
      return resourcelocation;
   }

   public CompletableFuture<Void> loadAsync(ResourceLocation p_215268_1_, Executor p_215268_2_) {
      if (!this.mapTextureObjects.containsKey(p_215268_1_)) {
         PreloadedTexture preloadedtexture = new PreloadedTexture(this.resourceManager, p_215268_1_, p_215268_2_);
         this.mapTextureObjects.put(p_215268_1_, preloadedtexture);
         return preloadedtexture.func_215248_a().thenRunAsync(() -> {
            this.func_229263_a_(p_215268_1_, preloadedtexture);
         }, TextureManager::func_229262_a_);
      } else {
         return CompletableFuture.completedFuture((Void)null);
      }
   }

   private static void func_229262_a_(Runnable p_229262_0_) {
      Minecraft.getInstance().execute(() -> {
         RenderSystem.recordRenderCall(p_229262_0_::run);
      });
   }

   public void tick() {
      Iterator var1 = this.listTickables.iterator();

      while(var1.hasNext()) {
         ITickable itickable = (ITickable)var1.next();
         itickable.tick();
      }

   }

   public void deleteTexture(ResourceLocation p_147645_1_) {
      Texture texture = this.func_229267_b_(p_147645_1_);
      if (texture != null) {
         this.mapTextureObjects.remove(p_147645_1_);
         TextureUtil.func_225679_a_(texture.getGlTextureId());
      }

   }

   public void close() {
      this.mapTextureObjects.values().forEach(Texture::deleteGlTexture);
      this.mapTextureObjects.clear();
      this.listTickables.clear();
      this.mapTextureCounters.clear();
   }

   public CompletableFuture<Void> reload(IFutureReloadListener.IStage p_215226_1_, IResourceManager p_215226_2_, IProfiler p_215226_3_, IProfiler p_215226_4_, Executor p_215226_5_, Executor p_215226_6_) {
      CompletableFuture var10000 = CompletableFuture.allOf(MainMenuScreen.loadAsync(this, p_215226_5_), this.loadAsync(Widget.WIDGETS_LOCATION, p_215226_5_));
      p_215226_1_.getClass();
      return var10000.thenCompose(p_215226_1_::markCompleteAwaitingOthers).thenAcceptAsync((p_lambda$reload$4_3_) -> {
         MissingTextureSprite.getDynamicTexture();
         RealmsMainScreen.func_227932_a_(this.resourceManager);
         Iterator iterator = this.mapTextureObjects.entrySet().iterator();

         while(true) {
            while(iterator.hasNext()) {
               Entry<ResourceLocation, Texture> entry = (Entry)iterator.next();
               ResourceLocation resourcelocation = (ResourceLocation)entry.getKey();
               Texture texture = (Texture)entry.getValue();
               if (texture == MissingTextureSprite.getDynamicTexture() && !resourcelocation.equals(MissingTextureSprite.getLocation())) {
                  iterator.remove();
               } else {
                  texture.func_215244_a(this, p_215226_2_, resourcelocation, p_215226_6_);
               }
            }

            return;
         }
      }, (p_lambda$reload$5_0_) -> {
         RenderSystem.recordRenderCall(p_lambda$reload$5_0_::run);
      });
   }
}
