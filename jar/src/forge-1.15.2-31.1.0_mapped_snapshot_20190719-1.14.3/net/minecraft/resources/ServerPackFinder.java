package net.minecraft.resources;

import java.util.Map;

public class ServerPackFinder implements IPackFinder {
   private final VanillaPack field_195738_a = new VanillaPack(new String[]{"minecraft"});

   public <T extends ResourcePackInfo> void addPackInfosToMap(Map<String, T> p_195730_1_, ResourcePackInfo.IFactory<T> p_195730_2_) {
      T lvt_3_1_ = ResourcePackInfo.createResourcePack("vanilla", false, () -> {
         return this.field_195738_a;
      }, p_195730_2_, ResourcePackInfo.Priority.BOTTOM);
      if (lvt_3_1_ != null) {
         p_195730_1_.put("vanilla", lvt_3_1_);
      }

   }
}
