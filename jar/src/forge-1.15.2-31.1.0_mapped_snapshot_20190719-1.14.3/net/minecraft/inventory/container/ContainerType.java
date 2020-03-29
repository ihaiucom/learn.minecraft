package net.minecraft.inventory.container;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.fml.network.IContainerFactory;
import net.minecraftforge.registries.ForgeRegistryEntry;

public class ContainerType<T extends Container> extends ForgeRegistryEntry<ContainerType<?>> implements IForgeContainerType<T> {
   public static final ContainerType<ChestContainer> GENERIC_9X1 = register("generic_9x1", ChestContainer::createGeneric9X1);
   public static final ContainerType<ChestContainer> GENERIC_9X2 = register("generic_9x2", ChestContainer::createGeneric9X2);
   public static final ContainerType<ChestContainer> GENERIC_9X3 = register("generic_9x3", ChestContainer::createGeneric9X3);
   public static final ContainerType<ChestContainer> GENERIC_9X4 = register("generic_9x4", ChestContainer::createGeneric9X4);
   public static final ContainerType<ChestContainer> GENERIC_9X5 = register("generic_9x5", ChestContainer::createGeneric9X5);
   public static final ContainerType<ChestContainer> GENERIC_9X6 = register("generic_9x6", ChestContainer::createGeneric9X6);
   public static final ContainerType<DispenserContainer> GENERIC_3X3 = register("generic_3x3", DispenserContainer::new);
   public static final ContainerType<RepairContainer> ANVIL = register("anvil", RepairContainer::new);
   public static final ContainerType<BeaconContainer> BEACON = register("beacon", BeaconContainer::new);
   public static final ContainerType<BlastFurnaceContainer> BLAST_FURNACE = register("blast_furnace", BlastFurnaceContainer::new);
   public static final ContainerType<BrewingStandContainer> BREWING_STAND = register("brewing_stand", BrewingStandContainer::new);
   public static final ContainerType<WorkbenchContainer> CRAFTING = register("crafting", WorkbenchContainer::new);
   public static final ContainerType<EnchantmentContainer> ENCHANTMENT = register("enchantment", EnchantmentContainer::new);
   public static final ContainerType<FurnaceContainer> FURNACE = register("furnace", FurnaceContainer::new);
   public static final ContainerType<GrindstoneContainer> GRINDSTONE = register("grindstone", GrindstoneContainer::new);
   public static final ContainerType<HopperContainer> HOPPER = register("hopper", HopperContainer::new);
   public static final ContainerType<LecternContainer> LECTERN = register("lectern", (p_lambda$static$0_0_, p_lambda$static$0_1_) -> {
      return new LecternContainer(p_lambda$static$0_0_);
   });
   public static final ContainerType<LoomContainer> LOOM = register("loom", LoomContainer::new);
   public static final ContainerType<MerchantContainer> MERCHANT = register("merchant", MerchantContainer::new);
   public static final ContainerType<ShulkerBoxContainer> SHULKER_BOX = register("shulker_box", ShulkerBoxContainer::new);
   public static final ContainerType<SmokerContainer> SMOKER = register("smoker", SmokerContainer::new);
   public static final ContainerType<CartographyContainer> field_226625_v_ = register("cartography_table", CartographyContainer::new);
   public static final ContainerType<StonecutterContainer> STONECUTTER = register("stonecutter", StonecutterContainer::new);
   private final ContainerType.IFactory<T> factory;

   private static <T extends Container> ContainerType<T> register(String p_221505_0_, ContainerType.IFactory<T> p_221505_1_) {
      return (ContainerType)Registry.register((Registry)Registry.MENU, (String)p_221505_0_, (Object)(new ContainerType(p_221505_1_)));
   }

   public ContainerType(ContainerType.IFactory<T> p_i50072_1_) {
      this.factory = p_i50072_1_;
   }

   @OnlyIn(Dist.CLIENT)
   public T create(int p_221506_1_, PlayerInventory p_221506_2_) {
      return this.factory.create(p_221506_1_, p_221506_2_);
   }

   public T create(int p_create_1_, PlayerInventory p_create_2_, PacketBuffer p_create_3_) {
      return this.factory instanceof IContainerFactory ? ((IContainerFactory)this.factory).create(p_create_1_, p_create_2_, p_create_3_) : this.create(p_create_1_, p_create_2_);
   }

   public interface IFactory<T extends Container> {
      @OnlyIn(Dist.CLIENT)
      T create(int var1, PlayerInventory var2);
   }
}
