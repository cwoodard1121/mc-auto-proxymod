package com.itzblaze.modulewithasm.asm;

import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraftforge.fml.common.asm.transformers.deobf.FMLDeobfuscatingRemapper;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.*;

import java.util.Iterator;

public class ClassTransformer implements IClassTransformer {
    public byte[] transform(String name, String transformedName, byte[] bytes) {
        if (!transformedName.startsWith("net.minecraft.network.NetworkManager") && !transformedName.startsWith("net.minecraft.network.gw"))
            return bytes;
        ClassReader classReader = new ClassReader(bytes);
        ClassNode classNode = new ClassNode();
        classReader.accept((ClassVisitor)classNode, 0);
        for (MethodNode method : classNode.methods) {
            String mappedMethodName = FMLDeobfuscatingRemapper.INSTANCE.mapMethodName(classNode.name, method.name, method.desc);
            if (mappedMethodName.equals("initChannel")) {
                AbstractInsnNode currentInsn = null;
                Iterator<AbstractInsnNode> insnIterator = method.instructions.iterator();
                while (insnIterator.hasNext()) {
                    currentInsn = insnIterator.next();
                    if (currentInsn.getOpcode() == 177) {
                        InsnList insnList = new InsnList();
                        insnList.add((AbstractInsnNode)new VarInsnNode(25, 1));
                        insnList.add((AbstractInsnNode)new MethodInsnNode(184, "com/itzblaze/modulewithasm/ProxyServer", "hook", "(Lio/netty/channel/Channel;)V", false));
                        method.instructions.insertBefore(currentInsn, insnList);
                        break;
                    }
                }
                break;
            }
        }
        ClassWriter classWriter = new ClassWriter(3);
        classNode.accept(classWriter);
        return classWriter.toByteArray();
    }
}
