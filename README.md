# KumuluzEE OpenTracing demo


## Usage

1. Run Jaeger via Docker:
```sh
docker run -d -p 5775:5775/udp -p 16686:16686 jaegertracing/all-in-one:latest
```

2. Build using maven:

```sh
$ cd kumuluzee-opentracing-test
$ mvn clean package
```

3. Run as uber-jar:

```sh
$ PORT=3000 java -jar customers/target/customers-1.0-SNAPSHOT.jar
$ PORT=3001 java -jar orders/target/orders-1.0-SNAPSHOT.jar
```

4. Navigate to <http://localhost:3000/v1/customers/1/orders>

5. View traces in Jaeger console
   <http://localhost:16686>

