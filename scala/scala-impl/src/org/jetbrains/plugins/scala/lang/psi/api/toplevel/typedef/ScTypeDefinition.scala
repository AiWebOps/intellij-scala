package org.jetbrains.plugins.scala
package lang
package psi
package api
package toplevel
package typedef

import com.intellij.navigation.NavigationItem
import com.intellij.psi._
import com.intellij.psi.impl.PsiClassImplUtil
import org.jetbrains.plugins.scala.extensions.{PsiElementExt, TraversableExt}
import org.jetbrains.plugins.scala.lang.psi.adapters.PsiClassAdapter
import org.jetbrains.plugins.scala.lang.psi.api.statements._
import org.jetbrains.plugins.scala.lang.psi.api.statements.params.ScTypeParam
import org.jetbrains.plugins.scala.lang.psi.types.PhysicalMethodSignature

import scala.collection.Seq

/**
 * @author AlexanderPodkhalyuzin
 */
trait ScTypeDefinition extends ScTemplateDefinition
  with ScMember.WithBaseIconProvider
  with NavigationItem
  with PsiClassAdapter
  with ScTypeParametersOwner
  with ScDocCommentOwner
  with ScCommentOwner {

  //name of additional class generated by scalac
  def additionalClassJavaName: Option[String] = None

  def isCase: Boolean = false

  def isObject: Boolean = false

  def isTopLevel: Boolean = !this.parentsInFile.exists(_.isInstanceOf[ScTypeDefinition])

  def getPath: String = {
    val qualName = qualifiedName
    val index = qualName.lastIndexOf('.')
    if (index < 0) "" else qualName.substring(0, index)
  }

  def getQualifiedNameForDebugger: String

  def methodsByName(name: String): Iterator[PhysicalMethodSignature]

  def isPackageObject = false

  override protected def acceptScala(visitor: ScalaElementVisitor): Unit = {
    visitor.visitTypeDefinition(this)
  }

  def getSourceMirrorClass: PsiClass

  override def isEquivalentTo(another: PsiElement): Boolean = {
    PsiClassImplUtil.isClassEquivalentTo(this, another)
  }

  def allInnerTypeDefinitions: Seq[ScTypeDefinition] = this.membersWithSynthetic.filterByType[ScTypeDefinition]

  def typeParameters: Seq[ScTypeParam]

  def fakeCompanionModule: Option[ScObject]

  override def showAsInheritor: Boolean = true

  def baseCompanionModule: Option[ScTypeDefinition]
}

object ScTypeDefinition {

  implicit class ScTypeDefinitionExt(private val target: ScTypeDefinition) extends AnyVal {
    def shortDefinition: String =
      keywordPrefix + target.name

    def keywordPrefix: String =
      target match {
        case _: ScObject => "object "
        case _: ScTrait  => "trait "
        case _: ScClass  => "class "
        case _: ScEnum   => "enum "
        case _: ScGiven   => "given "
        case _           => ""
      }
  }
}