package model;

sealed trait CodeModel

sealed trait Reference extends CodeModel {
    def getType : Type[?] = this match
        case Literal(t, v) => t
        case Variable(n, t) => t
}
case class Literal[T](variableType: Type[T], variableValue: T) extends Reference
case class Variable[T](variableName: String, variableType: Type[T]) extends Reference

case class Repetition(variableName: String, variableType: Type[Int], times: Int, section: CodeModel) extends CodeModel
case class VariableCreation[T](variableType: Type[T], variableName: String, ref: Reference) extends CodeModel
case class VariableAssignment[T](variableName: String, ref: Reference) extends CodeModel
case class CodeBlock(sections: List[CodeModel]) extends CodeModel
case class Display(reference: Reference) extends CodeModel

trait Type[T](val name: String) {
    def displayInstance(obj: T): String
    def randomGenerate() : T
}

trait ModelTranslator {
    def translateModel(model:CodeModel): String
    def randomGenerate(): CodeModel
}