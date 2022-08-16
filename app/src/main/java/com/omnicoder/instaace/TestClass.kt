package com.omnicoder.instaace


class Solution {
//    fun isValid(s: String): Boolean {
//        val isOpen  = mapOf('(' to true, '{' to true,'[' to true, ')' to false , ']' to false, '}' to false)
//        val closers= mapOf(')' to '(' , ']' to '[', '}' to '{')
//        val checkers= mapOf(')' to 1,'(' to 1, '[' to 2, ']' to 2, '{' to 3, '}' to 3)
//        val size= s.length
//        val array: CharArray= s.toCharArray()
//        if(isOpen[array[0]]==false){
//            return false
//        }
//        var index = 1
//        var changedIndex = -1
//        while (index < size) {
//            val current = array[index]
//            if (isOpen[current] == false) {
//                if (checkers[current] != checkers[array[index - 1]]) {
//                    val previousBracketIndex = if (changedIndex == -1) index - 1 else changedIndex - 1
//                    val previousBracket = array[previousBracketIndex]
//                    if (closers[current] == previousBracket) {
//                        changedIndex = previousBracketIndex
//                    } else {
//                        return false
//                    }
//
//                }
//
//            }
//            index += 1
//        }
//
//        return true
//    }
    fun isValid(s: String): Boolean {
        val isOpen  = mapOf('(' to true, '{' to true,'[' to true, ')' to false , ']' to false, '}' to false)
        val closers= mapOf(')' to '(' , ']' to '[', '}' to '{')
        val checkers= mapOf(')' to 1,'(' to 1, '[' to 2, ']' to 2, '{' to 3, '}' to 3)
        val size= s.length
        val array: CharArray= s.toCharArray()
        if(isOpen[array[0]]==false){
            return false
        }
        var index = 1
        var changedIndex = -1
        while (index < size) {
            val current = array[index]
            index += 1
        }

        return true
    }
}

fun main(){
    val solution= Solution()
    solution.isValid("([{}])")

}