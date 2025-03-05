package com.rms.mock;

import com.rms.exeptions.NotFoundException;
import com.rms.exeptions.PdfGenerationException;
import com.rms.model.Partnership;
import com.rms.service.PdfService;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.io.IOException;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PdfServiceTest {

	@Spy
    @InjectMocks
    private PdfService pdfService;
    
    @Mock
    private PDDocument documentMock;

    @Mock
    private PDPage pageMock;

    @Mock
    private PDPageContentStream contentStreamMock;

    private Partnership partnership;

    @BeforeEach
    void setUp() {
        partnership = new Partnership();
        partnership.setPartnershipId(1);
        partnership.setArtistId(100);
        partnership.setManagerId(200);
        partnership.setStatus("Active");
        partnership.setPercentage(50.00);
        partnership.setComments("Successful partnership");
        partnership.setStartDate(new Date());
        partnership.setEndDate(new Date());
        partnership.setDurationMonths(12);
    }

    @Test
    void generatePartnershipPdf_ShouldReturnByteArray_WhenPartnershipExists() {
        Optional<Partnership> partnershipOptional = Optional.of(partnership);
        byte[] pdfBytes = pdfService.generatePartnershipPdf(partnershipOptional);
        
        assertNotNull(pdfBytes);
        assertTrue(pdfBytes.length > 0);
    }

    @Test
    void generatePartnershipPdf_ShouldThrowNotFoundException_WhenPartnershipIsEmpty() {
        Optional<Partnership> emptyOptional = Optional.empty();
        
        assertThrows(NotFoundException.class, () -> pdfService.generatePartnershipPdf(emptyOptional));
    }

    @Test
    void generatePartnershipPdf_ShouldThrowPdfGenerationException_OnIOException() throws Exception {
        // Create a sample Partnership
        Partnership partnership = new Partnership();
        partnership.setPartnershipId(1);
        Optional<Partnership> partnershipOptional = Optional.of(partnership);

        // Spy on the pdfService and force a PdfGenerationException inside the method
        doThrow(new PdfGenerationException("Simulated PdfGenerationException", new IOException()))
        .when(pdfService)
        .generatePartnershipPdf(any());


        // Ensure the method throws PdfGenerationException
        assertThrows(PdfGenerationException.class, () -> pdfService.generatePartnershipPdf(partnershipOptional));
    }

}
