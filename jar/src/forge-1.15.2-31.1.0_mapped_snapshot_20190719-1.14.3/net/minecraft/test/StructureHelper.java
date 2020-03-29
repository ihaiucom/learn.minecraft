package net.minecraft.test;

import com.google.common.collect.Lists;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.command.arguments.BlockStateInput;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.state.properties.StructureMode;
import net.minecraft.tileentity.CommandBlockTileEntity;
import net.minecraft.tileentity.StructureBlockTileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.gen.FlatGenerationSettings;
import net.minecraft.world.gen.GenerationSettings;
import net.minecraft.world.gen.feature.template.Template;
import net.minecraft.world.gen.feature.template.TemplateManager;
import net.minecraft.world.server.ServerWorld;
import org.apache.commons.io.IOUtils;

public class StructureHelper {
   public static String field_229590_a_ = "gameteststructures";

   public static AxisAlignedBB func_229594_a_(StructureBlockTileEntity p_229594_0_) {
      BlockPos lvt_1_1_ = p_229594_0_.getPos().add(p_229594_0_.getPosition());
      return new AxisAlignedBB(lvt_1_1_, lvt_1_1_.add(p_229594_0_.getStructureSize()));
   }

   public static void func_229600_a_(BlockPos p_229600_0_, ServerWorld p_229600_1_) {
      p_229600_1_.setBlockState(p_229600_0_, Blocks.COMMAND_BLOCK.getDefaultState());
      CommandBlockTileEntity lvt_2_1_ = (CommandBlockTileEntity)p_229600_1_.getTileEntity(p_229600_0_);
      lvt_2_1_.getCommandBlockLogic().setCommand("test runthis");
      p_229600_1_.setBlockState(p_229600_0_.add(0, 0, -1), Blocks.STONE_BUTTON.getDefaultState());
   }

   public static void func_229603_a_(String p_229603_0_, BlockPos p_229603_1_, BlockPos p_229603_2_, int p_229603_3_, ServerWorld p_229603_4_) {
      MutableBoundingBox lvt_5_1_ = func_229598_a_(p_229603_1_, p_229603_2_, p_229603_3_);
      func_229595_a_(lvt_5_1_, p_229603_1_.getY(), p_229603_4_);
      p_229603_4_.setBlockState(p_229603_1_, Blocks.STRUCTURE_BLOCK.getDefaultState());
      StructureBlockTileEntity lvt_6_1_ = (StructureBlockTileEntity)p_229603_4_.getTileEntity(p_229603_1_);
      lvt_6_1_.setIgnoresEntities(false);
      lvt_6_1_.setName(new ResourceLocation(p_229603_0_));
      lvt_6_1_.setSize(p_229603_2_);
      lvt_6_1_.setMode(StructureMode.SAVE);
      lvt_6_1_.setShowBoundingBox(true);
   }

   public static StructureBlockTileEntity func_229602_a_(String p_229602_0_, BlockPos p_229602_1_, int p_229602_2_, ServerWorld p_229602_3_, boolean p_229602_4_) {
      MutableBoundingBox lvt_5_1_ = func_229598_a_(p_229602_1_, func_229605_a_(p_229602_0_, p_229602_3_).getSize(), p_229602_2_);
      func_229608_b_(p_229602_1_, p_229602_3_);
      func_229595_a_(lvt_5_1_, p_229602_1_.getY(), p_229602_3_);
      StructureBlockTileEntity lvt_6_1_ = func_229604_a_(p_229602_0_, p_229602_1_, p_229602_3_, p_229602_4_);
      p_229602_3_.getPendingBlockTicks().getPending(lvt_5_1_, true, false);
      p_229602_3_.func_229854_a_(lvt_5_1_);
      return lvt_6_1_;
   }

   private static void func_229608_b_(BlockPos p_229608_0_, ServerWorld p_229608_1_) {
      ChunkPos lvt_2_1_ = new ChunkPos(p_229608_0_);

      for(int lvt_3_1_ = -1; lvt_3_1_ < 4; ++lvt_3_1_) {
         for(int lvt_4_1_ = -1; lvt_4_1_ < 4; ++lvt_4_1_) {
            int lvt_5_1_ = lvt_2_1_.x + lvt_3_1_;
            int lvt_6_1_ = lvt_2_1_.z + lvt_4_1_;
            p_229608_1_.forceChunk(lvt_5_1_, lvt_6_1_, true);
         }
      }

   }

   public static void func_229595_a_(MutableBoundingBox p_229595_0_, int p_229595_1_, ServerWorld p_229595_2_) {
      BlockPos.func_229383_a_(p_229595_0_).forEach((p_229592_2_) -> {
         func_229591_a_(p_229595_1_, p_229592_2_, p_229595_2_);
      });
      p_229595_2_.getPendingBlockTicks().getPending(p_229595_0_, true, false);
      p_229595_2_.func_229854_a_(p_229595_0_);
      AxisAlignedBB lvt_3_1_ = new AxisAlignedBB((double)p_229595_0_.minX, (double)p_229595_0_.minY, (double)p_229595_0_.minZ, (double)p_229595_0_.maxX, (double)p_229595_0_.maxY, (double)p_229595_0_.maxZ);
      List<Entity> lvt_4_1_ = p_229595_2_.getEntitiesWithinAABB(Entity.class, lvt_3_1_, (p_229593_0_) -> {
         return !(p_229593_0_ instanceof PlayerEntity);
      });
      lvt_4_1_.forEach(Entity::remove);
   }

   public static MutableBoundingBox func_229598_a_(BlockPos p_229598_0_, BlockPos p_229598_1_, int p_229598_2_) {
      BlockPos lvt_3_1_ = p_229598_0_.add(-p_229598_2_, -3, -p_229598_2_);
      BlockPos lvt_4_1_ = p_229598_0_.add(p_229598_1_).add(p_229598_2_ - 1, 30, p_229598_2_ - 1);
      return MutableBoundingBox.createProper(lvt_3_1_.getX(), lvt_3_1_.getY(), lvt_3_1_.getZ(), lvt_4_1_.getX(), lvt_4_1_.getY(), lvt_4_1_.getZ());
   }

   public static Optional<BlockPos> func_229596_a_(BlockPos p_229596_0_, int p_229596_1_, ServerWorld p_229596_2_) {
      return func_229609_c_(p_229596_0_, p_229596_1_, p_229596_2_).stream().filter((p_229601_2_) -> {
         return func_229599_a_(p_229601_2_, p_229596_0_, p_229596_2_);
      }).findFirst();
   }

   @Nullable
   public static BlockPos func_229607_b_(BlockPos p_229607_0_, int p_229607_1_, ServerWorld p_229607_2_) {
      Comparator<BlockPos> lvt_3_1_ = Comparator.comparingInt((p_229597_1_) -> {
         return p_229597_1_.manhattanDistance(p_229607_0_);
      });
      Collection<BlockPos> lvt_4_1_ = func_229609_c_(p_229607_0_, p_229607_1_, p_229607_2_);
      Optional<BlockPos> lvt_5_1_ = lvt_4_1_.stream().min(lvt_3_1_);
      return (BlockPos)lvt_5_1_.orElse((Object)null);
   }

   public static Collection<BlockPos> func_229609_c_(BlockPos p_229609_0_, int p_229609_1_, ServerWorld p_229609_2_) {
      Collection<BlockPos> lvt_3_1_ = Lists.newArrayList();
      AxisAlignedBB lvt_4_1_ = new AxisAlignedBB(p_229609_0_);
      lvt_4_1_ = lvt_4_1_.grow((double)p_229609_1_);

      for(int lvt_5_1_ = (int)lvt_4_1_.minX; lvt_5_1_ <= (int)lvt_4_1_.maxX; ++lvt_5_1_) {
         for(int lvt_6_1_ = (int)lvt_4_1_.minY; lvt_6_1_ <= (int)lvt_4_1_.maxY; ++lvt_6_1_) {
            for(int lvt_7_1_ = (int)lvt_4_1_.minZ; lvt_7_1_ <= (int)lvt_4_1_.maxZ; ++lvt_7_1_) {
               BlockPos lvt_8_1_ = new BlockPos(lvt_5_1_, lvt_6_1_, lvt_7_1_);
               BlockState lvt_9_1_ = p_229609_2_.getBlockState(lvt_8_1_);
               if (lvt_9_1_.getBlock() == Blocks.STRUCTURE_BLOCK) {
                  lvt_3_1_.add(lvt_8_1_);
               }
            }
         }
      }

      return lvt_3_1_;
   }

   private static Template func_229605_a_(String p_229605_0_, ServerWorld p_229605_1_) {
      TemplateManager lvt_2_1_ = p_229605_1_.getStructureTemplateManager();
      Template lvt_3_1_ = lvt_2_1_.getTemplate(new ResourceLocation(p_229605_0_));
      if (lvt_3_1_ != null) {
         return lvt_3_1_;
      } else {
         String lvt_4_1_ = p_229605_0_ + ".snbt";
         Path lvt_5_1_ = Paths.get(field_229590_a_, lvt_4_1_);
         CompoundNBT lvt_6_1_ = func_229606_a_(lvt_5_1_);
         if (lvt_6_1_ == null) {
            throw new RuntimeException("Could not find structure file " + lvt_5_1_ + ", and the structure is not available in the world structures either.");
         } else {
            return lvt_2_1_.func_227458_a_(lvt_6_1_);
         }
      }
   }

   private static StructureBlockTileEntity func_229604_a_(String p_229604_0_, BlockPos p_229604_1_, ServerWorld p_229604_2_, boolean p_229604_3_) {
      p_229604_2_.setBlockState(p_229604_1_, Blocks.STRUCTURE_BLOCK.getDefaultState());
      StructureBlockTileEntity lvt_4_1_ = (StructureBlockTileEntity)p_229604_2_.getTileEntity(p_229604_1_);
      lvt_4_1_.setMode(StructureMode.LOAD);
      lvt_4_1_.setIgnoresEntities(false);
      lvt_4_1_.setName(new ResourceLocation(p_229604_0_));
      lvt_4_1_.load(p_229604_3_);
      if (lvt_4_1_.getStructureSize() != BlockPos.ZERO) {
         return lvt_4_1_;
      } else {
         Template lvt_5_1_ = func_229605_a_(p_229604_0_, p_229604_2_);
         lvt_4_1_.func_227013_a_(p_229604_3_, lvt_5_1_);
         if (lvt_4_1_.getStructureSize() == BlockPos.ZERO) {
            throw new RuntimeException("Failed to load structure " + p_229604_0_);
         } else {
            return lvt_4_1_;
         }
      }
   }

   @Nullable
   private static CompoundNBT func_229606_a_(Path p_229606_0_) {
      try {
         BufferedReader lvt_1_1_ = Files.newBufferedReader(p_229606_0_);
         String lvt_2_1_ = IOUtils.toString(lvt_1_1_);
         return JsonToNBT.getTagFromJson(lvt_2_1_);
      } catch (IOException var3) {
         return null;
      } catch (CommandSyntaxException var4) {
         throw new RuntimeException("Error while trying to load structure " + p_229606_0_, var4);
      }
   }

   private static void func_229591_a_(int p_229591_0_, BlockPos p_229591_1_, ServerWorld p_229591_2_) {
      GenerationSettings lvt_4_1_ = p_229591_2_.getChunkProvider().getChunkGenerator().getSettings();
      BlockState lvt_3_5_;
      if (lvt_4_1_ instanceof FlatGenerationSettings) {
         BlockState[] lvt_5_1_ = ((FlatGenerationSettings)lvt_4_1_).getStates();
         if (p_229591_1_.getY() < p_229591_0_) {
            lvt_3_5_ = lvt_5_1_[p_229591_1_.getY() - 1];
         } else {
            lvt_3_5_ = Blocks.AIR.getDefaultState();
         }
      } else if (p_229591_1_.getY() == p_229591_0_ - 1) {
         lvt_3_5_ = p_229591_2_.func_226691_t_(p_229591_1_).getSurfaceBuilderConfig().getTop();
      } else if (p_229591_1_.getY() < p_229591_0_ - 1) {
         lvt_3_5_ = p_229591_2_.func_226691_t_(p_229591_1_).getSurfaceBuilderConfig().getUnder();
      } else {
         lvt_3_5_ = Blocks.AIR.getDefaultState();
      }

      BlockStateInput lvt_5_2_ = new BlockStateInput(lvt_3_5_, Collections.emptySet(), (CompoundNBT)null);
      lvt_5_2_.place(p_229591_2_, p_229591_1_, 2);
      p_229591_2_.notifyNeighbors(p_229591_1_, lvt_3_5_.getBlock());
   }

   private static boolean func_229599_a_(BlockPos p_229599_0_, BlockPos p_229599_1_, ServerWorld p_229599_2_) {
      StructureBlockTileEntity lvt_3_1_ = (StructureBlockTileEntity)p_229599_2_.getTileEntity(p_229599_0_);
      AxisAlignedBB lvt_4_1_ = func_229594_a_(lvt_3_1_);
      return lvt_4_1_.contains(new Vec3d(p_229599_1_));
   }
}
