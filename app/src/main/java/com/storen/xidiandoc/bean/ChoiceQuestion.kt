package com.storen.xidiandoc.bean

data class ChoiceQuestion(
    val question: String,
    val itemList: List<String> = listOf("A.正确", "B.错误"),
    val answerIndexes: List<Int> = findAnswer(question)
) {
    companion object {
        fun findAnswer(question: String): List<Int> {
            val list = mutableListOf<Int>()
            if (question.contains("（A）")) list.add(0)
            if (question.contains("（B）")) list.add(1)
            if (question.contains("（C）")) list.add(2)
            if (question.contains("（D）")) list.add(3)
            return list
        }

        fun fixQuestionTitle(num: Int, str: String): String {
            val title = str.trim().run {
                replace('(', '（')
                replace(')', ')')
            }
            return if (title.contains("$num. ")) title else "$num. $title"
        }

        fun fixAnswerList(list: List<String>) = list.mapIndexed { index, s ->
            val item = s.trim()
            val orderNum = when (index) {
                0 -> "A."
                1 -> "B."
                2 -> "C."
                3 -> "D."
                else -> ""
            }
            if (s.contains(orderNum)) item else "$orderNum $item"
        }
    }
}