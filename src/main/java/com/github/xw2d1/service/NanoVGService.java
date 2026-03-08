package com.github.xw2d1.service;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import org.lwjgl.nanovg.NanoVG;
import org.lwjgl.nanovg.NanoVGGL3;

import java.io.InputStream;

public class NanoVGService {
    private static long vg = -1;
    private static boolean fontsLoaded = false;
    private static java.nio.ByteBuffer fontBuffer;

    public static long get() {
        if (vg == -1) {
            vg = NanoVGGL3.nvgCreate(NanoVGGL3.NVG_ANTIALIAS | NanoVGGL3.NVG_DEBUG);
            if (vg == -1) throw new RuntimeException("Failed to create NanoVG context");
            System.out.println("[NanoVG] Created context: " + vg);
            fontsLoaded = false;
        }
        if (!fontsLoaded) {
            loadFonts();
            fontsLoaded = true;
        }
        return vg;
    }

    private static void loadFonts() {
        System.out.println("[NanoVG] Loading fonts on context: " + vg);
        try {
            Identifier fontId = Identifier.fromNamespaceAndPath("nanovgfabric", "font/delight-regular.ttf");
            var resource = Minecraft.getInstance().getResourceManager().getResource(fontId);

            if (resource.isEmpty()) {
                System.out.println("[NanoVG] ERROR: Font resource not found!");
                return;
            }

            InputStream is = resource.get().open();
            byte[] bytes = is.readAllBytes();
            is.close();
            System.out.println("[NanoVG] Font bytes: " + bytes.length);

            if (fontBuffer != null) {
                org.lwjgl.system.MemoryUtil.memFree(fontBuffer);
            }
            fontBuffer = org.lwjgl.system.MemoryUtil.memAlloc(bytes.length);
            fontBuffer.put(bytes).flip();

            int font = NanoVG.nvgCreateFontMem(vg, "default", fontBuffer, false);
            System.out.println("[NanoVG] Font registered: " + font);

            int check = NanoVG.nvgFindFont(vg, "default");
            System.out.println("[NanoVG] Verify findFont: " + check);
        } catch (Exception e) {
            System.out.println("[NanoVG] ERROR: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void destroy() {
        if (vg != -1) {
            NanoVGGL3.nvgDelete(vg);
            vg = -1;
            fontsLoaded = false;
        }
        if (fontBuffer != null) {
            org.lwjgl.system.MemoryUtil.memFree(fontBuffer);
            fontBuffer = null;
        }
    }
}
