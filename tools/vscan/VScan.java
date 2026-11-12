import jdk.internal.org.objectweb.asm.*;
import jdk.internal.org.objectweb.asm.tree.*;
import jdk.internal.org.objectweb.asm.tree.analysis.*;
import java.nio.file.*;
import java.util.*;
import java.util.zip.*;

/**
 * Finds places where the JVM verifier would have to load a client-only class to check an
 * assignment: a value whose static type is client-only flows into a slot of a different class type.
 * Usage: VScan <target.jar> <classpath jars...>
 */
public class VScan {
    static final Map<String, String> SUPER = new HashMap<>();
    static final Set<String> IFACE = new HashSet<>();
    static final Map<String, Boolean> CLIENT = new HashMap<>();

    static void index(Path jar) throws Exception {
        try (ZipFile z = new ZipFile(jar.toFile())) {
            for (Enumeration<? extends ZipEntry> e = z.entries(); e.hasMoreElements(); ) {
                ZipEntry en = e.nextElement();
                if (!en.getName().endsWith(".class")) continue;
                ClassReader r = new ClassReader(z.getInputStream(en).readAllBytes());
                SUPER.put(r.getClassName(), r.getSuperName());
                if ((r.getAccess() & Opcodes.ACC_INTERFACE) != 0) IFACE.add(r.getClassName());
            }
        }
    }

    static boolean clientName(String n) {
        return n.startsWith("net/minecraft/client/") || n.startsWith("com/mojang/blaze3d/")
            || n.startsWith("dev/engine_room/flywheel/") || n.startsWith("net/createmod/ponder/")
            || n.startsWith("net/neoforged/neoforge/client/");
    }

    static boolean isClient(String n) {
        if (n == null) return false;
        Boolean c = CLIENT.get(n);
        if (c != null) return c;
        CLIENT.put(n, false);
        boolean r = clientName(n) || isClient(SUPER.get(n));
        CLIENT.put(n, r);
        return r;
    }

    static boolean onlyClient(List<AnnotationNode> anns) {
        if (anns == null) return false;
        for (AnnotationNode an : anns) {
            if (an.desc.equals("Lnet/neoforged/api/distmarker/OnlyIn;") && an.values != null && an.values.toString().contains("CLIENT")) return true;
        }
        return false;
    }

    public static void main(String[] a) throws Exception {
        for (String p : a) index(Path.of(p));
        Set<String> out = new TreeSet<>();
        try (ZipFile z = new ZipFile(a[0])) {
            for (Enumeration<? extends ZipEntry> e = z.entries(); e.hasMoreElements(); ) {
                ZipEntry en = e.nextElement();
                if (!en.getName().endsWith(".class")) continue;
                ClassNode cn = new ClassNode();
                new ClassReader(z.getInputStream(en).readAllBytes()).accept(cn, 0);
                if (isClient(cn.name) || onlyClient(cn.visibleAnnotations)) continue;
                for (MethodNode m : cn.methods) {
                    if (m.instructions.size() == 0 || onlyClient(m.visibleAnnotations)) continue;
                    SimpleVerifier v = new SimpleVerifier(Opcodes.ASM9, Type.getObjectType(cn.name),
                            cn.superName == null ? null : Type.getObjectType(cn.superName), null, false) {
                        @Override protected boolean isInterface(Type t) { return IFACE.contains(t.getInternalName()); }
                        @Override protected Type getSuperClass(Type t) {
                            String s = SUPER.get(t.getInternalName());
                            return s == null ? (t.getInternalName().equals("java/lang/Object") ? null : Type.getObjectType("java/lang/Object")) : Type.getObjectType(s);
                        }
                        @Override protected boolean isAssignableFrom(Type t, Type u) { return true; }
                        @Override protected Class<?> getClass(Type t) { return Object.class; }
                    };
                    Frame<BasicValue>[] frames;
                    try { frames = new Analyzer<>(v).analyze(cn.name, m); } catch (AnalyzerException ex) { continue; }
                    Map<String, Integer> seen = new HashMap<>();
                    AbstractInsnNode[] insns = m.instructions.toArray();
                    for (int i = 0; i < insns.length; i++) {
                        AbstractInsnNode in = insns[i];
                        Frame<BasicValue> f = frames[i];
                        if (f == null) continue;
                        int op = in.getOpcode();
                        String key; Type[] params; boolean hasRecv; String recvOwner;
                        if (in instanceof MethodInsnNode mi) {
                            key = op + " " + mi.owner + " " + mi.name + " " + mi.desc;
                            params = Type.getArgumentTypes(mi.desc); hasRecv = op != Opcodes.INVOKESTATIC; recvOwner = mi.owner;
                        } else if (in instanceof FieldInsnNode fi && (op == Opcodes.PUTFIELD || op == Opcodes.PUTSTATIC)) {
                            key = op + " " + fi.owner + " " + fi.name + " " + fi.desc;
                            params = new Type[]{Type.getType(fi.desc)}; hasRecv = false; recvOwner = null;
                        } else if (op == Opcodes.ARETURN) {
                            key = op + " - - -";
                            params = new Type[]{Type.getReturnType(m.desc)}; hasRecv = false; recvOwner = null;
                        } else continue;
                        int occ = seen.merge(key, 1, Integer::sum) - 1;
                        int n = params.length, sz = f.getStackSize();
                        for (int k = -1; k < n; k++) {
                            if (k == -1 && (!hasRecv || recvOwner.startsWith("["))) continue;
                            int fromTop = (n - 1 - k);
                            if (sz - 1 - fromTop < 0) continue;
                            BasicValue val = f.getStack(sz - 1 - fromTop);
                            Type from = val.getType();
                            Type to = k == -1 ? Type.getObjectType(recvOwner) : params[k];
                            if (from == null || from.getSort() != Type.OBJECT || to.getSort() != Type.OBJECT) continue;
                            String fn = from.getInternalName(), tn = to.getInternalName();
                            if (fn.equals(tn) || tn.equals("java/lang/Object") || !isClient(fn)) continue;
                            if (k == -1 && in instanceof MethodInsnNode mi2 && mi2.name.equals("<init>")) continue;
                            out.add(String.join("\t", cn.name, m.name, m.desc, key.replace(' ', '\t'), String.valueOf(occ), String.valueOf(k), tn, fn));
                        }
                    }
                }
            }
        }
        out.forEach(System.out::println);
    }
}
