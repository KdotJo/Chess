# ♕ This is Kalo's Chess

This project demonstrates mastery of proper software design, client/server architecture, networking using HTTP and WebSocket, database persistence, unit testing, serialization, and security.

## 10k Architecture Overview

The application implements a multiplayer chess server and a command line chess client.

[![Sequence Diagram](10k-architecture.png)][(https://sequencediagram.org/index.html#initialData=C4S2BsFMAIGEAtIGckCh0AcCGAnUBjEbAO2DnBElIEZVs8RCSzYKrgAmO3AorU6AGVIOAG4jUAEyzAsAIyxIYAERnzFkdKgrFIuaKlaUa0ALQA+ISPE4AXNABWAexDFoAcywBbTcLEizS1VZBSVbbVc9HGgnADNYiN19QzZSDkCrfztHFzdPH1Q-Gwzg9TDEqJj4iuSjdmoMopF7LywAaxgvJ3FC6wCLaFLQyHCdSriEseSm6NMBurT7AFcMaWAYOSdcSRTjTka+7NaO6C6emZK1YdHI-Qma6N6ss3nU4Gpl1ZkNrZwdhfeByy9hwyBA7mIT2KAyGGhuSWi9wuc0sAI49nyMG6ElQQA)](https://sequencediagram.org/index.html?presentationMode=readOnly#initialData=IYYwLg9gTgBAwgGwJYFMB2YBQAHYUxIhK4YwDKKUAbpTngUSWDABLBoAmCtu+hx7ZhWqEUdPo0EwAIsDDAAgiBAoAzqswc5wAEbBVKGBx2ZM6MFACeq3ETQBzGAAYAdAE5M9qBACu2AMQALADMABwATG4gMP7I9gAWYDoIPoYASij2SKoWckgQaJiIqKQAtAB85JQ0UABcMADaAAoA8mQAKgC6MAD0PgZQADpoAN4ARP2UaMAAtihjtWMwYwA0y7jqAO7QHAtLq8soM8BICHvLAL6YwjUwFazsXJT145NQ03PnB2MbqttQu0WyzWYyOJzOQLGVzYnG4sHuN1E9SgmWyYEoAAoMlkcpQMgBHVI5ACU12qojulVk8iUKnU9XsKDAAFUBhi3h8UKTqYplGpVJSjDpagAxJCcGCsyg8mA6SwwDmzMQ6FHAADWkoGME2SDA8QVA05MGACFVHHlKAAHmiNDzafy7gjySp6lKoDyySIVI7KjdnjAFKaUMBze11egAKKWlTYAgFT23Ur3YrmeqBJzBYbjObqYCMhbLCNQbx1A1TJXGoMh+XyNXoKFmTiYO189Q+qpelD1NA+BAIBMU+4tumqWogVXot3sgY87nae1t+7GWoKDgcTXS7QD71D+et0fj4PohQ+PUY4Cn+Kz5t7keC5er9cnvUexE7+4wp6l7FovFqXtYJ+cLtn6pavIaSpLPU+wgheertBAdZoFByyXAmlDtimGD1OEThOFmEwQZ8MDQcCyxwfECFISh+xXOgHCmJ43h+P40DsIyMRsQ4hgADIQFkhRYcwTrUP6zRtF0vQGOo+RoFmipzGsvz-BwVygRUQH+uB5afJCILKTsXzQo8cIVG+XYwAg-HihifECQSRJgKS5kVMO-IMkyU4KVyN40vuFTLjAYoSm6MpymW7xKpgKrBhqAByEAbrAUY2r5vIjmZzoWaF2gwAAvHlMA9n224oJlomlglbopTkGjqeUQlphmBE5qoebzNBRYlq6WocBAahoAA5MwVpog2bnqOVVBIjATT6H8OwygVRW9ggMDQDAACEhVzVsi1bi55SgfU4ogNAKLgDl8joVAFSNTA6aZqMYyte1BZjF10D1AAkmgZ3Fig4CzfNKkNvRaULqoU0zVdwAQ-5R1ZfUFEAPqQEhpUaSZ-pWfZ-4IIB2O3YjFUvIROkdTB5GXmjiH1pCanVJQd3IKmMC4fhz3eV8sE0+j9O0Y2DFMb4AReCg6AxHEiTi5Ldm+FgQmCsdjTSBGPERu0EbdD00mqLJwwUVR6A3VjsJaWMqP88hDOYJpxPmfUuMKxizung5ahOZj5QTaORgoNwx6XuefN02gc5+Rl5SBcFT402F8pW2H8NR47AbxwdWVm1+Tv8QrGSqABdtE1N-oYmMz7xCjPJfM5TPE-dHMwCMMB0U2IssSi67+Ng4oajxaIwAA4kqGhKyJNT1A0Q+azr9hKobl7G4U9X22TRth+9xnmw7SMwIyYDIDkGIH0fFgoISnt152rm3u5+9MgPx9J0hEfpfyAXCkF4rrk-zAQAAM2HqPWUidQ4YxiuqGACVgE5lmggYAlgUAMV9tDF0lk0RLUKsVfsh0VYJRHjmGqWB6r3QAKx4RgC1fkb1oIwPnnA4hYMmyoJJtNdBf9XxZ3KGvDBOQC5F3tqXMCYwGEoG+tIAsABGcIwRAggk2PEXUKA3Scj2N8ZIoA1SqMgosb4Yi4pKj0YzCqLMShgBwpQrMywxGqALA0cYYiJHSNkfI5YijlE6N0mMDRCCQDaKIpTfRSpDFzGMTATobdhZeFFv4DgAB2NwTgUBOBiBGYIcA4ARgAGzwAnIYQhhgiiszKL6euU9WgdDngvaYS8w5ZgMUqExNRs5wnXnU6iekbEhN0YLIRbCZqHjkCgQpGIhnokKR7Ek3tfb1HGSMpUId4JhzWI0uYb9Iaf3qHAfJxoiooE2PvCsOo9TGg6ZLR4Ry5ihMMAQZBMBIAwF1NFVUGpvpALWYYYhto76TQGegz5XCb5sP9GInk3yowxjjCveuZi2bpikc3F6ND8zQUKQGKs5oYDfOYSg35UN-kWUBblZaODva8LQAcwpxcd7CJeN065vSljNOZg1EpFjGgc26C3URSpnH1BkXI1uQtTAxJYkgvsEBDmxCQAkMAEqrKHIAFIQHFLAww-hNH+OKeY5W5TGhNGZJJHoYjF7LKQlmbACCkFQDgBAKyUBVl8ukCy4mvDxgb06VTH41rKB2odTzBl4jJG236WnAAVqqtAozI3ikmRfRy192FlR9vi+osbo2LM9egJSvrbX2ugE6uYEjiQwBTh-aOX9Y5XMMOFbNhRIEanRdkGAADVrlr+WnMFW5WFpy1WqbyW1sHtt7XvDx6JB3bRWiVPB+qnGqBFKtSFKBYyyVNmy8xU9HrcuWK9VFhZixfUrGaGs4ZkLCvBo2+ABbYAtrDBjUdnZtk3o9I+5N9QrWIL9Te-K2CCjTrWmdB13sVZiP9dAb6C722kPZVujMO7kW5n3R9Q9pYgMbRNCeh5Z7mUirfTNbt11Dq8IzdSsN+qeWfpteBqAewADqLBvpax6AAIR4goOAABpYJxaQ0wEFYEYVMHN2crwjuqJormJiygIgqWsqZYyflIgYMsBgDYCtYQPI-6dXmD1aTVW6tNba16MYdd7ryaRW8WRXlOZA2W3OTbVCDZyNPpgCAbgeAFDqbGR5qAXnsBTK9odWZbnfP+enBTItag1j1o2QjGOP9KxrS0PIGAKIEHonXI8t4oCznmpzRFTkUWfmRwrWnSu1dM7At4e5lTAiCY0q-HSmA5cKs10hEm1ljdKEtwk0AA)

## Modules

The application has three modules.

- **Client**: The command line program used to play a game of chess over the network.
- **Server**: The command line program that listens for network requests from the client and manages users and games.
- **Shared**: Code that is used by both the client and the server. This includes the rules of chess and tracking the state of a game.

## Starter Code

As you create your chess application you will move through specific phases of development. This starts with implementing the moves of chess and finishes with sending game moves over the network between your client and server. You will start each phase by copying course provided [starter-code](starter-code/) for that phase into the source code of the project. Do not copy a phases' starter code before you are ready to begin work on that phase.

## IntelliJ Support

Open the project directory in IntelliJ in order to develop, run, and debug your code using an IDE.

## Maven Support

You can use the following commands to build, test, package, and run your code.

| Command                    | Description                                     |
| -------------------------- | ----------------------------------------------- |
| `mvn compile`              | Builds the code                                 |
| `mvn package`              | Run the tests and build an Uber jar file        |
| `mvn package -DskipTests`  | Build an Uber jar file                          |
| `mvn install`              | Installs the packages into the local repository |
| `mvn test`                 | Run all the tests                               |
| `mvn -pl shared test`      | Run all the shared tests                        |
| `mvn -pl client exec:java` | Build and run the client `Main`                 |
| `mvn -pl server exec:java` | Build and run the server `Main`                 |

These commands are configured by the `pom.xml` (Project Object Model) files. There is a POM file in the root of the project, and one in each of the modules. The root POM defines any global dependencies and references the module POM files.

## Running the program using Java

Once you have compiled your project into an uber jar, you can execute it with the following command.

```sh
java -jar client/target/client-jar-with-dependencies.jar

♕ 240 Chess Client: chess.ChessPiece@7852e922
```
This will be a supa fun project XD!
