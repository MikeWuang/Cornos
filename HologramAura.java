/*
@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
# Project: Cornos
# File: HologramAura
# Created by constantin at 20:51, Mär 20 2021
PLEASE READ THE COPYRIGHT NOTICE IN THE PROJECT ROOT, IF EXISTENT
@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
*/
package me.constantindev.ccl.module.EXPLOIT;

import com.sun.javafx.geom.Vec2d;
import me.constantindev.ccl.Cornos;
import me.constantindev.ccl.command.Hologram;
import me.constantindev.ccl.etc.Notification;
import me.constantindev.ccl.etc.base.Module;
import me.constantindev.ccl.etc.config.MultiOption;
import me.constantindev.ccl.etc.config.Num;
import me.constantindev.ccl.etc.config.Toggleable;
import me.constantindev.ccl.etc.event.EventHelper;
import me.constantindev.ccl.etc.event.EventType;
import me.constantindev.ccl.etc.event.arg.PacketEvent;
import me.constantindev.ccl.etc.helper.ClientHelper;
import me.constantindev.ccl.etc.helper.RandomHelper;
import me.constantindev.ccl.etc.ms.MType;
import me.constantindev.ccl.etc.render.RenderableBlock;
import me.constantindev.ccl.etc.render.RenderableLine;
import me.constantindev.ccl.module.ext.Hud;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.CreativeInventoryActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HologramAura extends Module {
    public static String message = "";
    public static String message2build = "";

    Num range = new Num("range", 20, 1000, 10);
    Num incrementor = new Num("incrementor", 0.8, 2, 0.1);
    Toggleable isEgg = new Toggleable("isEgg", false);
    Toggleable autoplace = new Toggleable("autoplace", false);
    MultiOption mode = new MultiOption("mode", "random", new String[]{"random", "circle", "sphere", "penis", "sin", "text"});
    Num fontSize = new Num("fontSize", 10, 30, 1);
    MultiOption textdirection = new MultiOption("textdirection", "x+", new String[]{"x+", "x-", "z+", "z-"});

    List<Vec3d> spherePointList = new ArrayList<>();
    List<Vec3d> penisPointList = new ArrayList<>();
    List<Vec3d> textPointList = new ArrayList<>();
    List<RenderableLine> bruh = new ArrayList<>();
    RenderableBlock rb;
    BlockPos origin;
    Vec3d lp;
    int currentIndex = 0;
    double progress = 0.0;

    public HologramAura() {
        super("HologramAura", "I dont even know anymore", MType.EXPLOIT);
        this.mconf.add(range);
        this.mconf.add(autoplace);
        this.mconf.add(isEgg);
        this.mconf.add(mode);
        this.mconf.add(incrementor);
        this.mconf.add(fontSize);
        this.mconf.add(textdirection);
        Module parent = this;
        EventHelper.BUS.registerEvent(EventType.ONPACKETSEND, event -> {
            if (!parent.isOn.isOn()) return;
            PacketEvent pe = (PacketEvent) event;
            if (pe.packet instanceof PlayerInteractBlockC2SPacket) {
                newPos();
            }
        });
    }

    @Override
    public void onDisable() {
        progress = 0.0;
        currentIndex = 0;
        lp = null;
        spherePointList.clear();
        penisPointList.clear();
        textPointList.clear();
        Hud.currentContext = "";
        super.onDisable();
    }

    @Override
    public void onEnable() {
        if (Cornos.minecraft.player == null) return;
        if (message.isEmpty()) {
            Notification.create("Error", new String[]{"Please only enable this via the", "funny items menu"}, 5000);
            this.isOn.setState(false);
            return;
        }


        origin = Cornos.minecraft.player.getBlockPos();
        Notification.create("HologramAura", new String[]{"Set point of origin to highlighted block"},5000);
        double incInv = Math.abs(incrementor.getValue() - incrementor.max);
        double res = 30 * incInv;
        for (double i = 0; i <= Math.PI; i += Math.PI / res) {
            double radius = Math.sin(i) * range.getValue();
            double y = Math.cos(i) * range.getValue();
            for (double a = 0; a < Math.PI * 2; a += Math.PI / res) {
                double x = Math.cos(a) * radius;
                double z = Math.sin(a) * radius;
                spherePointList.add(new Vec3d(x, y, z));
            }
        }

        for (double i = 0; i <= Math.PI; i += Math.PI / res) {
            double radius = Math.sin(i);
            double y = Math.cos(i);
            for (double a = 0; a < Math.PI * 2; a += Math.PI / res) {
                double x = Math.cos(a) * radius;
                double z = Math.sin(a) * radius;
                Vec3d current = new Vec3d(x, y, z).subtract(0, 0, 1);
                penisPointList.add(current);
            }
        }
        for (double i = 0; i <= Math.PI; i += Math.PI / res) {
            double radius = Math.sin(i);
            double y = Math.cos(i);
            for (double a = 0; a < Math.PI * 2; a += Math.PI / res) {
                double x = Math.cos(a) * radius;
                double z = Math.sin(a) * radius;
                Vec3d current = new Vec3d(x, y, z).subtract(0, 0, -1);
                penisPointList.add(current);
            }
        }
        for (double i = 0; i <= Math.PI; i += Math.PI / (res * 3)) {
            double radius = Math.sin(i);
            double y = Math.cos(i) * 3;
            for (double a = 0; a < Math.PI * 2; a += Math.PI / res) {
                double x = Math.cos(a) * radius;
                double z = Math.sin(a) * radius;
                Vec3d current = new Vec3d(x, y, z).subtract(0, -3, 0);
                penisPointList.add(current);
            }
        }


        List<Vec2d> imagePoints = stringToPoints(message2build);
        for (Vec2d current : imagePoints) {
            switch (textdirection.value) {
                case "x+": {
                    textPointList.add(new Vec3d(current.x * incrementor.getValue(), (-current.y) * incrementor.getValue(), 0));
                    break;
                }
                case "x-": {
                    textPointList.add(new Vec3d(-current.x * incrementor.getValue(), (-current.y) * incrementor.getValue(), 0));
                    break;
                }
                case "z+": {
                    textPointList.add(new Vec3d(0, (-current.y) * incrementor.getValue(), current.x * incrementor.getValue()));
                    break;
                }
                case "z-": {
                    textPointList.add(new Vec3d(0, (-current.y) * incrementor.getValue(), (-current.x) * incrementor.getValue()));
                    break;
                }
            }
        }

        super.onEnable();
    }

    @Override
    public void onRender(MatrixStack ms, float td) {
        if (rb != null) this.rbq.add(rb);
        this.rlq.addAll(bruh);
        Vec3d sphereBefore = null;
        if (spherePointList.size() != 0 && origin != null && mode.value.equalsIgnoreCase("sphere")) {
            Vec3d origin1 = new Vec3d(origin.getX(), origin.getY(), origin.getZ());
            for (Vec3d v3d : spherePointList) {
                if (sphereBefore != null) {
                    RenderableLine rl = new RenderableLine(sphereBefore.add(origin1), v3d.add(origin1), 50, 255, 255, 255, 1);
                    this.rlq.add(rl);
                }
                sphereBefore = v3d;
            }
        }
        Vec3d textBefore = null;
        if(textPointList.size() != 0 && origin != null && mode.value.equalsIgnoreCase("text"))
        {
            Vec3d origin1 = new Vec3d(origin.getX(), origin.getY(), origin.getZ());
            for (Vec3d v3d : textPointList)
            {
                if(textBefore != null)
                {
                    RenderableLine rl = new RenderableLine(textBefore.add(origin1), v3d.add(origin1), 50, 255, 255, 255, 1);
                }
                textBefore = v3d;
            }
        }
        super.onRender(ms, td);
    }

    @Override
    public void onExecute() {
        if (lp == null) newPos();
        bruh.clear();
        if (Cornos.minecraft.getNetworkHandler() == null) return;
        if (Cornos.minecraft.interactionManager == null) return;
        if (origin == null) {
            this.setEnabled(false);
            return;
        }
        Color c = Hud.themeColor.getColor();
        this.rb = new RenderableBlock(new Vec3d(origin.getX() - .5, origin.getY() - .5, origin.getZ() - .5), c.getRed(), c.getGreen(), c.getBlue(), 255);
        RenderableLine rl = new RenderableLine(new Vec3d(origin.getX(), origin.getY(), origin.getZ()), lp, c.getRed(), c.getGreen(), c.getBlue(), 255, 2);
        this.bruh.add(rl);
        if (autoplace.isEnabled()) {
            assert Cornos.minecraft.player != null;
            if (!(Cornos.minecraft.crosshairTarget instanceof BlockHitResult)) return;
            BlockHitResult bhr = (BlockHitResult) Cornos.minecraft.crosshairTarget;
            Cornos.minecraft.interactionManager.interactBlock(Cornos.minecraft.player, Cornos.minecraft.world, Hand.MAIN_HAND, bhr);
        }
        super.onExecute();
    }

    void newPos() {
        if (Cornos.minecraft.getNetworkHandler() == null) return;
        double range = this.range.getValue();
        switch (mode.value) {
            case "random":
                lp = new Vec3d(origin.getX() + RandomHelper.rndD(range) - (range / 2), origin.getY() + RandomHelper.rndD(range) - (range / 2), origin.getZ() + RandomHelper.rndD(range) - (range / 2));
                break;
            case "circle":
                progress += incrementor.getValue();
                Hud.currentContext = "Progress: " + (progress + " / " + 360) + " (" + Math.floor(progress / 360.0 * 100) + "%)";
                if (progress > 360) {
                    progress = 0;
                    Notification.create("Hologramaura", new String[]{"Finished circle creation"}, 5000);
                    this.setEnabled(false);
                }
                double rad = Math.toRadians(progress);
                double sin = Math.sin(rad);
                double cos = Math.cos(rad);
                lp = new Vec3d(origin.getX() + sin * range, origin.getY(), origin.getZ() + cos * range);
                break;
            case "sphere":
                Vec3d sphereP = spherePointList.get(currentIndex);
                currentIndex++;
                if (currentIndex >= spherePointList.size()) {
                    currentIndex = 0;
                    Notification.create("Hologramaura", new String[]{"Finished sphere creation"}, 5000);
                    this.setEnabled(false);
                }
                Hud.currentContext = "Progress: " + (currentIndex + " / " + spherePointList.size()) + " (" + Math.floor(
                        ((double) currentIndex) / ((double) spherePointList.size()) * 100) + "%)";
                lp = new Vec3d(origin.getX() + sphereP.getX(), origin.getY() + sphereP.getY(), origin.getZ() + sphereP.getZ());
                break;
            case "penis":
                Vec3d penisP = penisPointList.get(currentIndex).multiply(range);
                currentIndex++;
                if (currentIndex >= penisPointList.size()) {
                    currentIndex = 0;
                    Notification.create("Hologramaura", new String[]{"Finished building penis", "Why did you do this"}, 5000);
                    this.setEnabled(false);
                }
                Hud.currentContext = "Progress: " + (currentIndex + " / " + penisPointList.size()) + " (" + Math.floor(
                        ((double) currentIndex) / ((double) penisPointList.size()) * 100) + "%)";
                lp = new Vec3d(origin.getX() + penisP.getX(), origin.getY() + penisP.getY(), origin.getZ() + penisP.getZ());
                break;
            case "sin":
                progress += incrementor.getValue();
                Hud.currentContext = "Progress: " + (progress + " / " + 360) + " (" + Math.floor(progress / 360.0 * 100) + "%)";
                if (progress > 360) {
                    progress = 0;
                    Notification.create("Hologramaura", new String[]{"Finished sin circle creation"}, 5000);
                    this.setEnabled(false);
                }
                double rad1 = Math.toRadians(progress);
                double sin1 = Math.sin(rad1);
                double cos1 = Math.cos(rad1);
                lp = new Vec3d(origin.getX() + sin1 * range, origin.getY() + (Math.sin(rad1 * 4) * range / 4), origin.getZ() + cos1 * range);
                break;
            case "text":
                Vec3d textP = textPointList.get(currentIndex);
                currentIndex++;
                if(currentIndex >= textPointList.size())
                {
                    currentIndex = 0;
                    Notification.create("Hologramaura", new String[]{"Finished building the text:", message2build}, 5000);
                    this.setEnabled(false);
                }
                Hud.currentContext = "Progress: " + (currentIndex + " / " + textPointList.size() + " ( " + Math.floor(((double) currentIndex) / ((double) textPointList.size()) * 100)) + "%)";
                lp = new Vec3d(origin.getX() + textP.x, origin.getY() + textP.y, origin.getZ() + textP.z);
                break;
        }
        //bernie sanders
        ItemStack bruh = Hologram.getHoloStack(lp.subtract(0, 2.36, 0), message, isEgg.isEnabled(), false, "armor_stand");
        int slot = 36;
        CreativeInventoryActionC2SPacket p = new CreativeInventoryActionC2SPacket(slot, bruh);
        Cornos.minecraft.getNetworkHandler().sendPacket(p);
    }


    private List<Vec2d> stringToPoints(String input) //2d point generated from the image
    {
        List<Vec2d> imgPoints = new ArrayList<>();

        BufferedImage image = null;

        image = string2img(input);

        for(int x = 0; x < image.getWidth(); x++)
        {

            for(int y = 0; y < image.getHeight(); y++)
            {
                if(image.getRGB(x, y) == Color.WHITE.getRGB())
                {
                    imgPoints.add(new Vec2d(x, y - image.getHeight()));
                }
            }
        }
        return imgPoints;
    }

    private BufferedImage string2img(String input) //generates a image with the given string
    {

        BufferedImage image = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics2d = image.createGraphics();
        Font font = new Font("TimesNewRoman", Font.BOLD, (int)fontSize.getValue());
        graphics2d.setFont(font);
        graphics2d.setFont(font);
        FontMetrics fontmetrics = graphics2d.getFontMetrics();
        int width = fontmetrics.stringWidth(input);
        int height = fontmetrics.getHeight();
        graphics2d.dispose();

        image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        graphics2d = image.createGraphics();
        graphics2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        graphics2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        graphics2d.setFont(font);
        fontmetrics = graphics2d.getFontMetrics();
        graphics2d.setColor(Color.WHITE);
        graphics2d.drawString(input, 0, fontmetrics.getAscent());
        graphics2d.dispose();

        //File outputfile = new File("text.png");
        //ImageIO.write(image, "png", outputfile);


        return image;
    }
}
