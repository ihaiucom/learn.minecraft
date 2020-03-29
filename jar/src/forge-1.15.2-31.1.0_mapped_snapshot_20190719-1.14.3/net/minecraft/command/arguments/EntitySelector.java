package net.minecraft.command.arguments;

import com.google.common.collect.Lists;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.advancements.criterion.MinMaxBounds;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentUtils;
import net.minecraft.world.server.ServerWorld;

public class EntitySelector {
   private final int limit;
   private final boolean includeNonPlayers;
   private final boolean currentWorldOnly;
   private final Predicate<Entity> filter;
   private final MinMaxBounds.FloatBound distance;
   private final Function<Vec3d, Vec3d> positionGetter;
   @Nullable
   private final AxisAlignedBB aabb;
   private final BiConsumer<Vec3d, List<? extends Entity>> sorter;
   private final boolean self;
   @Nullable
   private final String username;
   @Nullable
   private final UUID uuid;
   @Nullable
   private final EntityType<?> type;
   private final boolean checkPermission;

   public EntitySelector(int p_i50800_1_, boolean p_i50800_2_, boolean p_i50800_3_, Predicate<Entity> p_i50800_4_, MinMaxBounds.FloatBound p_i50800_5_, Function<Vec3d, Vec3d> p_i50800_6_, @Nullable AxisAlignedBB p_i50800_7_, BiConsumer<Vec3d, List<? extends Entity>> p_i50800_8_, boolean p_i50800_9_, @Nullable String p_i50800_10_, @Nullable UUID p_i50800_11_, @Nullable EntityType<?> p_i50800_12_, boolean p_i50800_13_) {
      this.limit = p_i50800_1_;
      this.includeNonPlayers = p_i50800_2_;
      this.currentWorldOnly = p_i50800_3_;
      this.filter = p_i50800_4_;
      this.distance = p_i50800_5_;
      this.positionGetter = p_i50800_6_;
      this.aabb = p_i50800_7_;
      this.sorter = p_i50800_8_;
      this.self = p_i50800_9_;
      this.username = p_i50800_10_;
      this.uuid = p_i50800_11_;
      this.type = p_i50800_12_;
      this.checkPermission = p_i50800_13_;
   }

   public int getLimit() {
      return this.limit;
   }

   public boolean includesEntities() {
      return this.includeNonPlayers;
   }

   public boolean isSelfSelector() {
      return this.self;
   }

   public boolean isWorldLimited() {
      return this.currentWorldOnly;
   }

   private void checkPermission(CommandSource p_210324_1_) throws CommandSyntaxException {
      if (this.checkPermission && !p_210324_1_.hasPermissionLevel(2)) {
         throw EntityArgument.SELECTOR_NOT_ALLOWED.create();
      }
   }

   public Entity selectOne(CommandSource p_197340_1_) throws CommandSyntaxException {
      this.checkPermission(p_197340_1_);
      List<? extends Entity> lvt_2_1_ = this.select(p_197340_1_);
      if (lvt_2_1_.isEmpty()) {
         throw EntityArgument.ENTITY_NOT_FOUND.create();
      } else if (lvt_2_1_.size() > 1) {
         throw EntityArgument.TOO_MANY_ENTITIES.create();
      } else {
         return (Entity)lvt_2_1_.get(0);
      }
   }

   public List<? extends Entity> select(CommandSource p_197341_1_) throws CommandSyntaxException {
      this.checkPermission(p_197341_1_);
      if (!this.includeNonPlayers) {
         return this.selectPlayers(p_197341_1_);
      } else if (this.username != null) {
         ServerPlayerEntity lvt_2_1_ = p_197341_1_.getServer().getPlayerList().getPlayerByUsername(this.username);
         return (List)(lvt_2_1_ == null ? Collections.emptyList() : Lists.newArrayList(new ServerPlayerEntity[]{lvt_2_1_}));
      } else if (this.uuid != null) {
         Iterator var7 = p_197341_1_.getServer().getWorlds().iterator();

         Entity lvt_4_1_;
         do {
            if (!var7.hasNext()) {
               return Collections.emptyList();
            }

            ServerWorld lvt_3_1_ = (ServerWorld)var7.next();
            lvt_4_1_ = lvt_3_1_.getEntityByUuid(this.uuid);
         } while(lvt_4_1_ == null);

         return Lists.newArrayList(new Entity[]{lvt_4_1_});
      } else {
         Vec3d lvt_2_2_ = (Vec3d)this.positionGetter.apply(p_197341_1_.getPos());
         Predicate<Entity> lvt_3_2_ = this.updateFilter(lvt_2_2_);
         if (this.self) {
            return (List)(p_197341_1_.getEntity() != null && lvt_3_2_.test(p_197341_1_.getEntity()) ? Lists.newArrayList(new Entity[]{p_197341_1_.getEntity()}) : Collections.emptyList());
         } else {
            List<Entity> lvt_4_2_ = Lists.newArrayList();
            if (this.isWorldLimited()) {
               this.getEntities(lvt_4_2_, p_197341_1_.getWorld(), lvt_2_2_, lvt_3_2_);
            } else {
               Iterator var5 = p_197341_1_.getServer().getWorlds().iterator();

               while(var5.hasNext()) {
                  ServerWorld lvt_6_1_ = (ServerWorld)var5.next();
                  this.getEntities(lvt_4_2_, lvt_6_1_, lvt_2_2_, lvt_3_2_);
               }
            }

            return this.sortAndLimit(lvt_2_2_, lvt_4_2_);
         }
      }
   }

   private void getEntities(List<Entity> p_197348_1_, ServerWorld p_197348_2_, Vec3d p_197348_3_, Predicate<Entity> p_197348_4_) {
      if (this.aabb != null) {
         p_197348_1_.addAll(p_197348_2_.getEntitiesWithinAABB(this.type, this.aabb.offset(p_197348_3_), p_197348_4_));
      } else {
         p_197348_1_.addAll(p_197348_2_.getEntities(this.type, p_197348_4_));
      }

   }

   public ServerPlayerEntity selectOnePlayer(CommandSource p_197347_1_) throws CommandSyntaxException {
      this.checkPermission(p_197347_1_);
      List<ServerPlayerEntity> lvt_2_1_ = this.selectPlayers(p_197347_1_);
      if (lvt_2_1_.size() != 1) {
         throw EntityArgument.PLAYER_NOT_FOUND.create();
      } else {
         return (ServerPlayerEntity)lvt_2_1_.get(0);
      }
   }

   public List<ServerPlayerEntity> selectPlayers(CommandSource p_197342_1_) throws CommandSyntaxException {
      this.checkPermission(p_197342_1_);
      ServerPlayerEntity lvt_2_2_;
      if (this.username != null) {
         lvt_2_2_ = p_197342_1_.getServer().getPlayerList().getPlayerByUsername(this.username);
         return (List)(lvt_2_2_ == null ? Collections.emptyList() : Lists.newArrayList(new ServerPlayerEntity[]{lvt_2_2_}));
      } else if (this.uuid != null) {
         lvt_2_2_ = p_197342_1_.getServer().getPlayerList().getPlayerByUUID(this.uuid);
         return (List)(lvt_2_2_ == null ? Collections.emptyList() : Lists.newArrayList(new ServerPlayerEntity[]{lvt_2_2_}));
      } else {
         Vec3d lvt_2_3_ = (Vec3d)this.positionGetter.apply(p_197342_1_.getPos());
         Predicate<Entity> lvt_3_1_ = this.updateFilter(lvt_2_3_);
         if (this.self) {
            if (p_197342_1_.getEntity() instanceof ServerPlayerEntity) {
               ServerPlayerEntity lvt_4_1_ = (ServerPlayerEntity)p_197342_1_.getEntity();
               if (lvt_3_1_.test(lvt_4_1_)) {
                  return Lists.newArrayList(new ServerPlayerEntity[]{lvt_4_1_});
               }
            }

            return Collections.emptyList();
         } else {
            Object lvt_4_3_;
            if (this.isWorldLimited()) {
               ServerWorld var10000 = p_197342_1_.getWorld();
               lvt_3_1_.getClass();
               lvt_4_3_ = var10000.getPlayers(lvt_3_1_::test);
            } else {
               lvt_4_3_ = Lists.newArrayList();
               Iterator var5 = p_197342_1_.getServer().getPlayerList().getPlayers().iterator();

               while(var5.hasNext()) {
                  ServerPlayerEntity lvt_6_1_ = (ServerPlayerEntity)var5.next();
                  if (lvt_3_1_.test(lvt_6_1_)) {
                     ((List)lvt_4_3_).add(lvt_6_1_);
                  }
               }
            }

            return this.sortAndLimit(lvt_2_3_, (List)lvt_4_3_);
         }
      }
   }

   private Predicate<Entity> updateFilter(Vec3d p_197349_1_) {
      Predicate<Entity> lvt_2_1_ = this.filter;
      if (this.aabb != null) {
         AxisAlignedBB lvt_3_1_ = this.aabb.offset(p_197349_1_);
         lvt_2_1_ = lvt_2_1_.and((p_197344_1_) -> {
            return lvt_3_1_.intersects(p_197344_1_.getBoundingBox());
         });
      }

      if (!this.distance.isUnbounded()) {
         lvt_2_1_ = lvt_2_1_.and((p_211376_2_) -> {
            return this.distance.testSquared(p_211376_2_.getDistanceSq(p_197349_1_));
         });
      }

      return lvt_2_1_;
   }

   private <T extends Entity> List<T> sortAndLimit(Vec3d p_197345_1_, List<T> p_197345_2_) {
      if (p_197345_2_.size() > 1) {
         this.sorter.accept(p_197345_1_, p_197345_2_);
      }

      return p_197345_2_.subList(0, Math.min(this.limit, p_197345_2_.size()));
   }

   public static ITextComponent joinNames(List<? extends Entity> p_197350_0_) {
      return TextComponentUtils.makeList(p_197350_0_, Entity::getDisplayName);
   }
}
