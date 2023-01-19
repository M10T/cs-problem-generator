package model;

sealed trait CodeModel

sealed trait Reference[T](val objType: Type[T]) extends CodeModel {
    def getType = objType
}

case class Literal[T](variableType: Type[T], variableValue: T) extends Reference[T](variableType)
case class Variable[T](val variableName: String, variableType: Type[T]) extends Reference[T](variableType)
case class FunctionReference[T](f: FunctionApplication[?, T], val returnType: Type[T]) extends Reference[T](returnType)

case class Repetition(variableName: String, variableType: Type[Int], times: Int, section: CodeModel) extends CodeModel
case class Conditional(condition: Reference[Boolean], body: CodeModel, elseBody: Option[CodeModel]) extends CodeModel
case class VariableCreation[T](variableType: Type[T], variableName: String, ref: Reference[T]) extends CodeModel
case class VariableAssignment[T](variableName: String, ref: Reference[T]) extends CodeModel
case class CodeBlock(sections: List[CodeModel]) extends CodeModel
case class Display(reference: Reference[?]) extends CodeModel

sealed abstract class MathResult[T](resultType: Type[T])(implicit val num: Numeric[T]) extends Reference[T](resultType)
case class Addition[T](ref1: Reference[T], ref2: Reference[T])(implicit num: Numeric[T])  extends MathResult[T](ref1.objType) {
    if (ref1.objType != ref2.objType) throw IllegalArgumentException("Different types of numbers!")
}
case class Subtraction[T](ref1: Reference[T], ref2: Reference[T])(implicit num: Numeric[T]) extends MathResult[T](ref1.objType) {
    if (ref1.objType != ref2.objType) throw IllegalArgumentException("Different types of numbers!")
}
case class Multiplication[T](ref1: Reference[T], ref2: Reference[T])(implicit num: Numeric[T])  extends MathResult[T](ref1.objType) {
    if (ref1.objType != ref2.objType) throw IllegalArgumentException("Different types of numbers!")
}

sealed abstract class BooleanResult(objType: Type[Boolean]) extends Reference[Boolean](objType)
case class NegationResult(ref: Reference[Boolean]) extends BooleanResult(ref.objType)
case class OrResult(ref1: Reference[Boolean], ref2: Reference[Boolean]) extends BooleanResult(ref1.objType) {
    if (ref1.objType != ref2.objType) throw IllegalArgumentException("Different types of booleans!")
}
case class AndResult(ref1: Reference[Boolean], ref2: Reference[Boolean]) extends BooleanResult(ref1.objType) {
    if (ref1.objType != ref2.objType) throw IllegalArgumentException("Different types of booleans!")
}
case class EqualsResult[T](ref1: Reference[T], ref2: Reference[T], returnType: Type[Boolean]) extends BooleanResult(returnType)

sealed trait FunctionModel[T, U]
case class ObjectMethod[A, B, C]
    (val obj: Reference[A], val name: String, val method: ((A,B)=>C)) 
        extends FunctionModel[B, C]
case class ScopedMethod[T, U](val name: String, val method: T=>U) extends FunctionModel[T, U]
case class Constructor[T,U](val objType: Type[U], val method: T=>U) extends FunctionModel[T, U]
case class FunctionBuilder[T, U]
    (val name: String, val argRef: Variable[T], val body: CodeModel, val returnRef: Reference[U]) extends FunctionModel[T, U]

case class FunctionApplication[T, U](val model: FunctionModel[T, U], val arg: Reference[T]) extends CodeModel

trait Type[T](val name: String) {
    def displayInstance(obj: T): String
    def codeInstance(obj: T) : String
    def randomGenerate() : T
}

trait ModelTranslator {
    def translateModel(model:CodeModel): String
    def randomGenerate(): CodeModel
}