package net.minecraft.advancements;

import java.util.Collection;
import java.util.Iterator;

public interface IRequirementsStrategy {
   IRequirementsStrategy AND = (p_223249_0_) -> {
      String[][] lvt_1_1_ = new String[p_223249_0_.size()][];
      int lvt_2_1_ = 0;

      String lvt_4_1_;
      for(Iterator var3 = p_223249_0_.iterator(); var3.hasNext(); lvt_1_1_[lvt_2_1_++] = new String[]{lvt_4_1_}) {
         lvt_4_1_ = (String)var3.next();
      }

      return lvt_1_1_;
   };
   IRequirementsStrategy OR = (p_223248_0_) -> {
      return new String[][]{(String[])p_223248_0_.toArray(new String[0])};
   };

   String[][] createRequirements(Collection<String> var1);
}
