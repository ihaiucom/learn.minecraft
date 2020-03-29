package net.minecraftforge.resource;

import com.google.common.collect.Sets;
import java.util.Set;
import java.util.function.Predicate;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public final class ReloadRequirements {
   public static Predicate<IResourceType> all() {
      return (type) -> {
         return true;
      };
   }

   public static Predicate<IResourceType> include(IResourceType... inclusion) {
      Set<IResourceType> inclusionSet = Sets.newHashSet(inclusion);
      return inclusionSet::contains;
   }
}
