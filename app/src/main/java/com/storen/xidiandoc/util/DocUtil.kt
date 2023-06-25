package com.storen.xidiandoc.util

import android.content.res.AssetManager
import android.text.TextUtils
import com.storen.xidiandoc.bean.ChoiceQuestion
import org.apache.poi.hwpf.HWPFDocument
import org.apache.poi.hwpf.extractor.WordExtractor

object DocUtil {

    private const val CHOICE_ANSWER_COUNT = 4
    private const val YES_OR_NO_ANSWER_COUNT = 1

    fun readDocFile1(assets: AssetManager): List<String> {
        return assets.open("xidian_doc_1.doc").use {
            val wordExtractor = WordExtractor(it)
            wordExtractor.paragraphText.toList()
        }
    }

    fun parseDoc1(list: List<String>): List<ChoiceQuestion> {
        val questionList: MutableList<ChoiceQuestion> = mutableListOf()
        val filterList = list.filter { !TextUtils.isEmpty(it) }
        val indexOfChoice = filterList.indexOfFirst { it.contains("选择题") }
        val indexOfYesOrNo = filterList.indexOfFirst { it.contains("判断题") }
        val choiceList = filterList.subList(indexOfChoice + 1, indexOfYesOrNo)
        val yesOrNoList = filterList.subList(indexOfYesOrNo + 1, filterList.size)
        var questionNum = 0
        choiceList.windowed(5, 5) {
            questionNum++
            questionList.add(
                ChoiceQuestion(
                    ChoiceQuestion.fixQuestionTitle(questionNum, it.first()),
                    ChoiceQuestion.fixAnswerList(
                        it.subList(1, it.size)
                    )
                )
            )
        }
        questionNum = 0
        yesOrNoList.windowed(2, 2) {
            questionNum++
            questionList.add(ChoiceQuestion(ChoiceQuestion.fixQuestionTitle(questionNum, it.first())))
        }
        return questionList
    }

    @Deprecated("不好用")
    fun readDocFile_(assets: AssetManager): List<String> {
        return assets.open("xidian_doc_1.doc").use {
            val list = mutableListOf<String>()
            val hwpfDocument = HWPFDocument(it)
            val numParagraphs = hwpfDocument.range.numParagraphs()
            for (i in 0 until numParagraphs) {
                val paragraph = hwpfDocument.range.getParagraph(i)
                if (paragraph.ilfo == 1) {
                    list.add("${i + 1}、${paragraph.text()}")
                }
            }
            list
        }
    }
}