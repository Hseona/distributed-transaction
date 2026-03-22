package org.seona.product.infrastructure;

import org.seona.product.domain.ProductTransactionHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductTransactionHistoryRepository extends JpaRepository<ProductTransactionHistory, Long> {
    List<ProductTransactionHistory> findAllByRequestIdAndTransactionType(String requestId, ProductTransactionHistory.TransactionType transactionType);
}
