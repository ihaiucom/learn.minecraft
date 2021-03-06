package net.minecraftforge.common.ticket;

import com.google.common.base.Preconditions;
import java.util.function.Consumer;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class SimpleTicket<T> {
   @Nullable
   private ITicketManager<T> masterManager;
   private ITicketManager<T>[] dummyManagers;
   protected boolean isValid = false;

   @SafeVarargs
   public final void setManager(@Nonnull ITicketManager<T> masterManager, @Nonnull ITicketManager<T>... dummyManagers) {
      Preconditions.checkState(this.masterManager == null, "Ticket is already registered to a managing system");
      this.masterManager = masterManager;
      this.dummyManagers = dummyManagers;
   }

   public boolean isValid() {
      return this.isValid;
   }

   public void invalidate() {
      if (this.isValid()) {
         this.forEachManager((ticketManager) -> {
            ticketManager.remove(this);
         });
      }

      this.isValid = false;
   }

   public boolean unload(ITicketManager<T> unloadingManager) {
      if (unloadingManager != this.masterManager) {
         return false;
      } else {
         ITicketManager[] var2 = this.dummyManagers;
         int var3 = var2.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            ITicketManager<T> manager = var2[var4];
            manager.remove(this);
         }

         this.isValid = false;
         return true;
      }
   }

   public void validate() {
      if (!this.isValid()) {
         this.forEachManager((ticketManager) -> {
            ticketManager.add(this);
         });
      }

      this.isValid = true;
   }

   public abstract boolean matches(T var1);

   protected final void forEachManager(Consumer<ITicketManager<T>> consumer) {
      Preconditions.checkState(this.masterManager != null, "Ticket is not registered to a managing system");
      consumer.accept(this.masterManager);
      ITicketManager[] var2 = this.dummyManagers;
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         ITicketManager<T> manager = var2[var4];
         consumer.accept(manager);
      }

   }

   protected final ITicketManager<T> getMasterManager() {
      return this.masterManager;
   }

   protected final ITicketManager<T>[] getDummyManagers() {
      return this.dummyManagers;
   }
}
