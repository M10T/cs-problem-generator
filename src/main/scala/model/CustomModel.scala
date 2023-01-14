package model
import scala.util.Random
import scala.collection.mutable.Map

object NativeString extends Type[String]("str") {
    private val randomGenerator: Random = new Random()
    def randomGenerate(): String = {
        val length : Int = randomGenerator.nextInt(10) + 1;
        return randomGenerator.alphanumeric.take(length).mkString
    }
    def displayInstance(obj: String): String = obj
    def codeInstance(obj: String) = "\"" + obj + "\""
}

object NativeInteger extends Type[Int]("int") {
    private val randomGenerator: Random = new Random()
    def randomGenerate(): Int = randomGenerator.nextInt(100)
    def displayInstance(obj: Int): String = obj.toString()
    def codeInstance(obj: Int) = displayInstance(obj)
}

object NativeTranslator extends ModelTranslator {
    private val randGenerator = new Random()
    def translateModel(model: CodeModel): String = model match
        case CodeBlock(sections) => sections.map(translateModel).reduce((a,b)=>a+"\n"+b)
        case VariableCreation(variableType, variableName, ref) 
            => s"def $variableName ${variableType.name} ${translateModel(ref)}"
        case VariableAssignment(variableName, ref)
            => s"set $variableName ${translateModel(ref)}"
        case Literal(variableType, variableValue) => variableType.codeInstance(variableValue)
        case Variable(variableName, variableType) => variableName
        case Display(ref) => s"disp ${translateModel(ref)}"
        case _ => ""

    def randomType(vars: Map[String, Any]) : Reference[?] = {
        val literal = randGenerator.nextBoolean()
        if(vars.isEmpty || literal) {
            val types = Array(NativeString, NativeInteger);
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
                newVars.put(varName, NativeInteger)
                return Repetition(varName, NativeInteger, times, randomGenerateHelper(newVars))
            }
            case 3 => {
                val blockLength = randGenerator.between(1,10)
                return CodeBlock((0 to blockLength map {(_:Int)=>randomGenerateHelper(vars,codeBound=3)}).toList)
            }
    }

    def randomGenerate() : CodeModel = randomGenerateHelper(Map())
}
