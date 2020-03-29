package net.minecraft.client.audio;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.client.GameSettings;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.resources.ReloadListener;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IResource;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.resource.IResourceType;
import net.minecraftforge.resource.VanillaResourceType;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class SoundHandler extends ReloadListener<SoundHandler.Loader> {
   public static final Sound MISSING_SOUND;
   private static final Logger LOGGER;
   private static final Gson GSON;
   private static final ParameterizedType TYPE;
   private final Map<ResourceLocation, SoundEventAccessor> soundRegistry = Maps.newHashMap();
   private final SoundEngine sndManager;

   public SoundHandler(IResourceManager p_i45122_1_, GameSettings p_i45122_2_) {
      this.sndManager = new SoundEngine(this, p_i45122_2_, p_i45122_1_);
   }

   protected SoundHandler.Loader prepare(IResourceManager p_212854_1_, IProfiler p_212854_2_) {
      SoundHandler.Loader soundhandler$loader = new SoundHandler.Loader();
      p_212854_2_.startTick();

      for(Iterator var4 = p_212854_1_.getResourceNamespaces().iterator(); var4.hasNext(); p_212854_2_.endSection()) {
         String s = (String)var4.next();
         p_212854_2_.startSection(s);

         try {
            for(Iterator var6 = p_212854_1_.getAllResources(new ResourceLocation(s, "sounds.json")).iterator(); var6.hasNext(); p_212854_2_.endSection()) {
               IResource iresource = (IResource)var6.next();
               p_212854_2_.startSection(iresource.getPackName());

               try {
                  p_212854_2_.startSection("parse");
                  Map<String, SoundList> map = getSoundMap(iresource.getInputStream());
                  p_212854_2_.endStartSection("register");
                  Iterator var9 = map.entrySet().iterator();

                  while(var9.hasNext()) {
                     Entry<String, SoundList> entry = (Entry)var9.next();
                     soundhandler$loader.func_217944_a(new ResourceLocation(s, (String)entry.getKey()), (SoundList)entry.getValue(), p_212854_1_);
                  }

                  p_212854_2_.endSection();
               } catch (RuntimeException var11) {
                  LOGGER.warn("Invalid sounds.json in resourcepack: '{}'", iresource.getPackName(), var11);
               }
            }
         } catch (IOException var12) {
         }
      }

      p_212854_2_.endTick();
      return soundhandler$loader;
   }

   protected void apply(SoundHandler.Loader p_212853_1_, IResourceManager p_212853_2_, IProfiler p_212853_3_) {
      p_212853_1_.func_217946_a(this.soundRegistry, this.sndManager);
      Iterator var4 = this.soundRegistry.keySet().iterator();

      ResourceLocation resourcelocation1;
      while(var4.hasNext()) {
         resourcelocation1 = (ResourceLocation)var4.next();
         SoundEventAccessor soundeventaccessor = (SoundEventAccessor)this.soundRegistry.get(resourcelocation1);
         if (soundeventaccessor.getSubtitle() instanceof TranslationTextComponent) {
            String s = ((TranslationTextComponent)soundeventaccessor.getSubtitle()).getKey();
            if (!I18n.hasKey(s)) {
               LOGGER.debug("Missing subtitle {} for event: {}", s, resourcelocation1);
            }
         }
      }

      if (LOGGER.isDebugEnabled()) {
         var4 = this.soundRegistry.keySet().iterator();

         while(var4.hasNext()) {
            resourcelocation1 = (ResourceLocation)var4.next();
            if (!Registry.SOUND_EVENT.containsKey(resourcelocation1)) {
               LOGGER.debug("Not having sound event for: {}", resourcelocation1);
            }
         }
      }

      this.sndManager.reload();
   }

   @Nullable
   protected static Map<String, SoundList> getSoundMap(InputStream p_175085_0_) {
      Map map;
      try {
         map = (Map)JSONUtils.fromJson(GSON, (Reader)(new InputStreamReader(p_175085_0_, StandardCharsets.UTF_8)), (Type)TYPE);
      } finally {
         IOUtils.closeQuietly(p_175085_0_);
      }

      return map;
   }

   private static boolean func_215292_b(Sound p_215292_0_, ResourceLocation p_215292_1_, IResourceManager p_215292_2_) {
      ResourceLocation resourcelocation = p_215292_0_.getSoundAsOggLocation();
      if (!p_215292_2_.hasResource(resourcelocation)) {
         LOGGER.warn("File {} does not exist, cannot add it to event {}", resourcelocation, p_215292_1_);
         return false;
      } else {
         return true;
      }
   }

   @Nullable
   public SoundEventAccessor getAccessor(ResourceLocation p_184398_1_) {
      return (SoundEventAccessor)this.soundRegistry.get(p_184398_1_);
   }

   public Collection<ResourceLocation> getAvailableSounds() {
      return this.soundRegistry.keySet();
   }

   public void func_229364_a_(ITickableSound p_229364_1_) {
      this.sndManager.func_229363_a_(p_229364_1_);
   }

   public void play(ISound p_147682_1_) {
      this.sndManager.play(p_147682_1_);
   }

   public void playDelayed(ISound p_147681_1_, int p_147681_2_) {
      this.sndManager.playDelayed(p_147681_1_, p_147681_2_);
   }

   public void updateListener(ActiveRenderInfo p_215289_1_) {
      this.sndManager.updateListener(p_215289_1_);
   }

   public void pause() {
      this.sndManager.pause();
   }

   public void stop() {
      this.sndManager.stopAllSounds();
   }

   public void unloadSounds() {
      this.sndManager.unload();
   }

   public void tick(boolean p_215290_1_) {
      this.sndManager.tick(p_215290_1_);
   }

   public void resume() {
      this.sndManager.resume();
   }

   public void setSoundLevel(SoundCategory p_184399_1_, float p_184399_2_) {
      if (p_184399_1_ == SoundCategory.MASTER && p_184399_2_ <= 0.0F) {
         this.stop();
      }

      this.sndManager.setVolume(p_184399_1_, p_184399_2_);
   }

   public void stop(ISound p_147683_1_) {
      this.sndManager.stop(p_147683_1_);
   }

   public boolean isPlaying(ISound p_215294_1_) {
      return this.sndManager.isPlaying(p_215294_1_);
   }

   public void addListener(ISoundEventListener p_184402_1_) {
      this.sndManager.addListener(p_184402_1_);
   }

   public void removeListener(ISoundEventListener p_184400_1_) {
      this.sndManager.removeListener(p_184400_1_);
   }

   public void stop(@Nullable ResourceLocation p_195478_1_, @Nullable SoundCategory p_195478_2_) {
      this.sndManager.stop(p_195478_1_, p_195478_2_);
   }

   public IResourceType getResourceType() {
      return VanillaResourceType.SOUNDS;
   }

   public String getDebugString() {
      return this.sndManager.getDebugString();
   }

   static {
      MISSING_SOUND = new Sound("meta:missing_sound", 1.0F, 1.0F, 1, Sound.Type.FILE, false, false, 16);
      LOGGER = LogManager.getLogger();
      GSON = (new GsonBuilder()).registerTypeHierarchyAdapter(ITextComponent.class, new ITextComponent.Serializer()).registerTypeAdapter(SoundList.class, new SoundListSerializer()).create();
      TYPE = new ParameterizedType() {
         public Type[] getActualTypeArguments() {
            return new Type[]{String.class, SoundList.class};
         }

         public Type getRawType() {
            return Map.class;
         }

         public Type getOwnerType() {
            return null;
         }
      };
   }

   @OnlyIn(Dist.CLIENT)
   public static class Loader {
      private final Map<ResourceLocation, SoundEventAccessor> field_217948_a = Maps.newHashMap();

      protected Loader() {
      }

      private void func_217944_a(ResourceLocation p_217944_1_, SoundList p_217944_2_, IResourceManager p_217944_3_) {
         SoundEventAccessor soundeventaccessor = (SoundEventAccessor)this.field_217948_a.get(p_217944_1_);
         boolean flag = soundeventaccessor == null;
         if (flag || p_217944_2_.canReplaceExisting()) {
            if (!flag) {
               SoundHandler.LOGGER.debug("Replaced sound event location {}", p_217944_1_);
            }

            soundeventaccessor = new SoundEventAccessor(p_217944_1_, p_217944_2_.getSubtitle());
            this.field_217948_a.put(p_217944_1_, soundeventaccessor);
         }

         Iterator var6 = p_217944_2_.getSounds().iterator();

         while(var6.hasNext()) {
            final Sound sound = (Sound)var6.next();
            final ResourceLocation resourcelocation = sound.getSoundLocation();
            Object isoundeventaccessor;
            switch(sound.getType()) {
            case FILE:
               if (!SoundHandler.func_215292_b(sound, p_217944_1_, p_217944_3_)) {
                  continue;
               }

               isoundeventaccessor = sound;
               break;
            case SOUND_EVENT:
               isoundeventaccessor = new ISoundEventAccessor<Sound>() {
                  public int getWeight() {
                     SoundEventAccessor soundeventaccessor1 = (SoundEventAccessor)Loader.this.field_217948_a.get(resourcelocation);
                     return soundeventaccessor1 == null ? 0 : soundeventaccessor1.getWeight();
                  }

                  public Sound cloneEntry() {
                     SoundEventAccessor soundeventaccessor1 = (SoundEventAccessor)Loader.this.field_217948_a.get(resourcelocation);
                     if (soundeventaccessor1 == null) {
                        return SoundHandler.MISSING_SOUND;
                     } else {
                        Sound sound1 = soundeventaccessor1.cloneEntry();
                        return new Sound(sound1.getSoundLocation().toString(), sound1.getVolume() * sound.getVolume(), sound1.getPitch() * sound.getPitch(), sound.getWeight(), Sound.Type.FILE, sound1.isStreaming() || sound.isStreaming(), sound1.shouldPreload(), sound1.getAttenuationDistance());
                     }
                  }

                  public void func_217867_a(SoundEngine p_217867_1_) {
                     SoundEventAccessor soundeventaccessor1 = (SoundEventAccessor)Loader.this.field_217948_a.get(resourcelocation);
                     if (soundeventaccessor1 != null) {
                        soundeventaccessor1.func_217867_a(p_217867_1_);
                     }

                  }
               };
               break;
            default:
               throw new IllegalStateException("Unknown SoundEventRegistration type: " + sound.getType());
            }

            soundeventaccessor.addSound((ISoundEventAccessor)isoundeventaccessor);
         }

      }

      public void func_217946_a(Map<ResourceLocation, SoundEventAccessor> p_217946_1_, SoundEngine p_217946_2_) {
         p_217946_1_.clear();
         Iterator var3 = this.field_217948_a.entrySet().iterator();

         while(var3.hasNext()) {
            Entry<ResourceLocation, SoundEventAccessor> entry = (Entry)var3.next();
            p_217946_1_.put(entry.getKey(), entry.getValue());
            ((SoundEventAccessor)entry.getValue()).func_217867_a(p_217946_2_);
         }

      }
   }
}
