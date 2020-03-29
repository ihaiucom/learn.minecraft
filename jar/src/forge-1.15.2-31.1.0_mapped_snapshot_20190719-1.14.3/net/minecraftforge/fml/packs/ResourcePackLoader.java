package net.minecraftforge.fml.packs;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.minecraft.resources.IPackFinder;
import net.minecraft.resources.ResourcePackInfo;
import net.minecraft.resources.ResourcePackList;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.moddiscovery.ModFile;
import net.minecraftforge.fml.loading.moddiscovery.ModFileInfo;

public class ResourcePackLoader {
   private static Map<ModFile, ModFileResourcePack> modResourcePacks;
   private static ResourcePackList<?> resourcePackList;

   public static Optional<ModFileResourcePack> getResourcePackFor(String modId) {
      return Optional.ofNullable(ModList.get().getModFileById(modId)).map(ModFileInfo::getFile).map((mf) -> {
         return (ModFileResourcePack)modResourcePacks.get(mf);
      });
   }

   public static <T extends ResourcePackInfo> void loadResourcePacks(ResourcePackList<T> resourcePacks, BiFunction<Map<ModFile, ? extends ModFileResourcePack>, BiConsumer<? super ModFileResourcePack, T>, ResourcePackLoader.IPackInfoFinder> packFinder) {
      resourcePackList = resourcePacks;
      modResourcePacks = (Map)ModList.get().getModFiles().stream().filter((mf) -> {
         return !Objects.equals(mf.getModLoader(), "minecraft");
      }).map((mf) -> {
         return new ModFileResourcePack(mf.getFile());
      }).collect(Collectors.toMap(ModFileResourcePack::getModFile, Function.identity()));
      resourcePacks.addPackFinder(new ResourcePackLoader.LambdaFriendlyPackFinder((ResourcePackLoader.IPackInfoFinder)packFinder.apply(modResourcePacks, ModFileResourcePack::setPackInfo)));
   }

   private static class LambdaFriendlyPackFinder implements IPackFinder {
      private ResourcePackLoader.IPackInfoFinder wrapped;

      private LambdaFriendlyPackFinder(ResourcePackLoader.IPackInfoFinder wrapped) {
         this.wrapped = wrapped;
      }

      public <T extends ResourcePackInfo> void addPackInfosToMap(Map<String, T> packList, ResourcePackInfo.IFactory<T> factory) {
         this.wrapped.addPackInfosToMap(packList, factory);
      }

      // $FF: synthetic method
      LambdaFriendlyPackFinder(ResourcePackLoader.IPackInfoFinder x0, Object x1) {
         this(x0);
      }
   }

   public interface IPackInfoFinder<T extends ResourcePackInfo> {
      void addPackInfosToMap(Map<String, T> var1, ResourcePackInfo.IFactory<T> var2);
   }
}
