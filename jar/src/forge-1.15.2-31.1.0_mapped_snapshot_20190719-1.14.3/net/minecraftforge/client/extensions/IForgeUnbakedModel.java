package net.minecraftforge.client.extensions;

import java.util.Optional;
import net.minecraftforge.common.model.animation.IClip;

public interface IForgeUnbakedModel {
   default Optional<? extends IClip> getClip(String name) {
      return Optional.empty();
   }
}
