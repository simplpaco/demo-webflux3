package com.example.demowebflux;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@ToString
@Getter
@EqualsAndHashCode
@RequiredArgsConstructor
public class TailEvents {
    public List<Movie> movies = new ArrayList<>();

}
