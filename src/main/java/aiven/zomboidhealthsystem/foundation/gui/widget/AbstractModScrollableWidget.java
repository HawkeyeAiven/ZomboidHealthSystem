package aiven.zomboidhealthsystem.foundation.gui.widget;

import aiven.zomboidhealthsystem.ZomboidHealthSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ScrollableWidget;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.joml.Vector2i;

import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

@Environment(EnvType.CLIENT)
public abstract class AbstractModScrollableWidget extends ScrollableWidget {
    public AbstractModScrollableWidget(int i, int j, int k, int l, Text text) {
        super(i, j, k, l, text);
    }

    public CopyOnWriteArrayList<Element> elements = new CopyOnWriteArrayList<>();

    public void addElement(Element element){
        elements.add(element);
    }

    public void removeElement(Element element){
        elements.remove(element);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float tickDelta) {
        renderOverlay(context);
        renderButton(context, mouseX, mouseY, tickDelta);
    }

    @Override
    protected void renderContents(DrawContext context, int mouseX, int mouseY, float delta) {
        int y = 15;
        for(Element element:elements){
            if(!element.texts.isEmpty()) {
                element.setPos(new Vector2i(this.getX() + 10, this.getY() + y));
                element.render(context);
                y += element.getSize() * 12;
                y += 7;
            }
        }
    }

    public void tick(){
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        setScrollY(getScrollY() + (amount * -7));
        return true;
    }

    @Override
    protected int getMaxScrollY() {
        int size = 0;
        for(Element element : elements){
            if(element.getSize() != -1) {
                size += element.getPixelSize();
            }
        }
        if(size <= 0) {
            return 0;
        }
        else {
            return Math.max(size - 30,0);
        }
    }

    @Override
    protected int getContentsHeight() {
        return 300;
    }

    @Override
    protected double getDeltaYPerScroll() {
        return 0.1;
    }

    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {

    }

    @Override
    protected void drawBox(DrawContext context) {

    }

    @Override
    protected void renderOverlay(DrawContext context) {

    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        mouseY += getScrollY();
        for (Element element : elements) {
            if (element.getPos() != null) {
                int x = element.getPos().x;
                int y = element.getPos().y;
                if (mouseX > x && mouseX < x + 90 && mouseY > y && mouseY < y + element.getPixelSize()) {
                    element.buttonElement.mouseClicked((int) mouseX, (int) mouseY, button, element);
                }
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    public static class Element {
        public static final int NAME_COLOR = 0xFFffffff;
        public final String name;
        public ArrayList<ColoredText> texts = new ArrayList<>();
        public final ColoredText name1;
        private Vector2i pos;
        ButtonElement buttonElement;

        public Element(String name, ButtonElement buttonElement){
            this.name = name;
            this.name1 = new ColoredText(NAME_COLOR,Text.translatable(ZomboidHealthSystem.ID + ".health." + name));
            this.buttonElement = buttonElement;
        }

        public void render(DrawContext context){
            if(pos != null) {
                int y1 = this.pos.y();

                drawText(context, this.pos.x(), this.pos.y(), name1);

                for (ColoredText text : texts) {
                    y1 += 12;
                    drawText(context, this.pos.x() + 10, y1, text);
                }
            }
        }

        public void addText(ColoredText text){
            if(!texts.contains(text)){
                texts.add(text);
            }
        }

        public void clearText(){
            texts.clear();
        }

        public void setPos(Vector2i pos) {
            this.pos = pos;
        }

        public Vector2i getPos() {
            return pos;
        }

        public int getSize(){
            if(!texts.isEmpty()) {
                return texts.size() + 1;
            }
            else {
                return -1;
            }
        }

        public int getPixelSize(){
            if(this.getSize() != -1) {
                return (this.getSize() * 10) + 7;
            } else {
                return -1;
            }
        }

        public String getName() {
            return name;
        }

        public void drawText(DrawContext context, int x, int y, ColoredText text){
            context.drawText(
                    MinecraftClient.getInstance().textRenderer,text.text,
                    x,y,text.color,false);
        }
    }

    public interface ButtonElement {
        void mouseClicked(int mouseX, int mouseY, int button, Element element);
    }


    public record ColoredText(int color, MutableText text) {
    }
}