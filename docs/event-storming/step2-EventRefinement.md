## Step 2 - 정제된 도메인 이벤트 (Refined Events)

Step 1에서 식별된 원시 이벤트를 분석하고 정제하여 핵심 이벤트만 추출합니다. 기술적 세부 사항이나 중복 정보는 제거합니다.

### 핵심 비즈니스 이벤트

- 주문 요청이 접수되었다
- 주문 항목이 검증되었다
- 상품 재고가 충분하다
- 쿠폰이 유효하다
- 쿠폰이 사용되었다
- 주문 금액이 계산되었다
- 사용자 잔액이 확인되었다
- 잔액이 충분하다
- 결제 금액이 차감되었다
- 결제가 완료되었다
- 주문이 생성되었다
- 주문 정보가 외부 플랫폼으로 전송되었다
- 쿠폰 발급 요청이 접수되었다
- 쿠폰 코드 중복 여부가 확인되었다
- 쿠폰 발행자가 유효하다
- 쿠폰이 생성되었다
- 쿠폰 발급 이벤트가 저장되었다 (Outbox)
- 쿠폰 발급 이벤트가 발행되었다
-

### 이벤트 재정렬

### 관리자/판매자 쿠폰 발행

1. **쿠폰 발급 요청이 접수되었다**
2. **쿠폰 발행자가 유효하다**
3. **쿠폰 코드 중복 여부가 확인되었다**
4. **쿠폰이 생성되었다**
5. **쿠폰 발급 이벤트가 저장되었다**
6. **쿠폰 발급 이벤트가 발행되었다**

### 기존 주문 흐름

1. 주문 요청이 접수되었다
2. 주문 항목이 검증되었다
3. 상품 재고가 충분하다
4. 쿠폰이 유효하다
5. 쿠폰이 사용되었다
6. 주문 금액이 계산되었다
7. 사용자 잔액이 확인되었다
8. 잔액이 충분하다
9. 결제 금액이 차감되었다
10. 결제가 완료되었다
11. 주문이 생성되었다
12. 주문 정보가 외부 플랫폼으로 전송되었다