package net.minecraft.advancements;

import com.google.common.collect.Maps;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.advancements.criterion.BeeNestDestroyedTrigger;
import net.minecraft.advancements.criterion.BredAnimalsTrigger;
import net.minecraft.advancements.criterion.BrewedPotionTrigger;
import net.minecraft.advancements.criterion.ChangeDimensionTrigger;
import net.minecraft.advancements.criterion.ChanneledLightningTrigger;
import net.minecraft.advancements.criterion.ConstructBeaconTrigger;
import net.minecraft.advancements.criterion.ConsumeItemTrigger;
import net.minecraft.advancements.criterion.CuredZombieVillagerTrigger;
import net.minecraft.advancements.criterion.EffectsChangedTrigger;
import net.minecraft.advancements.criterion.EnchantedItemTrigger;
import net.minecraft.advancements.criterion.EnterBlockTrigger;
import net.minecraft.advancements.criterion.EntityHurtPlayerTrigger;
import net.minecraft.advancements.criterion.FilledBucketTrigger;
import net.minecraft.advancements.criterion.FishingRodHookedTrigger;
import net.minecraft.advancements.criterion.ImpossibleTrigger;
import net.minecraft.advancements.criterion.InventoryChangeTrigger;
import net.minecraft.advancements.criterion.ItemDurabilityTrigger;
import net.minecraft.advancements.criterion.KilledByCrossbowTrigger;
import net.minecraft.advancements.criterion.KilledTrigger;
import net.minecraft.advancements.criterion.LevitationTrigger;
import net.minecraft.advancements.criterion.NetherTravelTrigger;
import net.minecraft.advancements.criterion.PlacedBlockTrigger;
import net.minecraft.advancements.criterion.PlayerHurtEntityTrigger;
import net.minecraft.advancements.criterion.PositionTrigger;
import net.minecraft.advancements.criterion.RecipeUnlockedTrigger;
import net.minecraft.advancements.criterion.RightClickBlockWithItemTrigger;
import net.minecraft.advancements.criterion.ShotCrossbowTrigger;
import net.minecraft.advancements.criterion.SlideDownBlockTrigger;
import net.minecraft.advancements.criterion.SummonedEntityTrigger;
import net.minecraft.advancements.criterion.TameAnimalTrigger;
import net.minecraft.advancements.criterion.TickTrigger;
import net.minecraft.advancements.criterion.UsedEnderEyeTrigger;
import net.minecraft.advancements.criterion.UsedTotemTrigger;
import net.minecraft.advancements.criterion.VillagerTradeTrigger;
import net.minecraft.util.ResourceLocation;

public class CriteriaTriggers {
   private static final Map<ResourceLocation, ICriterionTrigger<?>> REGISTRY = Maps.newHashMap();
   public static final ImpossibleTrigger IMPOSSIBLE = (ImpossibleTrigger)register(new ImpossibleTrigger());
   public static final KilledTrigger PLAYER_KILLED_ENTITY = (KilledTrigger)register(new KilledTrigger(new ResourceLocation("player_killed_entity")));
   public static final KilledTrigger ENTITY_KILLED_PLAYER = (KilledTrigger)register(new KilledTrigger(new ResourceLocation("entity_killed_player")));
   public static final EnterBlockTrigger ENTER_BLOCK = (EnterBlockTrigger)register(new EnterBlockTrigger());
   public static final InventoryChangeTrigger INVENTORY_CHANGED = (InventoryChangeTrigger)register(new InventoryChangeTrigger());
   public static final RecipeUnlockedTrigger RECIPE_UNLOCKED = (RecipeUnlockedTrigger)register(new RecipeUnlockedTrigger());
   public static final PlayerHurtEntityTrigger PLAYER_HURT_ENTITY = (PlayerHurtEntityTrigger)register(new PlayerHurtEntityTrigger());
   public static final EntityHurtPlayerTrigger ENTITY_HURT_PLAYER = (EntityHurtPlayerTrigger)register(new EntityHurtPlayerTrigger());
   public static final EnchantedItemTrigger ENCHANTED_ITEM = (EnchantedItemTrigger)register(new EnchantedItemTrigger());
   public static final FilledBucketTrigger FILLED_BUCKET = (FilledBucketTrigger)register(new FilledBucketTrigger());
   public static final BrewedPotionTrigger BREWED_POTION = (BrewedPotionTrigger)register(new BrewedPotionTrigger());
   public static final ConstructBeaconTrigger CONSTRUCT_BEACON = (ConstructBeaconTrigger)register(new ConstructBeaconTrigger());
   public static final UsedEnderEyeTrigger USED_ENDER_EYE = (UsedEnderEyeTrigger)register(new UsedEnderEyeTrigger());
   public static final SummonedEntityTrigger SUMMONED_ENTITY = (SummonedEntityTrigger)register(new SummonedEntityTrigger());
   public static final BredAnimalsTrigger BRED_ANIMALS = (BredAnimalsTrigger)register(new BredAnimalsTrigger());
   public static final PositionTrigger LOCATION = (PositionTrigger)register(new PositionTrigger(new ResourceLocation("location")));
   public static final PositionTrigger SLEPT_IN_BED = (PositionTrigger)register(new PositionTrigger(new ResourceLocation("slept_in_bed")));
   public static final CuredZombieVillagerTrigger CURED_ZOMBIE_VILLAGER = (CuredZombieVillagerTrigger)register(new CuredZombieVillagerTrigger());
   public static final VillagerTradeTrigger VILLAGER_TRADE = (VillagerTradeTrigger)register(new VillagerTradeTrigger());
   public static final ItemDurabilityTrigger ITEM_DURABILITY_CHANGED = (ItemDurabilityTrigger)register(new ItemDurabilityTrigger());
   public static final LevitationTrigger LEVITATION = (LevitationTrigger)register(new LevitationTrigger());
   public static final ChangeDimensionTrigger CHANGED_DIMENSION = (ChangeDimensionTrigger)register(new ChangeDimensionTrigger());
   public static final TickTrigger TICK = (TickTrigger)register(new TickTrigger());
   public static final TameAnimalTrigger TAME_ANIMAL = (TameAnimalTrigger)register(new TameAnimalTrigger());
   public static final PlacedBlockTrigger PLACED_BLOCK = (PlacedBlockTrigger)register(new PlacedBlockTrigger());
   public static final ConsumeItemTrigger CONSUME_ITEM = (ConsumeItemTrigger)register(new ConsumeItemTrigger());
   public static final EffectsChangedTrigger EFFECTS_CHANGED = (EffectsChangedTrigger)register(new EffectsChangedTrigger());
   public static final UsedTotemTrigger USED_TOTEM = (UsedTotemTrigger)register(new UsedTotemTrigger());
   public static final NetherTravelTrigger NETHER_TRAVEL = (NetherTravelTrigger)register(new NetherTravelTrigger());
   public static final FishingRodHookedTrigger FISHING_ROD_HOOKED = (FishingRodHookedTrigger)register(new FishingRodHookedTrigger());
   public static final ChanneledLightningTrigger CHANNELED_LIGHTNING = (ChanneledLightningTrigger)register(new ChanneledLightningTrigger());
   public static final ShotCrossbowTrigger SHOT_CROSSBOW = (ShotCrossbowTrigger)register(new ShotCrossbowTrigger());
   public static final KilledByCrossbowTrigger KILLED_BY_CROSSBOW = (KilledByCrossbowTrigger)register(new KilledByCrossbowTrigger());
   public static final PositionTrigger HERO_OF_THE_VILLAGE = (PositionTrigger)register(new PositionTrigger(new ResourceLocation("hero_of_the_village")));
   public static final PositionTrigger VOLUNTARY_EXILE = (PositionTrigger)register(new PositionTrigger(new ResourceLocation("voluntary_exile")));
   public static final RightClickBlockWithItemTrigger field_229863_J_ = (RightClickBlockWithItemTrigger)register(new RightClickBlockWithItemTrigger(new ResourceLocation("safely_harvest_honey")));
   public static final SlideDownBlockTrigger field_229864_K_ = (SlideDownBlockTrigger)register(new SlideDownBlockTrigger());
   public static final BeeNestDestroyedTrigger field_229865_L_ = (BeeNestDestroyedTrigger)register(new BeeNestDestroyedTrigger());

   public static <T extends ICriterionTrigger<?>> T register(T p_192118_0_) {
      if (REGISTRY.containsKey(p_192118_0_.getId())) {
         throw new IllegalArgumentException("Duplicate criterion id " + p_192118_0_.getId());
      } else {
         REGISTRY.put(p_192118_0_.getId(), p_192118_0_);
         return p_192118_0_;
      }
   }

   @Nullable
   public static <T extends ICriterionInstance> ICriterionTrigger<T> get(ResourceLocation p_192119_0_) {
      return (ICriterionTrigger)REGISTRY.get(p_192119_0_);
   }

   public static Iterable<? extends ICriterionTrigger<?>> getAll() {
      return REGISTRY.values();
   }
}
