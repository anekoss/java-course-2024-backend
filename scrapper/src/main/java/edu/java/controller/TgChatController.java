package edu.java.controller;

import edu.java.exception.AlreadyRegisterException;
import edu.java.exception.ChatNotFoundException;
import edu.java.service.ChatService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@AllArgsConstructor
public class TgChatController {

    private final ChatService chatService;

    @PostMapping(path = "/tg-chat/{id}")
    public ResponseEntity<Void> registerChat(@PathVariable("id") Long id) throws AlreadyRegisterException {
        chatService.register(id);
        log.info("Чат зарегистрирован {}", id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping(path = "/tg-chat/{id}")
    public ResponseEntity<Void> deleteChat(@PathVariable("id") Long id) throws ChatNotFoundException {
        chatService.unregister(id);
        log.info("Чат успешно удалён {}", id);
        return ResponseEntity.ok().build();
    }

}
