package com.rms.controller;

import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.rms.CONSTANTS;
import com.rms.model.Transactions;
import com.rms.service.PdfService;
import com.rms.service.TransactionService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/trans")
@CrossOrigin(origins = "*")
public class TransactionController {

	private final TransactionService transactionService;
	private final PdfService pdfService;	

	@GetMapping("/showTrans")
	public List<Transactions> showTransactions() {
		return transactionService.showTrans();
	}

	@GetMapping("/showTransById/{id}")
	public List<Transactions> showByUserTransactions(@PathVariable int id) {
		return transactionService.showTransByReceiver(id);
	}

	@GetMapping("/showTransByManId/{id}")
	public List<Transactions> showByManTrans(@PathVariable int id) {
		return transactionService.showTransByManId(id);
	}

	@PostMapping("/addTransaction")
	public ResponseEntity<String> addTransaction(@RequestBody Transactions transaction) {
		transactionService.addTransaction(transaction);
		return ResponseEntity.ok("Transaction successfully added");
	}

	@GetMapping("/export-pdf")
	public ResponseEntity<byte[]> exportAllTransactionsToPDF() {
		List<Transactions> transactions = transactionService.showTrans();
		byte[] pdfBytes = pdfService.generateTransactionPdf(transactions);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_PDF);
		headers.setContentDispositionFormData(CONSTANTS.ATTACHMENT, "alltransactions.pdf");

		return ResponseEntity.ok().headers(headers).body(pdfBytes);
	}

	@GetMapping("/exportPDF/{userId}")
	public ResponseEntity<byte[]> exportTransactionsToPDF(@PathVariable int userId) {
		List<Transactions> transactions = transactionService.getTransactionsByReceiver(userId);
		byte[] pdfBytes = pdfService.generateTransactionPdf(transactions);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_PDF);
		headers.setContentDispositionFormData(CONSTANTS.ATTACHMENT, "transactions.pdf");

		return ResponseEntity.ok().headers(headers).body(pdfBytes);
	}

	@GetMapping("/exportPDF/manager/{managerId}")
	public ResponseEntity<byte[]> exportTransactionsByManagerToPDF(@PathVariable int managerId) {
		List<Transactions> transactions = transactionService.showTransByManId(managerId);
		byte[] pdfBytes = pdfService.generateTransactionPdf(transactions);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_PDF);
		headers.setContentDispositionFormData(CONSTANTS.ATTACHMENT, "manager_transactions.pdf");

		return ResponseEntity.ok().headers(headers).body(pdfBytes);
	}
	@GetMapping("/exportTransPDF/{transactionId}")
    public ResponseEntity<byte[]> exportSingleTransactionToPDF(@PathVariable int transactionId) {
        // ✅ Fetch the specific transaction
        Transactions transaction = transactionService.getTransactionById(transactionId);
        if (transaction == null) {
            return ResponseEntity.noContent().build();
        }
 
        // ✅ Generate a PDF with only this transaction
        byte[] pdfBytes = pdfService.generateTransactionPdf(List.of(transaction));
 
        // ✅ Set headers for downloading
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "transaction_" + transactionId + ".pdf");
 
        return ResponseEntity.ok().headers(headers).body(pdfBytes);
    }
}
