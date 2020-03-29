package net.minecraft.client.renderer.model;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.renderer.Atlases;
import net.minecraft.client.renderer.BlockModelShapes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.TransformationMatrix;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.client.renderer.model.multipart.Multipart;
import net.minecraft.client.renderer.model.multipart.Selector;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.MissingTextureSprite;
import net.minecraft.client.renderer.texture.SpriteMap;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.tileentity.BellTileEntityRenderer;
import net.minecraft.client.renderer.tileentity.ConduitTileEntityRenderer;
import net.minecraft.client.renderer.tileentity.EnchantmentTableTileEntityRenderer;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IResource;
import net.minecraft.resources.IResourceManager;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.IProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.ForgeHooksClient;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.tuple.Triple;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class ModelBakery {
   public static final Material LOCATION_FIRE_0;
   public static final Material LOCATION_FIRE_1;
   public static final Material LOCATION_LAVA_FLOW;
   public static final Material LOCATION_WATER_FLOW;
   public static final Material LOCATION_WATER_OVERLAY;
   public static final Material field_229315_f_;
   public static final Material field_229316_g_;
   public static final Material field_229317_h_;
   public static final List<ResourceLocation> field_229318_i_;
   public static final List<ResourceLocation> field_229319_j_;
   public static final List<RenderType> field_229320_k_;
   protected static final Set<Material> LOCATIONS_BUILTIN_TEXTURES;
   private static final Logger LOGGER;
   public static final ModelResourceLocation MODEL_MISSING;
   private static final String field_229321_r_;
   @VisibleForTesting
   public static final String MISSING_MODEL_MESH;
   private static final Map<String, String> BUILT_IN_MODELS;
   private static final Splitter SPLITTER_COMMA;
   private static final Splitter EQUALS_SPLITTER;
   protected static final BlockModel MODEL_GENERATED;
   protected static final BlockModel MODEL_ENTITY;
   private static final StateContainer<Block, BlockState> STATE_CONTAINER_ITEM_FRAME;
   private static final ItemModelGenerator field_217854_z;
   private static final Map<ResourceLocation, StateContainer<Block, BlockState>> STATE_CONTAINER_OVERRIDES;
   protected final IResourceManager resourceManager;
   @Nullable
   private SpriteMap field_229322_z_;
   private final BlockColors field_225365_D;
   private final Set<ResourceLocation> field_217848_D;
   private final BlockModelDefinition.ContainerHolder containerHolder;
   private final Map<ResourceLocation, IUnbakedModel> field_217849_F;
   private final Map<Triple<ResourceLocation, TransformationMatrix, Boolean>, IBakedModel> field_217850_G;
   private final Map<ResourceLocation, IUnbakedModel> field_217851_H;
   private final Map<ResourceLocation, IBakedModel> field_217852_I;
   private Map<ResourceLocation, Pair<AtlasTexture, AtlasTexture.SheetData>> field_217853_J;
   private int field_225366_L;
   private final Object2IntMap<BlockState> field_225367_M;

   public ModelBakery(IResourceManager p_i226056_1_, BlockColors p_i226056_2_, IProfiler p_i226056_3_, int p_i226056_4_) {
      this(p_i226056_1_, p_i226056_2_, true);
      this.processLoading(p_i226056_3_, p_i226056_4_);
   }

   protected ModelBakery(IResourceManager p_i230085_1_, BlockColors p_i230085_2_, boolean p_i230085_3_) {
      this.field_217848_D = Sets.newHashSet();
      this.containerHolder = new BlockModelDefinition.ContainerHolder();
      this.field_217849_F = Maps.newHashMap();
      this.field_217850_G = Maps.newHashMap();
      this.field_217851_H = Maps.newHashMap();
      this.field_217852_I = Maps.newHashMap();
      this.field_225366_L = 1;
      this.field_225367_M = (Object2IntMap)Util.make(new Object2IntOpenHashMap(), (p_lambda$new$5_0_) -> {
         p_lambda$new$5_0_.defaultReturnValue(-1);
      });
      this.resourceManager = p_i230085_1_;
      this.field_225365_D = p_i230085_2_;
   }

   protected void processLoading(IProfiler p_processLoading_1_, int p_processLoading_2_) {
      p_processLoading_1_.startSection("missing_model");

      try {
         this.field_217849_F.put(MODEL_MISSING, this.loadModel(MODEL_MISSING));
         this.func_217843_a(MODEL_MISSING);
      } catch (IOException var10) {
         LOGGER.error("Error loading missing model, should never happen :(", var10);
         throw new RuntimeException(var10);
      }

      p_processLoading_1_.endStartSection("static_definitions");
      STATE_CONTAINER_OVERRIDES.forEach((p_lambda$processLoading$7_1_, p_lambda$processLoading$7_2_) -> {
         p_lambda$processLoading$7_2_.getValidStates().forEach((p_lambda$null$6_2_) -> {
            this.func_217843_a(BlockModelShapes.getModelLocation(p_lambda$processLoading$7_1_, p_lambda$null$6_2_));
         });
      });
      p_processLoading_1_.endStartSection("blocks");
      Iterator var3 = Registry.BLOCK.iterator();

      while(var3.hasNext()) {
         Block block = (Block)var3.next();
         block.getStateContainer().getValidStates().forEach((p_lambda$processLoading$8_1_) -> {
            this.func_217843_a(BlockModelShapes.getModelLocation(p_lambda$processLoading$8_1_));
         });
      }

      p_processLoading_1_.endStartSection("items");
      var3 = Registry.ITEM.keySet().iterator();

      ResourceLocation rl;
      while(var3.hasNext()) {
         rl = (ResourceLocation)var3.next();
         this.func_217843_a(new ModelResourceLocation(rl, "inventory"));
      }

      p_processLoading_1_.endStartSection("special");
      this.func_217843_a(new ModelResourceLocation("minecraft:trident_in_hand#inventory"));
      var3 = this.getSpecialModels().iterator();

      while(var3.hasNext()) {
         rl = (ResourceLocation)var3.next();
         this.addModelToCache(rl);
      }

      p_processLoading_1_.endStartSection("textures");
      Set<Pair<String, String>> set = Sets.newLinkedHashSet();
      Set<Material> set1 = (Set)this.field_217851_H.values().stream().flatMap((p_lambda$processLoading$9_2_) -> {
         return p_lambda$processLoading$9_2_.func_225614_a_(this::getUnbakedModel, set).stream();
      }).collect(Collectors.toSet());
      set1.addAll(LOCATIONS_BUILTIN_TEXTURES);
      ForgeHooksClient.gatherFluidTextures(set1);
      set.stream().filter((p_lambda$processLoading$10_0_) -> {
         return !((String)p_lambda$processLoading$10_0_.getSecond()).equals(field_229321_r_);
      }).forEach((p_lambda$processLoading$11_0_) -> {
         LOGGER.warn("Unable to resolve texture reference: {} in {}", p_lambda$processLoading$11_0_.getFirst(), p_lambda$processLoading$11_0_.getSecond());
      });
      Map<ResourceLocation, List<Material>> map = (Map)set1.stream().collect(Collectors.groupingBy(Material::func_229310_a_));
      p_processLoading_1_.endStartSection("stitching");
      this.field_217853_J = Maps.newHashMap();
      Iterator var6 = map.entrySet().iterator();

      while(var6.hasNext()) {
         Entry<ResourceLocation, List<Material>> entry = (Entry)var6.next();
         AtlasTexture atlastexture = new AtlasTexture((ResourceLocation)entry.getKey());
         AtlasTexture.SheetData atlastexture$sheetdata = atlastexture.func_229220_a_(this.resourceManager, ((List)entry.getValue()).stream().map(Material::func_229313_b_), p_processLoading_1_, p_processLoading_2_);
         this.field_217853_J.put(entry.getKey(), Pair.of(atlastexture, atlastexture$sheetdata));
      }

      p_processLoading_1_.endSection();
   }

   public SpriteMap func_229333_a_(TextureManager p_229333_1_, IProfiler p_229333_2_) {
      p_229333_2_.startSection("atlas");
      Iterator var3 = this.field_217853_J.values().iterator();

      while(var3.hasNext()) {
         Pair<AtlasTexture, AtlasTexture.SheetData> pair = (Pair)var3.next();
         AtlasTexture atlastexture = (AtlasTexture)pair.getFirst();
         AtlasTexture.SheetData atlastexture$sheetdata = (AtlasTexture.SheetData)pair.getSecond();
         atlastexture.upload(atlastexture$sheetdata);
         p_229333_1_.func_229263_a_(atlastexture.func_229223_g_(), atlastexture);
         p_229333_1_.bindTexture(atlastexture.func_229223_g_());
         atlastexture.func_229221_b_(atlastexture$sheetdata);
      }

      this.field_229322_z_ = new SpriteMap((Collection)this.field_217853_J.values().stream().map(Pair::getFirst).collect(Collectors.toList()));
      p_229333_2_.endStartSection("baking");
      this.field_217851_H.keySet().forEach((p_lambda$func_229333_a_$12_1_) -> {
         IBakedModel ibakedmodel = null;

         try {
            ibakedmodel = this.func_217845_a(p_lambda$func_229333_a_$12_1_, ModelRotation.X0_Y0);
         } catch (Exception var4) {
            var4.printStackTrace();
            LOGGER.warn("Unable to bake model: '{}': {}", p_lambda$func_229333_a_$12_1_, var4);
         }

         if (ibakedmodel != null) {
            this.field_217852_I.put(p_lambda$func_229333_a_$12_1_, ibakedmodel);
         }

      });
      p_229333_2_.endSection();
      return this.field_229322_z_;
   }

   private static Predicate<BlockState> parseVariantKey(StateContainer<Block, BlockState> p_209605_0_, String p_209605_1_) {
      Map<IProperty<?>, Comparable<?>> map = Maps.newHashMap();
      Iterator var3 = SPLITTER_COMMA.split(p_209605_1_).iterator();

      while(true) {
         while(true) {
            Iterator iterator;
            do {
               if (!var3.hasNext()) {
                  Block block = (Block)p_209605_0_.getOwner();
                  return (p_lambda$parseVariantKey$13_2_) -> {
                     if (p_lambda$parseVariantKey$13_2_ != null && block == p_lambda$parseVariantKey$13_2_.getBlock()) {
                        Iterator var3 = map.entrySet().iterator();

                        Entry entry;
                        do {
                           if (!var3.hasNext()) {
                              return true;
                           }

                           entry = (Entry)var3.next();
                        } while(Objects.equals(p_lambda$parseVariantKey$13_2_.get((IProperty)entry.getKey()), entry.getValue()));

                        return false;
                     } else {
                        return false;
                     }
                  };
               }

               String s = (String)var3.next();
               iterator = EQUALS_SPLITTER.split(s).iterator();
            } while(!iterator.hasNext());

            String s1 = (String)iterator.next();
            IProperty<?> iproperty = p_209605_0_.getProperty(s1);
            if (iproperty != null && iterator.hasNext()) {
               String s2 = (String)iterator.next();
               Comparable<?> comparable = parseValue(iproperty, s2);
               if (comparable == null) {
                  throw new RuntimeException("Unknown value: '" + s2 + "' for blockstate property: '" + s1 + "' " + iproperty.getAllowedValues());
               }

               map.put(iproperty, comparable);
            } else if (!s1.isEmpty()) {
               throw new RuntimeException("Unknown blockstate property: '" + s1 + "'");
            }
         }
      }
   }

   @Nullable
   static <T extends Comparable<T>> T parseValue(IProperty<T> p_209592_0_, String p_209592_1_) {
      return (Comparable)p_209592_0_.parseValue(p_209592_1_).orElse((Comparable)null);
   }

   public IUnbakedModel getUnbakedModel(ResourceLocation p_209597_1_) {
      if (this.field_217849_F.containsKey(p_209597_1_)) {
         return (IUnbakedModel)this.field_217849_F.get(p_209597_1_);
      } else if (this.field_217848_D.contains(p_209597_1_)) {
         throw new IllegalStateException("Circular reference while loading " + p_209597_1_);
      } else {
         this.field_217848_D.add(p_209597_1_);
         IUnbakedModel iunbakedmodel = (IUnbakedModel)this.field_217849_F.get(MODEL_MISSING);

         while(!this.field_217848_D.isEmpty()) {
            ResourceLocation resourcelocation = (ResourceLocation)this.field_217848_D.iterator().next();

            try {
               if (!this.field_217849_F.containsKey(resourcelocation)) {
                  this.loadBlockstate(resourcelocation);
               }
            } catch (ModelBakery.BlockStateDefinitionException var9) {
               LOGGER.warn(var9.getMessage());
               this.field_217849_F.put(resourcelocation, iunbakedmodel);
            } catch (Exception var10) {
               LOGGER.warn("Unable to load model: '{}' referenced from: {}: {}", resourcelocation, p_209597_1_, var10);
               this.field_217849_F.put(resourcelocation, iunbakedmodel);
            } finally {
               this.field_217848_D.remove(resourcelocation);
            }
         }

         return (IUnbakedModel)this.field_217849_F.getOrDefault(p_209597_1_, iunbakedmodel);
      }
   }

   private void loadBlockstate(ResourceLocation p_209598_1_) throws Exception {
      if (!(p_209598_1_ instanceof ModelResourceLocation)) {
         this.putModel(p_209598_1_, this.loadModel(p_209598_1_));
      } else {
         ModelResourceLocation modelresourcelocation = (ModelResourceLocation)p_209598_1_;
         ResourceLocation resourcelocation;
         if (Objects.equals(modelresourcelocation.getVariant(), "inventory")) {
            resourcelocation = new ResourceLocation(p_209598_1_.getNamespace(), "item/" + p_209598_1_.getPath());
            BlockModel blockmodel = this.loadModel(resourcelocation);
            this.putModel(modelresourcelocation, blockmodel);
            this.field_217849_F.put(resourcelocation, blockmodel);
         } else {
            resourcelocation = new ResourceLocation(p_209598_1_.getNamespace(), p_209598_1_.getPath());
            StateContainer<Block, BlockState> statecontainer = (StateContainer)Optional.ofNullable(STATE_CONTAINER_OVERRIDES.get(resourcelocation)).orElseGet(() -> {
               return ((Block)Registry.BLOCK.getOrDefault(resourcelocation)).getStateContainer();
            });
            this.containerHolder.setStateContainer(statecontainer);
            List<IProperty<?>> list = ImmutableList.copyOf(this.field_225365_D.func_225310_a((Block)statecontainer.getOwner()));
            ImmutableList<BlockState> immutablelist = statecontainer.getValidStates();
            Map<ModelResourceLocation, BlockState> map = Maps.newHashMap();
            immutablelist.forEach((p_lambda$loadBlockstate$15_2_) -> {
               BlockState blockstate = (BlockState)map.put(BlockModelShapes.getModelLocation(resourcelocation, p_lambda$loadBlockstate$15_2_), p_lambda$loadBlockstate$15_2_);
            });
            Map<BlockState, Pair<IUnbakedModel, Supplier<ModelBakery.ModelListWrapper>>> map1 = Maps.newHashMap();
            ResourceLocation resourcelocation1 = new ResourceLocation(p_209598_1_.getNamespace(), "blockstates/" + p_209598_1_.getPath() + ".json");
            IUnbakedModel iunbakedmodel = (IUnbakedModel)this.field_217849_F.get(MODEL_MISSING);
            ModelBakery.ModelListWrapper modelbakery$modellistwrapper = new ModelBakery.ModelListWrapper(ImmutableList.of(iunbakedmodel), ImmutableList.of());
            Pair pair = Pair.of(iunbakedmodel, () -> {
               return modelbakery$modellistwrapper;
            });
            boolean var25 = false;

            label98: {
               try {
                  label112: {
                     List list1;
                     try {
                        var25 = true;
                        list1 = (List)this.resourceManager.getAllResources(resourcelocation1).stream().map((p_lambda$loadBlockstate$17_1_) -> {
                           try {
                              InputStream inputstream = p_lambda$loadBlockstate$17_1_.getInputStream();
                              Throwable var3 = null;

                              Pair var5;
                              try {
                                 Pair<String, BlockModelDefinition> pair2 = Pair.of(p_lambda$loadBlockstate$17_1_.getPackName(), BlockModelDefinition.fromJson(this.containerHolder, new InputStreamReader(inputstream, StandardCharsets.UTF_8)));
                                 var5 = pair2;
                              } catch (Throwable var15) {
                                 var3 = var15;
                                 throw var15;
                              } finally {
                                 if (inputstream != null) {
                                    if (var3 != null) {
                                       try {
                                          inputstream.close();
                                       } catch (Throwable var14) {
                                          var3.addSuppressed(var14);
                                       }
                                    } else {
                                       inputstream.close();
                                    }
                                 }

                              }

                              return var5;
                           } catch (Exception var17) {
                              throw new ModelBakery.BlockStateDefinitionException(String.format("Exception loading blockstate definition: '%s' in resourcepack: '%s': %s", p_lambda$loadBlockstate$17_1_.getLocation(), p_lambda$loadBlockstate$17_1_.getPackName(), var17.getMessage()));
                           }
                        }).collect(Collectors.toList());
                     } catch (IOException var26) {
                        LOGGER.warn("Exception loading blockstate definition: {}: {}", resourcelocation1, var26);
                        var25 = false;
                        break label112;
                     }

                     Iterator var14 = list1.iterator();

                     while(var14.hasNext()) {
                        Pair<String, BlockModelDefinition> pair1 = (Pair)var14.next();
                        BlockModelDefinition blockmodeldefinition = (BlockModelDefinition)pair1.getSecond();
                        Map<BlockState, Pair<IUnbakedModel, Supplier<ModelBakery.ModelListWrapper>>> map2 = Maps.newIdentityHashMap();
                        Multipart multipart;
                        if (blockmodeldefinition.hasMultipartData()) {
                           multipart = blockmodeldefinition.getMultipartData();
                           immutablelist.forEach((p_lambda$loadBlockstate$19_3_) -> {
                              Pair pair2 = (Pair)map2.put(p_lambda$loadBlockstate$19_3_, Pair.of(multipart, () -> {
                                 return ModelBakery.ModelListWrapper.func_225335_a(p_lambda$loadBlockstate$19_3_, multipart, list);
                              }));
                           });
                        } else {
                           multipart = null;
                        }

                        blockmodeldefinition.getVariants().forEach((p_lambda$loadBlockstate$23_9_, p_lambda$loadBlockstate$23_10_) -> {
                           try {
                              immutablelist.stream().filter(parseVariantKey(statecontainer, p_lambda$loadBlockstate$23_9_)).forEach((p_lambda$null$22_6_) -> {
                                 Pair<IUnbakedModel, Supplier<ModelBakery.ModelListWrapper>> pair2 = (Pair)map2.put(p_lambda$null$22_6_, Pair.of(p_lambda$loadBlockstate$23_10_, () -> {
                                    return ModelBakery.ModelListWrapper.func_225336_a(p_lambda$null$22_6_, p_lambda$loadBlockstate$23_10_, list);
                                 }));
                                 if (pair2 != null && pair2.getFirst() != multipart) {
                                    map2.put(p_lambda$null$22_6_, pair);
                                    throw new RuntimeException("Overlapping definition with: " + (String)((Entry)blockmodeldefinition.getVariants().entrySet().stream().filter((p_lambda$null$21_1_) -> {
                                       return p_lambda$null$21_1_.getValue() == pair2.getFirst();
                                    }).findFirst().get()).getKey());
                                 }
                              });
                           } catch (Exception var12) {
                              LOGGER.warn("Exception loading blockstate definition: '{}' in resourcepack: '{}' for variant: '{}': {}", resourcelocation1, pair1.getFirst(), p_lambda$loadBlockstate$23_9_, var12.getMessage());
                           }

                        });
                        map1.putAll(map2);
                     }

                     var25 = false;
                     break label98;
                  }
               } catch (ModelBakery.BlockStateDefinitionException var27) {
                  throw var27;
               } catch (Exception var28) {
                  throw new ModelBakery.BlockStateDefinitionException(String.format("Exception loading blockstate definition: '%s': %s", resourcelocation1, var28));
               } finally {
                  if (var25) {
                     HashMap lvt_20_1_ = Maps.newHashMap();
                     map.forEach((p_lambda$loadBlockstate$25_5_, p_lambda$loadBlockstate$25_6_) -> {
                        Pair<IUnbakedModel, Supplier<ModelBakery.ModelListWrapper>> pair2 = (Pair)map1.get(p_lambda$loadBlockstate$25_6_);
                        if (pair2 == null) {
                           LOGGER.warn("Exception loading blockstate definition: '{}' missing model for variant: '{}'", resourcelocation1, p_lambda$loadBlockstate$25_5_);
                           pair2 = pair;
                        }

                        this.putModel(p_lambda$loadBlockstate$25_5_, (IUnbakedModel)pair2.getFirst());

                        try {
                           ModelBakery.ModelListWrapper modelbakery$modellistwrapper1 = (ModelBakery.ModelListWrapper)((Supplier)pair2.getSecond()).get();
                           ((Set)lvt_20_1_.computeIfAbsent(modelbakery$modellistwrapper1, (p_lambda$null$24_0_) -> {
                              return Sets.newIdentityHashSet();
                           })).add(p_lambda$loadBlockstate$25_6_);
                        } catch (Exception var9) {
                           LOGGER.warn("Exception evaluating model definition: '{}'", p_lambda$loadBlockstate$25_5_, var9);
                        }

                     });
                     lvt_20_1_.forEach((p_lambda$loadBlockstate$26_1_, p_lambda$loadBlockstate$26_2_) -> {
                        Iterator iterator = p_lambda$loadBlockstate$26_2_.iterator();

                        while(iterator.hasNext()) {
                           BlockState blockstate = (BlockState)iterator.next();
                           if (blockstate.getRenderType() != BlockRenderType.MODEL) {
                              iterator.remove();
                              this.field_225367_M.put(blockstate, 0);
                           }
                        }

                        if (p_lambda$loadBlockstate$26_2_.size() > 1) {
                           this.func_225352_a(p_lambda$loadBlockstate$26_2_);
                        }

                     });
                  }
               }

               HashMap<ModelBakery.ModelListWrapper, Set<BlockState>> lvt_20_1_ = Maps.newHashMap();
               map.forEach((p_lambda$loadBlockstate$25_5_, p_lambda$loadBlockstate$25_6_) -> {
                  Pair<IUnbakedModel, Supplier<ModelBakery.ModelListWrapper>> pair2 = (Pair)map1.get(p_lambda$loadBlockstate$25_6_);
                  if (pair2 == null) {
                     LOGGER.warn("Exception loading blockstate definition: '{}' missing model for variant: '{}'", resourcelocation1, p_lambda$loadBlockstate$25_5_);
                     pair2 = pair;
                  }

                  this.putModel(p_lambda$loadBlockstate$25_5_, (IUnbakedModel)pair2.getFirst());

                  try {
                     ModelBakery.ModelListWrapper modelbakery$modellistwrapper1 = (ModelBakery.ModelListWrapper)((Supplier)pair2.getSecond()).get();
                     ((Set)lvt_20_1_.computeIfAbsent(modelbakery$modellistwrapper1, (p_lambda$null$24_0_) -> {
                        return Sets.newIdentityHashSet();
                     })).add(p_lambda$loadBlockstate$25_6_);
                  } catch (Exception var9) {
                     LOGGER.warn("Exception evaluating model definition: '{}'", p_lambda$loadBlockstate$25_5_, var9);
                  }

               });
               lvt_20_1_.forEach((p_lambda$loadBlockstate$26_1_, p_lambda$loadBlockstate$26_2_) -> {
                  Iterator iterator = p_lambda$loadBlockstate$26_2_.iterator();

                  while(iterator.hasNext()) {
                     BlockState blockstate = (BlockState)iterator.next();
                     if (blockstate.getRenderType() != BlockRenderType.MODEL) {
                        iterator.remove();
                        this.field_225367_M.put(blockstate, 0);
                     }
                  }

                  if (p_lambda$loadBlockstate$26_2_.size() > 1) {
                     this.func_225352_a(p_lambda$loadBlockstate$26_2_);
                  }

               });
               return;
            }

            HashMap<ModelBakery.ModelListWrapper, Set<BlockState>> lvt_20_1_ = Maps.newHashMap();
            map.forEach((p_lambda$loadBlockstate$25_5_, p_lambda$loadBlockstate$25_6_) -> {
               Pair<IUnbakedModel, Supplier<ModelBakery.ModelListWrapper>> pair2 = (Pair)map1.get(p_lambda$loadBlockstate$25_6_);
               if (pair2 == null) {
                  LOGGER.warn("Exception loading blockstate definition: '{}' missing model for variant: '{}'", resourcelocation1, p_lambda$loadBlockstate$25_5_);
                  pair2 = pair;
               }

               this.putModel(p_lambda$loadBlockstate$25_5_, (IUnbakedModel)pair2.getFirst());

               try {
                  ModelBakery.ModelListWrapper modelbakery$modellistwrapper1 = (ModelBakery.ModelListWrapper)((Supplier)pair2.getSecond()).get();
                  ((Set)lvt_20_1_.computeIfAbsent(modelbakery$modellistwrapper1, (p_lambda$null$24_0_) -> {
                     return Sets.newIdentityHashSet();
                  })).add(p_lambda$loadBlockstate$25_6_);
               } catch (Exception var9) {
                  LOGGER.warn("Exception evaluating model definition: '{}'", p_lambda$loadBlockstate$25_5_, var9);
               }

            });
            lvt_20_1_.forEach((p_lambda$loadBlockstate$26_1_, p_lambda$loadBlockstate$26_2_) -> {
               Iterator iterator = p_lambda$loadBlockstate$26_2_.iterator();

               while(iterator.hasNext()) {
                  BlockState blockstate = (BlockState)iterator.next();
                  if (blockstate.getRenderType() != BlockRenderType.MODEL) {
                     iterator.remove();
                     this.field_225367_M.put(blockstate, 0);
                  }
               }

               if (p_lambda$loadBlockstate$26_2_.size() > 1) {
                  this.func_225352_a(p_lambda$loadBlockstate$26_2_);
               }

            });
         }
      }

   }

   private void putModel(ResourceLocation p_209593_1_, IUnbakedModel p_209593_2_) {
      this.field_217849_F.put(p_209593_1_, p_209593_2_);
      this.field_217848_D.addAll(p_209593_2_.getDependencies());
   }

   private void addModelToCache(ResourceLocation p_addModelToCache_1_) {
      IUnbakedModel iunbakedmodel = this.getUnbakedModel(p_addModelToCache_1_);
      this.field_217849_F.put(p_addModelToCache_1_, iunbakedmodel);
      this.field_217851_H.put(p_addModelToCache_1_, iunbakedmodel);
   }

   private void func_217843_a(ModelResourceLocation p_217843_1_) {
      IUnbakedModel iunbakedmodel = this.getUnbakedModel(p_217843_1_);
      this.field_217849_F.put(p_217843_1_, iunbakedmodel);
      this.field_217851_H.put(p_217843_1_, iunbakedmodel);
   }

   private void func_225352_a(Iterable<BlockState> p_225352_1_) {
      int i = this.field_225366_L++;
      p_225352_1_.forEach((p_lambda$func_225352_a$27_2_) -> {
         this.field_225367_M.put(p_lambda$func_225352_a$27_2_, i);
      });
   }

   /** @deprecated */
   @Nullable
   @Deprecated
   public IBakedModel func_217845_a(ResourceLocation p_217845_1_, IModelTransform p_217845_2_) {
      SpriteMap var10003 = this.field_229322_z_;
      var10003.getClass();
      return this.getBakedModel(p_217845_1_, p_217845_2_, var10003::func_229151_a_);
   }

   @Nullable
   public IBakedModel getBakedModel(ResourceLocation p_getBakedModel_1_, IModelTransform p_getBakedModel_2_, Function<Material, TextureAtlasSprite> p_getBakedModel_3_) {
      Triple<ResourceLocation, TransformationMatrix, Boolean> triple = Triple.of(p_getBakedModel_1_, p_getBakedModel_2_.func_225615_b_(), p_getBakedModel_2_.isUvLock());
      if (this.field_217850_G.containsKey(triple)) {
         return (IBakedModel)this.field_217850_G.get(triple);
      } else if (this.field_229322_z_ == null) {
         throw new IllegalStateException("bake called too early");
      } else {
         IUnbakedModel iunbakedmodel = this.getUnbakedModel(p_getBakedModel_1_);
         if (iunbakedmodel instanceof BlockModel) {
            BlockModel blockmodel = (BlockModel)iunbakedmodel;
            if (blockmodel.getRootModel() == MODEL_GENERATED) {
               return field_217854_z.makeItemModel(p_getBakedModel_3_, blockmodel).func_228813_a_(this, blockmodel, this.field_229322_z_::func_229151_a_, p_getBakedModel_2_, p_getBakedModel_1_, false);
            }
         }

         IBakedModel ibakedmodel = iunbakedmodel.func_225613_a_(this, p_getBakedModel_3_, p_getBakedModel_2_, p_getBakedModel_1_);
         this.field_217850_G.put(triple, ibakedmodel);
         return ibakedmodel;
      }
   }

   protected BlockModel loadModel(ResourceLocation p_177594_1_) throws IOException {
      Reader reader = null;
      IResource iresource = null;

      BlockModel var6;
      try {
         String s = p_177594_1_.getPath();
         BlockModel lvt_5_2_;
         if ("builtin/generated".equals(s)) {
            lvt_5_2_ = MODEL_GENERATED;
            return lvt_5_2_;
         }

         if (!"builtin/entity".equals(s)) {
            if (s.startsWith("builtin/")) {
               String s2 = s.substring("builtin/".length());
               String s1 = (String)BUILT_IN_MODELS.get(s2);
               if (s1 == null) {
                  throw new FileNotFoundException(p_177594_1_.toString());
               }

               reader = new StringReader(s1);
            } else {
               iresource = this.resourceManager.getResource(new ResourceLocation(p_177594_1_.getNamespace(), "models/" + p_177594_1_.getPath() + ".json"));
               reader = new InputStreamReader(iresource.getInputStream(), StandardCharsets.UTF_8);
            }

            lvt_5_2_ = BlockModel.deserialize((Reader)reader);
            lvt_5_2_.name = p_177594_1_.toString();
            var6 = lvt_5_2_;
            return var6;
         }

         lvt_5_2_ = MODEL_ENTITY;
         var6 = lvt_5_2_;
      } finally {
         IOUtils.closeQuietly((Reader)reader);
         IOUtils.closeQuietly(iresource);
      }

      return var6;
   }

   public Map<ResourceLocation, IBakedModel> func_217846_a() {
      return this.field_217852_I;
   }

   public Object2IntMap<BlockState> func_225354_b() {
      return this.field_225367_M;
   }

   public Set<ResourceLocation> getSpecialModels() {
      return Collections.emptySet();
   }

   public SpriteMap getSpriteMap() {
      return this.field_229322_z_;
   }

   static {
      LOCATION_FIRE_0 = new Material(AtlasTexture.LOCATION_BLOCKS_TEXTURE, new ResourceLocation("block/fire_0"));
      LOCATION_FIRE_1 = new Material(AtlasTexture.LOCATION_BLOCKS_TEXTURE, new ResourceLocation("block/fire_1"));
      LOCATION_LAVA_FLOW = new Material(AtlasTexture.LOCATION_BLOCKS_TEXTURE, new ResourceLocation("block/lava_flow"));
      LOCATION_WATER_FLOW = new Material(AtlasTexture.LOCATION_BLOCKS_TEXTURE, new ResourceLocation("block/water_flow"));
      LOCATION_WATER_OVERLAY = new Material(AtlasTexture.LOCATION_BLOCKS_TEXTURE, new ResourceLocation("block/water_overlay"));
      field_229315_f_ = new Material(AtlasTexture.LOCATION_BLOCKS_TEXTURE, new ResourceLocation("entity/banner_base"));
      field_229316_g_ = new Material(AtlasTexture.LOCATION_BLOCKS_TEXTURE, new ResourceLocation("entity/shield_base"));
      field_229317_h_ = new Material(AtlasTexture.LOCATION_BLOCKS_TEXTURE, new ResourceLocation("entity/shield_base_nopattern"));
      field_229318_i_ = (List)IntStream.range(0, 10).mapToObj((p_lambda$static$0_0_) -> {
         return new ResourceLocation("block/destroy_stage_" + p_lambda$static$0_0_);
      }).collect(Collectors.toList());
      field_229319_j_ = (List)field_229318_i_.stream().map((p_lambda$static$1_0_) -> {
         return new ResourceLocation("textures/" + p_lambda$static$1_0_.getPath() + ".png");
      }).collect(Collectors.toList());
      field_229320_k_ = (List)field_229319_j_.stream().map(RenderType::func_228656_k_).collect(Collectors.toList());
      LOCATIONS_BUILTIN_TEXTURES = (Set)Util.make(Sets.newHashSet(), (p_lambda$static$2_0_) -> {
         p_lambda$static$2_0_.add(LOCATION_WATER_FLOW);
         p_lambda$static$2_0_.add(LOCATION_LAVA_FLOW);
         p_lambda$static$2_0_.add(LOCATION_WATER_OVERLAY);
         p_lambda$static$2_0_.add(LOCATION_FIRE_0);
         p_lambda$static$2_0_.add(LOCATION_FIRE_1);
         p_lambda$static$2_0_.add(BellTileEntityRenderer.field_217653_c);
         p_lambda$static$2_0_.add(ConduitTileEntityRenderer.BASE_TEXTURE);
         p_lambda$static$2_0_.add(ConduitTileEntityRenderer.CAGE_TEXTURE);
         p_lambda$static$2_0_.add(ConduitTileEntityRenderer.WIND_TEXTURE);
         p_lambda$static$2_0_.add(ConduitTileEntityRenderer.VERTICAL_WIND_TEXTURE);
         p_lambda$static$2_0_.add(ConduitTileEntityRenderer.OPEN_EYE_TEXTURE);
         p_lambda$static$2_0_.add(ConduitTileEntityRenderer.CLOSED_EYE_TEXTURE);
         p_lambda$static$2_0_.add(EnchantmentTableTileEntityRenderer.TEXTURE_BOOK);
         p_lambda$static$2_0_.add(field_229315_f_);
         p_lambda$static$2_0_.add(field_229316_g_);
         p_lambda$static$2_0_.add(field_229317_h_);
         Iterator var1 = field_229318_i_.iterator();

         while(var1.hasNext()) {
            ResourceLocation resourcelocation = (ResourceLocation)var1.next();
            p_lambda$static$2_0_.add(new Material(AtlasTexture.LOCATION_BLOCKS_TEXTURE, resourcelocation));
         }

         p_lambda$static$2_0_.add(new Material(AtlasTexture.LOCATION_BLOCKS_TEXTURE, PlayerContainer.field_226616_d_));
         p_lambda$static$2_0_.add(new Material(AtlasTexture.LOCATION_BLOCKS_TEXTURE, PlayerContainer.field_226617_e_));
         p_lambda$static$2_0_.add(new Material(AtlasTexture.LOCATION_BLOCKS_TEXTURE, PlayerContainer.field_226618_f_));
         p_lambda$static$2_0_.add(new Material(AtlasTexture.LOCATION_BLOCKS_TEXTURE, PlayerContainer.field_226619_g_));
         p_lambda$static$2_0_.add(new Material(AtlasTexture.LOCATION_BLOCKS_TEXTURE, PlayerContainer.field_226620_h_));
         Atlases.func_228775_a_(p_lambda$static$2_0_::add);
      });
      LOGGER = LogManager.getLogger();
      MODEL_MISSING = new ModelResourceLocation("builtin/missing", "missing");
      field_229321_r_ = MODEL_MISSING.toString();
      MISSING_MODEL_MESH = ("{    'textures': {       'particle': '" + MissingTextureSprite.getLocation().getPath() + "',       'missingno': '" + MissingTextureSprite.getLocation().getPath() + "'    },    'elements': [         {  'from': [ 0, 0, 0 ],            'to': [ 16, 16, 16 ],            'faces': {                'down':  { 'uv': [ 0, 0, 16, 16 ], 'cullface': 'down',  'texture': '#missingno' },                'up':    { 'uv': [ 0, 0, 16, 16 ], 'cullface': 'up',    'texture': '#missingno' },                'north': { 'uv': [ 0, 0, 16, 16 ], 'cullface': 'north', 'texture': '#missingno' },                'south': { 'uv': [ 0, 0, 16, 16 ], 'cullface': 'south', 'texture': '#missingno' },                'west':  { 'uv': [ 0, 0, 16, 16 ], 'cullface': 'west',  'texture': '#missingno' },                'east':  { 'uv': [ 0, 0, 16, 16 ], 'cullface': 'east',  'texture': '#missingno' }            }        }    ]}").replace('\'', '"');
      BUILT_IN_MODELS = Maps.newHashMap(ImmutableMap.of("missing", MISSING_MODEL_MESH));
      SPLITTER_COMMA = Splitter.on(',');
      EQUALS_SPLITTER = Splitter.on('=').limit(2);
      MODEL_GENERATED = (BlockModel)Util.make(BlockModel.deserialize("{\"gui_light\": \"front\"}"), (p_lambda$static$3_0_) -> {
         p_lambda$static$3_0_.name = "generation marker";
      });
      MODEL_ENTITY = (BlockModel)Util.make(BlockModel.deserialize("{\"gui_light\": \"side\"}"), (p_lambda$static$4_0_) -> {
         p_lambda$static$4_0_.name = "block entity marker";
      });
      STATE_CONTAINER_ITEM_FRAME = (new StateContainer.Builder(Blocks.AIR)).add(BooleanProperty.create("map")).create(BlockState::new);
      field_217854_z = new ItemModelGenerator();
      STATE_CONTAINER_OVERRIDES = ImmutableMap.of(new ResourceLocation("item_frame"), STATE_CONTAINER_ITEM_FRAME);
   }

   @OnlyIn(Dist.CLIENT)
   static class ModelListWrapper {
      private final List<IUnbakedModel> field_225339_a;
      private final List<Object> field_225340_b;

      public ModelListWrapper(List<IUnbakedModel> p_i51613_1_, List<Object> p_i51613_2_) {
         this.field_225339_a = p_i51613_1_;
         this.field_225340_b = p_i51613_2_;
      }

      public boolean equals(Object p_equals_1_) {
         if (this == p_equals_1_) {
            return true;
         } else if (!(p_equals_1_ instanceof ModelBakery.ModelListWrapper)) {
            return false;
         } else {
            ModelBakery.ModelListWrapper modelbakery$modellistwrapper = (ModelBakery.ModelListWrapper)p_equals_1_;
            return Objects.equals(this.field_225339_a, modelbakery$modellistwrapper.field_225339_a) && Objects.equals(this.field_225340_b, modelbakery$modellistwrapper.field_225340_b);
         }
      }

      public int hashCode() {
         return 31 * this.field_225339_a.hashCode() + this.field_225340_b.hashCode();
      }

      public static ModelBakery.ModelListWrapper func_225335_a(BlockState p_225335_0_, Multipart p_225335_1_, Collection<IProperty<?>> p_225335_2_) {
         StateContainer<Block, BlockState> statecontainer = p_225335_0_.getBlock().getStateContainer();
         List<IUnbakedModel> list = (List)p_225335_1_.getSelectors().stream().filter((p_lambda$func_225335_a$0_2_) -> {
            return p_lambda$func_225335_a$0_2_.getPredicate(statecontainer).test(p_225335_0_);
         }).map(Selector::getVariantList).collect(ImmutableList.toImmutableList());
         List<Object> list1 = func_225337_a(p_225335_0_, p_225335_2_);
         return new ModelBakery.ModelListWrapper(list, list1);
      }

      public static ModelBakery.ModelListWrapper func_225336_a(BlockState p_225336_0_, IUnbakedModel p_225336_1_, Collection<IProperty<?>> p_225336_2_) {
         List<Object> list = func_225337_a(p_225336_0_, p_225336_2_);
         return new ModelBakery.ModelListWrapper(ImmutableList.of(p_225336_1_), list);
      }

      private static List<Object> func_225337_a(BlockState p_225337_0_, Collection<IProperty<?>> p_225337_1_) {
         Stream var10000 = p_225337_1_.stream();
         p_225337_0_.getClass();
         return (List)var10000.map(p_225337_0_::get).collect(ImmutableList.toImmutableList());
      }
   }

   @OnlyIn(Dist.CLIENT)
   static class BlockStateDefinitionException extends RuntimeException {
      public BlockStateDefinitionException(String p_i49526_1_) {
         super(p_i49526_1_);
      }
   }
}
