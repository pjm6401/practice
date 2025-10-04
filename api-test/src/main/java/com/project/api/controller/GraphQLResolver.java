package com.project.api.controller;

import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

@Controller
public class GraphQLResolver {

    @QueryMapping
    public String hello(@Argument String name) {
        return "Hello " + name + " from GraphQL!";
    }

    @MutationMapping
    public String createUser(@Argument String name) {
        return "Created user: " + name;
    }
}