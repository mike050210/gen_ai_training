package com.epam.training.gen.ai.configuration;

public record DbProperties(String server, int port, String collection, int vectorSize, String ragCollection, int ragVectorSize, int limit) {
}
