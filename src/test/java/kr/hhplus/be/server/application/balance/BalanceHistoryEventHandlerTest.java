package kr.hhplus.be.server.application.balance;

import kr.hhplus.be.server.domain.balance.BalanceChangeType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

class BalanceHistoryEventHandlerTest {

    private BalanceHistoryEventHandler handler;
    private BalanceHistoryUseCase balanceHistoryUseCase;

    @BeforeEach
    void setUp() {
        balanceHistoryUseCase = mock(BalanceHistoryUseCase.class);
        handler = new BalanceHistoryEventHandler(balanceHistoryUseCase);
    }

    @Test
    @DisplayName("이벤트 수신 시 BalanceHistoryUseCase.recordHistory가 호출된다")
    void handle_shouldDelegateToUseCase() {
        // given
        BalanceChargedEvent event = new BalanceChargedEvent(1L, 10000L, BalanceChangeType.CHARGE,"테스트충전", "REQ-1234");

        // when
        handler.handle(event);

        // then
        verify(balanceHistoryUseCase).recordHistory(
                argThat(cmd ->
                        cmd.userId().equals(1L) &&
                                cmd.amount() == 10000L &&
                                cmd.reason().equals("테스트충전") &&
                                cmd.requestId().equals("REQ-1234")
                )
        );
    }
}
