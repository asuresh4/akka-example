package org.example;

import akka.actor.typed.ActorSystem;
import akka.actor.typed.javadsl.Behaviors;
import akka.http.javadsl.Http;
import akka.http.javadsl.ServerBinding;
import akka.http.javadsl.server.AllDirectives;
import akka.http.javadsl.server.Route;
import com.typesafe.config.ConfigFactory;

import java.io.File;
import java.util.concurrent.CompletionStage;

import static akka.http.javadsl.server.PathMatchers.integerSegment;

public class HttpServer extends AllDirectives {

    public static void main(String[] args) throws Exception {
        ActorSystem<Void> system = ActorSystem.create(
                Behaviors.empty(),
                "routes",
                ConfigFactory.load(ConfigFactory.parseFile(new File("src/main/resources/application.conf")).resolve()));

        final Http http = Http.get(system);

        HttpServer app = new HttpServer();

        final CompletionStage<ServerBinding> binding =
                http.newServerAt("localhost", 8080)
                        .bind(app.createRoute());

        System.in.read(); // let it run until user presses return

        binding
                .thenCompose(ServerBinding::unbind)
                .thenAccept(unbound -> system.terminate());
    }

    private Route createRoute() {
        return concat(
                pathEndOrSingleSlash(() -> complete("root")),
                pathPrefix("ball", () ->
                        concat(
                                pathSingleSlash(() -> complete("/ball")),
                                path(integerSegment(), (i) -> complete((i % 2 == 0) ? "even ball" : "odd ball"))
                        )
                )
        );
    }
}