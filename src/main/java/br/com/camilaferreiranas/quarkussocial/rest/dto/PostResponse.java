package br.com.camilaferreiranas.quarkussocial.rest.dto;

import br.com.camilaferreiranas.quarkussocial.domain.model.Post;
import lombok.Data;

@Data
public class PostResponse {

    private String text;

    public static PostResponse fromEntity(Post post) {
        var response = new PostResponse();
        response.setText(post.getText());
        return response;
    }

}
