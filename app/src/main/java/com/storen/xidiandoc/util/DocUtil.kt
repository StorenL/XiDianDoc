package com.storen.xidiandoc.util

import android.content.res.AssetManager
import android.text.TextUtils
import android.util.Log
import com.storen.xidiandoc.bean.ChoiceQuestion
import org.apache.poi.hwpf.HWPFDocument
import org.apache.poi.hwpf.extractor.WordExtractor
import java.io.InputStreamReader

object DocUtil {

    private const val CHOICE_ANSWER_COUNT = 4
    private const val YES_OR_NO_ANSWER_COUNT = 1

    fun readDocFile(assets: AssetManager): List<String> {
        return assets.open("xidian_doc_1.doc").use {
            val wordExtractor = WordExtractor(it)
            wordExtractor.paragraphText.toList()
        }
    }

    fun readTxtFile(assets: AssetManager): List<String> {
        return assets.open("xidian.txt").use {
            InputStreamReader(it).use(InputStreamReader::readLines)
        }
    }

    fun parseDoc1(list: List<String>): List<ChoiceQuestion> {
        val questionList: MutableList<ChoiceQuestion> = mutableListOf()
        val filterList = list.filter { !TextUtils.isEmpty(it) }
        val indexOfChoice1 = filterList.indexOfFirst { it.contains("选择题") }
        val indexOfYesOrNo1 = filterList.indexOfFirst { it.contains("判断题") }
        val indexOfChoice2 = filterList.indexOfFirst { it.contains("一、单选题(第1题～第80题。每题1.0分，满分80.0分。)") }
        val indexOfYesOrNo2 = filterList.indexOfFirst { it.contains("二、判断题(第81题～第120题。每题0.5分，满分20.0分。)") }
        val choiceList1 = filterList.subList(indexOfChoice1 + 1, indexOfYesOrNo1)
        val yesOrNoList1 = filterList.subList(indexOfYesOrNo1 + 1, indexOfChoice2)
        val choiceList2 = filterList.subList(indexOfChoice2 + 1, indexOfYesOrNo2)
        val yesOrNoList2 = filterList.subList(indexOfYesOrNo2 + 1, filterList.size)
        var questionNum = 0
        choiceList1.windowed(5, 5) {
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
        yesOrNoList1.windowed(2, 2) {
            questionNum++
            questionList.add(ChoiceQuestion(ChoiceQuestion.fixQuestionTitle(questionNum, it.first())))
        }
        questionNum = 0
        choiceList2.windowed(6, 6) {
            questionNum++
            questionList.add(
                ChoiceQuestion(
                    it.first(),
                    it.subList(1, it.size - 1).toList(),
                    ChoiceQuestion.findAnswer2(it.last())
                )
            )
        }
        yesOrNoList2.windowed(2, 2) {
            questionNum++
            questionList.add(
                ChoiceQuestion(
                    it.first(),
                    it.subList(1, it.size).toList(),
                    listOf()
                )
            )
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