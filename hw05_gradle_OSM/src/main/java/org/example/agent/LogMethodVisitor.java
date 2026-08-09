package org.example.agent;

import org.objectweb.asm.MethodVisitor;

import static org.objectweb.asm.Opcodes.GETSTATIC;
import static org.objectweb.asm.Opcodes.INVOKEVIRTUAL;

public class LogMethodVisitor extends MethodVisitor {
    private final String className;
    private final String methodName;
    private boolean hasLogAnnotation = false;
    private static final String LOG_ANNOTATION_DESC = "Lorg/example/annotations/Log;";

    public LogMethodVisitor(int api, MethodVisitor mv, String className, String methodName) {
        super(api, mv);
        this.className = className;
        this.methodName = methodName;
    }
    @Override
    public org.objectweb.asm.AnnotationVisitor visitAnnotation(String desc, boolean visible) {
        if (LOG_ANNOTATION_DESC.equals(desc)) {
            hasLogAnnotation = true;
        }
       return super.visitAnnotation(desc, visible);
    }
    @Override
    public void visitCode() {
        super.visitCode();

        if (!hasLogAnnotation) {
            return; // не логируем, если нет аннотации
        }

        // Вставляем: System.out.println("[LOG] Enter: pkg.Cls.method")
        String fullName = className.replace('/', '.');
        String msg = "[LOG] Класс: " + fullName + " аннотированный метод: " + methodName;

        mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
        mv.visitLdcInsn(msg);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println",
                "(Ljava/lang/String;)V", false);
    }
}
