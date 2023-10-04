package kr.codesquad.secondhand.documentation.presentation;

import kr.codesquad.secondhand.application.chat.ChatLogService;
import kr.codesquad.secondhand.application.chat.ChatRoomService;
import kr.codesquad.secondhand.documentation.DocumentationTestSupport;
import kr.codesquad.secondhand.presentation.dto.ApiResponse;
import kr.codesquad.secondhand.presentation.dto.CustomSlice;
import kr.codesquad.secondhand.presentation.dto.chat.ChatRoomResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ChatControllerTest extends DocumentationTestSupport {

    @Autowired
    private ChatLogService chatLogService;

    @Autowired
    private ChatRoomService chatRoomService;

    @DisplayName("채팅방 목록 조회")
    @Test
    void readAllChatRooms() throws Exception {
        // given
        Long chatRoomId = 1L;
        Long receiverId = 1L;
        Long senderId = 2L;

        var pagedChatRoomResponse = createPagedChatRoomResponse(chatRoomId);

        willDoNothing().given(chatLogService).sendMessage(anyString(), anyLong(), anyLong());
        given(chatRoomService.getReceiverId(anyLong())).willReturn(receiverId);
        given(chatRoomService.read(anyLong(), any(Pageable.class))).willReturn(pagedChatRoomResponse);

        var asyncListener = mockMvc.perform(MockMvcRequestBuilders.request(HttpMethod.GET, "/api/chats")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtProvider.createAccessToken(receiverId)))
                .andExpect(request().asyncStarted())
                .andReturn();

        // when
        sendMessage(senderId, chatRoomId);

        var resultActions = mockMvc.perform(asyncDispatch(asyncListener));

        // then
        String response = resultActions
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        ApiResponse<CustomSlice<ChatRoomResponse>> apiResponse = objectMapper.readValue(response, ApiResponse.class);

        assertThat(apiResponse.getStatusCode()).isEqualTo(200);
    }

    private CustomSlice<ChatRoomResponse> createPagedChatRoomResponse(Long chatRoomId) {
        var chatRoomResponse = new ChatRoomResponse(
                chatRoomId,
                "item-thumbnail",
                "sender",
                "sender-profile",
                LocalDateTime.now(),
                "",
                1L);
        return new CustomSlice<>(List.of(chatRoomResponse), null, false);
    }

    private void sendMessage(Long senderId, Long chatRoomId) throws Exception {
        given(authenticationContext.getMemberId()).willReturn(Optional.of(senderId));

        mockMvc.perform(post("/api/chats/{chatRoomId}", chatRoomId)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtProvider.createAccessToken(senderId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"message\": \"hello\"}"))
                .andExpect(status().isOk())
                .andDo(print());
    }
}
