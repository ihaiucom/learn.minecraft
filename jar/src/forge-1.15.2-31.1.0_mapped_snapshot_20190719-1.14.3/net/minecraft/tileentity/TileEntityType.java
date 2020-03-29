package net.minecraft.tileentity;

import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.types.Type;
import java.util.Set;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SharedConstants;
import net.minecraft.util.datafix.DataFixesManager;
import net.minecraft.util.datafix.TypeReferences;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.registries.ForgeRegistryEntry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TileEntityType<T extends TileEntity> extends ForgeRegistryEntry<TileEntityType<?>> {
   private static final Logger LOGGER = LogManager.getLogger();
   public static final TileEntityType<FurnaceTileEntity> FURNACE;
   public static final TileEntityType<ChestTileEntity> CHEST;
   public static final TileEntityType<TrappedChestTileEntity> TRAPPED_CHEST;
   public static final TileEntityType<EnderChestTileEntity> ENDER_CHEST;
   public static final TileEntityType<JukeboxTileEntity> JUKEBOX;
   public static final TileEntityType<DispenserTileEntity> DISPENSER;
   public static final TileEntityType<DropperTileEntity> DROPPER;
   public static final TileEntityType<SignTileEntity> SIGN;
   public static final TileEntityType<MobSpawnerTileEntity> MOB_SPAWNER;
   public static final TileEntityType<PistonTileEntity> PISTON;
   public static final TileEntityType<BrewingStandTileEntity> BREWING_STAND;
   public static final TileEntityType<EnchantingTableTileEntity> ENCHANTING_TABLE;
   public static final TileEntityType<EndPortalTileEntity> END_PORTAL;
   public static final TileEntityType<BeaconTileEntity> BEACON;
   public static final TileEntityType<SkullTileEntity> SKULL;
   public static final TileEntityType<DaylightDetectorTileEntity> DAYLIGHT_DETECTOR;
   public static final TileEntityType<HopperTileEntity> HOPPER;
   public static final TileEntityType<ComparatorTileEntity> COMPARATOR;
   public static final TileEntityType<BannerTileEntity> BANNER;
   public static final TileEntityType<StructureBlockTileEntity> STRUCTURE_BLOCK;
   public static final TileEntityType<EndGatewayTileEntity> END_GATEWAY;
   public static final TileEntityType<CommandBlockTileEntity> COMMAND_BLOCK;
   public static final TileEntityType<ShulkerBoxTileEntity> SHULKER_BOX;
   public static final TileEntityType<BedTileEntity> BED;
   public static final TileEntityType<ConduitTileEntity> CONDUIT;
   public static final TileEntityType<BarrelTileEntity> BARREL;
   public static final TileEntityType<SmokerTileEntity> SMOKER;
   public static final TileEntityType<BlastFurnaceTileEntity> BLAST_FURNACE;
   public static final TileEntityType<LecternTileEntity> LECTERN;
   public static final TileEntityType<BellTileEntity> BELL;
   public static final TileEntityType<JigsawTileEntity> JIGSAW;
   public static final TileEntityType<CampfireTileEntity> CAMPFIRE;
   public static final TileEntityType<BeehiveTileEntity> field_226985_G_;
   private final Supplier<? extends T> factory;
   private final Set<Block> validBlocks;
   private final Type<?> datafixerType;

   @Nullable
   public static ResourceLocation getId(TileEntityType<?> p_200969_0_) {
      return Registry.BLOCK_ENTITY_TYPE.getKey(p_200969_0_);
   }

   private static <T extends TileEntity> TileEntityType<T> register(String p_200966_0_, TileEntityType.Builder<T> p_200966_1_) {
      Type type = null;

      try {
         type = DataFixesManager.getDataFixer().getSchema(DataFixUtils.makeKey(SharedConstants.getVersion().getWorldVersion())).getChoiceType(TypeReferences.BLOCK_ENTITY, p_200966_0_);
      } catch (IllegalArgumentException var4) {
         LOGGER.error("No data fixer registered for block entity {}", p_200966_0_);
         if (SharedConstants.developmentMode) {
            throw var4;
         }
      }

      if (p_200966_1_.blocks.isEmpty()) {
         LOGGER.warn("Block entity type {} requires at least one valid block to be defined!", p_200966_0_);
      }

      return (TileEntityType)Registry.register((Registry)Registry.BLOCK_ENTITY_TYPE, (String)p_200966_0_, (Object)p_200966_1_.build(type));
   }

   public TileEntityType(Supplier<? extends T> p_i51497_1_, Set<Block> p_i51497_2_, Type<?> p_i51497_3_) {
      this.factory = p_i51497_1_;
      this.validBlocks = p_i51497_2_;
      this.datafixerType = p_i51497_3_;
   }

   @Nullable
   public T create() {
      return (TileEntity)this.factory.get();
   }

   public boolean isValidBlock(Block p_223045_1_) {
      return this.validBlocks.contains(p_223045_1_);
   }

   @Nullable
   public T func_226986_a_(IBlockReader p_226986_1_, BlockPos p_226986_2_) {
      TileEntity tileentity = p_226986_1_.getTileEntity(p_226986_2_);
      return tileentity != null && tileentity.getType() == this ? tileentity : null;
   }

   static {
      FURNACE = register("furnace", TileEntityType.Builder.create(FurnaceTileEntity::new, Blocks.FURNACE));
      CHEST = register("chest", TileEntityType.Builder.create(ChestTileEntity::new, Blocks.CHEST));
      TRAPPED_CHEST = register("trapped_chest", TileEntityType.Builder.create(TrappedChestTileEntity::new, Blocks.TRAPPED_CHEST));
      ENDER_CHEST = register("ender_chest", TileEntityType.Builder.create(EnderChestTileEntity::new, Blocks.ENDER_CHEST));
      JUKEBOX = register("jukebox", TileEntityType.Builder.create(JukeboxTileEntity::new, Blocks.JUKEBOX));
      DISPENSER = register("dispenser", TileEntityType.Builder.create(DispenserTileEntity::new, Blocks.DISPENSER));
      DROPPER = register("dropper", TileEntityType.Builder.create(DropperTileEntity::new, Blocks.DROPPER));
      SIGN = register("sign", TileEntityType.Builder.create(SignTileEntity::new, Blocks.OAK_SIGN, Blocks.SPRUCE_SIGN, Blocks.BIRCH_SIGN, Blocks.ACACIA_SIGN, Blocks.JUNGLE_SIGN, Blocks.DARK_OAK_SIGN, Blocks.OAK_WALL_SIGN, Blocks.SPRUCE_WALL_SIGN, Blocks.BIRCH_WALL_SIGN, Blocks.ACACIA_WALL_SIGN, Blocks.JUNGLE_WALL_SIGN, Blocks.DARK_OAK_WALL_SIGN));
      MOB_SPAWNER = register("mob_spawner", TileEntityType.Builder.create(MobSpawnerTileEntity::new, Blocks.SPAWNER));
      PISTON = register("piston", TileEntityType.Builder.create(PistonTileEntity::new, Blocks.MOVING_PISTON));
      BREWING_STAND = register("brewing_stand", TileEntityType.Builder.create(BrewingStandTileEntity::new, Blocks.BREWING_STAND));
      ENCHANTING_TABLE = register("enchanting_table", TileEntityType.Builder.create(EnchantingTableTileEntity::new, Blocks.ENCHANTING_TABLE));
      END_PORTAL = register("end_portal", TileEntityType.Builder.create(EndPortalTileEntity::new, Blocks.END_PORTAL));
      BEACON = register("beacon", TileEntityType.Builder.create(BeaconTileEntity::new, Blocks.BEACON));
      SKULL = register("skull", TileEntityType.Builder.create(SkullTileEntity::new, Blocks.SKELETON_SKULL, Blocks.SKELETON_WALL_SKULL, Blocks.CREEPER_HEAD, Blocks.CREEPER_WALL_HEAD, Blocks.DRAGON_HEAD, Blocks.DRAGON_WALL_HEAD, Blocks.ZOMBIE_HEAD, Blocks.ZOMBIE_WALL_HEAD, Blocks.WITHER_SKELETON_SKULL, Blocks.WITHER_SKELETON_WALL_SKULL, Blocks.PLAYER_HEAD, Blocks.PLAYER_WALL_HEAD));
      DAYLIGHT_DETECTOR = register("daylight_detector", TileEntityType.Builder.create(DaylightDetectorTileEntity::new, Blocks.DAYLIGHT_DETECTOR));
      HOPPER = register("hopper", TileEntityType.Builder.create(HopperTileEntity::new, Blocks.HOPPER));
      COMPARATOR = register("comparator", TileEntityType.Builder.create(ComparatorTileEntity::new, Blocks.COMPARATOR));
      BANNER = register("banner", TileEntityType.Builder.create(BannerTileEntity::new, Blocks.WHITE_BANNER, Blocks.ORANGE_BANNER, Blocks.MAGENTA_BANNER, Blocks.LIGHT_BLUE_BANNER, Blocks.YELLOW_BANNER, Blocks.LIME_BANNER, Blocks.PINK_BANNER, Blocks.GRAY_BANNER, Blocks.LIGHT_GRAY_BANNER, Blocks.CYAN_BANNER, Blocks.PURPLE_BANNER, Blocks.BLUE_BANNER, Blocks.BROWN_BANNER, Blocks.GREEN_BANNER, Blocks.RED_BANNER, Blocks.BLACK_BANNER, Blocks.WHITE_WALL_BANNER, Blocks.ORANGE_WALL_BANNER, Blocks.MAGENTA_WALL_BANNER, Blocks.LIGHT_BLUE_WALL_BANNER, Blocks.YELLOW_WALL_BANNER, Blocks.LIME_WALL_BANNER, Blocks.PINK_WALL_BANNER, Blocks.GRAY_WALL_BANNER, Blocks.LIGHT_GRAY_WALL_BANNER, Blocks.CYAN_WALL_BANNER, Blocks.PURPLE_WALL_BANNER, Blocks.BLUE_WALL_BANNER, Blocks.BROWN_WALL_BANNER, Blocks.GREEN_WALL_BANNER, Blocks.RED_WALL_BANNER, Blocks.BLACK_WALL_BANNER));
      STRUCTURE_BLOCK = register("structure_block", TileEntityType.Builder.create(StructureBlockTileEntity::new, Blocks.STRUCTURE_BLOCK));
      END_GATEWAY = register("end_gateway", TileEntityType.Builder.create(EndGatewayTileEntity::new, Blocks.END_GATEWAY));
      COMMAND_BLOCK = register("command_block", TileEntityType.Builder.create(CommandBlockTileEntity::new, Blocks.COMMAND_BLOCK, Blocks.CHAIN_COMMAND_BLOCK, Blocks.REPEATING_COMMAND_BLOCK));
      SHULKER_BOX = register("shulker_box", TileEntityType.Builder.create(ShulkerBoxTileEntity::new, Blocks.SHULKER_BOX, Blocks.BLACK_SHULKER_BOX, Blocks.BLUE_SHULKER_BOX, Blocks.BROWN_SHULKER_BOX, Blocks.CYAN_SHULKER_BOX, Blocks.GRAY_SHULKER_BOX, Blocks.GREEN_SHULKER_BOX, Blocks.LIGHT_BLUE_SHULKER_BOX, Blocks.LIGHT_GRAY_SHULKER_BOX, Blocks.LIME_SHULKER_BOX, Blocks.MAGENTA_SHULKER_BOX, Blocks.ORANGE_SHULKER_BOX, Blocks.PINK_SHULKER_BOX, Blocks.PURPLE_SHULKER_BOX, Blocks.RED_SHULKER_BOX, Blocks.WHITE_SHULKER_BOX, Blocks.YELLOW_SHULKER_BOX));
      BED = register("bed", TileEntityType.Builder.create(BedTileEntity::new, Blocks.RED_BED, Blocks.BLACK_BED, Blocks.BLUE_BED, Blocks.BROWN_BED, Blocks.CYAN_BED, Blocks.GRAY_BED, Blocks.GREEN_BED, Blocks.LIGHT_BLUE_BED, Blocks.LIGHT_GRAY_BED, Blocks.LIME_BED, Blocks.MAGENTA_BED, Blocks.ORANGE_BED, Blocks.PINK_BED, Blocks.PURPLE_BED, Blocks.WHITE_BED, Blocks.YELLOW_BED));
      CONDUIT = register("conduit", TileEntityType.Builder.create(ConduitTileEntity::new, Blocks.CONDUIT));
      BARREL = register("barrel", TileEntityType.Builder.create(BarrelTileEntity::new, Blocks.BARREL));
      SMOKER = register("smoker", TileEntityType.Builder.create(SmokerTileEntity::new, Blocks.SMOKER));
      BLAST_FURNACE = register("blast_furnace", TileEntityType.Builder.create(BlastFurnaceTileEntity::new, Blocks.BLAST_FURNACE));
      LECTERN = register("lectern", TileEntityType.Builder.create(LecternTileEntity::new, Blocks.LECTERN));
      BELL = register("bell", TileEntityType.Builder.create(BellTileEntity::new, Blocks.BELL));
      JIGSAW = register("jigsaw", TileEntityType.Builder.create(JigsawTileEntity::new, Blocks.field_226904_lY_));
      CAMPFIRE = register("campfire", TileEntityType.Builder.create(CampfireTileEntity::new, Blocks.CAMPFIRE));
      field_226985_G_ = register("beehive", TileEntityType.Builder.create(BeehiveTileEntity::new, Blocks.field_226905_ma_, Blocks.field_226906_mb_));
   }

   public static final class Builder<T extends TileEntity> {
      private final Supplier<? extends T> factory;
      private final Set<Block> blocks;

      private Builder(Supplier<? extends T> p_i51498_1_, Set<Block> p_i51498_2_) {
         this.factory = p_i51498_1_;
         this.blocks = p_i51498_2_;
      }

      public static <T extends TileEntity> TileEntityType.Builder<T> create(Supplier<? extends T> p_223042_0_, Block... p_223042_1_) {
         return new TileEntityType.Builder(p_223042_0_, ImmutableSet.copyOf(p_223042_1_));
      }

      public TileEntityType<T> build(Type<?> p_206865_1_) {
         return new TileEntityType(this.factory, this.blocks, p_206865_1_);
      }
   }
}
