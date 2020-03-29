package net.minecraft.test;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.BlockStateInput;
import net.minecraft.data.NBTToSNBTConverter;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.network.DebugPacketSender;
import net.minecraft.tileentity.StructureBlockTileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.server.ServerWorld;
import org.apache.commons.io.IOUtils;

public class TestCommand {
   public static void func_229613_a_(CommandDispatcher<CommandSource> p_229613_0_) {
      p_229613_0_.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("test").then(Commands.literal("runthis").executes((p_229647_0_) -> {
         return func_229615_a_((CommandSource)p_229647_0_.getSource());
      }))).then(Commands.literal("runthese").executes((p_229646_0_) -> {
         return func_229629_b_((CommandSource)p_229646_0_.getSource());
      }))).then(Commands.literal("run").then(Commands.argument("testName", TestArgArgument.func_229665_a_()).executes((p_229645_0_) -> {
         return func_229620_a_((CommandSource)p_229645_0_.getSource(), TestArgArgument.func_229666_a_(p_229645_0_, "testName"));
      })))).then(((LiteralArgumentBuilder)Commands.literal("runall").executes((p_229644_0_) -> {
         return func_229633_c_((CommandSource)p_229644_0_.getSource());
      })).then(Commands.argument("testClassName", TestTypeArgument.func_229611_a_()).executes((p_229643_0_) -> {
         return func_229630_b_((CommandSource)p_229643_0_.getSource(), TestTypeArgument.func_229612_a_(p_229643_0_, "testClassName"));
      })))).then(Commands.literal("export").then(Commands.argument("testName", StringArgumentType.word()).executes((p_229642_0_) -> {
         return func_229636_d_((CommandSource)p_229642_0_.getSource(), StringArgumentType.getString(p_229642_0_, "testName"));
      })))).then(Commands.literal("import").then(Commands.argument("testName", StringArgumentType.word()).executes((p_229641_0_) -> {
         return func_229638_e_((CommandSource)p_229641_0_.getSource(), StringArgumentType.getString(p_229641_0_, "testName"));
      })))).then(((LiteralArgumentBuilder)Commands.literal("pos").executes((p_229640_0_) -> {
         return func_229617_a_((CommandSource)p_229640_0_.getSource(), "pos");
      })).then(Commands.argument("var", StringArgumentType.word()).executes((p_229639_0_) -> {
         return func_229617_a_((CommandSource)p_229639_0_.getSource(), StringArgumentType.getString(p_229639_0_, "var"));
      })))).then(Commands.literal("create").then(((RequiredArgumentBuilder)Commands.argument("testName", StringArgumentType.word()).executes((p_229637_0_) -> {
         return func_229618_a_((CommandSource)p_229637_0_.getSource(), StringArgumentType.getString(p_229637_0_, "testName"), 5, 5, 5);
      })).then(((RequiredArgumentBuilder)Commands.argument("width", IntegerArgumentType.integer()).executes((p_229635_0_) -> {
         return func_229618_a_((CommandSource)p_229635_0_.getSource(), StringArgumentType.getString(p_229635_0_, "testName"), IntegerArgumentType.getInteger(p_229635_0_, "width"), IntegerArgumentType.getInteger(p_229635_0_, "width"), IntegerArgumentType.getInteger(p_229635_0_, "width"));
      })).then(Commands.argument("height", IntegerArgumentType.integer()).then(Commands.argument("depth", IntegerArgumentType.integer()).executes((p_229632_0_) -> {
         return func_229618_a_((CommandSource)p_229632_0_.getSource(), StringArgumentType.getString(p_229632_0_, "testName"), IntegerArgumentType.getInteger(p_229632_0_, "width"), IntegerArgumentType.getInteger(p_229632_0_, "height"), IntegerArgumentType.getInteger(p_229632_0_, "depth"));
      }))))))).then(((LiteralArgumentBuilder)Commands.literal("clearall").executes((p_229628_0_) -> {
         return func_229616_a_((CommandSource)p_229628_0_.getSource(), 200);
      })).then(Commands.argument("radius", IntegerArgumentType.integer()).executes((p_229614_0_) -> {
         return func_229616_a_((CommandSource)p_229614_0_.getSource(), IntegerArgumentType.getInteger(p_229614_0_, "radius"));
      }))));
   }

   private static int func_229618_a_(CommandSource p_229618_0_, String p_229618_1_, int p_229618_2_, int p_229618_3_, int p_229618_4_) {
      if (p_229618_2_ <= 32 && p_229618_3_ <= 32 && p_229618_4_ <= 32) {
         ServerWorld lvt_5_1_ = p_229618_0_.getWorld();
         BlockPos lvt_6_1_ = new BlockPos(p_229618_0_.getPos());
         BlockPos lvt_7_1_ = new BlockPos(lvt_6_1_.getX(), p_229618_0_.getWorld().getHeight(Heightmap.Type.WORLD_SURFACE, lvt_6_1_).getY(), lvt_6_1_.getZ() + 3);
         StructureHelper.func_229603_a_(p_229618_1_.toLowerCase(), lvt_7_1_, new BlockPos(p_229618_2_, p_229618_3_, p_229618_4_), 2, lvt_5_1_);

         for(int lvt_8_1_ = 0; lvt_8_1_ < p_229618_2_; ++lvt_8_1_) {
            for(int lvt_9_1_ = 0; lvt_9_1_ < p_229618_4_; ++lvt_9_1_) {
               BlockPos lvt_10_1_ = new BlockPos(lvt_7_1_.getX() + lvt_8_1_, lvt_7_1_.getY() + 1, lvt_7_1_.getZ() + lvt_9_1_);
               Block lvt_11_1_ = Blocks.POLISHED_ANDESITE;
               BlockStateInput lvt_12_1_ = new BlockStateInput(lvt_11_1_.getDefaultState(), Collections.EMPTY_SET, (CompoundNBT)null);
               lvt_12_1_.place(lvt_5_1_, lvt_10_1_, 2);
            }
         }

         StructureHelper.func_229600_a_(lvt_7_1_.add(1, 0, -1), lvt_5_1_);
         return 0;
      } else {
         throw new IllegalArgumentException("The structure must be less than 32 blocks big in each axis");
      }
   }

   private static int func_229617_a_(CommandSource p_229617_0_, String p_229617_1_) throws CommandSyntaxException {
      BlockRayTraceResult lvt_2_1_ = (BlockRayTraceResult)p_229617_0_.asPlayer().func_213324_a(10.0D, 1.0F, false);
      BlockPos lvt_3_1_ = lvt_2_1_.getPos();
      ServerWorld lvt_4_1_ = p_229617_0_.getWorld();
      Optional<BlockPos> lvt_5_1_ = StructureHelper.func_229596_a_(lvt_3_1_, 15, lvt_4_1_);
      if (!lvt_5_1_.isPresent()) {
         lvt_5_1_ = StructureHelper.func_229596_a_(lvt_3_1_, 200, lvt_4_1_);
      }

      if (!lvt_5_1_.isPresent()) {
         p_229617_0_.sendErrorMessage(new StringTextComponent("Can't find a structure block that contains the targeted pos " + lvt_3_1_));
         return 0;
      } else {
         StructureBlockTileEntity lvt_6_1_ = (StructureBlockTileEntity)lvt_4_1_.getTileEntity((BlockPos)lvt_5_1_.get());
         BlockPos lvt_7_1_ = lvt_3_1_.subtract((Vec3i)lvt_5_1_.get());
         String lvt_8_1_ = lvt_7_1_.getX() + ", " + lvt_7_1_.getY() + ", " + lvt_7_1_.getZ();
         String lvt_9_1_ = lvt_6_1_.func_227014_f_();
         ITextComponent lvt_10_1_ = (new StringTextComponent(lvt_8_1_)).setStyle((new Style()).setBold(true).setColor(TextFormatting.GREEN).setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new StringTextComponent("Click to copy to clipboard"))).setClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, "final BlockPos " + p_229617_1_ + " = new BlockPos(" + lvt_8_1_ + ");")));
         p_229617_0_.sendFeedback((new StringTextComponent("Position relative to " + lvt_9_1_ + ": ")).appendSibling(lvt_10_1_), false);
         DebugPacketSender.func_229752_a_(lvt_4_1_, new BlockPos(lvt_3_1_), lvt_8_1_, -2147418368, 10000);
         return 1;
      }
   }

   private static int func_229615_a_(CommandSource p_229615_0_) {
      BlockPos lvt_1_1_ = new BlockPos(p_229615_0_.getPos());
      ServerWorld lvt_2_1_ = p_229615_0_.getWorld();
      BlockPos lvt_3_1_ = StructureHelper.func_229607_b_(lvt_1_1_, 15, lvt_2_1_);
      if (lvt_3_1_ == null) {
         func_229624_a_(lvt_2_1_, "Couldn't find any structure block within 15 radius", TextFormatting.RED);
         return 0;
      } else {
         TestUtils.func_229552_a_(lvt_2_1_);
         func_229623_a_(lvt_2_1_, lvt_3_1_, (TestResultList)null);
         return 1;
      }
   }

   private static int func_229629_b_(CommandSource p_229629_0_) {
      BlockPos lvt_1_1_ = new BlockPos(p_229629_0_.getPos());
      ServerWorld lvt_2_1_ = p_229629_0_.getWorld();
      Collection<BlockPos> lvt_3_1_ = StructureHelper.func_229609_c_(lvt_1_1_, 200, lvt_2_1_);
      if (lvt_3_1_.isEmpty()) {
         func_229624_a_(lvt_2_1_, "Couldn't find any structure blocks within 200 block radius", TextFormatting.RED);
         return 1;
      } else {
         TestUtils.func_229552_a_(lvt_2_1_);
         func_229634_c_(p_229629_0_, "Running " + lvt_3_1_.size() + " tests...");
         TestResultList lvt_4_1_ = new TestResultList();
         lvt_3_1_.forEach((p_229626_2_) -> {
            func_229623_a_(lvt_2_1_, p_229626_2_, lvt_4_1_);
         });
         return 1;
      }
   }

   private static void func_229623_a_(ServerWorld p_229623_0_, BlockPos p_229623_1_, @Nullable TestResultList p_229623_2_) {
      StructureBlockTileEntity lvt_3_1_ = (StructureBlockTileEntity)p_229623_0_.getTileEntity(p_229623_1_);
      String lvt_4_1_ = lvt_3_1_.func_227014_f_();
      TestFunctionInfo lvt_5_1_ = TestRegistry.func_229538_e_(lvt_4_1_);
      TestTracker lvt_6_1_ = new TestTracker(lvt_5_1_, p_229623_1_, p_229623_0_);
      if (p_229623_2_ != null) {
         p_229623_2_.func_229579_a_(lvt_6_1_);
         lvt_6_1_.func_229504_a_(new TestCommand.Callback(p_229623_0_, p_229623_2_));
      }

      func_229622_a_(lvt_5_1_, p_229623_0_);
      TestUtils.func_229542_a_(lvt_6_1_, TestCollection.field_229570_a_);
   }

   private static void func_229631_b_(ServerWorld p_229631_0_, TestResultList p_229631_1_) {
      if (p_229631_1_.func_229588_i_()) {
         func_229624_a_(p_229631_0_, "GameTest done! " + p_229631_1_.func_229587_h_() + " tests were run", TextFormatting.WHITE);
         if (p_229631_1_.func_229585_d_()) {
            func_229624_a_(p_229631_0_, "" + p_229631_1_.func_229578_a_() + " required tests failed :(", TextFormatting.RED);
         } else {
            func_229624_a_(p_229631_0_, "All required tests passed :)", TextFormatting.GREEN);
         }

         if (p_229631_1_.func_229586_e_()) {
            func_229624_a_(p_229631_0_, "" + p_229631_1_.func_229583_b_() + " optional tests failed", TextFormatting.GRAY);
         }
      }

   }

   private static int func_229616_a_(CommandSource p_229616_0_, int p_229616_1_) {
      ServerWorld lvt_2_1_ = p_229616_0_.getWorld();
      TestUtils.func_229552_a_(lvt_2_1_);
      BlockPos lvt_3_1_ = new BlockPos(p_229616_0_.getPos().x, (double)p_229616_0_.getWorld().getHeight(Heightmap.Type.WORLD_SURFACE, new BlockPos(p_229616_0_.getPos())).getY(), p_229616_0_.getPos().z);
      TestUtils.func_229555_a_(lvt_2_1_, lvt_3_1_, TestCollection.field_229570_a_, MathHelper.clamp(p_229616_1_, 0, 1024));
      return 1;
   }

   private static int func_229620_a_(CommandSource p_229620_0_, TestFunctionInfo p_229620_1_) {
      ServerWorld lvt_2_1_ = p_229620_0_.getWorld();
      BlockPos lvt_3_1_ = new BlockPos(p_229620_0_.getPos());
      BlockPos lvt_4_1_ = new BlockPos(lvt_3_1_.getX(), p_229620_0_.getWorld().getHeight(Heightmap.Type.WORLD_SURFACE, lvt_3_1_).getY(), lvt_3_1_.getZ() + 3);
      TestUtils.func_229552_a_(lvt_2_1_);
      func_229622_a_(p_229620_1_, lvt_2_1_);
      TestTracker lvt_5_1_ = new TestTracker(p_229620_1_, lvt_4_1_, lvt_2_1_);
      TestUtils.func_229542_a_(lvt_5_1_, TestCollection.field_229570_a_);
      return 1;
   }

   private static void func_229622_a_(TestFunctionInfo p_229622_0_, ServerWorld p_229622_1_) {
      Consumer<ServerWorld> lvt_2_1_ = TestRegistry.func_229536_c_(p_229622_0_.func_229662_e_());
      if (lvt_2_1_ != null) {
         lvt_2_1_.accept(p_229622_1_);
      }

   }

   private static int func_229633_c_(CommandSource p_229633_0_) {
      TestUtils.func_229552_a_(p_229633_0_.getWorld());
      func_229619_a_(p_229633_0_, TestRegistry.func_229529_a_());
      return 1;
   }

   private static int func_229630_b_(CommandSource p_229630_0_, String p_229630_1_) {
      Collection<TestFunctionInfo> lvt_2_1_ = TestRegistry.func_229530_a_(p_229630_1_);
      TestUtils.func_229552_a_(p_229630_0_.getWorld());
      func_229619_a_(p_229630_0_, lvt_2_1_);
      return 1;
   }

   private static void func_229619_a_(CommandSource p_229619_0_, Collection<TestFunctionInfo> p_229619_1_) {
      BlockPos lvt_2_1_ = new BlockPos(p_229619_0_.getPos());
      BlockPos lvt_3_1_ = new BlockPos(lvt_2_1_.getX(), p_229619_0_.getWorld().getHeight(Heightmap.Type.WORLD_SURFACE, lvt_2_1_).getY(), lvt_2_1_.getZ() + 3);
      ServerWorld lvt_4_1_ = p_229619_0_.getWorld();
      func_229634_c_(p_229619_0_, "Running " + p_229619_1_.size() + " tests...");
      Collection<TestTracker> lvt_5_1_ = TestUtils.func_229561_b_(p_229619_1_, lvt_3_1_, lvt_4_1_, TestCollection.field_229570_a_);
      TestResultList lvt_6_1_ = new TestResultList(lvt_5_1_);
      lvt_6_1_.func_229580_a_(new TestCommand.Callback(lvt_4_1_, lvt_6_1_));
   }

   private static void func_229634_c_(CommandSource p_229634_0_, String p_229634_1_) {
      p_229634_0_.sendFeedback(new StringTextComponent(p_229634_1_), false);
   }

   private static int func_229636_d_(CommandSource p_229636_0_, String p_229636_1_) {
      Path lvt_2_1_ = Paths.get(StructureHelper.field_229590_a_);
      ResourceLocation lvt_3_1_ = new ResourceLocation("minecraft", p_229636_1_);
      Path lvt_4_1_ = p_229636_0_.getWorld().getStructureTemplateManager().resolvePathStructures(lvt_3_1_, ".nbt");
      Path lvt_5_1_ = NBTToSNBTConverter.func_229443_a_(lvt_4_1_, p_229636_1_, lvt_2_1_);
      if (lvt_5_1_ == null) {
         func_229634_c_(p_229636_0_, "Failed to export " + lvt_4_1_);
         return 1;
      } else {
         try {
            Files.createDirectories(lvt_5_1_.getParent());
         } catch (IOException var7) {
            func_229634_c_(p_229636_0_, "Could not create folder " + lvt_5_1_.getParent());
            var7.printStackTrace();
            return 1;
         }

         func_229634_c_(p_229636_0_, "Exported to " + lvt_5_1_.toAbsolutePath());
         return 0;
      }
   }

   private static int func_229638_e_(CommandSource p_229638_0_, String p_229638_1_) {
      Path lvt_2_1_ = Paths.get(StructureHelper.field_229590_a_, p_229638_1_ + ".snbt");
      ResourceLocation lvt_3_1_ = new ResourceLocation("minecraft", p_229638_1_);
      Path lvt_4_1_ = p_229638_0_.getWorld().getStructureTemplateManager().resolvePathStructures(lvt_3_1_, ".nbt");

      try {
         BufferedReader lvt_5_1_ = Files.newBufferedReader(lvt_2_1_);
         String lvt_6_1_ = IOUtils.toString(lvt_5_1_);
         Files.createDirectories(lvt_4_1_.getParent());
         OutputStream lvt_7_1_ = Files.newOutputStream(lvt_4_1_);
         CompressedStreamTools.writeCompressed(JsonToNBT.getTagFromJson(lvt_6_1_), lvt_7_1_);
         func_229634_c_(p_229638_0_, "Imported to " + lvt_4_1_.toAbsolutePath());
         return 0;
      } catch (CommandSyntaxException | IOException var8) {
         System.err.println("Failed to load structure " + p_229638_1_);
         var8.printStackTrace();
         return 1;
      }
   }

   private static void func_229624_a_(ServerWorld p_229624_0_, String p_229624_1_, TextFormatting p_229624_2_) {
      p_229624_0_.getPlayers((p_229627_0_) -> {
         return true;
      }).forEach((p_229621_2_) -> {
         p_229621_2_.sendMessage(new StringTextComponent(p_229624_2_ + p_229624_1_));
      });
   }

   static class Callback implements ITestCallback {
      private final ServerWorld field_229648_a_;
      private final TestResultList field_229649_b_;

      public Callback(ServerWorld p_i226073_1_, TestResultList p_i226073_2_) {
         this.field_229648_a_ = p_i226073_1_;
         this.field_229649_b_ = p_i226073_2_;
      }

      public void func_225644_a_(TestTracker p_225644_1_) {
      }

      public void func_225645_c_(TestTracker p_225645_1_) {
         TestCommand.func_229631_b_(this.field_229648_a_, this.field_229649_b_);
      }
   }
}
