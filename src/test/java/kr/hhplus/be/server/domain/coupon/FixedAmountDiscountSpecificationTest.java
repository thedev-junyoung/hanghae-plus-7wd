package kr.hhplus.be.server.domain.coupon;

import kr.hhplus.be.server.common.vo.Money;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class FixedAmountDiscountSpecificationTest {

    @Test
    @DisplayName("정액 할인: 주문 금액이 할인 금액보다 크면 정상 적용된다")
    void fixed_discount_should_apply_when_order_gte_discount() {
        DiscountSpecification spec = new FixedAmountDiscountSpecification(3000);
        Money orderAmount = Money.wons(10000);

        Money discount = spec.calculateDiscount(orderAmount);

        assertThat(discount).isEqualTo(Money.wons(3000));
    }

    @Test
    @DisplayName("정액 할인: 주문 금액이 할인 금액보다 작으면 적용되지 않는다")
    void fixed_discount_should_throw_when_order_lt_discount() {
        DiscountSpecification spec = new FixedAmountDiscountSpecification(5000);
        Money orderAmount = Money.wons(3000);

        assertThat(spec.isApplicableTo(orderAmount)).isFalse();

        assertThatThrownBy(() -> spec.calculateDiscount(orderAmount))
                .isInstanceOf(CouponException.NotApplicableException.class)
                .hasMessageContaining("주문 금액");
    }
}
