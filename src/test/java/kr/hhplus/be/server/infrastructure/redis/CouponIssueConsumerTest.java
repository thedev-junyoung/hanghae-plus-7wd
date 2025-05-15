package kr.hhplus.be.server.infrastructure.redis;

import kr.hhplus.be.server.application.coupon.CouponAsyncIssueService;
import kr.hhplus.be.server.application.coupon.CouponUseCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.connection.stream.Consumer;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.stream.StreamOffset;
import org.springframework.data.redis.connection.stream.StreamReadOptions;
import org.springframework.data.redis.core.StreamOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CouponIssueConsumerTest {

    @Mock
    StringRedisTemplate redisTemplate;

    @Mock
    CouponUseCase couponService;

    @Mock
    CouponAsyncIssueService issueService;

    @InjectMocks
    CouponIssueConsumer consumer;

    @Test
    @DisplayName("초기 그룹 생성: Stream 존재하지 않으면 생성 및 그룹 생성 시도")
    void initStreamGroups_createStreamAndGroup() {
        given(couponService.findAllCouponCodes()).willReturn(List.of("WELCOME10"));
        StreamOperations<String, Object, Object> streamOps = mock(StreamOperations.class);
        given(redisTemplate.hasKey(anyString())).willReturn(false);
        given(redisTemplate.opsForStream()).willReturn(streamOps);

        consumer.initStreamGroups();

        verify(streamOps, atLeastOnce()).add(anyString(), anyMap());
        verify(streamOps, atLeastOnce()).createGroup(anyString(), eq("coupon-consumer-group"));
    }

    @Test
    @DisplayName("정상적으로 레코드를 가져오고 쿠폰 발급 처리")
    void consume_shouldProcessRecords() {
        String couponCode = "WELCOME10";
        Map<Object, Object> value = Map.of(
                "userId", "1",
                "couponCode", couponCode,
                "requestId", "req1"
        );

        MapRecord<String, Object, Object> record = MapRecord.create("coupon:stream:WELCOME10", value);

        given(couponService.findAllCouponCodes()).willReturn(List.of(couponCode));
        StreamOperations<String, Object, Object> streamOps = mock(StreamOperations.class);
        given(redisTemplate.opsForStream()).willReturn(streamOps);
        given(streamOps.read(
                any(Consumer.class),
                any(StreamReadOptions.class),
                any(StreamOffset.class)
        )).willReturn(List.of(record));
        consumer.consume();

        verify(issueService).processAsync(value);
    }
}
