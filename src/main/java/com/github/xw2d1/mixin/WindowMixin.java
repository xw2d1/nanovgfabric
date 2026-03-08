package com.github.xw2d1.mixin;

import com.github.xw2d1.screen.NanoVGTestScreen;
import com.mojang.blaze3d.platform.Window;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Window.class)
public class WindowMixin {

    @Inject(method = "updateDisplay", at = @At("HEAD"))
    private void beforeSwap(CallbackInfo ci) {
        if (Minecraft.getInstance().screen instanceof NanoVGTestScreen nanoScreen) {
            nanoScreen.renderNanoVG();
        }
    }
}
