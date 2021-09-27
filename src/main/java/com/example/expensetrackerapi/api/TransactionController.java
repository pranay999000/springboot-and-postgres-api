package com.example.expensetrackerapi.api;

import com.example.expensetrackerapi.domain.Transaction;
import com.example.expensetrackerapi.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/categories/{categoryId}/")
public class TransactionController {

    @Autowired
    TransactionService transactionService;

    @PostMapping("transaction")
    public ResponseEntity<Transaction> addTransaction(
            HttpServletRequest request,
            @PathVariable("categoryId") Integer categoryId,
            @RequestBody Map<String, Object> transactionMap
    ) {
        int userId = (Integer) request.getAttribute("userId");
        Double amount = Double.valueOf(transactionMap.get("amount").toString());
        String note = (String) transactionMap.get("note");
        Long transactionDate = (Long) transactionMap.get("transaction_date");

        Transaction transaction = transactionService.addTransaction(
                userId,
                categoryId,
                amount,
                note,
                transactionDate
        );

        return new ResponseEntity<>(transaction, HttpStatus.CREATED);
    }

    @GetMapping("transaction")
    public ResponseEntity<List<Transaction>> getAllTransactions(
            HttpServletRequest request,
            @PathVariable("categoryId") Integer categoryId
    ) {
        int userId = (Integer) request.getAttribute("userId");

        List<Transaction> transactions = transactionService.fetchAllTransactions(userId, categoryId);

        return new ResponseEntity<>(transactions, HttpStatus.OK);
    }

    @GetMapping("transaction/{transactionId}")
    public ResponseEntity<Transaction> getTransactionById(
            HttpServletRequest request,
            @PathVariable("categoryId") Integer categoryId,
            @PathVariable("transactionId") Integer transactionId
    ) {
        int userId = (Integer) request.getAttribute("userId");

        Transaction transaction = transactionService.fetchTransactionById(userId, categoryId, transactionId);

        return new ResponseEntity<>(transaction, HttpStatus.OK);
    }

    @PutMapping("transaction/{transactionId}")
    public ResponseEntity<Map<String, Boolean>> updateTransaction(
            HttpServletRequest request,
            @PathVariable("categoryId") Integer categoryId,
            @PathVariable("transactionId") Integer transactionId,
            @RequestBody Transaction transaction
    ) {
        int userId = (Integer) request.getAttribute("userId");

        transactionService.updateTransaction(
                userId,
                categoryId,
                transactionId,
                transaction
        );

        Map<String, Boolean> map = new HashMap<>();
        map.put("Success", true);

        return new ResponseEntity<>(map, HttpStatus.OK);
    }

    @DeleteMapping("transaction/{transactionId}")
    public ResponseEntity<Map<String, Boolean>> deleteTransaction(
            HttpServletRequest request,
            @PathVariable("categoryId") Integer categoryId,
            @PathVariable("transactionId") Integer transactionId
    ) {
        int userId = (Integer) request.getAttribute("userId");

        transactionService.removeTransaction(
                userId,
                 categoryId,
                transactionId
        );

        Map<String, Boolean> map = new HashMap<>();
        map.put("Success", true);

        return new ResponseEntity<>(map, HttpStatus.OK);
    }

}
