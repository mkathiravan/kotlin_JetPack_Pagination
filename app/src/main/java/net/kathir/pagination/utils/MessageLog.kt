package net.kathir.pagination.utils

import android.util.Log

class MessageLog {

    companion object{

        fun setLogCat(tag: String, msg: String?) {
            Log.i("$tag :", msg!!)
        }
    }
}