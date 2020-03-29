package net.minecraft.util;

import com.mojang.datafixers.Dynamic;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public interface IDynamicDeserializer<T> {
   Logger field_214908_a = LogManager.getLogger();

   T deserialize(Dynamic<?> var1);

   static <T, V, U extends IDynamicDeserializer<V>> V func_214907_a(Dynamic<T> p_214907_0_, Registry<U> p_214907_1_, String p_214907_2_, V p_214907_3_) {
      U lvt_4_1_ = (IDynamicDeserializer)p_214907_1_.getOrDefault(new ResourceLocation(p_214907_0_.get(p_214907_2_).asString("")));
      Object lvt_5_2_;
      if (lvt_4_1_ != null) {
         lvt_5_2_ = lvt_4_1_.deserialize(p_214907_0_);
      } else {
         field_214908_a.error("Unknown type {}, replacing with {}", p_214907_0_.get(p_214907_2_).asString(""), p_214907_3_);
         lvt_5_2_ = p_214907_3_;
      }

      return lvt_5_2_;
   }
}
