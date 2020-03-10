package hzkj.cc.base.base

interface Convert<T> {
    fun convert(holder: CommonAdapter.BaseHolder, position: Int,data:T)
}