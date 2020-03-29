package net.minecraft.client.gui;

import com.google.common.collect.Maps;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.EnchantmentScreen;
import net.minecraft.client.gui.screen.GrindstoneScreen;
import net.minecraft.client.gui.screen.HopperScreen;
import net.minecraft.client.gui.screen.LecternScreen;
import net.minecraft.client.gui.screen.LoomScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.inventory.AnvilScreen;
import net.minecraft.client.gui.screen.inventory.BeaconScreen;
import net.minecraft.client.gui.screen.inventory.BlastFurnaceScreen;
import net.minecraft.client.gui.screen.inventory.BrewingStandScreen;
import net.minecraft.client.gui.screen.inventory.CartographyTableScreen;
import net.minecraft.client.gui.screen.inventory.ChestScreen;
import net.minecraft.client.gui.screen.inventory.CraftingScreen;
import net.minecraft.client.gui.screen.inventory.DispenserScreen;
import net.minecraft.client.gui.screen.inventory.FurnaceScreen;
import net.minecraft.client.gui.screen.inventory.MerchantScreen;
import net.minecraft.client.gui.screen.inventory.ShulkerBoxScreen;
import net.minecraft.client.gui.screen.inventory.SmokerScreen;
import net.minecraft.client.gui.screen.inventory.StonecutterScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class ScreenManager {
   private static final Logger LOG = LogManager.getLogger();
   private static final Map<ContainerType<?>, ScreenManager.IScreenFactory<?, ?>> FACTORIES = Maps.newHashMap();

   public static <T extends Container> void openScreen(@Nullable ContainerType<T> p_216909_0_, Minecraft p_216909_1_, int p_216909_2_, ITextComponent p_216909_3_) {
      getScreenFactory(p_216909_0_, p_216909_1_, p_216909_2_, p_216909_3_).ifPresent((p_lambda$openScreen$0_4_) -> {
         p_lambda$openScreen$0_4_.createScreen(p_216909_3_, p_216909_0_, p_216909_1_, p_216909_2_);
      });
   }

   public static <T extends Container> Optional<ScreenManager.IScreenFactory<T, ?>> getScreenFactory(@Nullable ContainerType<T> p_getScreenFactory_0_, Minecraft p_getScreenFactory_1_, int p_getScreenFactory_2_, ITextComponent p_getScreenFactory_3_) {
      if (p_getScreenFactory_0_ == null) {
         LOG.warn("Trying to open invalid screen with name: {}", p_getScreenFactory_3_.getString());
      } else {
         ScreenManager.IScreenFactory<T, ?> iscreenfactory = getFactory(p_getScreenFactory_0_);
         if (iscreenfactory != null) {
            return Optional.of(iscreenfactory);
         }

         LOG.warn("Failed to create screen for menu type: {}", Registry.MENU.getKey(p_getScreenFactory_0_));
      }

      return Optional.empty();
   }

   @Nullable
   private static <T extends Container> ScreenManager.IScreenFactory<T, ?> getFactory(ContainerType<T> p_216912_0_) {
      return (ScreenManager.IScreenFactory)FACTORIES.get(p_216912_0_);
   }

   public static <M extends Container, U extends Screen & IHasContainer<M>> void registerFactory(ContainerType<? extends M> p_216911_0_, ScreenManager.IScreenFactory<M, U> p_216911_1_) {
      ScreenManager.IScreenFactory<?, ?> iscreenfactory = (ScreenManager.IScreenFactory)FACTORIES.put(p_216911_0_, p_216911_1_);
      if (iscreenfactory != null) {
         throw new IllegalStateException("Duplicate registration for " + Registry.MENU.getKey(p_216911_0_));
      }
   }

   public static boolean isMissingScreen() {
      boolean flag = false;
      Iterator var1 = Registry.MENU.iterator();

      while(var1.hasNext()) {
         ContainerType<?> containertype = (ContainerType)var1.next();
         if (!FACTORIES.containsKey(containertype)) {
            LOG.debug("Menu {} has no matching screen", Registry.MENU.getKey(containertype));
            flag = true;
         }
      }

      return flag;
   }

   static {
      registerFactory(ContainerType.GENERIC_9X1, ChestScreen::new);
      registerFactory(ContainerType.GENERIC_9X2, ChestScreen::new);
      registerFactory(ContainerType.GENERIC_9X3, ChestScreen::new);
      registerFactory(ContainerType.GENERIC_9X4, ChestScreen::new);
      registerFactory(ContainerType.GENERIC_9X5, ChestScreen::new);
      registerFactory(ContainerType.GENERIC_9X6, ChestScreen::new);
      registerFactory(ContainerType.GENERIC_3X3, DispenserScreen::new);
      registerFactory(ContainerType.ANVIL, AnvilScreen::new);
      registerFactory(ContainerType.BEACON, BeaconScreen::new);
      registerFactory(ContainerType.BLAST_FURNACE, BlastFurnaceScreen::new);
      registerFactory(ContainerType.BREWING_STAND, BrewingStandScreen::new);
      registerFactory(ContainerType.CRAFTING, CraftingScreen::new);
      registerFactory(ContainerType.ENCHANTMENT, EnchantmentScreen::new);
      registerFactory(ContainerType.FURNACE, FurnaceScreen::new);
      registerFactory(ContainerType.GRINDSTONE, GrindstoneScreen::new);
      registerFactory(ContainerType.HOPPER, HopperScreen::new);
      registerFactory(ContainerType.LECTERN, LecternScreen::new);
      registerFactory(ContainerType.LOOM, LoomScreen::new);
      registerFactory(ContainerType.MERCHANT, MerchantScreen::new);
      registerFactory(ContainerType.SHULKER_BOX, ShulkerBoxScreen::new);
      registerFactory(ContainerType.SMOKER, SmokerScreen::new);
      registerFactory(ContainerType.field_226625_v_, CartographyTableScreen::new);
      registerFactory(ContainerType.STONECUTTER, StonecutterScreen::new);
   }

   @OnlyIn(Dist.CLIENT)
   public interface IScreenFactory<T extends Container, U extends Screen & IHasContainer<T>> {
      default void createScreen(ITextComponent p_216908_1_, ContainerType<T> p_216908_2_, Minecraft p_216908_3_, int p_216908_4_) {
         U u = this.create(p_216908_2_.create(p_216908_4_, p_216908_3_.player.inventory), p_216908_3_.player.inventory, p_216908_1_);
         p_216908_3_.player.openContainer = ((IHasContainer)u).getContainer();
         p_216908_3_.displayGuiScreen(u);
      }

      U create(T var1, PlayerInventory var2, ITextComponent var3);
   }
}
