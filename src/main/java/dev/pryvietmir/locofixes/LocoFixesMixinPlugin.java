package dev.pryvietmir.locofixes;

import java.util.List;
import java.util.Set;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.fml.loading.moddiscovery.ModFileInfo;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

/**
 * Миксины нацелены на внутренности конкретной сборки Loconautics (в т.ч. на синтетическую
 * лямбду lambda$handle$1). В другой версии имена могут поменяться, и жёсткий require=1
 * уронил бы игру при запуске. Поэтому патч применяется только к проверенной версии,
 * а на любой другой честно отключается с предупреждением в логе.
 */
public final class LocoFixesMixinPlugin implements IMixinConfigPlugin {
    private static final Logger LOG = LoggerFactory.getLogger("loconautics_fixes");
    private static final Set<String> TESTED_VERSIONS = Set.of("0.39");

    private boolean enabled = true;
    /** Running on a rebuilt fork ("0.39+..."): only the wheel debug line mixins make sense there. */
    private boolean forkBuild = false;

    /** Mixins whose targets are plain named methods that exist in fork builds too. */
    private static final Set<String> FORK_SAFE = Set.of(
            "dev.pryvietmir.locofixes.mixin.WheelSupportsMixin",
            "dev.pryvietmir.locofixes.mixin.HoseLinkWrenchMixin",
            "dev.pryvietmir.locofixes.mixin.SmallEngineBlockEntityMixin",
            "dev.pryvietmir.locofixes.mixin.DebugLinesClientMixin");

    @Override
    public void onLoad(String mixinPackage) {
        String version = loconauticsVersion();
        if (version == null) {
            LOG.warn("[loconautics_fixes] Не удалось определить версию Loconautics — применяю патч как есть.");
            return;
        }
        if (TESTED_VERSIONS.contains(version)) {
            LOG.info("[loconautics_fixes] Loconautics {} — патч применяется.", version);
        } else if (version.startsWith("0.39+")) {
            forkBuild = true;
            LOG.info("[loconautics_fixes] Loconautics {} (сборка форка) — применяются только фиксы, которых нет в форке.", version);
        } else {
            enabled = false;
            LOG.warn("[loconautics_fixes] Loconautics {} не проверялся (проверено: {}). Патч ОТКЛЮЧЁН, "
                    + "чтобы не сломать игру. Обнови loconautics_fixes.", version, TESTED_VERSIONS);
        }
    }

    private static String loconauticsVersion() {
        try {
            ModFileInfo file = FMLLoader.getLoadingModList().getModFileById("loconautics");
            if (file == null || file.getMods().isEmpty()) return null;
            return file.getMods().get(0).getVersion().toString();
        } catch (Throwable t) {
            return null;
        }
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        if (!enabled) return false;
        return !forkBuild || FORK_SAFE.contains(mixinClassName);
    }

    @Override public String getRefMapperConfig() { return null; }
    @Override public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {}
    @Override public List<String> getMixins() { return null; }
    private static final String SMALL_ENGINE = "com.lycoris.loconautics.content.combustion.SmallEngineBlockEntity";
    private static final String HEAT_OWNER = "com/lycoris/loconautics/compat/heat/PropulsionEngineHeat";
    // Same values as PropulsionHeatBridge.OWNER/DESC, spelled out so the plugin never touches game classes.
    private static final String BRIDGE_OWNER = "dev/pryvietmir/locofixes/compat/PropulsionHeatBridge";
    private static final String BRIDGE_DESC =
            "(Lcom/simibubi/create/foundation/blockEntity/SmartBlockEntity;)Lcom/simibubi/create/foundation/blockEntity/behaviour/BlockEntityBehaviour;";

    private static final String SOUND_MANAGER = "net/minecraft/client/sounds/SoundManager";
    private static final String SOUND_BRIDGE_OWNER = "dev/pryvietmir/locofixes/client/EngineSoundBridge";
    private static final String SOUND_BRIDGE_DESC = "(Lnet/minecraft/client/sounds/SoundManager;Ljava/lang/Object;)V";

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
        if (!SMALL_ENGINE.equals(targetClassName.replace('/', '.'))) {
            return;
        }
        // Swap PropulsionEngineHeat.create(..) -> PropulsionHeatBridge.create(..), which returns the base
        // BlockEntityBehaviour type. See PropulsionHeatBridge for why the original crashes without Propulsion.
        int patched = 0;
        for (MethodNode method : targetClass.methods) {
            for (AbstractInsnNode insn : method.instructions) {
                if (insn.getOpcode() == Opcodes.INVOKESTATIC && insn instanceof MethodInsnNode call
                        && HEAT_OWNER.equals(call.owner) && "create".equals(call.name)) {
                    call.owner = BRIDGE_OWNER;
                    call.desc = BRIDGE_DESC;
                    call.itf = false;
                    patched++;
                }
            }
        }
        if (patched > 0) {
            LOG.info("[loconautics_fixes] SmallEngineBlockEntity: made Create Propulsion optional ({} call(s) patched)", patched);
        } else {
            LOG.info("[loconautics_fixes] SmallEngineBlockEntity: PropulsionEngineHeat.create call not found, nothing to patch");
        }

        // SoundManager.play(SoundInstance) is called with a SmallEngineSound. To check that call the JVM
        // verifier loads SmallEngineSound, whose superclass is client-only, so on a dedicated server the
        // whole block entity class fails to load: engines cannot be placed and turn into ghost blocks.
        // Route the call through a helper that takes Object, so nothing client-only is loaded on the server.
        int sounds = 0;
        for (MethodNode method : targetClass.methods) {
            List<AbstractInsnNode> found = new java.util.ArrayList<>();
            for (AbstractInsnNode insn : method.instructions) {
                if (insn.getOpcode() == Opcodes.INVOKEVIRTUAL && insn instanceof MethodInsnNode call
                        && SOUND_MANAGER.equals(call.owner) && "play".equals(call.name)
                        && "(Lnet/minecraft/client/resources/sounds/SoundInstance;)V".equals(call.desc)) {
                    found.add(insn);
                }
            }
            for (AbstractInsnNode insn : found) {
                method.instructions.set(insn, new MethodInsnNode(Opcodes.INVOKESTATIC,
                        SOUND_BRIDGE_OWNER, "play", SOUND_BRIDGE_DESC, false));
                sounds++;
            }
        }
        LOG.info("[loconautics_fixes] SmallEngineBlockEntity: made server-safe ({} sound call(s) patched)", sounds);
    }
    @Override public void postApply(String t, ClassNode c, String m, IMixinInfo i) {}
}
