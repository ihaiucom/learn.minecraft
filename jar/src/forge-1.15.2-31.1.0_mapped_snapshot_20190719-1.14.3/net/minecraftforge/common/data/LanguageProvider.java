package net.minecraftforge.common.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.function.Supplier;
import net.minecraft.block.Block;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DirectoryCache;
import net.minecraft.data.IDataProvider;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Effect;
import net.minecraft.world.biome.Biome;
import org.apache.commons.lang3.text.translate.JavaUnicodeEscaper;

public abstract class LanguageProvider implements IDataProvider {
   private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();
   private final Map<String, String> data = new TreeMap();
   private final DataGenerator gen;
   private final String modid;
   private final String locale;

   public LanguageProvider(DataGenerator gen, String modid, String locale) {
      this.gen = gen;
      this.modid = modid;
      this.locale = locale;
   }

   protected abstract void addTranslations();

   public void act(DirectoryCache cache) throws IOException {
      this.addTranslations();
      if (!this.data.isEmpty()) {
         this.save(cache, this.data, this.gen.getOutputFolder().resolve("assets/" + this.modid + "/lang/" + this.locale + ".json"));
      }

   }

   public String getName() {
      return "Languages: " + this.locale;
   }

   private void save(DirectoryCache cache, Object object, Path target) throws IOException {
      String data = GSON.toJson(object);
      data = JavaUnicodeEscaper.outsideOf(0, 127).translate(data);
      String hash = IDataProvider.HASH_FUNCTION.hashUnencodedChars(data).toString();
      if (!Objects.equals(cache.getPreviousHash(target), hash) || !Files.exists(target, new LinkOption[0])) {
         Files.createDirectories(target.getParent());
         BufferedWriter bufferedwriter = Files.newBufferedWriter(target);
         Throwable var7 = null;

         try {
            bufferedwriter.write(data);
         } catch (Throwable var16) {
            var7 = var16;
            throw var16;
         } finally {
            if (bufferedwriter != null) {
               if (var7 != null) {
                  try {
                     bufferedwriter.close();
                  } catch (Throwable var15) {
                     var7.addSuppressed(var15);
                  }
               } else {
                  bufferedwriter.close();
               }
            }

         }
      }

      cache.func_208316_a(target, hash);
   }

   public void addBlock(Supplier<? extends Block> key, String name) {
      this.add((Block)key.get(), name);
   }

   public void add(Block key, String name) {
      this.add(key.getTranslationKey(), name);
   }

   public void addItem(Supplier<? extends Item> key, String name) {
      this.add((Item)key.get(), name);
   }

   public void add(Item key, String name) {
      this.add(key.getTranslationKey(), name);
   }

   public void addItemStack(Supplier<ItemStack> key, String name) {
      this.add((ItemStack)key.get(), name);
   }

   public void add(ItemStack key, String name) {
      this.add(key.getTranslationKey(), name);
   }

   public void addEnchantment(Supplier<? extends Enchantment> key, String name) {
      this.add((Enchantment)key.get(), name);
   }

   public void add(Enchantment key, String name) {
      this.add(key.getName(), name);
   }

   public void addBiome(Supplier<? extends Biome> key, String name) {
      this.add((Biome)key.get(), name);
   }

   public void add(Biome key, String name) {
      this.add(key.getTranslationKey(), name);
   }

   public void addEffect(Supplier<? extends Effect> key, String name) {
      this.add((Effect)key.get(), name);
   }

   public void add(Effect key, String name) {
      this.add(key.getName(), name);
   }

   public void addEntityType(Supplier<? extends EntityType<?>> key, String name) {
      this.add((EntityType)key.get(), name);
   }

   public void add(EntityType<?> key, String name) {
      this.add(key.getTranslationKey(), name);
   }

   public void add(String key, String value) {
      if (this.data.put(key, value) != null) {
         throw new IllegalStateException("Duplicate translation key " + key);
      }
   }
}
