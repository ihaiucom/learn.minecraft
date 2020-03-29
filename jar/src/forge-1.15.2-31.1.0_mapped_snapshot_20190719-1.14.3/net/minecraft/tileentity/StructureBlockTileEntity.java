package net.minecraft.tileentity;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.StructureBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.state.properties.StructureMode;
import net.minecraft.util.Mirror;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.ResourceLocationException;
import net.minecraft.util.Rotation;
import net.minecraft.util.StringUtils;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.gen.feature.template.IntegrityProcessor;
import net.minecraft.world.gen.feature.template.PlacementSettings;
import net.minecraft.world.gen.feature.template.Template;
import net.minecraft.world.gen.feature.template.TemplateManager;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class StructureBlockTileEntity extends TileEntity {
   private ResourceLocation name;
   private String author = "";
   private String metadata = "";
   private BlockPos position = new BlockPos(0, 1, 0);
   private BlockPos size;
   private Mirror mirror;
   private Rotation rotation;
   private StructureMode mode;
   private boolean ignoreEntities;
   private boolean powered;
   private boolean showAir;
   private boolean showBoundingBox;
   private float integrity;
   private long seed;

   public StructureBlockTileEntity() {
      super(TileEntityType.STRUCTURE_BLOCK);
      this.size = BlockPos.ZERO;
      this.mirror = Mirror.NONE;
      this.rotation = Rotation.NONE;
      this.mode = StructureMode.DATA;
      this.ignoreEntities = true;
      this.showBoundingBox = true;
      this.integrity = 1.0F;
   }

   public CompoundNBT write(CompoundNBT p_189515_1_) {
      super.write(p_189515_1_);
      p_189515_1_.putString("name", this.getName());
      p_189515_1_.putString("author", this.author);
      p_189515_1_.putString("metadata", this.metadata);
      p_189515_1_.putInt("posX", this.position.getX());
      p_189515_1_.putInt("posY", this.position.getY());
      p_189515_1_.putInt("posZ", this.position.getZ());
      p_189515_1_.putInt("sizeX", this.size.getX());
      p_189515_1_.putInt("sizeY", this.size.getY());
      p_189515_1_.putInt("sizeZ", this.size.getZ());
      p_189515_1_.putString("rotation", this.rotation.toString());
      p_189515_1_.putString("mirror", this.mirror.toString());
      p_189515_1_.putString("mode", this.mode.toString());
      p_189515_1_.putBoolean("ignoreEntities", this.ignoreEntities);
      p_189515_1_.putBoolean("powered", this.powered);
      p_189515_1_.putBoolean("showair", this.showAir);
      p_189515_1_.putBoolean("showboundingbox", this.showBoundingBox);
      p_189515_1_.putFloat("integrity", this.integrity);
      p_189515_1_.putLong("seed", this.seed);
      return p_189515_1_;
   }

   public void read(CompoundNBT p_145839_1_) {
      super.read(p_145839_1_);
      this.setName(p_145839_1_.getString("name"));
      this.author = p_145839_1_.getString("author");
      this.metadata = p_145839_1_.getString("metadata");
      int lvt_2_1_ = MathHelper.clamp(p_145839_1_.getInt("posX"), -32, 32);
      int lvt_3_1_ = MathHelper.clamp(p_145839_1_.getInt("posY"), -32, 32);
      int lvt_4_1_ = MathHelper.clamp(p_145839_1_.getInt("posZ"), -32, 32);
      this.position = new BlockPos(lvt_2_1_, lvt_3_1_, lvt_4_1_);
      int lvt_5_1_ = MathHelper.clamp(p_145839_1_.getInt("sizeX"), 0, 32);
      int lvt_6_1_ = MathHelper.clamp(p_145839_1_.getInt("sizeY"), 0, 32);
      int lvt_7_1_ = MathHelper.clamp(p_145839_1_.getInt("sizeZ"), 0, 32);
      this.size = new BlockPos(lvt_5_1_, lvt_6_1_, lvt_7_1_);

      try {
         this.rotation = Rotation.valueOf(p_145839_1_.getString("rotation"));
      } catch (IllegalArgumentException var11) {
         this.rotation = Rotation.NONE;
      }

      try {
         this.mirror = Mirror.valueOf(p_145839_1_.getString("mirror"));
      } catch (IllegalArgumentException var10) {
         this.mirror = Mirror.NONE;
      }

      try {
         this.mode = StructureMode.valueOf(p_145839_1_.getString("mode"));
      } catch (IllegalArgumentException var9) {
         this.mode = StructureMode.DATA;
      }

      this.ignoreEntities = p_145839_1_.getBoolean("ignoreEntities");
      this.powered = p_145839_1_.getBoolean("powered");
      this.showAir = p_145839_1_.getBoolean("showair");
      this.showBoundingBox = p_145839_1_.getBoolean("showboundingbox");
      if (p_145839_1_.contains("integrity")) {
         this.integrity = p_145839_1_.getFloat("integrity");
      } else {
         this.integrity = 1.0F;
      }

      this.seed = p_145839_1_.getLong("seed");
      this.updateBlockState();
   }

   private void updateBlockState() {
      if (this.world != null) {
         BlockPos lvt_1_1_ = this.getPos();
         BlockState lvt_2_1_ = this.world.getBlockState(lvt_1_1_);
         if (lvt_2_1_.getBlock() == Blocks.STRUCTURE_BLOCK) {
            this.world.setBlockState(lvt_1_1_, (BlockState)lvt_2_1_.with(StructureBlock.MODE, this.mode), 2);
         }

      }
   }

   @Nullable
   public SUpdateTileEntityPacket getUpdatePacket() {
      return new SUpdateTileEntityPacket(this.pos, 7, this.getUpdateTag());
   }

   public CompoundNBT getUpdateTag() {
      return this.write(new CompoundNBT());
   }

   public boolean usedBy(PlayerEntity p_189701_1_) {
      if (!p_189701_1_.canUseCommandBlock()) {
         return false;
      } else {
         if (p_189701_1_.getEntityWorld().isRemote) {
            p_189701_1_.openStructureBlock(this);
         }

         return true;
      }
   }

   public String getName() {
      return this.name == null ? "" : this.name.toString();
   }

   public String func_227014_f_() {
      return this.name == null ? "" : this.name.getPath();
   }

   public boolean hasName() {
      return this.name != null;
   }

   public void setName(@Nullable String p_184404_1_) {
      this.setName(StringUtils.isNullOrEmpty(p_184404_1_) ? null : ResourceLocation.tryCreate(p_184404_1_));
   }

   public void setName(@Nullable ResourceLocation p_210163_1_) {
      this.name = p_210163_1_;
   }

   public void createdBy(LivingEntity p_189720_1_) {
      this.author = p_189720_1_.getName().getString();
   }

   public BlockPos getPosition() {
      return this.position;
   }

   public void setPosition(BlockPos p_184414_1_) {
      this.position = p_184414_1_;
   }

   public BlockPos getStructureSize() {
      return this.size;
   }

   public void setSize(BlockPos p_184409_1_) {
      this.size = p_184409_1_;
   }

   @OnlyIn(Dist.CLIENT)
   public Mirror getMirror() {
      return this.mirror;
   }

   public void setMirror(Mirror p_184411_1_) {
      this.mirror = p_184411_1_;
   }

   @OnlyIn(Dist.CLIENT)
   public Rotation getRotation() {
      return this.rotation;
   }

   public void setRotation(Rotation p_184408_1_) {
      this.rotation = p_184408_1_;
   }

   @OnlyIn(Dist.CLIENT)
   public String getMetadata() {
      return this.metadata;
   }

   public void setMetadata(String p_184410_1_) {
      this.metadata = p_184410_1_;
   }

   public StructureMode getMode() {
      return this.mode;
   }

   public void setMode(StructureMode p_184405_1_) {
      this.mode = p_184405_1_;
      BlockState lvt_2_1_ = this.world.getBlockState(this.getPos());
      if (lvt_2_1_.getBlock() == Blocks.STRUCTURE_BLOCK) {
         this.world.setBlockState(this.getPos(), (BlockState)lvt_2_1_.with(StructureBlock.MODE, p_184405_1_), 2);
      }

   }

   @OnlyIn(Dist.CLIENT)
   public void nextMode() {
      switch(this.getMode()) {
      case SAVE:
         this.setMode(StructureMode.LOAD);
         break;
      case LOAD:
         this.setMode(StructureMode.CORNER);
         break;
      case CORNER:
         this.setMode(StructureMode.DATA);
         break;
      case DATA:
         this.setMode(StructureMode.SAVE);
      }

   }

   @OnlyIn(Dist.CLIENT)
   public boolean ignoresEntities() {
      return this.ignoreEntities;
   }

   public void setIgnoresEntities(boolean p_184406_1_) {
      this.ignoreEntities = p_184406_1_;
   }

   @OnlyIn(Dist.CLIENT)
   public float getIntegrity() {
      return this.integrity;
   }

   public void setIntegrity(float p_189718_1_) {
      this.integrity = p_189718_1_;
   }

   @OnlyIn(Dist.CLIENT)
   public long getSeed() {
      return this.seed;
   }

   public void setSeed(long p_189725_1_) {
      this.seed = p_189725_1_;
   }

   public boolean detectSize() {
      if (this.mode != StructureMode.SAVE) {
         return false;
      } else {
         BlockPos lvt_1_1_ = this.getPos();
         int lvt_2_1_ = true;
         BlockPos lvt_3_1_ = new BlockPos(lvt_1_1_.getX() - 80, 0, lvt_1_1_.getZ() - 80);
         BlockPos lvt_4_1_ = new BlockPos(lvt_1_1_.getX() + 80, 255, lvt_1_1_.getZ() + 80);
         List<StructureBlockTileEntity> lvt_5_1_ = this.getNearbyCornerBlocks(lvt_3_1_, lvt_4_1_);
         List<StructureBlockTileEntity> lvt_6_1_ = this.filterRelatedCornerBlocks(lvt_5_1_);
         if (lvt_6_1_.size() < 1) {
            return false;
         } else {
            MutableBoundingBox lvt_7_1_ = this.calculateEnclosingBoundingBox(lvt_1_1_, lvt_6_1_);
            if (lvt_7_1_.maxX - lvt_7_1_.minX > 1 && lvt_7_1_.maxY - lvt_7_1_.minY > 1 && lvt_7_1_.maxZ - lvt_7_1_.minZ > 1) {
               this.position = new BlockPos(lvt_7_1_.minX - lvt_1_1_.getX() + 1, lvt_7_1_.minY - lvt_1_1_.getY() + 1, lvt_7_1_.minZ - lvt_1_1_.getZ() + 1);
               this.size = new BlockPos(lvt_7_1_.maxX - lvt_7_1_.minX - 1, lvt_7_1_.maxY - lvt_7_1_.minY - 1, lvt_7_1_.maxZ - lvt_7_1_.minZ - 1);
               this.markDirty();
               BlockState lvt_8_1_ = this.world.getBlockState(lvt_1_1_);
               this.world.notifyBlockUpdate(lvt_1_1_, lvt_8_1_, lvt_8_1_, 3);
               return true;
            } else {
               return false;
            }
         }
      }
   }

   private List<StructureBlockTileEntity> filterRelatedCornerBlocks(List<StructureBlockTileEntity> p_184415_1_) {
      Predicate<StructureBlockTileEntity> lvt_2_1_ = (p_200665_1_) -> {
         return p_200665_1_.mode == StructureMode.CORNER && Objects.equals(this.name, p_200665_1_.name);
      };
      return (List)p_184415_1_.stream().filter(lvt_2_1_).collect(Collectors.toList());
   }

   private List<StructureBlockTileEntity> getNearbyCornerBlocks(BlockPos p_184418_1_, BlockPos p_184418_2_) {
      List<StructureBlockTileEntity> lvt_3_1_ = Lists.newArrayList();
      Iterator var4 = BlockPos.getAllInBoxMutable(p_184418_1_, p_184418_2_).iterator();

      while(var4.hasNext()) {
         BlockPos lvt_5_1_ = (BlockPos)var4.next();
         BlockState lvt_6_1_ = this.world.getBlockState(lvt_5_1_);
         if (lvt_6_1_.getBlock() == Blocks.STRUCTURE_BLOCK) {
            TileEntity lvt_7_1_ = this.world.getTileEntity(lvt_5_1_);
            if (lvt_7_1_ != null && lvt_7_1_ instanceof StructureBlockTileEntity) {
               lvt_3_1_.add((StructureBlockTileEntity)lvt_7_1_);
            }
         }
      }

      return lvt_3_1_;
   }

   private MutableBoundingBox calculateEnclosingBoundingBox(BlockPos p_184416_1_, List<StructureBlockTileEntity> p_184416_2_) {
      MutableBoundingBox lvt_3_2_;
      if (p_184416_2_.size() > 1) {
         BlockPos lvt_4_1_ = ((StructureBlockTileEntity)p_184416_2_.get(0)).getPos();
         lvt_3_2_ = new MutableBoundingBox(lvt_4_1_, lvt_4_1_);
      } else {
         lvt_3_2_ = new MutableBoundingBox(p_184416_1_, p_184416_1_);
      }

      Iterator var7 = p_184416_2_.iterator();

      while(var7.hasNext()) {
         StructureBlockTileEntity lvt_5_1_ = (StructureBlockTileEntity)var7.next();
         BlockPos lvt_6_1_ = lvt_5_1_.getPos();
         if (lvt_6_1_.getX() < lvt_3_2_.minX) {
            lvt_3_2_.minX = lvt_6_1_.getX();
         } else if (lvt_6_1_.getX() > lvt_3_2_.maxX) {
            lvt_3_2_.maxX = lvt_6_1_.getX();
         }

         if (lvt_6_1_.getY() < lvt_3_2_.minY) {
            lvt_3_2_.minY = lvt_6_1_.getY();
         } else if (lvt_6_1_.getY() > lvt_3_2_.maxY) {
            lvt_3_2_.maxY = lvt_6_1_.getY();
         }

         if (lvt_6_1_.getZ() < lvt_3_2_.minZ) {
            lvt_3_2_.minZ = lvt_6_1_.getZ();
         } else if (lvt_6_1_.getZ() > lvt_3_2_.maxZ) {
            lvt_3_2_.maxZ = lvt_6_1_.getZ();
         }
      }

      return lvt_3_2_;
   }

   public boolean save() {
      return this.save(true);
   }

   public boolean save(boolean p_189712_1_) {
      if (this.mode == StructureMode.SAVE && !this.world.isRemote && this.name != null) {
         BlockPos lvt_2_1_ = this.getPos().add(this.position);
         ServerWorld lvt_3_1_ = (ServerWorld)this.world;
         TemplateManager lvt_4_1_ = lvt_3_1_.getStructureTemplateManager();

         Template lvt_5_2_;
         try {
            lvt_5_2_ = lvt_4_1_.getTemplateDefaulted(this.name);
         } catch (ResourceLocationException var8) {
            return false;
         }

         lvt_5_2_.takeBlocksFromWorld(this.world, lvt_2_1_, this.size, !this.ignoreEntities, Blocks.STRUCTURE_VOID);
         lvt_5_2_.setAuthor(this.author);
         if (p_189712_1_) {
            try {
               return lvt_4_1_.writeToFile(this.name);
            } catch (ResourceLocationException var7) {
               return false;
            }
         } else {
            return true;
         }
      } else {
         return false;
      }
   }

   public boolean load() {
      return this.load(true);
   }

   private static Random func_214074_b(long p_214074_0_) {
      return p_214074_0_ == 0L ? new Random(Util.milliTime()) : new Random(p_214074_0_);
   }

   public boolean load(boolean p_189714_1_) {
      if (this.mode == StructureMode.LOAD && !this.world.isRemote && this.name != null) {
         ServerWorld lvt_2_1_ = (ServerWorld)this.world;
         TemplateManager lvt_3_1_ = lvt_2_1_.getStructureTemplateManager();

         Template lvt_4_2_;
         try {
            lvt_4_2_ = lvt_3_1_.getTemplate(this.name);
         } catch (ResourceLocationException var6) {
            return false;
         }

         return lvt_4_2_ == null ? false : this.func_227013_a_(p_189714_1_, lvt_4_2_);
      } else {
         return false;
      }
   }

   public boolean func_227013_a_(boolean p_227013_1_, Template p_227013_2_) {
      BlockPos lvt_3_1_ = this.getPos();
      if (!StringUtils.isNullOrEmpty(p_227013_2_.getAuthor())) {
         this.author = p_227013_2_.getAuthor();
      }

      BlockPos lvt_4_1_ = p_227013_2_.getSize();
      boolean lvt_5_1_ = this.size.equals(lvt_4_1_);
      if (!lvt_5_1_) {
         this.size = lvt_4_1_;
         this.markDirty();
         BlockState lvt_6_1_ = this.world.getBlockState(lvt_3_1_);
         this.world.notifyBlockUpdate(lvt_3_1_, lvt_6_1_, lvt_6_1_, 3);
      }

      if (p_227013_1_ && !lvt_5_1_) {
         return false;
      } else {
         PlacementSettings lvt_6_2_ = (new PlacementSettings()).setMirror(this.mirror).setRotation(this.rotation).setIgnoreEntities(this.ignoreEntities).setChunk((ChunkPos)null);
         if (this.integrity < 1.0F) {
            lvt_6_2_.func_215219_b().addProcessor(new IntegrityProcessor(MathHelper.clamp(this.integrity, 0.0F, 1.0F))).setRandom(func_214074_b(this.seed));
         }

         BlockPos lvt_7_1_ = lvt_3_1_.add(this.position);
         p_227013_2_.addBlocksToWorldChunk(this.world, lvt_7_1_, lvt_6_2_);
         return true;
      }
   }

   public void unloadStructure() {
      if (this.name != null) {
         ServerWorld lvt_1_1_ = (ServerWorld)this.world;
         TemplateManager lvt_2_1_ = lvt_1_1_.getStructureTemplateManager();
         lvt_2_1_.remove(this.name);
      }
   }

   public boolean isStructureLoadable() {
      if (this.mode == StructureMode.LOAD && !this.world.isRemote && this.name != null) {
         ServerWorld lvt_1_1_ = (ServerWorld)this.world;
         TemplateManager lvt_2_1_ = lvt_1_1_.getStructureTemplateManager();

         try {
            return lvt_2_1_.getTemplate(this.name) != null;
         } catch (ResourceLocationException var4) {
            return false;
         }
      } else {
         return false;
      }
   }

   public boolean isPowered() {
      return this.powered;
   }

   public void setPowered(boolean p_189723_1_) {
      this.powered = p_189723_1_;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean showsAir() {
      return this.showAir;
   }

   public void setShowAir(boolean p_189703_1_) {
      this.showAir = p_189703_1_;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean showsBoundingBox() {
      return this.showBoundingBox;
   }

   public void setShowBoundingBox(boolean p_189710_1_) {
      this.showBoundingBox = p_189710_1_;
   }

   public static enum UpdateCommand {
      UPDATE_DATA,
      SAVE_AREA,
      LOAD_AREA,
      SCAN_AREA;
   }
}
