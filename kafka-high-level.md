# kafka High Level
It is a messaging system, "A high-throughput distributed message system" to be precise.

To help manage these challenges, there are some solutions
1. Database Replication and Log Shipping. RDBMS to RDBMS only, Database specific, tight coupling (schema), performance challenges (log shipping) and cumbersome.
2. ETL - typically property and costly, lots of custom development, scalability challenged, performance challenges and often require multiple instances.
3. Messaging - Traditional systems. Limited scalability, requires smaller messages, requires rapid consumption, not fault-tolerant
4. Custom middleware magic

**Messaging**
Publishers -> Broker -> Consumers.
1. Backpressure
2. Message Processing bug - message may be lost as well

**Customer Middleware magic**
1. Increasingly complex
2. Deceiving
3. Consistency concerns - more distributed, more complexity
4. Potentially expensive
    Multiple-write pattern (Atomic transaction, coordination logic, performance and operational challenges)
    Messaging broken pattern (competing consumers and non-consuming consumer)

## Goal
In order to move High volume, High velocity and High variety data around
1. Cleanly
2. Reliably
3. Quickly
4. Autonomously
we need a solution that has following architectural / design constraints
1. High throughput - No Serialization / Deserialization and Zero Copy (available for non-TLS connections).
2. horizontally scalable
3. Reliable and durable
4. Loosely coupled producers and consumers
5. Flexible publish-subscribe semantics

This is what Apache Kafka is all about. It is named after Franz Kafka.

**Brief history of Kafka**
* 2009: Kafka Inception Development begins
* 2010: Initial Kafka Deployment @LinkedIn
* 2011: Kafka Open sourced to ASF
