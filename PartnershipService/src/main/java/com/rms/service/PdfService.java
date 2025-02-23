package com.rms.service;

import org.apache.pdfbox.pdmodel.*;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.springframework.stereotype.Service;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Optional;

import com.rms.exeptions.NotFoundException;
import com.rms.exeptions.PdfGenerationException;
import com.rms.model.Partnership;


@Service
public class PdfService {

		
	public byte[] generatePartnershipPdf(Optional<Partnership> partnershipOptional) {
        if (partnershipOptional.isEmpty()) {
            throw new NotFoundException("Partnership record not found.");
        }

        Partnership partnership = partnershipOptional.get();

        try (PDDocument document = new PDDocument();
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);
            PDPageContentStream contentStream = new PDPageContentStream(document, page);

            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 16);
            contentStream.beginText();
            contentStream.newLineAtOffset(200, 750);
            contentStream.showText("Partnership Details Report");
            contentStream.endText();

            contentStream.setFont(PDType1Font.HELVETICA, 12);
            int yPosition = 700;
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

            String[] details = {
                "Partnership ID: " + partnership.getPartnershipId(),
                "Artist ID: " + partnership.getArtistId(),
                "Manager ID: " + partnership.getManagerId(),
                "Status: " + partnership.getStatus(),
                "Percentage: " + partnership.getPercentage() + "%",
                "Comments: " + (partnership.getComments() != null ? partnership.getComments() : "N/A"),
                "Start Date: " + sdf.format(partnership.getStartDate()),
                "End Date: " + sdf.format(partnership.getEndDate()),
                "Duration: " + partnership.getDurationMonths() + " months"
            };

            for (String detail : details) {
                contentStream.beginText();
                contentStream.newLineAtOffset(50, yPosition);
                contentStream.showText(detail);
                contentStream.endText();
                yPosition -= 20;
            }

            contentStream.close();
            document.save(outputStream);
            return outputStream.toByteArray();
        } catch (IOException e) {
            throw new PdfGenerationException("Error generating Partnership PDF", e);
        }
    }
}
