package net.minecraft.world.storage;

import javax.annotation.Nullable;
import net.minecraft.command.TimerCallbackManager;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameRules;
import net.minecraft.world.GameType;
import net.minecraft.world.WorldType;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class DerivedWorldInfo extends WorldInfo {
   private final WorldInfo delegate;

   public DerivedWorldInfo(WorldInfo p_i2145_1_) {
      this.delegate = p_i2145_1_;
   }

   public CompoundNBT cloneNBTCompound(@Nullable CompoundNBT p_76082_1_) {
      return this.delegate.cloneNBTCompound(p_76082_1_);
   }

   public long getSeed() {
      return this.delegate.getSeed();
   }

   public int getSpawnX() {
      return this.delegate.getSpawnX();
   }

   public int getSpawnY() {
      return this.delegate.getSpawnY();
   }

   public int getSpawnZ() {
      return this.delegate.getSpawnZ();
   }

   public long getGameTime() {
      return this.delegate.getGameTime();
   }

   public long getDayTime() {
      return this.delegate.getDayTime();
   }

   public CompoundNBT getPlayerNBTTagCompound() {
      return this.delegate.getPlayerNBTTagCompound();
   }

   public String getWorldName() {
      return this.delegate.getWorldName();
   }

   public int getSaveVersion() {
      return this.delegate.getSaveVersion();
   }

   @OnlyIn(Dist.CLIENT)
   public long getLastTimePlayed() {
      return this.delegate.getLastTimePlayed();
   }

   public boolean isThundering() {
      return this.delegate.isThundering();
   }

   public int getThunderTime() {
      return this.delegate.getThunderTime();
   }

   public boolean isRaining() {
      return this.delegate.isRaining();
   }

   public int getRainTime() {
      return this.delegate.getRainTime();
   }

   public GameType getGameType() {
      return this.delegate.getGameType();
   }

   @OnlyIn(Dist.CLIENT)
   public void setSpawnX(int p_76058_1_) {
   }

   @OnlyIn(Dist.CLIENT)
   public void setSpawnY(int p_76056_1_) {
   }

   @OnlyIn(Dist.CLIENT)
   public void setSpawnZ(int p_76087_1_) {
   }

   public void setGameTime(long p_82572_1_) {
   }

   public void setDayTime(long p_76068_1_) {
   }

   public void setSpawn(BlockPos p_176143_1_) {
   }

   public void setWorldName(String p_76062_1_) {
   }

   public void setSaveVersion(int p_76078_1_) {
   }

   public void setThundering(boolean p_76069_1_) {
   }

   public void setThunderTime(int p_76090_1_) {
   }

   public void setRaining(boolean p_76084_1_) {
   }

   public void setRainTime(int p_76080_1_) {
   }

   public boolean isMapFeaturesEnabled() {
      return this.delegate.isMapFeaturesEnabled();
   }

   public boolean isHardcore() {
      return this.delegate.isHardcore();
   }

   public WorldType getGenerator() {
      return this.delegate.getGenerator();
   }

   public void setGenerator(WorldType p_76085_1_) {
   }

   public boolean areCommandsAllowed() {
      return this.delegate.areCommandsAllowed();
   }

   public void setAllowCommands(boolean p_176121_1_) {
   }

   public boolean isInitialized() {
      return this.delegate.isInitialized();
   }

   public void setInitialized(boolean p_76091_1_) {
   }

   public GameRules getGameRulesInstance() {
      return this.delegate.getGameRulesInstance();
   }

   public Difficulty getDifficulty() {
      return this.delegate.getDifficulty();
   }

   public void setDifficulty(Difficulty p_176144_1_) {
   }

   public boolean isDifficultyLocked() {
      return this.delegate.isDifficultyLocked();
   }

   public void setDifficultyLocked(boolean p_180783_1_) {
   }

   public TimerCallbackManager<MinecraftServer> getScheduledEvents() {
      return this.delegate.getScheduledEvents();
   }

   public void setDimensionData(DimensionType p_186345_1_, CompoundNBT p_186345_2_) {
      this.delegate.setDimensionData(p_186345_1_, p_186345_2_);
   }

   public CompoundNBT getDimensionData(DimensionType p_186347_1_) {
      return this.delegate.getDimensionData(p_186347_1_);
   }

   public void addToCrashReport(CrashReportCategory p_85118_1_) {
      p_85118_1_.addDetail("Derived", (Object)true);
      this.delegate.addToCrashReport(p_85118_1_);
   }
}
