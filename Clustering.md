#### Introduction ####

You can setup the OpenMobster Cloud as a highly available Clustered environment.


#### High Availability ####

The **high availability** cluster consists of multiple **JBoss** nodes of the **OpenMobster Cloud Server**. Out of these nodes there is a single node that serves as the Master node. All the incoming traffic is directed to this Master node. The Master node is not a single point of failure because if the Master node goes down, one of the other nodes immediately becomes a Master node. This process keeps going till all the nodes are used up. This is how you can get a highly available cluster running since at any given time there is always one master node processing requests from the mobile devices.


#### Load Balancing ####

At this point in time the Master node processes all the incoming requests. It does not delegate any requests to its other nodes to balance the load. This feature will be supported in a future release. This is a challenge because it needs to replicate local state among the cluster members. At this point, the Sync service does not support this replication except data sharing via the shared database. This is not enough and will require some re-architecting to make the service truly stateless. From here on out, all new services developed will support load balancing to get the best out of a clustered setup.


#### Setup ####

This will cover the steps for setting up an **OpenMobster Cloud** in a clustered environment

##### Configuration #####

In your JBoss server open the following file: **deploy/openmobster.last/clustering-2.4-SNAPSHOT.jar/META-INF/openmobster-config.xml**. Make sure the file looks as the following to activate the node as a Cluster node

```
<?xml version="1.0" encoding="UTF-8"?>

<deployment xmlns="urn:jboss:bean-deployer:2.0">        
   <bean name="clustering://ClusterService" class="org.openmobster.core.cluster.ClusterService">
   	  <!-- Make this value true to activate this node as a Cluster node -->
   	  <property name="active">true</property>
   </bean>                       
</deployment>
```

##### Starting the Cluster #####

To start the cluster you start one node at a time. The first started node starts off as the Master node. The other nodes remain in standby to become the Master node when/if it goes down. You start the node using the standard JBoss command:

```
./run.sh -c {server-name} -b {node-ipaddress}
```