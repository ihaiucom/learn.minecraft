package net.minecraft.client.particle;

import com.google.common.base.Charsets;
import com.google.common.collect.EvictingQueue;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Queues;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.MissingTextureSprite;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ReportedException;
import net.minecraft.entity.Entity;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleType;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IFutureReloadListener;
import net.minecraft.resources.IResource;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.Direction;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ParticleManager implements IFutureReloadListener {
   private static final List<IParticleRenderType> TYPES;
   protected World world;
   private final Map<IParticleRenderType, Queue<Particle>> byType = Maps.newIdentityHashMap();
   private final Queue<EmitterParticle> particleEmitters = Queues.newArrayDeque();
   private final TextureManager renderer;
   private final Random rand = new Random();
   private final Map<ResourceLocation, IParticleFactory<?>> factories = new HashMap();
   private final Queue<Particle> queue = Queues.newArrayDeque();
   private final Map<ResourceLocation, ParticleManager.AnimatedSpriteImpl> sprites = Maps.newHashMap();
   private final AtlasTexture atlas;

   public ParticleManager(World p_i1220_1_, TextureManager p_i1220_2_) {
      this.atlas = new AtlasTexture(AtlasTexture.LOCATION_PARTICLES_TEXTURE);
      p_i1220_2_.func_229263_a_(this.atlas.func_229223_g_(), this.atlas);
      this.world = p_i1220_1_;
      this.renderer = p_i1220_2_;
      this.registerFactories();
   }

   private void registerFactories() {
      this.registerFactory(ParticleTypes.AMBIENT_ENTITY_EFFECT, (ParticleManager.IParticleMetaFactory)(SpellParticle.AmbientMobFactory::new));
      this.registerFactory(ParticleTypes.ANGRY_VILLAGER, (ParticleManager.IParticleMetaFactory)(HeartParticle.AngryVillagerFactory::new));
      this.registerFactory(ParticleTypes.BARRIER, (IParticleFactory)(new BarrierParticle.Factory()));
      this.registerFactory(ParticleTypes.BLOCK, (IParticleFactory)(new DiggingParticle.Factory()));
      this.registerFactory(ParticleTypes.BUBBLE, (ParticleManager.IParticleMetaFactory)(BubbleParticle.Factory::new));
      this.registerFactory(ParticleTypes.BUBBLE_COLUMN_UP, (ParticleManager.IParticleMetaFactory)(BubbleColumnUpParticle.Factory::new));
      this.registerFactory(ParticleTypes.BUBBLE_POP, (ParticleManager.IParticleMetaFactory)(BubblePopParticle.Factory::new));
      this.registerFactory(ParticleTypes.CAMPFIRE_COSY_SMOKE, (ParticleManager.IParticleMetaFactory)(CampfireParticle.CozySmokeFactory::new));
      this.registerFactory(ParticleTypes.CAMPFIRE_SIGNAL_SMOKE, (ParticleManager.IParticleMetaFactory)(CampfireParticle.SignalSmokeFactory::new));
      this.registerFactory(ParticleTypes.CLOUD, (ParticleManager.IParticleMetaFactory)(CloudParticle.Factory::new));
      this.registerFactory(ParticleTypes.COMPOSTER, (ParticleManager.IParticleMetaFactory)(SuspendedTownParticle.ComposterFactory::new));
      this.registerFactory(ParticleTypes.CRIT, (ParticleManager.IParticleMetaFactory)(CritParticle.Factory::new));
      this.registerFactory(ParticleTypes.CURRENT_DOWN, (ParticleManager.IParticleMetaFactory)(CurrentDownParticle.Factory::new));
      this.registerFactory(ParticleTypes.DAMAGE_INDICATOR, (ParticleManager.IParticleMetaFactory)(CritParticle.DamageIndicatorFactory::new));
      this.registerFactory(ParticleTypes.DRAGON_BREATH, (ParticleManager.IParticleMetaFactory)(DragonBreathParticle.Factory::new));
      this.registerFactory(ParticleTypes.DOLPHIN, (ParticleManager.IParticleMetaFactory)(SuspendedTownParticle.DolphinSpeedFactory::new));
      this.registerFactory(ParticleTypes.DRIPPING_LAVA, (ParticleManager.IParticleMetaFactory)(DripParticle.DrippingLavaFactory::new));
      this.registerFactory(ParticleTypes.FALLING_LAVA, (ParticleManager.IParticleMetaFactory)(DripParticle.FallingLavaFactory::new));
      this.registerFactory(ParticleTypes.LANDING_LAVA, (ParticleManager.IParticleMetaFactory)(DripParticle.LandingLavaFactory::new));
      this.registerFactory(ParticleTypes.DRIPPING_WATER, (ParticleManager.IParticleMetaFactory)(DripParticle.DrippingWaterFactory::new));
      this.registerFactory(ParticleTypes.FALLING_WATER, (ParticleManager.IParticleMetaFactory)(DripParticle.FallingWaterFactory::new));
      this.registerFactory(ParticleTypes.DUST, RedstoneParticle.Factory::new);
      this.registerFactory(ParticleTypes.EFFECT, (ParticleManager.IParticleMetaFactory)(SpellParticle.Factory::new));
      this.registerFactory(ParticleTypes.ELDER_GUARDIAN, (IParticleFactory)(new MobAppearanceParticle.Factory()));
      this.registerFactory(ParticleTypes.ENCHANTED_HIT, (ParticleManager.IParticleMetaFactory)(CritParticle.MagicFactory::new));
      this.registerFactory(ParticleTypes.ENCHANT, (ParticleManager.IParticleMetaFactory)(EnchantmentTableParticle.EnchantmentTable::new));
      this.registerFactory(ParticleTypes.END_ROD, (ParticleManager.IParticleMetaFactory)(EndRodParticle.Factory::new));
      this.registerFactory(ParticleTypes.ENTITY_EFFECT, (ParticleManager.IParticleMetaFactory)(SpellParticle.MobFactory::new));
      this.registerFactory(ParticleTypes.EXPLOSION_EMITTER, (IParticleFactory)(new HugeExplosionParticle.Factory()));
      this.registerFactory(ParticleTypes.EXPLOSION, (ParticleManager.IParticleMetaFactory)(LargeExplosionParticle.Factory::new));
      this.registerFactory(ParticleTypes.FALLING_DUST, FallingDustParticle.Factory::new);
      this.registerFactory(ParticleTypes.FIREWORK, (ParticleManager.IParticleMetaFactory)(FireworkParticle.SparkFactory::new));
      this.registerFactory(ParticleTypes.FISHING, (ParticleManager.IParticleMetaFactory)(WaterWakeParticle.Factory::new));
      this.registerFactory(ParticleTypes.FLAME, (ParticleManager.IParticleMetaFactory)(FlameParticle.Factory::new));
      this.registerFactory(ParticleTypes.FLASH, (ParticleManager.IParticleMetaFactory)(FireworkParticle.OverlayFactory::new));
      this.registerFactory(ParticleTypes.HAPPY_VILLAGER, (ParticleManager.IParticleMetaFactory)(SuspendedTownParticle.HappyVillagerFactory::new));
      this.registerFactory(ParticleTypes.HEART, (ParticleManager.IParticleMetaFactory)(HeartParticle.Factory::new));
      this.registerFactory(ParticleTypes.INSTANT_EFFECT, (ParticleManager.IParticleMetaFactory)(SpellParticle.InstantFactory::new));
      this.registerFactory(ParticleTypes.ITEM, (IParticleFactory)(new BreakingParticle.Factory()));
      this.registerFactory(ParticleTypes.ITEM_SLIME, (IParticleFactory)(new BreakingParticle.SlimeFactory()));
      this.registerFactory(ParticleTypes.ITEM_SNOWBALL, (IParticleFactory)(new BreakingParticle.SnowballFactory()));
      this.registerFactory(ParticleTypes.LARGE_SMOKE, (ParticleManager.IParticleMetaFactory)(LargeSmokeParticle.Factory::new));
      this.registerFactory(ParticleTypes.LAVA, (ParticleManager.IParticleMetaFactory)(LavaParticle.Factory::new));
      this.registerFactory(ParticleTypes.MYCELIUM, (ParticleManager.IParticleMetaFactory)(SuspendedTownParticle.Factory::new));
      this.registerFactory(ParticleTypes.NAUTILUS, (ParticleManager.IParticleMetaFactory)(EnchantmentTableParticle.NautilusFactory::new));
      this.registerFactory(ParticleTypes.NOTE, (ParticleManager.IParticleMetaFactory)(NoteParticle.Factory::new));
      this.registerFactory(ParticleTypes.POOF, (ParticleManager.IParticleMetaFactory)(PoofParticle.Factory::new));
      this.registerFactory(ParticleTypes.PORTAL, (ParticleManager.IParticleMetaFactory)(PortalParticle.Factory::new));
      this.registerFactory(ParticleTypes.RAIN, (ParticleManager.IParticleMetaFactory)(RainParticle.Factory::new));
      this.registerFactory(ParticleTypes.SMOKE, (ParticleManager.IParticleMetaFactory)(SmokeParticle.Factory::new));
      this.registerFactory(ParticleTypes.SNEEZE, (ParticleManager.IParticleMetaFactory)(CloudParticle.SneezeFactory::new));
      this.registerFactory(ParticleTypes.SPIT, (ParticleManager.IParticleMetaFactory)(SpitParticle.Factory::new));
      this.registerFactory(ParticleTypes.SWEEP_ATTACK, (ParticleManager.IParticleMetaFactory)(SweepAttackParticle.Factory::new));
      this.registerFactory(ParticleTypes.TOTEM_OF_UNDYING, (ParticleManager.IParticleMetaFactory)(TotemOfUndyingParticle.Factory::new));
      this.registerFactory(ParticleTypes.SQUID_INK, (ParticleManager.IParticleMetaFactory)(SquidInkParticle.Factory::new));
      this.registerFactory(ParticleTypes.UNDERWATER, (ParticleManager.IParticleMetaFactory)(UnderwaterParticle.Factory::new));
      this.registerFactory(ParticleTypes.SPLASH, (ParticleManager.IParticleMetaFactory)(SplashParticle.Factory::new));
      this.registerFactory(ParticleTypes.WITCH, (ParticleManager.IParticleMetaFactory)(SpellParticle.WitchFactory::new));
      this.registerFactory(ParticleTypes.field_229427_ag_, (ParticleManager.IParticleMetaFactory)(DripParticle.DrippingHoneyFactory::new));
      this.registerFactory(ParticleTypes.field_229428_ah_, (ParticleManager.IParticleMetaFactory)(DripParticle.FallingHoneyFactory::new));
      this.registerFactory(ParticleTypes.field_229429_ai_, (ParticleManager.IParticleMetaFactory)(DripParticle.LandingHoneyFactory::new));
      this.registerFactory(ParticleTypes.field_229430_aj_, (ParticleManager.IParticleMetaFactory)(DripParticle.FallingNectarFactory::new));
   }

   public <T extends IParticleData> void registerFactory(ParticleType<T> p_199283_1_, IParticleFactory<T> p_199283_2_) {
      this.factories.put(Registry.PARTICLE_TYPE.getKey(p_199283_1_), p_199283_2_);
   }

   public <T extends IParticleData> void registerFactory(ParticleType<T> p_215234_1_, ParticleManager.IParticleMetaFactory<T> p_215234_2_) {
      ParticleManager.AnimatedSpriteImpl particlemanager$animatedspriteimpl = new ParticleManager.AnimatedSpriteImpl();
      this.sprites.put(Registry.PARTICLE_TYPE.getKey(p_215234_1_), particlemanager$animatedspriteimpl);
      this.factories.put(Registry.PARTICLE_TYPE.getKey(p_215234_1_), p_215234_2_.create(particlemanager$animatedspriteimpl));
   }

   public CompletableFuture<Void> reload(IFutureReloadListener.IStage p_215226_1_, IResourceManager p_215226_2_, IProfiler p_215226_3_, IProfiler p_215226_4_, Executor p_215226_5_, Executor p_215226_6_) {
      Map<ResourceLocation, List<ResourceLocation>> map = Maps.newConcurrentMap();
      CompletableFuture<?>[] completablefuture = (CompletableFuture[])Registry.PARTICLE_TYPE.keySet().stream().map((p_lambda$reload$1_4_) -> {
         return CompletableFuture.runAsync(() -> {
            this.loadTextureLists(p_215226_2_, p_lambda$reload$1_4_, map);
         }, p_215226_5_);
      }).toArray((p_lambda$reload$2_0_) -> {
         return new CompletableFuture[p_lambda$reload$2_0_];
      });
      CompletableFuture var10000 = CompletableFuture.allOf(completablefuture).thenApplyAsync((p_lambda$reload$3_4_) -> {
         p_215226_3_.startTick();
         p_215226_3_.startSection("stitching");
         AtlasTexture.SheetData atlastexture$sheetdata = this.atlas.func_229220_a_(p_215226_2_, map.values().stream().flatMap(Collection::stream), p_215226_3_, 0);
         p_215226_3_.endSection();
         p_215226_3_.endTick();
         return atlastexture$sheetdata;
      }, p_215226_5_);
      p_215226_1_.getClass();
      return var10000.thenCompose(p_215226_1_::markCompleteAwaitingOthers).thenAcceptAsync((p_lambda$reload$5_3_) -> {
         this.byType.clear();
         p_215226_4_.startTick();
         p_215226_4_.startSection("upload");
         this.atlas.upload(p_lambda$reload$5_3_);
         p_215226_4_.endStartSection("bindSpriteSets");
         TextureAtlasSprite textureatlassprite = this.atlas.getSprite(MissingTextureSprite.getLocation());
         map.forEach((p_lambda$null$4_2_, p_lambda$null$4_3_) -> {
            ImmutableList var10000;
            if (p_lambda$null$4_3_.isEmpty()) {
               var10000 = ImmutableList.of(textureatlassprite);
            } else {
               Stream var5 = p_lambda$null$4_3_.stream();
               AtlasTexture var10001 = this.atlas;
               var10001.getClass();
               var10000 = (ImmutableList)var5.map(var10001::getSprite).collect(ImmutableList.toImmutableList());
            }

            ImmutableList<TextureAtlasSprite> immutablelist = var10000;
            ((ParticleManager.AnimatedSpriteImpl)this.sprites.get(p_lambda$null$4_2_)).setSprites(immutablelist);
         });
         p_215226_4_.endSection();
         p_215226_4_.endTick();
      }, p_215226_6_);
   }

   public void func_215232_a() {
      this.atlas.clear();
   }

   private void loadTextureLists(IResourceManager p_215236_1_, ResourceLocation p_215236_2_, Map<ResourceLocation, List<ResourceLocation>> p_215236_3_) {
      ResourceLocation resourcelocation = new ResourceLocation(p_215236_2_.getNamespace(), "particles/" + p_215236_2_.getPath() + ".json");

      try {
         IResource iresource = p_215236_1_.getResource(resourcelocation);
         Throwable var6 = null;

         try {
            Reader reader = new InputStreamReader(iresource.getInputStream(), Charsets.UTF_8);
            Throwable var8 = null;

            try {
               TexturesParticle texturesparticle = TexturesParticle.deserialize(JSONUtils.fromJson((Reader)reader));
               List<ResourceLocation> list = texturesparticle.getTextures();
               boolean flag = this.sprites.containsKey(p_215236_2_);
               if (list == null) {
                  if (flag) {
                     throw new IllegalStateException("Missing texture list for particle " + p_215236_2_);
                  }
               } else {
                  if (!flag) {
                     throw new IllegalStateException("Redundant texture list for particle " + p_215236_2_);
                  }

                  p_215236_3_.put(p_215236_2_, list.stream().map((p_lambda$loadTextureLists$6_0_) -> {
                     return new ResourceLocation(p_lambda$loadTextureLists$6_0_.getNamespace(), "particle/" + p_lambda$loadTextureLists$6_0_.getPath());
                  }).collect(Collectors.toList()));
               }
            } catch (Throwable var35) {
               var8 = var35;
               throw var35;
            } finally {
               if (reader != null) {
                  if (var8 != null) {
                     try {
                        reader.close();
                     } catch (Throwable var34) {
                        var8.addSuppressed(var34);
                     }
                  } else {
                     reader.close();
                  }
               }

            }
         } catch (Throwable var37) {
            var6 = var37;
            throw var37;
         } finally {
            if (iresource != null) {
               if (var6 != null) {
                  try {
                     iresource.close();
                  } catch (Throwable var33) {
                     var6.addSuppressed(var33);
                  }
               } else {
                  iresource.close();
               }
            }

         }

      } catch (IOException var39) {
         throw new IllegalStateException("Failed to load description for particle " + p_215236_2_, var39);
      }
   }

   public void addParticleEmitter(Entity p_199282_1_, IParticleData p_199282_2_) {
      this.particleEmitters.add(new EmitterParticle(this.world, p_199282_1_, p_199282_2_));
   }

   public void emitParticleAtEntity(Entity p_199281_1_, IParticleData p_199281_2_, int p_199281_3_) {
      this.particleEmitters.add(new EmitterParticle(this.world, p_199281_1_, p_199281_2_, p_199281_3_));
   }

   @Nullable
   public Particle addParticle(IParticleData p_199280_1_, double p_199280_2_, double p_199280_4_, double p_199280_6_, double p_199280_8_, double p_199280_10_, double p_199280_12_) {
      Particle particle = this.makeParticle(p_199280_1_, p_199280_2_, p_199280_4_, p_199280_6_, p_199280_8_, p_199280_10_, p_199280_12_);
      if (particle != null) {
         this.addEffect(particle);
         return particle;
      } else {
         return null;
      }
   }

   @Nullable
   private <T extends IParticleData> Particle makeParticle(T p_199927_1_, double p_199927_2_, double p_199927_4_, double p_199927_6_, double p_199927_8_, double p_199927_10_, double p_199927_12_) {
      IParticleFactory<T> iparticlefactory = (IParticleFactory)this.factories.get(Registry.PARTICLE_TYPE.getKey(p_199927_1_.getType()));
      return iparticlefactory == null ? null : iparticlefactory.makeParticle(p_199927_1_, this.world, p_199927_2_, p_199927_4_, p_199927_6_, p_199927_8_, p_199927_10_, p_199927_12_);
   }

   public void addEffect(Particle p_78873_1_) {
      if (p_78873_1_ != null) {
         this.queue.add(p_78873_1_);
      }
   }

   public void tick() {
      this.byType.forEach((p_lambda$tick$7_1_, p_lambda$tick$7_2_) -> {
         this.world.getProfiler().startSection(p_lambda$tick$7_1_.toString());
         this.tickParticleList(p_lambda$tick$7_2_);
         this.world.getProfiler().endSection();
      });
      if (!this.particleEmitters.isEmpty()) {
         List<EmitterParticle> list = Lists.newArrayList();
         Iterator var2 = this.particleEmitters.iterator();

         while(var2.hasNext()) {
            EmitterParticle emitterparticle = (EmitterParticle)var2.next();
            emitterparticle.tick();
            if (!emitterparticle.isAlive()) {
               list.add(emitterparticle);
            }
         }

         this.particleEmitters.removeAll(list);
      }

      Particle particle;
      if (!this.queue.isEmpty()) {
         while((particle = (Particle)this.queue.poll()) != null) {
            ((Queue)this.byType.computeIfAbsent(particle.getRenderType(), (p_lambda$tick$8_0_) -> {
               return EvictingQueue.create(16384);
            })).add(particle);
         }
      }

   }

   private void tickParticleList(Collection<Particle> p_187240_1_) {
      if (!p_187240_1_.isEmpty()) {
         Iterator iterator = p_187240_1_.iterator();

         while(iterator.hasNext()) {
            Particle particle = (Particle)iterator.next();
            this.tickParticle(particle);
            if (!particle.isAlive()) {
               iterator.remove();
            }
         }
      }

   }

   private void tickParticle(Particle p_178923_1_) {
      try {
         p_178923_1_.tick();
      } catch (Throwable var5) {
         CrashReport crashreport = CrashReport.makeCrashReport(var5, "Ticking Particle");
         CrashReportCategory crashreportcategory = crashreport.makeCategory("Particle being ticked");
         crashreportcategory.addDetail("Particle", p_178923_1_::toString);
         IParticleRenderType var10002 = p_178923_1_.getRenderType();
         crashreportcategory.addDetail("Particle Type", var10002::toString);
         throw new ReportedException(crashreport);
      }
   }

   public void func_228345_a_(MatrixStack p_228345_1_, IRenderTypeBuffer.Impl p_228345_2_, LightTexture p_228345_3_, ActiveRenderInfo p_228345_4_, float p_228345_5_) {
      p_228345_3_.enableLightmap();
      Runnable enable = () -> {
         RenderSystem.enableAlphaTest();
         RenderSystem.defaultAlphaFunc();
         RenderSystem.enableDepthTest();
         RenderSystem.enableFog();
      };
      RenderSystem.pushMatrix();
      RenderSystem.multMatrix(p_228345_1_.func_227866_c_().func_227870_a_());
      Iterator var7 = this.byType.keySet().iterator();

      while(true) {
         IParticleRenderType iparticlerendertype;
         Iterable iterable;
         do {
            do {
               if (!var7.hasNext()) {
                  RenderSystem.popMatrix();
                  RenderSystem.depthMask(true);
                  RenderSystem.disableBlend();
                  RenderSystem.defaultAlphaFunc();
                  p_228345_3_.disableLightmap();
                  RenderSystem.disableFog();
                  return;
               }

               iparticlerendertype = (IParticleRenderType)var7.next();
            } while(iparticlerendertype == IParticleRenderType.NO_RENDER);

            enable.run();
            iterable = (Iterable)this.byType.get(iparticlerendertype);
         } while(iterable == null);

         RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
         Tessellator tessellator = Tessellator.getInstance();
         BufferBuilder bufferbuilder = tessellator.getBuffer();
         iparticlerendertype.beginRender(bufferbuilder, this.renderer);
         Iterator var12 = iterable.iterator();

         while(var12.hasNext()) {
            Particle particle = (Particle)var12.next();

            try {
               particle.func_225606_a_(bufferbuilder, p_228345_4_, p_228345_5_);
            } catch (Throwable var17) {
               CrashReport crashreport = CrashReport.makeCrashReport(var17, "Rendering Particle");
               CrashReportCategory crashreportcategory = crashreport.makeCategory("Particle being rendered");
               crashreportcategory.addDetail("Particle", particle::toString);
               crashreportcategory.addDetail("Particle Type", iparticlerendertype::toString);
               throw new ReportedException(crashreport);
            }
         }

         iparticlerendertype.finishRender(tessellator);
      }
   }

   public void clearEffects(@Nullable World p_78870_1_) {
      this.world = p_78870_1_;
      this.byType.clear();
      this.particleEmitters.clear();
   }

   public void addBlockDestroyEffects(BlockPos p_180533_1_, BlockState p_180533_2_) {
      if (!p_180533_2_.isAir(this.world, p_180533_1_) && !p_180533_2_.addDestroyEffects(this.world, p_180533_1_, this)) {
         VoxelShape voxelshape = p_180533_2_.getShape(this.world, p_180533_1_);
         double d0 = 0.25D;
         voxelshape.forEachBox((p_lambda$addBlockDestroyEffects$10_3_, p_lambda$addBlockDestroyEffects$10_5_, p_lambda$addBlockDestroyEffects$10_7_, p_lambda$addBlockDestroyEffects$10_9_, p_lambda$addBlockDestroyEffects$10_11_, p_lambda$addBlockDestroyEffects$10_13_) -> {
            double d1 = Math.min(1.0D, p_lambda$addBlockDestroyEffects$10_9_ - p_lambda$addBlockDestroyEffects$10_3_);
            double d2 = Math.min(1.0D, p_lambda$addBlockDestroyEffects$10_11_ - p_lambda$addBlockDestroyEffects$10_5_);
            double d3 = Math.min(1.0D, p_lambda$addBlockDestroyEffects$10_13_ - p_lambda$addBlockDestroyEffects$10_7_);
            int i = Math.max(2, MathHelper.ceil(d1 / 0.25D));
            int j = Math.max(2, MathHelper.ceil(d2 / 0.25D));
            int k = Math.max(2, MathHelper.ceil(d3 / 0.25D));

            for(int l = 0; l < i; ++l) {
               for(int i1 = 0; i1 < j; ++i1) {
                  for(int j1 = 0; j1 < k; ++j1) {
                     double d4 = ((double)l + 0.5D) / (double)i;
                     double d5 = ((double)i1 + 0.5D) / (double)j;
                     double d6 = ((double)j1 + 0.5D) / (double)k;
                     double d7 = d4 * d1 + p_lambda$addBlockDestroyEffects$10_3_;
                     double d8 = d5 * d2 + p_lambda$addBlockDestroyEffects$10_5_;
                     double d9 = d6 * d3 + p_lambda$addBlockDestroyEffects$10_7_;
                     this.addEffect((new DiggingParticle(this.world, (double)p_180533_1_.getX() + d7, (double)p_180533_1_.getY() + d8, (double)p_180533_1_.getZ() + d9, d4 - 0.5D, d5 - 0.5D, d6 - 0.5D, p_180533_2_)).setBlockPos(p_180533_1_));
                  }
               }
            }

         });
      }

   }

   public void addBlockHitEffects(BlockPos p_180532_1_, Direction p_180532_2_) {
      BlockState blockstate = this.world.getBlockState(p_180532_1_);
      if (blockstate.getRenderType() != BlockRenderType.INVISIBLE) {
         int i = p_180532_1_.getX();
         int j = p_180532_1_.getY();
         int k = p_180532_1_.getZ();
         float f = 0.1F;
         AxisAlignedBB axisalignedbb = blockstate.getShape(this.world, p_180532_1_).getBoundingBox();
         double d0 = (double)i + this.rand.nextDouble() * (axisalignedbb.maxX - axisalignedbb.minX - 0.20000000298023224D) + 0.10000000149011612D + axisalignedbb.minX;
         double d1 = (double)j + this.rand.nextDouble() * (axisalignedbb.maxY - axisalignedbb.minY - 0.20000000298023224D) + 0.10000000149011612D + axisalignedbb.minY;
         double d2 = (double)k + this.rand.nextDouble() * (axisalignedbb.maxZ - axisalignedbb.minZ - 0.20000000298023224D) + 0.10000000149011612D + axisalignedbb.minZ;
         if (p_180532_2_ == Direction.DOWN) {
            d1 = (double)j + axisalignedbb.minY - 0.10000000149011612D;
         }

         if (p_180532_2_ == Direction.UP) {
            d1 = (double)j + axisalignedbb.maxY + 0.10000000149011612D;
         }

         if (p_180532_2_ == Direction.NORTH) {
            d2 = (double)k + axisalignedbb.minZ - 0.10000000149011612D;
         }

         if (p_180532_2_ == Direction.SOUTH) {
            d2 = (double)k + axisalignedbb.maxZ + 0.10000000149011612D;
         }

         if (p_180532_2_ == Direction.WEST) {
            d0 = (double)i + axisalignedbb.minX - 0.10000000149011612D;
         }

         if (p_180532_2_ == Direction.EAST) {
            d0 = (double)i + axisalignedbb.maxX + 0.10000000149011612D;
         }

         this.addEffect((new DiggingParticle(this.world, d0, d1, d2, 0.0D, 0.0D, 0.0D, blockstate)).setBlockPos(p_180532_1_).multiplyVelocity(0.2F).multipleParticleScaleBy(0.6F));
      }

   }

   public String getStatistics() {
      return String.valueOf(this.byType.values().stream().mapToInt(Collection::size).sum());
   }

   public void addBlockHitEffects(BlockPos p_addBlockHitEffects_1_, BlockRayTraceResult p_addBlockHitEffects_2_) {
      BlockState state = this.world.getBlockState(p_addBlockHitEffects_1_);
      if (!state.addHitEffects(this.world, p_addBlockHitEffects_2_, this)) {
         this.addBlockHitEffects(p_addBlockHitEffects_1_, p_addBlockHitEffects_2_.getFace());
      }

   }

   static {
      TYPES = ImmutableList.of(IParticleRenderType.TERRAIN_SHEET, IParticleRenderType.PARTICLE_SHEET_OPAQUE, IParticleRenderType.PARTICLE_SHEET_LIT, IParticleRenderType.PARTICLE_SHEET_TRANSLUCENT, IParticleRenderType.CUSTOM);
   }

   @FunctionalInterface
   @OnlyIn(Dist.CLIENT)
   public interface IParticleMetaFactory<T extends IParticleData> {
      IParticleFactory<T> create(IAnimatedSprite var1);
   }

   @OnlyIn(Dist.CLIENT)
   class AnimatedSpriteImpl implements IAnimatedSprite {
      private List<TextureAtlasSprite> field_217594_b;

      private AnimatedSpriteImpl() {
      }

      public TextureAtlasSprite get(int p_217591_1_, int p_217591_2_) {
         return (TextureAtlasSprite)this.field_217594_b.get(p_217591_1_ * (this.field_217594_b.size() - 1) / p_217591_2_);
      }

      public TextureAtlasSprite get(Random p_217590_1_) {
         return (TextureAtlasSprite)this.field_217594_b.get(p_217590_1_.nextInt(this.field_217594_b.size()));
      }

      public void setSprites(List<TextureAtlasSprite> p_217592_1_) {
         this.field_217594_b = ImmutableList.copyOf(p_217592_1_);
      }

      // $FF: synthetic method
      AnimatedSpriteImpl(Object p_i50877_2_) {
         this();
      }
   }
}
