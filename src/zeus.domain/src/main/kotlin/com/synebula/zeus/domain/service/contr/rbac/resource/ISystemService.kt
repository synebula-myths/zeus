package com.synebula.zeus.domain.service.contr.rbac.resource

import com.synebula.gaea.domain.service.Domain
import com.synebula.gaea.domain.service.IService
import com.synebula.zeus.domain.model.rbac.resource.System

@Domain(clazz = System::class)
interface ISystemService : IService<String>