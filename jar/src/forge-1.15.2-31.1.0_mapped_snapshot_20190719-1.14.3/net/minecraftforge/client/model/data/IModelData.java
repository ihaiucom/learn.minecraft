package net.minecraftforge.client.model.data;

import javax.annotation.Nullable;

public interface IModelData {
   boolean hasProperty(ModelProperty<?> var1);

   @Nullable
   <T> T getData(ModelProperty<T> var1);

   @Nullable
   <T> T setData(ModelProperty<T> var1, T var2);
}
