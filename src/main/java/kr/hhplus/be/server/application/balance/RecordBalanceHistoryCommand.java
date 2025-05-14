package kr.hhplus.be.server.application.balance;

import kr.hhplus.be.server.domain.balance.BalanceChangeType;


public record RecordBalanceHistoryCommand(
        Long userId,
        long amount,
        BalanceChangeType type,
        String reason,
        String requestId
) {

    public static RecordBalanceHistoryCommand of(BalanceChargedEvent event) {
        return new RecordBalanceHistoryCommand(
                event.userId(),
                event.amount(),
                BalanceChangeType.CHARGE,
                event.reason(),
                event.requestId()
        );
    }

    public static RecordBalanceHistoryCommand of(ChargeBalanceCriteria criteria) {
        return new RecordBalanceHistoryCommand(
                criteria.userId(),
                criteria.amount(),
                BalanceChangeType.CHARGE,
                criteria.reason(),
                criteria.requestId()
        );
    }
    public static RecordBalanceHistoryCommand of(ChargeBalanceCriteria criteria, BalanceChangeType type) {
        return new RecordBalanceHistoryCommand(
                criteria.userId(),
                criteria.amount(),
                type,
                criteria.reason(),
                criteria.requestId()
        );
    }
}

