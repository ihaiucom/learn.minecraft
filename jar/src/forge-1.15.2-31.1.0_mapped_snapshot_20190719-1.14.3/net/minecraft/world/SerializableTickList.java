package net.minecraft.world;

import com.google.common.collect.Sets;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Stream;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerTickList;

public class SerializableTickList<T> implements ITickList<T> {
   private final Set<NextTickListEntry<T>> field_219500_a;
   private final Function<T, ResourceLocation> field_219501_b;

   public SerializableTickList(Function<T, ResourceLocation> p_i50010_1_, List<NextTickListEntry<T>> p_i50010_2_) {
      this(p_i50010_1_, (Set)Sets.newHashSet(p_i50010_2_));
   }

   private SerializableTickList(Function<T, ResourceLocation> p_i51499_1_, Set<NextTickListEntry<T>> p_i51499_2_) {
      this.field_219500_a = p_i51499_2_;
      this.field_219501_b = p_i51499_1_;
   }

   public boolean isTickScheduled(BlockPos p_205359_1_, T p_205359_2_) {
      return false;
   }

   public void scheduleTick(BlockPos p_205362_1_, T p_205362_2_, int p_205362_3_, TickPriority p_205362_4_) {
      this.field_219500_a.add(new NextTickListEntry(p_205362_1_, p_205362_2_, (long)p_205362_3_, p_205362_4_));
   }

   public boolean isTickPending(BlockPos p_205361_1_, T p_205361_2_) {
      return false;
   }

   public void func_219497_a(Stream<NextTickListEntry<T>> p_219497_1_) {
      Set var10001 = this.field_219500_a;
      p_219497_1_.forEach(var10001::add);
   }

   public Stream<NextTickListEntry<T>> func_219499_a() {
      return this.field_219500_a.stream();
   }

   public ListNBT func_219498_a(long p_219498_1_) {
      return ServerTickList.func_219502_a(this.field_219501_b, this.field_219500_a, p_219498_1_);
   }

   public static <T> SerializableTickList<T> func_222984_a(ListNBT p_222984_0_, Function<T, ResourceLocation> p_222984_1_, Function<ResourceLocation, T> p_222984_2_) {
      Set<NextTickListEntry<T>> lvt_3_1_ = Sets.newHashSet();

      for(int lvt_4_1_ = 0; lvt_4_1_ < p_222984_0_.size(); ++lvt_4_1_) {
         CompoundNBT lvt_5_1_ = p_222984_0_.getCompound(lvt_4_1_);
         T lvt_6_1_ = p_222984_2_.apply(new ResourceLocation(lvt_5_1_.getString("i")));
         if (lvt_6_1_ != null) {
            lvt_3_1_.add(new NextTickListEntry(new BlockPos(lvt_5_1_.getInt("x"), lvt_5_1_.getInt("y"), lvt_5_1_.getInt("z")), lvt_6_1_, (long)lvt_5_1_.getInt("t"), TickPriority.getPriority(lvt_5_1_.getInt("p"))));
         }
      }

      return new SerializableTickList(p_222984_1_, lvt_3_1_);
   }
}
