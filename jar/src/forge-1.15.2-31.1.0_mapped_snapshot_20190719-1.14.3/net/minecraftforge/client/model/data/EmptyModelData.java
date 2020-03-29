package net.minecraftforge.client.model.data;

public enum EmptyModelData implements IModelData {
   INSTANCE;

   public boolean hasProperty(ModelProperty<?> prop) {
      return false;
   }

   public <T> T getData(ModelProperty<T> prop) {
      return null;
   }

   public <T> T setData(ModelProperty<T> prop, T data) {
      return null;
   }
}
