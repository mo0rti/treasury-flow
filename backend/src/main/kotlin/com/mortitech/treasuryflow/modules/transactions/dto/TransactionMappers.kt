package com.mortitech.treasuryflow.modules.transactions.dto

import com.mortitech.treasuryflow.modules.transactions.model.Transaction

fun Transaction.toResponse() = TransactionResponse(
    id = id,
    reference = reference,
    amount = amount,
    currency = currency,
    description = description,
    type = type,
    status = status,
    createdBy = createdBy.id,
    createdAt = createdAt,
    updatedAt = updatedAt
)
