package net.minecraftforge.common.extensions;

import java.util.Random;
import java.util.function.LongFunction;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.CreateBuffetWorldScreen;
import net.minecraft.client.gui.screen.CreateFlatWorldScreen;
import net.minecraft.client.gui.screen.CreateWorldScreen;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.IExtendedNoiseRandom;
import net.minecraft.world.gen.OverworldGenSettings;
import net.minecraft.world.gen.area.IArea;
import net.minecraft.world.gen.area.IAreaFactory;
import net.minecraft.world.gen.layer.AddBambooForestLayer;
import net.minecraft.world.gen.layer.BiomeLayer;
import net.minecraft.world.gen.layer.EdgeBiomeLayer;
import net.minecraft.world.gen.layer.LayerUtil;
import net.minecraft.world.gen.layer.ZoomLayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public interface IForgeWorldType {
   default WorldType getWorldType() {
      return (WorldType)this;
   }

   default void onGUICreateWorldPress() {
   }

   @OnlyIn(Dist.CLIENT)
   default void onCustomizeButton(Minecraft mc, CreateWorldScreen gui) {
      if (this == WorldType.FLAT) {
         mc.displayGuiScreen(new CreateFlatWorldScreen(gui, gui.chunkProviderSettingsJson));
      } else if (this == WorldType.BUFFET) {
         mc.displayGuiScreen(new CreateBuffetWorldScreen(gui, gui.chunkProviderSettingsJson));
      }

   }

   default boolean handleSlimeSpawnReduction(Random random, IWorld world) {
      return this == WorldType.FLAT ? random.nextInt(4) != 1 : false;
   }

   default double getHorizon(World world) {
      return this == WorldType.FLAT ? 0.0D : 63.0D;
   }

   default double voidFadeMagnitude() {
      return this == WorldType.FLAT ? 1.0D : 0.03125D;
   }

   default float getCloudHeight() {
      return 128.0F;
   }

   default ChunkGenerator<?> createChunkGenerator(World world) {
      return world.dimension.createChunkGenerator();
   }

   default <T extends IArea, C extends IExtendedNoiseRandom<T>> IAreaFactory<T> getBiomeLayer(IAreaFactory<T> parentLayer, OverworldGenSettings chunkSettings, LongFunction<C> contextFactory) {
      parentLayer = (new BiomeLayer(this.getWorldType(), chunkSettings.getBiomeId())).apply((IExtendedNoiseRandom)contextFactory.apply(200L), parentLayer);
      parentLayer = AddBambooForestLayer.INSTANCE.apply((IExtendedNoiseRandom)contextFactory.apply(1001L), parentLayer);
      parentLayer = LayerUtil.repeat(1000L, ZoomLayer.NORMAL, parentLayer, 2, contextFactory);
      parentLayer = EdgeBiomeLayer.INSTANCE.apply((IExtendedNoiseRandom)contextFactory.apply(1000L), parentLayer);
      return parentLayer;
   }
}
