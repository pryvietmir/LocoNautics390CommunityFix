package dev.pryvietmir.locofixes.client.ponder;

import net.createmod.ponder.api.registration.PonderPlugin;
import net.createmod.ponder.api.registration.PonderSceneRegistrationHelper;
import net.minecraft.resources.ResourceLocation;

/**
 * Extra Ponder scenes for Loconautics blocks that ship without one. Blocks are referenced by id only,
 * so nothing here depends on Loconautics classes.
 */
public final class LocoFixesPonderPlugin implements PonderPlugin {
    @Override
    public String getModId() {
        return "loconautics_fixes";
    }

    @Override
    public void registerScenes(PonderSceneRegistrationHelper<ResourceLocation> helper) {
        helper.forComponents(loco("transmission"))
                .addStoryBoard("transmission", ExtraScenes::transmission);
        helper.forComponents(controllers())
                .addStoryBoard("analog_controller", ExtraScenes::analogController);
        helper.forComponents(loco("bearing_axle"))
                .addStoryBoard("bearing_axle", ExtraScenes::bearingAxle);
        helper.forComponents(loco("handbrake"))
                .addStoryBoard("handbrake", ExtraScenes::handbrake);
        helper.forComponents(loco("knuckle"))
                .addStoryBoard("knuckle", ExtraScenes::knuckle);
        helper.forComponents(loco("screw_link"))
                .addStoryBoard("screw_link", ExtraScenes::screwLink);
        helper.forComponents(loco("copycat_headstock"))
                .addStoryBoard("copycat_headstock", ExtraScenes::copycatHeadstock);
        helper.forComponents(loco("turntable_bearing"), loco("turntable_plate"))
                .addStoryBoard("turntable", ExtraScenes::turntable);
        helper.forComponents(loco("industrial_pump"), loco("industrial_pipe"), loco("industrial_glass_pipe"),
                        loco("industrial_smart_pipe"), loco("industrial_valve"))
                .addStoryBoard("industrial", ExtraScenes::industrialPipes);
        helper.forComponents(loco("industrial_fluid_tank"))
                .addStoryBoard("industrial", ExtraScenes::industrialTank);
        helper.forComponents(loco("hose_connector"))
                .addStoryBoard("hoses", ExtraScenes::hoseConnector);
        helper.forComponents(loco("fluid_hatch_gray"))
                .addStoryBoard("hoses", ExtraScenes::nozzleSocket);
    }

    static ResourceLocation loco(String path) {
        return ResourceLocation.fromNamespaceAndPath("loconautics", path);
    }

    private static ResourceLocation[] controllers() {
        String[] colors = {"white", "orange", "magenta", "light_blue", "yellow", "lime", "pink", "gray",
                "light_gray", "cyan", "purple", "blue", "brown", "green", "red", "black"};
        ResourceLocation[] ids = new ResourceLocation[colors.length];
        for (int i = 0; i < colors.length; i++) {
            ids[i] = loco(colors[i] + "_analog_controller");
        }
        return ids;
    }
}
