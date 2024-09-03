package beyond.orderSystem.common.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class StockInventoryService {
    @Qualifier("9")
    private final RedisTemplate<String, Object> redisTemplate;

    public StockInventoryService(@Qualifier("9") RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

//    상품 등록시 increaseStock() 호출
    public Long increaseStock(Long itemId, int quantity){
//        redis가 음수까지 내려갈 경우 추후 재고 update 상황에서 increase값이 정확하지 않을 수 있으므로,
//        음수이면 0으로 setting 로직이 필요
        Object remains = redisTemplate.opsForValue().get(String.valueOf(itemId));
        if (remains != null && Integer.parseInt(remains.toString()) < 0) {
            redisTemplate.opsForValue().set(itemId.toString(), 0);
        }
//        아래 메서드의 리턴 값은 잔량 값을 리턴(아래도 동일)
        return redisTemplate.opsForValue().increment(String.valueOf(itemId), quantity);
    }

//    주문 등록시 decreaseStock() 호출
    public Long decreaseStock(Long itemId, int quantity){
        Object remains = redisTemplate.opsForValue().get(String.valueOf(itemId));
        int intRemains = Integer.parseInt(remains.toString());
        if(intRemains < quantity) return -1L;
        else {
            return redisTemplate.opsForValue().decrement(String.valueOf(itemId), quantity);
        }
    }
}