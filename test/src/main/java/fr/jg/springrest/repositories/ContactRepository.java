package fr.jg.springrest.repositories;

import fr.jg.springrest.entities.ContactEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ContactRepository extends JpaRepository<ContactEntity, Object>, JpaSpecificationExecutor<ContactEntity> {
}
