package aiven.zomboidhealthsystem.foundation.gui.hud;

import aiven.zomboidhealthsystem.Config;
import aiven.zomboidhealthsystem.foundation.utility.RenderHelper;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import org.joml.Vector2f;

@Environment(EnvType.CLIENT)
public enum BodyPartHud {
    HEAD(16,16,10,0),
    BODY(16,30,10,18),
    LEFT_ARM(7,30,1,18),
    RIGHT_ARM(7,30,28,18),
    LEFT_LEG(7,20,10,50),
    RIGHT_LEG(7,20,19,50),
    LEFT_FOOT(7,7,10,72),
    RIGHT_FOOT(7,7,19,72)
    ;

    private final int width, height, x, y;
    private int color = Colors.FULL_HEALTH_COLOR.getColor();
    private float hpPercent;
    private int borderColor = Colors.BORDER_COLOR.getColor();


    BodyPartHud(int width, int height, int x, int y) {
        this.width = width;
        this.height = height;
        this.x = x;
        this.y = y;
    }


    public void renderPart(DrawContext context, float tickDelta, Vector2f pos){
        int posX = (int) pos.x;
        int posY = (int) pos.y;
        RenderHelper.DrawQuad(context, width, height, x + posX, y + posY, color, borderColor);
    }

    private void setColor(int color) {
        this.color = color;
    }

    public void setHp(float hpPercent, boolean addHp, boolean showBandaged) {
        this.hpPercent = hpPercent;
        if(addHp) {
            borderColor = Colors.WHITE.getColor();
        } else {
            borderColor = Colors.BORDER_COLOR.getColor();
        }
        if(!showBandaged) {
            if (hpPercent > 1.0F) setColor(Colors.ADDITIONAL_HEALTH_COLOR.getColor());
            else if (hpPercent == 1.0F) setColor(Colors.FULL_HEALTH_COLOR.getColor());
            else if (hpPercent >= 0.90F) setColor(Colors.LITTLE_DAMAGED_HEALTH_COLOR.getColor());
            else if (hpPercent >= 0.75F) setColor(Colors.DAMAGED_HEALTH_COLOR.getColor());
            else if (hpPercent >= 0.50F) setColor(Colors.HALF_HEALTH_COLOR.getColor());
            else if (hpPercent >= 0.25F) setColor(Colors.VERY_DAMAGED_HEALTH_COLOR.getColor());
            else if (hpPercent > 0.00F) setColor(Colors.VERY_VERY_DAMAGED_HEALTH_COLOR.getColor());
            else setColor(Colors.NULL_HEALTH_COLOR.getColor());
        } else {
            setColor(Colors.BANDAGED_BODY_PART.getColor());
        }
    }

    public float getHpPercent() {
        return hpPercent;
    }

    public enum Colors{
        BANDAGED_BODY_PART (0xFFe6d9ad), //  e9ddb4
        ADDITIONAL_HEALTH_COLOR (0xFF05ff10),
        FULL_HEALTH_COLOR (0xFF347a2e),
        LITTLE_DAMAGED_HEALTH_COLOR (0xFFc8e000),
        DAMAGED_HEALTH_COLOR (0xFFe0aa00),
        HALF_HEALTH_COLOR  (0xFFe08000),
        VERY_DAMAGED_HEALTH_COLOR  (0xFFe04200),
        VERY_VERY_DAMAGED_HEALTH_COLOR(0xFF00000),
        NULL_HEALTH_COLOR  (0xFF424242),
        WHITE (0xFFffffff),
        BORDER_COLOR (0xFF121212)
        ;

        private final int color;

        Colors(int color){
            this.color = color;
        }

        int getColor(){
            return color;
        }

    }

    public static void renderAllParts(DrawContext context, float tickDelta, Vector2f pos){
        for (BodyPartHud part : values()) {
            part.renderPart(context, tickDelta, pos);
        }
    }

    public static void renderAllParts(DrawContext context, float tickDelta) {
        renderAllParts(context, tickDelta, POS);
    }

    public static void setPos(Vector2f pos) {
        BodyPartHud.POS = pos;
    }

    public static Vector2f getPos() {
        return POS;
    }

    public static int getWidth(){
        return 35;
    }

    public static int getHeight() {
        return 73;
    }

    public static Vector2f POS = Config.HEALTH_HUD_POS.getValue();
}