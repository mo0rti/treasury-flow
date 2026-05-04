package com.mortitech.treasuryflow.modules.transactions.dto

import com.mortitech.treasuryflow.modules.transactions.model.TransactionStatus
import com.mortitech.treasuryflow.modules.transactions.model.TransactionType
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

data class TransactionResponse(
    val id: UUID,
    val reference: String,
    val amount: BigDecimal,
    val currency: String,
    val description: String?,
    val type: TransactionType,
    val status: TransactionStatus,
    val createdBy: UUID,
    val createdAt: Instant,
    val updatedAt: Instant
)
