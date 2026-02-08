package eu.asgardschmiede.programmverwaltung.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Set;

public record Program(
        @JsonProperty("name") String name,
        @JsonProperty("purposes") Set<Purpose> purposes,
        @JsonProperty("description") String description,
        @JsonProperty("operatingSystem") String operatingSystem,
        @JsonProperty("alternatives") List<String> alternatives
) {}