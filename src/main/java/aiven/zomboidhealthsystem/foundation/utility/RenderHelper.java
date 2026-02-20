package aiven.zomboidhealthsystem.foundation.utility;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.*;
import org.joml.Matrix4f;

public class RenderHelper {
    public static void drawQuad(DrawContext drawContext, int x, int y, int width, int height, int color, int borderColor){
        Matrix4f transformationMatrix = drawContext.getMatrices().peek().getPositionMatrix();
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();


        buffer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);

        buffer.vertex(transformationMatrix, x, y, 5).color(color).next();
        buffer.vertex(transformationMatrix, x, height+y, 5).color(color).next();
        buffer.vertex(transformationMatrix, x+width, height+y, 5).color(color).next();
        buffer.vertex(transformationMatrix, x+width, y, 5).color(color).next();

        drawContext.drawBorder(x-1,y-1,width+2,height+2,borderColor);

        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        tessellator.draw();
    }
}
