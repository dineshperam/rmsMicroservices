package com.rms.mock;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rms.controller.PartnershipController;
import com.rms.exeptions.NotFoundException;
import com.rms.model.Partnership;
import com.rms.repository.PartnershipRepository;
import com.rms.service.PartnershipService;
import com.rms.service.PdfService;

@ExtendWith(MockitoExtension.class)
class PartnershipControllerTest {

    private MockMvc mockMvc;

    @Mock
    private PartnershipService partnershipService;

    @Mock
    private PdfService pdfService;

    @InjectMocks
    private PartnershipController partnershipController;
    
    @Mock
    private PartnershipRepository partnershipRepository;


    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(partnershipController).build();
    }

    @Test
    void testGetAllPartnerships() throws Exception {
        when(partnershipService.getAllPartnerships()).thenReturn(List.of(new Partnership()));
        mockMvc.perform(get("/partnerships/showAllPartners"))
                .andExpect(status().isOk());
        verify(partnershipService, times(1)).getAllPartnerships();
    }

    @Test
    void testGetPartnershipById() throws Exception {
        // The controller uses repository.findById(), not service.getPartnershipById()
        Partnership partnership = new Partnership();
        when(partnershipRepository.findById(1)).thenReturn(Optional.of(partnership));

        mockMvc.perform(get("/partnerships/showbyID/1"))
                .andExpect(status().isOk());
        
        verify(partnershipRepository, times(1)).findById(1);
    }

    @Test
    void testGetPartnershipById_NotFound() throws Exception {
        // The controller uses repository.findById(), not service.getPartnershipById()
        when(partnershipRepository.findById(1)).thenReturn(Optional.empty());

        mockMvc.perform(get("/partnerships/showbyID/1"))
                .andExpect(status().isNotFound());
        
        verify(partnershipRepository, times(1)).findById(1);
    }

    @Test
    void testCreatePartnership() throws Exception {
        Partnership partnership = new Partnership();
        when(partnershipService.createPartnership(any())).thenReturn(partnership);

        mockMvc.perform(post("/partnerships/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(partnership)))
                .andExpect(status().isCreated());
        verify(partnershipService, times(1)).createPartnership(any());
    }

    @Test
    void testDeletePartnership() throws Exception {
        doNothing().when(partnershipService).deletePartnership(1);

        mockMvc.perform(delete("/partnerships/deleteById/1"))
                .andExpect(status().isOk());
        verify(partnershipService, times(1)).deletePartnership(1);
    }

    @Test
    void testGetLatestPartnershipByArtistId_NotFound() throws Exception {
        when(partnershipService.getPartnershipByArtistId(1)).thenReturn(Optional.empty());

        mockMvc.perform(get("/partnerships/latest/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testExportPartnershipsToPdf_NotFound() throws Exception {
        when(partnershipService.getPartnershipByArtistId(1)).thenReturn(Optional.empty());

        mockMvc.perform(get("/partnerships/export-pdf-partner/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testSendRequest_Success() throws Exception {
        Partnership partnership = new Partnership();
        when(partnershipService.sendRequest(anyInt(), anyInt(), anyDouble(), anyInt(), anyString())).thenReturn(partnership);

        mockMvc.perform(post("/partnerships/request")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "artistId", 1,
                                "managerId", 2,
                                "percentage", 10.0,
                                "durationMonths", 12,
                                "comments", "Test Comment"
                        ))))
                .andExpect(status().isOk());
    }

    @Test
    void testSendRequest_MissingFields() throws Exception {
        mockMvc.perform(post("/partnerships/request")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of())))
                .andExpect(status().isBadRequest());
    }
}
