package net.minecraftforge.common.model.animation;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;

public class CapabilityAnimation {
   @CapabilityInject(IAnimationStateMachine.class)
   public static Capability<IAnimationStateMachine> ANIMATION_CAPABILITY = null;

   public static void register() {
      CapabilityManager.INSTANCE.register(IAnimationStateMachine.class, new Capability.IStorage<IAnimationStateMachine>() {
         public INBT writeNBT(Capability<IAnimationStateMachine> capability, IAnimationStateMachine instance, Direction side) {
            return null;
         }

         public void readNBT(Capability<IAnimationStateMachine> capability, IAnimationStateMachine instance, Direction side, INBT nbt) {
         }
      }, AnimationStateMachine::getMissing);
   }

   public static class DefaultItemAnimationCapabilityProvider implements ICapabilityProvider {
      @Nonnull
      private final LazyOptional<IAnimationStateMachine> asm;

      public DefaultItemAnimationCapabilityProvider(@Nonnull LazyOptional<IAnimationStateMachine> asm) {
         this.asm = asm;
      }

      @Nonnull
      public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, @Nullable Direction facing) {
         return CapabilityAnimation.ANIMATION_CAPABILITY.orEmpty(capability, this.asm);
      }
   }
}
