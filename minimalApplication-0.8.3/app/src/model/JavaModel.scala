package model;
import scala.util.Random
import scala.collection.mutable.Map

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
    private val randGenerator = new Random()
    def translateModel(model: CodeModel): String = model match
        case CodeBlock(sections) => sections.map(translateModel).reduce((a,b)=>a+"\n"+b)
        case Repetition(variableName, varType, times, section) 
            => f"for(int $variableName = 0; $variableName < $times; $variableName++) {\n" 
                + translateModel(section).split("\n").map(x=>"\t"+x).mkString("\n") + "\n}"
        case VariableCreation(variableType, variableName, ref) 
            => variableType.name + " " + variableName + " = " + translateModel(ref) + ";"
        case VariableAssignment(variableName, ref)
            => variableName + " = " + translateModel(ref) + ";"
        case Literal(variableType, variableValue) => variableType.displayInstance(variableValue)
        case Variable(variableName, variableType) => variableName
        case Display(ref) => f"System.out.println(${translateModel(ref)});"

    def randomType(vars: Map[String, Any]) : Reference = {
        val literal = randGenerator.nextBoolean()
        if(vars.isEmpty || literal) {
            val types = Array(JavaString, JavaInt);
            val t = types(randGenerator.nextInt(types.length))
            return Literal(t, t.randomGenerate())
        } else {
            val keyList = vars.keySet.toList
            val name = keyList(randGenerator.nextInt(keyList.size))
            return new Variable(name, vars.get(name).get.asInstanceOf[Type[?]])
        }
    }

    def randomGenerateHelper(vars: Map[String, Any], blockAllowed: Boolean = true) : CodeModel = {
        val code = if blockAllowed then randGenerator.nextInt(3) else randGenerator.nextInt(2)
        return code match
            case 0 => Display(randomType(vars))
            case 1 => {
                val varName = "var" + vars.keySet.size
                val varRef = randomType(vars)
                vars.put(varName, varRef.getType)
                return VariableCreation(varRef.getType, varName, varRef)
            }
            case 2 => {
                val blockLength = randGenerator.between(1,10)
                return CodeBlock((0 to blockLength map {(_:Int)=>randomGenerateHelper(vars,blockAllowed=false)}).toList)
            }
    }

    def randomGenerate() : CodeModel = randomGenerateHelper(Map())
}