package net.minecraft.state;

import com.google.common.collect.ImmutableMap;
import java.util.Optional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public interface IStateHolder<C> {
   Logger field_215672_b = LogManager.getLogger();

   <T extends Comparable<T>> T get(IProperty<T> var1);

   <T extends Comparable<T>, V extends T> C with(IProperty<T> var1, V var2);

   ImmutableMap<IProperty<?>, Comparable<?>> getValues();

   static <T extends Comparable<T>> String func_215670_b(IProperty<T> p_215670_0_, Comparable<?> p_215670_1_) {
      return p_215670_0_.getName(p_215670_1_);
   }

   static <S extends IStateHolder<S>, T extends Comparable<T>> S func_215671_a(S p_215671_0_, IProperty<T> p_215671_1_, String p_215671_2_, String p_215671_3_, String p_215671_4_) {
      Optional<T> lvt_5_1_ = p_215671_1_.parseValue(p_215671_4_);
      if (lvt_5_1_.isPresent()) {
         return (IStateHolder)p_215671_0_.with(p_215671_1_, (Comparable)lvt_5_1_.get());
      } else {
         field_215672_b.warn("Unable to read property: {} with value: {} for input: {}", p_215671_2_, p_215671_4_, p_215671_3_);
         return p_215671_0_;
      }
   }
}
