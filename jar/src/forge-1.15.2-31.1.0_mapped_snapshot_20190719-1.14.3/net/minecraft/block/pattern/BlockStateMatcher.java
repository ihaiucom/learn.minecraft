package net.minecraft.block.pattern;

import com.google.common.collect.Maps;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.IProperty;
import net.minecraft.state.StateContainer;

public class BlockStateMatcher implements Predicate<BlockState> {
   public static final Predicate<BlockState> ANY = (p_201026_0_) -> {
      return true;
   };
   private final StateContainer<Block, BlockState> blockstate;
   private final Map<IProperty<?>, Predicate<Object>> propertyPredicates = Maps.newHashMap();

   private BlockStateMatcher(StateContainer<Block, BlockState> p_i45653_1_) {
      this.blockstate = p_i45653_1_;
   }

   public static BlockStateMatcher forBlock(Block p_177638_0_) {
      return new BlockStateMatcher(p_177638_0_.getStateContainer());
   }

   public boolean test(@Nullable BlockState p_test_1_) {
      if (p_test_1_ != null && p_test_1_.getBlock().equals(this.blockstate.getOwner())) {
         if (this.propertyPredicates.isEmpty()) {
            return true;
         } else {
            Iterator var2 = this.propertyPredicates.entrySet().iterator();

            Entry lvt_3_1_;
            do {
               if (!var2.hasNext()) {
                  return true;
               }

               lvt_3_1_ = (Entry)var2.next();
            } while(this.matches(p_test_1_, (IProperty)lvt_3_1_.getKey(), (Predicate)lvt_3_1_.getValue()));

            return false;
         }
      } else {
         return false;
      }
   }

   protected <T extends Comparable<T>> boolean matches(BlockState p_185927_1_, IProperty<T> p_185927_2_, Predicate<Object> p_185927_3_) {
      T lvt_4_1_ = p_185927_1_.get(p_185927_2_);
      return p_185927_3_.test(lvt_4_1_);
   }

   public <V extends Comparable<V>> BlockStateMatcher where(IProperty<V> p_201028_1_, Predicate<Object> p_201028_2_) {
      if (!this.blockstate.getProperties().contains(p_201028_1_)) {
         throw new IllegalArgumentException(this.blockstate + " cannot support property " + p_201028_1_);
      } else {
         this.propertyPredicates.put(p_201028_1_, p_201028_2_);
         return this;
      }
   }

   // $FF: synthetic method
   public boolean test(@Nullable Object p_test_1_) {
      return this.test((BlockState)p_test_1_);
   }
}
