package org.jetbrains.plugins.scala.lang.psi.impl.expr

import org.jetbrains.plugins.scala.extensions._
import org.jetbrains.plugins.scala.lang.parser.ScalaElementType
import org.jetbrains.plugins.scala.lang.psi.api.base.patterns.ScCaseClauses
import org.jetbrains.plugins.scala.lang.psi.api.expr._

trait ScEnumeratorImpl extends ScEnumerator {

  override def forStatement: Option[ScForStatementImpl] = this.parentOfType(classOf[ScForStatementImpl])

  override def desugared: Option[ScEnumerator.DesugaredEnumerator] = forStatement flatMap {
    _.desugareEnumerator(this)
  }
}

object ScEnumeratorImpl {

  class DesugaredEnumeratorImpl(override val analogMethodCall: ScMethodCall) extends ScEnumerator.DesugaredEnumerator {
    override def callExpr: Option[ScReferenceExpression] =
      Option(analogMethodCall.getInvokedExpr).collect { case refExpr: ScReferenceExpression => refExpr }

    override def content: Option[ScExpression] = {
      analogMethodCall
        .getLastChild
        .getLastChild
        .asInstanceOf[ScBlockExpr]
        .findLastChildByType[ScCaseClauses](ScalaElementType.CASE_CLAUSES)
        .getLastChild
        .lastChild collect { case block: ScBlock => block}
    }
  }
}