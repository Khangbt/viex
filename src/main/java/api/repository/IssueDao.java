package api.repository;

import api.entity.Issue;
import io.netty.util.internal.StringUtil;
import io.vertx.core.json.JsonObject;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

/**
 * Created by tiago on 07/10/2017.
 */
public class IssueDao {
    private static IssueDao instance;
    protected EntityManager entityManager;

    public static IssueDao getInstance() {
        if (instance == null) {
            instance = new IssueDao();
        }

        return instance;
    }

    private IssueDao() {
        entityManager = getEntityManager();
    }

    private EntityManager getEntityManager() {
        EntityManagerFactory factory = Persistence.createEntityManagerFactory("crudHibernatePU");
        if (entityManager == null) {
            entityManager = factory.createEntityManager();
        }

        return entityManager;
    }

    public CompletionStage<Issue> getById(String id) {
        return CompletableFuture.supplyAsync(() -> {
            if (Objects.isNull(id))
                return null;
            Issue result = entityManager.find(Issue.class, id);
            if (result != null) {
                return result;
            } else {
                return null;
            }
        });
    }

//    @SuppressWarnings("unchecked")
//    public List<Issue> findAll() {
//        return entityManager.createQuery("FROM " + Issue.class.getName()).getResultList();
//    }

    public CompletionStage<List<Issue>> getByFilter(JsonObject filter) {
        return CompletableFuture.supplyAsync(() -> {
            Query query = entityManager.createNativeQuery(sqlFilter(filter));
            parametersFilter(filter, query);
            List<Object[]> result = query.getResultList();
            return result.stream().map(objects -> conventObject(objects)).collect(Collectors.toList());
        });
    }

    private String sqlFilter(JsonObject filter) {
        String sqlQuery = "SELECT i.* FROM issue i";
        String preParameter = " WHERE";
        String sqlParameter = "";

        if (!StringUtil.isNullOrEmpty(filter.getString("title"))) {
            sqlParameter += preParameter + " upper(i.title) LIKE upper(:title)";
            preParameter = " OR";
        }

        if (!StringUtil.isNullOrEmpty(filter.getString("title"))) {
            sqlParameter += preParameter + " i.decscription = :decscription";
            preParameter = " OR";
        }

        return sqlQuery + sqlParameter;
    }

    private void parametersFilter(JsonObject filter, Query query) {
        if (!StringUtil.isNullOrEmpty(filter.getString("title"))) {
            String likeNameParam = "%" + filter.getString("title") + "%";
            query.setParameter("title", likeNameParam);
        }

        if (!StringUtil.isNullOrEmpty(filter.getString("title"))) {
            String likeNameParam = "%" + filter.getString("decscription") + "%";
            query.setParameter("decscription", likeNameParam);
        }

    }

    public CompletionStage<Issue> persist(Issue issue) {

        return CompletableFuture.supplyAsync(() -> {
            if (Objects.isNull(issue))
                return null;
            entityManager.getTransaction().begin();
            issue.setId(UUID.randomUUID().toString());
            issue.setKey(UUID.randomUUID().toString());
            Issue issue1 = entityManager.merge(issue);
            entityManager.getTransaction().commit();
            return issue1;
        }).exceptionally(throwable -> {
            entityManager.getTransaction().rollback();
            return null;
        });


    }

    public CompletionStage<Issue> merge(Issue issue) {
        return CompletableFuture.supplyAsync(() -> {
            if (Objects.isNull(issue))
                return null;
            entityManager.getTransaction().begin();
            Issue issue1 = entityManager.merge(issue);
            entityManager.getTransaction().commit();
            return issue1;
        }).exceptionally(throwable -> {
            entityManager.getTransaction().rollback();
            return null;
        });

    }

    public void remove(Issue issue) {
        try {
            entityManager.getTransaction().begin();
            issue = entityManager.find(Issue.class, issue.getId());
            entityManager.remove(issue);
            entityManager.getTransaction().commit();
        } catch (Exception ex) {
            ex.printStackTrace();
            entityManager.getTransaction().rollback();
        }
    }

    public CompletionStage<Boolean> removeById(Issue issue) {
        return CompletableFuture.supplyAsync(() -> {
            if (Objects.nonNull(issue)) {
                remove(issue);
                return true;
            }
            return false;
        });
    }

    public CompletionStage<List<Issue>> getAllIssue() {
        return CompletableFuture.supplyAsync(() -> {
            List<Issue> list = (List<Issue>) entityManager.createQuery("FROM " + Issue.class.getName()).getResultList();
            return list;
        }).exceptionally(throwable -> {
            System.out.println("lỗi gọi ham all");
            System.out.println(throwable.getCause().toString());
            return null;
        });
    }

    private Issue conventObject(Object[] o) {
        Issue issue = new Issue();
        issue.setId((String) o[0]);
        issue.setTitle((String) o[1]);
        issue.setCreatedAt((Date) o[2]);
        issue.setDecscription((String) o[3]);
        issue.setKey((String) o[4]);
        return issue;
    }

}
