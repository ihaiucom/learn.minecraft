package net.minecraftforge.common.model.animation;

import net.minecraft.client.renderer.model.IModelTransform;
import net.minecraftforge.common.animation.Event;
import org.apache.commons.lang3.tuple.Pair;

public interface IAnimationStateMachine {
   Pair<IModelTransform, Iterable<Event>> apply(float var1);

   void transition(String var1);

   String currentState();

   void shouldHandleSpecialEvents(boolean var1);
}
