package com.example.demowebflux;

import io.reactivex.Flowable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.*;

@Service
public class MovieService {

    private final ApplicationEventPublisher publisher;
    private final MovieRepository repository;

    public MovieService(ApplicationEventPublisher publisher, MovieRepository repository) {
        this.publisher = publisher;
        this.repository = repository;
    }

    public Flux<Movie> findAll() {
        return repository.findAll();
    }

    public Mono<TailEvents> getTailEventsFlux() {
        return repository
                .getTailEventsFlux()
                .doOnSuccess(entity -> this.publisher.publishEvent(new MovieCreatedEvent(entity)));
    }

    public Mono<Movie> addMovie(String movieName) {
        return repository
                .addMovie(movieName)
                .doOnSuccess(entity -> this.publisher.publishEvent(new MovieCreatedEvent(entity)));
    }

    private static int getRandomNumberInRange(int min, int max) {
        if (min >= max) {
            throw new IllegalArgumentException("max must be greater than min");
        }
        Random r = new Random();
        return r.nextInt((max - min) + 1) + min;
    }

    public Mono<TailEvents> getFlowable() {
        /*int count = getRandomNumberInRange(3,8);
        TailEvents tailEvents = new TailEvents();
        for (int i=0; i<count; i++) {
            tailEvents.movies.add(new Movie(UUID.randomUUID().toString(), getRandomNumberInRange(40,190)));
        }*/
        TailEvents tailEvents = getFromKafka();
        /*if(tailEvents.movies.isEmpty())
            tailEvents.movies.add(new Movie(System.currentTimeMillis() + "_" + UUID.randomUUID().toString(),0));*/
        System.out.println(tailEvents);
        return Mono.just(tailEvents).doOnSuccess(entity -> this.publisher.publishEvent(new MovieCreatedEvent(entity)));
    }

    private KafkaConsumer getConsumer() {
        Properties properties = new Properties();
        properties.put("bootstrap.servers", "localhost:9092");
        properties.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        properties.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        properties.put("group.id", "test-group");
        properties.put("enable.auto.commit", "true");

        KafkaConsumer<String, String> kafkaConsumer = new KafkaConsumer<String, String>(properties);
        List<String> topics = new ArrayList<String>();
        topics.add("test");
        kafkaConsumer.subscribe(topics);
        return kafkaConsumer;
    }

    public Flowable<TailEvents> flowableKafka() {
        return Flowable.generate(() -> {
            System.out.println("INIT");
            return new TailState(0, 0, getConsumer(), new TailEvents());
        }, (state, subscriber) -> {
            if (state.countEmptyRecords >= 1000) {
                System.out.println("CLOSING");
                state.consumer.close();
                state.start = 0;
                state.consumer = null;
                subscriber.onComplete();
            }
            if (state.consumer == null && state.countEmptyRecords < 1000) {
                state.consumer = getConsumer();
                state.start = 0;
                state.countEmptyRecords = 0;
            }
            if(state.consumer != null) {
                TailEvents tailEvents = new TailEvents();
                ConsumerRecords<String, String> records = state.getConsumer().poll(10);
                if (records.isEmpty()) {
                    state.countEmptyRecords++;
                } else {
                    state.countEmptyRecords = 0;
                    for (ConsumerRecord<String, String> record : records) {
                        tailEvents.movies.add(new Movie(record.value(), getRandomNumberInRange(500, 700)));
                        System.out.println(String.format("Topic - %s, Partition - %d, Value: %s", record.topic(), record.partition(), record.value()));
                    }
                }
                if (!tailEvents.movies.isEmpty()) {
                    this.publisher.publishEvent(new MovieCreatedEvent(tailEvents));
                    state.start++;
                }
                state.tailEvents = tailEvents;
            }

            return state;
        });
    }

    private TailEvents getFromKafka() {

        TailEvents tailEvents = new TailEvents();

        Properties properties = new Properties();
        properties.put("bootstrap.servers", "localhost:9092");
        properties.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        properties.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        properties.put("group.id", "test-group");

        KafkaConsumer<String, String> kafkaConsumer = new KafkaConsumer<String, String>(properties);
        List<String> topics = new ArrayList<String>();
        topics.add("test");
        kafkaConsumer.subscribe(topics);
        try {
            ConsumerRecords<String, String> records = kafkaConsumer.poll(1000);
            for (ConsumerRecord<String, String> record : records) {
                tailEvents.movies.add(new Movie(record.value(), getRandomNumberInRange(500, 700)));
                System.out.println(String.format("Topic - %s, Partition - %d, Value: %s", record.topic(), record.partition(), record.value()));
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            kafkaConsumer.close();
        }
        return tailEvents;
    }


    @ToString
    @EqualsAndHashCode
    @Getter
    @AllArgsConstructor
    public class TailState {
        public Integer start = 0;
        public Integer countEmptyRecords = 0;
        public KafkaConsumer consumer;
        public TailEvents tailEvents;
    }

    /*public Flowable<Object> readKafka() {
        return Flowable.generate(() -> {
            System.out.println("INIT");
            String consumer = "mykafkaconsumer";
            return new TailState(consumer, new Movie("empty", 0));
        }, (state, subscriber) -> {
            //System.out.println("FETCHING");
            Movie in = new Movie("ZOBI: " + UUID.randomUUID().toString(), 0);
            //subscriber.onNext(in);
            this.publisher.publishEvent(new MovieCreatedEvent(in));
            state.movie = in;
            Thread.sleep(5000);
            return state;
        });
    }*/
}
