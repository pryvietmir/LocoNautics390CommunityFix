package dev.pryvietmir.locofixes;

import net.neoforged.neoforge.common.ModConfigSpec;

public final class LocoFixesConfig {
    /** Per-world server settings (config/…/serverconfig in the world folder). */
    public static final ModConfigSpec SERVER_SPEC;
    public static final ModConfigSpec.BooleanValue REQUIRE_OWNERSHIP;

    /** Per-installation settings, used on both client and server. */
    public static final ModConfigSpec COMMON_SPEC;
    public static final ModConfigSpec.BooleanValue QUIET_INFO_LOGS;

    static {
        ModConfigSpec.Builder server = new ModConfigSpec.Builder();
        server.push("relocation");
        REQUIRE_OWNERSHIP = server
                .comment("Only the train's owner or an operator (permission level 2+) may move it with the relocator.",
                        "Holding the relocator is always required.")
                .define("requireOwnership", false);
        server.pop();
        SERVER_SPEC = server.build();

        ModConfigSpec.Builder common = new ModConfigSpec.Builder();
        common.push("logging");
        QUIET_INFO_LOGS = common
                .comment("Hide Loconautics debug INFO messages ([maneuver], [boilerfix], [issue274], ...).",
                        "Warnings and errors are kept.")
                .define("quietInfo", true);
        common.pop();
        COMMON_SPEC = common.build();
    }

    private LocoFixesConfig() {}
}
