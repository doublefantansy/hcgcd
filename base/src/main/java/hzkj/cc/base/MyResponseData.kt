package hzkj.cc.base

data class MyResponseData<T>(
  var code: Int,
  var msg: String,
  var data: T
)