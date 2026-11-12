package dev.pryvietmir.locofixes;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

/**
 * Loconautics passes values of client-only types (ClientLevel, LocalPlayer, its own screens and sounds)
 * where another type is expected. To check such a call the JVM verifier loads the client class, so on a
 * dedicated server the whole Loconautics class fails to load (the engine, the panel gauge, ...).
 *
 * A CHECKCAST to the expected type right before the call makes the verifier see the expected type directly;
 * the cast only resolves its class when it runs, which is on the client. The exact places were found offline
 * by scanning Loconautics 0.39 and are listed in loconautics_fixes_verify.tsv.
 */
final class VerifierSafety {
    private static final Logger LOG = LoggerFactory.getLogger("loconautics_fixes");
    private static final String RULES = "/loconautics_fixes_verify.tsv";

    record Rule(String method, String methodDesc, int opcode, String owner, String name, String desc,
                int occurrence, int argIndex, String castTo) {
    }

    private static Map<String, List<Rule>> rules;

    private VerifierSafety() {
    }

    static synchronized Map<String, List<Rule>> rules() {
        if (rules != null) {
            return rules;
        }
        Map<String, List<Rule>> map = new HashMap<>();
        try (InputStream in = VerifierSafety.class.getResourceAsStream(RULES)) {
            if (in == null) {
                LOG.warn("[loconautics_fixes] {} is missing, server-safety rewrites are off", RULES);
            } else {
                BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.isBlank() || line.startsWith("#")) {
                        continue;
                    }
                    String[] p = line.split("\t");
                    map.computeIfAbsent(p[0].replace('/', '.'), k -> new ArrayList<>()).add(new Rule(
                            p[1], p[2], Integer.parseInt(p[3]), p[4], p[5], p[6],
                            Integer.parseInt(p[7]), Integer.parseInt(p[8]), p[9]));
                }
            }
        } catch (Exception e) {
            LOG.warn("[loconautics_fixes] could not read {}: {}", RULES, e.toString());
        }
        rules = map;
        return map;
    }

    private static final java.util.Set<String> DONE = java.util.concurrent.ConcurrentHashMap.newKeySet();

    /** A class can get several mixins (and so several preApply calls); rewrite it only once. */
    static void applyOnce(String className, ClassNode node) {
        if (rules().containsKey(className) && DONE.add(className)) {
            apply(className, node);
        }
    }

    static void apply(String className, ClassNode node) {
        List<Rule> list = rules().get(className);
        if (list == null) {
            return;
        }
        int done = 0;
        for (Rule rule : list) {
            MethodNode method = find(node, rule.method(), rule.methodDesc());
            AbstractInsnNode consumer = method == null ? null : consumer(method, rule);
            if (consumer != null && insertCast(method, consumer, rule)) {
                done++;
            }
        }
        if (done == list.size()) {
            LOG.debug("[loconautics_fixes] {}: made server-safe ({} place(s))", className, done);
        } else {
            LOG.info("[loconautics_fixes] {}: made server-safe {} of {} place(s)", className, done, list.size());
        }
    }

    private static MethodNode find(ClassNode node, String name, String desc) {
        for (MethodNode m : node.methods) {
            if (m.name.equals(name) && m.desc.equals(desc)) {
                return m;
            }
        }
        return null;
    }

    private static AbstractInsnNode consumer(MethodNode method, Rule rule) {
        int seen = 0;
        for (AbstractInsnNode insn : method.instructions) {
            if (insn.getOpcode() != rule.opcode()) {
                continue;
            }
            boolean same;
            if (insn instanceof MethodInsnNode call) {
                same = call.owner.equals(rule.owner()) && call.name.equals(rule.name()) && call.desc.equals(rule.desc());
            } else if (insn instanceof FieldInsnNode field) {
                same = field.owner.equals(rule.owner()) && field.name.equals(rule.name()) && field.desc.equals(rule.desc());
            } else {
                same = rule.opcode() == Opcodes.ARETURN;
            }
            if (same && seen++ == rule.occurrence()) {
                return insn;
            }
        }
        return null;
    }

    /** Casts the operand {@code argIndex} (-1 = receiver) of the consumer to {@code castTo}. */
    private static boolean insertCast(MethodNode method, AbstractInsnNode consumer, Rule rule) {
        Type[] above;
        if (consumer instanceof MethodInsnNode call) {
            Type[] params = Type.getArgumentTypes(call.desc);
            int from = rule.argIndex() + 1;
            if (from < 0 || from > params.length) {
                return false;
            }
            above = new Type[params.length - from];
            System.arraycopy(params, from, above, 0, above.length);
        } else {
            above = new Type[0];
        }
        InsnList patch = new InsnList();
        int[] slots = new int[above.length];
        int next = method.maxLocals;
        for (int i = above.length - 1; i >= 0; i--) {
            slots[i] = next;
            next += above[i].getSize();
            patch.add(new VarInsnNode(above[i].getOpcode(Opcodes.ISTORE), slots[i]));
        }
        patch.add(new TypeInsnNode(Opcodes.CHECKCAST, rule.castTo()));
        for (int i = 0; i < above.length; i++) {
            patch.add(new VarInsnNode(above[i].getOpcode(Opcodes.ILOAD), slots[i]));
        }
        method.instructions.insertBefore(consumer, patch);
        method.maxLocals = next;
        method.maxStack = Math.max(method.maxStack, 1);
        return true;
    }
}
