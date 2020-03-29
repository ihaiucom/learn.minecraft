package net.minecraftforge.fml;

import java.util.Iterator;
import java.util.Spliterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class InterModComms {
   private static ConcurrentMap<String, ConcurrentLinkedQueue<InterModComms.IMCMessage>> containerQueues = new ConcurrentHashMap();

   public static boolean sendTo(String modId, String method, Supplier<?> thing) {
      if (!ModList.get().isLoaded(modId)) {
         return false;
      } else {
         ((ConcurrentLinkedQueue)containerQueues.computeIfAbsent(modId, (k) -> {
            return new ConcurrentLinkedQueue();
         })).add(new InterModComms.IMCMessage(ModLoadingContext.get().getActiveContainer().getModId(), modId, method, thing));
         return true;
      }
   }

   public static boolean sendTo(String senderModId, String modId, String method, Supplier<?> thing) {
      if (!ModList.get().isLoaded(modId)) {
         return false;
      } else {
         ((ConcurrentLinkedQueue)containerQueues.computeIfAbsent(modId, (k) -> {
            return new ConcurrentLinkedQueue();
         })).add(new InterModComms.IMCMessage(senderModId, modId, method, thing));
         return true;
      }
   }

   public static Stream<InterModComms.IMCMessage> getMessages(String modId, Predicate<String> methodMatcher) {
      ConcurrentLinkedQueue<InterModComms.IMCMessage> queue = (ConcurrentLinkedQueue)containerQueues.get(modId);
      return queue == null ? Stream.empty() : StreamSupport.stream(new InterModComms.QueueFilteringSpliterator(queue, methodMatcher), false);
   }

   public static Stream<InterModComms.IMCMessage> getMessages(String modId) {
      return getMessages(modId, (s) -> {
         return Boolean.TRUE;
      });
   }

   private static class QueueFilteringSpliterator implements Spliterator<InterModComms.IMCMessage> {
      private final ConcurrentLinkedQueue<InterModComms.IMCMessage> queue;
      private final Predicate<String> methodFilter;
      private final Iterator<InterModComms.IMCMessage> iterator;

      public QueueFilteringSpliterator(ConcurrentLinkedQueue<InterModComms.IMCMessage> queue, Predicate<String> methodFilter) {
         this.queue = queue;
         this.iterator = queue.iterator();
         this.methodFilter = methodFilter;
      }

      public int characteristics() {
         return 4368;
      }

      public long estimateSize() {
         return (long)this.queue.size();
      }

      public boolean tryAdvance(Consumer<? super InterModComms.IMCMessage> action) {
         InterModComms.IMCMessage next;
         do {
            if (!this.iterator.hasNext()) {
               return false;
            }

            next = (InterModComms.IMCMessage)this.iterator.next();
         } while(!this.methodFilter.test(next.method));

         action.accept(next);
         this.iterator.remove();
         return true;
      }

      public Spliterator<InterModComms.IMCMessage> trySplit() {
         return null;
      }
   }

   public static final class IMCMessage {
      private final String modId;
      private final String method;
      private final String senderModId;
      private final Supplier<?> thing;

      IMCMessage(String senderModId, String modId, String method, Supplier<?> thing) {
         this.senderModId = senderModId;
         this.modId = modId;
         this.method = method;
         this.thing = thing;
      }

      public final String getSenderModId() {
         return this.senderModId;
      }

      public final String getModId() {
         return this.modId;
      }

      public final String getMethod() {
         return this.method;
      }

      public final <T> Supplier<T> getMessageSupplier() {
         return this.thing;
      }
   }
}
