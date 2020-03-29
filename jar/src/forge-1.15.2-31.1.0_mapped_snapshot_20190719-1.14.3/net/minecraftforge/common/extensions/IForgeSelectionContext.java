package net.minecraftforge.common.extensions;

import javax.annotation.Nullable;
import net.minecraft.entity.Entity;

public interface IForgeSelectionContext {
   @Nullable
   default Entity getEntity() {
      return null;
   }
}
