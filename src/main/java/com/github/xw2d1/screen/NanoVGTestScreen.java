package com.github.xw2d1.screen;

import com.github.xw2d1.service.NanoVGService;
import com.mojang.blaze3d.platform.Window;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.nanovg.NVGColor;
import org.lwjgl.nanovg.NVGPaint;
import org.lwjgl.nanovg.NanoVG;
import org.lwjgl.opengl.*;

public class NanoVGTestScreen extends Screen {

    private final String[] tabs = {"\u2699 General", "\u270F Appearance", "\u266B Audio", "\u2691 Advanced"};
    private final String[] dropdownItems = {"Peaceful", "Easy", "Normal", "Hard"};
    private final String[] themes = {"Light", "Dark", "Midnight", "Ocean"};
    private final String[] gpuModes = {"Performance", "Balanced", "Quality", "Ultra"};
    private boolean wasMouseDown = false;
    private float lastDelta = 0;
    private int selectedTab = 0;
    private boolean dropdownOpen = false;
    private int dropdownSelected = 2;
    private float renderDist = 0.5f;
    private boolean renderDistDrag = false;
    private float fov = 0.6f;
    private boolean fovDrag = false;
    private float mouseSens = 0.5f;
    private boolean mousSensDrag = false;
    private boolean smoothLight = true, fancyGfx = false, vsync = true, fullscreen = false;
    private float hue = 0.55f, sat = 0.7f, val = 0.9f;
    private boolean hueDrag = false, svDrag = false;
    private int themeSelected = 1;
    private float uiScale = 0.1f;
    private boolean uiScaleDrag = false;
    private boolean showFps = true, showCoords = false, showBiome = true, compactMode = false, animatedBg = true;
    private float masterVol = 0.8f;
    private boolean masterVolDrag = false;
    private float musicVol = 0.6f;
    private boolean musicVolDrag = false;
    private float sfxVol = 0.9f;
    private boolean sfxVolDrag = false;
    private float ambientVol = 0.5f;
    private boolean ambientVolDrag = false;
    private float voiceVol = 0.7f;
    private boolean voiceVolDrag = false;
    private boolean muteAll = false, subtitles = false, directionalAudio = true;
    private int dropdown2Selected = 1;
    private boolean dropdown2Open = false;
    private float gamma = 0.5f;
    private boolean gammaDrag = false;
    private float chunkUpdates = 0.3f;
    private boolean chunkUpdatesDrag = false;
    private boolean entityShadows = true, particles = true, clouds = false, smoothChunk = true, antiAlias = true;
    private float loadProgress = 0f;
    private boolean loadingActive = false;

    public NanoVGTestScreen() {
        super(Component.literal("NanoVG UI"));
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        super.render(guiGraphics, mouseX, mouseY, delta);
        lastDelta = delta;
    }

    private boolean inRect(double mx, double my, float x, float y, float w, float h) {
        return mx >= x && mx <= x + w && my >= y && my <= y + h;
    }

    private void col(NVGColor c, float r, float g, float b, float a) {
        NanoVG.nvgRGBAf(r, g, b, a, c);
    }

    private float[] hsvToRgb(float h, float s, float v) {
        float c2 = v * s, x = c2 * (1 - Math.abs((h * 6) % 2 - 1)), m = v - c2;
        float r, g, b;
        int hi = (int) (h * 6) % 6;
        switch (hi) {
            case 0 -> {
                r = c2;
                g = x;
                b = 0;
            }
            case 1 -> {
                r = x;
                g = c2;
                b = 0;
            }
            case 2 -> {
                r = 0;
                g = c2;
                b = x;
            }
            case 3 -> {
                r = 0;
                g = x;
                b = c2;
            }
            case 4 -> {
                r = x;
                g = 0;
                b = c2;
            }
            default -> {
                r = c2;
                g = 0;
                b = x;
            }
        }
        return new float[]{r + m, g + m, b + m};
    }

    public void renderNanoVG() {
        long vg = NanoVGService.get();
        int[] prevProg = new int[1];
        GL11.glGetIntegerv(GL20.GL_CURRENT_PROGRAM, prevProg);
        int[] prevVao = new int[1];
        GL30.glGetIntegerv(GL30.GL_VERTEX_ARRAY_BINDING, prevVao);
        boolean dWas = GL11.glIsEnabled(GL11.GL_DEPTH_TEST), bWas = GL11.glIsEnabled(GL11.GL_BLEND);
        boolean cWas = GL11.glIsEnabled(GL11.GL_CULL_FACE), sWas = GL11.glIsEnabled(GL11.GL_STENCIL_TEST);

        GL20.glUseProgram(0);
        GL30.glBindVertexArray(0);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDisable(GL11.GL_CULL_FACE);
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL15.glBindBuffer(GL21.GL_PIXEL_UNPACK_BUFFER, 0);
        GL33.glBindSampler(0, 0);

        Window window = Minecraft.getInstance().getWindow();
        long handle = GLFW.glfwGetCurrentContext();
        float scale = (float) window.getGuiScale();
        float sw = window.getWidth() / scale, sh = window.getHeight() / scale;

        double[] xp = new double[1], yp = new double[1];
        GLFW.glfwGetCursorPos(handle, xp, yp);
        double mx = xp[0] / scale, my = yp[0] / scale;
        boolean md = GLFW.glfwGetMouseButton(handle, GLFW.GLFW_MOUSE_BUTTON_LEFT) == GLFW.GLFW_PRESS;
        boolean cl = md && !wasMouseDown;
        if (!md) {
            renderDistDrag = false;
            fovDrag = false;
            mousSensDrag = false;
            hueDrag = false;
            svDrag = false;
            uiScaleDrag = false;
            masterVolDrag = false;
            musicVolDrag = false;
            sfxVolDrag = false;
            ambientVolDrag = false;
            voiceVolDrag = false;
            gammaDrag = false;
            chunkUpdatesDrag = false;
        }
        wasMouseDown = md;

        NanoVG.nvgBeginFrame(vg, sw, sh, scale);
        NVGColor c = NVGColor.calloc();
        NVGColor c2 = NVGColor.calloc();
        NVGPaint p = NVGPaint.calloc();

        float pw = 520, ph = 480, px = (sw - pw) / 2, py = (sh - ph) / 2;


        col(c, 0, 0, 0, 0);
        col(c2, 0, 0, 0, 0.5f);
        NanoVG.nvgBoxGradient(vg, px, py + 6, pw, ph, 14, 28, c2, c, p);
        NanoVG.nvgBeginPath(vg);
        NanoVG.nvgRect(vg, px - 40, py - 40, pw + 80, ph + 80);
        NanoVG.nvgFillPaint(vg, p);
        NanoVG.nvgFill(vg);


        NanoVG.nvgBeginPath(vg);
        NanoVG.nvgRoundedRect(vg, px, py, pw, ph, 14);
        col(c, 0.10f, 0.10f, 0.13f, 0.97f);
        NanoVG.nvgFillColor(vg, c);
        NanoVG.nvgFill(vg);


        col(c, 0.35f, 0.55f, 1, 0.4f);
        col(c2, 0.6f, 0.3f, 1, 0);
        NanoVG.nvgLinearGradient(vg, px, py, px + pw, py, c, c2, p);
        NanoVG.nvgBeginPath(vg);
        NanoVG.nvgRoundedRectVarying(vg, px, py, pw, 2, 14, 14, 0, 0);
        NanoVG.nvgFillPaint(vg, p);
        NanoVG.nvgFill(vg);


        NanoVG.nvgBeginPath(vg);
        NanoVG.nvgRoundedRect(vg, px, py, pw, ph, 14);
        col(c, 1, 1, 1, 0.05f);
        NanoVG.nvgStrokeColor(vg, c);
        NanoVG.nvgStrokeWidth(vg, 1);
        NanoVG.nvgStroke(vg);


        NanoVG.nvgBeginPath(vg);
        NanoVG.nvgRoundedRectVarying(vg, px, py, pw, 48, 14, 14, 0, 0);
        col(c, 0.13f, 0.13f, 0.17f, 1);
        NanoVG.nvgFillColor(vg, c);
        NanoVG.nvgFill(vg);

        NanoVG.nvgFontFace(vg, "default");
        NanoVG.nvgFontSize(vg, 17);
        col(c, 1, 1, 1, 0.92f);
        NanoVG.nvgFillColor(vg, c);
        NanoVG.nvgTextAlign(vg, NanoVG.NVG_ALIGN_LEFT | NanoVG.NVG_ALIGN_MIDDLE);
        NanoVG.nvgText(vg, px + 20, py + 24, "\u2699  Settings");

        NanoVG.nvgFontSize(vg, 10);
        col(c, 1, 1, 1, 0.3f);
        NanoVG.nvgFillColor(vg, c);
        NanoVG.nvgTextAlign(vg, NanoVG.NVG_ALIGN_RIGHT | NanoVG.NVG_ALIGN_MIDDLE);
        NanoVG.nvgText(vg, px + pw - 50, py + 24, "v1.0.0");

        float clX = px + pw - 38, clY = py + 14;
        boolean clH = inRect(mx, my, clX, clY, 20, 20);
        NanoVG.nvgBeginPath(vg);
        NanoVG.nvgCircle(vg, clX + 10, clY + 10, 10);
        col(c, 1, 0.3f, 0.3f, clH ? 0.4f : 0);
        NanoVG.nvgFillColor(vg, c);
        NanoVG.nvgFill(vg);
        col(c, 1, 1, 1, clH ? 0.9f : 0.35f);
        NanoVG.nvgStrokeColor(vg, c);
        NanoVG.nvgStrokeWidth(vg, 1.5f);
        NanoVG.nvgBeginPath(vg);
        NanoVG.nvgMoveTo(vg, clX + 5, clY + 5);
        NanoVG.nvgLineTo(vg, clX + 15, clY + 15);
        NanoVG.nvgMoveTo(vg, clX + 15, clY + 5);
        NanoVG.nvgLineTo(vg, clX + 5, clY + 15);
        NanoVG.nvgStroke(vg);
        if (clH && cl) this.onClose();


        NanoVG.nvgBeginPath(vg);
        NanoVG.nvgMoveTo(vg, px, py + 48);
        NanoVG.nvgLineTo(vg, px + pw, py + 48);
        col(c, 1, 1, 1, 0.06f);
        NanoVG.nvgStrokeColor(vg, c);
        NanoVG.nvgStrokeWidth(vg, 1);
        NanoVG.nvgStroke(vg);


        float sideW = 120, tabSY = py + 58;
        NanoVG.nvgBeginPath(vg);
        NanoVG.nvgRoundedRectVarying(vg, px, tabSY, sideW, ph - 58, 0, 0, 14, 0);
        col(c, 0.08f, 0.08f, 0.10f, 1);
        NanoVG.nvgFillColor(vg, c);
        NanoVG.nvgFill(vg);
        NanoVG.nvgBeginPath(vg);
        NanoVG.nvgMoveTo(vg, px + sideW, tabSY);
        NanoVG.nvgLineTo(vg, px + sideW, py + ph);
        col(c, 1, 1, 1, 0.06f);
        NanoVG.nvgStrokeColor(vg, c);
        NanoVG.nvgStroke(vg);

        for (int i = 0; i < tabs.length; i++) {
            float ty = tabSY + 8 + i * 40;
            boolean tH = inRect(mx, my, px, ty, sideW, 36);
            if (i == selectedTab) {
                NanoVG.nvgBeginPath(vg);
                NanoVG.nvgRoundedRect(vg, px + 4, ty + 4, 3, 28, 2);
                col(c, 0.4f, 0.6f, 1, 0.9f);
                NanoVG.nvgFillColor(vg, c);
                NanoVG.nvgFill(vg);
                NanoVG.nvgBeginPath(vg);
                NanoVG.nvgRoundedRect(vg, px + 10, ty, sideW - 14, 36, 6);
                col(c, 0.35f, 0.55f, 1, 0.12f);
                NanoVG.nvgFillColor(vg, c);
                NanoVG.nvgFill(vg);
            } else if (tH) {
                NanoVG.nvgBeginPath(vg);
                NanoVG.nvgRoundedRect(vg, px + 10, ty, sideW - 14, 36, 6);
                col(c, 1, 1, 1, 0.04f);
                NanoVG.nvgFillColor(vg, c);
                NanoVG.nvgFill(vg);
            }
            NanoVG.nvgFontFace(vg, "default");
            NanoVG.nvgFontSize(vg, 12);
            NanoVG.nvgTextAlign(vg, NanoVG.NVG_ALIGN_LEFT | NanoVG.NVG_ALIGN_MIDDLE);
            col(c, 1, 1, 1, i == selectedTab ? 0.95f : (tH ? 0.7f : 0.45f));
            NanoVG.nvgFillColor(vg, c);
            NanoVG.nvgText(vg, px + 22, ty + 18, tabs[i]);
            if (tH && cl) {
                selectedTab = i;
                dropdownOpen = false;
                dropdown2Open = false;
            }
        }


        float cx = px + sideW + 20, cy = tabSY + 12, cw = pw - sideW - 40;
        switch (selectedTab) {
            case 0 -> renderGeneral(vg, c, c2, p, cx, cy, cw, mx, my, md, cl);
            case 1 -> renderAppearance(vg, c, c2, p, cx, cy, cw, mx, my, md, cl);
            case 2 -> renderAudio(vg, c, c2, p, cx, cy, cw, mx, my, md, cl);
            case 3 -> renderAdvanced(vg, c, c2, p, cx, cy, cw, mx, my, md, cl);
        }


        float bbY = py + ph - 52;
        NanoVG.nvgBeginPath(vg);
        NanoVG.nvgMoveTo(vg, px + sideW, bbY);
        NanoVG.nvgLineTo(vg, px + pw, bbY);
        col(c, 1, 1, 1, 0.06f);
        NanoVG.nvgStrokeColor(vg, c);
        NanoVG.nvgStroke(vg);
        float bw = 90, bh = 32;
        drawBtn(vg, c, c2, p, "Reset", cx, bbY + 10, bw, bh, inRect(mx, my, cx, bbY + 10, bw, bh), false);
        float canX = px + pw - 20 - bw * 2 - 10;
        boolean canH = inRect(mx, my, canX, bbY + 10, bw, bh);
        drawBtn(vg, c, c2, p, "Cancel", canX, bbY + 10, bw, bh, canH, false);
        if (canH && cl) this.onClose();
        float apX = px + pw - 20 - bw;
        drawBtn(vg, c, c2, p, "Apply", apX, bbY + 10, bw, bh, inRect(mx, my, apX, bbY + 10, bw, bh), true);


        if (dropdownOpen) renderDDPopup(vg, c, c2, p, cx, cy + 38, cw, dropdownItems, dropdownSelected, mx, my, cl, 0);
        if (dropdown2Open) renderDDPopup(vg, c, c2, p, cx, cy + 38, cw, gpuModes, dropdown2Selected, mx, my, cl, 1);

        c.free();
        c2.free();
        p.free();
        NanoVG.nvgEndFrame(vg);

        GL20.glUseProgram(prevProg[0]);
        GL30.glBindVertexArray(prevVao[0]);
        if (dWas) GL11.glEnable(GL11.GL_DEPTH_TEST);
        else GL11.glDisable(GL11.GL_DEPTH_TEST);
        if (bWas) GL11.glEnable(GL11.GL_BLEND);
        else GL11.glDisable(GL11.GL_BLEND);
        if (cWas) GL11.glEnable(GL11.GL_CULL_FACE);
        else GL11.glDisable(GL11.GL_CULL_FACE);
        if (sWas) GL11.glEnable(GL11.GL_STENCIL_TEST);
        else GL11.glDisable(GL11.GL_STENCIL_TEST);
    }

    private void renderGeneral(long vg, NVGColor c, NVGColor c2, NVGPaint p, float cx, float cy, float cw, double mx, double my, boolean md, boolean cl) {
        float y = cy;
        drawSection(vg, c, "Game Settings", cx, y);
        y += 22;
        drawLabel(vg, c, "Difficulty", cx, y);
        float ddY = y + 16;
        drawDDBox(vg, c, dropdownItems[dropdownSelected], cx, ddY, cw, mx, my, dropdownOpen);
        if (inRect(mx, my, cx, ddY, cw, 28) && cl) {
            dropdownOpen = !dropdownOpen;
            dropdown2Open = false;
        }
        y = ddY + 38;
        drawLabel(vg, c, "Render Distance: " + (int) (renderDist * 32), cx, y);
        y += 16;
        renderDist = drawSlider(vg, c, c2, p, cx, y, cw, renderDist, mx, my, md, renderDistDrag);
        if (inRect(mx, my, cx - 8, y, cw + 16, 20) && md) renderDistDrag = true;
        y += 30;
        drawLabel(vg, c, "FOV: " + (int) (70 + fov * 40), cx, y);
        y += 16;
        fov = drawSlider(vg, c, c2, p, cx, y, cw, fov, mx, my, md, fovDrag);
        if (inRect(mx, my, cx - 8, y, cw + 16, 20) && md) fovDrag = true;
        y += 30;
        drawLabel(vg, c, "Mouse Sensitivity: " + (int) (mouseSens * 200) + "%", cx, y);
        y += 16;
        mouseSens = drawSlider(vg, c, c2, p, cx, y, cw, mouseSens, mx, my, md, mousSensDrag);
        if (inRect(mx, my, cx - 8, y, cw + 16, 20) && md) mousSensDrag = true;
        y += 36;
        drawSection(vg, c, "Graphics", cx, y);
        y += 22;
        smoothLight = drawToggle(vg, c, "Smooth Lighting", cx, y, cw, smoothLight, mx, my, cl);
        y += 32;
        fancyGfx = drawToggle(vg, c, "Fancy Graphics", cx, y, cw, fancyGfx, mx, my, cl);
        y += 32;
        vsync = drawToggle(vg, c, "VSync", cx, y, cw, vsync, mx, my, cl);
        y += 32;
        fullscreen = drawToggle(vg, c, "Fullscreen", cx, y, cw, fullscreen, mx, my, cl);
    }

    private void renderAppearance(long vg, NVGColor c, NVGColor c2, NVGPaint p, float cx, float cy, float cw, double mx, double my, boolean md, boolean cl) {
        float y = cy;
        drawSection(vg, c, "Color Picker", cx, y);
        y += 22;
        float svW = cw - 30, svH = 120;
        float[] rgb = hsvToRgb(hue, 1, 1);
        col(c, 1, 1, 1, 1);
        col(c2, rgb[0], rgb[1], rgb[2], 1);
        NanoVG.nvgLinearGradient(vg, cx, y, cx + svW, y, c, c2, p);
        NanoVG.nvgBeginPath(vg);
        NanoVG.nvgRoundedRect(vg, cx, y, svW, svH, 6);
        NanoVG.nvgFillPaint(vg, p);
        NanoVG.nvgFill(vg);
        col(c, 0, 0, 0, 0);
        col(c2, 0, 0, 0, 1);
        NanoVG.nvgLinearGradient(vg, cx, y, cx, y + svH, c, c2, p);
        NanoVG.nvgBeginPath(vg);
        NanoVG.nvgRoundedRect(vg, cx, y, svW, svH, 6);
        NanoVG.nvgFillPaint(vg, p);
        NanoVG.nvgFill(vg);
        NanoVG.nvgBeginPath(vg);
        NanoVG.nvgRoundedRect(vg, cx, y, svW, svH, 6);
        col(c, 1, 1, 1, 0.1f);
        NanoVG.nvgStrokeColor(vg, c);
        NanoVG.nvgStrokeWidth(vg, 1);
        NanoVG.nvgStroke(vg);
        float scx = cx + sat * svW, scy = y + (1 - val) * svH;
        NanoVG.nvgBeginPath(vg);
        NanoVG.nvgCircle(vg, scx, scy, 7);
        col(c, 1, 1, 1, 0.9f);
        NanoVG.nvgStrokeColor(vg, c);
        NanoVG.nvgStrokeWidth(vg, 2);
        NanoVG.nvgStroke(vg);
        float[] sr = hsvToRgb(hue, sat, val);
        NanoVG.nvgBeginPath(vg);
        NanoVG.nvgCircle(vg, scx, scy, 5);
        col(c, sr[0], sr[1], sr[2], 1);
        NanoVG.nvgFillColor(vg, c);
        NanoVG.nvgFill(vg);
        if (inRect(mx, my, cx, y, svW, svH) && md) svDrag = true;
        if (svDrag) {
            sat = (float) Math.max(0, Math.min(1, (mx - cx) / svW));
            val = (float) Math.max(0, Math.min(1, 1 - (my - y) / svH));
        }

        float hx = cx + svW + 8, hw = 18;
        for (int i = 0; i < (int) svH; i++) {
            float[] hr = hsvToRgb((float) i / svH, 1, 1);
            NanoVG.nvgBeginPath(vg);
            NanoVG.nvgRect(vg, hx, y + i, hw, 1);
            col(c, hr[0], hr[1], hr[2], 1);
            NanoVG.nvgFillColor(vg, c);
            NanoVG.nvgFill(vg);
        }
        NanoVG.nvgBeginPath(vg);
        NanoVG.nvgRoundedRect(vg, hx, y, hw, svH, 4);
        col(c, 1, 1, 1, 0.1f);
        NanoVG.nvgStrokeColor(vg, c);
        NanoVG.nvgStroke(vg);
        float hcy = y + hue * svH;
        NanoVG.nvgBeginPath(vg);
        NanoVG.nvgRoundedRect(vg, hx - 2, hcy - 3, hw + 4, 6, 3);
        col(c, 1, 1, 1, 0.95f);
        NanoVG.nvgStrokeColor(vg, c);
        NanoVG.nvgStrokeWidth(vg, 2);
        NanoVG.nvgStroke(vg);
        if (inRect(mx, my, hx - 4, y, hw + 8, svH) && md) hueDrag = true;
        if (hueDrag) hue = (float) Math.max(0, Math.min(0.999, (my - y) / svH));

        y += svH + 10;
        NanoVG.nvgBeginPath(vg);
        NanoVG.nvgRoundedRect(vg, cx, y, cw, 24, 6);
        col(c, sr[0], sr[1], sr[2], 1);
        NanoVG.nvgFillColor(vg, c);
        NanoVG.nvgFill(vg);
        NanoVG.nvgFontFace(vg, "default");
        NanoVG.nvgFontSize(vg, 11);
        NanoVG.nvgTextAlign(vg, NanoVG.NVG_ALIGN_CENTER | NanoVG.NVG_ALIGN_MIDDLE);
        float lum = sr[0] * 0.299f + sr[1] * 0.587f + sr[2] * 0.114f;
        col(c, lum > 0.5f ? 0 : 1, lum > 0.5f ? 0 : 1, lum > 0.5f ? 0 : 1, 0.9f);
        NanoVG.nvgFillColor(vg, c);
        NanoVG.nvgText(vg, cx + cw / 2, y + 12, String.format("#%02X%02X%02X", (int) (sr[0] * 255), (int) (sr[1] * 255), (int) (sr[2] * 255)));
        y += 34;

        drawSection(vg, c, "Theme", cx, y);
        y += 22;
        for (int i = 0; i < themes.length; i++) {
            float rx = cx + (i % 2) * (cw / 2), ry = y + (i / 2) * 28;
            boolean rH = inRect(mx, my, rx, ry, cw / 2, 24);
            boolean sel = themeSelected == i;
            NanoVG.nvgBeginPath(vg);
            NanoVG.nvgCircle(vg, rx + 10, ry + 12, 7);
            col(c, 1, 1, 1, sel ? 0 : 0.1f);
            NanoVG.nvgFillColor(vg, c);
            NanoVG.nvgFill(vg);
            col(c, 0.4f, 0.6f, 1, sel ? 0.9f : 0.3f);
            NanoVG.nvgStrokeColor(vg, c);
            NanoVG.nvgStrokeWidth(vg, 1.5f);
            NanoVG.nvgStroke(vg);
            if (sel) {
                NanoVG.nvgBeginPath(vg);
                NanoVG.nvgCircle(vg, rx + 10, ry + 12, 4);
                col(c, 0.4f, 0.6f, 1, 1);
                NanoVG.nvgFillColor(vg, c);
                NanoVG.nvgFill(vg);
            }
            NanoVG.nvgFontFace(vg, "default");
            NanoVG.nvgFontSize(vg, 12);
            NanoVG.nvgTextAlign(vg, NanoVG.NVG_ALIGN_LEFT | NanoVG.NVG_ALIGN_MIDDLE);
            col(c, 1, 1, 1, rH ? 0.9f : 0.6f);
            NanoVG.nvgFillColor(vg, c);
            NanoVG.nvgText(vg, rx + 22, ry + 12, themes[i]);
            if (rH && cl) themeSelected = i;
        }
        y += 66;
        drawLabel(vg, c, "UI Scale: " + (int) (1 + uiScale * 3) + "x", cx, y);
        y += 16;
        uiScale = drawSlider(vg, c, c2, p, cx, y, cw, uiScale, mx, my, md, uiScaleDrag);
        if (inRect(mx, my, cx - 8, y, cw + 16, 20) && md) uiScaleDrag = true;
        y += 30;
        drawSection(vg, c, "HUD Options", cx, y);
        y += 22;
        showFps = drawCB(vg, c, "Show FPS", cx, y, showFps, mx, my, cl);
        y += 26;
        showCoords = drawCB(vg, c, "Show Coordinates", cx, y, showCoords, mx, my, cl);
        y += 26;
        showBiome = drawCB(vg, c, "Show Biome", cx, y, showBiome, mx, my, cl);
        y += 26;
        compactMode = drawCB(vg, c, "Compact Mode", cx, y, compactMode, mx, my, cl);
        y += 26;
        animatedBg = drawCB(vg, c, "Animated Background", cx, y, animatedBg, mx, my, cl);
    }

    private void renderAudio(long vg, NVGColor c, NVGColor c2, NVGPaint p, float cx, float cy, float cw, double mx, double my, boolean md, boolean cl) {
        float y = cy;
        drawSection(vg, c, "Volume Controls", cx, y);
        y += 22;
        y = drawVolSlider(vg, c, c2, p, "\u266B Master", masterVol, cx, y, cw, mx, md);
        if (inRect(mx, my, cx, y - 30, cw, 24) && md) masterVolDrag = true;
        if (masterVolDrag) masterVol = (float) Math.max(0, Math.min(1, (mx - cx) / cw));
        y = drawVolSlider(vg, c, c2, p, "\u266A Music", musicVol, cx, y, cw, mx, md);
        if (inRect(mx, my, cx, y - 30, cw, 24) && md) musicVolDrag = true;
        if (musicVolDrag) musicVol = (float) Math.max(0, Math.min(1, (mx - cx) / cw));
        y = drawVolSlider(vg, c, c2, p, "\u2606 Effects", sfxVol, cx, y, cw, mx, md);
        if (inRect(mx, my, cx, y - 30, cw, 24) && md) sfxVolDrag = true;
        if (sfxVolDrag) sfxVol = (float) Math.max(0, Math.min(1, (mx - cx) / cw));
        y = drawVolSlider(vg, c, c2, p, "\u2601 Ambient", ambientVol, cx, y, cw, mx, md);
        if (inRect(mx, my, cx, y - 30, cw, 24) && md) ambientVolDrag = true;
        if (ambientVolDrag) ambientVol = (float) Math.max(0, Math.min(1, (mx - cx) / cw));
        y = drawVolSlider(vg, c, c2, p, "\u2709 Voice", voiceVol, cx, y, cw, mx, md);
        if (inRect(mx, my, cx, y - 30, cw, 24) && md) voiceVolDrag = true;
        if (voiceVolDrag) voiceVol = (float) Math.max(0, Math.min(1, (mx - cx) / cw));
        y += 6;
        drawSection(vg, c, "Audio Options", cx, y);
        y += 22;
        muteAll = drawToggle(vg, c, "Mute All", cx, y, cw, muteAll, mx, my, cl);
        y += 32;
        subtitles = drawToggle(vg, c, "Subtitles", cx, y, cw, subtitles, mx, my, cl);
        y += 32;
        directionalAudio = drawToggle(vg, c, "Directional Audio", cx, y, cw, directionalAudio, mx, my, cl);
    }

    private void renderAdvanced(long vg, NVGColor c, NVGColor c2, NVGPaint p, float cx, float cy, float cw, double mx, double my, boolean md, boolean cl) {
        float y = cy;
        drawSection(vg, c, "Rendering", cx, y);
        y += 22;
        drawLabel(vg, c, "GPU Mode", cx, y);
        float ddY = y + 16;
        drawDDBox(vg, c, gpuModes[dropdown2Selected], cx, ddY, cw, mx, my, dropdown2Open);
        if (inRect(mx, my, cx, ddY, cw, 28) && cl) {
            dropdown2Open = !dropdown2Open;
            dropdownOpen = false;
        }
        y = ddY + 38;
        drawLabel(vg, c, "Gamma: " + String.format("%.1f", gamma * 2), cx, y);
        y += 16;
        gamma = drawSlider(vg, c, c2, p, cx, y, cw, gamma, mx, my, md, gammaDrag);
        if (inRect(mx, my, cx - 8, y, cw + 16, 20) && md) gammaDrag = true;
        y += 30;
        drawLabel(vg, c, "Chunk Updates: " + (int) (1 + chunkUpdates * 9), cx, y);
        y += 16;
        chunkUpdates = drawSlider(vg, c, c2, p, cx, y, cw, chunkUpdates, mx, my, md, chunkUpdatesDrag);
        if (inRect(mx, my, cx - 8, y, cw + 16, 20) && md) chunkUpdatesDrag = true;
        y += 36;
        drawSection(vg, c, "Features", cx, y);
        y += 22;
        entityShadows = drawCB(vg, c, "Entity Shadows", cx, y, entityShadows, mx, my, cl);
        y += 26;
        particles = drawCB(vg, c, "Particles", cx, y, particles, mx, my, cl);
        y += 26;
        clouds = drawCB(vg, c, "Clouds", cx, y, clouds, mx, my, cl);
        y += 26;
        smoothChunk = drawCB(vg, c, "Smooth Chunk Anim", cx, y, smoothChunk, mx, my, cl);
        y += 26;
        antiAlias = drawCB(vg, c, "Anti-Aliasing", cx, y, antiAlias, mx, my, cl);
        y += 36;
        drawSection(vg, c, "Shader Compilation", cx, y);
        y += 22;
        if (loadingActive) {
            loadProgress += lastDelta * 0.01f;
            if (loadProgress >= 1) {
                loadProgress = 1;
                loadingActive = false;
            }
        }
        drawProgress(vg, c, c2, p, cx, y, cw, loadProgress);
        y += 24;
        boolean rbH = inRect(mx, my, cx, y, 110, 28);
        drawBtn(vg, c, c2, p, "Rebuild", cx, y, 110, 28, rbH, true);
        if (rbH && cl) {
            loadProgress = 0;
            loadingActive = true;
        }
    }

    private void drawSection(long vg, NVGColor c, String t, float x, float y) {
        NanoVG.nvgFontFace(vg, "default");
        NanoVG.nvgFontSize(vg, 10);
        NanoVG.nvgTextAlign(vg, NanoVG.NVG_ALIGN_LEFT | NanoVG.NVG_ALIGN_MIDDLE);
        col(c, 0.4f, 0.6f, 1, 0.7f);
        NanoVG.nvgFillColor(vg, c);
        NanoVG.nvgText(vg, x, y + 6, t.toUpperCase());
    }

    private void drawLabel(long vg, NVGColor c, String t, float x, float y) {
        NanoVG.nvgFontFace(vg, "default");
        NanoVG.nvgFontSize(vg, 12);
        NanoVG.nvgTextAlign(vg, NanoVG.NVG_ALIGN_LEFT | NanoVG.NVG_ALIGN_MIDDLE);
        col(c, 1, 1, 1, 0.55f);
        NanoVG.nvgFillColor(vg, c);
        NanoVG.nvgText(vg, x, y + 6, t);
    }

    private float drawSlider(long vg, NVGColor c, NVGColor c2, NVGPaint p, float x, float y, float w, float v, double mx, double my, boolean md, boolean drag) {
        float ty = y + 6;
        NanoVG.nvgBeginPath(vg);
        NanoVG.nvgRoundedRect(vg, x, ty, w, 6, 3);
        col(c, 1, 1, 1, 0.07f);
        NanoVG.nvgFillColor(vg, c);
        NanoVG.nvgFill(vg);
        float fw = w * v;
        col(c, 0.3f, 0.5f, 1, 0.5f);
        col(c2, 0.5f, 0.7f, 1, 0.7f);
        NanoVG.nvgLinearGradient(vg, x, ty, x + fw, ty, c, c2, p);
        NanoVG.nvgBeginPath(vg);
        NanoVG.nvgRoundedRect(vg, x, ty, fw, 6, 3);
        NanoVG.nvgFillPaint(vg, p);
        NanoVG.nvgFill(vg);
        float kx = x + fw, ky = ty + 3;
        boolean kH = inRect(mx, my, kx - 8, ky - 8, 16, 16) || drag;
        if (kH) {
            NanoVG.nvgBeginPath(vg);
            NanoVG.nvgCircle(vg, kx, ky, 10);
            col(c, 0.4f, 0.6f, 1, 0.12f);
            NanoVG.nvgFillColor(vg, c);
            NanoVG.nvgFill(vg);
        }
        NanoVG.nvgBeginPath(vg);
        NanoVG.nvgCircle(vg, kx, ky, 6);
        col(c, 0.45f, 0.65f, 1, 1);
        NanoVG.nvgFillColor(vg, c);
        NanoVG.nvgFill(vg);
        NanoVG.nvgBeginPath(vg);
        NanoVG.nvgCircle(vg, kx, ky, 2.5f);
        col(c, 1, 1, 1, 0.85f);
        NanoVG.nvgFillColor(vg, c);
        NanoVG.nvgFill(vg);
        if (drag) return (float) Math.max(0, Math.min(1, (mx - x) / w));
        return v;
    }

    private boolean drawToggle(long vg, NVGColor c, String label, float x, float y, float w, boolean on, double mx, double my, boolean cl) {
        NanoVG.nvgFontFace(vg, "default");
        NanoVG.nvgFontSize(vg, 12);
        NanoVG.nvgTextAlign(vg, NanoVG.NVG_ALIGN_LEFT | NanoVG.NVG_ALIGN_MIDDLE);
        col(c, 1, 1, 1, 0.7f);
        NanoVG.nvgFillColor(vg, c);
        NanoVG.nvgText(vg, x, y + 11, label);
        float tX = x + w - 40, tY = y + 2, tW = 40, tH = 20;
        boolean h = inRect(mx, my, tX, tY, tW, tH);
        NanoVG.nvgBeginPath(vg);
        NanoVG.nvgRoundedRect(vg, tX, tY, tW, tH, 10);
        if (on) col(c, 0.35f, 0.55f, 1, h ? 0.8f : 0.6f);
        else col(c, 1, 1, 1, h ? 0.13f : 0.07f);
        NanoVG.nvgFillColor(vg, c);
        NanoVG.nvgFill(vg);
        float ko = on ? tW - tH + 2 : 2;
        NanoVG.nvgBeginPath(vg);
        NanoVG.nvgCircle(vg, tX + ko + (tH - 4) / 2, tY + tH / 2, (tH - 4) / 2);
        col(c, 1, 1, 1, on ? 0.95f : 0.55f);
        NanoVG.nvgFillColor(vg, c);
        NanoVG.nvgFill(vg);
        if (h && cl) return !on;
        return on;
    }

    private boolean drawCB(long vg, NVGColor c, String label, float x, float y, boolean chk, double mx, double my, boolean cl) {
        float bx = x, by = y + 2, bs = 16;
        boolean h = inRect(mx, my, bx, by, bs + 100, bs);
        NanoVG.nvgBeginPath(vg);
        NanoVG.nvgRoundedRect(vg, bx, by, bs, bs, 4);
        if (chk) col(c, 0.35f, 0.55f, 1, h ? 0.9f : 0.7f);
        else col(c, 1, 1, 1, h ? 0.15f : 0.08f);
        NanoVG.nvgFillColor(vg, c);
        NanoVG.nvgFill(vg);
        if (!chk) {
            NanoVG.nvgBeginPath(vg);
            NanoVG.nvgRoundedRect(vg, bx, by, bs, bs, 4);
            col(c, 1, 1, 1, 0.15f);
            NanoVG.nvgStrokeColor(vg, c);
            NanoVG.nvgStrokeWidth(vg, 1);
            NanoVG.nvgStroke(vg);
        }
        if (chk) {
            col(c, 1, 1, 1, 0.95f);
            NanoVG.nvgStrokeColor(vg, c);
            NanoVG.nvgStrokeWidth(vg, 2);
            NanoVG.nvgBeginPath(vg);
            NanoVG.nvgMoveTo(vg, bx + 3.5f, by + 8);
            NanoVG.nvgLineTo(vg, bx + 6.5f, by + 12);
            NanoVG.nvgLineTo(vg, bx + 12.5f, by + 4);
            NanoVG.nvgStroke(vg);
        }
        NanoVG.nvgFontFace(vg, "default");
        NanoVG.nvgFontSize(vg, 12);
        NanoVG.nvgTextAlign(vg, NanoVG.NVG_ALIGN_LEFT | NanoVG.NVG_ALIGN_MIDDLE);
        col(c, 1, 1, 1, h ? 0.85f : 0.6f);
        NanoVG.nvgFillColor(vg, c);
        NanoVG.nvgText(vg, bx + bs + 8, by + bs / 2, label);
        if (h && cl) return !chk;
        return chk;
    }

    private void drawDDBox(long vg, NVGColor c, String text, float x, float y, float w, double mx, double my, boolean open) {
        boolean h = inRect(mx, my, x, y, w, 28);
        NanoVG.nvgBeginPath(vg);
        NanoVG.nvgRoundedRect(vg, x, y, w, 28, 6);
        col(c, 1, 1, 1, h || open ? 0.10f : 0.05f);
        NanoVG.nvgFillColor(vg, c);
        NanoVG.nvgFill(vg);
        NanoVG.nvgBeginPath(vg);
        NanoVG.nvgRoundedRect(vg, x, y, w, 28, 6);
        col(c, 1, 1, 1, open ? 0.2f : 0.1f);
        NanoVG.nvgStrokeColor(vg, c);
        NanoVG.nvgStrokeWidth(vg, 1);
        NanoVG.nvgStroke(vg);
        NanoVG.nvgFontFace(vg, "default");
        NanoVG.nvgFontSize(vg, 12);
        NanoVG.nvgTextAlign(vg, NanoVG.NVG_ALIGN_LEFT | NanoVG.NVG_ALIGN_MIDDLE);
        col(c, 1, 1, 1, 0.85f);
        NanoVG.nvgFillColor(vg, c);
        NanoVG.nvgText(vg, x + 10, y + 14, text);
        float ax = x + w - 18, ay = y + 14;
        col(c, 1, 1, 1, 0.4f);
        NanoVG.nvgStrokeColor(vg, c);
        NanoVG.nvgStrokeWidth(vg, 1.5f);
        NanoVG.nvgBeginPath(vg);
        if (open) {
            NanoVG.nvgMoveTo(vg, ax - 4, ay + 2);
            NanoVG.nvgLineTo(vg, ax, ay - 3);
            NanoVG.nvgLineTo(vg, ax + 4, ay + 2);
        } else {
            NanoVG.nvgMoveTo(vg, ax - 4, ay - 2);
            NanoVG.nvgLineTo(vg, ax, ay + 3);
            NanoVG.nvgLineTo(vg, ax + 4, ay - 2);
        }
        NanoVG.nvgStroke(vg);
    }

    private void renderDDPopup(long vg, NVGColor c, NVGColor c2, NVGPaint p, float x, float y, float w, String[] items, int sel, double mx, double my, boolean cl, int id) {
        float py2 = y + 4, ph = items.length * 28 + 8;
        col(c, 0, 0, 0, 0);
        col(c2, 0, 0, 0, 0.5f);
        NanoVG.nvgBoxGradient(vg, x, py2 + 2, w, ph, 6, 10, c2, c, p);
        NanoVG.nvgBeginPath(vg);
        NanoVG.nvgRect(vg, x - 15, py2 - 15, w + 30, ph + 30);
        NanoVG.nvgFillPaint(vg, p);
        NanoVG.nvgFill(vg);
        NanoVG.nvgBeginPath(vg);
        NanoVG.nvgRoundedRect(vg, x, py2, w, ph, 6);
        col(c, 0.14f, 0.14f, 0.18f, 0.98f);
        NanoVG.nvgFillColor(vg, c);
        NanoVG.nvgFill(vg);
        NanoVG.nvgBeginPath(vg);
        NanoVG.nvgRoundedRect(vg, x, py2, w, ph, 6);
        col(c, 1, 1, 1, 0.1f);
        NanoVG.nvgStrokeColor(vg, c);
        NanoVG.nvgStroke(vg);
        for (int i = 0; i < items.length; i++) {
            float iy = py2 + 4 + i * 28;
            boolean iH = inRect(mx, my, x, iy, w, 28);
            if (iH) {
                NanoVG.nvgBeginPath(vg);
                NanoVG.nvgRoundedRect(vg, x + 4, iy, w - 8, 28, 4);
                col(c, 0.35f, 0.55f, 1, 0.18f);
                NanoVG.nvgFillColor(vg, c);
                NanoVG.nvgFill(vg);
            }
            NanoVG.nvgFontFace(vg, "default");
            NanoVG.nvgFontSize(vg, 12);
            NanoVG.nvgTextAlign(vg, NanoVG.NVG_ALIGN_LEFT | NanoVG.NVG_ALIGN_MIDDLE);
            col(c, 1, 1, 1, i == sel ? 0.95f : (iH ? 0.8f : 0.55f));
            NanoVG.nvgFillColor(vg, c);
            NanoVG.nvgText(vg, x + 12, iy + 14, items[i]);
            if (i == sel) {
                col(c, 0.4f, 0.65f, 1, 0.9f);
                NanoVG.nvgFillColor(vg, c);
                NanoVG.nvgTextAlign(vg, NanoVG.NVG_ALIGN_RIGHT | NanoVG.NVG_ALIGN_MIDDLE);
                NanoVG.nvgText(vg, x + w - 12, iy + 14, "\u2713");
            }
            if (iH && cl) {
                if (id == 0) {
                    dropdownSelected = i;
                    dropdownOpen = false;
                } else {
                    dropdown2Selected = i;
                    dropdown2Open = false;
                }
            }
        }
        if (cl && !inRect(mx, my, x, py2, w, ph)) {
            if (id == 0) dropdownOpen = false;
            else dropdown2Open = false;
        }
    }

    private float drawVolSlider(long vg, NVGColor c, NVGColor c2, NVGPaint p, String label, float v, float x, float y, float w, double mx, boolean md) {
        NanoVG.nvgFontFace(vg, "default");
        NanoVG.nvgFontSize(vg, 11);
        NanoVG.nvgTextAlign(vg, NanoVG.NVG_ALIGN_LEFT | NanoVG.NVG_ALIGN_MIDDLE);
        col(c, 1, 1, 1, 0.55f);
        NanoVG.nvgFillColor(vg, c);
        NanoVG.nvgText(vg, x, y + 5, label);
        NanoVG.nvgTextAlign(vg, NanoVG.NVG_ALIGN_RIGHT | NanoVG.NVG_ALIGN_MIDDLE);
        col(c, 1, 1, 1, 0.4f);
        NanoVG.nvgFillColor(vg, c);
        NanoVG.nvgText(vg, x + w, y + 5, (int) (v * 100) + "%");
        float by = y + 16, bh = 8, fw = w * v;
        NanoVG.nvgBeginPath(vg);
        NanoVG.nvgRoundedRect(vg, x, by, w, bh, 4);
        col(c, 1, 1, 1, 0.06f);
        NanoVG.nvgFillColor(vg, c);
        NanoVG.nvgFill(vg);
        float r = v > 0.8f ? 0.9f : 0.35f, g = v > 0.8f ? 0.3f : 0.55f, b = v > 0.8f ? 0.2f : 1.0f;
        col(c, r * 0.7f, g * 0.7f, b * 0.7f, 0.6f);
        col(c2, r, g, b, 0.8f);
        NanoVG.nvgLinearGradient(vg, x, by, x + fw, by, c, c2, p);
        NanoVG.nvgBeginPath(vg);
        NanoVG.nvgRoundedRect(vg, x, by, fw, bh, 4);
        NanoVG.nvgFillPaint(vg, p);
        NanoVG.nvgFill(vg);
        for (int i = 1; i < 10; i++) {
            float sx = x + (w * i / 10);
            NanoVG.nvgBeginPath(vg);
            NanoVG.nvgMoveTo(vg, sx, by);
            NanoVG.nvgLineTo(vg, sx, by + bh);
            col(c, 0, 0, 0, 0.2f);
            NanoVG.nvgStrokeColor(vg, c);
            NanoVG.nvgStrokeWidth(vg, 1);
            NanoVG.nvgStroke(vg);
        }
        return y + 36;
    }

    private void drawProgress(long vg, NVGColor c, NVGColor c2, NVGPaint p, float x, float y, float w, float prog) {
        float h = 12;
        NanoVG.nvgBeginPath(vg);
        NanoVG.nvgRoundedRect(vg, x, y, w, h, 6);
        col(c, 1, 1, 1, 0.06f);
        NanoVG.nvgFillColor(vg, c);
        NanoVG.nvgFill(vg);
        float fw = w * prog;
        if (fw > 0) {
            col(c, 0.2f, 0.8f, 0.4f, 0.6f);
            col(c2, 0.3f, 0.9f, 0.5f, 0.9f);
            NanoVG.nvgLinearGradient(vg, x, y, x + fw, y, c, c2, p);
            NanoVG.nvgBeginPath(vg);
            NanoVG.nvgRoundedRect(vg, x, y, fw, h, 6);
            NanoVG.nvgFillPaint(vg, p);
            NanoVG.nvgFill(vg);
        }
        NanoVG.nvgFontFace(vg, "default");
        NanoVG.nvgFontSize(vg, 9);
        NanoVG.nvgTextAlign(vg, NanoVG.NVG_ALIGN_CENTER | NanoVG.NVG_ALIGN_MIDDLE);
        col(c, 1, 1, 1, 0.8f);
        NanoVG.nvgFillColor(vg, c);
        NanoVG.nvgText(vg, x + w / 2, y + h / 2, (int) (prog * 100) + "%");
    }

    private void drawBtn(long vg, NVGColor c, NVGColor c2, NVGPaint p, String t, float x, float y, float w, float h, boolean hov, boolean primary) {
        NanoVG.nvgBeginPath(vg);
        NanoVG.nvgRoundedRect(vg, x, y, w, h, 8);
        if (primary) {
            if (hov) {
                col(c, 0.35f, 0.6f, 1, 0.95f);
                col(c2, 0.5f, 0.7f, 1, 0.95f);
            } else {
                col(c, 0.3f, 0.5f, 1, 0.7f);
                col(c2, 0.4f, 0.6f, 1, 0.7f);
            }
            NanoVG.nvgLinearGradient(vg, x, y, x + w, y, c, c2, p);
            NanoVG.nvgFillPaint(vg, p);
        } else {
            col(c, 1, 1, 1, hov ? 0.10f : 0.04f);
            NanoVG.nvgFillColor(vg, c);
        }
        NanoVG.nvgFill(vg);
        if (!primary) {
            NanoVG.nvgBeginPath(vg);
            NanoVG.nvgRoundedRect(vg, x, y, w, h, 8);
            col(c, 1, 1, 1, 0.12f);
            NanoVG.nvgStrokeColor(vg, c);
            NanoVG.nvgStrokeWidth(vg, 1);
            NanoVG.nvgStroke(vg);
        }
        NanoVG.nvgFontFace(vg, "default");
        NanoVG.nvgFontSize(vg, 12);
        NanoVG.nvgTextAlign(vg, NanoVG.NVG_ALIGN_CENTER | NanoVG.NVG_ALIGN_MIDDLE);
        col(c, 1, 1, 1, primary ? 0.95f : (hov ? 0.85f : 0.55f));
        NanoVG.nvgFillColor(vg, c);
        NanoVG.nvgText(vg, x + w / 2, y + h / 2, t);
    }
}