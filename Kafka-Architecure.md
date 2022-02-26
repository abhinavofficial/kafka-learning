# Kafka Architecture

It is a pub-sub messaging system. Publishers (called as Producers in Kafka) creates some data (also known as message) to a specific location and interested and authorized Subscribers (called as Consumers in Kafka) retrieve the message and process it. 
These publishers and subscribers are simple application that implement publishing and consuming APIs. 
Topic is a collection or grouping of messages and has specific name (defined upfront or on-demand) and form. The location where Kafka keep / maintains topics is called Broker. 
Broker is a software process (more precisely a daemon service). It has access to resources on which it is running. For example, file system where it may be storing its messages in logical grouping under Topics.

## The Apache Kafka Cluster
