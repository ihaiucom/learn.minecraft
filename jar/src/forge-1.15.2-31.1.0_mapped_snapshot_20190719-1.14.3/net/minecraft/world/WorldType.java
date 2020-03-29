package net.minecraft.world;

import java.util.Arrays;
import javax.annotation.Nullable;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.extensions.IForgeWorldType;

public class WorldType implements IForgeWorldType {
   public static WorldType[] WORLD_TYPES = new WorldType[16];
   public static final WorldType DEFAULT = (new WorldType(0, "default", 1)).setVersioned();
   public static final WorldType FLAT = (new WorldType(1, "flat")).setCustomOptions(true);
   public static final WorldType LARGE_BIOMES = new WorldType(2, "largeBiomes");
   public static final WorldType AMPLIFIED = (new WorldType(3, "amplified")).enableInfoNotice();
   public static final WorldType CUSTOMIZED = (new WorldType(4, "customized", "normal", 0)).setCustomOptions(true).setCanBeCreated(false);
   public static final WorldType BUFFET = (new WorldType(5, "buffet")).setCustomOptions(true);
   public static final WorldType DEBUG_ALL_BLOCK_STATES = new WorldType(6, "debug_all_block_states");
   public static final WorldType DEFAULT_1_1 = (new WorldType(8, "default_1_1", 0)).setCanBeCreated(false);
   private final int id;
   private final String name;
   private final String field_211890_l;
   private final int version;
   private boolean canBeCreated;
   private boolean versioned;
   private boolean hasInfoNotice;
   private boolean field_205395_p;

   public WorldType(String p_i230068_1_) {
      this(getNextID(), p_i230068_1_);
   }

   private WorldType(int p_i1959_1_, String p_i1959_2_) {
      this(p_i1959_1_, p_i1959_2_, p_i1959_2_, 0);
   }

   private WorldType(int p_i1960_1_, String p_i1960_2_, int p_i1960_3_) {
      this(p_i1960_1_, p_i1960_2_, p_i1960_2_, p_i1960_3_);
   }

   private WorldType(int p_i49778_1_, String p_i49778_2_, String p_i49778_3_, int p_i49778_4_) {
      if (p_i49778_2_.length() > 16 && DEBUG_ALL_BLOCK_STATES != null) {
         throw new IllegalArgumentException("World type names must not be longer then 16: " + p_i49778_2_);
      } else {
         this.name = p_i49778_2_;
         this.field_211890_l = p_i49778_3_;
         this.version = p_i49778_4_;
         this.canBeCreated = true;
         this.id = p_i49778_1_;
         WORLD_TYPES[p_i49778_1_] = this;
      }
   }

   private static int getNextID() {
      int x;
      for(x = 0; x < WORLD_TYPES.length; ++x) {
         if (WORLD_TYPES[x] == null) {
            return x;
         }
      }

      x = WORLD_TYPES.length;
      WORLD_TYPES = (WorldType[])Arrays.copyOf(WORLD_TYPES, x + 16);
      return x;
   }

   public String getName() {
      return this.name;
   }

   public String getSerialization() {
      return this.field_211890_l;
   }

   @OnlyIn(Dist.CLIENT)
   public String getTranslationKey() {
      return "generator." + this.name;
   }

   @OnlyIn(Dist.CLIENT)
   public String getInfoTranslationKey() {
      return this.getTranslationKey() + ".info";
   }

   public int getVersion() {
      return this.version;
   }

   public WorldType getWorldTypeForGeneratorVersion(int p_77132_1_) {
      return this == DEFAULT && p_77132_1_ == 0 ? DEFAULT_1_1 : this;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean hasCustomOptions() {
      return this.field_205395_p;
   }

   public WorldType setCustomOptions(boolean p_205392_1_) {
      this.field_205395_p = p_205392_1_;
      return this;
   }

   private WorldType setCanBeCreated(boolean p_77124_1_) {
      this.canBeCreated = p_77124_1_;
      return this;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean canBeCreated() {
      return this.canBeCreated;
   }

   private WorldType setVersioned() {
      this.versioned = true;
      return this;
   }

   public boolean isVersioned() {
      return this.versioned;
   }

   @Nullable
   public static WorldType byName(String p_77130_0_) {
      WorldType[] var1 = WORLD_TYPES;
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         WorldType worldtype = var1[var3];
         if (worldtype != null && worldtype.name.equalsIgnoreCase(p_77130_0_)) {
            return worldtype;
         }
      }

      return null;
   }

   public int getId() {
      return this.id;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean hasInfoNotice() {
      return this.hasInfoNotice;
   }

   private WorldType enableInfoNotice() {
      this.hasInfoNotice = true;
      return this;
   }
}
