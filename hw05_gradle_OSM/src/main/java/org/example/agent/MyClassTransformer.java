package org.example.agent;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;

import java.lang.instrument.ClassFileTransformer;
import java.security.ProtectionDomain;

public class MyClassTransformer implements ClassFileTransformer {
    @Override
    public byte[] transform(ClassLoader loader,
                            String className,
                            Class<?> classBeingRedefined,
                            ProtectionDomain protectionDomain,
                            byte[] classBytes) {
               if (!className.startsWith("org/example/") || !className.startsWith("org/example/application/")) {
            return classBytes;
        }
        ClassReader cr = new ClassReader(classBytes);
        // Важно: COMPUTE_FRAMES нужен, иначе верификатор ругнётся
        ClassWriter cw = new ClassWriter(cr, ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
        ClassVisitor cv = new LogAnnotatedMethodsVisitor(Opcodes.ASM9, cw);

        cr.accept(cv, 0); // не пропускаем фреймы

        return cw.toByteArray();
    }
}
