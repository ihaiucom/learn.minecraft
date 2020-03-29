package net.minecraftforge.fml;

import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.resources.IResourcePack;
import net.minecraftforge.fml.packs.ModFileResourcePack;
import org.apache.commons.lang3.tuple.Pair;

public class ExtensionPoint<T> {
   public static final ExtensionPoint<BiFunction<Minecraft, Screen, Screen>> CONFIGGUIFACTORY = new ExtensionPoint();
   public static final ExtensionPoint<BiFunction<Minecraft, ModFileResourcePack, IResourcePack>> RESOURCEPACK = new ExtensionPoint();
   public static final ExtensionPoint<Pair<Supplier<String>, BiPredicate<String, Boolean>>> DISPLAYTEST = new ExtensionPoint();
   private Class<T> type;

   private ExtensionPoint() {
   }
}
