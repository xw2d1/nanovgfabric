package com.github.xw2d1;

import com.github.xw2d1.screen.NanoVGTestScreen;
import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NanoVGFabric implements ModInitializer {
    public static final String MOD_ID = "nanovgfabric";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    private static final KeyMapping OPEN_SCREEN = KeyBindingHelper.registerKeyBinding(
            new KeyMapping(
                    "key.mymod.open_screen",
                    InputConstants.Type.KEYSYM,
                    GLFW.GLFW_KEY_R,
                    KeyMapping.Category.DEBUG
            )
    );

    @Override
    public void onInitialize() {
        LOGGER.info("Hello Fabric world!");

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (OPEN_SCREEN.consumeClick()) {
                client.setScreen(new NanoVGTestScreen());
            }
        });
    }
}