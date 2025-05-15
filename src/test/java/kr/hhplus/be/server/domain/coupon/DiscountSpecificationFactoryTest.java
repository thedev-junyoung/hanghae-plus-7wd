package kr.hhplus.be.server.domain.coupon;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import static org.assertj.core.api.Assertions.assertThat;

class DiscountSpecificationFactoryTest {

    private final Clock fixedClock = Clock.fixed(
            LocalDateTime.of(2025, 5, 1, 0, 0).toInstant(ZoneOffset.UTC),
            ZoneOffset.UTC
    );

    private final LocalDateTime now = LocalDateTime.now(fixedClock);
    private final LocalDateTime start = now.minusDays(1);
    private final LocalDateTime end = now.plusDays(1);

    @Test
    @DisplayName("FIXED 타입 쿠폰은 FixedAmountDiscountSpecification을 반환한다")
    void factory_should_return_fixed_specification() {
        Coupon coupon = Coupon.create("FIXED10", CouponType.FIXED, 1000, 100, start, end);
        DiscountSpecification spec = DiscountSpecificationFactory.from(coupon);

        assertThat(spec).isInstanceOf(FixedAmountDiscountSpecification.class);
    }

    @Test
    @DisplayName("PERCENTAGE 타입 쿠폰은 PercentageDiscountSpecification을 반환한다")
    void factory_should_return_percentage_specification() {
        Coupon coupon = Coupon.create("RATE10", CouponType.PERCENTAGE, 10, 100, start, end);
        DiscountSpecification spec = DiscountSpecificationFactory.from(coupon);

        assertThat(spec).isInstanceOf(PercentageDiscountSpecification.class);
    }
}
