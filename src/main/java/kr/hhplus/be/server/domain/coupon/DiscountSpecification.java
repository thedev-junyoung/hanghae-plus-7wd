package kr.hhplus.be.server.domain.coupon;

import kr.hhplus.be.server.common.vo.Money;

public interface DiscountSpecification {
    Money calculateDiscount(Money orderAmount);
    boolean isApplicableTo(Money orderAmount);
}