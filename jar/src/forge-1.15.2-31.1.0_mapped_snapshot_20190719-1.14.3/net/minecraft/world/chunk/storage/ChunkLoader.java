package net.minecraft.world.chunk.storage;

import com.mojang.datafixers.DataFixer;
import java.io.File;
import java.io.IOException;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.SharedConstants;
import net.minecraft.util.datafix.DefaultTypeReferences;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.feature.structure.LegacyStructureDataUtil;
import net.minecraft.world.storage.DimensionSavedDataManager;

public class ChunkLoader implements AutoCloseable {
   private final IOWorker field_227077_a_;
   protected final DataFixer dataFixer;
   @Nullable
   private LegacyStructureDataUtil field_219167_a;

   public ChunkLoader(File p_i49939_1_, DataFixer p_i49939_2_) {
      this.dataFixer = p_i49939_2_;
      this.field_227077_a_ = new IOWorker(new RegionFileCache(p_i49939_1_), "chunk");
   }

   public CompoundNBT updateChunkData(DimensionType p_219166_1_, Supplier<DimensionSavedDataManager> p_219166_2_, CompoundNBT p_219166_3_) {
      int lvt_4_1_ = getDataVersion(p_219166_3_);
      int lvt_5_1_ = true;
      if (lvt_4_1_ < 1493) {
         p_219166_3_ = NBTUtil.update(this.dataFixer, DefaultTypeReferences.CHUNK, p_219166_3_, lvt_4_1_, 1493);
         if (p_219166_3_.getCompound("Level").getBoolean("hasLegacyStructureData")) {
            if (this.field_219167_a == null) {
               this.field_219167_a = LegacyStructureDataUtil.func_215130_a(p_219166_1_, (DimensionSavedDataManager)p_219166_2_.get());
            }

            p_219166_3_ = this.field_219167_a.func_212181_a(p_219166_3_);
         }
      }

      p_219166_3_ = NBTUtil.update(this.dataFixer, DefaultTypeReferences.CHUNK, p_219166_3_, Math.max(1493, lvt_4_1_));
      if (lvt_4_1_ < SharedConstants.getVersion().getWorldVersion()) {
         p_219166_3_.putInt("DataVersion", SharedConstants.getVersion().getWorldVersion());
      }

      return p_219166_3_;
   }

   public static int getDataVersion(CompoundNBT p_219165_0_) {
      return p_219165_0_.contains("DataVersion", 99) ? p_219165_0_.getInt("DataVersion") : -1;
   }

   @Nullable
   public CompoundNBT func_227078_e_(ChunkPos p_227078_1_) throws IOException {
      return this.field_227077_a_.func_227090_a_(p_227078_1_);
   }

   public void writeChunk(ChunkPos p_219100_1_, CompoundNBT p_219100_2_) {
      this.field_227077_a_.func_227093_a_(p_219100_1_, p_219100_2_);
      if (this.field_219167_a != null) {
         this.field_219167_a.func_208216_a(p_219100_1_.asLong());
      }

   }

   public void func_227079_i_() {
      this.field_227077_a_.func_227088_a_().join();
   }

   public void close() throws IOException {
      this.field_227077_a_.close();
   }
}
