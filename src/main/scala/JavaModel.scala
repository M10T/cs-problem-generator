import scala.util.Random

object JavaString extends Type[String]("String"){
    private val randomGenerator: Random = new Random()
    def randomGenerate(): String = {
        val length : Int = randomGenerator.nextInt(10) + 1;
        return randomGenerator.alphanumeric.take(length).mkString
    }
    def displayInstance(obj: String): String = '"'+obj+'"'
}

object JavaInt extends Type[Int]("int"){
    private val randomGenerator: Random = new Random()
    def randomGenerate(): Int = randomGenerator.nextInt(100)
    def displayInstance(obj: Int): String = obj.toString()
}

object JavaTranslator extends ModelTranslator {
    def translateModel(model: CodeModel): String = model match
        case CodeBlock(sections) => sections.map(translateModel).reduce((a,b)=>a+"\n"+b)
        case Repetition(variableName, times, section) 
            => f"for(int $variableName = 0; $variableName < $times; $variableName++) {\n" 
                + translateModel(section).split("\n").map(x=>"\t"+x).mkString("\n") + "\n}"
        case VariableCreation(variableType, variableName, ref) 
            => variableType.name + " " + variableName + " = " + translateModel(ref) + ";"
        case VariableAssignment(variableName, ref)
            => variableName + " = " + translateModel(ref) + ";"
        case Literal(variableType, variableValue) => variableType.displayInstance(variableValue)
        case Variable(variableName, variableType) => variableName
        case Display(ref) => f"System.out.println(${translateModel(ref)});"
}