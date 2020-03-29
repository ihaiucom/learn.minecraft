package net.minecraftforge.common.crafting.conditions;

import net.minecraft.util.ResourceLocation;

public interface ICondition {
   ResourceLocation getID();

   boolean test();
}
