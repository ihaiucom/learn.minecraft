package net.minecraft.data;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.google.common.collect.UnmodifiableIterator;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.datafixers.util.Pair;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.data.loot.BlockLootTables;
import net.minecraft.data.loot.ChestLootTables;
import net.minecraft.data.loot.EntityLootTables;
import net.minecraft.data.loot.FishingLootTables;
import net.minecraft.data.loot.GiftLootTables;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootParameterSet;
import net.minecraft.world.storage.loot.LootParameterSets;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraft.world.storage.loot.LootTableManager;
import net.minecraft.world.storage.loot.LootTables;
import net.minecraft.world.storage.loot.ValidationTracker;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LootTableProvider implements IDataProvider {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();
   private final DataGenerator dataGenerator;
   private final List<Pair<Supplier<Consumer<BiConsumer<ResourceLocation, LootTable.Builder>>>, LootParameterSet>> field_218444_e;

   public LootTableProvider(DataGenerator p_i50789_1_) {
      this.field_218444_e = ImmutableList.of(Pair.of(FishingLootTables::new, LootParameterSets.FISHING), Pair.of(ChestLootTables::new, LootParameterSets.CHEST), Pair.of(EntityLootTables::new, LootParameterSets.ENTITY), Pair.of(BlockLootTables::new, LootParameterSets.BLOCK), Pair.of(GiftLootTables::new, LootParameterSets.GIFT));
      this.dataGenerator = p_i50789_1_;
   }

   public void act(DirectoryCache p_200398_1_) {
      Path path = this.dataGenerator.getOutputFolder();
      Map<ResourceLocation, LootTable> map = Maps.newHashMap();
      this.getTables().forEach((p_lambda$act$1_1_) -> {
         ((Consumer)((Supplier)p_lambda$act$1_1_.getFirst()).get()).accept((p_lambda$null$0_2_, p_lambda$null$0_3_) -> {
            if (map.put(p_lambda$null$0_2_, p_lambda$null$0_3_.setParameterSet((LootParameterSet)p_lambda$act$1_1_.getSecond()).build()) != null) {
               throw new IllegalStateException("Duplicate loot table " + p_lambda$null$0_2_);
            }
         });
      });
      LootParameterSet var10002 = LootParameterSets.GENERIC;
      Function var10003 = (p_lambda$act$2_0_) -> {
         return null;
      };
      map.getClass();
      ValidationTracker validationtracker = new ValidationTracker(var10002, var10003, map::get);
      this.validate(map, validationtracker);
      Multimap<String, String> multimap = validationtracker.func_227527_a_();
      if (!multimap.isEmpty()) {
         multimap.forEach((p_lambda$act$3_0_, p_lambda$act$3_1_) -> {
            LOGGER.warn("Found validation problem in " + p_lambda$act$3_0_ + ": " + p_lambda$act$3_1_);
         });
         throw new IllegalStateException("Failed to validate loot tables, see logs");
      } else {
         map.forEach((p_lambda$act$4_2_, p_lambda$act$4_3_) -> {
            Path path1 = getPath(path, p_lambda$act$4_2_);

            try {
               IDataProvider.save(GSON, p_200398_1_, LootTableManager.toJson(p_lambda$act$4_3_), path1);
            } catch (IOException var6) {
               LOGGER.error("Couldn't save loot table {}", path1, var6);
            }

         });
      }
   }

   protected List<Pair<Supplier<Consumer<BiConsumer<ResourceLocation, LootTable.Builder>>>, LootParameterSet>> getTables() {
      return this.field_218444_e;
   }

   protected void validate(Map<ResourceLocation, LootTable> p_validate_1_, ValidationTracker p_validate_2_) {
      UnmodifiableIterator var3 = Sets.difference(LootTables.func_215796_a(), p_validate_1_.keySet()).iterator();

      while(var3.hasNext()) {
         ResourceLocation resourcelocation = (ResourceLocation)var3.next();
         p_validate_2_.func_227530_a_("Missing built-in table: " + resourcelocation);
      }

      p_validate_1_.forEach((p_lambda$validate$5_1_, p_lambda$validate$5_2_) -> {
         LootTableManager.func_227508_a_(p_validate_2_, p_lambda$validate$5_1_, p_lambda$validate$5_2_);
      });
   }

   private static Path getPath(Path p_218439_0_, ResourceLocation p_218439_1_) {
      return p_218439_0_.resolve("data/" + p_218439_1_.getNamespace() + "/loot_tables/" + p_218439_1_.getPath() + ".json");
   }

   public String getName() {
      return "LootTables";
   }
}
