package kr.hhplus.be.server.domain.coupon;

public class DiscountSpecificationFactory {
    public static DiscountSpecification from(Coupon coupon) {
        return switch (coupon.getType()) {
            case FIXED -> new FixedAmountDiscountSpecification(coupon.getDiscountRate());
            case PERCENTAGE -> new PercentageDiscountSpecification(coupon.getDiscountRate());
        };
    }
}