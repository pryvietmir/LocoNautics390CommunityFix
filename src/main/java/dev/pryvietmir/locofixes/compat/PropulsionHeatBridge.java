package dev.pryvietmir.locofixes.compat;

import com.lycoris.loconautics.compat.heat.PropulsionEngineHeat;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;

/**
 * Replacement for {@code PropulsionEngineHeat.create(...)} inside SmallEngineBlockEntity.
 *
 * <p>The original returns Create Propulsion's HeatSourceBehavior and stores it straight into a
 * BlockEntityBehaviour field. To verify that store the JVM loads HeatSourceBehavior as soon as
 * SmallEngineBlockEntity is loaded, so without Propulsion installed the game crashes on placing
 * the engine, opening its ponder, etc. — even though the call itself is behind an isLoaded() check.
 *
 * <p>This bridge returns the base type and goes through Object, so neither this class nor the
 * engine mentions Propulsion types in a way the verifier has to check. It is only ever executed
 * when Propulsion is present.
 */
public final class PropulsionHeatBridge {
    public static final String OWNER = "dev/pryvietmir/locofixes/compat/PropulsionHeatBridge";
    public static final String DESC =
            "(Lcom/simibubi/create/foundation/blockEntity/SmartBlockEntity;)Lcom/simibubi/create/foundation/blockEntity/behaviour/BlockEntityBehaviour;";

    private PropulsionHeatBridge() {}

    public static BlockEntityBehaviour create(SmartBlockEntity engine) {
        Object behaviour = PropulsionEngineHeat.create(engine);
        return (BlockEntityBehaviour) behaviour;
    }
}
