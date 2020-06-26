package com.freezer.mathsolver

object LatexProcessor {
    suspend fun getCalculableLatexText(latexText: String): String {
        var calculableText = latexText

        if (!calculableText.contains("\\lceil")) {
            calculableText = "$calculableText\\lceil"
        }

        val pattern = """\^\{(.*?[\dxyz])\}.""".toRegex() // Pattern to remove redundant bracket of pow

        val matchingResults = pattern.findAll(calculableText).toList()

        if(matchingResults !=  null) {
            if(matchingResults.size == 1) {
                if(calculableText[matchingResults[0].range.last] != '{') {
                    // Check if equation which is inside brackets is valid (invalid if +-*/ not at the begin)
                    for(i in matchingResults[0].range.first+3..matchingResults[0].range.last) {
                        if(calculableText[i] == '+' || calculableText[i] == '-' || calculableText[i] == '*' || calculableText[i] == '/')
                            break
                    }
                    calculableText = calculableText.removeRange(matchingResults[0].range.last - 1, matchingResults[0].range.last)
                    calculableText = calculableText.removeRange(matchingResults[0].range.first + 1, matchingResults[0].range.first + 2)
                }
            }
            else {
                matchingResults.asReversed().forEach {
                    if(calculableText[it.range.last] != '{') {
                        // Check if equation which is inside brackets is valid (invalid if +-*/ not at the begin)
                        for(i in it.range.first+3..it.range.last) {
                            if(calculableText[i] == '+' || calculableText[i] == '-' || calculableText[i] == '*' || calculableText[i] == '/')
                                break
                        }
                        calculableText = calculableText.removeRange(it.range.last - 1, it.range.last)
                        calculableText = calculableText.removeRange(it.range.first + 1, it.range.first + 2)
                    }
                }
            }
        }
        return calculableText.replace("\\lceil", "")
    }
}