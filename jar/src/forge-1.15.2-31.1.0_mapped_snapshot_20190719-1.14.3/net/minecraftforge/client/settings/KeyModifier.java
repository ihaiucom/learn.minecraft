package net.minecraftforge.client.settings;

import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.InputMappings;

public enum KeyModifier {
   CONTROL {
      public boolean matches(InputMappings.Input key) {
         int keyCode = key.getKeyCode();
         if (Minecraft.IS_RUNNING_ON_MAC) {
            return keyCode == 342 || keyCode == 346;
         } else {
            return keyCode == 341 || keyCode == 345;
         }
      }

      public boolean isActive(@Nullable IKeyConflictContext conflictContext) {
         return Screen.hasControlDown();
      }

      public String getLocalizedComboName(InputMappings.Input key, Supplier<String> defaultLogic) {
         String keyName = (String)defaultLogic.get();
         String localizationFormatKey = Minecraft.IS_RUNNING_ON_MAC ? "forge.controlsgui.control.mac" : "forge.controlsgui.control";
         return I18n.format(localizationFormatKey, keyName);
      }
   },
   SHIFT {
      public boolean matches(InputMappings.Input key) {
         return key.getKeyCode() == 340 || key.getKeyCode() == 344;
      }

      public boolean isActive(@Nullable IKeyConflictContext conflictContext) {
         return Screen.hasShiftDown();
      }

      public String getLocalizedComboName(InputMappings.Input key, Supplier<String> defaultLogic) {
         return I18n.format("forge.controlsgui.shift", defaultLogic.get());
      }
   },
   ALT {
      public boolean matches(InputMappings.Input key) {
         return key.getKeyCode() == 342 || key.getKeyCode() == 346;
      }

      public boolean isActive(@Nullable IKeyConflictContext conflictContext) {
         return Screen.hasAltDown();
      }

      public String getLocalizedComboName(InputMappings.Input keyCode, Supplier<String> defaultLogic) {
         return I18n.format("forge.controlsgui.alt", defaultLogic.get());
      }
   },
   NONE {
      public boolean matches(InputMappings.Input key) {
         return false;
      }

      public boolean isActive(@Nullable IKeyConflictContext conflictContext) {
         if (conflictContext != null && !conflictContext.conflicts(KeyConflictContext.IN_GAME)) {
            KeyModifier[] var2 = MODIFIER_VALUES;
            int var3 = var2.length;

            for(int var4 = 0; var4 < var3; ++var4) {
               KeyModifier keyModifier = var2[var4];
               if (keyModifier.isActive(conflictContext)) {
                  return false;
               }
            }
         }

         return true;
      }

      public String getLocalizedComboName(InputMappings.Input key, Supplier<String> defaultLogic) {
         return (String)defaultLogic.get();
      }
   };

   public static final KeyModifier[] MODIFIER_VALUES = new KeyModifier[]{SHIFT, CONTROL, ALT};

   private KeyModifier() {
   }

   public static KeyModifier getActiveModifier() {
      KeyModifier[] var0 = MODIFIER_VALUES;
      int var1 = var0.length;

      for(int var2 = 0; var2 < var1; ++var2) {
         KeyModifier keyModifier = var0[var2];
         if (keyModifier.isActive((IKeyConflictContext)null)) {
            return keyModifier;
         }
      }

      return NONE;
   }

   public static boolean isKeyCodeModifier(InputMappings.Input key) {
      KeyModifier[] var1 = MODIFIER_VALUES;
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         KeyModifier keyModifier = var1[var3];
         if (keyModifier.matches(key)) {
            return true;
         }
      }

      return false;
   }

   public static KeyModifier valueFromString(String stringValue) {
      try {
         return valueOf(stringValue);
      } catch (IllegalArgumentException | NullPointerException var2) {
         return NONE;
      }
   }

   public abstract boolean matches(InputMappings.Input var1);

   public abstract boolean isActive(@Nullable IKeyConflictContext var1);

   public abstract String getLocalizedComboName(InputMappings.Input var1, Supplier<String> var2);

   // $FF: synthetic method
   KeyModifier(Object x2) {
      this();
   }
}
