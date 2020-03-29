package net.minecraft.client.tutorial;

import java.util.function.Function;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public enum TutorialSteps {
   MOVEMENT("movement", MovementStep::new),
   FIND_TREE("find_tree", FindTreeStep::new),
   PUNCH_TREE("punch_tree", PunchTreeStep::new),
   OPEN_INVENTORY("open_inventory", OpenInventoryStep::new),
   CRAFT_PLANKS("craft_planks", CraftPlanksStep::new),
   NONE("none", CompletedTutorialStep::new);

   private final String name;
   private final Function<Tutorial, ? extends ITutorialStep> tutorial;

   private <T extends ITutorialStep> TutorialSteps(String p_i47577_3_, Function<Tutorial, T> p_i47577_4_) {
      this.name = p_i47577_3_;
      this.tutorial = p_i47577_4_;
   }

   public ITutorialStep create(Tutorial p_193309_1_) {
      return (ITutorialStep)this.tutorial.apply(p_193309_1_);
   }

   public String getName() {
      return this.name;
   }

   public static TutorialSteps byName(String p_193307_0_) {
      TutorialSteps[] var1 = values();
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         TutorialSteps lvt_4_1_ = var1[var3];
         if (lvt_4_1_.name.equals(p_193307_0_)) {
            return lvt_4_1_;
         }
      }

      return NONE;
   }
}
