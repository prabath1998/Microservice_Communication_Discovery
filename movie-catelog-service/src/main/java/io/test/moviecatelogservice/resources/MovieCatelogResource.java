package io.test.moviecatelogservice.resources;

import io.test.moviecatelogservice.models.CatelogItem;
import io.test.moviecatelogservice.models.Movie;
import io.test.moviecatelogservice.models.Rating;
import io.test.moviecatelogservice.models.UserRating;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/catelog")
public class MovieCatelogResource {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private DiscoveryClient discoveryClient;

    @Autowired
    private WebClient.Builder webClientBuilder;

    @RequestMapping("/{userId}")
    public List<CatelogItem> getCatelog(@PathVariable("userId") String userId){
        UserRating ratings = restTemplate.getForObject("http://ratings-data-service/ratingsdata/users/"+userId, UserRating.class);
         return ratings.getUserRating().stream().map(rating -> {
             Movie movie =  restTemplate.getForObject("http://movie-info-service/movies/" + rating.getMovieId(), Movie.class);
             return new CatelogItem(movie.getName(),"Description",rating.getRating());
         }).collect(Collectors.toList());
    }
}

//    Reactive way using web client
//    Movie movie = webClientBuilder.build()
//            .get()
//            .uri("http://localhost:8082/movies/" + rating.getMovieId())
//            .retrieve()
//            .bodyToMono(Movie.class)
//            .block();
