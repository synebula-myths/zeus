package com.synebula.zeus.app.controller

import com.synebula.gaea.app.IApplication
import com.synebula.gaea.app.component.security.session.UserSession
import com.synebula.gaea.app.component.security.session.UserSessionManager
import com.synebula.gaea.data.message.HttpMessage
import com.synebula.gaea.data.message.HttpMessageFactory
import com.synebula.gaea.data.message.Status
import com.synebula.gaea.data.serialization.json.IJsonSerializer
import com.synebula.gaea.log.ILogger
import com.synebula.gaea.spring.aop.annotation.Method
import com.synebula.zeus.domain.service.cmd.rbac.UserCmd
import com.synebula.zeus.domain.service.contr.rbac.IUserService
import com.synebula.zeus.query.contr.IUserQuery
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/sign")
class SignInOutApp(override var logger: ILogger) : IApplication {

    @Autowired
    lateinit var userQuery: IUserQuery

    @Autowired
    lateinit var userService: IUserService

    @Autowired
    lateinit var userSessionManager: UserSessionManager

    @Autowired
    lateinit var serializer: IJsonSerializer

    @Autowired
    override lateinit var httpMessageFactory: HttpMessageFactory

    override var name: String = "用户登录管理"

    @Method("用户登录")
    @PostMapping("/in")
    fun signIn(name: String, password: String, remember: Boolean?): HttpMessage {
        return this.safeExecute("用户登录出现异常") {
            val message = this.userQuery.signIn(name, password)
            if (message.data != null) {
                val user = message.data
                user!!.remember = remember ?: false
                val token = userSessionManager.signIn(user.uid, user)
                it.data = token
            } else {
                it.load(message)
            }
        }
    }


    @Method("登录用户信息")
    @GetMapping("/user")
    fun signUser(): HttpMessage {
        val userSession = SecurityContextHolder.getContext().authentication.principal as UserSession
        return httpMessageFactory.create(userSession.user)
    }

    @Method("用户登出")
    @PostMapping("/out")
    fun signOut(token: String): HttpMessage {
        userSessionManager.signOut(token)
        return this.httpMessageFactory.create(token)
    }

    @Method("用户注册")
    @PostMapping("/up")
    fun signUp(@RequestBody command: UserCmd): HttpMessage {
        return this.safeExecute("用户注册出错, 用户信息: ${serializer.serialize(command)}") {
            val list = this.userQuery.list(mapOf(Pair("name", command.name)))
            if (list.isEmpty()) {
                val message = userService.add(command)
                it.data = message.data
            } else {
                it.status = Status.Failure
                it.message = "系统中已存在该用户"
            }
        }
    }
}