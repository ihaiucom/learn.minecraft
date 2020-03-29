package net.minecraft.data;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagCollection;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class TagsProvider<T> implements IDataProvider {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().create();
   protected final DataGenerator generator;
   protected final Registry<T> registry;
   protected final Map<Tag<T>, Tag.Builder<T>> tagToBuilder = Maps.newLinkedHashMap();

   protected TagsProvider(DataGenerator p_i49827_1_, Registry<T> p_i49827_2_) {
      this.generator = p_i49827_1_;
      this.registry = p_i49827_2_;
   }

   protected abstract void registerTags();

   public void act(DirectoryCache p_200398_1_) {
      this.tagToBuilder.clear();
      this.registerTags();
      TagCollection<T> tagcollection = new TagCollection((p_lambda$act$0_0_) -> {
         return Optional.empty();
      }, "", false, "generated");
      Map<ResourceLocation, Tag.Builder<T>> map = (Map)this.tagToBuilder.entrySet().stream().collect(Collectors.toMap((p_lambda$act$1_0_) -> {
         return ((Tag)p_lambda$act$1_0_.getKey()).getId();
      }, Entry::getValue));
      tagcollection.registerAll(map);
      tagcollection.getTagMap().forEach((p_lambda$act$2_2_, p_lambda$act$2_3_) -> {
         Registry var10001 = this.registry;
         var10001.getClass();
         JsonObject jsonobject = p_lambda$act$2_3_.serialize(var10001::getKey);
         Path path = this.makePath(p_lambda$act$2_2_);
         if (path != null) {
            try {
               String s = GSON.toJson(jsonobject);
               String s1 = HASH_FUNCTION.hashUnencodedChars(s).toString();
               if (!Objects.equals(p_200398_1_.getPreviousHash(path), s1) || !Files.exists(path, new LinkOption[0])) {
                  Files.createDirectories(path.getParent());
                  BufferedWriter bufferedwriter = Files.newBufferedWriter(path);
                  Throwable var9 = null;

                  try {
                     bufferedwriter.write(s);
                  } catch (Throwable var19) {
                     var9 = var19;
                     throw var19;
                  } finally {
                     if (bufferedwriter != null) {
                        if (var9 != null) {
                           try {
                              bufferedwriter.close();
                           } catch (Throwable var18) {
                              var9.addSuppressed(var18);
                           }
                        } else {
                           bufferedwriter.close();
                        }
                     }

                  }
               }

               p_200398_1_.func_208316_a(path, s1);
            } catch (IOException var21) {
               LOGGER.error("Couldn't save tags to {}", path, var21);
            }

         }
      });
      this.setCollection(tagcollection);
   }

   protected abstract void setCollection(TagCollection<T> var1);

   protected abstract Path makePath(ResourceLocation var1);

   protected Tag.Builder<T> getBuilder(Tag<T> p_200426_1_) {
      return (Tag.Builder)this.tagToBuilder.computeIfAbsent(p_200426_1_, (p_lambda$getBuilder$3_0_) -> {
         return Tag.Builder.create();
      });
   }
}
