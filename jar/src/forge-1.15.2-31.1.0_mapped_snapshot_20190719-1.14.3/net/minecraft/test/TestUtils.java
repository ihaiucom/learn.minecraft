package net.minecraft.test;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Streams;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.LecternBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.network.DebugPacketSender;
import net.minecraft.tileentity.StructureBlockTileEntity;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.server.ServerWorld;

public class TestUtils {
   public static ITestLogger field_229539_a_ = new TestLogger();

   public static void func_229542_a_(TestTracker p_229542_0_, TestCollection p_229542_1_) {
      p_229542_0_.func_229501_a_();
      p_229542_1_.func_229573_a_(p_229542_0_);
      p_229542_0_.func_229504_a_(new ITestCallback() {
         public void func_225644_a_(TestTracker p_225644_1_) {
            TestUtils.func_229559_b_(p_225644_1_, Blocks.LIGHT_GRAY_STAINED_GLASS);
         }

         public void func_225645_c_(TestTracker p_225645_1_) {
            TestUtils.func_229559_b_(p_225645_1_, p_225645_1_.func_229520_q_() ? Blocks.RED_STAINED_GLASS : Blocks.ORANGE_STAINED_GLASS);
            TestUtils.func_229560_b_(p_225645_1_, Util.func_229758_d_(p_225645_1_.func_229519_n_()));
            TestUtils.func_229563_c_(p_225645_1_);
         }
      });
      p_229542_0_.func_229502_a_(2);
   }

   public static Collection<TestTracker> func_229549_a_(Collection<TestBatch> p_229549_0_, BlockPos p_229549_1_, ServerWorld p_229549_2_, TestCollection p_229549_3_) {
      TestExecutor lvt_4_1_ = new TestExecutor(p_229549_0_, p_229549_1_, p_229549_2_, p_229549_3_);
      lvt_4_1_.func_229482_b_();
      return lvt_4_1_.func_229476_a_();
   }

   public static Collection<TestTracker> func_229561_b_(Collection<TestFunctionInfo> p_229561_0_, BlockPos p_229561_1_, ServerWorld p_229561_2_, TestCollection p_229561_3_) {
      return func_229549_a_(func_229548_a_(p_229561_0_), p_229561_1_, p_229561_2_, p_229561_3_);
   }

   public static Collection<TestBatch> func_229548_a_(Collection<TestFunctionInfo> p_229548_0_) {
      Map<String, Collection<TestFunctionInfo>> lvt_1_1_ = Maps.newHashMap();
      p_229548_0_.forEach((p_229551_1_) -> {
         String lvt_2_1_ = p_229551_1_.func_229662_e_();
         Collection<TestFunctionInfo> lvt_3_1_ = (Collection)lvt_1_1_.computeIfAbsent(lvt_2_1_, (p_229543_0_) -> {
            return Lists.newArrayList();
         });
         lvt_3_1_.add(p_229551_1_);
      });
      return (Collection)lvt_1_1_.keySet().stream().flatMap((p_229550_1_) -> {
         Collection<TestFunctionInfo> lvt_2_1_ = (Collection)lvt_1_1_.get(p_229550_1_);
         Consumer<ServerWorld> lvt_3_1_ = TestRegistry.func_229536_c_(p_229550_1_);
         AtomicInteger lvt_4_1_ = new AtomicInteger();
         return Streams.stream(Iterables.partition(lvt_2_1_, 100)).map((p_229545_4_) -> {
            return new TestBatch(p_229550_1_ + ":" + lvt_4_1_.incrementAndGet(), lvt_2_1_, lvt_3_1_);
         });
      }).collect(Collectors.toList());
   }

   private static void func_229563_c_(TestTracker p_229563_0_) {
      Throwable lvt_1_1_ = p_229563_0_.func_229519_n_();
      String lvt_2_1_ = p_229563_0_.func_229510_c_() + " failed! " + Util.func_229758_d_(lvt_1_1_);
      func_229556_a_(p_229563_0_.func_229514_g_(), TextFormatting.RED, lvt_2_1_);
      if (lvt_1_1_ instanceof TestBlockPosException) {
         TestBlockPosException lvt_3_1_ = (TestBlockPosException)lvt_1_1_;
         func_229554_a_(p_229563_0_.func_229514_g_(), lvt_3_1_.func_229459_c_(), lvt_3_1_.func_229458_a_());
      }

      field_229539_a_.func_225646_a_(p_229563_0_);
   }

   private static void func_229559_b_(TestTracker p_229559_0_, Block p_229559_1_) {
      ServerWorld lvt_2_1_ = p_229559_0_.func_229514_g_();
      BlockPos lvt_3_1_ = p_229559_0_.func_229512_d_();
      BlockPos lvt_4_1_ = lvt_3_1_.add(-1, -1, -1);
      lvt_2_1_.setBlockState(lvt_4_1_, Blocks.BEACON.getDefaultState());
      BlockPos lvt_5_1_ = lvt_4_1_.add(0, 1, 0);
      lvt_2_1_.setBlockState(lvt_5_1_, p_229559_1_.getDefaultState());

      for(int lvt_6_1_ = -1; lvt_6_1_ <= 1; ++lvt_6_1_) {
         for(int lvt_7_1_ = -1; lvt_7_1_ <= 1; ++lvt_7_1_) {
            BlockPos lvt_8_1_ = lvt_4_1_.add(lvt_6_1_, -1, lvt_7_1_);
            lvt_2_1_.setBlockState(lvt_8_1_, Blocks.IRON_BLOCK.getDefaultState());
         }
      }

   }

   private static void func_229560_b_(TestTracker p_229560_0_, String p_229560_1_) {
      ServerWorld lvt_2_1_ = p_229560_0_.func_229514_g_();
      BlockPos lvt_3_1_ = p_229560_0_.func_229512_d_();
      BlockPos lvt_4_1_ = lvt_3_1_.add(-1, 1, -1);
      lvt_2_1_.setBlockState(lvt_4_1_, Blocks.LECTERN.getDefaultState());
      BlockState lvt_5_1_ = lvt_2_1_.getBlockState(lvt_4_1_);
      ItemStack lvt_6_1_ = func_229546_a_(p_229560_0_.func_229510_c_(), p_229560_0_.func_229520_q_(), p_229560_1_);
      LecternBlock.tryPlaceBook(lvt_2_1_, lvt_4_1_, lvt_5_1_, lvt_6_1_);
   }

   private static ItemStack func_229546_a_(String p_229546_0_, boolean p_229546_1_, String p_229546_2_) {
      ItemStack lvt_3_1_ = new ItemStack(Items.WRITABLE_BOOK);
      ListNBT lvt_4_1_ = new ListNBT();
      StringBuffer lvt_5_1_ = new StringBuffer();
      Arrays.stream(p_229546_0_.split("\\.")).forEach((p_229547_1_) -> {
         lvt_5_1_.append(p_229547_1_).append('\n');
      });
      if (!p_229546_1_) {
         lvt_5_1_.append("(optional)\n");
      }

      lvt_5_1_.append("-------------------\n");
      lvt_4_1_.add(StringNBT.func_229705_a_(lvt_5_1_.toString() + p_229546_2_));
      lvt_3_1_.setTagInfo("pages", lvt_4_1_);
      return lvt_3_1_;
   }

   private static void func_229556_a_(ServerWorld p_229556_0_, TextFormatting p_229556_1_, String p_229556_2_) {
      p_229556_0_.getPlayers((p_229557_0_) -> {
         return true;
      }).forEach((p_229544_2_) -> {
         p_229544_2_.sendMessage((new StringTextComponent(p_229556_2_)).applyTextStyle(p_229556_1_));
      });
   }

   public static void func_229552_a_(ServerWorld p_229552_0_) {
      DebugPacketSender.func_229751_a_(p_229552_0_);
   }

   private static void func_229554_a_(ServerWorld p_229554_0_, BlockPos p_229554_1_, String p_229554_2_) {
      DebugPacketSender.func_229752_a_(p_229554_0_, p_229554_1_, p_229554_2_, -2130771968, Integer.MAX_VALUE);
   }

   public static void func_229555_a_(ServerWorld p_229555_0_, BlockPos p_229555_1_, TestCollection p_229555_2_, int p_229555_3_) {
      p_229555_2_.func_229572_a_();
      BlockPos lvt_4_1_ = p_229555_1_.add(-p_229555_3_, 0, -p_229555_3_);
      BlockPos lvt_5_1_ = p_229555_1_.add(p_229555_3_, 0, p_229555_3_);
      BlockPos.getAllInBox(lvt_4_1_, lvt_5_1_).filter((p_229562_1_) -> {
         return p_229555_0_.getBlockState(p_229562_1_).getBlock() == Blocks.STRUCTURE_BLOCK;
      }).forEach((p_229553_1_) -> {
         StructureBlockTileEntity lvt_2_1_ = (StructureBlockTileEntity)p_229555_0_.getTileEntity(p_229553_1_);
         BlockPos lvt_3_1_ = lvt_2_1_.getPos();
         MutableBoundingBox lvt_4_1_ = StructureHelper.func_229598_a_(lvt_3_1_, lvt_2_1_.getStructureSize(), 2);
         StructureHelper.func_229595_a_(lvt_4_1_, lvt_3_1_.getY(), p_229555_0_);
      });
   }
}
