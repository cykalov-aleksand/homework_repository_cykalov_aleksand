package org.example.agent;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class LogAnnotatedMethodsVisitor extends ClassVisitor {
    private String className = "";

    public LogAnnotatedMethodsVisitor(int api, ClassVisitor cv) {
        super(api, cv);
    }

    @Override
    public void visit(int version, int access, String name, String signature,
                      String superName, String[] interfaces) {
        this.className = name;
        super.visit(version, access, name, signature, superName, interfaces);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc,
                                     String signature, String[] exceptions) {
        // Пропуск конструкторов
        if ("<init>".equals(name) || "<clinit>".equals(name)) {
            return super.visitMethod(access, name, desc, signature, exceptions);
        }

        MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
        return new LogMethodVisitor(Opcodes.ASM9, mv, className, name);
    }
}
