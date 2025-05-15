package kr.hhplus.be.server.domain.coupon;


public enum CouponType {
    FIXED { public String description() { return "정액 할인"; }},
    PERCENTAGE { public String description() { return "정률 할인"; }};

    public abstract String description();
}
