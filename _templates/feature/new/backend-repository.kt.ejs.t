---
to: "<%= entity ? 'backend/src/main/kotlin/com/mortitech/treasuryflow/modules/' + h.changeCase.camel(name) + '/repository/' + h.changeCase.pascal(entity) + 'Repository.kt' : null %>"
---
package com.mortitech.treasuryflow.modules.<%= h.changeCase.camel(name) %>.repository

import com.mortitech.treasuryflow.modules.<%= h.changeCase.camel(name) %>.model.<%= h.changeCase.pascal(entity) %>
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface <%= h.changeCase.pascal(entity) %>Repository : JpaRepository<<%= h.changeCase.pascal(entity) %>, Long> {

    fun findAllByOrderByCreatedAtDesc(pageable: Pageable): Page<<%= h.changeCase.pascal(entity) %>>
}
