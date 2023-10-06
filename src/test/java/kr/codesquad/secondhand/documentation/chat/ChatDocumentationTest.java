package kr.codesquad.secondhand.documentation.chat;

import kr.codesquad.secondhand.application.chat.ChatLogService;
import kr.codesquad.secondhand.application.chat.ChatRoomService;
import kr.codesquad.secondhand.documentation.DocumentationTestSupport;
import kr.codesquad.secondhand.documentation.support.ConstraintsHelper;
import kr.codesquad.secondhand.presentation.dto.CustomSlice;
import kr.codesquad.secondhand.presentation.dto.chat.ChatLogResponse;
import kr.codesquad.secondhand.presentation.dto.chat.ChatRequest;
import kr.codesquad.secondhand.presentation.dto.chat.ChatRoomResponse;
import kr.codesquad.secondhand.presentation.dto.chat.SimpleChatLog;
import kr.codesquad.secondhand.presentation.dto.item.ItemSimpleResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.JsonFieldType.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class ChatDocumentationTest extends DocumentationTestSupport {

    @Autowired
    private ChatRoomService chatRoomService;
    @Autowired
    private ChatLogService chatLogService;

    @DisplayName("채팅 메시지 목록 조회")
    @Test
    void readAllChatLogs() throws Exception {
        // given
        given(chatLogService.getMessages(anyLong(), anyLong(), anyLong()))
                .willReturn(new ChatLogResponse(
                        "joy",
                        new ItemSimpleResponse("선풍기", "thumbnail", 10000L),
                        List.of(new SimpleChatLog(5L, true, "안녕하세요"),
                                new SimpleChatLog(12L, false, "네 안녕하세요")),
                        12L
                ));

        // when
        var asyncListener = mockMvc.perform(get("/api/chats/{chatRoomId}", 1)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtProvider.createAccessToken(1L))
                        .param("messageId", "3"))
                .andReturn();

        var resultActions = mockMvc.perform(asyncDispatch(asyncListener));

        // docs
        resultActions
                .andDo(document("chat/read-all-chat-logs",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("액세스 토큰을 담는 인증 헤더")
                        ),
                        pathParameters(
                                parameterWithName("chatRoomId").description("채팅방 ID")
                        ),
                        responseFields(
                                fieldWithPath("statusCode").type(NUMBER).description("응답 상태 코드"),
                                fieldWithPath("message").type(STRING).description("응답 메시지"),
                                fieldWithPath("data.chatPartnerName").type(STRING).description("채팅 대화 상대"),
                                fieldWithPath("data.item").type(OBJECT).description("채팅방에 등록된 상품"),
                                fieldWithPath("data.item.title").type(STRING).description("상품 이름"),
                                fieldWithPath("data.item.thumbnailUrl").type(STRING).description("상품 썸네일 URL"),
                                fieldWithPath("data.item.price").type(NUMBER).description("상품 가격"),
                                fieldWithPath("data.chat").type(ARRAY).description("채팅 메시지 목록"),
                                fieldWithPath("data.chat[].messageId").type(NUMBER).description("채팅 메시지 ID"),
                                fieldWithPath("data.chat[].isMe").type(BOOLEAN).description("내가 보낸 메시지인지 여부"),
                                fieldWithPath("data.chat[].message").type(STRING).description("채팅 메시지"),
                                fieldWithPath("data.nextMessageId").type(NUMBER).description("마지막으로 읽은 메시지 ID, 다음 요청으로 사용할 쿼리 파라미터")
                        )
                ));
    }

    @DisplayName("채팅방 목록 조회 - 네비게이션 바")
    @Test
    void readAllChatRooms() throws Exception {
        // given
        Long receiverId = 1L;
        Long senderId = 2L;

        var today = LocalDateTime.of(2023, 10, 6, 12, 30, 30);
        var yesterday = LocalDateTime.of(2023, 10, 5, 12, 30, 30);
        given(chatRoomService.read(anyLong(), any(Pageable.class), any()))
                .willReturn(createChatRoomResponseCustomSlice(today, yesterday));

        // when
        var asyncListener = mockMvc.perform(get("/api/chats")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtProvider.createAccessToken(receiverId)))
                .andExpect(request().asyncStarted())
                .andReturn();

        sendMessage(senderId, 4L, receiverId);

        var resultActions = mockMvc.perform(asyncDispatch(asyncListener));

        resultActions.andReturn();

        // docs
        resultActions
                .andDo(document("chat/read-all-chat-rooms",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("액세스 토큰을 담는 인증 헤더")
                        ),
                        responseFields(
                                fieldWithPath("statusCode").type(NUMBER).description("응답 상태 코드"),
                                fieldWithPath("message").type(STRING).description("응답 메시지"),
                                fieldWithPath("data.contents").type(ARRAY).description("채팅방 목록"),
                                fieldWithPath("data.contents[].chatRoomId").type(NUMBER).description("채팅방 ID"),
                                fieldWithPath("data.contents[].thumbnailUrl").type(STRING).description("채팅방에 등록된 상품 썸네일 URL"),
                                fieldWithPath("data.contents[].chatPartnerName").type(STRING).description("채팅 대화 상대"),
                                fieldWithPath("data.contents[].chatPartnerProfile").type(STRING).description("채팅 대화 상대 프로필 사진 URL"),
                                fieldWithPath("data.contents[].lastSendTime").type(STRING).description("채팅방의 마지막 메시지 전송 시간"),
                                fieldWithPath("data.contents[].lastSendMessage").type(STRING).description("채팅방의 마지막 메시지"),
                                fieldWithPath("data.contents[].newMessageCount").type(NUMBER).description("읽지 않은 메시지 개수"),
                                fieldWithPath("data.paging").type(OBJECT).description("페이징 정보"),
                                fieldWithPath("data.paging.nextCursor").type(NUMBER).description("다음 페이지로 요청할 쿼리 파라미터"),
                                fieldWithPath("data.paging.hasNext").type(BOOLEAN).description("다음 페이지가 있는지 여부")
                        )
                ));
    }

    @DisplayName("채팅 전송")
    @Test
    void sendMessage() throws Exception {
        // given
        Long chatRoomId = 1L;
        Long senderId = 1L;
        Long receiverId = 2L;

        willDoNothing().given(chatLogService).sendMessage(anyString(), anyLong(), anyLong());
        given(chatRoomService.getReceiverId(anyLong())).willReturn(receiverId);

        // when
        var response = mockMvc.perform(RestDocumentationRequestBuilders.post("/api/chats/{chatRoomId}", chatRoomId)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtProvider.createAccessToken(senderId))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"message\": \"hello\"}"));

        // then
        var resultActions = response
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.message").value("성공했습니다."));

        // docs
        resultActions
                .andDo(document("chat/send-message",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("액세스 토큰을 담는 인증 헤더")
                        ),
                        pathParameters(
                                parameterWithName("chatRoomId").description("채팅방 ID")
                        ),
                        requestFields(
                                ConstraintsHelper.withPath("message", ChatRequest.class).type(STRING).description("전송할 메시지")
                        ),
                        responseFields(
                                fieldWithPath("statusCode").type(NUMBER).description("응답 상태 코드"),
                                fieldWithPath("message").type(STRING).description("응답 메시지"),
                                fieldWithPath("data").ignored()
                        )
                ));
    }

    @DisplayName("채팅방 생성")
    @Test
    void createChatRoom() throws Exception {
        // given
        given(chatRoomService.createChatRoom(anyLong(), anyLong())).willReturn(1L);

        // when
        var response = mockMvc.perform(RestDocumentationRequestBuilders.post("/api/items/{itemId}/chats", 1)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtProvider.createAccessToken(1L)));

        // then
        var resultActions = response
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.statusCode").value(201))
                .andExpect(jsonPath("$.message").value("성공했습니다."))
                .andExpect(jsonPath("$.data.chatRoomId").value(1L));

        // docs
        resultActions
                .andDo(document("chat/create-chat-room",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("액세스 토큰을 담는 인증 헤더")
                        ),
                        pathParameters(
                                parameterWithName("itemId").description("상품 ID")
                        ),
                        responseFields(
                                fieldWithPath("statusCode").type(NUMBER).description("응답 상태 코드"),
                                fieldWithPath("message").type(STRING).description("응답 메시지"),
                                fieldWithPath("data.chatRoomId").type(NUMBER).description("생성된 채팅방 ID")
                        )
                ));
    }

    private CustomSlice<ChatRoomResponse> createChatRoomResponseCustomSlice(LocalDateTime today, LocalDateTime yesterday) {
        return new CustomSlice<>(
                List.of(new ChatRoomResponse(
                                4L,
                                "item-thumbnail",
                                "joy",
                                "joy-profile",
                                today,
                                "안녕하세요 조이에요",
                                1L
                        ),
                        new ChatRoomResponse(
                                1L,
                                "item-thumbnail",
                                "khundi",
                                "khundi-profile",
                                yesterday,
                                "쿤디에요",
                                3L
                        )),
                2L,
                true);
    }

    private void sendMessage(Long senderId, Long chatRoomId, Long receiverId) throws Exception {
        given(authenticationContext.getMemberId()).willReturn(Optional.of(senderId));
        willDoNothing().given(chatLogService).sendMessage(anyString(), anyLong(), anyLong());
        given(chatRoomService.getReceiverId(anyLong())).willReturn(receiverId);

        mockMvc.perform(post("/api/chats/{chatRoomId}", chatRoomId)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtProvider.createAccessToken(senderId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"message\": \"hello\"}"))
                .andExpect(status().isOk())
                .andDo(print());
    }
}
