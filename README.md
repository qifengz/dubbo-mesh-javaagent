# dubbo-mesh-javaagent

#### 项目介绍
项目中使用Dubbo，可通过指定-javaagent的方式，实现拦截指定的dubbo请求到MOSN sidecar，其中是基于InterfaceName的DNS寻址方式。
本项目的构建产物是实现上述功能的javaagent jar包。

#### 使用技术
 - Instrument
 - Javassist

#### 使用说明
  在服务消费方添加`-javaagent:[目录]/[jar包]` 例如:
  1.默认拦截所有接口
    java -javaagent:C:\Users\ucweb\Desktop\dubbo-mesh-javaagent-1.0-SNAPSHOT.jar -jar dubbo-consumer.jar
  2.不拦截任何接口
      java -javaagent:C:\Users\ucweb\Desktop\dubbo-mesh-javaagent-1.0-SNAPSHOT.jar=skip -jar dubbo-consumer.jar
  3.拦截指定接口
      java -javaagent:C:\Users\ucweb\Desktop\dubbo-mesh-javaagent-1.0-SNAPSHOT.jar=com.alibaba.boot.dubbo.demo.consumer.meshservice,com.alibaba.boot.dubbo.demo.consumer.demoservice -jar dubbo-consumer.jar
