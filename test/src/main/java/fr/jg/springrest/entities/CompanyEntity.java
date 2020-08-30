package fr.jg.springrest.entities;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "company")
public class CompanyEntity {

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID id;

    private String name;

    private String siret;

    private LocalDate establishmentDate;

    private Integer totalEmployees;

    private Boolean isRun;

    @ManyToMany(cascade = CascadeType.PERSIST)
    @JoinTable(name = "company_contact", joinColumns = {@JoinColumn(name = "company_id")}, inverseJoinColumns = {@JoinColumn(name = "contact_id")})
    private List<ContactEntity> contacts;

    @CreationTimestamp
    private LocalDateTime createdAt;

    public CompanyEntity() {

    }

    public CompanyEntity(final CompanyEntity companyEntity) {
        this.id = companyEntity.id;
        this.name = companyEntity.name;
    }

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

    public List<ContactEntity> getContacts() {
        return this.contacts;
    }

    public void setContacts(final List<ContactEntity> contacts) {
        this.contacts = this.contacts;
    }

    public Boolean isRun() {
        return this.isRun;
    }

    public void setRun(final Boolean run) {
        this.isRun = run;
    }
}
