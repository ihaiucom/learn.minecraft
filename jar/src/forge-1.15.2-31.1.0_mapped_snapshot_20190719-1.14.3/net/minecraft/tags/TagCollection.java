package net.minecraft.tags;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.resources.IResource;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TagCollection<T> {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final Gson GSON = new Gson();
   private static final int JSON_EXTENSION_LENGTH = ".json".length();
   private Map<ResourceLocation, Tag<T>> tagMap = ImmutableMap.of();
   private final Function<ResourceLocation, Optional<T>> resourceLocationToItem;
   private final String resourceLocationPrefix;
   private final boolean preserveOrder;
   private final String itemTypeName;

   public TagCollection(Function<ResourceLocation, Optional<T>> p_i50686_1_, String p_i50686_2_, boolean p_i50686_3_, String p_i50686_4_) {
      this.resourceLocationToItem = p_i50686_1_;
      this.resourceLocationPrefix = p_i50686_2_;
      this.preserveOrder = p_i50686_3_;
      this.itemTypeName = p_i50686_4_;
   }

   @Nullable
   public Tag<T> get(ResourceLocation p_199910_1_) {
      return (Tag)this.tagMap.get(p_199910_1_);
   }

   public Tag<T> getOrCreate(ResourceLocation p_199915_1_) {
      Tag<T> tag = (Tag)this.tagMap.get(p_199915_1_);
      return tag == null ? new Tag(p_199915_1_) : tag;
   }

   public Collection<ResourceLocation> getRegisteredTags() {
      return this.tagMap.keySet();
   }

   public Collection<ResourceLocation> getOwningTags(T p_199913_1_) {
      List<ResourceLocation> list = Lists.newArrayList();
      Iterator var3 = this.tagMap.entrySet().iterator();

      while(var3.hasNext()) {
         Entry<ResourceLocation, Tag<T>> entry = (Entry)var3.next();
         if (((Tag)entry.getValue()).contains(p_199913_1_)) {
            list.add(entry.getKey());
         }
      }

      return list;
   }

   public CompletableFuture<Map<ResourceLocation, Tag.Builder<T>>> reload(IResourceManager p_219781_1_, Executor p_219781_2_) {
      return CompletableFuture.supplyAsync(() -> {
         Map<ResourceLocation, Tag.Builder<T>> map = Maps.newHashMap();
         Iterator var3 = p_219781_1_.getAllResourceLocations(this.resourceLocationPrefix, (p_lambda$null$0_0_) -> {
            return p_lambda$null$0_0_.endsWith(".json");
         }).iterator();

         while(var3.hasNext()) {
            ResourceLocation resourcelocation = (ResourceLocation)var3.next();
            String s = resourcelocation.getPath();
            ResourceLocation resourcelocation1 = new ResourceLocation(resourcelocation.getNamespace(), s.substring(this.resourceLocationPrefix.length() + 1, s.length() - JSON_EXTENSION_LENGTH));

            try {
               Iterator var7 = p_219781_1_.getAllResources(resourcelocation).iterator();

               while(var7.hasNext()) {
                  IResource iresource = (IResource)var7.next();

                  try {
                     InputStream inputstream = iresource.getInputStream();
                     Throwable var10 = null;

                     try {
                        Reader reader = new BufferedReader(new InputStreamReader(inputstream, StandardCharsets.UTF_8));
                        Throwable var12 = null;

                        try {
                           JsonObject jsonobject = (JsonObject)JSONUtils.fromJson(GSON, (Reader)reader, (Class)JsonObject.class);
                           if (jsonobject == null) {
                              LOGGER.error("Couldn't load {} tag list {} from {} in data pack {} as it's empty or null", this.itemTypeName, resourcelocation1, resourcelocation, iresource.getPackName());
                           } else {
                              ((Tag.Builder)map.computeIfAbsent(resourcelocation1, (p_lambda$null$2_1_) -> {
                                 return (Tag.Builder)Util.make(Tag.Builder.create(), (p_lambda$null$1_1_) -> {
                                    p_lambda$null$1_1_.ordered(this.preserveOrder);
                                 });
                              })).fromJson(this.resourceLocationToItem, jsonobject);
                           }
                        } catch (Throwable var53) {
                           var12 = var53;
                           throw var53;
                        } finally {
                           if (reader != null) {
                              if (var12 != null) {
                                 try {
                                    reader.close();
                                 } catch (Throwable var52) {
                                    var12.addSuppressed(var52);
                                 }
                              } else {
                                 reader.close();
                              }
                           }

                        }
                     } catch (Throwable var55) {
                        var10 = var55;
                        throw var55;
                     } finally {
                        if (inputstream != null) {
                           if (var10 != null) {
                              try {
                                 inputstream.close();
                              } catch (Throwable var51) {
                                 var10.addSuppressed(var51);
                              }
                           } else {
                              inputstream.close();
                           }
                        }

                     }
                  } catch (IOException | RuntimeException var57) {
                     LOGGER.error("Couldn't read {} tag list {} from {} in data pack {}", this.itemTypeName, resourcelocation1, resourcelocation, iresource.getPackName(), var57);
                  } finally {
                     IOUtils.closeQuietly(iresource);
                  }
               }
            } catch (IOException var59) {
               LOGGER.error("Couldn't read {} tag list {} from {}", this.itemTypeName, resourcelocation1, resourcelocation, var59);
            }
         }

         return map;
      }, p_219781_2_);
   }

   public void registerAll(Map<ResourceLocation, Tag.Builder<T>> p_219779_1_) {
      HashMap map = Maps.newHashMap();

      while(!p_219779_1_.isEmpty()) {
         boolean flag = false;
         Iterator iterator = p_219779_1_.entrySet().iterator();

         while(iterator.hasNext()) {
            Entry<ResourceLocation, Tag.Builder<T>> entry = (Entry)iterator.next();
            Tag.Builder<T> builder = (Tag.Builder)entry.getValue();
            map.getClass();
            if (builder.resolve(map::get)) {
               flag = true;
               ResourceLocation resourcelocation = (ResourceLocation)entry.getKey();
               map.put(resourcelocation, builder.build(resourcelocation));
               iterator.remove();
            }
         }

         if (!flag) {
            p_219779_1_.forEach((p_lambda$registerAll$4_1_, p_lambda$registerAll$4_2_) -> {
               LOGGER.error("Couldn't load {} tag {} as it either references another tag that doesn't exist, or ultimately references itself", this.itemTypeName, p_lambda$registerAll$4_1_);
            });
            break;
         }
      }

      p_219779_1_.forEach((p_lambda$registerAll$5_1_, p_lambda$registerAll$5_2_) -> {
         Tag tag = (Tag)map.put(p_lambda$registerAll$5_1_, p_lambda$registerAll$5_2_.build(p_lambda$registerAll$5_1_));
      });
      this.func_223507_b(map);
   }

   protected void func_223507_b(Map<ResourceLocation, Tag<T>> p_223507_1_) {
      this.tagMap = ImmutableMap.copyOf(p_223507_1_);
   }

   public Map<ResourceLocation, Tag<T>> getTagMap() {
      return this.tagMap;
   }
}
