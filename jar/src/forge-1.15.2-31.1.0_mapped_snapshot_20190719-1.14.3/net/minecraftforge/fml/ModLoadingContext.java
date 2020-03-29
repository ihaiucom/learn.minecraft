package net.minecraftforge.fml;

import java.util.function.Supplier;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.config.ModConfig;

public class ModLoadingContext {
   private static ThreadLocal<ModLoadingContext> context = ThreadLocal.withInitial(ModLoadingContext::new);
   private Object languageExtension;
   private ModContainer activeContainer;

   public static ModLoadingContext get() {
      return (ModLoadingContext)context.get();
   }

   public void setActiveContainer(ModContainer container, Object languageExtension) {
      this.activeContainer = container;
      this.languageExtension = languageExtension;
   }

   public ModContainer getActiveContainer() {
      return this.activeContainer == null ? (ModContainer)ModList.get().getModContainerById("minecraft").orElseThrow(() -> {
         return new RuntimeException("Where is minecraft???!");
      }) : this.activeContainer;
   }

   public String getActiveNamespace() {
      return this.activeContainer == null ? "minecraft" : this.activeContainer.getNamespace();
   }

   public <T> void registerExtensionPoint(ExtensionPoint<T> point, Supplier<T> extension) {
      this.getActiveContainer().registerExtensionPoint(point, extension);
   }

   public void registerConfig(ModConfig.Type type, ForgeConfigSpec spec) {
      this.getActiveContainer().addConfig(new ModConfig(type, spec, this.getActiveContainer()));
   }

   public void registerConfig(ModConfig.Type type, ForgeConfigSpec spec, String fileName) {
      this.getActiveContainer().addConfig(new ModConfig(type, spec, this.getActiveContainer(), fileName));
   }

   public <T> T extension() {
      return this.languageExtension;
   }
}
