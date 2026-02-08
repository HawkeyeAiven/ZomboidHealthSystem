package aiven.zomboidhealthsystem.foundation.utility;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.*;
import org.joml.Matrix4f;

public class RenderHelper {
    public static void DrawQuad(DrawContext drawContext, int x, int y, int x1, int y1, int color, int borderColor){
        Matrix4f transformationMatrix = drawContext.getMatrices().peek().getPositionMatrix();
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();


        buffer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);

        buffer.vertex(transformationMatrix, x1, y1, 5).color(color).next();
        buffer.vertex(transformationMatrix, x1, y+y1, 5).color(color).next();
        buffer.vertex(transformationMatrix, x1+x, y+y1, 5).color(color).next();
        buffer.vertex(transformationMatrix, x1+x, y1, 5).color(color).next();

        drawContext.drawBorder(x1-1,y1-1,x+2,y+2,borderColor);

        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        tessellator.draw();
    }
}
