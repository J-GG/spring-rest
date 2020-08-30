package fr.jg.springrest.repositories;

import fr.jg.springrest.entities.CompanyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface CompanyRepository extends JpaRepository<CompanyEntity, Object>, JpaSpecificationExecutor<CompanyEntity> {
}
