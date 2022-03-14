# ksqlDB
It is under Confluent Community License. It is an event streaming database purpose-built to help developers create stream processing application on top of Apache Kafka. It was formerly known as ksql. Roughly, it is a sql variant of Kafka stream with some custom functions.

| Traditional DB   | ksqlDB                                                                                                                |
|------------------|-----------------------------------------------------------------------------------------------------------------------|
| GUI or CLI       | **Webserver**: HTTPS </br> **CLI**: ksql                                                                              |
| Query Engine     | ksqlDB                                                                                                                |
| Execution Engine | Kafka Streams                                                                                                         |
| Storage Engine   | * for persistent storage, use **Kafka** </br> * for more transient storage say state information, it uses **RocksDB** |

## Why?
```svg
Dev
Friendly
 ^             KSQL                  |
 |      -------------------          |
 |         Kafka Stream API          |
 |      -----------------------      |
 |      Producer and Consumer API    V
                                   Capabilities
```
For simple operations like filtering, KSQL is quite good but for complex aggregations Kafka Streams may be the right solution. The main point is to find the balance between ease of development and flexibility for your application.

## How?
It has a very similar architecture as a traditional database. Everything starts with the Kafka Cluster since it is the core of Streaming platform.The streaming application will be built inside the KSQL server. This server connects to the Kafka Cluster and consumes and produces messages based on the instructions provided by the User. In Non-Prod, the user can use KSQL CLI to interact with KSQL server. The CLI does not necessarily have to be on the same machine as server because the two are using REST APIs to interact with each other. CLI statement would be passed to Server via REST API where the instruction will be parsed, and the streaming engine runs it. Each query represents a different streaming application.

Every query that would run on KSQL server would be parsed to a topology and then run.

```
CREATE STREAM pipeapple_pizza AS   | <- .to("pineapple_pizza")  - Step 4
    SELECT crust, size, topping    | <- .mapValues( pizza -> pizza.getCrust() + ", " + pizza.getSize() + ", " + pizza.getTopping()) - Step 3
    FROM pizza                     | <- .stream("pizza") - Step 1
    WHERE type = 'pineapple';      | <- .filter( pizza -> pizza.getType().equals("pineapple")) - Step 2
```

## When?
* Streaming Operations (Data Analytics, Monitoring, IOT, etc.)
* Viewing data - showing the content of a topic
* Very easily enhance and manipulate the data stored in a topic.

We can combine two data streams, change fields, or even eliminate them.

## Installing ksqlDB

### Using Docker Compose
Two images that are required for installing ksqlDB
* ksql CLI
* ksqlDB (web interface or headless mode)

If kafka is not configured, you would need images for
* kafka
* zookeeper
* optionally, schema registry

Containers are modular and standardized. They are same sized and are interchangeable. They are isolated from each other. Containers are built on images. Images are templates for creating containers and are built in layers. Then we have Docker which is a container service that manages all the logistics and details of building and running Docker containers. Finally, we have Docker compose which is a tool for running applications that depend on many Docker containers. Docker compose allows us to keep all configuration information in one place, as well as starting all the containers in the right order and taking into consideration different dependencies between them.

```shell
$ docker-compose pull
$ docker-compose up
# -it: iteractive
# ksqldb-cli: container to run
# args: ksql which is the server and its location
$ docker exec -it ksqldb-cli ksql http://ksqldb-server:8088
```

### Using Confluent Platform
Have already done it.

### Manual Build from GitHub
Not recommended

## Configuration Options
In the order of increasing temporariness -
* There should be a ```ksql-server.properties``` that is read by default. This file uses the syntax of standard Java property files.

* When we move into the world of DevOps, maintaining file based configuration is not the best choice. We can use **environment variables** in such case. This can be set up in docker-compose file under ```environment:``` tag or via run command by including a -e or a --env if manually starting up a docker container.
```shell
$ ksql-server-start ksql-server.properties
```

* The third option is to use a **command line parameter** when calling -parameter.command in headless mode and specify which query to run at the startup.
```shell
$ ksql-server-start ksql-server.properties --query-file /path/to/queries.sql
```

* Per-session properties if you want to persist it per session using SET command. Typically usage is for ```auto.offset.reset```
```
SET 'bootstrap.server'='localhost:9095';
SET 'auto.offset.reset'='earliest';
```

Order of precedence (successor overrides the predecessor)
* ksql-server.properties
* environment variable
* command line parameter
* per-session

## Syntax
ksqlDB queries are ANSI SQL plus extra features to work with streams.

SQL was developed in 1970. American National Standards Institute (ANSI) and ISO also adopted SQL in 1986 (SQL-86) which was upgraded in 1992 (SQL-92). ksqlDB is based on SQL-92 with some other features like windowing. New version SQL:2003 included windowing. Later SQL:2008 was release. A total of 9 versions with most recent ones like SQL:2016 with 44 new optional features such as support for JSON parsing.
MySQL and Postgres both implement some feature of SQL:2016.

> ksqlDB burrows a lot of grammar from Presto which SQL-92.

**Difference from the traditional database**

| Traditional Database                                    | ksqlDB                                                                                                                                                                                                                                 |
|---------------------------------------------------------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Table - Group of Rows and Columns                       | Table does have rows and columns but it behaves slightly differently. It is more akin to reference table to slowly changing dimension in Analytical DB or key value pair in NoSQL DB. It has latest data of record for that key (SCD2) |
| Think of streams as transaction table in Data warehouse | Think of streams as Unbounded stream of immutable events in an append mode                                                                                                                                                             |
|                                                         | * In 2019, ksqlDB added support for Pull Query being able to pull data from these constantly updated tables   <br> * updated Push Query to have a keyword emit changes which pushed the result of one query to a new stream.           |

**Some syntax**

| Data Definition Language                                                                                                       | Data Manipulation Language                                                                                   |
|--------------------------------------------------------------------------------------------------------------------------------|--------------------------------------------------------------------------------------------------------------|
| CREATE STREAM </br> CREATE TABLE </br> DROP STREAM </br> DROP TABLE </br> CREATE STREAM AS SELECT </br> CREATE TABLE AS SELECT | SELECT </br> INSERT <br/> UPDATE ? </br> DELETE ? </br> CREATE STREAM AS SELECT </br> CREATE TABLE AS SELECT |

We can create a stream or table from an underlying Kafka topic. While running one of these two statements, DDL will update its internal metadata with no effect whatsoever on any actual topic. Things evolve over time, so some streams or tables may become redundant. Drop are used for the same.


## Types of Queries

* Push Queries: This is a select statement that pushes the results to either the command interface or whatever interface you are using or to a new stream. Note the ```EMIT CHANGES```

```jql
CREATE STREAM tempReadings (zipcode VARCHAR, sensortime BIGINT, temp DOUBLE)
WITH (kafka_topic='readings', timestamp='sensortime', value_format='json', partitions=1);

SELECT zipcode, TIMESTAMPTOSTRING(WINDOWSTART, 'HH:mm:ss') AS windowtime, AVG(temp) as temp
FROM tempReadings
WINDOW TUMBLING (SIZE 1 HOURS)
GROUP BY zipcode EMIT CHANGES;
```

> Mentioning timestamp is important if you want to run windowing or doing any grouping by time. If you do not define the timestamp, it would default to the row time in kafka, which is usually not what we want. We want the event time, not the ingestion time.

* Pull Queries: This was fundamental change by virtue of which now the tables can be used as a lookup or reference table.

```jql
--Using same tempReadings
CREATE TABLE highsandlows WITH (kafka_topic='readings') AS
    SELECT zipcode, MIN(temp) as min_temp, MAX(temp) as max_temp
    FROM tempReadings
    GROUP BY zipcode;

--pull query    
SELECT min_temp, max_temp, zipcode
FROM highsandlows
WHERE zipcode = '25005';
```

* Joins: You can now combine these two. Stream + Stream = Stream or Table + Table = New table or Stream + Table = New Enriched Stream
```jql
--
CREATE STREAM tempReadings2 (zipcode VARCHAR, sensortime BIGINT, temp DOUBLE)
WITH (kafka_topic='readings', timestamp='sensortime', value_format='json', partitions=1);

SELECT tempreadings2.temp, CASE
    WHEN tempreadings2.temp - higsandlows.min_temp <=5 THEN 'Low'
    WHEN highsandlows.max_temp - tempreadings2.temp <= 5 THEN 'High'
    ELSE 'Normal' END AS classification
FROM tempreadings2 
LEFT JOIN highsandlows on tempreadings2.zipcode = highsandlows.zipcode 
EMIT CHANGES;
```

## Interactive vs Headless Deployments
Headless just means no front-end. Ideal for production mode. Interactive mode is used for analysis and initial development.

|               Headless | Interactive             |
|-----------------------:|:------------------------|
|             File-based | Query-based             |
|   Predictable Workload | User-defined workload   |
| Production environment | Development environment |

### Deploying a Streaming ETL Pipeline in Headless mode
In essence, there are two types of end product that we create. Let's understand them.
* Streaming ETL Pipeline: Some process that takes in streaming events, manipulates or transforms them in some way to new events through multiple different transformation steps, and then outputs those new events.
* Event Driven Microservice: An event driven microservice is a program that communicates primarily by reading and writing events. And as a result, it is able to be small, agile, and independent of the rest of infrastructure because it's only taking a dependency on kafka or whatever event store you might use. In some sense, this is almost like triggering side effect which may be direct (like the application sending an email alert based on high temperature warning) or indirect (like a fraud alert being put on kafka for further processing).

For Deployment:
* First, Kafka Connect set for load and basic transforms. ksqlDB can run these in embedded mode or use an existing Kafka Connect Cluster.
* Next, create a query file, a .sql file that has all the streams and tables you want to create.
* Once query file is created, you will need to tell the ksqlDB server to run it via command like parameter (--query-file /path/to/sqlfile) or by setting the ```ksql.query.file``` property in the config file. If both are set, command line takes precedence.
* Finally, you would need a kafka client to check your results.


## Exercise 2: Alerts in Fraud Detection Application
Let's now learn another some concepts using Alerts in Fraud Detection Application.

Topic once registered is visible in KSQL Server. To register a topic into KSQL metadata Stream, we use create table or stream.

```sql
create stream <stream name> 
    with (kafka_topic='topic??', value_format='AVRO');

create table warnings 
as select userId, count(*) 
    from <stream name> 
    window hopping (size 10 minutes, advance by 1 minute) 
    group by userID 
    having count(*) > 1
```