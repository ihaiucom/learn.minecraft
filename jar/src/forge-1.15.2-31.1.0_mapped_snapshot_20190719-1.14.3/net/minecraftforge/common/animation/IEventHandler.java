package net.minecraftforge.common.animation;

public interface IEventHandler<T> {
   void handleEvents(T var1, float var2, Iterable<Event> var3);
}
