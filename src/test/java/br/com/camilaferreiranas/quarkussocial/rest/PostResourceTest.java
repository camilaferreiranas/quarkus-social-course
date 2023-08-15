package br.com.camilaferreiranas.quarkussocial.rest;

import br.com.camilaferreiranas.quarkussocial.domain.model.User;
import br.com.camilaferreiranas.quarkussocial.domain.repository.UserRepository;
import br.com.camilaferreiranas.quarkussocial.rest.dto.CreatePostRequest;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import jakarta.json.bind.JsonbBuilder;
import jakarta.transaction.Transactional;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.*;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
@TestHTTPEndpoint(PostResource.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class PostResourceTest {

    @Inject
    UserRepository repository;

    @BeforeEach
    @Transactional
    public void setUp() {
        var user = new User();
        user.setAge(26);
        user.setName("Camila");
        repository.persist(user);
    }

    @Test
    @DisplayName("Should create a post successfully")
    @Order(1)
    void createPost() {
        var post = new CreatePostRequest();
        post.setText("Um texto qualquer");

        var userId = 1;

        var response = given().contentType(ContentType.JSON)
                .body(JsonbBuilder.create().toJson(post))
                .pathParam("userId", 1)
                .when()
                .post()
                .then()
                .extract().response();
        assertEquals(201, response.statusCode());
    }

    @Test
    @DisplayName("Should list all posts")
    @Order(2)
    void listPost() {
        var userId = 1;
        given()
                .contentType(ContentType.JSON)
                .pathParam("userId", 1)
                .when().get()
                .then()
                .statusCode(200)
                .body("size()", Matchers.is(1))

        ;
    }
}