package net.minecraft.block;

import java.util.Random;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.CommandBlockLogic;
import net.minecraft.tileentity.CommandBlockTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.StringUtils;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.GameRules;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CommandBlockBlock extends ContainerBlock {
   private static final Logger LOGGER = LogManager.getLogger();
   public static final DirectionProperty FACING;
   public static final BooleanProperty CONDITIONAL;

   public CommandBlockBlock(Block.Properties p_i48425_1_) {
      super(p_i48425_1_);
      this.setDefaultState((BlockState)((BlockState)((BlockState)this.stateContainer.getBaseState()).with(FACING, Direction.NORTH)).with(CONDITIONAL, false));
   }

   public TileEntity createNewTileEntity(IBlockReader p_196283_1_) {
      CommandBlockTileEntity lvt_2_1_ = new CommandBlockTileEntity();
      lvt_2_1_.setAuto(this == Blocks.CHAIN_COMMAND_BLOCK);
      return lvt_2_1_;
   }

   public void neighborChanged(BlockState p_220069_1_, World p_220069_2_, BlockPos p_220069_3_, Block p_220069_4_, BlockPos p_220069_5_, boolean p_220069_6_) {
      if (!p_220069_2_.isRemote) {
         TileEntity lvt_7_1_ = p_220069_2_.getTileEntity(p_220069_3_);
         if (lvt_7_1_ instanceof CommandBlockTileEntity) {
            CommandBlockTileEntity lvt_8_1_ = (CommandBlockTileEntity)lvt_7_1_;
            boolean lvt_9_1_ = p_220069_2_.isBlockPowered(p_220069_3_);
            boolean lvt_10_1_ = lvt_8_1_.isPowered();
            lvt_8_1_.setPowered(lvt_9_1_);
            if (!lvt_10_1_ && !lvt_8_1_.isAuto() && lvt_8_1_.getMode() != CommandBlockTileEntity.Mode.SEQUENCE) {
               if (lvt_9_1_) {
                  lvt_8_1_.setConditionMet();
                  p_220069_2_.getPendingBlockTicks().scheduleTick(p_220069_3_, this, this.tickRate(p_220069_2_));
               }

            }
         }
      }
   }

   public void func_225534_a_(BlockState p_225534_1_, ServerWorld p_225534_2_, BlockPos p_225534_3_, Random p_225534_4_) {
      TileEntity lvt_5_1_ = p_225534_2_.getTileEntity(p_225534_3_);
      if (lvt_5_1_ instanceof CommandBlockTileEntity) {
         CommandBlockTileEntity lvt_6_1_ = (CommandBlockTileEntity)lvt_5_1_;
         CommandBlockLogic lvt_7_1_ = lvt_6_1_.getCommandBlockLogic();
         boolean lvt_8_1_ = !StringUtils.isNullOrEmpty(lvt_7_1_.getCommand());
         CommandBlockTileEntity.Mode lvt_9_1_ = lvt_6_1_.getMode();
         boolean lvt_10_1_ = lvt_6_1_.isConditionMet();
         if (lvt_9_1_ == CommandBlockTileEntity.Mode.AUTO) {
            lvt_6_1_.setConditionMet();
            if (lvt_10_1_) {
               this.execute(p_225534_1_, p_225534_2_, p_225534_3_, lvt_7_1_, lvt_8_1_);
            } else if (lvt_6_1_.isConditional()) {
               lvt_7_1_.setSuccessCount(0);
            }

            if (lvt_6_1_.isPowered() || lvt_6_1_.isAuto()) {
               p_225534_2_.getPendingBlockTicks().scheduleTick(p_225534_3_, this, this.tickRate(p_225534_2_));
            }
         } else if (lvt_9_1_ == CommandBlockTileEntity.Mode.REDSTONE) {
            if (lvt_10_1_) {
               this.execute(p_225534_1_, p_225534_2_, p_225534_3_, lvt_7_1_, lvt_8_1_);
            } else if (lvt_6_1_.isConditional()) {
               lvt_7_1_.setSuccessCount(0);
            }
         }

         p_225534_2_.updateComparatorOutputLevel(p_225534_3_, this);
      }

   }

   private void execute(BlockState p_193387_1_, World p_193387_2_, BlockPos p_193387_3_, CommandBlockLogic p_193387_4_, boolean p_193387_5_) {
      if (p_193387_5_) {
         p_193387_4_.trigger(p_193387_2_);
      } else {
         p_193387_4_.setSuccessCount(0);
      }

      executeChain(p_193387_2_, p_193387_3_, (Direction)p_193387_1_.get(FACING));
   }

   public int tickRate(IWorldReader p_149738_1_) {
      return 1;
   }

   public ActionResultType func_225533_a_(BlockState p_225533_1_, World p_225533_2_, BlockPos p_225533_3_, PlayerEntity p_225533_4_, Hand p_225533_5_, BlockRayTraceResult p_225533_6_) {
      TileEntity lvt_7_1_ = p_225533_2_.getTileEntity(p_225533_3_);
      if (lvt_7_1_ instanceof CommandBlockTileEntity && p_225533_4_.canUseCommandBlock()) {
         p_225533_4_.openCommandBlock((CommandBlockTileEntity)lvt_7_1_);
         return ActionResultType.SUCCESS;
      } else {
         return ActionResultType.PASS;
      }
   }

   public boolean hasComparatorInputOverride(BlockState p_149740_1_) {
      return true;
   }

   public int getComparatorInputOverride(BlockState p_180641_1_, World p_180641_2_, BlockPos p_180641_3_) {
      TileEntity lvt_4_1_ = p_180641_2_.getTileEntity(p_180641_3_);
      return lvt_4_1_ instanceof CommandBlockTileEntity ? ((CommandBlockTileEntity)lvt_4_1_).getCommandBlockLogic().getSuccessCount() : 0;
   }

   public void onBlockPlacedBy(World p_180633_1_, BlockPos p_180633_2_, BlockState p_180633_3_, LivingEntity p_180633_4_, ItemStack p_180633_5_) {
      TileEntity lvt_6_1_ = p_180633_1_.getTileEntity(p_180633_2_);
      if (lvt_6_1_ instanceof CommandBlockTileEntity) {
         CommandBlockTileEntity lvt_7_1_ = (CommandBlockTileEntity)lvt_6_1_;
         CommandBlockLogic lvt_8_1_ = lvt_7_1_.getCommandBlockLogic();
         if (p_180633_5_.hasDisplayName()) {
            lvt_8_1_.setName(p_180633_5_.getDisplayName());
         }

         if (!p_180633_1_.isRemote) {
            if (p_180633_5_.getChildTag("BlockEntityTag") == null) {
               lvt_8_1_.setTrackOutput(p_180633_1_.getGameRules().getBoolean(GameRules.SEND_COMMAND_FEEDBACK));
               lvt_7_1_.setAuto(this == Blocks.CHAIN_COMMAND_BLOCK);
            }

            if (lvt_7_1_.getMode() == CommandBlockTileEntity.Mode.SEQUENCE) {
               boolean lvt_9_1_ = p_180633_1_.isBlockPowered(p_180633_2_);
               lvt_7_1_.setPowered(lvt_9_1_);
            }
         }

      }
   }

   public BlockRenderType getRenderType(BlockState p_149645_1_) {
      return BlockRenderType.MODEL;
   }

   public BlockState rotate(BlockState p_185499_1_, Rotation p_185499_2_) {
      return (BlockState)p_185499_1_.with(FACING, p_185499_2_.rotate((Direction)p_185499_1_.get(FACING)));
   }

   public BlockState mirror(BlockState p_185471_1_, Mirror p_185471_2_) {
      return p_185471_1_.rotate(p_185471_2_.toRotation((Direction)p_185471_1_.get(FACING)));
   }

   protected void fillStateContainer(StateContainer.Builder<Block, BlockState> p_206840_1_) {
      p_206840_1_.add(FACING, CONDITIONAL);
   }

   public BlockState getStateForPlacement(BlockItemUseContext p_196258_1_) {
      return (BlockState)this.getDefaultState().with(FACING, p_196258_1_.getNearestLookingDirection().getOpposite());
   }

   private static void executeChain(World p_193386_0_, BlockPos p_193386_1_, Direction p_193386_2_) {
      BlockPos.Mutable lvt_3_1_ = new BlockPos.Mutable(p_193386_1_);
      GameRules lvt_4_1_ = p_193386_0_.getGameRules();

      int lvt_5_1_;
      BlockState lvt_6_1_;
      for(lvt_5_1_ = lvt_4_1_.getInt(GameRules.MAX_COMMAND_CHAIN_LENGTH); lvt_5_1_-- > 0; p_193386_2_ = (Direction)lvt_6_1_.get(FACING)) {
         lvt_3_1_.move(p_193386_2_);
         lvt_6_1_ = p_193386_0_.getBlockState(lvt_3_1_);
         Block lvt_7_1_ = lvt_6_1_.getBlock();
         if (lvt_7_1_ != Blocks.CHAIN_COMMAND_BLOCK) {
            break;
         }

         TileEntity lvt_8_1_ = p_193386_0_.getTileEntity(lvt_3_1_);
         if (!(lvt_8_1_ instanceof CommandBlockTileEntity)) {
            break;
         }

         CommandBlockTileEntity lvt_9_1_ = (CommandBlockTileEntity)lvt_8_1_;
         if (lvt_9_1_.getMode() != CommandBlockTileEntity.Mode.SEQUENCE) {
            break;
         }

         if (lvt_9_1_.isPowered() || lvt_9_1_.isAuto()) {
            CommandBlockLogic lvt_10_1_ = lvt_9_1_.getCommandBlockLogic();
            if (lvt_9_1_.setConditionMet()) {
               if (!lvt_10_1_.trigger(p_193386_0_)) {
                  break;
               }

               p_193386_0_.updateComparatorOutputLevel(lvt_3_1_, lvt_7_1_);
            } else if (lvt_9_1_.isConditional()) {
               lvt_10_1_.setSuccessCount(0);
            }
         }
      }

      if (lvt_5_1_ <= 0) {
         int lvt_6_2_ = Math.max(lvt_4_1_.getInt(GameRules.MAX_COMMAND_CHAIN_LENGTH), 0);
         LOGGER.warn("Command Block chain tried to execute more than {} steps!", lvt_6_2_);
      }

   }

   static {
      FACING = DirectionalBlock.FACING;
      CONDITIONAL = BlockStateProperties.CONDITIONAL;
   }
}
