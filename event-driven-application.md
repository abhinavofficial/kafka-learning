# Event Driven Application

## Enterprise Software Architecture

### Monolithic
Layered approach
UI (User) <-> Business Layer (Orders, Promotions, Users, Payments, Suggestion module) <-> DB (persistence)

### Microservices
Loosely coupled independent services interacting with light-weight protocol (REST or rpc). It can support multiple clients (web or mobile). Each services like orders, suggestions, users, payments and promotions are self-contained and has its own database to enable loose coupling. To have a single point for clients, an API gateway is required which job is to route the client request to appropriate microservices, but can also have features like authentication and monitoring. As the system becomes more complex, dependencies between the services get complex as well leading to "microservices hell". To solve this, event driven architecture came into place.

### Event driven architecture
Per wikipedia, EDA is a software architecture pattern promoting the production, detection, consumption of, and reaction to **events**. Many flavors of programming model across EDA is available.
* Microservices. Handles particular events using broker based technology.
* Serverless, now popular within Cloud technology
* Function as a Service (FaaS) or lambda - short-lived application
* Streaming - Events are process as when as they arrive
* Event Sourcing where you can formulate current state based on immutable event as received (Think of Journal or Delta Lake)
* CQRS (Command Query Responsibility Segregation) - interface for Read and write separately and exposed over different APIs.

## Messages, Events and Commands
Message: Basic unit of communication and can be anything. It is generic with no special intent and can be made purposeful post interpretation using event and command.  
Event: It is a message which informs listening that something has happened. It is typically a past tense verb e.g. advertisement_posted. Producer produces an event and subscribers to that event, called consumer would typically consume the associated message.
Command: It is a targeted action having a 1:1 relation between a producer and a consumer. For example, ordering tacos is a command

## Benefits of EDA
* Decoupled components interaction via a broker based technology. A broker is a middleware which facilitates transmission of data between two services.
* Encapsulation: Events can be classified within different functional boundaries or could be processed under the same boundary.
* Optimization: It is designed to run in almost near time by reacting to the incoming events.
* Scalability: It is naturally aligned.

## Drawbacks of EDA
* Steep Learning Curve
* Complex flow to events
* Loss of transactionality
* May lose Lineage in case event loss - **Simple solution to add an identifier to the event in each application that it is passing through.**

## Event Storming & Domain Driven Design
Typical system revolves around data(base). EDA revolves around events.

### Event Storming
#### Room
Enabling free movement - clear off as many things as possible

#### Right People
Minimally facilitator, small team of technical experts, developers and UX, and lastly domain experts.

#### Process Mapping 
* Sheet of paper / empty wall is to be used for modeling space. 
* Events are written out on sticky notes of different colors. Color Scheme for sticky notes - Domain Event (relevant to business) on Orange Stickers, Policies (process occurred by an event) on purple stickers (e.g. **whenever** this happens, that should happen as well), External systems on Pink Stickers and finally, Command which is an action initiated by user or system on Blue stickers.

#### Duration
One or two full days. Keep food prepared.

Once the business model is complete and bottlenecks have been addressed, we can now solutionize using DDD. 

### Domain driven design
We first need to identify the **aggregate** by locally grouping various commands and events together. The goal is to define structures that are isolating the related concerns from one to another post which bounded context.
**Aggregate** groups related behaviors together whereas **bound context** groups meaning allowing the use of the same terms in different subdomains.