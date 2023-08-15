package br.com.camilaferreiranas.quarkussocial.rest;

import br.com.camilaferreiranas.quarkussocial.rest.dto.CreateUserRequest;
import groovy.json.JsonBuilder;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.json.bind.JsonbBuilder;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.*;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserResourceTest {

    @Test
    @DisplayName("should create an user successfully")
    @Order(1)
   public void createUserTest() {
       var user = new CreateUserRequest();
       user.setName("Camila");
       user.setAge(26);
       var response = given().contentType(ContentType.JSON).body(JsonbBuilder.create().toJson(user))
               .when()
               .post("/users")
               .then()
               .extract().response();
       assertEquals(201, response.statusCode());
        assertNotNull(response.jsonPath().getString("id"));
   }


   @Test
   @DisplayName("should return error when json is not valid")
   @Order(2)
   public void createUserValidationErrorTest() {
       var user = new CreateUserRequest();
       user.setName(null);
       user.setAge(null);

       var response = given().contentType(ContentType.JSON).body(JsonbBuilder.create().toJson(user))
               .when()
               .post("/users")
               .then()
               .extract().response();

       assertEquals(400,response.statusCode());
       assertNotNull("Validation Error", response.jsonPath().getString("message"));

   }

    @Test
    @DisplayName("should list all users ")
    @Order(3)
   public void listAllUsersTest() {
        given()
                .contentType(ContentType.JSON)
                .when().get("/users")
                .then()
                .statusCode(200)
                .body("size()", Matchers.is(1));
   }

}