package net.minecraftforge.client.event;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.entity.Entity;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.eventbus.api.Event.HasResult;

@HasResult
public class RenderNameplateEvent extends EntityEvent {
   private String nameplateContent;
   private final String originalContent;
   private final MatrixStack matrixStack;
   private final IRenderTypeBuffer renderTypeBuffer;

   public RenderNameplateEvent(Entity entity, String content, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer) {
      super(entity);
      this.originalContent = content;
      this.setContent(this.originalContent);
      this.matrixStack = matrixStack;
      this.renderTypeBuffer = renderTypeBuffer;
   }

   public void setContent(String contents) {
      this.nameplateContent = contents;
   }

   public String getContent() {
      return this.nameplateContent;
   }

   public String getOriginalContent() {
      return this.originalContent;
   }

   public MatrixStack getMatrixStack() {
      return this.matrixStack;
   }

   public IRenderTypeBuffer getRenderTypeBuffer() {
      return this.renderTypeBuffer;
   }
}
