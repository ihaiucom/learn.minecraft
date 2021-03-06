package net.minecraftforge.common.ticket;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.WeakHashMap;
import net.minecraft.util.math.ChunkPos;

public class ChunkTicketManager<T> implements ITicketGetter<T> {
   private final Set<SimpleTicket<T>> tickets = Collections.newSetFromMap(new WeakHashMap());
   public final ChunkPos pos;

   public ChunkTicketManager(ChunkPos pos) {
      this.pos = pos;
   }

   public void add(SimpleTicket<T> ticket) {
      this.tickets.add(ticket);
   }

   public void remove(SimpleTicket<T> ticket) {
      this.tickets.remove(ticket);
   }

   public Collection<SimpleTicket<T>> getTickets() {
      return this.tickets;
   }
}
