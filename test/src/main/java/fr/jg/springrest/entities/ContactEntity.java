package fr.jg.springrest.entities;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "contact")
public class ContactEntity {

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID id;

    private String firstName;

    private String lastName;

    @ManyToMany(mappedBy = "contacts")
    private List<CompanyEntity> companies;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "address_id")
    private AddressEntity address;

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

    public List<CompanyEntity> getCompanies() {
        return this.companies;
    }

    public void setCompanies(final List<CompanyEntity> companies) {
        this.companies = companies;
    }

    public AddressEntity getAddress() {
        return this.address;
    }

    public void setAddress(final AddressEntity address) {
        this.address = address;
    }
}
