/*
 * Copyright 2010-2017 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jetbrains.kotlin.idea.refactoring.inline

import com.intellij.openapi.project.Project
import com.intellij.openapi.util.text.StringUtil
import com.intellij.refactoring.inline.InlineOptionsDialog
import org.jetbrains.kotlin.idea.references.KtSimpleNameReference
import org.jetbrains.kotlin.psi.KtCallableDeclaration

abstract class AbstractKotlinInlineDialog(
        protected val callable: KtCallableDeclaration,
        protected val reference: KtSimpleNameReference?,
        project: Project = callable.project
) : InlineOptionsDialog(project, true, callable)  {

    protected val occurrencesNumber = initOccurrencesNumber(callable)

    private val occurrencesString get() = if (occurrencesNumber >= 0) {
        " (" + occurrencesNumber + " occurrence" + (if (occurrencesNumber == 1) ")" else "s)")
    } else ""

    protected abstract val kind: String

    private val refactoringName get() = "Inline ${StringUtil.capitalizeWords(kind, true)}"

    init {
        myInvokedOnReference = reference != null
        title = refactoringName
        init()
    }

    override fun allowInlineAll() = occurrencesNumber > 1 || !myInvokedOnReference

    override fun getBorderTitle() = refactoringName

    override fun getNameLabelText(): String {
        val occurrencesString =
                if (occurrencesNumber > -1) " - $occurrencesNumber occurrence${if (occurrencesNumber == 1) "" else "s"}"
                else ""
        return "${kind.capitalize()} ${callable.nameAsSafeName} $occurrencesString"
    }

    override fun getInlineAllText() =
            "Inline all references and remove the $kind" + occurrencesString

    override fun getKeepTheDeclarationText(): String? =
            if (callable.isWritable) "Inline all references and keep the $kind" + occurrencesString
            else super.getKeepTheDeclarationText()

    override fun getInlineThisText() = "Inline this occurrence and leave the $kind"

    override fun canInlineThisOnly() = myInvokedOnReference
}