package net.minecraft.util.datafix.fixes;

import com.google.common.collect.Sets;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import java.util.Optional;
import java.util.Set;
import net.minecraft.util.datafix.TypeReferences;

public class EntityHealth extends DataFix {
   private static final Set<String> ENTITY_LIST = Sets.newHashSet(new String[]{"ArmorStand", "Bat", "Blaze", "CaveSpider", "Chicken", "Cow", "Creeper", "EnderDragon", "Enderman", "Endermite", "EntityHorse", "Ghast", "Giant", "Guardian", "LavaSlime", "MushroomCow", "Ozelot", "Pig", "PigZombie", "Rabbit", "Sheep", "Shulker", "Silverfish", "Skeleton", "Slime", "SnowMan", "Spider", "Squid", "Villager", "VillagerGolem", "Witch", "WitherBoss", "Wolf", "Zombie"});

   public EntityHealth(Schema p_i49666_1_, boolean p_i49666_2_) {
      super(p_i49666_1_, p_i49666_2_);
   }

   public Dynamic<?> fixTag(Dynamic<?> p_209743_1_) {
      Optional<Number> lvt_3_1_ = p_209743_1_.get("HealF").asNumber();
      Optional<Number> lvt_4_1_ = p_209743_1_.get("Health").asNumber();
      float lvt_2_3_;
      if (lvt_3_1_.isPresent()) {
         lvt_2_3_ = ((Number)lvt_3_1_.get()).floatValue();
         p_209743_1_ = p_209743_1_.remove("HealF");
      } else {
         if (!lvt_4_1_.isPresent()) {
            return p_209743_1_;
         }

         lvt_2_3_ = ((Number)lvt_4_1_.get()).floatValue();
      }

      return p_209743_1_.set("Health", p_209743_1_.createFloat(lvt_2_3_));
   }

   public TypeRewriteRule makeRule() {
      return this.fixTypeEverywhereTyped("EntityHealthFix", this.getInputSchema().getType(TypeReferences.ENTITY), (p_207449_1_) -> {
         return p_207449_1_.update(DSL.remainderFinder(), this::fixTag);
      });
   }
}
