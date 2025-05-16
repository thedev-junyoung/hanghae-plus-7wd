package kr.hhplus.be.server.application.balance;

import kr.hhplus.be.server.domain.balance.BalanceChangeType;

public record BalanceChargedEvent(
        Long userId,
        long amount,
        BalanceChangeType type,
        String reason,
        String requestId
) {
    public static BalanceChargedEvent from(ChargeBalanceCommand command) {
        return new BalanceChargedEvent(
                command.userId(),
                command.amount(),
                BalanceChangeType.CHARGE,
                command.reason(),
                command.requestId()
        );
    }
}

