package net.minecraftforge.client.extensions;

import javax.annotation.Nonnull;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraftforge.client.settings.IKeyConflictContext;
import net.minecraftforge.client.settings.KeyModifier;

public interface IForgeKeybinding {
   default KeyBinding getKeyBinding() {
      return (KeyBinding)this;
   }

   @Nonnull
   InputMappings.Input getKey();

   default boolean isActiveAndMatches(InputMappings.Input keyCode) {
      return keyCode != InputMappings.INPUT_INVALID && keyCode.equals(this.getKey()) && this.getKeyConflictContext().isActive() && this.getKeyModifier().isActive(this.getKeyConflictContext());
   }

   default void setToDefault() {
      this.setKeyModifierAndCode(this.getKeyModifierDefault(), this.getKeyBinding().getDefault());
   }

   void setKeyConflictContext(IKeyConflictContext var1);

   IKeyConflictContext getKeyConflictContext();

   KeyModifier getKeyModifierDefault();

   KeyModifier getKeyModifier();

   void setKeyModifierAndCode(KeyModifier var1, InputMappings.Input var2);

   default boolean hasKeyCodeModifierConflict(KeyBinding other) {
      return (this.getKeyConflictContext().conflicts(other.getKeyConflictContext()) || other.getKeyConflictContext().conflicts(this.getKeyConflictContext())) && (this.getKeyModifier().matches(other.getKey()) || other.getKeyModifier().matches(this.getKey()));
   }
}
