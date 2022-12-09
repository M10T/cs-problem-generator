package model;
import scala.util.Random
import scala.collection.mutable.Map

object JavaString extends Type[String]("String"){
    private val randomGenerator: Random = new Random()
    def randomGenerate(): String = {
        val length : Int = randomGenerator.nextInt(10) + 1;
        return randomGenerator.alphanumeric.take(length).mkString
    }
    def displayInstance(obj: String): String = obj
    def codeInstance(obj: String): String = "\"" + obj + "\""
}

object JavaInt extends Type[Int]("int"){
    private val randomGenerator: Random = new Random()
    def randomGenerate(): Int = randomGenerator.nextInt(100)
    def displayInstance(obj: Int): String = obj.toString()
    def codeInstance(obj: Int) = displayInstance(obj)
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
        case Literal(variableType, variableValue) => variableType.codeInstance(variableValue)
        case Variable(variableName, variableType) => variableName
        case Addition(r1, r2) => s"(${translateModel(r1)} + ${translateModel(r2)})"
        case Subtraction(r1, r2) => s"(${translateModel(r1)} - ${translateModel(r2)})"
        case Multiplication(r1, r2) => s"(${translateModel(r1)} * ${translateModel(r2)})"
        case Display(ref) => f"System.out.println(${translateModel(ref)});"

    def randomType(vars: Map[String, Any]) : Reference[?] = {
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

    def randomGenerateHelper(vars: Map[String, Any], codeBound : Int = 4) : CodeModel = {
        val code = randGenerator.nextInt(codeBound)
        return code match
            case 0 => Display(randomType(vars))
            case 1 => {
                val varName = "var" + vars.keySet.size
                val varRef = randomType(vars)
                vars.put(varName, varRef.getType)
                return VariableCreation(varRef.getType, varName, varRef)
            }
            case 2 => {
                val newVars = vars.clone()
                val times : Int = randGenerator.between(2,6)
                val varName = "i" + vars.keySet.count(name=>name.startsWith("i"))
                newVars.put(varName, JavaInt)
                return Repetition(varName, JavaInt, times, randomGenerateHelper(newVars))
            }
            case 3 => {
                val blockLength = randGenerator.between(1,10)
                return CodeBlock((0 to blockLength map {(_:Int)=>randomGenerateHelper(vars,codeBound=3)}).toList)
            }
    }

    def randomGenerate() : CodeModel = randomGenerateHelper(Map())
}