package com.synebula.zeus.domain.service.cmd.rbac

import com.synebula.gaea.domain.service.Command

class UserCmd : Command() {
    var id: String? = null
    var name = ""
    var password = ""
    var realName: String? = null
    var phone: String? = null
    var role: String? = null
    var group: String = ""
    var alive: Boolean = false
    var token: String? = null
}
