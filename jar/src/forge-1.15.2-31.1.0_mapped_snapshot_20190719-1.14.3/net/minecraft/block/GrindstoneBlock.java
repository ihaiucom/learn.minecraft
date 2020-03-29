package net.minecraft.block;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.GrindstoneContainer;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.inventory.container.SimpleNamedContainerProvider;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.AttachFace;
import net.minecraft.stats.Stats;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

public class GrindstoneBlock extends HorizontalFaceBlock {
   public static final VoxelShape field_220238_a = Block.makeCuboidShape(2.0D, 0.0D, 6.0D, 4.0D, 7.0D, 10.0D);
   public static final VoxelShape field_220239_b = Block.makeCuboidShape(12.0D, 0.0D, 6.0D, 14.0D, 7.0D, 10.0D);
   public static final VoxelShape field_220240_c = Block.makeCuboidShape(2.0D, 7.0D, 5.0D, 4.0D, 13.0D, 11.0D);
   public static final VoxelShape field_220241_d = Block.makeCuboidShape(12.0D, 7.0D, 5.0D, 14.0D, 13.0D, 11.0D);
   public static final VoxelShape field_220242_e;
   public static final VoxelShape field_220243_f;
   public static final VoxelShape field_220244_g;
   public static final VoxelShape field_220245_h;
   public static final VoxelShape field_220246_i;
   public static final VoxelShape field_220247_j;
   public static final VoxelShape field_220248_k;
   public static final VoxelShape field_220249_w;
   public static final VoxelShape field_220250_x;
   public static final VoxelShape field_220251_y;
   public static final VoxelShape field_220252_z;
   public static final VoxelShape field_220213_A;
   public static final VoxelShape field_220214_B;
   public static final VoxelShape field_220215_D;
   public static final VoxelShape field_220216_E;
   public static final VoxelShape field_220217_F;
   public static final VoxelShape field_220218_G;
   public static final VoxelShape field_220219_H;
   public static final VoxelShape field_220220_I;
   public static final VoxelShape field_220221_J;
   public static final VoxelShape field_220222_K;
   public static final VoxelShape field_220223_L;
   public static final VoxelShape field_220224_M;
   public static final VoxelShape field_220225_N;
   public static final VoxelShape field_220226_O;
   public static final VoxelShape field_220227_P;
   public static final VoxelShape field_220228_Q;
   public static final VoxelShape field_220229_R;
   public static final VoxelShape field_220230_S;
   public static final VoxelShape field_220231_T;
   public static final VoxelShape field_220232_U;
   public static final VoxelShape field_220233_V;
   public static final VoxelShape field_220234_W;
   public static final VoxelShape field_220235_X;
   public static final VoxelShape field_220236_Y;
   public static final VoxelShape field_220237_Z;
   public static final VoxelShape field_220188_aa;
   public static final VoxelShape field_220189_ab;
   public static final VoxelShape field_220190_ac;
   public static final VoxelShape field_220191_ad;
   public static final VoxelShape field_220192_ae;
   public static final VoxelShape field_220193_af;
   public static final VoxelShape field_220194_ag;
   public static final VoxelShape field_220195_ah;
   public static final VoxelShape field_220196_ai;
   public static final VoxelShape field_220197_aj;
   public static final VoxelShape field_220198_ak;
   public static final VoxelShape field_220199_al;
   public static final VoxelShape field_220200_am;
   public static final VoxelShape field_220201_an;
   public static final VoxelShape field_220202_ao;
   public static final VoxelShape field_220203_ap;
   public static final VoxelShape field_220204_aq;
   public static final VoxelShape field_220205_ar;
   public static final VoxelShape field_220206_as;
   public static final VoxelShape field_220207_at;
   public static final VoxelShape field_220208_au;
   public static final VoxelShape field_220209_av;
   public static final VoxelShape field_220210_aw;
   public static final VoxelShape field_220211_ax;
   private static final TranslationTextComponent field_220212_az;

   protected GrindstoneBlock(Block.Properties p_i49983_1_) {
      super(p_i49983_1_);
      this.setDefaultState((BlockState)((BlockState)((BlockState)this.stateContainer.getBaseState()).with(HORIZONTAL_FACING, Direction.NORTH)).with(FACE, AttachFace.WALL));
   }

   public BlockRenderType getRenderType(BlockState p_149645_1_) {
      return BlockRenderType.MODEL;
   }

   private VoxelShape func_220186_q(BlockState p_220186_1_) {
      Direction lvt_2_1_ = (Direction)p_220186_1_.get(HORIZONTAL_FACING);
      switch((AttachFace)p_220186_1_.get(FACE)) {
      case FLOOR:
         if (lvt_2_1_ != Direction.NORTH && lvt_2_1_ != Direction.SOUTH) {
            return field_220213_A;
         }

         return field_220245_h;
      case WALL:
         if (lvt_2_1_ == Direction.NORTH) {
            return field_220229_R;
         } else if (lvt_2_1_ == Direction.SOUTH) {
            return field_220221_J;
         } else {
            if (lvt_2_1_ == Direction.EAST) {
               return field_220195_ah;
            }

            return field_220237_Z;
         }
      case CEILING:
         if (lvt_2_1_ != Direction.NORTH && lvt_2_1_ != Direction.SOUTH) {
            return field_220211_ax;
         }

         return field_220203_ap;
      default:
         return field_220213_A;
      }
   }

   public VoxelShape getCollisionShape(BlockState p_220071_1_, IBlockReader p_220071_2_, BlockPos p_220071_3_, ISelectionContext p_220071_4_) {
      return this.func_220186_q(p_220071_1_);
   }

   public VoxelShape getShape(BlockState p_220053_1_, IBlockReader p_220053_2_, BlockPos p_220053_3_, ISelectionContext p_220053_4_) {
      return this.func_220186_q(p_220053_1_);
   }

   public boolean isValidPosition(BlockState p_196260_1_, IWorldReader p_196260_2_, BlockPos p_196260_3_) {
      return true;
   }

   public ActionResultType func_225533_a_(BlockState p_225533_1_, World p_225533_2_, BlockPos p_225533_3_, PlayerEntity p_225533_4_, Hand p_225533_5_, BlockRayTraceResult p_225533_6_) {
      if (p_225533_2_.isRemote) {
         return ActionResultType.SUCCESS;
      } else {
         p_225533_4_.openContainer(p_225533_1_.getContainer(p_225533_2_, p_225533_3_));
         p_225533_4_.addStat(Stats.field_226146_aB_);
         return ActionResultType.SUCCESS;
      }
   }

   public INamedContainerProvider getContainer(BlockState p_220052_1_, World p_220052_2_, BlockPos p_220052_3_) {
      return new SimpleNamedContainerProvider((p_220187_2_, p_220187_3_, p_220187_4_) -> {
         return new GrindstoneContainer(p_220187_2_, p_220187_3_, IWorldPosCallable.of(p_220052_2_, p_220052_3_));
      }, field_220212_az);
   }

   public BlockState rotate(BlockState p_185499_1_, Rotation p_185499_2_) {
      return (BlockState)p_185499_1_.with(HORIZONTAL_FACING, p_185499_2_.rotate((Direction)p_185499_1_.get(HORIZONTAL_FACING)));
   }

   public BlockState mirror(BlockState p_185471_1_, Mirror p_185471_2_) {
      return p_185471_1_.rotate(p_185471_2_.toRotation((Direction)p_185471_1_.get(HORIZONTAL_FACING)));
   }

   protected void fillStateContainer(StateContainer.Builder<Block, BlockState> p_206840_1_) {
      p_206840_1_.add(HORIZONTAL_FACING, FACE);
   }

   public boolean allowsMovement(BlockState p_196266_1_, IBlockReader p_196266_2_, BlockPos p_196266_3_, PathType p_196266_4_) {
      return false;
   }

   static {
      field_220242_e = VoxelShapes.or(field_220238_a, field_220240_c);
      field_220243_f = VoxelShapes.or(field_220239_b, field_220241_d);
      field_220244_g = VoxelShapes.or(field_220242_e, field_220243_f);
      field_220245_h = VoxelShapes.or(field_220244_g, Block.makeCuboidShape(4.0D, 4.0D, 2.0D, 12.0D, 16.0D, 14.0D));
      field_220246_i = Block.makeCuboidShape(6.0D, 0.0D, 2.0D, 10.0D, 7.0D, 4.0D);
      field_220247_j = Block.makeCuboidShape(6.0D, 0.0D, 12.0D, 10.0D, 7.0D, 14.0D);
      field_220248_k = Block.makeCuboidShape(5.0D, 7.0D, 2.0D, 11.0D, 13.0D, 4.0D);
      field_220249_w = Block.makeCuboidShape(5.0D, 7.0D, 12.0D, 11.0D, 13.0D, 14.0D);
      field_220250_x = VoxelShapes.or(field_220246_i, field_220248_k);
      field_220251_y = VoxelShapes.or(field_220247_j, field_220249_w);
      field_220252_z = VoxelShapes.or(field_220250_x, field_220251_y);
      field_220213_A = VoxelShapes.or(field_220252_z, Block.makeCuboidShape(2.0D, 4.0D, 4.0D, 14.0D, 16.0D, 12.0D));
      field_220214_B = Block.makeCuboidShape(2.0D, 6.0D, 0.0D, 4.0D, 10.0D, 7.0D);
      field_220215_D = Block.makeCuboidShape(12.0D, 6.0D, 0.0D, 14.0D, 10.0D, 7.0D);
      field_220216_E = Block.makeCuboidShape(2.0D, 5.0D, 7.0D, 4.0D, 11.0D, 13.0D);
      field_220217_F = Block.makeCuboidShape(12.0D, 5.0D, 7.0D, 14.0D, 11.0D, 13.0D);
      field_220218_G = VoxelShapes.or(field_220214_B, field_220216_E);
      field_220219_H = VoxelShapes.or(field_220215_D, field_220217_F);
      field_220220_I = VoxelShapes.or(field_220218_G, field_220219_H);
      field_220221_J = VoxelShapes.or(field_220220_I, Block.makeCuboidShape(4.0D, 2.0D, 4.0D, 12.0D, 14.0D, 16.0D));
      field_220222_K = Block.makeCuboidShape(2.0D, 6.0D, 7.0D, 4.0D, 10.0D, 16.0D);
      field_220223_L = Block.makeCuboidShape(12.0D, 6.0D, 7.0D, 14.0D, 10.0D, 16.0D);
      field_220224_M = Block.makeCuboidShape(2.0D, 5.0D, 3.0D, 4.0D, 11.0D, 9.0D);
      field_220225_N = Block.makeCuboidShape(12.0D, 5.0D, 3.0D, 14.0D, 11.0D, 9.0D);
      field_220226_O = VoxelShapes.or(field_220222_K, field_220224_M);
      field_220227_P = VoxelShapes.or(field_220223_L, field_220225_N);
      field_220228_Q = VoxelShapes.or(field_220226_O, field_220227_P);
      field_220229_R = VoxelShapes.or(field_220228_Q, Block.makeCuboidShape(4.0D, 2.0D, 0.0D, 12.0D, 14.0D, 12.0D));
      field_220230_S = Block.makeCuboidShape(7.0D, 6.0D, 2.0D, 16.0D, 10.0D, 4.0D);
      field_220231_T = Block.makeCuboidShape(7.0D, 6.0D, 12.0D, 16.0D, 10.0D, 14.0D);
      field_220232_U = Block.makeCuboidShape(3.0D, 5.0D, 2.0D, 9.0D, 11.0D, 4.0D);
      field_220233_V = Block.makeCuboidShape(3.0D, 5.0D, 12.0D, 9.0D, 11.0D, 14.0D);
      field_220234_W = VoxelShapes.or(field_220230_S, field_220232_U);
      field_220235_X = VoxelShapes.or(field_220231_T, field_220233_V);
      field_220236_Y = VoxelShapes.or(field_220234_W, field_220235_X);
      field_220237_Z = VoxelShapes.or(field_220236_Y, Block.makeCuboidShape(0.0D, 2.0D, 4.0D, 12.0D, 14.0D, 12.0D));
      field_220188_aa = Block.makeCuboidShape(0.0D, 6.0D, 2.0D, 9.0D, 10.0D, 4.0D);
      field_220189_ab = Block.makeCuboidShape(0.0D, 6.0D, 12.0D, 9.0D, 10.0D, 14.0D);
      field_220190_ac = Block.makeCuboidShape(7.0D, 5.0D, 2.0D, 13.0D, 11.0D, 4.0D);
      field_220191_ad = Block.makeCuboidShape(7.0D, 5.0D, 12.0D, 13.0D, 11.0D, 14.0D);
      field_220192_ae = VoxelShapes.or(field_220188_aa, field_220190_ac);
      field_220193_af = VoxelShapes.or(field_220189_ab, field_220191_ad);
      field_220194_ag = VoxelShapes.or(field_220192_ae, field_220193_af);
      field_220195_ah = VoxelShapes.or(field_220194_ag, Block.makeCuboidShape(4.0D, 2.0D, 4.0D, 16.0D, 14.0D, 12.0D));
      field_220196_ai = Block.makeCuboidShape(2.0D, 9.0D, 6.0D, 4.0D, 16.0D, 10.0D);
      field_220197_aj = Block.makeCuboidShape(12.0D, 9.0D, 6.0D, 14.0D, 16.0D, 10.0D);
      field_220198_ak = Block.makeCuboidShape(2.0D, 3.0D, 5.0D, 4.0D, 9.0D, 11.0D);
      field_220199_al = Block.makeCuboidShape(12.0D, 3.0D, 5.0D, 14.0D, 9.0D, 11.0D);
      field_220200_am = VoxelShapes.or(field_220196_ai, field_220198_ak);
      field_220201_an = VoxelShapes.or(field_220197_aj, field_220199_al);
      field_220202_ao = VoxelShapes.or(field_220200_am, field_220201_an);
      field_220203_ap = VoxelShapes.or(field_220202_ao, Block.makeCuboidShape(4.0D, 0.0D, 2.0D, 12.0D, 12.0D, 14.0D));
      field_220204_aq = Block.makeCuboidShape(6.0D, 9.0D, 2.0D, 10.0D, 16.0D, 4.0D);
      field_220205_ar = Block.makeCuboidShape(6.0D, 9.0D, 12.0D, 10.0D, 16.0D, 14.0D);
      field_220206_as = Block.makeCuboidShape(5.0D, 3.0D, 2.0D, 11.0D, 9.0D, 4.0D);
      field_220207_at = Block.makeCuboidShape(5.0D, 3.0D, 12.0D, 11.0D, 9.0D, 14.0D);
      field_220208_au = VoxelShapes.or(field_220204_aq, field_220206_as);
      field_220209_av = VoxelShapes.or(field_220205_ar, field_220207_at);
      field_220210_aw = VoxelShapes.or(field_220208_au, field_220209_av);
      field_220211_ax = VoxelShapes.or(field_220210_aw, Block.makeCuboidShape(2.0D, 0.0D, 4.0D, 14.0D, 12.0D, 12.0D));
      field_220212_az = new TranslationTextComponent("container.grindstone_title", new Object[0]);
   }
}
