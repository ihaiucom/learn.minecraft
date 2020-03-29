package net.minecraft.advancements.criterion;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.advancements.ICriterionInstance;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;

public class ShotCrossbowTrigger extends AbstractCriterionTrigger<ShotCrossbowTrigger.Instance> {
   private static final ResourceLocation ID = new ResourceLocation("shot_crossbow");

   public ResourceLocation getId() {
      return ID;
   }

   public ShotCrossbowTrigger.Instance deserializeInstance(JsonObject p_192166_1_, JsonDeserializationContext p_192166_2_) {
      ItemPredicate lvt_3_1_ = ItemPredicate.deserialize(p_192166_1_.get("item"));
      return new ShotCrossbowTrigger.Instance(lvt_3_1_);
   }

   public void func_215111_a(ServerPlayerEntity p_215111_1_, ItemStack p_215111_2_) {
      this.func_227070_a_(p_215111_1_.getAdvancements(), (p_227037_1_) -> {
         return p_227037_1_.func_215121_a(p_215111_2_);
      });
   }

   // $FF: synthetic method
   public ICriterionInstance deserializeInstance(JsonObject p_192166_1_, JsonDeserializationContext p_192166_2_) {
      return this.deserializeInstance(p_192166_1_, p_192166_2_);
   }

   public static class Instance extends CriterionInstance {
      private final ItemPredicate field_215123_a;

      public Instance(ItemPredicate p_i50604_1_) {
         super(ShotCrossbowTrigger.ID);
         this.field_215123_a = p_i50604_1_;
      }

      public static ShotCrossbowTrigger.Instance func_215122_a(IItemProvider p_215122_0_) {
         return new ShotCrossbowTrigger.Instance(ItemPredicate.Builder.create().item(p_215122_0_).build());
      }

      public boolean func_215121_a(ItemStack p_215121_1_) {
         return this.field_215123_a.test(p_215121_1_);
      }

      public JsonElement serialize() {
         JsonObject lvt_1_1_ = new JsonObject();
         lvt_1_1_.add("item", this.field_215123_a.serialize());
         return lvt_1_1_;
      }
   }
}
