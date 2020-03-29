package net.minecraftforge.common.property;

import net.minecraft.client.renderer.model.IModelTransform;
import net.minecraft.state.BooleanProperty;
import net.minecraftforge.client.model.data.ModelProperty;

public class Properties {
   public static final BooleanProperty StaticProperty = BooleanProperty.create("static");
   public static final ModelProperty<IModelTransform> AnimationProperty = new ModelProperty();
}
