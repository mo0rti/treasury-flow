package com.mortitech.treasuryflow.modules.transactions.dto

import com.mortitech.treasuryflow.modules.transactions.model.TransactionStatus
import jakarta.validation.constraints.Size

data class UpdateTransactionRequest(
    @field:Size(max = 2000) val description: String? = null,
    val status: TransactionStatus? = null
)
