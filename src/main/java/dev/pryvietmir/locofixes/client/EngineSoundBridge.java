package dev.pryvietmir.locofixes.client;

import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundManager;

/**
 * Called from the rewritten SmallEngineBlockEntity instead of SoundManager.play(SoundInstance).
 * Taking Object keeps the engine class verifiable on a dedicated server; this class itself is only
 * loaded when the call actually runs, which happens on the client.
 */
public final class EngineSoundBridge {
    private EngineSoundBridge() {
    }

    public static void play(SoundManager manager, Object sound) {
        manager.play((SoundInstance) sound);
    }
}
