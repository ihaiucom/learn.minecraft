package net.minecraft.world;

import it.unimi.dsi.fastutil.longs.LongSet;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.world.gen.feature.structure.StructureStart;

public interface IStructureReader {
   @Nullable
   StructureStart getStructureStart(String var1);

   void putStructureStart(String var1, StructureStart var2);

   LongSet getStructureReferences(String var1);

   void addStructureReference(String var1, long var2);

   Map<String, LongSet> getStructureReferences();

   void setStructureReferences(Map<String, LongSet> var1);
}
