sealed trait CodeModel

sealed trait Reference extends CodeModel
case class Literal[T](variableType: Type[T], variableValue: T) extends Reference
case class Variable[T](variableName: String, variableType: Type[T]) extends Reference

case class Repetition(variableName: String, times: Int, section: CodeModel) extends CodeModel
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
}