# Tomcat on YARN #

## Futures ##
* Getting containers that die to automatically restart
* Get the Application Master to restart if it dies
* Management of the clients. Currently I have to kill clients through the YARN Cli. 

      
      `yarn application -kill [app#] `
      

* Adding in unit tests and sample/test applications
* Have TOYA clean things up if the AM dies or is exited.
* Migrate paramaters to configeration file
* Add Jetty server to handle commands sent to the AM
* Develop Zookeeper watcher to keep load balancer in sync with running tomcat servers
* Add option to toggle remote debugging

## Prerequisites ##
Password-less ssh to sandbox is setup:
cat ~/.ssh/id_rsa.pub | ssh root@sandbox.hortonworks.com -p 2222 'cat >> .ssh/authorized_keys'

## Usage ##
```
hadoop jar [toya-client jar] org.toya.core.yarn.Client

usage: Client
 -appname <arg>            Optional: Application Name. Default value - toya
 -container_memory <arg>   Recommended: Amount of memory in MB to be requested to run
                           the shell command - Defaults to 10, Recommended is 512. 
 -debug                    Optional: Dump out debug information
 -help                     Optional: Print usage
 -jar <arg>                Required: Jar file containing the application master - toya-client jar
 -lib <arg>                Required: Runnable Jar with toya inside - toya-server jar
 -log_properties <arg>     Optional: log4j.properties file
 -master_memory <arg>      Recommended: Amount of memory in MB to be requested to run
                           the application master - Defaults to 10, Recommended is 128
 -toya_priority <arg>      Optional: Priority for the TOYA containers - Defaults to 0
 -num_containers <arg>     Recommended: No. of containers on which the shell command
                           needs to be executed, Defaults to 1
 -priority <arg>           Optional: Application Priority - Default 0
 -queue <arg>              Optional: RM Queue in which this application is to be
                           submitted - Defaults to 'default'
 -ZK <arg>                 Required: Comma seperated list of ZK hosts ie -
                           host1:port,host2:port
 -webapp_root <arg>        Required: Webapp root on HDFS
 -start_port <arg>         Required: Start of tomcat port range on each node

```

## Example ##
```
See start.sh script.
```
