package api.service;

import api.entity.Issue;
import io.vertx.core.json.JsonObject;

import java.util.List;
import java.util.concurrent.CompletionStage;

public interface IssueService {
    CompletionStage<List<Issue>> list();

    CompletionStage<List<Issue>> getByFilter(JsonObject filter);

    CompletionStage<Issue> getById(String id);

    CompletionStage<Issue> save(Issue newUser);

    CompletionStage<Issue> update(Issue user);

    CompletionStage<Boolean> remove(String id);
}
