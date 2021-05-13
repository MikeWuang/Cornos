package me.constantindev.ccl.gui.widget;

import me.constantindev.ccl.Cornos;
import me.constantindev.ccl.etc.config.CConf;
import me.constantindev.ccl.etc.helper.Renderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.widget.AbstractPressableButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.awt.*;

public class CustomButtonWidget extends AbstractPressableButtonWidget {
    public Color unselectedColor = new Color(30, 30, 30, 100);
    protected int r;
    long lastCache = 0;
    double timer = 0;
    Runnable onPressed;

    public CustomButtonWidget(int x, int y, int width, int height, Text message, Runnable onPress) {
        super(x, y, width, height, message);
        this.r = 5;
        this.onPressed = onPress;
    }

    public CustomButtonWidget(int x, int y, int width, int height, int roundness, Text message, Runnable onPress) {
        super(x, y, width, height, message);
        this.onPressed = onPress;
        this.r = roundness;
    }

    @Override
    public void onPress() {
        onPressed.run();
    }

    @Override
    public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        Color c = CConf.getRGB();
        long current = System.currentTimeMillis();
        if (current - lastCache > 3) {
            double cvalA = (double) (current - lastCache) / 250.0;
            double cval = this.isHovered() ? cvalA : -cvalA;
            timer += cval;
            lastCache = current;
        }
        timer = MathHelper.clamp(timer, 0, 1);
        if (timer != 0) {
            double a = easeInOutQuart(timer);
            double yProg = a * (double) height;
            double xProg = a * (double) width;
            Renderer.renderLineScreen(new Vec3d(x, y, 0), new Vec3d(x, y + yProg, 0), c, 2);
            Renderer.renderLineScreen(new Vec3d(x, y + height, 0), new Vec3d(x + xProg, y + height, 0), c, 2);
            Renderer.renderLineScreen(new Vec3d(x + width, y + height, 0), new Vec3d(x + width, y + height - yProg, 0), c, 2);
            Renderer.renderLineScreen(new Vec3d(x + width, y, 0), new Vec3d(x + width - xProg, y, 0), c, 2);
        }
        //if ((a * width) > r) Renderer.renderRoundedQuad(x, y, x + (a * width), y + height, r - 1, selectedColor);
        //Renderer.renderRoundedQuad(x, y, x + width, y + height, r, unselectedColor);
        DrawableHelper.fill(matrices, x, y, x + width, y + height, unselectedColor.getRGB());
        drawCenteredText(matrices, Cornos.minecraft.textRenderer, this.getMessage(), this.x + this.width / 2, this.y + (this.height - 8) / 2, Color.white.getRGB());
    }

    double easeInOutQuart(double x) {
        return x < 0.5 ? 4 * x * x * x : 1 - Math.pow(-2 * x + 2, 3) / 2;
    }
}
