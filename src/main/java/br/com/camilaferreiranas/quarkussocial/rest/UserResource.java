package br.com.camilaferreiranas.quarkussocial.rest;

import br.com.camilaferreiranas.quarkussocial.domain.model.User;
import br.com.camilaferreiranas.quarkussocial.domain.repository.UserRepository;
import br.com.camilaferreiranas.quarkussocial.rest.dto.CreateUserRequest;
import br.com.camilaferreiranas.quarkussocial.rest.dto.ResponseError;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Valid;
import jakarta.validation.Validator;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.hibernate.engine.spi.Status;

import java.util.Set;

@Path("/users")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class UserResource {

    @Inject
    UserRepository repository;

    @Inject
    Validator validator;

    @POST
    @Transactional
    public Response createUser( CreateUserRequest userRequest) {

        Set<ConstraintViolation<CreateUserRequest>> violations = validator.validate(userRequest);
        if(!violations.isEmpty()) {

            ResponseError error = ResponseError.createFromValidation(violations);

            return Response.status(400).entity(error).build();
        }
        User user = new User();
        user.setAge(userRequest.getAge());
        user.setName(userRequest.getName());
        repository.persist(user);
        return  Response.status(Response.Status.CREATED).entity(user).build();
    }

    @GET
    public Response listAllUsers() {

        return Response.ok(repository.listAll()).build();
    }

    @DELETE
    @Path("{id}")
    @Transactional
    public Response deleteUser(@PathParam("id") Long id) {
        User user = repository.findById(id);
        if(user != null) {
            repository.delete(user);
            return Response.status(Response.Status.OK).build();
        }
       return Response.status(Response.Status.NOT_FOUND).build();
    }

    @PUT
    @Path("{id}")
    @Transactional
    public Response updateUser(@PathParam("id") Long id, CreateUserRequest userRequest) {
        User user = repository.findById(id);
        if(user != null) {
          user.setName(userRequest.getName());
          user.setAge(userRequest.getAge());
          return Response.status(Response.Status.NO_CONTENT).build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }
}
