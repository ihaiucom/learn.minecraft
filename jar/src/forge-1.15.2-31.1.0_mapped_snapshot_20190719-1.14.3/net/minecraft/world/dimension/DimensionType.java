package net.minecraft.world.dimension;

import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.io.File;
import java.util.function.BiFunction;
import javax.annotation.Nullable;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.IDynamicSerializable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraft.world.biome.ColumnFuzzedBiomeMagnifier;
import net.minecraft.world.biome.FuzzedBiomeMagnifier;
import net.minecraft.world.biome.IBiomeMagnifier;
import net.minecraftforge.common.ModDimension;
import net.minecraftforge.registries.ForgeRegistryEntry;

public class DimensionType extends ForgeRegistryEntry<DimensionType> implements IDynamicSerializable {
   public static final DimensionType OVERWORLD;
   public static final DimensionType THE_NETHER;
   public static final DimensionType THE_END;
   private final int id;
   private final String suffix;
   private final String directory;
   private final BiFunction<World, DimensionType, ? extends Dimension> factory;
   private final boolean field_218273_h;
   private final IBiomeMagnifier field_227175_i_;
   private final boolean isVanilla;
   private final ModDimension modType;
   private final PacketBuffer data;

   private static DimensionType register(String p_212677_0_, DimensionType p_212677_1_) {
      return (DimensionType)Registry.register(Registry.DIMENSION_TYPE, p_212677_1_.id, p_212677_0_, p_212677_1_);
   }

   /** @deprecated */
   @Deprecated
   protected DimensionType(int p_i225789_1_, String p_i225789_2_, String p_i225789_3_, BiFunction<World, DimensionType, ? extends Dimension> p_i225789_4_, boolean p_i225789_5_, IBiomeMagnifier p_i225789_6_) {
      this(p_i225789_1_, p_i225789_2_, p_i225789_3_, p_i225789_4_, p_i225789_5_, p_i225789_6_, (ModDimension)null, (PacketBuffer)null);
   }

   /** @deprecated */
   @Deprecated
   public DimensionType(int p_i230063_1_, String p_i230063_2_, String p_i230063_3_, BiFunction<World, DimensionType, ? extends Dimension> p_i230063_4_, boolean p_i230063_5_, IBiomeMagnifier p_i230063_6_, @Nullable ModDimension p_i230063_7_, @Nullable PacketBuffer p_i230063_8_) {
      this.id = p_i230063_1_;
      this.suffix = p_i230063_2_;
      this.directory = p_i230063_3_;
      this.factory = p_i230063_4_;
      this.field_218273_h = p_i230063_5_;
      this.field_227175_i_ = p_i230063_6_;
      this.isVanilla = this.id >= 0 && this.id <= 2;
      this.modType = p_i230063_7_;
      this.data = p_i230063_8_;
   }

   public static DimensionType func_218271_a(Dynamic<?> p_218271_0_) {
      return (DimensionType)Registry.DIMENSION_TYPE.getOrDefault(new ResourceLocation(p_218271_0_.asString("")));
   }

   public static Iterable<DimensionType> getAll() {
      return Registry.DIMENSION_TYPE;
   }

   public int getId() {
      return this.id + -1;
   }

   /** @deprecated */
   @Deprecated
   public String getSuffix() {
      return this.isVanilla ? this.suffix : "";
   }

   public File getDirectory(File p_212679_1_) {
      return this.directory.isEmpty() ? p_212679_1_ : new File(p_212679_1_, this.directory);
   }

   public Dimension create(World p_218270_1_) {
      return (Dimension)this.factory.apply(p_218270_1_, this);
   }

   public String toString() {
      return "DimensionType{" + getKey(this) + "}";
   }

   @Nullable
   public static DimensionType getById(int p_186069_0_) {
      return (DimensionType)Registry.DIMENSION_TYPE.getByValue(p_186069_0_ - -1);
   }

   public boolean isVanilla() {
      return this.isVanilla;
   }

   @Nullable
   public ModDimension getModType() {
      return this.modType;
   }

   @Nullable
   public PacketBuffer getData() {
      return this.data;
   }

   @Nullable
   public static DimensionType byName(ResourceLocation p_193417_0_) {
      return (DimensionType)Registry.DIMENSION_TYPE.getOrDefault(p_193417_0_);
   }

   @Nullable
   public static ResourceLocation getKey(DimensionType p_212678_0_) {
      return Registry.DIMENSION_TYPE.getKey(p_212678_0_);
   }

   public boolean func_218272_d() {
      return this.field_218273_h;
   }

   public IBiomeMagnifier func_227176_e_() {
      return this.field_227175_i_;
   }

   public <T> T serialize(DynamicOps<T> p_218175_1_) {
      return p_218175_1_.createString(Registry.DIMENSION_TYPE.getKey(this).toString());
   }

   static {
      OVERWORLD = register("overworld", new DimensionType(1, "", "", OverworldDimension::new, true, ColumnFuzzedBiomeMagnifier.INSTANCE));
      THE_NETHER = register("the_nether", new DimensionType(0, "_nether", "DIM-1", NetherDimension::new, false, FuzzedBiomeMagnifier.INSTANCE));
      THE_END = register("the_end", new DimensionType(2, "_end", "DIM1", EndDimension::new, false, FuzzedBiomeMagnifier.INSTANCE));
   }
}
