package com.example.vetclinic.repository;

import com.example.vetclinic.entity.ChatMessage;
import com.example.vetclinic.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    // Получить всю переписку между двумя пользователями
    @Query("""
    SELECT m FROM ChatMessage m
    WHERE (m.sender = :user1 AND m.receiver = :user2)
       OR (m.sender = :user2 AND m.receiver = :user1)
    ORDER BY m.timestamp ASC
""")
    List<ChatMessage> findConversation(@Param("user1") User user1, @Param("user2") User user2);


    List<ChatMessage> findBySenderOrReceiver(User sender, User receiver);


}
