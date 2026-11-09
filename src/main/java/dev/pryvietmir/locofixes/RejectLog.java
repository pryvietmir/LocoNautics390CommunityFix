package dev.pryvietmir.locofixes;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.server.level.ServerPlayer;

/**
 * Logs rejected (likely forged) packets, at most once per player per {@link #INTERVAL_MS}.
 * Without a limit a cheating client could flood the server log by spamming rejected packets.
 */
public final class RejectLog {
    private static final long INTERVAL_MS = 10_000L;
    private static final Map<UUID, Long> LAST = new ConcurrentHashMap<>();

    private RejectLog() {}

    public static void warn(ServerPlayer player, String what) {
        long now = System.currentTimeMillis();
        UUID id = player.getUUID();
        Long last = LAST.get(id);
        if (last != null && now - last < INTERVAL_MS) {
            return;
        }
        LAST.put(id, now);
        if (LAST.size() > 512) {
            LAST.values().removeIf(t -> now - t > INTERVAL_MS);
        }
        LocoFixes.LOG.warn("[loconautics_fixes] {} rejected: {}", player.getGameProfile().getName(), what);
    }
}
