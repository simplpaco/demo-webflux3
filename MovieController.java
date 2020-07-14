package com.example.demowebflux;

import io.reactivex.Flowable;
import org.reactivestreams.Publisher;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MovieController {

    private final MovieService movieService;

    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }


    @RequestMapping("/all")
    public Publisher<Movie> index() {
        return movieService.findAll();
    }

    @RequestMapping("/list")
    public Publisher<TailEvents> getTailEventsPublisher() {
        return movieService.getTailEventsFlux();
    }

    @RequestMapping("/haha")
    @CrossOrigin
    public Flowable<TailEvents> getFlowable() {
        System.out.println("hahahaha");
        return movieService.flowableKafka();
    }

    @RequestMapping("/add")
    public Publisher<Movie> add(@RequestParam(name = "movieName") String movieName) {
        return movieService.addMovie(movieName);
    }

    /*@RequestMapping("/kafka")
    public Flowable<Object> kafka() {
        return movieService.readKafka();
    }*/

    @RequestMapping("/bassim")
    public String bassim() {
        return "salut toi";
    }

}
