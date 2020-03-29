package net.minecraft.tags;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraftforge.common.ForgeHooks;

public class Tag<T> {
   private final ResourceLocation resourceLocation;
   private final Set<T> taggedItems;
   private final Collection<Tag.ITagEntry<T>> entries;
   private boolean replace;

   public Tag(ResourceLocation p_i48236_1_) {
      this.replace = false;
      this.resourceLocation = p_i48236_1_;
      this.taggedItems = Collections.emptySet();
      this.entries = Collections.emptyList();
   }

   public Tag(ResourceLocation p_i48224_1_, Collection<Tag.ITagEntry<T>> p_i48224_2_, boolean p_i48224_3_) {
      this(p_i48224_1_, p_i48224_2_, p_i48224_3_, false);
   }

   private Tag(ResourceLocation p_i230077_1_, Collection<Tag.ITagEntry<T>> p_i230077_2_, boolean p_i230077_3_, boolean p_i230077_4_) {
      this.replace = false;
      this.resourceLocation = p_i230077_1_;
      this.taggedItems = (Set)(p_i230077_3_ ? Sets.newLinkedHashSet() : Sets.newHashSet());
      this.entries = p_i230077_2_;
      Iterator var5 = p_i230077_2_.iterator();

      while(var5.hasNext()) {
         Tag.ITagEntry<T> itagentry = (Tag.ITagEntry)var5.next();
         itagentry.populate(this.taggedItems);
      }

   }

   public JsonObject serialize(Function<T, ResourceLocation> p_200571_1_) {
      JsonObject jsonobject = new JsonObject();
      JsonArray jsonarray = new JsonArray();
      Iterator var4 = this.entries.iterator();

      while(var4.hasNext()) {
         Tag.ITagEntry<T> itagentry = (Tag.ITagEntry)var4.next();
         itagentry.serialize(jsonarray, p_200571_1_);
      }

      jsonobject.addProperty("replace", this.replace);
      jsonobject.add("values", jsonarray);
      return jsonobject;
   }

   public boolean contains(T p_199685_1_) {
      return this.taggedItems.contains(p_199685_1_);
   }

   public Collection<T> getAllElements() {
      return this.taggedItems;
   }

   public Collection<Tag.ITagEntry<T>> getEntries() {
      return this.entries;
   }

   public T getRandomElement(Random p_205596_1_) {
      List<T> list = Lists.newArrayList(this.getAllElements());
      return list.get(p_205596_1_.nextInt(list.size()));
   }

   public ResourceLocation getId() {
      return this.resourceLocation;
   }

   // $FF: synthetic method
   Tag(ResourceLocation p_i230078_1_, Collection p_i230078_2_, boolean p_i230078_3_, boolean p_i230078_4_, Object p_i230078_5_) {
      this(p_i230078_1_, p_i230078_2_, p_i230078_3_, p_i230078_4_);
   }

   public static class TagEntry<T> implements Tag.ITagEntry<T> {
      @Nullable
      private final ResourceLocation id;
      @Nullable
      private Tag<T> tag;

      public TagEntry(ResourceLocation p_i48228_1_) {
         this.id = p_i48228_1_;
      }

      public TagEntry(Tag<T> p_i48229_1_) {
         this.id = p_i48229_1_.getId();
         this.tag = p_i48229_1_;
      }

      public boolean resolve(Function<ResourceLocation, Tag<T>> p_200161_1_) {
         if (this.tag == null) {
            this.tag = (Tag)p_200161_1_.apply(this.id);
         }

         return this.tag != null;
      }

      public void populate(Collection<T> p_200162_1_) {
         if (this.tag == null) {
            throw (IllegalStateException)Util.func_229757_c_(new IllegalStateException("Cannot build unresolved tag entry"));
         } else {
            p_200162_1_.addAll(this.tag.getAllElements());
         }
      }

      public ResourceLocation getSerializedId() {
         if (this.tag != null) {
            return this.tag.getId();
         } else if (this.id != null) {
            return this.id;
         } else {
            throw new IllegalStateException("Cannot serialize an anonymous tag to json!");
         }
      }

      public void serialize(JsonArray p_200576_1_, Function<T, ResourceLocation> p_200576_2_) {
         p_200576_1_.add("#" + this.getSerializedId());
      }

      public int hashCode() {
         return Objects.hashCode(this.id);
      }

      public boolean equals(Object p_equals_1_) {
         return p_equals_1_ == this || p_equals_1_ instanceof Tag.TagEntry && Objects.equals(this.id, ((Tag.TagEntry)p_equals_1_).id);
      }
   }

   public static class ListEntry<T> implements Tag.ITagEntry<T> {
      private final Collection<T> taggedItems;

      public ListEntry(Collection<T> p_i48227_1_) {
         this.taggedItems = p_i48227_1_;
      }

      public void populate(Collection<T> p_200162_1_) {
         p_200162_1_.addAll(this.taggedItems);
      }

      public void serialize(JsonArray p_200576_1_, Function<T, ResourceLocation> p_200576_2_) {
         Iterator var3 = this.taggedItems.iterator();

         while(var3.hasNext()) {
            T t = var3.next();
            ResourceLocation resourcelocation = (ResourceLocation)p_200576_2_.apply(t);
            if (resourcelocation == null) {
               throw new IllegalStateException("Unable to serialize an anonymous value to json!");
            }

            p_200576_1_.add(resourcelocation.toString());
         }

      }

      public Collection<T> getTaggedItems() {
         return this.taggedItems;
      }

      public int hashCode() {
         return this.taggedItems.hashCode();
      }

      public boolean equals(Object p_equals_1_) {
         return p_equals_1_ == this || p_equals_1_ instanceof Tag.ListEntry && this.taggedItems.equals(((Tag.ListEntry)p_equals_1_).taggedItems);
      }
   }

   public interface ITagEntry<T> {
      default boolean resolve(Function<ResourceLocation, Tag<T>> p_200161_1_) {
         return true;
      }

      void populate(Collection<T> var1);

      void serialize(JsonArray var1, Function<T, ResourceLocation> var2);
   }

   public static class Builder<T> {
      private final Set<Tag.ITagEntry<T>> entries = Sets.newLinkedHashSet();
      private boolean preserveOrder;
      private boolean replace = false;

      public static <T> Tag.Builder<T> create() {
         return new Tag.Builder();
      }

      public Tag.Builder<T> add(Tag.ITagEntry<T> p_200575_1_) {
         this.entries.add(p_200575_1_);
         return this;
      }

      public Tag.Builder<T> add(T p_200048_1_) {
         this.entries.add(new Tag.ListEntry(Collections.singleton(p_200048_1_)));
         return this;
      }

      @SafeVarargs
      public final Tag.Builder<T> add(T... p_200573_1_) {
         this.entries.add(new Tag.ListEntry(Lists.newArrayList(p_200573_1_)));
         return this;
      }

      public Tag.Builder<T> add(Tag<T> p_200574_1_) {
         this.entries.add(new Tag.TagEntry(p_200574_1_));
         return this;
      }

      @SafeVarargs
      public final Tag.Builder<T> add(Tag<T>... p_add_1_) {
         Tag[] var2 = p_add_1_;
         int var3 = p_add_1_.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            Tag<T> tag = var2[var4];
            this.add(tag);
         }

         return this;
      }

      public Tag.Builder<T> replace(boolean p_replace_1_) {
         this.replace = p_replace_1_;
         return this;
      }

      public Tag.Builder<T> replace() {
         return this.replace(true);
      }

      public Tag.Builder<T> ordered(boolean p_200045_1_) {
         this.preserveOrder = p_200045_1_;
         return this;
      }

      public boolean resolve(Function<ResourceLocation, Tag<T>> p_200160_1_) {
         Iterator var2 = this.entries.iterator();

         Tag.ITagEntry itagentry;
         do {
            if (!var2.hasNext()) {
               return true;
            }

            itagentry = (Tag.ITagEntry)var2.next();
         } while(itagentry.resolve(p_200160_1_));

         return false;
      }

      public Tag<T> build(ResourceLocation p_200051_1_) {
         return new Tag(p_200051_1_, this.entries, this.preserveOrder, this.replace);
      }

      public Tag.Builder<T> fromJson(Function<ResourceLocation, Optional<T>> p_219783_1_, JsonObject p_219783_2_) {
         JsonArray jsonarray = JSONUtils.getJsonArray(p_219783_2_, "values");
         List<Tag.ITagEntry<T>> list = Lists.newArrayList();
         Iterator var5 = jsonarray.iterator();

         while(var5.hasNext()) {
            JsonElement jsonelement = (JsonElement)var5.next();
            String s = JSONUtils.getString(jsonelement, "value");
            if (s.startsWith("#")) {
               list.add(new Tag.TagEntry(new ResourceLocation(s.substring(1))));
            } else {
               ResourceLocation resourcelocation = new ResourceLocation(s);
               list.add(new Tag.ListEntry(Collections.singleton(((Optional)p_219783_1_.apply(resourcelocation)).orElseThrow(() -> {
                  return new JsonParseException("Unknown value '" + resourcelocation + "'");
               }))));
            }
         }

         if (JSONUtils.getBoolean(p_219783_2_, "replace", false)) {
            this.entries.clear();
         }

         this.entries.addAll(list);
         ForgeHooks.deserializeTagAdditions(this, p_219783_1_, p_219783_2_);
         return this;
      }

      public Tag.Builder<T> remove(Tag.ITagEntry<T> p_remove_1_) {
         this.entries.remove(p_remove_1_);
         return this;
      }
   }
}
