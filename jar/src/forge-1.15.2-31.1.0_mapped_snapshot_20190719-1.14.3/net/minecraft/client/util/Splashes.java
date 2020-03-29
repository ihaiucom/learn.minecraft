package net.minecraft.client.util;

import com.google.common.collect.Lists;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.ReloadListener;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IResource;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Session;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class Splashes extends ReloadListener<List<String>> {
   private static final ResourceLocation SPLASHES_LOCATION = new ResourceLocation("texts/splashes.txt");
   private static final Random RANDOM = new Random();
   private final List<String> possibleSplashes = Lists.newArrayList();
   private final Session gameSession;

   public Splashes(Session p_i50906_1_) {
      this.gameSession = p_i50906_1_;
   }

   protected List<String> prepare(IResourceManager p_212854_1_, IProfiler p_212854_2_) {
      try {
         IResource lvt_3_1_ = Minecraft.getInstance().getResourceManager().getResource(SPLASHES_LOCATION);
         Throwable var4 = null;

         Object var7;
         try {
            BufferedReader lvt_5_1_ = new BufferedReader(new InputStreamReader(lvt_3_1_.getInputStream(), StandardCharsets.UTF_8));
            Throwable var6 = null;

            try {
               var7 = (List)lvt_5_1_.lines().map(String::trim).filter((p_215277_0_) -> {
                  return p_215277_0_.hashCode() != 125780783;
               }).collect(Collectors.toList());
            } catch (Throwable var32) {
               var7 = var32;
               var6 = var32;
               throw var32;
            } finally {
               if (lvt_5_1_ != null) {
                  if (var6 != null) {
                     try {
                        lvt_5_1_.close();
                     } catch (Throwable var31) {
                        var6.addSuppressed(var31);
                     }
                  } else {
                     lvt_5_1_.close();
                  }
               }

            }
         } catch (Throwable var34) {
            var4 = var34;
            throw var34;
         } finally {
            if (lvt_3_1_ != null) {
               if (var4 != null) {
                  try {
                     lvt_3_1_.close();
                  } catch (Throwable var30) {
                     var4.addSuppressed(var30);
                  }
               } else {
                  lvt_3_1_.close();
               }
            }

         }

         return (List)var7;
      } catch (IOException var36) {
         return Collections.emptyList();
      }
   }

   protected void apply(List<String> p_212853_1_, IResourceManager p_212853_2_, IProfiler p_212853_3_) {
      this.possibleSplashes.clear();
      this.possibleSplashes.addAll(p_212853_1_);
   }

   @Nullable
   public String getSplashText() {
      Calendar lvt_1_1_ = Calendar.getInstance();
      lvt_1_1_.setTime(new Date());
      if (lvt_1_1_.get(2) + 1 == 12 && lvt_1_1_.get(5) == 24) {
         return "Merry X-mas!";
      } else if (lvt_1_1_.get(2) + 1 == 1 && lvt_1_1_.get(5) == 1) {
         return "Happy new year!";
      } else if (lvt_1_1_.get(2) + 1 == 10 && lvt_1_1_.get(5) == 31) {
         return "OOoooOOOoooo! Spooky!";
      } else if (this.possibleSplashes.isEmpty()) {
         return null;
      } else {
         return this.gameSession != null && RANDOM.nextInt(this.possibleSplashes.size()) == 42 ? this.gameSession.getUsername().toUpperCase(Locale.ROOT) + " IS YOU" : (String)this.possibleSplashes.get(RANDOM.nextInt(this.possibleSplashes.size()));
      }
   }

   // $FF: synthetic method
   protected Object prepare(IResourceManager p_212854_1_, IProfiler p_212854_2_) {
      return this.prepare(p_212854_1_, p_212854_2_);
   }
}
