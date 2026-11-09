package dev.pryvietmir.locofixes;

import com.mojang.logging.LogUtils;
import dev.pryvietmir.locofixes.client.ClientCleanup;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.config.Configurator;
import org.slf4j.Logger;

@Mod(LocoFixes.MOD_ID)
public final class LocoFixes {
    public static final String MOD_ID = "loconautics_fixes";
    public static final Logger LOG = LogUtils.getLogger();

    /**
     * Loconautics получает логгер через LogUtils.getLogger() внутри LoconauticsConstants,
     * поэтому имя логгера — полное имя этого класса. Все ~320 отладочных info-сообщений
     * идут через него.
     */
    private static final String LOCONAUTICS_LOGGER = "com.lycoris.loconautics.LoconauticsConstants";

    public LocoFixes(IEventBus modBus, ModContainer container) {
        container.registerConfig(ModConfig.Type.COMMON, LocoFixesConfig.COMMON_SPEC);
        container.registerConfig(ModConfig.Type.SERVER, LocoFixesConfig.SERVER_SPEC);
        modBus.addListener(LocoFixes::onConfig);
        modBus.addListener(LocoFixes::onConfigReload);

        if (FMLEnvironment.dist.isClient()) {
            ClientCleanup.register();
        }
    }

    private static void onConfig(ModConfigEvent.Loading event) {
        if (event.getConfig().getSpec() == LocoFixesConfig.COMMON_SPEC) applyLogLevel();
    }

    private static void onConfigReload(ModConfigEvent.Reloading event) {
        if (event.getConfig().getSpec() == LocoFixesConfig.COMMON_SPEC) applyLogLevel();
    }

    /** Оставляет от логгера Loconautics только WARN и ERROR — отладочные трассы в INFO отсекаются. */
    private static void applyLogLevel() {
        try {
            Level level = LocoFixesConfig.QUIET_INFO_LOGS.get()
                    ? Level.WARN
                    : LogManager.getRootLogger().getLevel();
            Configurator.setLevel(LOCONAUTICS_LOGGER, level);
            LOG.info("Loconautics log level set to {}", level);
        } catch (Throwable t) {
            // Не log4j-core в рантайме (нестандартный лаунчер) — просто оставляем как есть.
            LOG.warn("Could not change Loconautics log level", t);
        }
    }
}
