package api.service;

import api.Verticle;
import io.vertx.core.Vertx;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class IssueServiceImplTest {
    private Vertx vertx;
    private Integer port;
    private IssueServiceImpl customerService;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void list() {

//        issueService.list().thenAccept(issues -> {
//
//            Assert.assertEquals(1, 2);
//        }).exceptionally(throwable -> {
//            Assert.assertFalse(false);
//            return null;
//        });

//        Mockito.when()
    }

    @Test
    public void getByFilter() {
    }

    @Test
    public void getById() {
    }

    @Test
    public void save() {
    }

    @Test
    public void update() {
    }

    @Test
    public void remove() {
    }
}
