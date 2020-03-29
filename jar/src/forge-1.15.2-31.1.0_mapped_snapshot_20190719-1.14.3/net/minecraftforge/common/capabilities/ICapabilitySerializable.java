package net.minecraftforge.common.capabilities;

import net.minecraft.nbt.INBT;
import net.minecraftforge.common.util.INBTSerializable;

public interface ICapabilitySerializable<T extends INBT> extends ICapabilityProvider, INBTSerializable<T> {
}
