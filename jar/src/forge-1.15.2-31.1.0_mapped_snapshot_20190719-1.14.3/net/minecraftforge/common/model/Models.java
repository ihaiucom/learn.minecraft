package net.minecraftforge.common.model;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.UnmodifiableIterator;

public enum Models {
   public static Object getHiddenModelPart(ImmutableList<String> path) {
      return new HiddenModelPart(path);
   }

   public static UnmodifiableIterator<String> getParts(Object part) {
      if (part instanceof HiddenModelPart) {
         return ((HiddenModelPart)part).getPath().iterator();
      } else {
         ImmutableSet<String> ret = ImmutableSet.of();
         return ret.iterator();
      }
   }
}
