# kafka-pluralsight
It is a messaging system, "A high-throughput distributed message system" to be precise.

To help manage these challenges, there are some solutions
1. Database Replication and Log Shipping. RDBMS to RDBMS only, Database specific, tight coupling (schema), performance challenges (log shipping) and cubersome.
2. ETL - tpyically properiety and costly, lots of custom development, scalability challenged, performance challenges and often require multiple instances.
3. Messaging - Traditional systems. Limited scalability, requires smaller messages, requires rapid consumption, not fault-tolerant
4. Custom middleware magic

Messaging
Publishers -> Broker -> Consumers.
1. Backpressure
2. 
