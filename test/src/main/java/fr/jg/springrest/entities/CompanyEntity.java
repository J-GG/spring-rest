package fr.jg.springrest.entities;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "company")
public class CompanyEntity {

    @Id
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID id;

    private String name;

    private String siret;

    private LocalDate establishmentDate;

    private Integer totalEmployees;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "contact_id", nullable = false)
    private ContactEntity contact;

    @CreationTimestamp
    private LocalDateTime createdAt;

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

    public LocalDateTime getCreatedAt() {
        return this.createdAt;
    }

    public void setCreatedAt(final LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public ContactEntity getContact() {
        return this.contact;
    }

    public void setContact(final ContactEntity contact) {
        this.contact = contact;
    }
}
