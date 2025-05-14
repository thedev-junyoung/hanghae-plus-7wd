package kr.hhplus.be.server.domain.coupon;


import kr.hhplus.be.server.common.vo.Money;

public class FixedAmountDiscountSpecification implements DiscountSpecification {
    private final int discountAmount;

    public FixedAmountDiscountSpecification(int discountAmount) {
        this.discountAmount = discountAmount;
    }

    @Override
    public Money calculateDiscount(Money orderAmount) {
        if (!isApplicableTo(orderAmount)) {
            throw new CouponException.NotApplicableException(orderAmount);
        }
        return Money.wons(discountAmount);
    }

    @Override
    public boolean isApplicableTo(Money orderAmount) {
        return orderAmount.isGreaterThanOrEqual(Money.wons(discountAmount));
    }
}