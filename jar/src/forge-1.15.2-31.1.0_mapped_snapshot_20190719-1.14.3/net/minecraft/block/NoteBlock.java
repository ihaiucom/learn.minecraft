package net.minecraft.block;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.NoteBlockInstrument;
import net.minecraft.stats.Stats;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.NoteBlockEvent;

public class NoteBlock extends Block {
   public static final EnumProperty<NoteBlockInstrument> INSTRUMENT;
   public static final BooleanProperty POWERED;
   public static final IntegerProperty NOTE;

   public NoteBlock(Block.Properties p_i48359_1_) {
      super(p_i48359_1_);
      this.setDefaultState((BlockState)((BlockState)((BlockState)((BlockState)this.stateContainer.getBaseState()).with(INSTRUMENT, NoteBlockInstrument.HARP)).with(NOTE, 0)).with(POWERED, false));
   }

   public BlockState getStateForPlacement(BlockItemUseContext p_196258_1_) {
      return (BlockState)this.getDefaultState().with(INSTRUMENT, NoteBlockInstrument.byState(p_196258_1_.getWorld().getBlockState(p_196258_1_.getPos().down())));
   }

   public BlockState updatePostPlacement(BlockState p_196271_1_, Direction p_196271_2_, BlockState p_196271_3_, IWorld p_196271_4_, BlockPos p_196271_5_, BlockPos p_196271_6_) {
      return p_196271_2_ == Direction.DOWN ? (BlockState)p_196271_1_.with(INSTRUMENT, NoteBlockInstrument.byState(p_196271_3_)) : super.updatePostPlacement(p_196271_1_, p_196271_2_, p_196271_3_, p_196271_4_, p_196271_5_, p_196271_6_);
   }

   public void neighborChanged(BlockState p_220069_1_, World p_220069_2_, BlockPos p_220069_3_, Block p_220069_4_, BlockPos p_220069_5_, boolean p_220069_6_) {
      boolean flag = p_220069_2_.isBlockPowered(p_220069_3_);
      if (flag != (Boolean)p_220069_1_.get(POWERED)) {
         if (flag) {
            this.triggerNote(p_220069_2_, p_220069_3_);
         }

         p_220069_2_.setBlockState(p_220069_3_, (BlockState)p_220069_1_.with(POWERED, flag), 3);
      }

   }

   private void triggerNote(World p_196482_1_, BlockPos p_196482_2_) {
      if (p_196482_1_.isAirBlock(p_196482_2_.up())) {
         p_196482_1_.addBlockEvent(p_196482_2_, this, 0, 0);
      }

   }

   public ActionResultType func_225533_a_(BlockState p_225533_1_, World p_225533_2_, BlockPos p_225533_3_, PlayerEntity p_225533_4_, Hand p_225533_5_, BlockRayTraceResult p_225533_6_) {
      if (p_225533_2_.isRemote) {
         return ActionResultType.SUCCESS;
      } else {
         int _new = ForgeHooks.onNoteChange(p_225533_2_, p_225533_3_, p_225533_1_, (Integer)p_225533_1_.get(NOTE), (Integer)((BlockState)p_225533_1_.cycle(NOTE)).get(NOTE));
         if (_new == -1) {
            return ActionResultType.FAIL;
         } else {
            p_225533_1_ = (BlockState)p_225533_1_.with(NOTE, _new);
            p_225533_1_ = (BlockState)p_225533_1_.cycle(NOTE);
            p_225533_2_.setBlockState(p_225533_3_, p_225533_1_, 3);
            this.triggerNote(p_225533_2_, p_225533_3_);
            p_225533_4_.addStat(Stats.TUNE_NOTEBLOCK);
            return ActionResultType.SUCCESS;
         }
      }
   }

   public void onBlockClicked(BlockState p_196270_1_, World p_196270_2_, BlockPos p_196270_3_, PlayerEntity p_196270_4_) {
      if (!p_196270_2_.isRemote) {
         this.triggerNote(p_196270_2_, p_196270_3_);
         p_196270_4_.addStat(Stats.PLAY_NOTEBLOCK);
      }

   }

   public boolean eventReceived(BlockState p_189539_1_, World p_189539_2_, BlockPos p_189539_3_, int p_189539_4_, int p_189539_5_) {
      NoteBlockEvent.Play e = new NoteBlockEvent.Play(p_189539_2_, p_189539_3_, p_189539_1_, (Integer)p_189539_1_.get(NOTE), (NoteBlockInstrument)p_189539_1_.get(INSTRUMENT));
      if (MinecraftForge.EVENT_BUS.post(e)) {
         return false;
      } else {
         p_189539_1_ = (BlockState)((BlockState)p_189539_1_.with(NOTE, e.getVanillaNoteId())).with(INSTRUMENT, e.getInstrument());
         int i = (Integer)p_189539_1_.get(NOTE);
         float f = (float)Math.pow(2.0D, (double)(i - 12) / 12.0D);
         p_189539_2_.playSound((PlayerEntity)null, p_189539_3_, ((NoteBlockInstrument)p_189539_1_.get(INSTRUMENT)).getSound(), SoundCategory.RECORDS, 3.0F, f);
         p_189539_2_.addParticle(ParticleTypes.NOTE, (double)p_189539_3_.getX() + 0.5D, (double)p_189539_3_.getY() + 1.2D, (double)p_189539_3_.getZ() + 0.5D, (double)i / 24.0D, 0.0D, 0.0D);
         return true;
      }
   }

   protected void fillStateContainer(StateContainer.Builder<Block, BlockState> p_206840_1_) {
      p_206840_1_.add(INSTRUMENT, POWERED, NOTE);
   }

   static {
      INSTRUMENT = BlockStateProperties.NOTE_BLOCK_INSTRUMENT;
      POWERED = BlockStateProperties.POWERED;
      NOTE = BlockStateProperties.NOTE_0_24;
   }
}
