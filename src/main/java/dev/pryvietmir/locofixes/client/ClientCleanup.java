package dev.pryvietmir.locofixes.client;

import dev.pryvietmir.locofixes.LocoFixes;
import java.lang.reflect.Field;
import java.util.Map;
import net.minecraft.world.level.LevelAccessor;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.level.LevelEvent;

/**
 * Memory leak fix: RailSeams.CLIENT_LEVELS keeps references to ClientLevels (with all their chunks
 * and entities) after leaving a world. Cleared on level unload and on disconnect.
 *
 * <p>Uses plain reflection rather than a Mixin accessor: classes in a mixin package must never be
 * loaded directly, and calling a static accessor from here did exactly that (crash on joining a
 * server in 1.0.0-1.0.2). Any failure here is logged once and ignored - it must never crash the game.
 * Only loaded on the client (see LocoFixes).
 */
public final class ClientCleanup {
    private static final String RAIL_SEAMS = "com.lycoris.loconautics.trainphysics.rail.RailSeams";

    private static Map<?, ?> clientLevels;
    private static boolean failed;

    private ClientCleanup() {}

    public static void register() {
        NeoForge.EVENT_BUS.addListener(ClientCleanup::onLevelUnload);
        NeoForge.EVENT_BUS.addListener(ClientCleanup::onLoggingOut);
    }

    private static Map<?, ?> clientLevels() {
        if (clientLevels == null && !failed) {
            try {
                Field field = Class.forName(RAIL_SEAMS).getDeclaredField("CLIENT_LEVELS");
                field.setAccessible(true);
                clientLevels = (Map<?, ?>) field.get(null);
            } catch (Throwable t) {
                failed = true;
                LocoFixes.LOG.warn("[loconautics_fixes] Could not reach RailSeams.CLIENT_LEVELS, client level cleanup disabled", t);
            }
        }
        return clientLevels;
    }

    private static void onLevelUnload(LevelEvent.Unload event) {
        LevelAccessor level = event.getLevel();
        if (!level.isClientSide()) return;
        try {
            Map<?, ?> levels = clientLevels();
            if (levels != null) levels.values().removeIf(l -> l == level);
        } catch (Throwable t) {
            LocoFixes.LOG.warn("[loconautics_fixes] Client level cleanup failed", t);
        }
    }

    private static void onLoggingOut(ClientPlayerNetworkEvent.LoggingOut event) {
        try {
            Map<?, ?> levels = clientLevels();
            if (levels != null) levels.clear();
        } catch (Throwable t) {
            LocoFixes.LOG.warn("[loconautics_fixes] Client level cleanup failed", t);
        }
    }
}
