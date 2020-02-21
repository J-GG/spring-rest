package fr.jg.springrest.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import fr.jg.springrest.data.annotations.Pageable;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public class CompanyDto {

    @Pageable
    @NotNull
    private UUID id;

    @Pageable
    @NotNull
    private String name;

    @Pageable
    private String siret;

    @Pageable
    @JsonProperty("establishment_date")
    private LocalDate establishmentDate;

    @Pageable
    @JsonProperty("total_employees")
    @Min(0)
    private Integer totalEmployees;

    @Pageable
    @JsonProperty("run")
    private Boolean isRun;

    @Pageable
    @NotNull
    @Valid
    private List<ContactDto> contacts;

    public UUID getId() {
        return this.id;
    }

    public void setId(final UUID id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getSiret() {
        return this.siret;
    }

    public void setSiret(final String siret) {
        this.siret = siret;
    }

    public LocalDate getEstablishmentDate() {
        return this.establishmentDate;
    }

    public void setEstablishmentDate(final LocalDate establishmentDate) {
        this.establishmentDate = establishmentDate;
    }

    public Integer getTotalEmployees() {
        return this.totalEmployees;
    }

    public void setTotalEmployees(final Integer totalEmployees) {
        this.totalEmployees = totalEmployees;
    }

    public List<ContactDto> getContacts() {
        return this.contacts;
    }

    public void setContacts(final List<ContactDto> contacts) {
        this.contacts = contacts;
    }

    public Boolean isRun() {
        return this.isRun;
    }

    public void setRun(final Boolean run) {
        this.isRun = run;
    }
}
