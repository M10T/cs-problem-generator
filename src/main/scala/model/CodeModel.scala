package model;

sealed trait CodeModel

sealed trait Reference[T] extends CodeModel {
    def getType : Type[T] = this match
        case Literal(t, v) => t
        case Variable(n, t) => t
        case r: MathResult[?] => r.resultType
        case FunctionReference(f, returnType) => returnType
}

case class Literal[T](variableType: Type[T], variableValue: T) extends Reference[T]
case class Variable[T](val variableName: String, variableType: Type[T]) extends Reference[T]
case class FunctionReference[T](f: FunctionApplication[?, T], val returnType: Type[T]) extends Reference[T]

case class Repetition(variableName: String, variableType: Type[Int], times: Int, section: CodeModel) extends CodeModel
case class VariableCreation[T](variableType: Type[T], variableName: String, ref: Reference[T]) extends CodeModel
case class VariableAssignment[T](variableName: String, ref: Reference[T]) extends CodeModel
case class CodeBlock(sections: List[CodeModel]) extends CodeModel
case class Display(reference: Reference[?]) extends CodeModel

sealed trait MathResult[T](val resultType: Type[T])(implicit val num: Numeric[T]) extends Reference[T]
case class Addition[T](ref1: Reference[T], ref2: Reference[T])(implicit num: Numeric[T])  extends MathResult[T](ref1.getType) {
    if (ref1.getType != ref2.getType) throw IllegalArgumentException("Different types of numbers!")
}
case class Subtraction[T](ref1: Reference[T], ref2: Reference[T])(implicit num: Numeric[T]) extends MathResult[T](ref1.getType) {
    if (ref1.getType != ref2.getType) throw IllegalArgumentException("Different types of numbers!")
}
case class Multiplication[T](ref1: Reference[T], ref2: Reference[T])(implicit num: Numeric[T])  extends MathResult[T](ref1.getType) {
    if (ref1.getType != ref2.getType) throw IllegalArgumentException("Different types of numbers!")
}

sealed trait FunctionModel[T, U]
case class ObjectMethod[A, B, C]
    (val obj: Reference[A], val name: String, val method: ((A,B)=>C)) 
        extends FunctionModel[B, C]
case class ScopedMethod[T, U](val name: String, val method: T=>U) extends FunctionModel[T, U]
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