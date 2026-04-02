package org.seona.order.infrastructure;

import org.seona.order.domain.CompensationRegistry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CompensationRegistryRepository extends JpaRepository<CompensationRegistry, Long> {
}
