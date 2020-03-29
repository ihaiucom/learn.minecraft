package net.minecraft.command;

import java.util.Iterator;
import net.minecraft.advancements.FunctionManager;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tags.Tag;
import net.minecraft.util.ResourceLocation;

public class TimedFunctionTag implements ITimerCallback<MinecraftServer> {
   private final ResourceLocation tagName;

   public TimedFunctionTag(ResourceLocation p_i51189_1_) {
      this.tagName = p_i51189_1_;
   }

   public void run(MinecraftServer p_212869_1_, TimerCallbackManager<MinecraftServer> p_212869_2_, long p_212869_3_) {
      FunctionManager lvt_5_1_ = p_212869_1_.getFunctionManager();
      Tag<FunctionObject> lvt_6_1_ = lvt_5_1_.getTagCollection().getOrCreate(this.tagName);
      Iterator var7 = lvt_6_1_.getAllElements().iterator();

      while(var7.hasNext()) {
         FunctionObject lvt_8_1_ = (FunctionObject)var7.next();
         lvt_5_1_.execute(lvt_8_1_, lvt_5_1_.getCommandSource());
      }

   }

   public static class Serializer extends ITimerCallback.Serializer<MinecraftServer, TimedFunctionTag> {
      public Serializer() {
         super(new ResourceLocation("function_tag"), TimedFunctionTag.class);
      }

      public void write(CompoundNBT p_212847_1_, TimedFunctionTag p_212847_2_) {
         p_212847_1_.putString("Name", p_212847_2_.tagName.toString());
      }

      public TimedFunctionTag read(CompoundNBT p_212846_1_) {
         ResourceLocation lvt_2_1_ = new ResourceLocation(p_212846_1_.getString("Name"));
         return new TimedFunctionTag(lvt_2_1_);
      }

      // $FF: synthetic method
      public ITimerCallback read(CompoundNBT p_212846_1_) {
         return this.read(p_212846_1_);
      }
   }
}
