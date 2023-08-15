package br.com.camilaferreiranas.quarkussocial.rest;

import br.com.camilaferreiranas.quarkussocial.domain.model.Post;
import br.com.camilaferreiranas.quarkussocial.domain.model.User;
import br.com.camilaferreiranas.quarkussocial.domain.repository.PostRepository;
import br.com.camilaferreiranas.quarkussocial.domain.repository.UserRepository;
import br.com.camilaferreiranas.quarkussocial.rest.dto.CreatePostRequest;
import br.com.camilaferreiranas.quarkussocial.rest.dto.PostResponse;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;
import java.util.stream.Collectors;

@Path("/users/{userId}/posts")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class PostResource {



    @Inject
    UserRepository userRepository;

    @Inject
    PostRepository postRepository;


    @POST
    @Transactional
    public Response createPost(@PathParam("userId") Long userId, CreatePostRequest postRequest) {
        User user = userRepository.findById(userId);
        if (user == null) {

            return Response.status(Response.Status.NOT_FOUND).build();
        }
        Post post = new Post();
        post.setText(postRequest.getText());
        post.setUser(user);
        postRepository.persist(post);
        return Response.status(Response.Status.CREATED).build();
    }


    @GET
    public Response listPost(@PathParam("userId") Long userId) {
        User user = userRepository.findById(userId);
        if (user == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        PanacheQuery<Post> query = postRepository.find("user", user);
        var list = query.list();

        List<PostResponse> listReponse = list.stream().map(
                PostResponse::fromEntity
        ).collect(Collectors.toList());

        return Response.ok(listReponse).build();
    }
}
