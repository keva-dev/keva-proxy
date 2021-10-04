# Keva Proxy

A separate process for proxy service

# Current flow

Proxy server requires a set of shard server when initializing. The shard server should be the master
When a request hit proxy, it will determine which shard responsible for that key via a consistent hashing map
Then ut will forward the request to corresponding shard
Shard server will execute the request and return result to the proxy

#Future improvements

- Proxy module reuse many components from the server module. These components should be moved into a common module for future use.
- Method to update the shard master endpoint in case failure happen (Require sentinel feature)
- Feature to rebalance the data when new shard is added/removed
- Increase the amount of thread when handling requests in proxy (current is 1)

# Run
Start shard 1 (use port 7272, turnoff snapshot feature):

./gradlew server:run --args="-p 7272 -ss false"

Start shard 2 (use port 7273, turnoff snapshot feature):

./gradlew server:run --args="-p 7273 -ss false"

Start proxy server
./gradlew proxy:run --args="-sl localhost:7272,localhost:7273"

Use netcat to test: nc localhost 6767
