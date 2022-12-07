package model;

sealed trait CodeModel

sealed trait Reference[T] extends CodeModel {
    def getType : Type[T] = this match
        case Literal(t, v) => t
        case Variable(n, t) => t
        case r: MathResult[?] => r.resultType
}
case class Literal[T](variableType: Type[T], variableValue: T) extends Reference[T]
case class Variable[T](variableName: String, variableType: Type[T]) extends Reference[T]

case class Repetition(variableName: String, variableType: Type[Int], times: Int, section: CodeModel) extends CodeModel
case class VariableCreation[T](variableType: Type[T], variableName: String, ref: Reference[T]) extends CodeModel
case class VariableAssignment[T](variableName: String, ref: Reference[T]) extends CodeModel
case class CodeBlock(sections: List[CodeModel]) extends CodeModel
case class Display(reference: Reference[?]) extends CodeModel

sealed trait MathResult[T <: Number](val resultType: Type[T]) extends Reference[T]
case class Addition[T <: Number](ref1: Reference[T], ref2: Reference[T])  extends MathResult[T](ref1.getType) {
    if (ref1.getType != ref2.getType) throw IllegalArgumentException("Different types of numbers!")
}
case class Subtraction[T <: Number](ref1: Reference[T], ref2: Reference[T])  extends MathResult[T](ref1.getType) {
    if (ref1.getType != ref2.getType) throw IllegalArgumentException("Different types of numbers!")
}
case class Multiplication[T <: Number](ref1: Reference[T], ref2: Reference[T])  extends MathResult[T](ref1.getType) {
    if (ref1.getType != ref2.getType) throw IllegalArgumentException("Different types of numbers!")
}
case class Division[T <: Number](ref1: Reference[T], ref2: Reference[T])  extends MathResult[T](ref1.getType) {
    if (ref1.getType != ref2.getType) throw IllegalArgumentException("Different types of numbers!")
}

trait Type[T](val name: String) {
    def displayInstance(obj: T): String
    def codeInstance(obj: T) : String
    def randomGenerate() : T
}

trait ModelTranslator {
    def translateModel(model:CodeModel): String
    def randomGenerate(): CodeModel
}