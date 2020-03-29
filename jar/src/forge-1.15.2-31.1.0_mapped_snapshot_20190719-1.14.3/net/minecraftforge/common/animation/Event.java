package net.minecraftforge.common.animation;

import com.google.common.base.MoreObjects;

public final class Event implements Comparable<Event> {
   private final String event;
   private final float offset;

   public Event(String event, float offset) {
      this.event = event;
      this.offset = offset;
   }

   public String event() {
      return this.event;
   }

   public float offset() {
      return this.offset;
   }

   public int compareTo(Event event) {
      return (new Float(this.offset)).compareTo(event.offset);
   }

   public String toString() {
      return MoreObjects.toStringHelper(this.getClass()).add("event", this.event).add("offset", this.offset).toString();
   }
}
