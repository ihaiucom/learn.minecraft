package net.minecraft.item;

import javax.annotation.Nullable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public interface IItemPropertyGetter {
   @OnlyIn(Dist.CLIENT)
   float call(ItemStack var1, @Nullable World var2, @Nullable LivingEntity var3);
}
