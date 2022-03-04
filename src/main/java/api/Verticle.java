/**
 * Created by tiago on 07/10/2017.
 */
package api;

import api.config.HazelcastConfig;
import api.entity.Issue;
import api.service.IssueService;
import api.service.IssueServiceImpl;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Launcher;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.CorsHandler;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class Verticle extends AbstractVerticle {

    private static final HazelcastConfig CONFIG = HazelcastConfig.getInstance();

    public static void main(final String[] args) {
//        CONFIG.saveObject(1,"key");
        Launcher.executeCommand("run", Verticle.class.getName());
    }

    @Override
    public void start(Future<Void> fut) {
        Router router = Router.router(vertx); // <1>
        // CORS support
        Set<String> allowHeaders = new HashSet<>();
        allowHeaders.add("x-requested-with");
        allowHeaders.add("Access-Control-Allow-Origin");
        allowHeaders.add("origin");
        allowHeaders.add("Content-Type");
        allowHeaders.add("accept");
        Set<HttpMethod> allowMethods = new HashSet<>();
        allowMethods.add(HttpMethod.GET);
        allowMethods.add(HttpMethod.POST);
        allowMethods.add(HttpMethod.DELETE);
        allowMethods.add(HttpMethod.PUT);

        router.route().handler(CorsHandler.create("*") // <2>
                .allowedHeaders(allowHeaders)
                .allowedMethods(allowMethods));
        router.route().handler(BodyHandler.create()); // <3>
        // routes
        router.get("/issue").handler(this::getIssues);
        router.get("/issue/:id").handler(this::getById);
        router.post("/issue").handler(this::save);
        router.put("/issue").handler(this::update);
        router.delete("/issue/:id").handler(this::remove);
        router.post("/issue/filter").handler(this::getIssuesByFilter);

        vertx.createHttpServer() // <4>
                .requestHandler(router::accept)
                .listen(8080, "0.0.0.0", result -> {
                    if (result.succeeded())
                        fut.complete();
                    else
                        fut.fail(result.cause());
                });
    }

    IssueService issueService = new IssueServiceImpl();


    private void getIssues(RoutingContext context) {
        issueService.list().thenAccept(issues -> {
            sendSuccess(Json.encodePrettily(issues), context.response());
        }).exceptionally(throwable -> {
            sendError(throwable.getCause().getMessage(), context.response());
            return null;
        });
    }

    private void getIssuesByFilter(RoutingContext context) {
        System.out.println(context.getBodyAsJson());
        issueService.getByFilter(context.getBodyAsJson()).thenAccept(issues -> {
            sendSuccess(Json.encodePrettily(issues), context.response());
        }).exceptionally(throwable -> {
            sendError(throwable.getCause().getMessage(), context.response());
            return null;
        });
    }

    private void getById(RoutingContext context) {
        issueService.getById(context.request().getParam("id")).thenAccept(issue -> {
            sendSuccess(Json.encodePrettily(issue), context.response());
        }).exceptionally(throwable -> {
            sendError(throwable.getCause().getMessage(), context.response());
            return null;
        });
    }

    private void save(RoutingContext context) {
        issueService.save(Json.decodeValue(context.getBodyAsString(), Issue.class)).thenAccept(issue -> {
            if (Objects.nonNull(issue)) {
                sendSuccess(Json.encodePrettily(issue), context.response());
            } else {
                sendError("Loi khi luwu", context.response());
            }
        }).exceptionally(throwable -> {
            sendError(throwable.getCause().getMessage(), context.response());
            return null;
        });
    }

    private void update(RoutingContext context) {
        issueService.update(Json.decodeValue(context.getBodyAsString(), Issue.class)).thenAccept(issue -> {
            if (Objects.nonNull(issue)) {
                sendSuccess(Json.encodePrettily(issue), context.response());
            } else {
                sendError("Khoong ton tai user ", context.response());
            }
        }).exceptionally(throwable -> {
            sendError("Co loi xay ra khi thuc hien", context.response());
            return null;
        });
    }

    private void remove(RoutingContext context) {
        issueService.remove(context.request().getParam("id")).thenAccept(aBoolean -> {
            if (aBoolean)
                sendSuccess("Xoa bang ghi thanh cong", context.response());
            else
                sendError("Bangr ghi ko tonf taij", context.response());
        }).exceptionally(throwable -> {
            sendError(throwable.getCause().getMessage(), context.response());
            return null;
        });
    }

    private void sendError(String errorMessage, HttpServerResponse response) {
        JsonObject jo = new JsonObject();
        jo.put("errorMessage", errorMessage);

        response
                .setStatusCode(500)
                .putHeader("content-type", "application/json; charset=utf-8")
                .end(Json.encodePrettily(jo));
    }

    private void sendSuccess(HttpServerResponse response) {
        response
                .setStatusCode(200)
                .putHeader("content-type", "application/json; charset=utf-8")
                .end();
    }

    private void sendSuccess(String responseBody, HttpServerResponse response) {
        response
                .setStatusCode(200)
                .putHeader("content-type", "application/json; charset=utf-8")
                .end(responseBody);
    }
}
