package dev.pryvietmir.locofixes.client.ponder;

import net.createmod.ponder.foundation.PonderIndex;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;

/** Client only: registers the extra Ponder scenes. */
public final class LocoFixesPonder {
    private LocoFixesPonder() {
    }

    public static void register(IEventBus modBus) {
        modBus.addListener(LocoFixesPonder::onClientSetup);
    }

    private static void onClientSetup(FMLClientSetupEvent event) {
        PonderIndex.addPlugin(new LocoFixesPonderPlugin());
    }
}
