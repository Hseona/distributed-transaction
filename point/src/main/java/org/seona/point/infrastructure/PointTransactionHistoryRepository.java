package org.seona.point.infrastructure;

import org.seona.point.domain.PointTransactionHistory;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PointTransactionHistoryRepository extends CrudRepository<PointTransactionHistory, Long> {
    PointTransactionHistory findByRequestIdAndTransactionType(String requestId, PointTransactionHistory.TransactionType transactionType);
}
