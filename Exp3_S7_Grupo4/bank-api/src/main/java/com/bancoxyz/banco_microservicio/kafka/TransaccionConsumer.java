package com.bancoxyz.banco_microservicio.kafka;

import com.bancoxyz.banco_microservicio.event.TransaccionCreadaEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class TransaccionConsumer {

    private static final Logger log =
            LoggerFactory.getLogger(TransaccionConsumer.class);

    @KafkaListener(
            topics = "transacciones-creadas",
            groupId = "bank-api-group"
    )
    public void consumirEvento(TransaccionCreadaEvent evento) {

        log.info(
                "EVENTO RECIBIDO DESDE KAFKA -> transacción: {} | fecha: {} | monto: {} | tipo: {}",
                evento.getTransaccionId(),
                evento.getFecha(),
                evento.getMonto(),
                evento.getTipo()
        );
    }
}