package model

import scala.util.Random
import scala.collection.mutable.Map

object MathGenerator {
    private val randGenerator = Random()
    def randomType(vars: Map[String, Any], recurses: Int = 0) : Reference[Int] = {
        val option = randGenerator.nextInt(5)
        if(vars.isEmpty || option == 0 || (recurses > 2 && option < 3)) {
            val types = Array(JavaInt);
            val t = types(randGenerator.nextInt(types.length))
            return Literal(t, t.randomGenerate())
        } else if (option == 1 || recurses > 2) {
            val keyList = vars.keySet.toList
            val name = keyList(randGenerator.nextInt(keyList.size))
            return new Variable(name, vars.get(name).get.asInstanceOf[Type[Int]])
        } else return option match
            case 2 => Addition(randomType(vars, recurses+1), randomType(vars,recurses+1))
            case 3 => Subtraction(randomType(vars,recurses+1), randomType(vars,recurses+1))
            case 4 => Multiplication(randomType(vars,recurses+1), randomType(vars,recurses+1))
        
    }
    def randomGenerateHelper(vars: Map[String, Any]) : CodeModel = {
        val lines = randGenerator.between(1, 11)
        val lineArray = Array.ofDim[CodeModel](lines)
        for(i <- 0 to lines-1) {
            val code = randGenerator.nextInt(3)
            lineArray(i) = code match
                case 0 => Display(randomType(vars))
                case 1 => {
                    val varName = "var" + vars.keySet.size
                    val varRef = randomType(vars)
                    vars.put(varName, varRef.getType)
                    VariableCreation(varRef.getType, varName, varRef)
                }
                case 2 => {
                    val keyList = vars.keySet.toList
                    if (keyList.size > 0) {
                        val name = keyList(randGenerator.nextInt(keyList.size))
                        VariableAssignment(name, randomType(vars))
                    } else {
                        val varName = "var" + vars.keySet.size
                        val varRef = randomType(vars)
                        vars.put(varName, varRef.getType)
                        VariableCreation(varRef.getType, varName, varRef)
                    }
                }
        }
        return CodeBlock(lineArray.toList)
    }

    def randomGenerate() : CodeModel = randomGenerateHelper(Map())
}
