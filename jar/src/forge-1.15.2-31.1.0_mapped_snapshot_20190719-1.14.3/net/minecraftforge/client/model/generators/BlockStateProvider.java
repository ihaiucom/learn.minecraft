package net.minecraftforge.client.model.generators;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import javax.annotation.Nonnull;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.DoorBlock;
import net.minecraft.block.FenceBlock;
import net.minecraft.block.FenceGateBlock;
import net.minecraft.block.FourWayBlock;
import net.minecraft.block.LogBlock;
import net.minecraft.block.PaneBlock;
import net.minecraft.block.RotatedPillarBlock;
import net.minecraft.block.SixWayBlock;
import net.minecraft.block.SlabBlock;
import net.minecraft.block.StairsBlock;
import net.minecraft.block.TrapDoorBlock;
import net.minecraft.block.WallBlock;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DirectoryCache;
import net.minecraft.data.IDataProvider;
import net.minecraft.state.IProperty;
import net.minecraft.state.properties.AttachFace;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.DoorHingeSide;
import net.minecraft.state.properties.DoubleBlockHalf;
import net.minecraft.state.properties.Half;
import net.minecraft.state.properties.SlabType;
import net.minecraft.state.properties.StairsShape;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class BlockStateProvider implements IDataProvider {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();
   @VisibleForTesting
   protected final Map<Block, IGeneratedBlockstate> registeredBlocks = new LinkedHashMap();
   private final DataGenerator generator;
   private final String modid;
   private final BlockModelProvider blockModels;
   private static final int DEFAULT_ANGLE_OFFSET = 180;

   public BlockStateProvider(DataGenerator gen, String modid, ExistingFileHelper exFileHelper) {
      this.generator = gen;
      this.modid = modid;
      this.blockModels = new BlockModelProvider(gen, modid, exFileHelper) {
         public String getName() {
            return BlockStateProvider.this.getName();
         }

         protected void registerModels() {
         }
      };
   }

   public void act(DirectoryCache cache) throws IOException {
      this.models().clear();
      this.registeredBlocks.clear();
      this.registerStatesAndModels();
      this.models().generateAll(cache);
      Iterator var2 = this.registeredBlocks.entrySet().iterator();

      while(var2.hasNext()) {
         Entry<Block, IGeneratedBlockstate> entry = (Entry)var2.next();
         this.saveBlockState(cache, ((IGeneratedBlockstate)entry.getValue()).toJson(), (Block)entry.getKey());
      }

   }

   protected abstract void registerStatesAndModels();

   public VariantBlockStateBuilder getVariantBuilder(Block b) {
      if (this.registeredBlocks.containsKey(b)) {
         IGeneratedBlockstate old = (IGeneratedBlockstate)this.registeredBlocks.get(b);
         Preconditions.checkState(old instanceof VariantBlockStateBuilder);
         return (VariantBlockStateBuilder)old;
      } else {
         VariantBlockStateBuilder ret = new VariantBlockStateBuilder(b);
         this.registeredBlocks.put(b, ret);
         return ret;
      }
   }

   public MultiPartBlockStateBuilder getMultipartBuilder(Block b) {
      if (this.registeredBlocks.containsKey(b)) {
         IGeneratedBlockstate old = (IGeneratedBlockstate)this.registeredBlocks.get(b);
         Preconditions.checkState(old instanceof MultiPartBlockStateBuilder);
         return (MultiPartBlockStateBuilder)old;
      } else {
         MultiPartBlockStateBuilder ret = new MultiPartBlockStateBuilder(b);
         this.registeredBlocks.put(b, ret);
         return ret;
      }
   }

   public BlockModelProvider models() {
      return this.blockModels;
   }

   public ResourceLocation modLoc(String name) {
      return new ResourceLocation(this.modid, name);
   }

   public ResourceLocation mcLoc(String name) {
      return new ResourceLocation(name);
   }

   private String name(Block block) {
      return block.getRegistryName().getPath();
   }

   public ResourceLocation blockTexture(Block block) {
      ResourceLocation name = block.getRegistryName();
      return new ResourceLocation(name.getNamespace(), "block/" + name.getPath());
   }

   private ResourceLocation extend(ResourceLocation rl, String suffix) {
      return new ResourceLocation(rl.getNamespace(), rl.getPath() + suffix);
   }

   public ModelFile cubeAll(Block block) {
      return this.models().cubeAll(this.name(block), this.blockTexture(block));
   }

   public void simpleBlock(Block block) {
      this.simpleBlock(block, this.cubeAll(block));
   }

   public void simpleBlock(Block block, Function<ModelFile, ConfiguredModel[]> expander) {
      this.simpleBlock(block, (ConfiguredModel[])expander.apply(this.cubeAll(block)));
   }

   public void simpleBlock(Block block, ModelFile model) {
      this.simpleBlock(block, new ConfiguredModel(model));
   }

   public void simpleBlock(Block block, ConfiguredModel... models) {
      this.getVariantBuilder(block).partialState().setModels(models);
   }

   public void axisBlock(RotatedPillarBlock block) {
      this.axisBlock(block, this.blockTexture(block));
   }

   public void logBlock(LogBlock block) {
      this.axisBlock(block, this.blockTexture(block), this.extend(this.blockTexture(block), "_top"));
   }

   public void axisBlock(RotatedPillarBlock block, ResourceLocation baseName) {
      this.axisBlock(block, this.extend(baseName, "_side"), this.extend(baseName, "_end"));
   }

   public void axisBlock(RotatedPillarBlock block, ResourceLocation side, ResourceLocation end) {
      this.axisBlock(block, (ModelFile)this.models().cubeColumn(this.name(block), side, end));
   }

   public void axisBlock(RotatedPillarBlock block, ModelFile model) {
      ((VariantBlockStateBuilder)((VariantBlockStateBuilder)this.getVariantBuilder(block).partialState().with(RotatedPillarBlock.AXIS, Direction.Axis.Y).modelForState().modelFile(model).addModel()).partialState().with(RotatedPillarBlock.AXIS, Direction.Axis.Z).modelForState().modelFile(model).rotationX(90).addModel()).partialState().with(RotatedPillarBlock.AXIS, Direction.Axis.X).modelForState().modelFile(model).rotationX(90).rotationY(90).addModel();
   }

   public void horizontalBlock(Block block, ResourceLocation side, ResourceLocation front, ResourceLocation top) {
      this.horizontalBlock(block, (ModelFile)this.models().orientable(this.name(block), side, front, top));
   }

   public void horizontalBlock(Block block, ModelFile model) {
      this.horizontalBlock(block, (ModelFile)model, 180);
   }

   public void horizontalBlock(Block block, ModelFile model, int angleOffset) {
      this.horizontalBlock(block, ($) -> {
         return model;
      }, angleOffset);
   }

   public void horizontalBlock(Block block, Function<BlockState, ModelFile> modelFunc) {
      this.horizontalBlock(block, (Function)modelFunc, 180);
   }

   public void horizontalBlock(Block block, Function<BlockState, ModelFile> modelFunc, int angleOffset) {
      this.getVariantBuilder(block).forAllStates((state) -> {
         return ConfiguredModel.builder().modelFile((ModelFile)modelFunc.apply(state)).rotationY(((int)((Direction)state.get(BlockStateProperties.HORIZONTAL_FACING)).getHorizontalAngle() + angleOffset) % 360).build();
      });
   }

   public void horizontalFaceBlock(Block block, ModelFile model) {
      this.horizontalFaceBlock(block, (ModelFile)model, 180);
   }

   public void horizontalFaceBlock(Block block, ModelFile model, int angleOffset) {
      this.horizontalFaceBlock(block, ($) -> {
         return model;
      }, angleOffset);
   }

   public void horizontalFaceBlock(Block block, Function<BlockState, ModelFile> modelFunc) {
      this.horizontalBlock(block, (Function)modelFunc, 180);
   }

   public void horizontalFaceBlock(Block block, Function<BlockState, ModelFile> modelFunc, int angleOffset) {
      this.getVariantBuilder(block).forAllStates((state) -> {
         return ConfiguredModel.builder().modelFile((ModelFile)modelFunc.apply(state)).rotationX(((AttachFace)state.get(BlockStateProperties.FACE)).ordinal() * 90).rotationY(((int)((Direction)state.get(BlockStateProperties.HORIZONTAL_FACING)).getHorizontalAngle() + angleOffset + (state.get(BlockStateProperties.FACE) == AttachFace.CEILING ? 180 : 0)) % 360).build();
      });
   }

   public void directionalBlock(Block block, ModelFile model) {
      this.directionalBlock(block, (ModelFile)model, 180);
   }

   public void directionalBlock(Block block, ModelFile model, int angleOffset) {
      this.directionalBlock(block, ($) -> {
         return model;
      }, angleOffset);
   }

   public void directionalBlock(Block block, Function<BlockState, ModelFile> modelFunc) {
      this.directionalBlock(block, (Function)modelFunc, 180);
   }

   public void directionalBlock(Block block, Function<BlockState, ModelFile> modelFunc, int angleOffset) {
      this.getVariantBuilder(block).forAllStates((state) -> {
         Direction dir = (Direction)state.get(BlockStateProperties.FACING);
         return ConfiguredModel.builder().modelFile((ModelFile)modelFunc.apply(state)).rotationX(dir == Direction.DOWN ? 180 : (dir.getAxis().isHorizontal() ? 90 : 0)).rotationY(dir.getAxis().isVertical() ? 0 : ((int)dir.getHorizontalAngle() + angleOffset) % 360).build();
      });
   }

   public void stairsBlock(StairsBlock block, ResourceLocation texture) {
      this.stairsBlock(block, texture, texture, texture);
   }

   public void stairsBlock(StairsBlock block, String name, ResourceLocation texture) {
      this.stairsBlock(block, name, texture, texture, texture);
   }

   public void stairsBlock(StairsBlock block, ResourceLocation side, ResourceLocation bottom, ResourceLocation top) {
      this.stairsBlockInternal(block, block.getRegistryName().toString(), side, bottom, top);
   }

   public void stairsBlock(StairsBlock block, String name, ResourceLocation side, ResourceLocation bottom, ResourceLocation top) {
      this.stairsBlockInternal(block, name + "_stairs", side, bottom, top);
   }

   private void stairsBlockInternal(StairsBlock block, String baseName, ResourceLocation side, ResourceLocation bottom, ResourceLocation top) {
      ModelFile stairs = this.models().stairs(baseName, side, bottom, top);
      ModelFile stairsInner = this.models().stairsInner(baseName + "_inner", side, bottom, top);
      ModelFile stairsOuter = this.models().stairsOuter(baseName + "_outer", side, bottom, top);
      this.stairsBlock(block, (ModelFile)stairs, (ModelFile)stairsInner, (ModelFile)stairsOuter);
   }

   public void stairsBlock(StairsBlock block, ModelFile stairs, ModelFile stairsInner, ModelFile stairsOuter) {
      this.getVariantBuilder(block).forAllStatesExcept((state) -> {
         Direction facing = (Direction)state.get(StairsBlock.FACING);
         Half half = (Half)state.get(StairsBlock.HALF);
         StairsShape shape = (StairsShape)state.get(StairsBlock.SHAPE);
         int yRot = (int)facing.rotateY().getHorizontalAngle();
         if (shape == StairsShape.INNER_LEFT || shape == StairsShape.OUTER_LEFT) {
            yRot += 270;
         }

         if (shape != StairsShape.STRAIGHT && half == Half.TOP) {
            yRot += 90;
         }

         yRot %= 360;
         boolean uvlock = yRot != 0 || half == Half.TOP;
         return ConfiguredModel.builder().modelFile(shape == StairsShape.STRAIGHT ? stairs : (shape != StairsShape.INNER_LEFT && shape != StairsShape.INNER_RIGHT ? stairsOuter : stairsInner)).rotationX(half == Half.BOTTOM ? 0 : 180).rotationY(yRot).uvLock(uvlock).build();
      }, StairsBlock.WATERLOGGED);
   }

   public void slabBlock(SlabBlock block, ResourceLocation doubleslab, ResourceLocation texture) {
      this.slabBlock(block, doubleslab, texture, texture, texture);
   }

   public void slabBlock(SlabBlock block, ResourceLocation doubleslab, ResourceLocation side, ResourceLocation bottom, ResourceLocation top) {
      this.slabBlock(block, this.models().slab(this.name(block), side, bottom, top), this.models().slabTop(this.name(block) + "_top", side, bottom, top), this.models().getExistingFile(doubleslab));
   }

   public void slabBlock(SlabBlock block, ModelFile bottom, ModelFile top, ModelFile doubleslab) {
      this.getVariantBuilder(block).partialState().with(SlabBlock.TYPE, SlabType.BOTTOM).addModels(new ConfiguredModel(bottom)).partialState().with(SlabBlock.TYPE, SlabType.TOP).addModels(new ConfiguredModel(top)).partialState().with(SlabBlock.TYPE, SlabType.DOUBLE).addModels(new ConfiguredModel(doubleslab));
   }

   public void fourWayBlock(FourWayBlock block, ModelFile post, ModelFile side) {
      MultiPartBlockStateBuilder builder = ((MultiPartBlockStateBuilder.PartBuilder)this.getMultipartBuilder(block).part().modelFile(post).addModel()).end();
      this.fourWayMultipart(builder, side);
   }

   public void fourWayMultipart(MultiPartBlockStateBuilder builder, ModelFile side) {
      SixWayBlock.FACING_TO_PROPERTY_MAP.entrySet().forEach((e) -> {
         Direction dir = (Direction)e.getKey();
         if (dir.getAxis().isHorizontal()) {
            ((MultiPartBlockStateBuilder.PartBuilder)builder.part().modelFile(side).rotationY(((int)dir.getHorizontalAngle() + 180) % 360).uvLock(true).addModel()).condition((IProperty)e.getValue(), true);
         }

      });
   }

   public void fenceBlock(FenceBlock block, ResourceLocation texture) {
      String baseName = block.getRegistryName().toString();
      this.fourWayBlock(block, this.models().fencePost(baseName + "_post", texture), this.models().fenceSide(baseName + "_side", texture));
   }

   public void fenceBlock(FenceBlock block, String name, ResourceLocation texture) {
      this.fourWayBlock(block, this.models().fencePost(name + "_fence_post", texture), this.models().fenceSide(name + "_fence_side", texture));
   }

   public void fenceGateBlock(FenceGateBlock block, ResourceLocation texture) {
      this.fenceGateBlockInternal(block, block.getRegistryName().toString(), texture);
   }

   public void fenceGateBlock(FenceGateBlock block, String name, ResourceLocation texture) {
      this.fenceGateBlockInternal(block, name + "_fence_gate", texture);
   }

   private void fenceGateBlockInternal(FenceGateBlock block, String baseName, ResourceLocation texture) {
      ModelFile gate = this.models().fenceGate(baseName, texture);
      ModelFile gateOpen = this.models().fenceGateOpen(baseName + "_open", texture);
      ModelFile gateWall = this.models().fenceGateWall(baseName + "_wall", texture);
      ModelFile gateWallOpen = this.models().fenceGateWallOpen(baseName + "_wall_open", texture);
      this.fenceGateBlock(block, gate, gateOpen, gateWall, gateWallOpen);
   }

   public void fenceGateBlock(FenceGateBlock block, ModelFile gate, ModelFile gateOpen, ModelFile gateWall, ModelFile gateWallOpen) {
      this.getVariantBuilder(block).forAllStatesExcept((state) -> {
         ModelFile model = gate;
         if ((Boolean)state.get(FenceGateBlock.IN_WALL)) {
            model = gateWall;
         }

         if ((Boolean)state.get(FenceGateBlock.OPEN)) {
            model = model == gateWall ? gateWallOpen : gateOpen;
         }

         return ConfiguredModel.builder().modelFile(model).rotationY((int)((Direction)state.get(FenceGateBlock.HORIZONTAL_FACING)).getHorizontalAngle()).uvLock(true).build();
      }, FenceGateBlock.POWERED);
   }

   public void wallBlock(WallBlock block, ResourceLocation texture) {
      this.wallBlockInternal(block, block.getRegistryName().toString(), texture);
   }

   public void wallBlock(WallBlock block, String name, ResourceLocation texture) {
      this.wallBlockInternal(block, name + "_wall", texture);
   }

   private void wallBlockInternal(WallBlock block, String baseName, ResourceLocation texture) {
      this.wallBlock(block, (ModelFile)this.models().wallPost(baseName + "_post", texture), (ModelFile)this.models().wallSide(baseName + "_side", texture));
   }

   public void wallBlock(WallBlock block, ModelFile post, ModelFile side) {
      MultiPartBlockStateBuilder builder = ((MultiPartBlockStateBuilder.PartBuilder)this.getMultipartBuilder(block).part().modelFile(post).addModel()).condition(WallBlock.UP, true).end();
      this.fourWayMultipart(builder, side);
   }

   public void paneBlock(PaneBlock block, ResourceLocation pane, ResourceLocation edge) {
      this.paneBlockInternal(block, block.getRegistryName().toString(), pane, edge);
   }

   public void paneBlock(PaneBlock block, String name, ResourceLocation pane, ResourceLocation edge) {
      this.paneBlockInternal(block, name + "_pane", pane, edge);
   }

   private void paneBlockInternal(PaneBlock block, String baseName, ResourceLocation pane, ResourceLocation edge) {
      ModelFile post = this.models().panePost(baseName + "_post", pane, edge);
      ModelFile side = this.models().paneSide(baseName + "_side", pane, edge);
      ModelFile sideAlt = this.models().paneSideAlt(baseName + "_side_alt", pane, edge);
      ModelFile noSide = this.models().paneNoSide(baseName + "_noside", pane);
      ModelFile noSideAlt = this.models().paneNoSideAlt(baseName + "_noside_alt", pane);
      this.paneBlock(block, post, side, sideAlt, noSide, noSideAlt);
   }

   public void paneBlock(PaneBlock block, ModelFile post, ModelFile side, ModelFile sideAlt, ModelFile noSide, ModelFile noSideAlt) {
      MultiPartBlockStateBuilder builder = ((MultiPartBlockStateBuilder.PartBuilder)this.getMultipartBuilder(block).part().modelFile(post).addModel()).end();
      SixWayBlock.FACING_TO_PROPERTY_MAP.entrySet().forEach((e) -> {
         Direction dir = (Direction)e.getKey();
         if (dir.getAxis().isHorizontal()) {
            boolean alt = dir == Direction.SOUTH;
            ((MultiPartBlockStateBuilder.PartBuilder)((MultiPartBlockStateBuilder.PartBuilder)builder.part().modelFile(!alt && dir != Direction.WEST ? side : sideAlt).rotationY(dir.getAxis() == Direction.Axis.X ? 90 : 0).addModel()).condition((IProperty)e.getValue(), true).end().part().modelFile(!alt && dir != Direction.EAST ? noSide : noSideAlt).rotationY(dir == Direction.WEST ? 270 : (dir == Direction.SOUTH ? 90 : 0)).addModel()).condition((IProperty)e.getValue(), false);
         }

      });
   }

   public void doorBlock(DoorBlock block, ResourceLocation bottom, ResourceLocation top) {
      this.doorBlockInternal(block, block.getRegistryName().toString(), bottom, top);
   }

   public void doorBlock(DoorBlock block, String name, ResourceLocation bottom, ResourceLocation top) {
      this.doorBlockInternal(block, name + "_door", bottom, top);
   }

   private void doorBlockInternal(DoorBlock block, String baseName, ResourceLocation bottom, ResourceLocation top) {
      ModelFile bottomLeft = this.models().doorBottomLeft(baseName + "_bottom", bottom, top);
      ModelFile bottomRight = this.models().doorBottomRight(baseName + "_bottom_hinge", bottom, top);
      ModelFile topLeft = this.models().doorTopLeft(baseName + "_top", bottom, top);
      ModelFile topRight = this.models().doorTopRight(baseName + "_top_hinge", bottom, top);
      this.doorBlock(block, bottomLeft, bottomRight, topLeft, topRight);
   }

   public void doorBlock(DoorBlock block, ModelFile bottomLeft, ModelFile bottomRight, ModelFile topLeft, ModelFile topRight) {
      this.getVariantBuilder(block).forAllStatesExcept((state) -> {
         int yRot = (int)((Direction)state.get(DoorBlock.FACING)).getHorizontalAngle() + 90;
         boolean rh = state.get(DoorBlock.HINGE) == DoorHingeSide.RIGHT;
         boolean open = (Boolean)state.get(DoorBlock.OPEN);
         boolean right = rh ^ open;
         if (open) {
            yRot += 90;
         }

         if (rh && open) {
            yRot += 180;
         }

         yRot %= 360;
         return ConfiguredModel.builder().modelFile(state.get(DoorBlock.HALF) == DoubleBlockHalf.LOWER ? (right ? bottomRight : bottomLeft) : (right ? topRight : topLeft)).rotationY(yRot).build();
      }, DoorBlock.POWERED);
   }

   public void trapdoorBlock(TrapDoorBlock block, ResourceLocation texture, boolean orientable) {
      this.trapdoorBlockInternal(block, block.getRegistryName().toString(), texture, orientable);
   }

   public void trapdoorBlock(TrapDoorBlock block, String name, ResourceLocation texture, boolean orientable) {
      this.trapdoorBlockInternal(block, name + "_trapdoor", texture, orientable);
   }

   private void trapdoorBlockInternal(TrapDoorBlock block, String baseName, ResourceLocation texture, boolean orientable) {
      ModelFile bottom = orientable ? this.models().trapdoorOrientableBottom(baseName + "_bottom", texture) : this.models().trapdoorBottom(baseName + "_bottom", texture);
      ModelFile top = orientable ? this.models().trapdoorOrientableTop(baseName + "_top", texture) : this.models().trapdoorTop(baseName + "_top", texture);
      ModelFile open = orientable ? this.models().trapdoorOrientableOpen(baseName + "_open", texture) : this.models().trapdoorOpen(baseName + "_open", texture);
      this.trapdoorBlock(block, bottom, top, open, orientable);
   }

   public void trapdoorBlock(TrapDoorBlock block, ModelFile bottom, ModelFile top, ModelFile open, boolean orientable) {
      this.getVariantBuilder(block).forAllStatesExcept((state) -> {
         int xRot = 0;
         int yRot = (int)((Direction)state.get(TrapDoorBlock.HORIZONTAL_FACING)).getHorizontalAngle() + 180;
         boolean isOpen = (Boolean)state.get(TrapDoorBlock.OPEN);
         if (orientable && isOpen && state.get(TrapDoorBlock.HALF) == Half.TOP) {
            xRot += 180;
            yRot += 180;
         }

         if (!orientable && !isOpen) {
            yRot = 0;
         }

         yRot %= 360;
         return ConfiguredModel.builder().modelFile(isOpen ? open : (state.get(TrapDoorBlock.HALF) == Half.TOP ? top : bottom)).rotationX(xRot).rotationY(yRot).build();
      }, TrapDoorBlock.POWERED, TrapDoorBlock.WATERLOGGED);
   }

   private void saveBlockState(DirectoryCache cache, JsonObject stateJson, Block owner) {
      ResourceLocation blockName = (ResourceLocation)Preconditions.checkNotNull(owner.getRegistryName());
      Path mainOutput = this.generator.getOutputFolder();
      String pathSuffix = "assets/" + blockName.getNamespace() + "/blockstates/" + blockName.getPath() + ".json";
      Path outputPath = mainOutput.resolve(pathSuffix);

      try {
         IDataProvider.save(GSON, cache, stateJson, outputPath);
      } catch (IOException var9) {
         LOGGER.error("Couldn't save blockstate to {}", outputPath, var9);
      }

   }

   @Nonnull
   public String getName() {
      return "Block States";
   }

   public static class ConfiguredModelList {
      private final List<ConfiguredModel> models;

      private ConfiguredModelList(List<ConfiguredModel> models) {
         Preconditions.checkArgument(!models.isEmpty());
         this.models = models;
      }

      public ConfiguredModelList(ConfiguredModel model) {
         this((List)ImmutableList.of(model));
      }

      public ConfiguredModelList(ConfiguredModel... models) {
         this(Arrays.asList(models));
      }

      public JsonElement toJSON() {
         if (this.models.size() == 1) {
            return ((ConfiguredModel)this.models.get(0)).toJSON(false);
         } else {
            JsonArray ret = new JsonArray();
            Iterator var2 = this.models.iterator();

            while(var2.hasNext()) {
               ConfiguredModel m = (ConfiguredModel)var2.next();
               ret.add(m.toJSON(true));
            }

            return ret;
         }
      }

      public BlockStateProvider.ConfiguredModelList append(ConfiguredModel... models) {
         return new BlockStateProvider.ConfiguredModelList(ImmutableList.builder().addAll(this.models).add(models).build());
      }
   }
}
