package net.minecraftforge.common.model.animation;

import net.minecraftforge.common.animation.Event;

public interface IClip {
   IJointClip apply(IJoint var1);

   Iterable<Event> pastEvents(float var1, float var2);
}
