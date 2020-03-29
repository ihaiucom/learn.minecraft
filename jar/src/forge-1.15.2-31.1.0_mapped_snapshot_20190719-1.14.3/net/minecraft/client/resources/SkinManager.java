package net.minecraft.client.resources;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.google.common.hash.Hashing;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.InsecureTextureException;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;
import com.mojang.blaze3d.systems.RenderSystem;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DownloadingTexture;
import net.minecraft.client.renderer.texture.Texture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SkinManager {
   private final TextureManager textureManager;
   private final File skinCacheDir;
   private final MinecraftSessionService sessionService;
   private final LoadingCache<GameProfile, Map<Type, MinecraftProfileTexture>> skinCacheLoader;

   public SkinManager(TextureManager p_i1044_1_, File p_i1044_2_, MinecraftSessionService p_i1044_3_) {
      this.textureManager = p_i1044_1_;
      this.skinCacheDir = p_i1044_2_;
      this.sessionService = p_i1044_3_;
      this.skinCacheLoader = CacheBuilder.newBuilder().expireAfterAccess(15L, TimeUnit.SECONDS).build(new CacheLoader<GameProfile, Map<Type, MinecraftProfileTexture>>() {
         public Map<Type, MinecraftProfileTexture> load(GameProfile p_load_1_) throws Exception {
            try {
               return Minecraft.getInstance().getSessionService().getTextures(p_load_1_, false);
            } catch (Throwable var3) {
               return Maps.newHashMap();
            }
         }

         // $FF: synthetic method
         public Object load(Object p_load_1_) throws Exception {
            return this.load((GameProfile)p_load_1_);
         }
      });
   }

   public ResourceLocation loadSkin(MinecraftProfileTexture p_152792_1_, Type p_152792_2_) {
      return this.loadSkin(p_152792_1_, p_152792_2_, (SkinManager.ISkinAvailableCallback)null);
   }

   public ResourceLocation loadSkin(MinecraftProfileTexture p_152789_1_, Type p_152789_2_, @Nullable SkinManager.ISkinAvailableCallback p_152789_3_) {
      String lvt_4_1_ = Hashing.sha1().hashUnencodedChars(p_152789_1_.getHash()).toString();
      ResourceLocation lvt_5_1_ = new ResourceLocation("skins/" + lvt_4_1_);
      Texture lvt_6_1_ = this.textureManager.func_229267_b_(lvt_5_1_);
      if (lvt_6_1_ != null) {
         if (p_152789_3_ != null) {
            p_152789_3_.onSkinTextureAvailable(p_152789_2_, lvt_5_1_, p_152789_1_);
         }
      } else {
         File lvt_7_1_ = new File(this.skinCacheDir, lvt_4_1_.length() > 2 ? lvt_4_1_.substring(0, 2) : "xx");
         File lvt_8_1_ = new File(lvt_7_1_, lvt_4_1_);
         DownloadingTexture lvt_9_1_ = new DownloadingTexture(lvt_8_1_, p_152789_1_.getUrl(), DefaultPlayerSkin.getDefaultSkinLegacy(), p_152789_2_ == Type.SKIN, () -> {
            if (p_152789_3_ != null) {
               p_152789_3_.onSkinTextureAvailable(p_152789_2_, lvt_5_1_, p_152789_1_);
            }

         });
         this.textureManager.func_229263_a_(lvt_5_1_, lvt_9_1_);
      }

      return lvt_5_1_;
   }

   public void loadProfileTextures(GameProfile p_152790_1_, SkinManager.ISkinAvailableCallback p_152790_2_, boolean p_152790_3_) {
      Runnable lvt_4_1_ = () -> {
         HashMap lvt_4_1_ = Maps.newHashMap();

         try {
            lvt_4_1_.putAll(this.sessionService.getTextures(p_152790_1_, p_152790_3_));
         } catch (InsecureTextureException var7) {
         }

         if (lvt_4_1_.isEmpty()) {
            p_152790_1_.getProperties().clear();
            if (p_152790_1_.getId().equals(Minecraft.getInstance().getSession().getProfile().getId())) {
               p_152790_1_.getProperties().putAll(Minecraft.getInstance().getProfileProperties());
               lvt_4_1_.putAll(this.sessionService.getTextures(p_152790_1_, false));
            } else {
               this.sessionService.fillProfileProperties(p_152790_1_, p_152790_3_);

               try {
                  lvt_4_1_.putAll(this.sessionService.getTextures(p_152790_1_, p_152790_3_));
               } catch (InsecureTextureException var6) {
               }
            }
         }

         Minecraft.getInstance().execute(() -> {
            RenderSystem.recordRenderCall(() -> {
               ImmutableList.of(Type.SKIN, Type.CAPE).forEach((p_229296_3_) -> {
                  if (lvt_4_1_.containsKey(p_229296_3_)) {
                     this.loadSkin((MinecraftProfileTexture)lvt_4_1_.get(p_229296_3_), p_229296_3_, p_152790_2_);
                  }

               });
            });
         });
      };
      Util.getServerExecutor().execute(lvt_4_1_);
   }

   public Map<Type, MinecraftProfileTexture> loadSkinFromCache(GameProfile p_152788_1_) {
      return (Map)this.skinCacheLoader.getUnchecked(p_152788_1_);
   }

   @OnlyIn(Dist.CLIENT)
   public interface ISkinAvailableCallback {
      void onSkinTextureAvailable(Type var1, ResourceLocation var2, MinecraftProfileTexture var3);
   }
}
