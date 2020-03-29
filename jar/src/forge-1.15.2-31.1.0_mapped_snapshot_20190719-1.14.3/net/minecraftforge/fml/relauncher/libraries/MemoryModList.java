package net.minecraftforge.fml.relauncher.libraries;

import java.io.IOException;

public class MemoryModList extends ModList {
   MemoryModList(Repository repo) {
      super(repo);
   }

   public void save() throws IOException {
   }

   public String getName() {
      return "MEMORY";
   }
}
