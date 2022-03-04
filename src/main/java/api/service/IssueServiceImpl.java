package api.service;

import api.config.HazelcastConfig;
import api.entity.Issue;
import api.repository.IssueDao;
import io.vertx.core.json.JsonObject;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

/**
 * Created by tiago on 07/10/2017.
 */
public class IssueServiceImpl implements IssueService {
    private final IssueDao issueDao = IssueDao.getInstance();

    private final HazelcastConfig cache = HazelcastConfig.getInstance();

    public CompletionStage<List<Issue>> list() {
        return issueDao.getAllIssue().thenApplyAsync(issues -> {
            issues.forEach(issue -> {
                issue.setKey("tesst");
            });
            return issues;
        }).exceptionally(throwable -> null);
    }

    public CompletionStage<List<Issue>> getByFilter(JsonObject filter) {
        return issueDao.getByFilter(filter);
    }

    public CompletionStage<Issue> getById(String id) {
        Issue issue = (Issue) cache.getObject(id);
        if (Objects.nonNull(issue)) {
            return CompletableFuture.supplyAsync(() -> issue);
        }
        return issueDao.getById(id);
    }

    public CompletionStage<Issue> save(Issue newUser) {
        return issueDao.getById(newUser.getId()).thenCompose(issue -> issueDao.persist(newUser).thenApply(issue1 -> {
            cache.saveObject(issue1, issue1.getId());
            return issue1;
        }));


    }

    public CompletionStage<Issue> update(Issue user) {
        return issueDao.getById(user.getId()).thenCompose(issue -> issueDao.merge(user).thenApply(issue1 -> {
            cache.saveObject(issue1, issue1.getId());
            return issue1;
        }));
    }

    public CompletionStage<Boolean> remove(String id) {
        return issueDao.getById(id).thenCompose(issue -> {
            if (Objects.nonNull(issue))
                cache.remote(id);
            return issueDao.removeById(issue);
        });
    }

}
