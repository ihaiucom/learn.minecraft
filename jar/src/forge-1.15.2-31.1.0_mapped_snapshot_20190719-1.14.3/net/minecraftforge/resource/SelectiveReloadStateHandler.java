package net.minecraftforge.resource;

import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.resources.IResourceManagerReloadListener;
import net.minecraftforge.common.ForgeConfig;

public enum SelectiveReloadStateHandler {
   INSTANCE;

   @Nullable
   private Predicate<IResourceType> currentPredicate = null;

   public void beginReload(Predicate<IResourceType> resourcePredicate) {
      if (this.currentPredicate != null) {
         throw new IllegalStateException("Recursive resource reloading detected");
      } else {
         this.currentPredicate = resourcePredicate;
      }
   }

   public Predicate<IResourceType> get() {
      return this.currentPredicate != null && (Boolean)ForgeConfig.CLIENT.selectiveResourceReloadEnabled.get() ? this.currentPredicate : ReloadRequirements.all();
   }

   public void endReload() {
      this.currentPredicate = null;
   }

   public boolean test(IResourceManagerReloadListener listener) {
      IResourceType type = listener.getResourceType();
      return type == null || this.get() == null || this.get().test(type);
   }
}
