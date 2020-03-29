package com.mojang.realmsclient.util;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Maps;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.util.UUIDTypeAdapter;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import net.minecraft.realms.Realms;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RealmsUtil {
   private static final YggdrasilAuthenticationService field_225195_b = new YggdrasilAuthenticationService(Realms.getProxy(), UUID.randomUUID().toString());
   private static final MinecraftSessionService field_225196_c;
   public static LoadingCache<String, GameProfile> field_225194_a;

   public static String func_225193_a(String p_225193_0_) throws Exception {
      GameProfile lvt_1_1_ = (GameProfile)field_225194_a.get(p_225193_0_);
      return lvt_1_1_.getName();
   }

   public static Map<Type, MinecraftProfileTexture> func_225191_b(String p_225191_0_) {
      try {
         GameProfile lvt_1_1_ = (GameProfile)field_225194_a.get(p_225191_0_);
         return field_225196_c.getTextures(lvt_1_1_, false);
      } catch (Exception var2) {
         return Maps.newHashMap();
      }
   }

   public static void func_225190_c(String p_225190_0_) {
      Realms.openUri(p_225190_0_);
   }

   public static String func_225192_a(Long p_225192_0_) {
      if (p_225192_0_ < 0L) {
         return "right now";
      } else {
         long lvt_1_1_ = p_225192_0_ / 1000L;
         if (lvt_1_1_ < 60L) {
            return (lvt_1_1_ == 1L ? "1 second" : lvt_1_1_ + " seconds") + " ago";
         } else {
            long lvt_3_3_;
            if (lvt_1_1_ < 3600L) {
               lvt_3_3_ = lvt_1_1_ / 60L;
               return (lvt_3_3_ == 1L ? "1 minute" : lvt_3_3_ + " minutes") + " ago";
            } else if (lvt_1_1_ < 86400L) {
               lvt_3_3_ = lvt_1_1_ / 3600L;
               return (lvt_3_3_ == 1L ? "1 hour" : lvt_3_3_ + " hours") + " ago";
            } else {
               lvt_3_3_ = lvt_1_1_ / 86400L;
               return (lvt_3_3_ == 1L ? "1 day" : lvt_3_3_ + " days") + " ago";
            }
         }
      }
   }

   static {
      field_225196_c = field_225195_b.createMinecraftSessionService();
      field_225194_a = CacheBuilder.newBuilder().expireAfterWrite(60L, TimeUnit.MINUTES).build(new CacheLoader<String, GameProfile>() {
         public GameProfile load(String p_load_1_) throws Exception {
            GameProfile lvt_2_1_ = RealmsUtil.field_225196_c.fillProfileProperties(new GameProfile(UUIDTypeAdapter.fromString(p_load_1_), (String)null), false);
            if (lvt_2_1_ == null) {
               throw new Exception("Couldn't get profile");
            } else {
               return lvt_2_1_;
            }
         }

         // $FF: synthetic method
         public Object load(Object p_load_1_) throws Exception {
            return this.load((String)p_load_1_);
         }
      });
   }
}
