package net.minecraftforge.client.model.data;

import com.google.common.base.Preconditions;
import java.util.IdentityHashMap;
import java.util.Map;

public class ModelDataMap implements IModelData {
   private final Map<ModelProperty<?>, Object> backingMap;

   private ModelDataMap(Map<ModelProperty<?>, Object> map) {
      this.backingMap = new IdentityHashMap(map);
   }

   public boolean hasProperty(ModelProperty<?> prop) {
      return this.backingMap.containsKey(prop);
   }

   public <T> T getData(ModelProperty<T> prop) {
      return this.backingMap.get(prop);
   }

   public <T> T setData(ModelProperty<T> prop, T data) {
      Preconditions.checkArgument(prop.test(data), "Value is invalid for this property");
      return this.backingMap.put(prop, data);
   }

   // $FF: synthetic method
   ModelDataMap(Map x0, Object x1) {
      this(x0);
   }

   public static class Builder {
      private final Map<ModelProperty<?>, Object> defaults = new IdentityHashMap();

      public ModelDataMap.Builder withProperty(ModelProperty<?> prop) {
         return this.withInitial(prop, (Object)null);
      }

      public <T> ModelDataMap.Builder withInitial(ModelProperty<T> prop, T data) {
         this.defaults.put(prop, data);
         return this;
      }

      public ModelDataMap build() {
         return new ModelDataMap(this.defaults);
      }
   }
}
