package com.ucweb;

import javassist.*;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.logging.Logger;


 /**
 * Dubbo mesh代理
 *
 * @author qifengz
 * @date 2018/10/30 09:10
 */

public class DubboMeshAgent implements ClassFileTransformer {

    private static Logger logger = Logger.getLogger("com.ucweb.DubboMeshAgent");

    /**
     * AbstractClusterInvoker类名称
     */
    private static final String TARGET_CLASS_NAME = "com.alibaba.dubbo.rpc.cluster.support.AbstractClusterInvoker";

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        className = className.replace("/", ".");
        if (className.equals(TARGET_CLASS_NAME)) {
            logger.info("调用目标类[" + TARGET_CLASS_NAME + "]");
            ClassPool pool = ClassPool.getDefault();
            try {
                pool.appendClassPath(new LoaderClassPath(Thread.currentThread().getContextClassLoader()));
                CtClass abstractClusterInvoker = pool.get(className);
                //doSelect 方法
                CtMethod select = abstractClusterInvoker.getDeclaredMethod("doSelect");
                /*
                  插入代码：
                  遍历所有的提供者，获取提供者的URL，判断是否包含与发起调用的methodN匹配的URL：
                    如果包含，则基于这个URL的refer出一个新的以${interfaceName}+".rpc:9090"为address的URL，并返回这个URL
                    如果不包含，则跳过，后面走默认的LoadBalance逻辑
                 */
                logger.info("begin to insert mesh code...");
                String sb =
                        "    if( $3 != null && !$3.isEmpty()) {\n" +
//                        "    System.out.println(\"=========>invokers size: \"+$3.size()+\", invocation: \"+$2);\n" +
                        "      com.alibaba.dubbo.rpc.Protocol refprotocol = com.alibaba.dubbo.common.extension.ExtensionLoader.getExtensionLoader(com.alibaba.dubbo.rpc.Protocol.class).getAdaptiveExtension();" +
                        "      for ( int i=0; i< $3.size(); i++ ) {\n" +
                        "        com.alibaba.dubbo.common.Node node = (com.alibaba.dubbo.common.Node) $3.get(i);\n" +
                        "        com.alibaba.dubbo.common.URL nodeUrl = (com.alibaba.dubbo.common.URL) node.getUrl();\n" +
                        "        if (nodeUrl.getParameter(\"methods\").contains($2.getMethodName())) {\n" +
//                        "          System.out.println(\"=========> get interface parameter: \"+nodeUrl.getParameter(\"interface\"));\n" +
                        "          nodeUrl = nodeUrl.setAddress(nodeUrl.getParameter(\"interface\").toLowerCase() + \".rpc:9090\");\n"+
//                        "          System.out.println(\"=========> mesh target url: \"+nodeUrl.getAddress());\n" +
                        "          return refprotocol.refer(java.lang.Class.forName(nodeUrl.getParameter(\"interface\")), nodeUrl);\n" +
                        "        }\n" +
                        "      }\n"+
                        "    }else{}\n";
                select.insertAfter(sb);

                return abstractClusterInvoker.toBytecode();
            } catch (Exception e) {
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                e.printStackTrace(pw);
                logger.severe("dubbo local debug  ：" + sw.toString());
            }
        }
        return null;
    }
}
