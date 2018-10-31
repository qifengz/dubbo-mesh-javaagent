# dubbo-mesh-javaagent

#### 项目介绍
项目中使用Dubbo，可通过指定-javaagent的方式，实现拦截所有的dubbo请求到MOSN sidecar，中间是基于InterfaceName的DNS寻址方式。
本项目的构建产物便是这样的javaagent jar包。

#### 使用技术
 - Javaagent
 - javassist

#### 使用说明
  在服务消费方添加`-javaagent:[目录]/[jar包]` 例如:java -javaagent:C:\Users\ucweb\Desktop\dubbo-mesh-javaagent-1.0-SNAPSHOT.jar -jar dubbo-consumer.jar
