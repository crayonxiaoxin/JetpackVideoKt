package com.github.crayonxiaoxin.ppjoke_kt.model

class BaseModel<T> {
    var status: Int? = null
    var message: String? = null
    var data: T? = null
}

data class Base<T>(
    var status: Int,
    var message: String,
    var data: Data<T>,
) {
    data class Data<T>(var data: T? = null)
}