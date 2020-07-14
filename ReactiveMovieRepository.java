package com.example.demowebflux;

import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Repository
public class ReactiveMovieRepository  implements MovieRepository{

    private static List<Movie> movieList = new ArrayList<>();

    static {
        movieList.add(new Movie("Polar (2019)", 64));
        movieList.add(new Movie("Iron Man (2008)", 79));
        movieList.add(new Movie("The Shawshank Redemption (1994)", 93));
        movieList.add(new Movie("Forrest Gump (1994)", 83));
        movieList.add(new Movie("Glass (2019)", 70));
    }

    @Override
    public Flux<Movie> findAll() {
        return Flux.fromIterable(movieList);//.delayElements(Duration.ofSeconds(2));
    }

    private static int getRandomNumberInRange(int min, int max) {

        if (min >= max) {
            throw new IllegalArgumentException("max must be greater than min");
        }

        Random r = new Random();
        return r.nextInt((max - min) + 1) + min;
    }
    @Override
    public Mono<TailEvents> getTailEventsFlux() {
        int count = getRandomNumberInRange(3,8);
        TailEvents tailEvents = new TailEvents();
        for (int i=0; i<count; i++) {
            tailEvents.movies.add(new Movie(UUID.randomUUID().toString(), getRandomNumberInRange(40,190)));
        }
        return Mono.just(tailEvents);
    }

    @Override
    public Mono<Movie> addMovie(String movieName) {
        Movie tmp = new Movie(movieName, 123456);
        movieList.add(tmp);
        movieList.forEach(movie1 -> {
            System.out.println(movie1);
        });
        return Mono.just(tmp);
    }
}
