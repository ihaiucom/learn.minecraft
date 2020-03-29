package net.minecraftforge.common.model.animation;

import java.util.Optional;
import net.minecraft.client.renderer.TransformationMatrix;

public interface IJoint {
   TransformationMatrix getInvBindPose();

   Optional<? extends IJoint> getParent();
}
