package fr.jg.springrest.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import fr.jg.springrest.data.annotations.Pageable;

import javax.validation.constraints.NotNull;
import java.util.UUID;

public class ContactDto {

    @Pageable
    @NotNull
    private UUID id;

    @Pageable
    @NotNull
    @JsonProperty("first_name")
    private String firstName;

    @Pageable
    @JsonProperty("last_name")
    private String lastName;

    public UUID getId() {
        return this.id;
    }

    public void setId(final UUID id) {
        this.id = id;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public void setFirstName(final String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return this.lastName;
    }

    public void setLastName(final String lastName) {
        this.lastName = lastName;
    }
}
