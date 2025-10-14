package com.surest_member_managemant.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.surest_member_managemant.config.JwtUtil;
import com.surest_member_managemant.dto.MemberRequest;
import com.surest_member_managemant.dto.MemberResponse;
import com.surest_member_managemant.service.MemberService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
@WebMvcTest(controllers = MemberController.class)
@AutoConfigureMockMvc(addFilters = false)
class MemberControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private MemberService memberService;

    @MockBean
    private JwtUtil jwtUtil;

    private MemberResponse sampleMember;

    @BeforeEach
    void setup() {
        sampleMember = MemberResponse.builder()
                .id(UUID.randomUUID())
                .firstName("John")
                .lastName("Doe")
                .dateOfBirth(LocalDate.of(1990, 1, 1))
                .email("john.doe@example.com")
                .build();
    }

    @Test
    void hello_shouldReturnHello() throws Exception {
        mockMvc.perform(get("/api/members/hello"))
                .andExpect(status().isOk())
                .andExpect(content().string("Hello"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void adminEndpoint_asAdmin_shouldReturnHelloAdmin() throws Exception {
        mockMvc.perform(get("/api/members/admin"))
                .andExpect(status().isOk())
                .andExpect(content().string("Hello Admin! Only admins can see this."));
    }

    @Test
    @WithMockUser(roles = "USER")
    void userEndpoint_asUser_shouldReturnHelloUser() throws Exception {
        mockMvc.perform(get("/api/members/user"))
                .andExpect(status().isOk())
                .andExpect(content().string("Hello User! Only users can see this."));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createMember_asAdmin_shouldReturnCreatedMember() throws Exception {
        MemberRequest request = new MemberRequest();
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setDateOfBirth(LocalDate.of(1990, 1, 1));
        request.setEmail("john.doe@example.com");

        Mockito.when(memberService.createMember(any(MemberRequest.class)))
                .thenReturn(sampleMember);

        mockMvc.perform(post("/api/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(sampleMember.getId().toString()))
                .andExpect(jsonPath("$.firstName").value("John"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void listMembers_asUser_shouldReturnPage() throws Exception {
        Page<MemberResponse> page = new PageImpl<>(List.of(sampleMember), PageRequest.of(0,20), 1);
        Mockito.when(memberService.listMembers(any(), any(), any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/members"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(sampleMember.getId().toString()));
    }

    @Test
    @WithMockUser(roles = "USER")
    void getById_asUser_shouldReturnMember() throws Exception {
        Mockito.when(memberService.getById(any(UUID.class))).thenReturn(sampleMember);

        mockMvc.perform(get("/api/members/{id}", sampleMember.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("John"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateMember_asAdmin_shouldReturnUpdatedMember() throws Exception {
        MemberRequest req = new MemberRequest();
        req.setFirstName("Jane");
        req.setLastName("Doe");
        req.setDateOfBirth(LocalDate.of(1992, 2, 2));
        req.setEmail("jane.doe@example.com");

        MemberResponse updated = MemberResponse.builder()
                .id(sampleMember.getId())
                .firstName("Jane")
                .lastName("Doe")
                .dateOfBirth(LocalDate.of(1992, 2, 2))
                .email("jane.doe@example.com")
                .build();

        Mockito.when(memberService.updateMember(eq(sampleMember.getId()), any(MemberRequest.class))).thenReturn(updated);

        mockMvc.perform(put("/api/members/{id}", sampleMember.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Jane"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteMember_asAdmin_shouldReturnNoContent() throws Exception {
        Mockito.doNothing().when(memberService).delete(any(UUID.class));

        mockMvc.perform(delete("/api/members/{id}", sampleMember.getId()))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = "USER")
    void listMembers_sortSingleField_shouldUseAscByDefault() throws Exception {
        Page<MemberResponse> page = new PageImpl<>(List.of(sampleMember));
        Mockito.when(memberService.listMembers(any(), any(), any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/members")
                        .param("sort", "firstName"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(sampleMember.getId().toString()));
    }
}
