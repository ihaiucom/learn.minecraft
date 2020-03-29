package net.minecraft.tileentity;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.BeaconContainer;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.IIntArray;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.LockCode;
import net.minecraft.world.gen.Heightmap;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class BeaconTileEntity extends TileEntity implements INamedContainerProvider, ITickableTileEntity {
   public static final Effect[][] EFFECTS_LIST;
   private static final Set<Effect> VALID_EFFECTS;
   private List<BeaconTileEntity.BeamSegment> beamSegments = Lists.newArrayList();
   private List<BeaconTileEntity.BeamSegment> field_213934_g = Lists.newArrayList();
   private int levels;
   private int field_213935_i = -1;
   @Nullable
   private Effect primaryEffect;
   @Nullable
   private Effect secondaryEffect;
   @Nullable
   private ITextComponent customName;
   private LockCode field_213936_m;
   private final IIntArray field_213937_n;

   public BeaconTileEntity() {
      super(TileEntityType.BEACON);
      this.field_213936_m = LockCode.EMPTY_CODE;
      this.field_213937_n = new IIntArray() {
         public int get(int p_221476_1_) {
            switch(p_221476_1_) {
            case 0:
               return BeaconTileEntity.this.levels;
            case 1:
               return Effect.getId(BeaconTileEntity.this.primaryEffect);
            case 2:
               return Effect.getId(BeaconTileEntity.this.secondaryEffect);
            default:
               return 0;
            }
         }

         public void set(int p_221477_1_, int p_221477_2_) {
            switch(p_221477_1_) {
            case 0:
               BeaconTileEntity.this.levels = p_221477_2_;
               break;
            case 1:
               if (!BeaconTileEntity.this.world.isRemote && !BeaconTileEntity.this.beamSegments.isEmpty()) {
                  BeaconTileEntity.this.playSound(SoundEvents.BLOCK_BEACON_POWER_SELECT);
               }

               BeaconTileEntity.this.primaryEffect = BeaconTileEntity.isBeaconEffect(p_221477_2_);
               break;
            case 2:
               BeaconTileEntity.this.secondaryEffect = BeaconTileEntity.isBeaconEffect(p_221477_2_);
            }

         }

         public int size() {
            return 3;
         }
      };
   }

   public void tick() {
      int i = this.pos.getX();
      int j = this.pos.getY();
      int k = this.pos.getZ();
      BlockPos blockpos;
      if (this.field_213935_i < j) {
         blockpos = this.pos;
         this.field_213934_g = Lists.newArrayList();
         this.field_213935_i = blockpos.getY() - 1;
      } else {
         blockpos = new BlockPos(i, this.field_213935_i + 1, k);
      }

      BeaconTileEntity.BeamSegment beacontileentity$beamsegment = this.field_213934_g.isEmpty() ? null : (BeaconTileEntity.BeamSegment)this.field_213934_g.get(this.field_213934_g.size() - 1);
      int l = this.world.getHeight(Heightmap.Type.WORLD_SURFACE, i, k);

      int j1;
      for(j1 = 0; j1 < 10 && blockpos.getY() <= l; ++j1) {
         BlockState blockstate = this.world.getBlockState(blockpos);
         Block block = blockstate.getBlock();
         float[] afloat = blockstate.getBeaconColorMultiplier(this.world, blockpos, this.getPos());
         if (afloat != null) {
            if (this.field_213934_g.size() <= 1) {
               beacontileentity$beamsegment = new BeaconTileEntity.BeamSegment(afloat);
               this.field_213934_g.add(beacontileentity$beamsegment);
            } else if (beacontileentity$beamsegment != null) {
               if (Arrays.equals(afloat, beacontileentity$beamsegment.colors)) {
                  beacontileentity$beamsegment.incrementHeight();
               } else {
                  beacontileentity$beamsegment = new BeaconTileEntity.BeamSegment(new float[]{(beacontileentity$beamsegment.colors[0] + afloat[0]) / 2.0F, (beacontileentity$beamsegment.colors[1] + afloat[1]) / 2.0F, (beacontileentity$beamsegment.colors[2] + afloat[2]) / 2.0F});
                  this.field_213934_g.add(beacontileentity$beamsegment);
               }
            }
         } else {
            if (beacontileentity$beamsegment == null || blockstate.getOpacity(this.world, blockpos) >= 15 && block != Blocks.BEDROCK) {
               this.field_213934_g.clear();
               this.field_213935_i = l;
               break;
            }

            beacontileentity$beamsegment.incrementHeight();
         }

         blockpos = blockpos.up();
         ++this.field_213935_i;
      }

      j1 = this.levels;
      if (this.world.getGameTime() % 80L == 0L) {
         if (!this.beamSegments.isEmpty()) {
            this.func_213927_a(i, j, k);
         }

         if (this.levels > 0 && !this.beamSegments.isEmpty()) {
            this.addEffectsToPlayers();
            this.playSound(SoundEvents.BLOCK_BEACON_AMBIENT);
         }
      }

      if (this.field_213935_i >= l) {
         this.field_213935_i = -1;
         boolean flag = j1 > 0;
         this.beamSegments = this.field_213934_g;
         if (!this.world.isRemote) {
            boolean flag1 = this.levels > 0;
            if (!flag && flag1) {
               this.playSound(SoundEvents.BLOCK_BEACON_ACTIVATE);
               Iterator var14 = this.world.getEntitiesWithinAABB(ServerPlayerEntity.class, (new AxisAlignedBB((double)i, (double)j, (double)k, (double)i, (double)(j - 4), (double)k)).grow(10.0D, 5.0D, 10.0D)).iterator();

               while(var14.hasNext()) {
                  ServerPlayerEntity serverplayerentity = (ServerPlayerEntity)var14.next();
                  CriteriaTriggers.CONSTRUCT_BEACON.trigger(serverplayerentity, this);
               }
            } else if (flag && !flag1) {
               this.playSound(SoundEvents.BLOCK_BEACON_DEACTIVATE);
            }
         }
      }

   }

   private void func_213927_a(int p_213927_1_, int p_213927_2_, int p_213927_3_) {
      this.levels = 0;

      for(int i = 1; i <= 4; this.levels = i++) {
         int j = p_213927_2_ - i;
         if (j < 0) {
            break;
         }

         boolean flag = true;

         for(int k = p_213927_1_ - i; k <= p_213927_1_ + i && flag; ++k) {
            for(int l = p_213927_3_ - i; l <= p_213927_3_ + i; ++l) {
               if (!this.world.getBlockState(new BlockPos(k, j, l)).isBeaconBase(this.world, new BlockPos(k, j, l), this.getPos())) {
                  flag = false;
                  break;
               }
            }
         }

         if (!flag) {
            break;
         }
      }

   }

   public void remove() {
      this.playSound(SoundEvents.BLOCK_BEACON_DEACTIVATE);
      super.remove();
   }

   private void addEffectsToPlayers() {
      if (!this.world.isRemote && this.primaryEffect != null) {
         double d0 = (double)(this.levels * 10 + 10);
         int i = 0;
         if (this.levels >= 4 && this.primaryEffect == this.secondaryEffect) {
            i = 1;
         }

         int j = (9 + this.levels * 2) * 20;
         AxisAlignedBB axisalignedbb = (new AxisAlignedBB(this.pos)).grow(d0).expand(0.0D, (double)this.world.getHeight(), 0.0D);
         List<PlayerEntity> list = this.world.getEntitiesWithinAABB(PlayerEntity.class, axisalignedbb);
         Iterator var7 = list.iterator();

         PlayerEntity playerentity1;
         while(var7.hasNext()) {
            playerentity1 = (PlayerEntity)var7.next();
            playerentity1.addPotionEffect(new EffectInstance(this.primaryEffect, j, i, true, true));
         }

         if (this.levels >= 4 && this.primaryEffect != this.secondaryEffect && this.secondaryEffect != null) {
            var7 = list.iterator();

            while(var7.hasNext()) {
               playerentity1 = (PlayerEntity)var7.next();
               playerentity1.addPotionEffect(new EffectInstance(this.secondaryEffect, j, 0, true, true));
            }
         }
      }

   }

   public void playSound(SoundEvent p_205736_1_) {
      this.world.playSound((PlayerEntity)null, this.pos, p_205736_1_, SoundCategory.BLOCKS, 1.0F, 1.0F);
   }

   @OnlyIn(Dist.CLIENT)
   public List<BeaconTileEntity.BeamSegment> getBeamSegments() {
      return (List)(this.levels == 0 ? ImmutableList.of() : this.beamSegments);
   }

   public int getLevels() {
      return this.levels;
   }

   @Nullable
   public SUpdateTileEntityPacket getUpdatePacket() {
      return new SUpdateTileEntityPacket(this.pos, 3, this.getUpdateTag());
   }

   public CompoundNBT getUpdateTag() {
      return this.write(new CompoundNBT());
   }

   @OnlyIn(Dist.CLIENT)
   public double getMaxRenderDistanceSquared() {
      return 65536.0D;
   }

   @Nullable
   private static Effect isBeaconEffect(int p_184279_0_) {
      Effect effect = Effect.get(p_184279_0_);
      return VALID_EFFECTS.contains(effect) ? effect : null;
   }

   public void read(CompoundNBT p_145839_1_) {
      super.read(p_145839_1_);
      this.primaryEffect = isBeaconEffect(p_145839_1_.getInt("Primary"));
      this.secondaryEffect = isBeaconEffect(p_145839_1_.getInt("Secondary"));
      if (p_145839_1_.contains("CustomName", 8)) {
         this.customName = ITextComponent.Serializer.fromJson(p_145839_1_.getString("CustomName"));
      }

      this.field_213936_m = LockCode.read(p_145839_1_);
   }

   public CompoundNBT write(CompoundNBT p_189515_1_) {
      super.write(p_189515_1_);
      p_189515_1_.putInt("Primary", Effect.getId(this.primaryEffect));
      p_189515_1_.putInt("Secondary", Effect.getId(this.secondaryEffect));
      p_189515_1_.putInt("Levels", this.levels);
      if (this.customName != null) {
         p_189515_1_.putString("CustomName", ITextComponent.Serializer.toJson(this.customName));
      }

      this.field_213936_m.write(p_189515_1_);
      return p_189515_1_;
   }

   public void setCustomName(@Nullable ITextComponent p_200227_1_) {
      this.customName = p_200227_1_;
   }

   @Nullable
   public Container createMenu(int p_createMenu_1_, PlayerInventory p_createMenu_2_, PlayerEntity p_createMenu_3_) {
      return LockableTileEntity.canUnlock(p_createMenu_3_, this.field_213936_m, this.getDisplayName()) ? new BeaconContainer(p_createMenu_1_, p_createMenu_2_, this.field_213937_n, IWorldPosCallable.of(this.world, this.getPos())) : null;
   }

   public ITextComponent getDisplayName() {
      return (ITextComponent)(this.customName != null ? this.customName : new TranslationTextComponent("container.beacon", new Object[0]));
   }

   static {
      EFFECTS_LIST = new Effect[][]{{Effects.SPEED, Effects.HASTE}, {Effects.RESISTANCE, Effects.JUMP_BOOST}, {Effects.STRENGTH}, {Effects.REGENERATION}};
      VALID_EFFECTS = (Set)Arrays.stream(EFFECTS_LIST).flatMap(Arrays::stream).collect(Collectors.toSet());
   }

   public static class BeamSegment {
      private final float[] colors;
      private int height;

      public BeamSegment(float[] p_i45669_1_) {
         this.colors = p_i45669_1_;
         this.height = 1;
      }

      protected void incrementHeight() {
         ++this.height;
      }

      @OnlyIn(Dist.CLIENT)
      public float[] getColors() {
         return this.colors;
      }

      @OnlyIn(Dist.CLIENT)
      public int getHeight() {
         return this.height;
      }
   }
}
