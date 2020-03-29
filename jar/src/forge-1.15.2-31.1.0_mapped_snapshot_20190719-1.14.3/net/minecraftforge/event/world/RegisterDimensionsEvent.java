package net.minecraftforge.event.world;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.eventbus.api.Event;

public class RegisterDimensionsEvent extends Event {
   private final Map<ResourceLocation, DimensionManager.SavedEntry> missing;
   private final Set<ResourceLocation> keys;

   public RegisterDimensionsEvent(Map<ResourceLocation, DimensionManager.SavedEntry> missing) {
      this.missing = missing;
      this.keys = Collections.unmodifiableSet(this.missing.keySet());
   }

   public Set<ResourceLocation> getMissingNames() {
      return this.keys;
   }

   @Nullable
   public DimensionManager.SavedEntry getEntry(ResourceLocation key) {
      return (DimensionManager.SavedEntry)this.missing.get(key);
   }
}
