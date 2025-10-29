package com.infy.customerRewards.dto;

import java.util.List;

import lombok.Data;

@Data
public class CustomerResponseDTO {
	private Long id;
    private String custName;
    private String phoneNo;
    private List<TransactionResponseDTO> transactions;
    
	public String getCustName() {
		return custName;
	}
	public void setCustName(String custName) {
		this.custName = custName;
	}
	public String getPhoneNo() {
		return phoneNo;
	}
	public void setPhoneNo(String phoneNo) {
		this.phoneNo = phoneNo;
	}
    
}
