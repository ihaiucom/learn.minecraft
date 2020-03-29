package net.minecraft.world;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.NetherPortalBlock;
import net.minecraft.block.pattern.BlockPattern;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.village.PointOfInterest;
import net.minecraft.village.PointOfInterestManager;
import net.minecraft.village.PointOfInterestType;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.server.TicketType;
import net.minecraftforge.common.util.ITeleporter;

public class Teleporter implements ITeleporter {
   protected final ServerWorld world;
   protected final Random random;

   public Teleporter(ServerWorld p_i1963_1_) {
      this.world = p_i1963_1_;
      this.random = new Random(p_i1963_1_.getSeed());
   }

   public boolean func_222268_a(Entity p_222268_1_, float p_222268_2_) {
      Vec3d vec3d = p_222268_1_.getLastPortalVec();
      Direction direction = p_222268_1_.getTeleportDirection();
      BlockPattern.PortalInfo blockpattern$portalinfo = this.func_222272_a(new BlockPos(p_222268_1_), p_222268_1_.getMotion(), direction, vec3d.x, vec3d.y, p_222268_1_ instanceof PlayerEntity);
      if (blockpattern$portalinfo == null) {
         return false;
      } else {
         Vec3d vec3d1 = blockpattern$portalinfo.field_222505_a;
         Vec3d vec3d2 = blockpattern$portalinfo.field_222506_b;
         p_222268_1_.setMotion(vec3d2);
         p_222268_1_.rotationYaw = p_222268_2_ + (float)blockpattern$portalinfo.field_222507_c;
         p_222268_1_.func_225653_b_(vec3d1.x, vec3d1.y, vec3d1.z);
         return true;
      }
   }

   @Nullable
   public BlockPattern.PortalInfo func_222272_a(BlockPos p_222272_1_, Vec3d p_222272_2_, Direction p_222272_3_, double p_222272_4_, double p_222272_6_, boolean p_222272_8_) {
      PointOfInterestManager pointofinterestmanager = this.world.func_217443_B();
      pointofinterestmanager.func_226347_a_(this.world, p_222272_1_, 128);
      List<PointOfInterest> list = (List)pointofinterestmanager.func_226353_b_((p_lambda$func_222272_a$0_0_) -> {
         return p_lambda$func_222272_a$0_0_ == PointOfInterestType.field_226358_u_;
      }, p_222272_1_, 128, PointOfInterestManager.Status.ANY).collect(Collectors.toList());
      Optional<PointOfInterest> optional = list.stream().min(Comparator.comparingDouble((p_lambda$func_222272_a$1_1_) -> {
         return p_lambda$func_222272_a$1_1_.getPos().distanceSq(p_222272_1_);
      }).thenComparingInt((p_lambda$func_222272_a$2_0_) -> {
         return p_lambda$func_222272_a$2_0_.getPos().getY();
      }));
      return (BlockPattern.PortalInfo)optional.map((p_lambda$func_222272_a$3_7_) -> {
         BlockPos blockpos = p_lambda$func_222272_a$3_7_.getPos();
         this.world.getChunkProvider().func_217228_a(TicketType.PORTAL, new ChunkPos(blockpos), 3, blockpos);
         BlockPattern.PatternHelper blockpattern$patternhelper = NetherPortalBlock.createPatternHelper(this.world, blockpos);
         return blockpattern$patternhelper.func_222504_a(p_222272_3_, blockpos, p_222272_6_, p_222272_2_, p_222272_4_);
      }).orElse((BlockPattern.PortalInfo)null);
   }

   public boolean makePortal(Entity p_85188_1_) {
      int i = true;
      double d0 = -1.0D;
      int j = MathHelper.floor(p_85188_1_.func_226277_ct_());
      int k = MathHelper.floor(p_85188_1_.func_226278_cu_());
      int l = MathHelper.floor(p_85188_1_.func_226281_cx_());
      int i1 = j;
      int j1 = k;
      int k1 = l;
      int l1 = 0;
      int i2 = this.random.nextInt(4);
      BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();

      int l5;
      double d3;
      int j6;
      double d4;
      int i7;
      int l7;
      int l8;
      int k9;
      int i10;
      int k10;
      int i11;
      int j11;
      int k11;
      double d6;
      double d8;
      for(l5 = j - 16; l5 <= j + 16; ++l5) {
         d3 = (double)l5 + 0.5D - p_85188_1_.func_226277_ct_();

         for(j6 = l - 16; j6 <= l + 16; ++j6) {
            d4 = (double)j6 + 0.5D - p_85188_1_.func_226281_cx_();

            label274:
            for(i7 = this.world.getActualHeight() - 1; i7 >= 0; --i7) {
               if (this.world.isAirBlock(blockpos$mutable.setPos(l5, i7, j6))) {
                  while(i7 > 0 && this.world.isAirBlock(blockpos$mutable.setPos(l5, i7 - 1, j6))) {
                     --i7;
                  }

                  for(l7 = i2; l7 < i2 + 4; ++l7) {
                     l8 = l7 % 2;
                     k9 = 1 - l8;
                     if (l7 % 4 >= 2) {
                        l8 = -l8;
                        k9 = -k9;
                     }

                     for(i10 = 0; i10 < 3; ++i10) {
                        for(k10 = 0; k10 < 4; ++k10) {
                           for(i11 = -1; i11 < 4; ++i11) {
                              j11 = l5 + (k10 - 1) * l8 + i10 * k9;
                              k11 = i7 + i11;
                              int k5 = j6 + (k10 - 1) * k9 - i10 * l8;
                              blockpos$mutable.setPos(j11, k11, k5);
                              if (i11 < 0 && !this.world.getBlockState(blockpos$mutable).getMaterial().isSolid() || i11 >= 0 && !this.world.isAirBlock(blockpos$mutable)) {
                                 continue label274;
                              }
                           }
                        }
                     }

                     d6 = (double)i7 + 0.5D - p_85188_1_.func_226278_cu_();
                     d8 = d3 * d3 + d6 * d6 + d4 * d4;
                     if (d0 < 0.0D || d8 < d0) {
                        d0 = d8;
                        i1 = l5;
                        j1 = i7;
                        k1 = j6;
                        l1 = l7 % 4;
                     }
                  }
               }
            }
         }
      }

      if (d0 < 0.0D) {
         for(l5 = j - 16; l5 <= j + 16; ++l5) {
            d3 = (double)l5 + 0.5D - p_85188_1_.func_226277_ct_();

            for(j6 = l - 16; j6 <= l + 16; ++j6) {
               d4 = (double)j6 + 0.5D - p_85188_1_.func_226281_cx_();

               label212:
               for(i7 = this.world.getActualHeight() - 1; i7 >= 0; --i7) {
                  if (this.world.isAirBlock(blockpos$mutable.setPos(l5, i7, j6))) {
                     while(i7 > 0 && this.world.isAirBlock(blockpos$mutable.setPos(l5, i7 - 1, j6))) {
                        --i7;
                     }

                     for(l7 = i2; l7 < i2 + 2; ++l7) {
                        l8 = l7 % 2;
                        k9 = 1 - l8;

                        for(i10 = 0; i10 < 4; ++i10) {
                           for(k10 = -1; k10 < 4; ++k10) {
                              i11 = l5 + (i10 - 1) * l8;
                              j11 = i7 + k10;
                              k11 = j6 + (i10 - 1) * k9;
                              blockpos$mutable.setPos(i11, j11, k11);
                              if (k10 < 0 && !this.world.getBlockState(blockpos$mutable).getMaterial().isSolid() || k10 >= 0 && !this.world.isAirBlock(blockpos$mutable)) {
                                 continue label212;
                              }
                           }
                        }

                        d6 = (double)i7 + 0.5D - p_85188_1_.func_226278_cu_();
                        d8 = d3 * d3 + d6 * d6 + d4 * d4;
                        if (d0 < 0.0D || d8 < d0) {
                           d0 = d8;
                           i1 = l5;
                           j1 = i7;
                           k1 = j6;
                           l1 = l7 % 2;
                        }
                     }
                  }
               }
            }
         }
      }

      l5 = i1;
      int k2 = j1;
      int k6 = k1;
      j6 = l1 % 2;
      int i3 = 1 - j6;
      if (l1 % 4 >= 2) {
         j6 = -j6;
         i3 = -i3;
      }

      int j7;
      if (d0 < 0.0D) {
         j1 = MathHelper.clamp(j1, 70, this.world.getActualHeight() - 10);
         k2 = j1;

         for(j7 = -1; j7 <= 1; ++j7) {
            for(i7 = 1; i7 < 3; ++i7) {
               for(l7 = -1; l7 < 3; ++l7) {
                  l8 = l5 + (i7 - 1) * j6 + j7 * i3;
                  k9 = k2 + l7;
                  i10 = k6 + (i7 - 1) * i3 - j7 * j6;
                  boolean flag = l7 < 0;
                  blockpos$mutable.setPos(l8, k9, i10);
                  this.world.setBlockState(blockpos$mutable, flag ? Blocks.OBSIDIAN.getDefaultState() : Blocks.AIR.getDefaultState());
               }
            }
         }
      }

      for(j7 = -1; j7 < 3; ++j7) {
         for(i7 = -1; i7 < 4; ++i7) {
            if (j7 == -1 || j7 == 2 || i7 == -1 || i7 == 3) {
               blockpos$mutable.setPos(l5 + j7 * j6, k2 + i7, k6 + j7 * i3);
               this.world.setBlockState(blockpos$mutable, Blocks.OBSIDIAN.getDefaultState(), 3);
            }
         }
      }

      BlockState blockstate = (BlockState)Blocks.NETHER_PORTAL.getDefaultState().with(NetherPortalBlock.AXIS, j6 == 0 ? Direction.Axis.Z : Direction.Axis.X);

      for(i7 = 0; i7 < 2; ++i7) {
         for(l7 = 0; l7 < 3; ++l7) {
            blockpos$mutable.setPos(l5 + i7 * j6, k2 + l7, k6 + i7 * i3);
            this.world.setBlockState(blockpos$mutable, blockstate, 18);
         }
      }

      return true;
   }
}
