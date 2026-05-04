---
to: "<%= entity ? 'backend/src/main/kotlin/com/mortitech/treasuryflow/modules/' + h.changeCase.camel(name) + '/service/' + h.changeCase.pascal(entity) + 'Service.kt' : null %>"
---
package com.mortitech.treasuryflow.modules.<%= h.changeCase.camel(name) %>.service

import com.mortitech.treasuryflow.shared.exception.ApiException
import com.mortitech.treasuryflow.modules.<%= h.changeCase.camel(name) %>.model.<%= h.changeCase.pascal(entity) %>
import com.mortitech.treasuryflow.modules.<%= h.changeCase.camel(name) %>.repository.<%= h.changeCase.pascal(entity) %>Repository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class <%= h.changeCase.pascal(entity) %>Service(
    private val repository: <%= h.changeCase.pascal(entity) %>Repository,
) {

    fun findAll(pageable: Pageable): Page<<%= h.changeCase.pascal(entity) %>> =
        repository.findAllByOrderByCreatedAtDesc(pageable)

    fun findById(id: Long): <%= h.changeCase.pascal(entity) %> =
        repository.findById(id).orElseThrow {
            ApiException("<%= h.changeCase.pascal(entity) %> not found", HttpStatus.NOT_FOUND)
        }

    @Transactional
    fun create(entity: <%= h.changeCase.pascal(entity) %>): <%= h.changeCase.pascal(entity) %> =
        repository.save(entity)

    @Transactional
    fun delete(id: Long) {
        if (!repository.existsById(id)) {
            throw ApiException("<%= h.changeCase.pascal(entity) %> not found", HttpStatus.NOT_FOUND)
        }
        repository.deleteById(id)
    }
}
