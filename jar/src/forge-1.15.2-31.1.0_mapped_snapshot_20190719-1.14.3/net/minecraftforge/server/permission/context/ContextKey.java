package net.minecraftforge.server.permission.context;

import com.google.common.base.Preconditions;

public final class ContextKey<T> {
   private final String ID;
   private final Class<T> typeClass;

   public static <E> ContextKey<E> create(String id, Class<E> c) {
      Preconditions.checkNotNull(id, "ContextKey's ID can't be null!");
      Preconditions.checkNotNull(c, "ContextKey's Type can't be null!");
      if (id.isEmpty()) {
         throw new IllegalArgumentException("ContextKey's ID can't be blank!");
      } else {
         return new ContextKey(id, c);
      }
   }

   private ContextKey(String id, Class<T> c) {
      this.ID = id;
      this.typeClass = c;
   }

   public String toString() {
      return this.ID;
   }

   public int hashCode() {
      return this.ID.hashCode();
   }

   public boolean equals(Object o) {
      return o == this || o != null && o.toString().equals(this.ID);
   }

   public Class<T> getTypeClass() {
      return this.typeClass;
   }
}
