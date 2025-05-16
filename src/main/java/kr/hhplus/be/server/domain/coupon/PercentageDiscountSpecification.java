package kr.hhplus.be.server.domain.coupon;


import kr.hhplus.be.server.common.vo.Money;

public class PercentageDiscountSpecification implements DiscountSpecification {
    private final int discountRate;
    private final Money maxDiscount;

    public PercentageDiscountSpecification(int discountRate, Money maxDiscount) {
        this.discountRate = discountRate;
        this.maxDiscount = maxDiscount;
    }

    public PercentageDiscountSpecification(int discountRate) {
        this(discountRate, null);
    }

    @Override
    public Money calculateDiscount(Money orderAmount) {
        Money discount = orderAmount.multiplyPercent(discountRate);
        if (maxDiscount != null && discount.isGreaterThan(maxDiscount)) {
            return maxDiscount;
        }
        return discount;
    }

    @Override
    public boolean isApplicableTo(Money orderAmount) {
        return true;
    }
}
