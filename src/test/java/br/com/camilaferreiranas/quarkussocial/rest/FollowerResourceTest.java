package br.com.camilaferreiranas.quarkussocial.rest;

import br.com.camilaferreiranas.quarkussocial.domain.model.Follower;
import br.com.camilaferreiranas.quarkussocial.domain.model.User;
import br.com.camilaferreiranas.quarkussocial.domain.repository.FollowerRepository;
import br.com.camilaferreiranas.quarkussocial.domain.repository.UserRepository;
import br.com.camilaferreiranas.quarkussocial.rest.dto.FollowerRequest;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import jakarta.json.bind.JsonbBuilder;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Response;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
@TestHTTPEndpoint(FollowerResource.class)
class FollowerResourceTest {

    @Inject
    UserRepository userRepository;
    @Inject
    FollowerRepository followerRepository;


    @BeforeEach
    @Transactional
    void setUp() {
        var user = new User();
        user.setAge(30);
        user.setName("Camila");
        userRepository.persist(user);

        var follower = new User();
        follower.setAge(31);
        follower.setName("Outro usuário");
        userRepository.persist(follower);


        var followerEntity = new Follower();
        followerEntity.setFollower(follower);
        followerEntity.setUser(user);
        followerRepository.persist(followerEntity);
    }

    @Test
    @DisplayName("should return 409 when Follower Id is equal to User id")
    public void sameUserAsFollowerTest(){

        var body = new FollowerRequest();
        body.setFollowerId(1L);

        given()
                .contentType(ContentType.JSON)
                .body(JsonbBuilder.create().toJson(body))
                .pathParam("userId", 1)
                .when()
                .put()
                .then()
                .statusCode(Response.Status.CONFLICT.getStatusCode())
                .body(Matchers.is("You can't follow yourself"));
    }

    @Test
    @DisplayName("should return 404 on follow a user when User id doen't exist")
    public void userNotFoundWhenTryingToFollowTest(){

        var body = new FollowerRequest();
        body.setFollowerId(1L);

        var inexistentUserId = 999;

        given()
                .contentType(ContentType.JSON)
                .body(JsonbBuilder.create().toJson(body))
                .pathParam("userId", inexistentUserId)
                .when()
                .put()
                .then()
                .statusCode(Response.Status.NOT_FOUND.getStatusCode());
    }

    @Test
    @DisplayName("should follow a user")
    public void followUserTest(){

        var body = new FollowerRequest();
        body.setFollowerId(1L);

        given()
                .contentType(ContentType.JSON)
                .body(JsonbBuilder.create().toJson(body))
                .pathParam("userId", 1)
                .when()
                .put()
                .then()
                .statusCode(409);
    }

    @Test
    @DisplayName("should return 404 on list user followers and User id does not exist")
    public void userNotFoundWhenListingFollowersTest(){


        given()
                .contentType(ContentType.JSON)
                .pathParam("userId", 999)
                .when()
                .get()
                .then()
                .statusCode(Response.Status.NOT_FOUND.getStatusCode());
    }

    @Test
    @DisplayName("should list a specify user followers")
    public void listFollowersTest(){
        var response =
                given()
                        .contentType(ContentType.JSON)
                        .pathParam("userId", 1)
                        .when()
                        .get()
                        .then()
                        .extract().response();

        var followersCount = response.jsonPath().get("followersCount");
        var followersContent = response.jsonPath().getList("content");

        assertEquals(200, response.statusCode());
        assertEquals(1, followersCount);
        assertEquals(1, followersContent.size());

    }

    @Test
    @DisplayName("should return 404 on unfollow user and User id does not exist")
    public void userNotFoundWhenUnfollowingAUserTest(){


        given()
                .pathParam("userId", 999)
                .queryParam("followerId", 1)
                .when()
                .delete()
                .then()
                .statusCode(Response.Status.NOT_FOUND.getStatusCode());
    }

    @Test
    @DisplayName("should Unfollow an user")
    public void unfollowUserTest(){
        given()
                .pathParam("userId", 1)
                .queryParam("followerId", 1)
                .when()
                .delete()
                .then()
                .statusCode(204);
    }

}