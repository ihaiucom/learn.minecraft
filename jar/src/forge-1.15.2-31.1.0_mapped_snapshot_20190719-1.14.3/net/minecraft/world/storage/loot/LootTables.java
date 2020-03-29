package net.minecraft.world.storage.loot;

import com.google.common.collect.Sets;
import java.util.Collections;
import java.util.Set;
import net.minecraft.util.ResourceLocation;

public class LootTables {
   private static final Set<ResourceLocation> LOOT_TABLES = Sets.newHashSet();
   private static final Set<ResourceLocation> READ_ONLY_LOOT_TABLES;
   public static final ResourceLocation EMPTY;
   public static final ResourceLocation CHESTS_SPAWN_BONUS_CHEST;
   public static final ResourceLocation CHESTS_END_CITY_TREASURE;
   public static final ResourceLocation CHESTS_SIMPLE_DUNGEON;
   public static final ResourceLocation CHESTS_VILLAGE_VILLAGE_WEAPONSMITH;
   public static final ResourceLocation CHESTS_VILLAGE_VILLAGE_TOOLSMITH;
   public static final ResourceLocation CHESTS_VILLAGE_VILLAGE_ARMORER;
   public static final ResourceLocation CHESTS_VILLAGE_VILLAGE_CARTOGRAPHER;
   public static final ResourceLocation CHESTS_VILLAGE_VILLAGE_MASON;
   public static final ResourceLocation CHESTS_VILLAGE_VILLAGE_SHEPHERD;
   public static final ResourceLocation CHESTS_VILLAGE_VILLAGE_BUTCHER;
   public static final ResourceLocation CHESTS_VILLAGE_VILLAGE_FLETCHER;
   public static final ResourceLocation CHESTS_VILLAGE_VILLAGE_FISHER;
   public static final ResourceLocation CHESTS_VILLAGE_VILLAGE_TANNERY;
   public static final ResourceLocation CHESTS_VILLAGE_VILLAGE_TEMPLE;
   public static final ResourceLocation CHESTS_VILLAGE_VILLAGE_DESERT_HOUSE;
   public static final ResourceLocation CHESTS_VILLAGE_VILLAGE_PLAINS_HOUSE;
   public static final ResourceLocation CHESTS_VILLAGE_VILLAGE_TAIGA_HOUSE;
   public static final ResourceLocation CHESTS_VILLAGE_VILLAGE_SNOWY_HOUSE;
   public static final ResourceLocation CHESTS_VILLAGE_VILLAGE_SAVANNA_HOUSE;
   public static final ResourceLocation CHESTS_ABANDONED_MINESHAFT;
   public static final ResourceLocation CHESTS_NETHER_BRIDGE;
   public static final ResourceLocation CHESTS_STRONGHOLD_LIBRARY;
   public static final ResourceLocation CHESTS_STRONGHOLD_CROSSING;
   public static final ResourceLocation CHESTS_STRONGHOLD_CORRIDOR;
   public static final ResourceLocation CHESTS_DESERT_PYRAMID;
   public static final ResourceLocation CHESTS_JUNGLE_TEMPLE;
   public static final ResourceLocation CHESTS_JUNGLE_TEMPLE_DISPENSER;
   public static final ResourceLocation CHESTS_IGLOO_CHEST;
   public static final ResourceLocation CHESTS_WOODLAND_MANSION;
   public static final ResourceLocation CHESTS_UNDERWATER_RUIN_SMALL;
   public static final ResourceLocation CHESTS_UNDERWATER_RUIN_BIG;
   public static final ResourceLocation CHESTS_BURIED_TREASURE;
   public static final ResourceLocation CHESTS_SHIPWRECK_MAP;
   public static final ResourceLocation CHESTS_SHIPWRECK_SUPPLY;
   public static final ResourceLocation CHESTS_SHIPWRECK_TREASURE;
   public static final ResourceLocation CHESTS_PILLAGER_OUTPOST;
   public static final ResourceLocation ENTITIES_SHEEP_WHITE;
   public static final ResourceLocation ENTITIES_SHEEP_ORANGE;
   public static final ResourceLocation ENTITIES_SHEEP_MAGENTA;
   public static final ResourceLocation ENTITIES_SHEEP_LIGHT_BLUE;
   public static final ResourceLocation ENTITIES_SHEEP_YELLOW;
   public static final ResourceLocation ENTITIES_SHEEP_LIME;
   public static final ResourceLocation ENTITIES_SHEEP_PINK;
   public static final ResourceLocation ENTITIES_SHEEP_GRAY;
   public static final ResourceLocation ENTITIES_SHEEP_LIGHT_GRAY;
   public static final ResourceLocation ENTITIES_SHEEP_CYAN;
   public static final ResourceLocation ENTITIES_SHEEP_PURPLE;
   public static final ResourceLocation ENTITIES_SHEEP_BLUE;
   public static final ResourceLocation ENTITIES_SHEEP_BROWN;
   public static final ResourceLocation ENTITIES_SHEEP_GREEN;
   public static final ResourceLocation ENTITIES_SHEEP_RED;
   public static final ResourceLocation ENTITIES_SHEEP_BLACK;
   public static final ResourceLocation GAMEPLAY_FISHING;
   public static final ResourceLocation GAMEPLAY_FISHING_JUNK;
   public static final ResourceLocation GAMEPLAY_FISHING_TREASURE;
   public static final ResourceLocation GAMEPLAY_FISHING_FISH;
   public static final ResourceLocation GAMEPLAY_CAT_MORNING_GIFT;
   public static final ResourceLocation GAMEPLAY_HERO_OF_THE_VILLAGE_ARMORER_GIFT;
   public static final ResourceLocation GAMEPLAY_HERO_OF_THE_VILLAGE_BUTCHER_GIFT;
   public static final ResourceLocation GAMEPLAY_HERO_OF_THE_VILLAGE_CARTOGRAPHER_GIFT;
   public static final ResourceLocation GAMEPLAY_HERO_OF_THE_VILLAGE_CLERIC_GIFT;
   public static final ResourceLocation GAMEPLAY_HERO_OF_THE_VILLAGE_FARMER_GIFT;
   public static final ResourceLocation GAMEPLAY_HERO_OF_THE_VILLAGE_FISHERMAN_GIFT;
   public static final ResourceLocation GAMEPLAY_HERO_OF_THE_VILLAGE_FLETCHER_GIFT;
   public static final ResourceLocation GAMEPLAY_HERO_OF_THE_VILLAGE_LEATHERWORKER_GIFT;
   public static final ResourceLocation GAMEPLAY_HERO_OF_THE_VILLAGE_LIBRARIAN_GIFT;
   public static final ResourceLocation GAMEPLAY_HERO_OF_THE_VILLAGE_MASON_GIFT;
   public static final ResourceLocation GAMEPLAY_HERO_OF_THE_VILLAGE_SHEPHERD_GIFT;
   public static final ResourceLocation GAMEPLAY_HERO_OF_THE_VILLAGE_TOOLSMITH_GIFT;
   public static final ResourceLocation GAMEPLAY_HERO_OF_THE_VILLAGE_WEAPONSMITH_GIFT;

   private static ResourceLocation register(String p_186373_0_) {
      return register(new ResourceLocation(p_186373_0_));
   }

   private static ResourceLocation register(ResourceLocation p_186375_0_) {
      if (LOOT_TABLES.add(p_186375_0_)) {
         return p_186375_0_;
      } else {
         throw new IllegalArgumentException(p_186375_0_ + " is already a registered built-in loot table");
      }
   }

   public static Set<ResourceLocation> func_215796_a() {
      return READ_ONLY_LOOT_TABLES;
   }

   static {
      READ_ONLY_LOOT_TABLES = Collections.unmodifiableSet(LOOT_TABLES);
      EMPTY = new ResourceLocation("empty");
      CHESTS_SPAWN_BONUS_CHEST = register("chests/spawn_bonus_chest");
      CHESTS_END_CITY_TREASURE = register("chests/end_city_treasure");
      CHESTS_SIMPLE_DUNGEON = register("chests/simple_dungeon");
      CHESTS_VILLAGE_VILLAGE_WEAPONSMITH = register("chests/village/village_weaponsmith");
      CHESTS_VILLAGE_VILLAGE_TOOLSMITH = register("chests/village/village_toolsmith");
      CHESTS_VILLAGE_VILLAGE_ARMORER = register("chests/village/village_armorer");
      CHESTS_VILLAGE_VILLAGE_CARTOGRAPHER = register("chests/village/village_cartographer");
      CHESTS_VILLAGE_VILLAGE_MASON = register("chests/village/village_mason");
      CHESTS_VILLAGE_VILLAGE_SHEPHERD = register("chests/village/village_shepherd");
      CHESTS_VILLAGE_VILLAGE_BUTCHER = register("chests/village/village_butcher");
      CHESTS_VILLAGE_VILLAGE_FLETCHER = register("chests/village/village_fletcher");
      CHESTS_VILLAGE_VILLAGE_FISHER = register("chests/village/village_fisher");
      CHESTS_VILLAGE_VILLAGE_TANNERY = register("chests/village/village_tannery");
      CHESTS_VILLAGE_VILLAGE_TEMPLE = register("chests/village/village_temple");
      CHESTS_VILLAGE_VILLAGE_DESERT_HOUSE = register("chests/village/village_desert_house");
      CHESTS_VILLAGE_VILLAGE_PLAINS_HOUSE = register("chests/village/village_plains_house");
      CHESTS_VILLAGE_VILLAGE_TAIGA_HOUSE = register("chests/village/village_taiga_house");
      CHESTS_VILLAGE_VILLAGE_SNOWY_HOUSE = register("chests/village/village_snowy_house");
      CHESTS_VILLAGE_VILLAGE_SAVANNA_HOUSE = register("chests/village/village_savanna_house");
      CHESTS_ABANDONED_MINESHAFT = register("chests/abandoned_mineshaft");
      CHESTS_NETHER_BRIDGE = register("chests/nether_bridge");
      CHESTS_STRONGHOLD_LIBRARY = register("chests/stronghold_library");
      CHESTS_STRONGHOLD_CROSSING = register("chests/stronghold_crossing");
      CHESTS_STRONGHOLD_CORRIDOR = register("chests/stronghold_corridor");
      CHESTS_DESERT_PYRAMID = register("chests/desert_pyramid");
      CHESTS_JUNGLE_TEMPLE = register("chests/jungle_temple");
      CHESTS_JUNGLE_TEMPLE_DISPENSER = register("chests/jungle_temple_dispenser");
      CHESTS_IGLOO_CHEST = register("chests/igloo_chest");
      CHESTS_WOODLAND_MANSION = register("chests/woodland_mansion");
      CHESTS_UNDERWATER_RUIN_SMALL = register("chests/underwater_ruin_small");
      CHESTS_UNDERWATER_RUIN_BIG = register("chests/underwater_ruin_big");
      CHESTS_BURIED_TREASURE = register("chests/buried_treasure");
      CHESTS_SHIPWRECK_MAP = register("chests/shipwreck_map");
      CHESTS_SHIPWRECK_SUPPLY = register("chests/shipwreck_supply");
      CHESTS_SHIPWRECK_TREASURE = register("chests/shipwreck_treasure");
      CHESTS_PILLAGER_OUTPOST = register("chests/pillager_outpost");
      ENTITIES_SHEEP_WHITE = register("entities/sheep/white");
      ENTITIES_SHEEP_ORANGE = register("entities/sheep/orange");
      ENTITIES_SHEEP_MAGENTA = register("entities/sheep/magenta");
      ENTITIES_SHEEP_LIGHT_BLUE = register("entities/sheep/light_blue");
      ENTITIES_SHEEP_YELLOW = register("entities/sheep/yellow");
      ENTITIES_SHEEP_LIME = register("entities/sheep/lime");
      ENTITIES_SHEEP_PINK = register("entities/sheep/pink");
      ENTITIES_SHEEP_GRAY = register("entities/sheep/gray");
      ENTITIES_SHEEP_LIGHT_GRAY = register("entities/sheep/light_gray");
      ENTITIES_SHEEP_CYAN = register("entities/sheep/cyan");
      ENTITIES_SHEEP_PURPLE = register("entities/sheep/purple");
      ENTITIES_SHEEP_BLUE = register("entities/sheep/blue");
      ENTITIES_SHEEP_BROWN = register("entities/sheep/brown");
      ENTITIES_SHEEP_GREEN = register("entities/sheep/green");
      ENTITIES_SHEEP_RED = register("entities/sheep/red");
      ENTITIES_SHEEP_BLACK = register("entities/sheep/black");
      GAMEPLAY_FISHING = register("gameplay/fishing");
      GAMEPLAY_FISHING_JUNK = register("gameplay/fishing/junk");
      GAMEPLAY_FISHING_TREASURE = register("gameplay/fishing/treasure");
      GAMEPLAY_FISHING_FISH = register("gameplay/fishing/fish");
      GAMEPLAY_CAT_MORNING_GIFT = register("gameplay/cat_morning_gift");
      GAMEPLAY_HERO_OF_THE_VILLAGE_ARMORER_GIFT = register("gameplay/hero_of_the_village/armorer_gift");
      GAMEPLAY_HERO_OF_THE_VILLAGE_BUTCHER_GIFT = register("gameplay/hero_of_the_village/butcher_gift");
      GAMEPLAY_HERO_OF_THE_VILLAGE_CARTOGRAPHER_GIFT = register("gameplay/hero_of_the_village/cartographer_gift");
      GAMEPLAY_HERO_OF_THE_VILLAGE_CLERIC_GIFT = register("gameplay/hero_of_the_village/cleric_gift");
      GAMEPLAY_HERO_OF_THE_VILLAGE_FARMER_GIFT = register("gameplay/hero_of_the_village/farmer_gift");
      GAMEPLAY_HERO_OF_THE_VILLAGE_FISHERMAN_GIFT = register("gameplay/hero_of_the_village/fisherman_gift");
      GAMEPLAY_HERO_OF_THE_VILLAGE_FLETCHER_GIFT = register("gameplay/hero_of_the_village/fletcher_gift");
      GAMEPLAY_HERO_OF_THE_VILLAGE_LEATHERWORKER_GIFT = register("gameplay/hero_of_the_village/leatherworker_gift");
      GAMEPLAY_HERO_OF_THE_VILLAGE_LIBRARIAN_GIFT = register("gameplay/hero_of_the_village/librarian_gift");
      GAMEPLAY_HERO_OF_THE_VILLAGE_MASON_GIFT = register("gameplay/hero_of_the_village/mason_gift");
      GAMEPLAY_HERO_OF_THE_VILLAGE_SHEPHERD_GIFT = register("gameplay/hero_of_the_village/shepherd_gift");
      GAMEPLAY_HERO_OF_THE_VILLAGE_TOOLSMITH_GIFT = register("gameplay/hero_of_the_village/toolsmith_gift");
      GAMEPLAY_HERO_OF_THE_VILLAGE_WEAPONSMITH_GIFT = register("gameplay/hero_of_the_village/weaponsmith_gift");
   }
}
