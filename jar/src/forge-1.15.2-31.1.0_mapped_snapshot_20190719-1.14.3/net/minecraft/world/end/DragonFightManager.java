package net.minecraft.world.end;

import com.google.common.collect.ContiguousSet;
import com.google.common.collect.DiscreteDomain;
import com.google.common.collect.Lists;
import com.google.common.collect.Range;
import com.google.common.collect.Sets;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.Blocks;
import net.minecraft.block.pattern.BlockMatcher;
import net.minecraft.block.pattern.BlockPattern;
import net.minecraft.block.pattern.BlockPatternBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.boss.dragon.phase.PhaseType;
import net.minecraft.entity.item.EnderCrystalEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.IntNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tileentity.EndPortalTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.CachedBlockInfo;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.Unit;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.BossInfo;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.dimension.EndDimension;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.EndGatewayConfig;
import net.minecraft.world.gen.feature.EndPodiumFeature;
import net.minecraft.world.gen.feature.EndSpikeFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.server.ChunkHolder;
import net.minecraft.world.server.ServerBossInfo;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.server.TicketType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DragonFightManager {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final Predicate<Entity> VALID_PLAYER;
   private final ServerBossInfo bossInfo;
   private final ServerWorld world;
   private final List<Integer> gateways;
   private final BlockPattern portalPattern;
   private int ticksSinceDragonSeen;
   private int aliveCrystals;
   private int ticksSinceCrystalsScanned;
   private int ticksSinceLastPlayerScan;
   private boolean dragonKilled;
   private boolean previouslyKilled;
   private UUID dragonUniqueId;
   private boolean scanForLegacyFight;
   private BlockPos exitPortalLocation;
   private DragonSpawnState respawnState;
   private int respawnStateTicks;
   private List<EnderCrystalEntity> crystals;

   public DragonFightManager(ServerWorld p_i230062_1_, CompoundNBT p_i230062_2_, EndDimension p_i230062_3_) {
      this.bossInfo = (ServerBossInfo)(new ServerBossInfo(new TranslationTextComponent("entity.minecraft.ender_dragon", new Object[0]), BossInfo.Color.PINK, BossInfo.Overlay.PROGRESS)).setPlayEndBossMusic(true).setCreateFog(true);
      this.gateways = Lists.newArrayList();
      this.scanForLegacyFight = true;
      this.world = p_i230062_1_;
      if (p_i230062_2_.contains("DragonKilled", 99)) {
         if (p_i230062_2_.hasUniqueId("DragonUUID")) {
            this.dragonUniqueId = p_i230062_2_.getUniqueId("DragonUUID");
         }

         this.dragonKilled = p_i230062_2_.getBoolean("DragonKilled");
         this.previouslyKilled = p_i230062_2_.getBoolean("PreviouslyKilled");
         this.scanForLegacyFight = !p_i230062_2_.getBoolean("LegacyScanPerformed");
         if (p_i230062_2_.getBoolean("IsRespawning")) {
            this.respawnState = DragonSpawnState.START;
         }

         if (p_i230062_2_.contains("ExitPortalLocation", 10)) {
            this.exitPortalLocation = NBTUtil.readBlockPos(p_i230062_2_.getCompound("ExitPortalLocation"));
         }
      } else {
         this.dragonKilled = true;
         this.previouslyKilled = true;
      }

      if (p_i230062_2_.contains("Gateways", 9)) {
         ListNBT listnbt = p_i230062_2_.getList("Gateways", 3);

         for(int i = 0; i < listnbt.size(); ++i) {
            this.gateways.add(listnbt.getInt(i));
         }
      } else {
         this.gateways.addAll(ContiguousSet.create(Range.closedOpen(0, 20), DiscreteDomain.integers()));
         Collections.shuffle(this.gateways, new Random(p_i230062_3_.getSeed()));
      }

      this.portalPattern = BlockPatternBuilder.start().aisle("       ", "       ", "       ", "   #   ", "       ", "       ", "       ").aisle("       ", "       ", "       ", "   #   ", "       ", "       ", "       ").aisle("       ", "       ", "       ", "   #   ", "       ", "       ", "       ").aisle("  ###  ", " #   # ", "#     #", "#  #  #", "#     #", " #   # ", "  ###  ").aisle("       ", "  ###  ", " ##### ", " ##### ", " ##### ", "  ###  ", "       ").where('#', CachedBlockInfo.hasState(BlockMatcher.forBlock(Blocks.BEDROCK))).build();
   }

   public CompoundNBT write() {
      CompoundNBT compoundnbt = new CompoundNBT();
      if (this.dragonUniqueId != null) {
         compoundnbt.putUniqueId("DragonUUID", this.dragonUniqueId);
      }

      compoundnbt.putBoolean("DragonKilled", this.dragonKilled);
      compoundnbt.putBoolean("PreviouslyKilled", this.previouslyKilled);
      compoundnbt.putBoolean("LegacyScanPerformed", !this.scanForLegacyFight);
      if (this.exitPortalLocation != null) {
         compoundnbt.put("ExitPortalLocation", NBTUtil.writeBlockPos(this.exitPortalLocation));
      }

      ListNBT listnbt = new ListNBT();
      Iterator var3 = this.gateways.iterator();

      while(var3.hasNext()) {
         int i = (Integer)var3.next();
         listnbt.add(IntNBT.func_229692_a_(i));
      }

      compoundnbt.put("Gateways", listnbt);
      return compoundnbt;
   }

   public void tick() {
      this.bossInfo.setVisible(!this.dragonKilled);
      if (++this.ticksSinceLastPlayerScan >= 20) {
         this.updatePlayers();
         this.ticksSinceLastPlayerScan = 0;
      }

      if (!this.bossInfo.getPlayers().isEmpty()) {
         this.world.getChunkProvider().func_217228_a(TicketType.DRAGON, new ChunkPos(0, 0), 9, Unit.INSTANCE);
         boolean flag = this.func_222670_k();
         if (this.scanForLegacyFight && flag) {
            this.func_210827_g();
            this.scanForLegacyFight = false;
         }

         if (this.respawnState != null) {
            if (this.crystals == null && flag) {
               this.respawnState = null;
               this.tryRespawnDragon();
            }

            this.respawnState.process(this.world, this, this.crystals, this.respawnStateTicks++, this.exitPortalLocation);
         }

         if (!this.dragonKilled) {
            if ((this.dragonUniqueId == null || ++this.ticksSinceDragonSeen >= 1200) && flag) {
               this.func_210828_h();
               this.ticksSinceDragonSeen = 0;
            }

            if (++this.ticksSinceCrystalsScanned >= 100 && flag) {
               this.findAliveCrystals();
               this.ticksSinceCrystalsScanned = 0;
            }
         }
      } else {
         this.world.getChunkProvider().func_217222_b(TicketType.DRAGON, new ChunkPos(0, 0), 9, Unit.INSTANCE);
      }

   }

   private void func_210827_g() {
      LOGGER.info("Scanning for legacy world dragon fight...");
      boolean flag = this.func_229981_i_();
      if (flag) {
         LOGGER.info("Found that the dragon has been killed in this world already.");
         this.previouslyKilled = true;
      } else {
         LOGGER.info("Found that the dragon has not yet been killed in this world.");
         this.previouslyKilled = false;
         if (this.findExitPortal() == null) {
            this.generatePortal(false);
         }
      }

      List<EnderDragonEntity> list = this.world.getDragons();
      if (list.isEmpty()) {
         this.dragonKilled = true;
      } else {
         EnderDragonEntity enderdragonentity = (EnderDragonEntity)list.get(0);
         this.dragonUniqueId = enderdragonentity.getUniqueID();
         LOGGER.info("Found that there's a dragon still alive ({})", enderdragonentity);
         this.dragonKilled = false;
         if (!flag) {
            LOGGER.info("But we didn't have a portal, let's remove it.");
            enderdragonentity.remove();
            this.dragonUniqueId = null;
         }
      }

      if (!this.previouslyKilled && this.dragonKilled) {
         this.dragonKilled = false;
      }

   }

   private void func_210828_h() {
      List<EnderDragonEntity> list = this.world.getDragons();
      if (list.isEmpty()) {
         LOGGER.debug("Haven't seen the dragon, respawning it");
         this.createNewDragon();
      } else {
         LOGGER.debug("Haven't seen our dragon, but found another one to use.");
         this.dragonUniqueId = ((EnderDragonEntity)list.get(0)).getUniqueID();
      }

   }

   protected void setRespawnState(DragonSpawnState p_186095_1_) {
      if (this.respawnState == null) {
         throw new IllegalStateException("Dragon respawn isn't in progress, can't skip ahead in the animation.");
      } else {
         this.respawnStateTicks = 0;
         if (p_186095_1_ == DragonSpawnState.END) {
            this.respawnState = null;
            this.dragonKilled = false;
            EnderDragonEntity enderdragonentity = this.createNewDragon();
            Iterator var3 = this.bossInfo.getPlayers().iterator();

            while(var3.hasNext()) {
               ServerPlayerEntity serverplayerentity = (ServerPlayerEntity)var3.next();
               CriteriaTriggers.SUMMONED_ENTITY.trigger(serverplayerentity, enderdragonentity);
            }
         } else {
            this.respawnState = p_186095_1_;
         }

      }
   }

   private boolean func_229981_i_() {
      for(int i = -8; i <= 8; ++i) {
         for(int j = -8; j <= 8; ++j) {
            Chunk chunk = this.world.getChunk(i, j);
            Iterator var4 = chunk.getTileEntityMap().values().iterator();

            while(var4.hasNext()) {
               TileEntity tileentity = (TileEntity)var4.next();
               if (tileentity instanceof EndPortalTileEntity) {
                  return true;
               }
            }
         }
      }

      return false;
   }

   @Nullable
   private BlockPattern.PatternHelper findExitPortal() {
      int i;
      int l;
      for(i = -8; i <= 8; ++i) {
         for(l = -8; l <= 8; ++l) {
            Chunk chunk = this.world.getChunk(i, l);
            Iterator var4 = chunk.getTileEntityMap().values().iterator();

            while(var4.hasNext()) {
               TileEntity tileentity = (TileEntity)var4.next();
               if (tileentity instanceof EndPortalTileEntity) {
                  BlockPattern.PatternHelper blockpattern$patternhelper = this.portalPattern.match(this.world, tileentity.getPos());
                  if (blockpattern$patternhelper != null) {
                     BlockPos blockpos = blockpattern$patternhelper.translateOffset(3, 3, 3).getPos();
                     if (this.exitPortalLocation == null && blockpos.getX() == 0 && blockpos.getZ() == 0) {
                        this.exitPortalLocation = blockpos;
                     }

                     return blockpattern$patternhelper;
                  }
               }
            }
         }
      }

      i = this.world.getHeight(Heightmap.Type.MOTION_BLOCKING, EndPodiumFeature.END_PODIUM_LOCATION).getY();

      for(l = i; l >= 0; --l) {
         BlockPattern.PatternHelper blockpattern$patternhelper1 = this.portalPattern.match(this.world, new BlockPos(EndPodiumFeature.END_PODIUM_LOCATION.getX(), l, EndPodiumFeature.END_PODIUM_LOCATION.getZ()));
         if (blockpattern$patternhelper1 != null) {
            if (this.exitPortalLocation == null) {
               this.exitPortalLocation = blockpattern$patternhelper1.translateOffset(3, 3, 3).getPos();
            }

            return blockpattern$patternhelper1;
         }
      }

      return null;
   }

   private boolean func_222670_k() {
      for(int i = -8; i <= 8; ++i) {
         for(int j = 8; j <= 8; ++j) {
            IChunk ichunk = this.world.getChunk(i, j, ChunkStatus.FULL, false);
            if (!(ichunk instanceof Chunk)) {
               return false;
            }

            ChunkHolder.LocationType chunkholder$locationtype = ((Chunk)ichunk).func_217321_u();
            if (!chunkholder$locationtype.isAtLeast(ChunkHolder.LocationType.TICKING)) {
               return false;
            }
         }
      }

      return true;
   }

   private void updatePlayers() {
      Set<ServerPlayerEntity> set = Sets.newHashSet();
      Iterator var2 = this.world.getPlayers(VALID_PLAYER).iterator();

      while(var2.hasNext()) {
         ServerPlayerEntity serverplayerentity = (ServerPlayerEntity)var2.next();
         this.bossInfo.addPlayer(serverplayerentity);
         set.add(serverplayerentity);
      }

      Set<ServerPlayerEntity> set1 = Sets.newHashSet(this.bossInfo.getPlayers());
      set1.removeAll(set);
      Iterator var6 = set1.iterator();

      while(var6.hasNext()) {
         ServerPlayerEntity serverplayerentity1 = (ServerPlayerEntity)var6.next();
         this.bossInfo.removePlayer(serverplayerentity1);
      }

   }

   private void findAliveCrystals() {
      this.ticksSinceCrystalsScanned = 0;
      this.aliveCrystals = 0;

      EndSpikeFeature.EndSpike endspikefeature$endspike;
      for(Iterator var1 = EndSpikeFeature.func_214554_a(this.world).iterator(); var1.hasNext(); this.aliveCrystals += this.world.getEntitiesWithinAABB(EnderCrystalEntity.class, endspikefeature$endspike.getTopBoundingBox()).size()) {
         endspikefeature$endspike = (EndSpikeFeature.EndSpike)var1.next();
      }

      LOGGER.debug("Found {} end crystals still alive", this.aliveCrystals);
   }

   public void processDragonDeath(EnderDragonEntity p_186096_1_) {
      if (p_186096_1_.getUniqueID().equals(this.dragonUniqueId)) {
         this.bossInfo.setPercent(0.0F);
         this.bossInfo.setVisible(false);
         this.generatePortal(true);
         this.spawnNewGateway();
         if (!this.previouslyKilled) {
            this.world.setBlockState(this.world.getHeight(Heightmap.Type.MOTION_BLOCKING, EndPodiumFeature.END_PODIUM_LOCATION), Blocks.DRAGON_EGG.getDefaultState());
         }

         this.previouslyKilled = true;
         this.dragonKilled = true;
      }

   }

   private void spawnNewGateway() {
      if (!this.gateways.isEmpty()) {
         int i = (Integer)this.gateways.remove(this.gateways.size() - 1);
         int j = MathHelper.floor(96.0D * Math.cos(2.0D * (-3.141592653589793D + 0.15707963267948966D * (double)i)));
         int k = MathHelper.floor(96.0D * Math.sin(2.0D * (-3.141592653589793D + 0.15707963267948966D * (double)i)));
         this.generateGateway(new BlockPos(j, 75, k));
      }

   }

   private void generateGateway(BlockPos p_186089_1_) {
      this.world.playEvent(3000, p_186089_1_, 0);
      Feature.END_GATEWAY.func_225566_b_(EndGatewayConfig.func_214698_a()).place(this.world, this.world.getChunkProvider().getChunkGenerator(), new Random(), p_186089_1_);
   }

   private void generatePortal(boolean p_186094_1_) {
      EndPodiumFeature endpodiumfeature = new EndPodiumFeature(p_186094_1_);
      if (this.exitPortalLocation == null) {
         for(this.exitPortalLocation = this.world.getHeight(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, EndPodiumFeature.END_PODIUM_LOCATION).down(); this.world.getBlockState(this.exitPortalLocation).getBlock() == Blocks.BEDROCK && this.exitPortalLocation.getY() > this.world.getSeaLevel(); this.exitPortalLocation = this.exitPortalLocation.down()) {
         }
      }

      endpodiumfeature.func_225566_b_(IFeatureConfig.NO_FEATURE_CONFIG).place(this.world, this.world.getChunkProvider().getChunkGenerator(), new Random(), this.exitPortalLocation);
   }

   private EnderDragonEntity createNewDragon() {
      this.world.getChunkAt(new BlockPos(0, 128, 0));
      EnderDragonEntity enderdragonentity = (EnderDragonEntity)EntityType.ENDER_DRAGON.create(this.world);
      enderdragonentity.getPhaseManager().setPhase(PhaseType.HOLDING_PATTERN);
      enderdragonentity.setLocationAndAngles(0.0D, 128.0D, 0.0D, this.world.rand.nextFloat() * 360.0F, 0.0F);
      this.world.addEntity(enderdragonentity);
      this.dragonUniqueId = enderdragonentity.getUniqueID();
      return enderdragonentity;
   }

   public void dragonUpdate(EnderDragonEntity p_186099_1_) {
      if (p_186099_1_.getUniqueID().equals(this.dragonUniqueId)) {
         this.bossInfo.setPercent(p_186099_1_.getHealth() / p_186099_1_.getMaxHealth());
         this.ticksSinceDragonSeen = 0;
         if (p_186099_1_.hasCustomName()) {
            this.bossInfo.setName(p_186099_1_.getDisplayName());
         }
      }

   }

   public int getNumAliveCrystals() {
      return this.aliveCrystals;
   }

   public void onCrystalDestroyed(EnderCrystalEntity p_186090_1_, DamageSource p_186090_2_) {
      if (this.respawnState != null && this.crystals.contains(p_186090_1_)) {
         LOGGER.debug("Aborting respawn sequence");
         this.respawnState = null;
         this.respawnStateTicks = 0;
         this.resetSpikeCrystals();
         this.generatePortal(true);
      } else {
         this.findAliveCrystals();
         Entity entity = this.world.getEntityByUuid(this.dragonUniqueId);
         if (entity instanceof EnderDragonEntity) {
            ((EnderDragonEntity)entity).onCrystalDestroyed(p_186090_1_, new BlockPos(p_186090_1_), p_186090_2_);
         }
      }

   }

   public boolean hasPreviouslyKilledDragon() {
      return this.previouslyKilled;
   }

   public void tryRespawnDragon() {
      if (this.dragonKilled && this.respawnState == null) {
         BlockPos blockpos = this.exitPortalLocation;
         if (blockpos == null) {
            LOGGER.debug("Tried to respawn, but need to find the portal first.");
            BlockPattern.PatternHelper blockpattern$patternhelper = this.findExitPortal();
            if (blockpattern$patternhelper == null) {
               LOGGER.debug("Couldn't find a portal, so we made one.");
               this.generatePortal(true);
            } else {
               LOGGER.debug("Found the exit portal & temporarily using it.");
            }

            blockpos = this.exitPortalLocation;
         }

         List<EnderCrystalEntity> list1 = Lists.newArrayList();
         BlockPos blockpos1 = blockpos.up(1);
         Iterator var4 = Direction.Plane.HORIZONTAL.iterator();

         while(var4.hasNext()) {
            Direction direction = (Direction)var4.next();
            List<EnderCrystalEntity> list = this.world.getEntitiesWithinAABB(EnderCrystalEntity.class, new AxisAlignedBB(blockpos1.offset(direction, 2)));
            if (list.isEmpty()) {
               return;
            }

            list1.addAll(list);
         }

         LOGGER.debug("Found all crystals, respawning dragon.");
         this.respawnDragon(list1);
      }

   }

   private void respawnDragon(List<EnderCrystalEntity> p_186093_1_) {
      if (this.dragonKilled && this.respawnState == null) {
         for(BlockPattern.PatternHelper blockpattern$patternhelper = this.findExitPortal(); blockpattern$patternhelper != null; blockpattern$patternhelper = this.findExitPortal()) {
            for(int i = 0; i < this.portalPattern.getPalmLength(); ++i) {
               for(int j = 0; j < this.portalPattern.getThumbLength(); ++j) {
                  for(int k = 0; k < this.portalPattern.getFingerLength(); ++k) {
                     CachedBlockInfo cachedblockinfo = blockpattern$patternhelper.translateOffset(i, j, k);
                     if (cachedblockinfo.getBlockState().getBlock() == Blocks.BEDROCK || cachedblockinfo.getBlockState().getBlock() == Blocks.END_PORTAL) {
                        this.world.setBlockState(cachedblockinfo.getPos(), Blocks.END_STONE.getDefaultState());
                     }
                  }
               }
            }
         }

         this.respawnState = DragonSpawnState.START;
         this.respawnStateTicks = 0;
         this.generatePortal(false);
         this.crystals = p_186093_1_;
      }

   }

   public void resetSpikeCrystals() {
      Iterator var1 = EndSpikeFeature.func_214554_a(this.world).iterator();

      while(var1.hasNext()) {
         EndSpikeFeature.EndSpike endspikefeature$endspike = (EndSpikeFeature.EndSpike)var1.next();
         Iterator var3 = this.world.getEntitiesWithinAABB(EnderCrystalEntity.class, endspikefeature$endspike.getTopBoundingBox()).iterator();

         while(var3.hasNext()) {
            EnderCrystalEntity endercrystalentity = (EnderCrystalEntity)var3.next();
            endercrystalentity.setInvulnerable(false);
            endercrystalentity.setBeamTarget((BlockPos)null);
         }
      }

   }

   public void addPlayer(ServerPlayerEntity p_addPlayer_1_) {
      this.bossInfo.addPlayer(p_addPlayer_1_);
   }

   public void removePlayer(ServerPlayerEntity p_removePlayer_1_) {
      this.bossInfo.removePlayer(p_removePlayer_1_);
   }

   static {
      VALID_PLAYER = EntityPredicates.IS_ALIVE.and(EntityPredicates.withinRange(0.0D, 128.0D, 0.0D, 192.0D));
   }
}
