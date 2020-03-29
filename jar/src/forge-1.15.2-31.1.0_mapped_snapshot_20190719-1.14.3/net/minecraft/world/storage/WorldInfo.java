package net.minecraft.world.storage;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.hash.Hashing;
import com.mojang.datafixers.DataFixer;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.JsonOps;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.command.TimerCallbackManager;
import net.minecraft.command.TimerCallbackSerializers;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.NBTDynamicOps;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.nbt.StringNBT;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.SharedConstants;
import net.minecraft.util.Util;
import net.minecraft.util.datafix.DefaultTypeReferences;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameRules;
import net.minecraft.world.GameType;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.WorldType;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class WorldInfo {
   private String versionName;
   private int versionId;
   private boolean versionSnapshot;
   public static final Difficulty DEFAULT_DIFFICULTY;
   private long randomSeed;
   private WorldType generator;
   private CompoundNBT generatorOptions;
   @Nullable
   private String legacyCustomOptions;
   private int spawnX;
   private int spawnY;
   private int spawnZ;
   private long gameTime;
   private long dayTime;
   private long lastTimePlayed;
   private long sizeOnDisk;
   @Nullable
   private final DataFixer fixer;
   private final int dataVersion;
   private boolean playerDataFixed;
   private CompoundNBT playerData;
   private String levelName;
   private int saveVersion;
   private int clearWeatherTime;
   private boolean raining;
   private int rainTime;
   private boolean thundering;
   private int thunderTime;
   private GameType gameType;
   private boolean mapFeaturesEnabled;
   private boolean hardcore;
   private boolean allowCommands;
   private boolean initialized;
   private Difficulty difficulty;
   private boolean difficultyLocked;
   private double borderCenterX;
   private double borderCenterZ;
   private double borderSize;
   private long borderSizeLerpTime;
   private double borderSizeLerpTarget;
   private double borderSafeZone;
   private double borderDamagePerBlock;
   private int borderWarningBlocks;
   private int borderWarningTime;
   private final Set<String> disabledDataPacks;
   private final Set<String> enabledDataPacks;
   private final Map<DimensionType, CompoundNBT> dimensionData;
   private CompoundNBT customBossEvents;
   private int wanderingTraderSpawnDelay;
   private int wanderingTraderSpawnChance;
   private UUID wanderingTraderId;
   private Set<String> field_230141_X_;
   private boolean field_230142_Y_;
   private final GameRules gameRules;
   private final TimerCallbackManager<MinecraftServer> scheduledEvents;

   protected WorldInfo() {
      this.generator = WorldType.DEFAULT;
      this.generatorOptions = new CompoundNBT();
      this.borderSize = 6.0E7D;
      this.borderSafeZone = 5.0D;
      this.borderDamagePerBlock = 0.2D;
      this.borderWarningBlocks = 5;
      this.borderWarningTime = 15;
      this.disabledDataPacks = Sets.newHashSet();
      this.enabledDataPacks = Sets.newLinkedHashSet();
      this.dimensionData = Maps.newIdentityHashMap();
      this.field_230141_X_ = Sets.newLinkedHashSet();
      this.gameRules = new GameRules();
      this.scheduledEvents = new TimerCallbackManager(TimerCallbackSerializers.field_216342_a);
      this.fixer = null;
      this.dataVersion = SharedConstants.getVersion().getWorldVersion();
      this.setGeneratorOptions(new CompoundNBT());
   }

   public WorldInfo(CompoundNBT p_i49564_1_, DataFixer p_i49564_2_, int p_i49564_3_, @Nullable CompoundNBT p_i49564_4_) {
      this.generator = WorldType.DEFAULT;
      this.generatorOptions = new CompoundNBT();
      this.borderSize = 6.0E7D;
      this.borderSafeZone = 5.0D;
      this.borderDamagePerBlock = 0.2D;
      this.borderWarningBlocks = 5;
      this.borderWarningTime = 15;
      this.disabledDataPacks = Sets.newHashSet();
      this.enabledDataPacks = Sets.newLinkedHashSet();
      this.dimensionData = Maps.newIdentityHashMap();
      this.field_230141_X_ = Sets.newLinkedHashSet();
      this.gameRules = new GameRules();
      this.scheduledEvents = new TimerCallbackManager(TimerCallbackSerializers.field_216342_a);
      this.fixer = p_i49564_2_;
      ListNBT listnbt = p_i49564_1_.getList("ServerBrands", 8);

      for(int i = 0; i < listnbt.size(); ++i) {
         this.field_230141_X_.add(listnbt.getString(i));
      }

      this.field_230142_Y_ = p_i49564_1_.getBoolean("WasModded");
      CompoundNBT compoundnbt2;
      if (p_i49564_1_.contains("Version", 10)) {
         compoundnbt2 = p_i49564_1_.getCompound("Version");
         this.versionName = compoundnbt2.getString("Name");
         this.versionId = compoundnbt2.getInt("Id");
         this.versionSnapshot = compoundnbt2.getBoolean("Snapshot");
      }

      this.randomSeed = p_i49564_1_.getLong("RandomSeed");
      if (p_i49564_1_.contains("generatorName", 8)) {
         String s1 = p_i49564_1_.getString("generatorName");
         this.generator = WorldType.byName(s1);
         if (this.generator == null) {
            this.generator = WorldType.DEFAULT;
         } else if (this.generator == WorldType.CUSTOMIZED) {
            this.legacyCustomOptions = p_i49564_1_.getString("generatorOptions");
         } else if (this.generator.isVersioned()) {
            int j = 0;
            if (p_i49564_1_.contains("generatorVersion", 99)) {
               j = p_i49564_1_.getInt("generatorVersion");
            }

            this.generator = this.generator.getWorldTypeForGeneratorVersion(j);
         }

         this.setGeneratorOptions(p_i49564_1_.getCompound("generatorOptions"));
      }

      this.gameType = GameType.getByID(p_i49564_1_.getInt("GameType"));
      if (p_i49564_1_.contains("legacy_custom_options", 8)) {
         this.legacyCustomOptions = p_i49564_1_.getString("legacy_custom_options");
      }

      if (p_i49564_1_.contains("MapFeatures", 99)) {
         this.mapFeaturesEnabled = p_i49564_1_.getBoolean("MapFeatures");
      } else {
         this.mapFeaturesEnabled = true;
      }

      this.spawnX = p_i49564_1_.getInt("SpawnX");
      this.spawnY = p_i49564_1_.getInt("SpawnY");
      this.spawnZ = p_i49564_1_.getInt("SpawnZ");
      this.gameTime = p_i49564_1_.getLong("Time");
      if (p_i49564_1_.contains("DayTime", 99)) {
         this.dayTime = p_i49564_1_.getLong("DayTime");
      } else {
         this.dayTime = this.gameTime;
      }

      this.lastTimePlayed = p_i49564_1_.getLong("LastPlayed");
      this.sizeOnDisk = p_i49564_1_.getLong("SizeOnDisk");
      this.levelName = p_i49564_1_.getString("LevelName");
      this.saveVersion = p_i49564_1_.getInt("version");
      this.clearWeatherTime = p_i49564_1_.getInt("clearWeatherTime");
      this.rainTime = p_i49564_1_.getInt("rainTime");
      this.raining = p_i49564_1_.getBoolean("raining");
      this.thunderTime = p_i49564_1_.getInt("thunderTime");
      this.thundering = p_i49564_1_.getBoolean("thundering");
      this.hardcore = p_i49564_1_.getBoolean("hardcore");
      if (p_i49564_1_.contains("initialized", 99)) {
         this.initialized = p_i49564_1_.getBoolean("initialized");
      } else {
         this.initialized = true;
      }

      if (p_i49564_1_.contains("allowCommands", 99)) {
         this.allowCommands = p_i49564_1_.getBoolean("allowCommands");
      } else {
         this.allowCommands = this.gameType == GameType.CREATIVE;
      }

      this.dataVersion = p_i49564_3_;
      if (p_i49564_4_ != null) {
         this.playerData = p_i49564_4_;
      }

      if (p_i49564_1_.contains("GameRules", 10)) {
         this.gameRules.read(p_i49564_1_.getCompound("GameRules"));
      }

      if (p_i49564_1_.contains("Difficulty", 99)) {
         this.difficulty = Difficulty.byId(p_i49564_1_.getByte("Difficulty"));
      }

      if (p_i49564_1_.contains("DifficultyLocked", 1)) {
         this.difficultyLocked = p_i49564_1_.getBoolean("DifficultyLocked");
      }

      if (p_i49564_1_.contains("BorderCenterX", 99)) {
         this.borderCenterX = p_i49564_1_.getDouble("BorderCenterX");
      }

      if (p_i49564_1_.contains("BorderCenterZ", 99)) {
         this.borderCenterZ = p_i49564_1_.getDouble("BorderCenterZ");
      }

      if (p_i49564_1_.contains("BorderSize", 99)) {
         this.borderSize = p_i49564_1_.getDouble("BorderSize");
      }

      if (p_i49564_1_.contains("BorderSizeLerpTime", 99)) {
         this.borderSizeLerpTime = p_i49564_1_.getLong("BorderSizeLerpTime");
      }

      if (p_i49564_1_.contains("BorderSizeLerpTarget", 99)) {
         this.borderSizeLerpTarget = p_i49564_1_.getDouble("BorderSizeLerpTarget");
      }

      if (p_i49564_1_.contains("BorderSafeZone", 99)) {
         this.borderSafeZone = p_i49564_1_.getDouble("BorderSafeZone");
      }

      if (p_i49564_1_.contains("BorderDamagePerBlock", 99)) {
         this.borderDamagePerBlock = p_i49564_1_.getDouble("BorderDamagePerBlock");
      }

      if (p_i49564_1_.contains("BorderWarningBlocks", 99)) {
         this.borderWarningBlocks = p_i49564_1_.getInt("BorderWarningBlocks");
      }

      if (p_i49564_1_.contains("BorderWarningTime", 99)) {
         this.borderWarningTime = p_i49564_1_.getInt("BorderWarningTime");
      }

      if (p_i49564_1_.contains("DimensionData", 10)) {
         compoundnbt2 = p_i49564_1_.getCompound("DimensionData");
         Iterator var12 = compoundnbt2.keySet().iterator();

         while(var12.hasNext()) {
            String s = (String)var12.next();
            this.dimensionData.put(DimensionType.getById(Integer.parseInt(s)), compoundnbt2.getCompound(s));
         }
      }

      if (p_i49564_1_.contains("DataPacks", 10)) {
         compoundnbt2 = p_i49564_1_.getCompound("DataPacks");
         ListNBT listnbt1 = compoundnbt2.getList("Disabled", 8);

         for(int l = 0; l < listnbt1.size(); ++l) {
            this.disabledDataPacks.add(listnbt1.getString(l));
         }

         ListNBT listnbt2 = compoundnbt2.getList("Enabled", 8);

         for(int k = 0; k < listnbt2.size(); ++k) {
            this.enabledDataPacks.add(listnbt2.getString(k));
         }
      }

      if (p_i49564_1_.contains("CustomBossEvents", 10)) {
         this.customBossEvents = p_i49564_1_.getCompound("CustomBossEvents");
      }

      if (p_i49564_1_.contains("ScheduledEvents", 9)) {
         this.scheduledEvents.read(p_i49564_1_.getList("ScheduledEvents", 10));
      }

      if (p_i49564_1_.contains("WanderingTraderSpawnDelay", 99)) {
         this.wanderingTraderSpawnDelay = p_i49564_1_.getInt("WanderingTraderSpawnDelay");
      }

      if (p_i49564_1_.contains("WanderingTraderSpawnChance", 99)) {
         this.wanderingTraderSpawnChance = p_i49564_1_.getInt("WanderingTraderSpawnChance");
      }

      if (p_i49564_1_.contains("WanderingTraderId", 8)) {
         this.wanderingTraderId = UUID.fromString(p_i49564_1_.getString("WanderingTraderId"));
      }

   }

   public WorldInfo(WorldSettings p_i2158_1_, String p_i2158_2_) {
      this.generator = WorldType.DEFAULT;
      this.generatorOptions = new CompoundNBT();
      this.borderSize = 6.0E7D;
      this.borderSafeZone = 5.0D;
      this.borderDamagePerBlock = 0.2D;
      this.borderWarningBlocks = 5;
      this.borderWarningTime = 15;
      this.disabledDataPacks = Sets.newHashSet();
      this.enabledDataPacks = Sets.newLinkedHashSet();
      this.dimensionData = Maps.newIdentityHashMap();
      this.field_230141_X_ = Sets.newLinkedHashSet();
      this.gameRules = new GameRules();
      this.scheduledEvents = new TimerCallbackManager(TimerCallbackSerializers.field_216342_a);
      this.fixer = null;
      this.dataVersion = SharedConstants.getVersion().getWorldVersion();
      this.populateFromWorldSettings(p_i2158_1_);
      this.levelName = p_i2158_2_;
      this.difficulty = DEFAULT_DIFFICULTY;
      this.initialized = false;
   }

   public void populateFromWorldSettings(WorldSettings p_176127_1_) {
      this.randomSeed = p_176127_1_.getSeed();
      this.gameType = p_176127_1_.getGameType();
      this.mapFeaturesEnabled = p_176127_1_.isMapFeaturesEnabled();
      this.hardcore = p_176127_1_.getHardcoreEnabled();
      this.generator = p_176127_1_.getTerrainType();
      this.setGeneratorOptions((CompoundNBT)Dynamic.convert(JsonOps.INSTANCE, NBTDynamicOps.INSTANCE, p_176127_1_.getGeneratorOptions()));
      this.allowCommands = p_176127_1_.areCommandsAllowed();
   }

   public CompoundNBT cloneNBTCompound(@Nullable CompoundNBT p_76082_1_) {
      this.fixPlayerData();
      if (p_76082_1_ == null) {
         p_76082_1_ = this.playerData;
      }

      CompoundNBT compoundnbt = new CompoundNBT();
      this.updateTagCompound(compoundnbt, p_76082_1_);
      return compoundnbt;
   }

   private void updateTagCompound(CompoundNBT p_76064_1_, CompoundNBT p_76064_2_) {
      ListNBT listnbt = new ListNBT();
      this.field_230141_X_.stream().map(StringNBT::func_229705_a_).forEach(listnbt::add);
      p_76064_1_.put("ServerBrands", listnbt);
      p_76064_1_.putBoolean("WasModded", this.field_230142_Y_);
      CompoundNBT compoundnbt = new CompoundNBT();
      compoundnbt.putString("Name", SharedConstants.getVersion().getName());
      compoundnbt.putInt("Id", SharedConstants.getVersion().getWorldVersion());
      compoundnbt.putBoolean("Snapshot", !SharedConstants.getVersion().isStable());
      p_76064_1_.put("Version", compoundnbt);
      p_76064_1_.putInt("DataVersion", SharedConstants.getVersion().getWorldVersion());
      p_76064_1_.putLong("RandomSeed", this.randomSeed);
      p_76064_1_.putString("generatorName", this.generator.getSerialization());
      p_76064_1_.putInt("generatorVersion", this.generator.getVersion());
      if (!this.generatorOptions.isEmpty()) {
         p_76064_1_.put("generatorOptions", this.generatorOptions);
      }

      if (this.legacyCustomOptions != null) {
         p_76064_1_.putString("legacy_custom_options", this.legacyCustomOptions);
      }

      p_76064_1_.putInt("GameType", this.gameType.getID());
      p_76064_1_.putBoolean("MapFeatures", this.mapFeaturesEnabled);
      p_76064_1_.putInt("SpawnX", this.spawnX);
      p_76064_1_.putInt("SpawnY", this.spawnY);
      p_76064_1_.putInt("SpawnZ", this.spawnZ);
      p_76064_1_.putLong("Time", this.gameTime);
      p_76064_1_.putLong("DayTime", this.dayTime);
      p_76064_1_.putLong("SizeOnDisk", this.sizeOnDisk);
      p_76064_1_.putLong("LastPlayed", Util.millisecondsSinceEpoch());
      p_76064_1_.putString("LevelName", this.levelName);
      p_76064_1_.putInt("version", this.saveVersion);
      p_76064_1_.putInt("clearWeatherTime", this.clearWeatherTime);
      p_76064_1_.putInt("rainTime", this.rainTime);
      p_76064_1_.putBoolean("raining", this.raining);
      p_76064_1_.putInt("thunderTime", this.thunderTime);
      p_76064_1_.putBoolean("thundering", this.thundering);
      p_76064_1_.putBoolean("hardcore", this.hardcore);
      p_76064_1_.putBoolean("allowCommands", this.allowCommands);
      p_76064_1_.putBoolean("initialized", this.initialized);
      p_76064_1_.putDouble("BorderCenterX", this.borderCenterX);
      p_76064_1_.putDouble("BorderCenterZ", this.borderCenterZ);
      p_76064_1_.putDouble("BorderSize", this.borderSize);
      p_76064_1_.putLong("BorderSizeLerpTime", this.borderSizeLerpTime);
      p_76064_1_.putDouble("BorderSafeZone", this.borderSafeZone);
      p_76064_1_.putDouble("BorderDamagePerBlock", this.borderDamagePerBlock);
      p_76064_1_.putDouble("BorderSizeLerpTarget", this.borderSizeLerpTarget);
      p_76064_1_.putDouble("BorderWarningBlocks", (double)this.borderWarningBlocks);
      p_76064_1_.putDouble("BorderWarningTime", (double)this.borderWarningTime);
      if (this.difficulty != null) {
         p_76064_1_.putByte("Difficulty", (byte)this.difficulty.getId());
      }

      p_76064_1_.putBoolean("DifficultyLocked", this.difficultyLocked);
      p_76064_1_.put("GameRules", this.gameRules.write());
      CompoundNBT compoundnbt1 = new CompoundNBT();
      Iterator var6 = this.dimensionData.entrySet().iterator();

      while(var6.hasNext()) {
         Entry<DimensionType, CompoundNBT> entry = (Entry)var6.next();
         if (entry.getValue() != null && !((CompoundNBT)entry.getValue()).isEmpty()) {
            compoundnbt1.put(String.valueOf(((DimensionType)entry.getKey()).getId()), (INBT)entry.getValue());
         }
      }

      p_76064_1_.put("DimensionData", compoundnbt1);
      if (p_76064_2_ != null) {
         p_76064_1_.put("Player", p_76064_2_);
      }

      CompoundNBT compoundnbt2 = new CompoundNBT();
      ListNBT listnbt1 = new ListNBT();
      Iterator var8 = this.enabledDataPacks.iterator();

      while(var8.hasNext()) {
         String s = (String)var8.next();
         listnbt1.add(StringNBT.func_229705_a_(s));
      }

      compoundnbt2.put("Enabled", listnbt1);
      ListNBT listnbt2 = new ListNBT();
      Iterator var14 = this.disabledDataPacks.iterator();

      while(var14.hasNext()) {
         String s1 = (String)var14.next();
         listnbt2.add(StringNBT.func_229705_a_(s1));
      }

      compoundnbt2.put("Disabled", listnbt2);
      p_76064_1_.put("DataPacks", compoundnbt2);
      if (this.customBossEvents != null) {
         p_76064_1_.put("CustomBossEvents", this.customBossEvents);
      }

      p_76064_1_.put("ScheduledEvents", this.scheduledEvents.write());
      p_76064_1_.putInt("WanderingTraderSpawnDelay", this.wanderingTraderSpawnDelay);
      p_76064_1_.putInt("WanderingTraderSpawnChance", this.wanderingTraderSpawnChance);
      if (this.wanderingTraderId != null) {
         p_76064_1_.putString("WanderingTraderId", this.wanderingTraderId.toString());
      }

   }

   public long getSeed() {
      return this.randomSeed;
   }

   public static long func_227498_c_(long p_227498_0_) {
      return Hashing.sha256().hashLong(p_227498_0_).asLong();
   }

   public int getSpawnX() {
      return this.spawnX;
   }

   public int getSpawnY() {
      return this.spawnY;
   }

   public int getSpawnZ() {
      return this.spawnZ;
   }

   public long getGameTime() {
      return this.gameTime;
   }

   public long getDayTime() {
      return this.dayTime;
   }

   private void fixPlayerData() {
      if (!this.playerDataFixed && this.playerData != null) {
         if (this.dataVersion < SharedConstants.getVersion().getWorldVersion()) {
            if (this.fixer == null) {
               throw (NullPointerException)Util.func_229757_c_(new NullPointerException("Fixer Upper not set inside LevelData, and the player tag is not upgraded."));
            }

            this.playerData = NBTUtil.update(this.fixer, DefaultTypeReferences.PLAYER, this.playerData, this.dataVersion);
         }

         this.playerDataFixed = true;
      }

   }

   public CompoundNBT getPlayerNBTTagCompound() {
      this.fixPlayerData();
      return this.playerData;
   }

   @OnlyIn(Dist.CLIENT)
   public void setSpawnX(int p_76058_1_) {
      this.spawnX = p_76058_1_;
   }

   @OnlyIn(Dist.CLIENT)
   public void setSpawnY(int p_76056_1_) {
      this.spawnY = p_76056_1_;
   }

   @OnlyIn(Dist.CLIENT)
   public void setSpawnZ(int p_76087_1_) {
      this.spawnZ = p_76087_1_;
   }

   public void setGameTime(long p_82572_1_) {
      this.gameTime = p_82572_1_;
   }

   public void setDayTime(long p_76068_1_) {
      this.dayTime = p_76068_1_;
   }

   public void setSpawn(BlockPos p_176143_1_) {
      this.spawnX = p_176143_1_.getX();
      this.spawnY = p_176143_1_.getY();
      this.spawnZ = p_176143_1_.getZ();
   }

   public String getWorldName() {
      return this.levelName;
   }

   public void setWorldName(String p_76062_1_) {
      this.levelName = p_76062_1_;
   }

   public int getSaveVersion() {
      return this.saveVersion;
   }

   public void setSaveVersion(int p_76078_1_) {
      this.saveVersion = p_76078_1_;
   }

   @OnlyIn(Dist.CLIENT)
   public long getLastTimePlayed() {
      return this.lastTimePlayed;
   }

   public int getClearWeatherTime() {
      return this.clearWeatherTime;
   }

   public void setClearWeatherTime(int p_176142_1_) {
      this.clearWeatherTime = p_176142_1_;
   }

   public boolean isThundering() {
      return this.thundering;
   }

   public void setThundering(boolean p_76069_1_) {
      this.thundering = p_76069_1_;
   }

   public int getThunderTime() {
      return this.thunderTime;
   }

   public void setThunderTime(int p_76090_1_) {
      this.thunderTime = p_76090_1_;
   }

   public boolean isRaining() {
      return this.raining;
   }

   public void setRaining(boolean p_76084_1_) {
      this.raining = p_76084_1_;
   }

   public int getRainTime() {
      return this.rainTime;
   }

   public void setRainTime(int p_76080_1_) {
      this.rainTime = p_76080_1_;
   }

   public GameType getGameType() {
      return this.gameType;
   }

   public boolean isMapFeaturesEnabled() {
      return this.mapFeaturesEnabled;
   }

   public void setMapFeaturesEnabled(boolean p_176128_1_) {
      this.mapFeaturesEnabled = p_176128_1_;
   }

   public void setGameType(GameType p_76060_1_) {
      this.gameType = p_76060_1_;
   }

   public boolean isHardcore() {
      return this.hardcore;
   }

   public void setHardcore(boolean p_176119_1_) {
      this.hardcore = p_176119_1_;
   }

   public WorldType getGenerator() {
      return this.generator;
   }

   public void setGenerator(WorldType p_76085_1_) {
      this.generator = p_76085_1_;
   }

   public CompoundNBT getGeneratorOptions() {
      return this.generatorOptions;
   }

   public void setGeneratorOptions(CompoundNBT p_212242_1_) {
      this.generatorOptions = p_212242_1_;
   }

   public boolean areCommandsAllowed() {
      return this.allowCommands;
   }

   public void setAllowCommands(boolean p_176121_1_) {
      this.allowCommands = p_176121_1_;
   }

   public boolean isInitialized() {
      return this.initialized;
   }

   public void setInitialized(boolean p_76091_1_) {
      this.initialized = p_76091_1_;
   }

   public GameRules getGameRulesInstance() {
      return this.gameRules;
   }

   public double getBorderCenterX() {
      return this.borderCenterX;
   }

   public double getBorderCenterZ() {
      return this.borderCenterZ;
   }

   public double getBorderSize() {
      return this.borderSize;
   }

   public void setBorderSize(double p_176145_1_) {
      this.borderSize = p_176145_1_;
   }

   public long getBorderSizeLerpTime() {
      return this.borderSizeLerpTime;
   }

   public void setBorderSizeLerpTime(long p_176135_1_) {
      this.borderSizeLerpTime = p_176135_1_;
   }

   public double getBorderSizeLerpTarget() {
      return this.borderSizeLerpTarget;
   }

   public void setBorderSizeLerpTarget(double p_176118_1_) {
      this.borderSizeLerpTarget = p_176118_1_;
   }

   public void setBorderCenterZ(double p_176141_1_) {
      this.borderCenterZ = p_176141_1_;
   }

   public void setBorderCenterX(double p_176124_1_) {
      this.borderCenterX = p_176124_1_;
   }

   public double getBorderSafeZone() {
      return this.borderSafeZone;
   }

   public void setBorderSafeZone(double p_176129_1_) {
      this.borderSafeZone = p_176129_1_;
   }

   public double getBorderDamagePerBlock() {
      return this.borderDamagePerBlock;
   }

   public void setBorderDamagePerBlock(double p_176125_1_) {
      this.borderDamagePerBlock = p_176125_1_;
   }

   public int getBorderWarningBlocks() {
      return this.borderWarningBlocks;
   }

   public int getBorderWarningTime() {
      return this.borderWarningTime;
   }

   public void setBorderWarningBlocks(int p_176122_1_) {
      this.borderWarningBlocks = p_176122_1_;
   }

   public void setBorderWarningTime(int p_176136_1_) {
      this.borderWarningTime = p_176136_1_;
   }

   public Difficulty getDifficulty() {
      return this.difficulty;
   }

   public void setDifficulty(Difficulty p_176144_1_) {
      this.difficulty = p_176144_1_;
   }

   public boolean isDifficultyLocked() {
      return this.difficultyLocked;
   }

   public void setDifficultyLocked(boolean p_180783_1_) {
      this.difficultyLocked = p_180783_1_;
   }

   public TimerCallbackManager<MinecraftServer> getScheduledEvents() {
      return this.scheduledEvents;
   }

   public void addToCrashReport(CrashReportCategory p_85118_1_) {
      p_85118_1_.addDetail("Level name", () -> {
         return this.levelName;
      });
      p_85118_1_.addDetail("Level seed", () -> {
         return String.valueOf(this.randomSeed);
      });
      p_85118_1_.addDetail("Level generator", () -> {
         return String.format("ID %02d - %s, ver %d. Features enabled: %b", this.generator.getId(), this.generator.getName(), this.generator.getVersion(), this.mapFeaturesEnabled);
      });
      p_85118_1_.addDetail("Level generator options", () -> {
         return this.generatorOptions.toString();
      });
      p_85118_1_.addDetail("Level spawn location", () -> {
         return CrashReportCategory.getCoordinateInfo(this.spawnX, this.spawnY, this.spawnZ);
      });
      p_85118_1_.addDetail("Level time", () -> {
         return String.format("%d game time, %d day time", this.gameTime, this.dayTime);
      });
      p_85118_1_.addDetail("Known server brands", () -> {
         return String.join(", ", this.field_230141_X_);
      });
      p_85118_1_.addDetail("Level was modded", () -> {
         return Boolean.toString(this.field_230142_Y_);
      });
      p_85118_1_.addDetail("Level storage version", () -> {
         String s = "Unknown?";

         try {
            switch(this.saveVersion) {
            case 19132:
               s = "McRegion";
               break;
            case 19133:
               s = "Anvil";
            }
         } catch (Throwable var3) {
         }

         return String.format("0x%05X - %s", this.saveVersion, s);
      });
      p_85118_1_.addDetail("Level weather", () -> {
         return String.format("Rain time: %d (now: %b), thunder time: %d (now: %b)", this.rainTime, this.raining, this.thunderTime, this.thundering);
      });
      p_85118_1_.addDetail("Level game mode", () -> {
         return String.format("Game mode: %s (ID %d). Hardcore: %b. Cheats: %b", this.gameType.getName(), this.gameType.getID(), this.hardcore, this.allowCommands);
      });
   }

   public CompoundNBT getDimensionData(DimensionType p_186347_1_) {
      CompoundNBT compoundnbt = (CompoundNBT)this.dimensionData.get(p_186347_1_);
      return compoundnbt == null ? new CompoundNBT() : compoundnbt;
   }

   public void setDimensionData(DimensionType p_186345_1_, CompoundNBT p_186345_2_) {
      this.dimensionData.put(p_186345_1_, p_186345_2_);
   }

   @OnlyIn(Dist.CLIENT)
   public int getVersionId() {
      return this.versionId;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean isVersionSnapshot() {
      return this.versionSnapshot;
   }

   @OnlyIn(Dist.CLIENT)
   public String getVersionName() {
      return this.versionName;
   }

   public Set<String> getDisabledDataPacks() {
      return this.disabledDataPacks;
   }

   public Set<String> getEnabledDataPacks() {
      return this.enabledDataPacks;
   }

   @Nullable
   public CompoundNBT getCustomBossEvents() {
      return this.customBossEvents;
   }

   public void setCustomBossEvents(@Nullable CompoundNBT p_201356_1_) {
      this.customBossEvents = p_201356_1_;
   }

   public int getWanderingTraderSpawnDelay() {
      return this.wanderingTraderSpawnDelay;
   }

   public void setWanderingTraderSpawnDelay(int p_215764_1_) {
      this.wanderingTraderSpawnDelay = p_215764_1_;
   }

   public int getWanderingTraderSpawnChance() {
      return this.wanderingTraderSpawnChance;
   }

   public void setWanderingTraderSpawnChance(int p_215762_1_) {
      this.wanderingTraderSpawnChance = p_215762_1_;
   }

   public void setWanderingTraderId(UUID p_215761_1_) {
      this.wanderingTraderId = p_215761_1_;
   }

   public void func_230145_a_(String p_230145_1_, boolean p_230145_2_) {
      this.field_230141_X_.add(p_230145_1_);
      this.field_230142_Y_ |= p_230145_2_;
   }

   static {
      DEFAULT_DIFFICULTY = Difficulty.NORMAL;
   }
}
