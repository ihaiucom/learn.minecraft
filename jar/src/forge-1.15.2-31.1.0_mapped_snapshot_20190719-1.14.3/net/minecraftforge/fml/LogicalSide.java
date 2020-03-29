package net.minecraftforge.fml;

public enum LogicalSide {
   CLIENT,
   SERVER;

   public boolean isServer() {
      return !this.isClient();
   }

   public boolean isClient() {
      return this == CLIENT;
   }
}
