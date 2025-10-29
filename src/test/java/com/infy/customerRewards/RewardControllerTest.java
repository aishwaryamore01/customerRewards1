package com.infy.customerRewards;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.infy.customerRewards.controller.RewardController;
import com.infy.customerRewards.dto.CustomerDTO;
import com.infy.customerRewards.dto.CustomerResponseDTO;
import com.infy.customerRewards.dto.RewardResponseDTO;
import com.infy.customerRewards.dto.TransactionDTO;
import com.infy.customerRewards.service.RewardService;

import java.time.LocalDate;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
class RewardControllerTest {

    @InjectMocks
    private RewardController rewardController;

    @Mock
    private RewardService rewardService;

    private CustomerDTO customerDTO;
    private CustomerResponseDTO customerResponseDTO;
    private TransactionDTO txDto1, txDto2;
    private RewardResponseDTO rewardResponseDTO;

    @BeforeEach
    void setUp() {
        customerDTO = new CustomerDTO();
        customerDTO.setCustName("John Doe");
        customerDTO.setPhoneNo("1234567890");

        customerResponseDTO = new CustomerResponseDTO();
        customerResponseDTO.setCustName("John Doe");
        customerResponseDTO.setPhoneNo("1234567890");

        txDto1 = new TransactionDTO();
        txDto1.setAmount(40.0);
        txDto1.setDate(LocalDate.of(2025, 8, 1));
        txDto1.setProduct("Product A");
        txDto1.setRewardPoints(0);

        txDto2 = new TransactionDTO();
        txDto2.setAmount(120.0);
        txDto2.setDate(LocalDate.of(2025, 8, 10));
        txDto2.setProduct("Product B");
        txDto2.setRewardPoints(90);

       

        rewardResponseDTO = new RewardResponseDTO();
        rewardResponseDTO.setCustomerId(1L);
        rewardResponseDTO.setCustName("John Doe");
        rewardResponseDTO.setTransactions(Arrays.asList(txDto1, txDto2));
        rewardResponseDTO.setTotalRewards(90);
        rewardResponseDTO.setMonthlyRewards(Map.of("2025-08", 90));
        rewardResponseDTO.setTimeFrame(Map.of("startDate", "2025-08-01", "endDate", "2025-08-31"));
    }

    // ---------------- POST /customers ----------------
    @Test
    @DisplayName("Should create a new customer successfully")
    void testCreateCustomerSuccess() {
        when(rewardService.createCustomer(customerDTO)).thenReturn(customerResponseDTO);

        ResponseEntity<CustomerResponseDTO> response = rewardController.createCustomer(customerDTO);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("John Doe", response.getBody().getCustName());
        verify(rewardService, times(1)).createCustomer(customerDTO);
    }

    // ---------------- GET /customers/{id}/transactions ----------------
    @Test
    @DisplayName("Should return customer transactions successfully")
    void testGetCustomerTransactionsSuccess() {
        when(rewardService.getCustomerTransactions(1L)).thenReturn(Arrays.asList(txDto1, txDto2));

        ResponseEntity<List<TransactionDTO>> response = rewardController.getCustomerTransactions(1L);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(2, response.getBody().size());
        assertEquals(90, response.getBody().get(1).getRewardPoints());
        verify(rewardService, times(1)).getCustomerTransactions(1L);
    }

    @Test
    @DisplayName("Should return empty list when customer has no transactions")
    void testGetCustomerTransactionsEmpty() {
        when(rewardService.getCustomerTransactions(1L)).thenReturn(Collections.emptyList());

        ResponseEntity<List<TransactionDTO>> response = rewardController.getCustomerTransactions(1L);

        assertNotNull(response);
        assertTrue(response.getBody().isEmpty());
        verify(rewardService, times(1)).getCustomerTransactions(1L);
    }

    // ---------------- GET /customers/{id}/rewards ----------------
    @Test
    @DisplayName("Should return rewards for customer successfully within timeframe")
    void testGetRewardsForCustomerSuccess() {
        LocalDate start = LocalDate.of(2025, 8, 1);
        LocalDate end = LocalDate.of(2025, 8, 31);
        when(rewardService.getRewardsForCustomer(1L, start, end)).thenReturn(rewardResponseDTO);

        ResponseEntity<RewardResponseDTO> response = rewardController.getRewardsForCustomer(1L, start, end);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(90, response.getBody().getTotalRewards());
        assertEquals("2025-08-01", response.getBody().getTimeFrame().get("startDate"));
        verify(rewardService, times(1)).getRewardsForCustomer(1L, start, end);
    }

    @Test
    @DisplayName("Should throw exception when no transactions found for customer in timeframe")
    void testGetRewardsForCustomerNoTransactions() {
        LocalDate start = LocalDate.of(2025, 8, 1);
        LocalDate end = LocalDate.of(2025, 8, 31);
        when(rewardService.getRewardsForCustomer(1L, start, end))
                .thenThrow(new RuntimeException("No transactions found"));

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                rewardController.getRewardsForCustomer(1L, start, end)
        );

        assertEquals("No transactions found", ex.getMessage());
        verify(rewardService, times(1)).getRewardsForCustomer(1L, start, end);
    }

    // ---------------- Edge Cases ----------------
    @Test
    @DisplayName("Should handle null list when fetching customer transactions")
    void testGetCustomerTransactionsNullList() {
        when(rewardService.getCustomerTransactions(1L)).thenReturn(null);

        ResponseEntity<List<TransactionDTO>> response = rewardController.getCustomerTransactions(1L);
        assertNull(response.getBody());
        verify(rewardService, times(1)).getCustomerTransactions(1L);
    }
}
