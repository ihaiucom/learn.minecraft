package net.minecraft.world.gen.feature.template;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ILiquidContainer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.PaintingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.IFluidState;
import net.minecraft.inventory.IClearable;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.DoubleNBT;
import net.minecraft.nbt.IntNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Mirror;
import net.minecraft.util.ObjectIntIdentityMap;
import net.minecraft.util.Rotation;
import net.minecraft.util.SharedConstants;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.shapes.BitSetVoxelShapePart;
import net.minecraft.util.math.shapes.VoxelShapePart;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class Template {
   private final List<List<Template.BlockInfo>> blocks = Lists.newArrayList();
   private final List<Template.EntityInfo> entities = Lists.newArrayList();
   private BlockPos size;
   private String author;

   public Template() {
      this.size = BlockPos.ZERO;
      this.author = "?";
   }

   public BlockPos getSize() {
      return this.size;
   }

   public void setAuthor(String p_186252_1_) {
      this.author = p_186252_1_;
   }

   public String getAuthor() {
      return this.author;
   }

   public void takeBlocksFromWorld(World p_186254_1_, BlockPos p_186254_2_, BlockPos p_186254_3_, boolean p_186254_4_, @Nullable Block p_186254_5_) {
      if (p_186254_3_.getX() >= 1 && p_186254_3_.getY() >= 1 && p_186254_3_.getZ() >= 1) {
         BlockPos blockpos = p_186254_2_.add(p_186254_3_).add(-1, -1, -1);
         List<Template.BlockInfo> list = Lists.newArrayList();
         List<Template.BlockInfo> list1 = Lists.newArrayList();
         List<Template.BlockInfo> list2 = Lists.newArrayList();
         BlockPos blockpos1 = new BlockPos(Math.min(p_186254_2_.getX(), blockpos.getX()), Math.min(p_186254_2_.getY(), blockpos.getY()), Math.min(p_186254_2_.getZ(), blockpos.getZ()));
         BlockPos blockpos2 = new BlockPos(Math.max(p_186254_2_.getX(), blockpos.getX()), Math.max(p_186254_2_.getY(), blockpos.getY()), Math.max(p_186254_2_.getZ(), blockpos.getZ()));
         this.size = p_186254_3_;
         Iterator var12 = BlockPos.getAllInBoxMutable(blockpos1, blockpos2).iterator();

         while(true) {
            while(true) {
               BlockPos blockpos3;
               BlockPos blockpos4;
               BlockState blockstate;
               do {
                  if (!var12.hasNext()) {
                     List<Template.BlockInfo> list3 = Lists.newArrayList();
                     list3.addAll(list);
                     list3.addAll(list1);
                     list3.addAll(list2);
                     this.blocks.clear();
                     this.blocks.add(list3);
                     if (p_186254_4_) {
                        this.takeEntitiesFromWorld(p_186254_1_, blockpos1, blockpos2.add(1, 1, 1));
                     } else {
                        this.entities.clear();
                     }

                     return;
                  }

                  blockpos3 = (BlockPos)var12.next();
                  blockpos4 = blockpos3.subtract(blockpos1);
                  blockstate = p_186254_1_.getBlockState(blockpos3);
               } while(p_186254_5_ != null && p_186254_5_ == blockstate.getBlock());

               TileEntity tileentity = p_186254_1_.getTileEntity(blockpos3);
               if (tileentity != null) {
                  CompoundNBT compoundnbt = tileentity.write(new CompoundNBT());
                  compoundnbt.remove("x");
                  compoundnbt.remove("y");
                  compoundnbt.remove("z");
                  list1.add(new Template.BlockInfo(blockpos4, blockstate, compoundnbt));
               } else if (!blockstate.isOpaqueCube(p_186254_1_, blockpos3) && !blockstate.func_224756_o(p_186254_1_, blockpos3)) {
                  list2.add(new Template.BlockInfo(blockpos4, blockstate, (CompoundNBT)null));
               } else {
                  list.add(new Template.BlockInfo(blockpos4, blockstate, (CompoundNBT)null));
               }
            }
         }
      }
   }

   private void takeEntitiesFromWorld(World p_186255_1_, BlockPos p_186255_2_, BlockPos p_186255_3_) {
      List<Entity> list = p_186255_1_.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(p_186255_2_, p_186255_3_), (p_lambda$takeEntitiesFromWorld$0_0_) -> {
         return !(p_lambda$takeEntitiesFromWorld$0_0_ instanceof PlayerEntity);
      });
      this.entities.clear();

      Vec3d vec3d;
      CompoundNBT compoundnbt;
      BlockPos blockpos;
      for(Iterator var5 = list.iterator(); var5.hasNext(); this.entities.add(new Template.EntityInfo(vec3d, blockpos, compoundnbt))) {
         Entity entity = (Entity)var5.next();
         vec3d = new Vec3d(entity.func_226277_ct_() - (double)p_186255_2_.getX(), entity.func_226278_cu_() - (double)p_186255_2_.getY(), entity.func_226281_cx_() - (double)p_186255_2_.getZ());
         compoundnbt = new CompoundNBT();
         entity.writeUnlessPassenger(compoundnbt);
         if (entity instanceof PaintingEntity) {
            blockpos = ((PaintingEntity)entity).getHangingPosition().subtract(p_186255_2_);
         } else {
            blockpos = new BlockPos(vec3d);
         }
      }

   }

   public List<Template.BlockInfo> func_215381_a(BlockPos p_215381_1_, PlacementSettings p_215381_2_, Block p_215381_3_) {
      return this.func_215386_a(p_215381_1_, p_215381_2_, p_215381_3_, true);
   }

   public List<Template.BlockInfo> func_215386_a(BlockPos p_215386_1_, PlacementSettings p_215386_2_, Block p_215386_3_, boolean p_215386_4_) {
      List<Template.BlockInfo> list = Lists.newArrayList();
      MutableBoundingBox mutableboundingbox = p_215386_2_.getBoundingBox();
      Iterator var7 = p_215386_2_.func_227459_a_(this.blocks, p_215386_1_).iterator();

      while(true) {
         Template.BlockInfo template$blockinfo;
         BlockPos blockpos;
         do {
            if (!var7.hasNext()) {
               return list;
            }

            template$blockinfo = (Template.BlockInfo)var7.next();
            blockpos = p_215386_4_ ? transformedBlockPos(p_215386_2_, template$blockinfo.pos).add(p_215386_1_) : template$blockinfo.pos;
         } while(mutableboundingbox != null && !mutableboundingbox.isVecInside(blockpos));

         BlockState blockstate = template$blockinfo.state;
         if (blockstate.getBlock() == p_215386_3_) {
            list.add(new Template.BlockInfo(blockpos, blockstate.rotate(p_215386_2_.getRotation()), template$blockinfo.nbt));
         }
      }
   }

   public BlockPos calculateConnectedPos(PlacementSettings p_186262_1_, BlockPos p_186262_2_, PlacementSettings p_186262_3_, BlockPos p_186262_4_) {
      BlockPos blockpos = transformedBlockPos(p_186262_1_, p_186262_2_);
      BlockPos blockpos1 = transformedBlockPos(p_186262_3_, p_186262_4_);
      return blockpos.subtract(blockpos1);
   }

   public static BlockPos transformedBlockPos(PlacementSettings p_186266_0_, BlockPos p_186266_1_) {
      return getTransformedPos(p_186266_1_, p_186266_0_.getMirror(), p_186266_0_.getRotation(), p_186266_0_.func_207664_d());
   }

   public static Vec3d transformedVec3d(PlacementSettings p_transformedVec3d_0_, Vec3d p_transformedVec3d_1_) {
      return getTransformedPos(p_transformedVec3d_1_, p_transformedVec3d_0_.getMirror(), p_transformedVec3d_0_.getRotation(), p_transformedVec3d_0_.func_207664_d());
   }

   public void addBlocksToWorldChunk(IWorld p_186260_1_, BlockPos p_186260_2_, PlacementSettings p_186260_3_) {
      p_186260_3_.setBoundingBoxFromChunk();
      this.addBlocksToWorld(p_186260_1_, p_186260_2_, p_186260_3_);
   }

   public void addBlocksToWorld(IWorld p_186253_1_, BlockPos p_186253_2_, PlacementSettings p_186253_3_) {
      this.addBlocksToWorld(p_186253_1_, p_186253_2_, p_186253_3_, 2);
   }

   public boolean addBlocksToWorld(IWorld p_189962_1_, BlockPos p_189962_2_, PlacementSettings p_189962_3_, int p_189962_4_) {
      if (this.blocks.isEmpty()) {
         return false;
      } else {
         List<Template.BlockInfo> list = p_189962_3_.func_227459_a_(this.blocks, p_189962_2_);
         if ((!list.isEmpty() || !p_189962_3_.getIgnoreEntities() && !this.entities.isEmpty()) && this.size.getX() >= 1 && this.size.getY() >= 1 && this.size.getZ() >= 1) {
            MutableBoundingBox mutableboundingbox = p_189962_3_.getBoundingBox();
            List<BlockPos> list1 = Lists.newArrayListWithCapacity(p_189962_3_.func_204763_l() ? list.size() : 0);
            List<Pair<BlockPos, CompoundNBT>> list2 = Lists.newArrayListWithCapacity(list.size());
            int i = Integer.MAX_VALUE;
            int j = Integer.MAX_VALUE;
            int k = Integer.MAX_VALUE;
            int l = Integer.MIN_VALUE;
            int i1 = Integer.MIN_VALUE;
            int j1 = Integer.MIN_VALUE;
            Iterator var15 = processBlockInfos(this, p_189962_1_, p_189962_2_, p_189962_3_, list).iterator();

            while(true) {
               Template.BlockInfo template$blockinfo;
               BlockPos blockpos;
               TileEntity tileentity2;
               do {
                  if (!var15.hasNext()) {
                     boolean flag = true;
                     Direction[] adirection = new Direction[]{Direction.UP, Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST};

                     Iterator iterator;
                     BlockPos blockpos4;
                     BlockState blockstate3;
                     while(flag && !list1.isEmpty()) {
                        flag = false;
                        iterator = list1.iterator();

                        while(iterator.hasNext()) {
                           BlockPos blockpos2 = (BlockPos)iterator.next();
                           blockpos4 = blockpos2;
                           IFluidState ifluidstate2 = p_189962_1_.getFluidState(blockpos2);

                           for(int k1 = 0; k1 < adirection.length && !ifluidstate2.isSource(); ++k1) {
                              BlockPos blockpos1 = blockpos4.offset(adirection[k1]);
                              IFluidState ifluidstate1 = p_189962_1_.getFluidState(blockpos1);
                              if (ifluidstate1.func_215679_a(p_189962_1_, blockpos1) > ifluidstate2.func_215679_a(p_189962_1_, blockpos4) || ifluidstate1.isSource() && !ifluidstate2.isSource()) {
                                 ifluidstate2 = ifluidstate1;
                                 blockpos4 = blockpos1;
                              }
                           }

                           if (ifluidstate2.isSource()) {
                              blockstate3 = p_189962_1_.getBlockState(blockpos2);
                              Block block = blockstate3.getBlock();
                              if (block instanceof ILiquidContainer) {
                                 ((ILiquidContainer)block).receiveFluid(p_189962_1_, blockpos2, blockstate3, ifluidstate2);
                                 flag = true;
                                 iterator.remove();
                              }
                           }
                        }
                     }

                     if (i <= l) {
                        if (!p_189962_3_.func_215218_i()) {
                           VoxelShapePart voxelshapepart = new BitSetVoxelShapePart(l - i + 1, i1 - j + 1, j1 - k + 1);
                           int l1 = i;
                           int i2 = j;
                           int j2 = k;
                           Iterator var37 = list2.iterator();

                           while(var37.hasNext()) {
                              Pair<BlockPos, CompoundNBT> pair1 = (Pair)var37.next();
                              BlockPos blockpos5 = (BlockPos)pair1.getFirst();
                              voxelshapepart.setFilled(blockpos5.getX() - l1, blockpos5.getY() - i2, blockpos5.getZ() - j2, true, true);
                           }

                           func_222857_a(p_189962_1_, p_189962_4_, voxelshapepart, l1, i2, j2);
                        }

                        iterator = list2.iterator();

                        while(iterator.hasNext()) {
                           Pair<BlockPos, CompoundNBT> pair = (Pair)iterator.next();
                           blockpos4 = (BlockPos)pair.getFirst();
                           if (!p_189962_3_.func_215218_i()) {
                              BlockState blockstate1 = p_189962_1_.getBlockState(blockpos4);
                              blockstate3 = Block.getValidBlockForPosition(blockstate1, p_189962_1_, blockpos4);
                              if (blockstate1 != blockstate3) {
                                 p_189962_1_.setBlockState(blockpos4, blockstate3, p_189962_4_ & -2 | 16);
                              }

                              p_189962_1_.notifyNeighbors(blockpos4, blockstate3.getBlock());
                           }

                           if (pair.getSecond() != null) {
                              tileentity2 = p_189962_1_.getTileEntity(blockpos4);
                              if (tileentity2 != null) {
                                 tileentity2.markDirty();
                              }
                           }
                        }
                     }

                     if (!p_189962_3_.getIgnoreEntities()) {
                        this.addEntitiesToWorld(p_189962_1_, p_189962_2_, p_189962_3_, p_189962_3_.getMirror(), p_189962_3_.getRotation(), p_189962_3_.func_207664_d(), p_189962_3_.getBoundingBox());
                     }

                     return true;
                  }

                  template$blockinfo = (Template.BlockInfo)var15.next();
                  blockpos = template$blockinfo.pos;
               } while(mutableboundingbox != null && !mutableboundingbox.isVecInside(blockpos));

               IFluidState ifluidstate = p_189962_3_.func_204763_l() ? p_189962_1_.getFluidState(blockpos) : null;
               BlockState blockstate = template$blockinfo.state.mirror(p_189962_3_.getMirror()).rotate(p_189962_3_.getRotation());
               if (template$blockinfo.nbt != null) {
                  tileentity2 = p_189962_1_.getTileEntity(blockpos);
                  IClearable.clearObj(tileentity2);
                  p_189962_1_.setBlockState(blockpos, Blocks.BARRIER.getDefaultState(), 20);
               }

               if (p_189962_1_.setBlockState(blockpos, blockstate, p_189962_4_)) {
                  i = Math.min(i, blockpos.getX());
                  j = Math.min(j, blockpos.getY());
                  k = Math.min(k, blockpos.getZ());
                  l = Math.max(l, blockpos.getX());
                  i1 = Math.max(i1, blockpos.getY());
                  j1 = Math.max(j1, blockpos.getZ());
                  list2.add(Pair.of(blockpos, template$blockinfo.nbt));
                  if (template$blockinfo.nbt != null) {
                     tileentity2 = p_189962_1_.getTileEntity(blockpos);
                     if (tileentity2 != null) {
                        template$blockinfo.nbt.putInt("x", blockpos.getX());
                        template$blockinfo.nbt.putInt("y", blockpos.getY());
                        template$blockinfo.nbt.putInt("z", blockpos.getZ());
                        tileentity2.read(template$blockinfo.nbt);
                        tileentity2.mirror(p_189962_3_.getMirror());
                        tileentity2.rotate(p_189962_3_.getRotation());
                     }
                  }

                  if (ifluidstate != null && blockstate.getBlock() instanceof ILiquidContainer) {
                     ((ILiquidContainer)blockstate.getBlock()).receiveFluid(p_189962_1_, blockpos, blockstate, ifluidstate);
                     if (!ifluidstate.isSource()) {
                        list1.add(blockpos);
                     }
                  }
               }
            }
         } else {
            return false;
         }
      }
   }

   public static void func_222857_a(IWorld p_222857_0_, int p_222857_1_, VoxelShapePart p_222857_2_, int p_222857_3_, int p_222857_4_, int p_222857_5_) {
      p_222857_2_.forEachFace((p_lambda$func_222857_a$1_5_, p_lambda$func_222857_a$1_6_, p_lambda$func_222857_a$1_7_, p_lambda$func_222857_a$1_8_) -> {
         BlockPos blockpos = new BlockPos(p_222857_3_ + p_lambda$func_222857_a$1_6_, p_222857_4_ + p_lambda$func_222857_a$1_7_, p_222857_5_ + p_lambda$func_222857_a$1_8_);
         BlockPos blockpos1 = blockpos.offset(p_lambda$func_222857_a$1_5_);
         BlockState blockstate = p_222857_0_.getBlockState(blockpos);
         BlockState blockstate1 = p_222857_0_.getBlockState(blockpos1);
         BlockState blockstate2 = blockstate.updatePostPlacement(p_lambda$func_222857_a$1_5_, blockstate1, p_222857_0_, blockpos, blockpos1);
         if (blockstate != blockstate2) {
            p_222857_0_.setBlockState(blockpos, blockstate2, p_222857_1_ & -2 | 16);
         }

         BlockState blockstate3 = blockstate1.updatePostPlacement(p_lambda$func_222857_a$1_5_.getOpposite(), blockstate2, p_222857_0_, blockpos1, blockpos);
         if (blockstate1 != blockstate3) {
            p_222857_0_.setBlockState(blockpos1, blockstate3, p_222857_1_ & -2 | 16);
         }

      });
   }

   /** @deprecated */
   @Deprecated
   public static List<Template.BlockInfo> func_215387_a(IWorld p_215387_0_, BlockPos p_215387_1_, PlacementSettings p_215387_2_, List<Template.BlockInfo> p_215387_3_) {
      return processBlockInfos((Template)null, p_215387_0_, p_215387_1_, p_215387_2_, p_215387_3_);
   }

   public static List<Template.BlockInfo> processBlockInfos(@Nullable Template p_processBlockInfos_0_, IWorld p_processBlockInfos_1_, BlockPos p_processBlockInfos_2_, PlacementSettings p_processBlockInfos_3_, List<Template.BlockInfo> p_processBlockInfos_4_) {
      List<Template.BlockInfo> list = Lists.newArrayList();
      Iterator var6 = p_processBlockInfos_4_.iterator();

      while(var6.hasNext()) {
         Template.BlockInfo template$blockinfo = (Template.BlockInfo)var6.next();
         BlockPos blockpos = transformedBlockPos(p_processBlockInfos_3_, template$blockinfo.pos).add(p_processBlockInfos_2_);
         Template.BlockInfo template$blockinfo1 = new Template.BlockInfo(blockpos, template$blockinfo.state, template$blockinfo.nbt);

         for(Iterator iterator = p_processBlockInfos_3_.getProcessors().iterator(); template$blockinfo1 != null && iterator.hasNext(); template$blockinfo1 = ((StructureProcessor)iterator.next()).process(p_processBlockInfos_1_, p_processBlockInfos_2_, template$blockinfo, template$blockinfo1, p_processBlockInfos_3_, p_processBlockInfos_0_)) {
         }

         if (template$blockinfo1 != null) {
            list.add(template$blockinfo1);
         }
      }

      return list;
   }

   public static List<Template.EntityInfo> processEntityInfos(@Nullable Template p_processEntityInfos_0_, IWorld p_processEntityInfos_1_, BlockPos p_processEntityInfos_2_, PlacementSettings p_processEntityInfos_3_, List<Template.EntityInfo> p_processEntityInfos_4_) {
      List<Template.EntityInfo> list = Lists.newArrayList();
      Iterator var6 = p_processEntityInfos_4_.iterator();

      while(var6.hasNext()) {
         Template.EntityInfo entityInfo = (Template.EntityInfo)var6.next();
         Vec3d pos = transformedVec3d(p_processEntityInfos_3_, entityInfo.pos).add(new Vec3d(p_processEntityInfos_2_));
         BlockPos blockpos = transformedBlockPos(p_processEntityInfos_3_, entityInfo.blockPos).add(p_processEntityInfos_2_);
         Template.EntityInfo info = new Template.EntityInfo(pos, blockpos, entityInfo.nbt);
         Iterator var11 = p_processEntityInfos_3_.getProcessors().iterator();

         while(var11.hasNext()) {
            StructureProcessor proc = (StructureProcessor)var11.next();
            info = proc.processEntity(p_processEntityInfos_1_, p_processEntityInfos_2_, entityInfo, info, p_processEntityInfos_3_, p_processEntityInfos_0_);
            if (info == null) {
               break;
            }
         }

         if (info != null) {
            list.add(info);
         }
      }

      return list;
   }

   /** @deprecated */
   @Deprecated
   private void func_207668_a(IWorld p_207668_1_, BlockPos p_207668_2_, Mirror p_207668_3_, Rotation p_207668_4_, BlockPos p_207668_5_, @Nullable MutableBoundingBox p_207668_6_) {
      this.addEntitiesToWorld(p_207668_1_, p_207668_2_, (new PlacementSettings()).setMirror(p_207668_3_).setRotation(p_207668_4_).setCenterOffset(p_207668_5_).setBoundingBox(p_207668_6_), p_207668_3_, p_207668_4_, p_207668_2_, p_207668_6_);
   }

   private void addEntitiesToWorld(IWorld p_addEntitiesToWorld_1_, BlockPos p_addEntitiesToWorld_2_, PlacementSettings p_addEntitiesToWorld_3_, Mirror p_addEntitiesToWorld_4_, Rotation p_addEntitiesToWorld_5_, BlockPos p_addEntitiesToWorld_6_, @Nullable MutableBoundingBox p_addEntitiesToWorld_7_) {
      Iterator var8 = processEntityInfos(this, p_addEntitiesToWorld_1_, p_addEntitiesToWorld_2_, p_addEntitiesToWorld_3_, this.entities).iterator();

      while(true) {
         Template.EntityInfo template$entityinfo;
         BlockPos blockpos;
         do {
            if (!var8.hasNext()) {
               return;
            }

            template$entityinfo = (Template.EntityInfo)var8.next();
            blockpos = getTransformedPos(template$entityinfo.blockPos, p_addEntitiesToWorld_4_, p_addEntitiesToWorld_5_, p_addEntitiesToWorld_6_).add(p_addEntitiesToWorld_2_);
            blockpos = template$entityinfo.blockPos;
         } while(p_addEntitiesToWorld_7_ != null && !p_addEntitiesToWorld_7_.isVecInside(blockpos));

         CompoundNBT compoundnbt = template$entityinfo.nbt;
         Vec3d vec3d = getTransformedPos(template$entityinfo.pos, p_addEntitiesToWorld_4_, p_addEntitiesToWorld_5_, p_addEntitiesToWorld_6_);
         vec3d.add((double)p_addEntitiesToWorld_2_.getX(), (double)p_addEntitiesToWorld_2_.getY(), (double)p_addEntitiesToWorld_2_.getZ());
         Vec3d vec3d1 = template$entityinfo.pos;
         ListNBT listnbt = new ListNBT();
         listnbt.add(DoubleNBT.func_229684_a_(vec3d1.x));
         listnbt.add(DoubleNBT.func_229684_a_(vec3d1.y));
         listnbt.add(DoubleNBT.func_229684_a_(vec3d1.z));
         compoundnbt.put("Pos", listnbt);
         compoundnbt.remove("UUIDMost");
         compoundnbt.remove("UUIDLeast");
         func_215382_a(p_addEntitiesToWorld_1_, compoundnbt).ifPresent((p_lambda$addEntitiesToWorld$2_4_) -> {
            float f = p_lambda$addEntitiesToWorld$2_4_.getMirroredYaw(p_addEntitiesToWorld_4_);
            f += p_lambda$addEntitiesToWorld$2_4_.rotationYaw - p_lambda$addEntitiesToWorld$2_4_.getRotatedYaw(p_addEntitiesToWorld_5_);
            p_lambda$addEntitiesToWorld$2_4_.setLocationAndAngles(vec3d1.x, vec3d1.y, vec3d1.z, f, p_lambda$addEntitiesToWorld$2_4_.rotationPitch);
            p_addEntitiesToWorld_1_.addEntity(p_lambda$addEntitiesToWorld$2_4_);
         });
      }
   }

   private static Optional<Entity> func_215382_a(IWorld p_215382_0_, CompoundNBT p_215382_1_) {
      try {
         return EntityType.loadEntityUnchecked(p_215382_1_, p_215382_0_.getWorld());
      } catch (Exception var3) {
         return Optional.empty();
      }
   }

   public BlockPos transformedSize(Rotation p_186257_1_) {
      switch(p_186257_1_) {
      case COUNTERCLOCKWISE_90:
      case CLOCKWISE_90:
         return new BlockPos(this.size.getZ(), this.size.getY(), this.size.getX());
      default:
         return this.size;
      }
   }

   public static BlockPos getTransformedPos(BlockPos p_207669_0_, Mirror p_207669_1_, Rotation p_207669_2_, BlockPos p_207669_3_) {
      int i = p_207669_0_.getX();
      int j = p_207669_0_.getY();
      int k = p_207669_0_.getZ();
      boolean flag = true;
      switch(p_207669_1_) {
      case LEFT_RIGHT:
         k = -k;
         break;
      case FRONT_BACK:
         i = -i;
         break;
      default:
         flag = false;
      }

      int l = p_207669_3_.getX();
      int i1 = p_207669_3_.getZ();
      switch(p_207669_2_) {
      case COUNTERCLOCKWISE_90:
         return new BlockPos(l - i1 + k, j, l + i1 - i);
      case CLOCKWISE_90:
         return new BlockPos(l + i1 - k, j, i1 - l + i);
      case CLOCKWISE_180:
         return new BlockPos(l + l - i, j, i1 + i1 - k);
      default:
         return flag ? new BlockPos(i, j, k) : p_207669_0_;
      }
   }

   private static Vec3d getTransformedPos(Vec3d p_207667_0_, Mirror p_207667_1_, Rotation p_207667_2_, BlockPos p_207667_3_) {
      double d0 = p_207667_0_.x;
      double d1 = p_207667_0_.y;
      double d2 = p_207667_0_.z;
      boolean flag = true;
      switch(p_207667_1_) {
      case LEFT_RIGHT:
         d2 = 1.0D - d2;
         break;
      case FRONT_BACK:
         d0 = 1.0D - d0;
         break;
      default:
         flag = false;
      }

      int i = p_207667_3_.getX();
      int j = p_207667_3_.getZ();
      switch(p_207667_2_) {
      case COUNTERCLOCKWISE_90:
         return new Vec3d((double)(i - j) + d2, d1, (double)(i + j + 1) - d0);
      case CLOCKWISE_90:
         return new Vec3d((double)(i + j + 1) - d2, d1, (double)(j - i) + d0);
      case CLOCKWISE_180:
         return new Vec3d((double)(i + i + 1) - d0, d1, (double)(j + j + 1) - d2);
      default:
         return flag ? new Vec3d(d0, d1, d2) : p_207667_0_;
      }
   }

   public BlockPos getZeroPositionWithTransform(BlockPos p_189961_1_, Mirror p_189961_2_, Rotation p_189961_3_) {
      return getZeroPositionWithTransform(p_189961_1_, p_189961_2_, p_189961_3_, this.getSize().getX(), this.getSize().getZ());
   }

   public static BlockPos getZeroPositionWithTransform(BlockPos p_191157_0_, Mirror p_191157_1_, Rotation p_191157_2_, int p_191157_3_, int p_191157_4_) {
      --p_191157_3_;
      --p_191157_4_;
      int i = p_191157_1_ == Mirror.FRONT_BACK ? p_191157_3_ : 0;
      int j = p_191157_1_ == Mirror.LEFT_RIGHT ? p_191157_4_ : 0;
      BlockPos blockpos = p_191157_0_;
      switch(p_191157_2_) {
      case COUNTERCLOCKWISE_90:
         blockpos = p_191157_0_.add(j, 0, p_191157_3_ - i);
         break;
      case CLOCKWISE_90:
         blockpos = p_191157_0_.add(p_191157_4_ - j, 0, i);
         break;
      case CLOCKWISE_180:
         blockpos = p_191157_0_.add(p_191157_3_ - i, 0, p_191157_4_ - j);
         break;
      case NONE:
         blockpos = p_191157_0_.add(i, 0, j);
      }

      return blockpos;
   }

   public MutableBoundingBox func_215388_b(PlacementSettings p_215388_1_, BlockPos p_215388_2_) {
      Rotation rotation = p_215388_1_.getRotation();
      BlockPos blockpos = p_215388_1_.func_207664_d();
      BlockPos blockpos1 = this.transformedSize(rotation);
      Mirror mirror = p_215388_1_.getMirror();
      int i = blockpos.getX();
      int j = blockpos.getZ();
      int k = blockpos1.getX() - 1;
      int l = blockpos1.getY() - 1;
      int i1 = blockpos1.getZ() - 1;
      MutableBoundingBox mutableboundingbox = new MutableBoundingBox(0, 0, 0, 0, 0, 0);
      switch(rotation) {
      case COUNTERCLOCKWISE_90:
         mutableboundingbox = new MutableBoundingBox(i - j, 0, i + j - i1, i - j + k, l, i + j);
         break;
      case CLOCKWISE_90:
         mutableboundingbox = new MutableBoundingBox(i + j - k, 0, j - i, i + j, l, j - i + i1);
         break;
      case CLOCKWISE_180:
         mutableboundingbox = new MutableBoundingBox(i + i - k, 0, j + j - i1, i + i, l, j + j);
         break;
      case NONE:
         mutableboundingbox = new MutableBoundingBox(0, 0, 0, k, l, i1);
      }

      switch(mirror) {
      case LEFT_RIGHT:
         this.func_215385_a(rotation, i1, k, mutableboundingbox, Direction.NORTH, Direction.SOUTH);
         break;
      case FRONT_BACK:
         this.func_215385_a(rotation, k, i1, mutableboundingbox, Direction.WEST, Direction.EAST);
      case NONE:
      }

      mutableboundingbox.offset(p_215388_2_.getX(), p_215388_2_.getY(), p_215388_2_.getZ());
      return mutableboundingbox;
   }

   private void func_215385_a(Rotation p_215385_1_, int p_215385_2_, int p_215385_3_, MutableBoundingBox p_215385_4_, Direction p_215385_5_, Direction p_215385_6_) {
      BlockPos blockpos = BlockPos.ZERO;
      if (p_215385_1_ != Rotation.CLOCKWISE_90 && p_215385_1_ != Rotation.COUNTERCLOCKWISE_90) {
         if (p_215385_1_ == Rotation.CLOCKWISE_180) {
            blockpos = blockpos.offset(p_215385_6_, p_215385_2_);
         } else {
            blockpos = blockpos.offset(p_215385_5_, p_215385_2_);
         }
      } else {
         blockpos = blockpos.offset(p_215385_1_.rotate(p_215385_5_), p_215385_3_);
      }

      p_215385_4_.offset(blockpos.getX(), 0, blockpos.getZ());
   }

   public CompoundNBT writeToNBT(CompoundNBT p_189552_1_) {
      if (this.blocks.isEmpty()) {
         p_189552_1_.put("blocks", new ListNBT());
         p_189552_1_.put("palette", new ListNBT());
      } else {
         List<Template.BasicPalette> list = Lists.newArrayList();
         Template.BasicPalette template$basicpalette = new Template.BasicPalette();
         list.add(template$basicpalette);

         for(int i = 1; i < this.blocks.size(); ++i) {
            list.add(new Template.BasicPalette());
         }

         ListNBT listnbt1 = new ListNBT();
         List<Template.BlockInfo> list1 = (List)this.blocks.get(0);

         for(int j = 0; j < list1.size(); ++j) {
            Template.BlockInfo template$blockinfo = (Template.BlockInfo)list1.get(j);
            CompoundNBT compoundnbt = new CompoundNBT();
            compoundnbt.put("pos", this.writeInts(template$blockinfo.pos.getX(), template$blockinfo.pos.getY(), template$blockinfo.pos.getZ()));
            int k = template$basicpalette.idFor(template$blockinfo.state);
            compoundnbt.putInt("state", k);
            if (template$blockinfo.nbt != null) {
               compoundnbt.put("nbt", template$blockinfo.nbt);
            }

            listnbt1.add(compoundnbt);

            for(int l = 1; l < this.blocks.size(); ++l) {
               Template.BasicPalette template$basicpalette1 = (Template.BasicPalette)list.get(l);
               template$basicpalette1.addMapping(((Template.BlockInfo)((List)this.blocks.get(l)).get(j)).state, k);
            }
         }

         p_189552_1_.put("blocks", listnbt1);
         ListNBT listnbt3;
         Iterator var18;
         if (list.size() == 1) {
            listnbt3 = new ListNBT();
            var18 = template$basicpalette.iterator();

            while(var18.hasNext()) {
               BlockState blockstate = (BlockState)var18.next();
               listnbt3.add(NBTUtil.writeBlockState(blockstate));
            }

            p_189552_1_.put("palette", listnbt3);
         } else {
            listnbt3 = new ListNBT();
            var18 = list.iterator();

            while(var18.hasNext()) {
               Template.BasicPalette template$basicpalette2 = (Template.BasicPalette)var18.next();
               ListNBT listnbt4 = new ListNBT();
               Iterator var22 = template$basicpalette2.iterator();

               while(var22.hasNext()) {
                  BlockState blockstate1 = (BlockState)var22.next();
                  listnbt4.add(NBTUtil.writeBlockState(blockstate1));
               }

               listnbt3.add(listnbt4);
            }

            p_189552_1_.put("palettes", listnbt3);
         }
      }

      ListNBT listnbt = new ListNBT();

      CompoundNBT compoundnbt1;
      for(Iterator var13 = this.entities.iterator(); var13.hasNext(); listnbt.add(compoundnbt1)) {
         Template.EntityInfo template$entityinfo = (Template.EntityInfo)var13.next();
         compoundnbt1 = new CompoundNBT();
         compoundnbt1.put("pos", this.writeDoubles(template$entityinfo.pos.x, template$entityinfo.pos.y, template$entityinfo.pos.z));
         compoundnbt1.put("blockPos", this.writeInts(template$entityinfo.blockPos.getX(), template$entityinfo.blockPos.getY(), template$entityinfo.blockPos.getZ()));
         if (template$entityinfo.nbt != null) {
            compoundnbt1.put("nbt", template$entityinfo.nbt);
         }
      }

      p_189552_1_.put("entities", listnbt);
      p_189552_1_.put("size", this.writeInts(this.size.getX(), this.size.getY(), this.size.getZ()));
      p_189552_1_.putInt("DataVersion", SharedConstants.getVersion().getWorldVersion());
      return p_189552_1_;
   }

   public void read(CompoundNBT p_186256_1_) {
      this.blocks.clear();
      this.entities.clear();
      ListNBT listnbt = p_186256_1_.getList("size", 3);
      this.size = new BlockPos(listnbt.getInt(0), listnbt.getInt(1), listnbt.getInt(2));
      ListNBT listnbt1 = p_186256_1_.getList("blocks", 10);
      ListNBT listnbt5;
      int j;
      if (p_186256_1_.contains("palettes", 9)) {
         listnbt5 = p_186256_1_.getList("palettes", 9);

         for(j = 0; j < listnbt5.size(); ++j) {
            this.func_204768_a(listnbt5.getList(j), listnbt1);
         }
      } else {
         this.func_204768_a(p_186256_1_.getList("palette", 10), listnbt1);
      }

      listnbt5 = p_186256_1_.getList("entities", 10);

      for(j = 0; j < listnbt5.size(); ++j) {
         CompoundNBT compoundnbt = listnbt5.getCompound(j);
         ListNBT listnbt3 = compoundnbt.getList("pos", 6);
         Vec3d vec3d = new Vec3d(listnbt3.getDouble(0), listnbt3.getDouble(1), listnbt3.getDouble(2));
         ListNBT listnbt4 = compoundnbt.getList("blockPos", 3);
         BlockPos blockpos = new BlockPos(listnbt4.getInt(0), listnbt4.getInt(1), listnbt4.getInt(2));
         if (compoundnbt.contains("nbt")) {
            CompoundNBT compoundnbt1 = compoundnbt.getCompound("nbt");
            this.entities.add(new Template.EntityInfo(vec3d, blockpos, compoundnbt1));
         }
      }

   }

   private void func_204768_a(ListNBT p_204768_1_, ListNBT p_204768_2_) {
      Template.BasicPalette template$basicpalette = new Template.BasicPalette();
      List<Template.BlockInfo> list = Lists.newArrayList();

      int j;
      for(j = 0; j < p_204768_1_.size(); ++j) {
         template$basicpalette.addMapping(NBTUtil.readBlockState(p_204768_1_.getCompound(j)), j);
      }

      for(j = 0; j < p_204768_2_.size(); ++j) {
         CompoundNBT compoundnbt = p_204768_2_.getCompound(j);
         ListNBT listnbt = compoundnbt.getList("pos", 3);
         BlockPos blockpos = new BlockPos(listnbt.getInt(0), listnbt.getInt(1), listnbt.getInt(2));
         BlockState blockstate = template$basicpalette.stateFor(compoundnbt.getInt("state"));
         CompoundNBT compoundnbt1;
         if (compoundnbt.contains("nbt")) {
            compoundnbt1 = compoundnbt.getCompound("nbt");
         } else {
            compoundnbt1 = null;
         }

         list.add(new Template.BlockInfo(blockpos, blockstate, compoundnbt1));
      }

      list.sort(Comparator.comparingInt((p_lambda$func_204768_a$3_0_) -> {
         return p_lambda$func_204768_a$3_0_.pos.getY();
      }));
      this.blocks.add(list);
   }

   private ListNBT writeInts(int... p_186267_1_) {
      ListNBT listnbt = new ListNBT();
      int[] var3 = p_186267_1_;
      int var4 = p_186267_1_.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         int i = var3[var5];
         listnbt.add(IntNBT.func_229692_a_(i));
      }

      return listnbt;
   }

   private ListNBT writeDoubles(double... p_186264_1_) {
      ListNBT listnbt = new ListNBT();
      double[] var3 = p_186264_1_;
      int var4 = p_186264_1_.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         double d0 = var3[var5];
         listnbt.add(DoubleNBT.func_229684_a_(d0));
      }

      return listnbt;
   }

   public static class EntityInfo {
      public final Vec3d pos;
      public final BlockPos blockPos;
      public final CompoundNBT nbt;

      public EntityInfo(Vec3d p_i47101_1_, BlockPos p_i47101_2_, CompoundNBT p_i47101_3_) {
         this.pos = p_i47101_1_;
         this.blockPos = p_i47101_2_;
         this.nbt = p_i47101_3_;
      }
   }

   public static class BlockInfo {
      public final BlockPos pos;
      public final BlockState state;
      public final CompoundNBT nbt;

      public BlockInfo(BlockPos p_i47042_1_, BlockState p_i47042_2_, @Nullable CompoundNBT p_i47042_3_) {
         this.pos = p_i47042_1_;
         this.state = p_i47042_2_;
         this.nbt = p_i47042_3_;
      }

      public String toString() {
         return String.format("<StructureBlockInfo | %s | %s | %s>", this.pos, this.state, this.nbt);
      }
   }

   static class BasicPalette implements Iterable<BlockState> {
      public static final BlockState DEFAULT_BLOCK_STATE;
      private final ObjectIntIdentityMap<BlockState> ids;
      private int lastId;

      private BasicPalette() {
         this.ids = new ObjectIntIdentityMap(16);
      }

      public int idFor(BlockState p_189954_1_) {
         int i = this.ids.get(p_189954_1_);
         if (i == -1) {
            i = this.lastId++;
            this.ids.put(p_189954_1_, i);
         }

         return i;
      }

      @Nullable
      public BlockState stateFor(int p_189955_1_) {
         BlockState blockstate = (BlockState)this.ids.getByValue(p_189955_1_);
         return blockstate == null ? DEFAULT_BLOCK_STATE : blockstate;
      }

      public Iterator<BlockState> iterator() {
         return this.ids.iterator();
      }

      public void addMapping(BlockState p_189956_1_, int p_189956_2_) {
         this.ids.put(p_189956_1_, p_189956_2_);
      }

      // $FF: synthetic method
      BasicPalette(Object p_i47158_1_) {
         this();
      }

      static {
         DEFAULT_BLOCK_STATE = Blocks.AIR.getDefaultState();
      }
   }
}
