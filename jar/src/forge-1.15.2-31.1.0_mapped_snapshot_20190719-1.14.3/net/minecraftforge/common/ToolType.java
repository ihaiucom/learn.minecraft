package net.minecraftforge.common;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.regex.Pattern;

public final class ToolType {
   private static final Pattern VALID_NAME = Pattern.compile("[^a-z_]");
   private static final Map<String, ToolType> values = Maps.newHashMap();
   public static final ToolType AXE = get("axe");
   public static final ToolType PICKAXE = get("pickaxe");
   public static final ToolType SHOVEL = get("shovel");
   private final String name;

   public static ToolType get(String name) {
      if (VALID_NAME.matcher(name).find()) {
         throw new IllegalArgumentException("ToolType.create() called with invalid name: " + name);
      } else {
         return (ToolType)values.computeIfAbsent(name, (k) -> {
            return new ToolType(name);
         });
      }
   }

   private ToolType(String name) {
      this.name = name;
   }

   public String getName() {
      return this.name;
   }
}
