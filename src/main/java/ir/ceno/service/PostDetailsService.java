package ir.ceno.service;

import ir.ceno.exception.ResourceNotFoundException;
import ir.ceno.model.Post;
import ir.ceno.model.PostDetails;
import ir.ceno.repository.PostDetailsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PostDetailsService {

    private PostDetailsRepository postDetailsRepository;

    @Autowired
    public PostDetailsService(PostDetailsRepository postDetailsRepository) {
        this.postDetailsRepository = postDetailsRepository;
    }

    public PostDetails getPostDetailsById(long postId) {
        Optional<PostDetails> optionalPostDetails = postDetailsRepository.findById(postId);
        return optionalPostDetails.orElseThrow(ResourceNotFoundException::new);
    }

    public void addPostDetails(PostDetails postDetails, Post post) {

    }

    public void deletePostDetails(long postId) {

    }
}
