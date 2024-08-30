//package beyond.orderSystem.ordering.service;
//
//import beyond.orderSystem.common.configs.RabbitMqConfig;
//import beyond.orderSystem.ordering.dto.StockDecreaseEvent;
//import beyond.orderSystem.product.domain.Product;
//import beyond.orderSystem.product.repository.ProductRepository;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.springframework.amqp.core.Message;
//import org.springframework.amqp.core.QueueInformation;
//import org.springframework.amqp.rabbit.annotation.RabbitListener;
//import org.springframework.amqp.rabbit.connection.ConnectionFactory;
//import org.springframework.amqp.rabbit.core.RabbitAdmin;
//import org.springframework.amqp.rabbit.core.RabbitTemplate;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.io.IOException;
//
//@Component
//public class StokDecreaseEventHandler {
//
//    @Autowired
//    private RabbitTemplate rabbitTemplate;
//
//    @Autowired
//    private ProductRepository productRepository;
//
//    @Autowired
//    private RabbitMqConfig rabbitMqConfig;
//
//    public void publish(StockDecreaseEvent event){
//        rabbitTemplate.convertAndSend(RabbitMqConfig.STOCK_DECREASE_QUEUE, event);
//    }
//
//    public int getMessageCount(ConnectionFactory connectionFactory, String queueName) {
//        RabbitAdmin rabbitAdmin = new RabbitAdmin(connectionFactory);
//        QueueInformation queueInfo = rabbitAdmin.getQueueInfo(queueName);
//        if (queueInfo != null) {
//            return queueInfo.getMessageCount();
//        } else {
//            throw new RuntimeException("Queue not found: " + queueName);
//        }
//    }
//
////    트랜잭션이 완료된 이후에 다음 메시지를 수신하므로, 동시성 이슈 발생 X
//    @Transactional
//    @RabbitListener(queues = RabbitMqConfig.STOCK_DECREASE_QUEUE)
//    public void listen(Message message){
//        String messageBody = new String(message.getBody());
////        json 메시지를 ObjectMapper로 직접 parsing
//        ObjectMapper obj = new ObjectMapper();
//        try {
//            StockDecreaseEvent stockDecreaseEvent = obj.readValue(messageBody, StockDecreaseEvent.class);
//            Product product = productRepository.findById(stockDecreaseEvent.getProductId()).orElse(null);
//            if(product != null)product.updateQuantity(stockDecreaseEvent.getProductCount());
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//    }
//}
