package com.example.vetclinic.controller;

import com.example.vetclinic.entity.ChatMessage;
import com.example.vetclinic.entity.Role;
import com.example.vetclinic.entity.User;
import com.example.vetclinic.repository.ChatMessageRepository;
import com.example.vetclinic.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @Autowired
    private UserRepository userRepository;

    // 📩 Отправить сообщение
    @PostMapping("/send")
    public ResponseEntity<?> sendMessage(@RequestParam Long receiverId,
                                         @RequestParam String content,
                                         Principal principal) {
        User sender = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException("Sender not found"));

        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new UsernameNotFoundException("Receiver not found"));

        ChatMessage message = new ChatMessage();
        message.setSender(sender);
        message.setReceiver(receiver);
        message.setContent(content);
        message.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(chatMessageRepository.save(message));
    }

    // 📖 Получить чат между текущим пользователем и другим
    @GetMapping("/with/{userId}")
    public ResponseEntity<?> getConversation(@PathVariable Long userId, Principal principal) {
        User current = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        User other = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("Other user not found"));

        List<ChatMessage> messages = chatMessageRepository.findConversation(current, other);


        return ResponseEntity.ok(messages);
    }


    @GetMapping("/contacts")
    public ResponseEntity<?> getChatContacts(Principal principal) {
        User current = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        List<ChatMessage> messages = chatMessageRepository.findBySenderOrReceiver(current, current);

        Set<User> contacts = new HashSet<>();
        for (ChatMessage msg : messages) {
            if (!msg.getSender().equals(current)) contacts.add(msg.getSender());
            if (!msg.getReceiver().equals(current)) contacts.add(msg.getReceiver());
        }

        return ResponseEntity.ok(contacts);
    }

    // Только клиентов для ветеринара и только ветеринаров для клиента
    @GetMapping("/available")
    public ResponseEntity<?> getAvailableChatUsers(Principal principal) {
        User current = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        Role targetRole = current.getRole() == Role.CLIENT ? Role.VET : Role.CLIENT;

        List<User> available = userRepository.findByRole(targetRole);
        return ResponseEntity.ok(available);
    }


}
