---
to: "<%= entity ? 'backend/src/main/kotlin/com/mortitech/treasuryflow/modules/' + h.changeCase.camel(name) + '/model/' + h.changeCase.pascal(entity) + '.kt' : null %>"
---
package com.mortitech.treasuryflow.modules.<%= h.changeCase.camel(name) %>.model

import com.mortitech.treasuryflow.shared.audit.AuditableEntity
import jakarta.persistence.*
import java.util.UUID

@Entity
@Table(name = "<%= h.changeCase.snake(h.inflection.pluralize(entity)) %>")
class <%= h.changeCase.pascal(entity) %>(
    @Id
    val id: UUID = UUID.randomUUID(),

    // TODO: Add entity fields
) : AuditableEntity()
