package kr.hhplus.be.server.domain.coupon;

import kr.hhplus.be.server.common.vo.Money;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class PercentageDiscountSpecificationTest {

    @Test
    @DisplayName("정률 할인: 할인율이 정상적으로 적용된다")
    void percentage_discount_should_apply_correctly() {
        DiscountSpecification spec = new PercentageDiscountSpecification(10); // 10%
        Money orderAmount = Money.wons(10000);

        Money discount = spec.calculateDiscount(orderAmount);

        assertThat(discount).isEqualTo(Money.wons(1000));
    }

    @Test
    @DisplayName("정률 할인: 최대 할인 금액을 초과하면 상한까지만 적용된다")
    void percentage_discount_should_cap_at_max_discount() {
        DiscountSpecification spec = new PercentageDiscountSpecification(50, Money.wons(3000));
        Money orderAmount = Money.wons(10000); // 50% = 5000 > 3000 cap

        Money discount = spec.calculateDiscount(orderAmount);

        assertThat(discount).isEqualTo(Money.wons(3000));
    }

    @Test
    @DisplayName("정률 할인: 최대 할인 금액 미만이면 그대로 적용된다")
    void percentage_discount_should_apply_without_cap() {
        DiscountSpecification spec = new PercentageDiscountSpecification(20, Money.wons(5000));
        Money orderAmount = Money.wons(10000); // 20% = 2000 < 5000 cap

        Money discount = spec.calculateDiscount(orderAmount);

        assertThat(discount).isEqualTo(Money.wons(2000));
    }
}
