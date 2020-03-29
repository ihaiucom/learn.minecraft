package net.minecraft.client.renderer.model.multipart;

import com.google.common.base.MoreObjects;
import com.google.common.base.Splitter;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.IProperty;
import net.minecraft.state.StateContainer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class PropertyValueCondition implements ICondition {
   private static final Splitter SPLITTER = Splitter.on('|').omitEmptyStrings();
   private final String key;
   private final String value;

   public PropertyValueCondition(String p_i46565_1_, String p_i46565_2_) {
      this.key = p_i46565_1_;
      this.value = p_i46565_2_;
   }

   public Predicate<BlockState> getPredicate(StateContainer<Block, BlockState> p_getPredicate_1_) {
      IProperty<?> lvt_2_1_ = p_getPredicate_1_.getProperty(this.key);
      if (lvt_2_1_ == null) {
         throw new RuntimeException(String.format("Unknown property '%s' on '%s'", this.key, ((Block)p_getPredicate_1_.getOwner()).toString()));
      } else {
         String lvt_3_1_ = this.value;
         boolean lvt_4_1_ = !lvt_3_1_.isEmpty() && lvt_3_1_.charAt(0) == '!';
         if (lvt_4_1_) {
            lvt_3_1_ = lvt_3_1_.substring(1);
         }

         List<String> lvt_5_1_ = SPLITTER.splitToList(lvt_3_1_);
         if (lvt_5_1_.isEmpty()) {
            throw new RuntimeException(String.format("Empty value '%s' for property '%s' on '%s'", this.value, this.key, ((Block)p_getPredicate_1_.getOwner()).toString()));
         } else {
            Predicate lvt_6_2_;
            if (lvt_5_1_.size() == 1) {
               lvt_6_2_ = this.func_212485_a(p_getPredicate_1_, lvt_2_1_, lvt_3_1_);
            } else {
               List<Predicate<BlockState>> lvt_7_1_ = (List)lvt_5_1_.stream().map((p_212482_3_) -> {
                  return this.func_212485_a(p_getPredicate_1_, lvt_2_1_, p_212482_3_);
               }).collect(Collectors.toList());
               lvt_6_2_ = (p_200687_1_) -> {
                  return lvt_7_1_.stream().anyMatch((p_200685_1_) -> {
                     return p_200685_1_.test(p_200687_1_);
                  });
               };
            }

            return lvt_4_1_ ? lvt_6_2_.negate() : lvt_6_2_;
         }
      }
   }

   private Predicate<BlockState> func_212485_a(StateContainer<Block, BlockState> p_212485_1_, IProperty<?> p_212485_2_, String p_212485_3_) {
      Optional<?> lvt_4_1_ = p_212485_2_.parseValue(p_212485_3_);
      if (!lvt_4_1_.isPresent()) {
         throw new RuntimeException(String.format("Unknown value '%s' for property '%s' on '%s' in '%s'", p_212485_3_, this.key, ((Block)p_212485_1_.getOwner()).toString(), this.value));
      } else {
         return (p_212483_2_) -> {
            return p_212483_2_.get(p_212485_2_).equals(lvt_4_1_.get());
         };
      }
   }

   public String toString() {
      return MoreObjects.toStringHelper(this).add("key", this.key).add("value", this.value).toString();
   }
}
