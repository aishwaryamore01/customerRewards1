package com.infy.customerRewards.dto;

import java.util.List;

import lombok.Data;

@Data
public class CustomerDTO {
	    private String custName;
	    private String phoneNo;
	    private List<TransactionDTO> transactions;
	    
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
		public List<TransactionDTO> getTransactions() {
			return transactions;
		}
		public void setTransactions(List<TransactionDTO> transactions) {
			this.transactions = transactions;
		}
	    

}
