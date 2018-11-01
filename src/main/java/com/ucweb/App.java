package com.ucweb;

import java.lang.instrument.Instrumentation;
import java.util.logging.Logger;

/**
 * 启动类
 *
 * @author qifengz
 * @date 2018/10/30 09:01
 */
public class App {
    private static Logger logger = Logger.getLogger("com.ucweb.App");

    public static void premain(String agentOps, Instrumentation inst) {
        logger.info("Dubbo mesh javaagent start ...");
        System.setProperty("mesh.interfaces", agentOps == null ? "" : agentOps);
        inst.addTransformer(new DubboMeshAgent());
    }

}
