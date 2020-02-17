package fr.jg.springrest.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import fr.jg.springrest.data.annotations.Pageable;

import javax.validation.constraints.NotNull;
import java.util.UUID;

public class AddressDto {

    @Pageable
    @NotNull
    private UUID id;

    @Pageable
    @NotNull
    private String city;

    @Pageable
    @JsonProperty("postal_code")
    private String postalCode;

    public UUID getId() {
        return this.id;
    }

    public void setId(final UUID id) {
        this.id = id;
    }

    public String getCity() {
        return this.city;
    }

    public void setCity(final String city) {
        this.city = city;
    }

    public String getPostalCode() {
        return this.postalCode;
    }

    public void setPostalCode(final String postalCode) {
        this.postalCode = postalCode;
    }
}
