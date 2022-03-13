# Integrating application with REST proxy

## Kafka Protocol

**OSI Network Model**

| Level   |    Layers    |               |
|:--------|:------------:|---------------|
| Layer 7 | Application  | } Host Layer  |
| Layer 6 | Presentation | } Host Layer  |
| Layer 5 |   Session    | } Host Layer  |
| Layer 4 |  Transport   | } Host Layer  |
| Layer 3 |   Network    | } Media Layer |
| Layer 2 |   DataLink   | } Media Layer | 
| Layer 1 |   Physical   | } Media Layer |

The modern internet is based on this system. It is a layered system, and the levels are counted from below to the top.

Media Layer: It deals with all the underlying details for creating a network between computers. By that I mean, both the physical infrastructure like routers and switches and the way to find a single machine inside a network via an IP address.

Host Layer: Starting with Transport Layer, we can already talk about end-to-end communication. In terms of protocols, we have examples such as TCP and UDP at level 4, Socket for Level 5, SSL/TLS/SSH at Level 6 and HTTP, FTP protocols at Level 7

Kafka protocol was initially developed as a binary protocol over TCP (non encrypted communication). By doing this, the clients were able to communicate with the Kafka broker over a non-encrypted transmission. A bit later, SSL support was added, and now we can actually decide which type we want to choose. If we preferred a secure way of transmitting the data, then we can choose SSL. But, this comes with a performance penalty.

The protocol is a standard request/response method of communication. At first, the producer opens up a TCP connection to the broker and without any additional handshakes, it is going to transfer the message as part of the request. In case it is needed, the broker can respond with the message acknowledgement as a response. The second requirement that a producer should meet is being asynchronous. A producer should be able to send a request even while awaiting responses from preceding requests. From a consumer perspective, things are pretty similar. The consumer opens a TCP connection, and when it enters the pool, it is going to ask the broker as part of the request.

There are a large number of programming languages which have implemented Kafka client (Java, C/C++, Python, Go, Erlang, DotNet, Clojure, Ruby, Node.js, Perl, PHP, Rust, Storm, Scala and Swift), only the Java clients are maintained as part of the main Kafka project.

Not everyone can use Kafka - a programming language which does not have Kafka client. Implementing a Kafka client is not an easy task, and most probably you wouldn't want to do  it all by yourself. Also, old application which cannot use the Kafka clients - for example, starting from Kafka 2.0, the support for Java version 7 has been dropped.

## REST
The style of communication requires a client/server model on where the server exposes some resources that can be either consumed or manipulated by the client. The most common implementation of REST is over HTTP. The server exposes so-called endpoints on where the client can either retrieve a specific resource using a GET verb or send a resource to the server for processing using POST verb.

We can integrate the above protocol in Kafka using REST proxy. It acts as a proxy server between Kafka server and the other services which cannot benefit from the consumer or producer API.

## Implementation
Solution by confluent under Confluent Community license, the [Source](https://github.com/confluentinc/kafka-rest.git) of which is on the link.

### Producer

```
kafka-rest $ curl -X POST \
> http://localhost:8082/topics/designs \
> -H 'Content-Type: application/vnd.kafka.json.v2+json' \
> -d '{
>   "records": [
>        {
>            "key": "design1",
>            "value": {
>                "color": "blue",
>                "image": "car"
>            }
>        },
>        {
>            "key": "design2",
>            "value": {
>                "color": "red",
>                "image": "suitcase"
>            }
>        }
>       ]
>   }' | json_pp

```

### Consumer
```
kakfa-rest $ curl -X POST \
> http://localhost:8082/consumers/designs-consumer-group/ \
> -H 'Content-Tye: application/vnd.kafka.v2+json' \
> -d '{
>   "name": "consumer-1",
>   "format": "json",
>   "auto-offset.reset": "earliest"
>   }' | json_pp

kakfa-rest $ curl -X POST \
> http://localhost:8082/consumers/designs-consumer-group/instances/consumer-1/subscription \
> -H 'Content-Tye: application/vnd.kafka.v2+json' \
> -d '{
>   "topics": [ "designs" ]
>   }'

kakfa-rest $ curl -X GET \
> http://localhost:8082/consumers/designs-consumer-group/instances/consumer-1/records \
> -H 'Accept: application/vnd.kafka.json.v2+json' | json_pp
```

