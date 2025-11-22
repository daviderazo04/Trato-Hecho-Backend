package com.tratoHecho.backend_trato_hecho.controller;

import com.tratoHecho.backend_trato_hecho.dto.InboxDTO;
import com.tratoHecho.backend_trato_hecho.dto.MensajeRequestDTO;
import com.tratoHecho.backend_trato_hecho.dto.MensajeResponseDTO;
import com.tratoHecho.backend_trato_hecho.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ChatController {

    private final ChatService chatService;

    @PostMapping("/send")
    public ResponseEntity<MensajeResponseDTO> sendMessage(@RequestBody MensajeRequestDTO request) {
        return ResponseEntity.ok(chatService.sendMessage(request));
    }

    @GetMapping("/inbox/{userId}")
    public ResponseEntity<List<InboxDTO>> getUserInbox(@PathVariable("userId") Long userId) {
        return ResponseEntity.ok(chatService.getUserInbox(userId));
    }

    @GetMapping("/history/{conId}")
    public ResponseEntity<List<MensajeResponseDTO>> getChatHistory(
            @PathVariable("conId") Long conId, 
            @RequestParam("userId") Long userId) {
        return ResponseEntity.ok(chatService.getChatHistory(conId, userId));
    }

    @GetMapping("/unread-status/{userId}")
    public ResponseEntity<Map<String, Boolean>> getUnreadStatus(@PathVariable("userId") Long userId) {
        boolean hasUnread = chatService.hasUnreadMessagesGlobal(userId);
        return ResponseEntity.ok(Map.of("hasUnread", hasUnread));
    }

    // --- ACTUALIZADO: Recibe serId opcional para verificar chat específico ---
    @GetMapping("/check/{receiverId}")
    public ResponseEntity<Long> getConversationId(
            @PathVariable("receiverId") Long receiverId,
            @RequestParam("senderId") Long senderId,
            @RequestParam(value = "serId", required = false) Long serId) { // Nuevo parámetro
        return ResponseEntity.ok(chatService.getConversationId(senderId, receiverId, serId));
    }
}