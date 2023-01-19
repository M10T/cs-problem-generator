package model;
import scala.util.Random
import scala.collection.mutable.Map
import scala.reflect.ClassTag

object JavaString extends Type[String]("String"){
    private val randomGenerator: Random = new Random()
    def randomGenerate(): String = {
        val length : Int = randomGenerator.nextInt(10) + 1;
        return randomGenerator.alphanumeric.take(length).mkString
    }
    def displayInstance(obj: String): String = obj
    def codeInstance(obj: String): String = "\"" + obj + "\""

    def startsWithProvider(ref: Reference[String]) : ObjectMethod[String, String, Boolean]
        = ObjectMethod(ref, "startsWith", (s1,s2)=>s1.startsWith(s2))
    def charAtProvider(ref: Reference[String]) : ObjectMethod[String, Int, Char]
        = ObjectMethod(ref, "charAt", (s, index) => s.charAt(index))
    def equalsProvider(ref: Reference[String]) : ObjectMethod[String, String, Boolean]
        = ObjectMethod(ref, "equals", (s1,s2)=>s1.equals(s2))
    def equalsIgnoreCaseProvider(ref: Reference[String]) : ObjectMethod[String, String, Boolean]
        = ObjectMethod(ref, "equalsIgnoreCase", (s1,s2)=>s1.equalsIgnoreCase(s2))
}

object JavaInt extends Type[Int]("int"){
    private val randomGenerator: Random = new Random()
    def randomGenerate(): Int = randomGenerator.nextInt(100)
    def displayInstance(obj: Int): String = obj.toString()
    def codeInstance(obj: Int) = displayInstance(obj)
}

object JavaBoolean extends Type[Boolean]("boolean") {
    private val randomGenerator : Random = new Random()
    def randomGenerate() : Boolean = randomGenerator.nextBoolean()
    def displayInstance(obj: Boolean) = obj.toString()
    def codeInstance(obj: Boolean) = displayInstance(obj)
}

object JavaChar extends Type[Char]("char") {
    private val randomGenerator : Random = new Random()
    def randomGenerate(): Char = randomGenerator.alphanumeric.head
    def displayInstance(obj: Char): String = obj.toString()
    def codeInstance(obj: Char): String = f"'$obj'"
}

class JavaArray[T](val valueType: Type[T]) extends Type[List[T]](valueType.name+"[]") {
    private val randomGenerator : Random = new Random()

    def randomGenerate() = {
        val len : Int = randomGenerator.between(1,6)
        (1 to len).map(x=>valueType.randomGenerate()).toList
    }
    def codeInstance(obj: List[T]): String = f"new ${valueType.name}[]{${obj.map(valueType.codeInstance).mkString(",")}}"
    def displayInstance(obj: List[T]): String = f"$displayedClassName@${Integer.toHexString(obj.hashCode())}"

    private def displayedClassName : String = "[" + (valueType match {
        case JavaInt => "I"
        case JavaString => "java.lang.String"
        case JavaBoolean => "Z"
        case JavaChar => "C"
        case (obj: JavaArray[?]) => obj.displayedClassName
    })
}

object JavaTranslator extends ModelTranslator {
    private val randGenerator = new Random()
    def translateModel(model: CodeModel): String = model match
        case CodeBlock(sections) if sections.isEmpty => ""
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
        case FunctionApplication(ObjectMethod(obj, name, method), arg) =>
            f"${translateModel(obj)}.$name(${translateModel(arg)});"
        case FunctionApplication(ScopedMethod(name, method), arg) => f"$name(${translateModel(arg)});"
        case FunctionApplication(FunctionBuilder(name, argRef, body, returnRef), arg) => f"$name(${translateModel(arg)});"
        case FunctionApplication(Constructor(objType, method), arg) => f"new ${objType.name}(${translateModel(arg)});"
        case FunctionReference(fModel, _) => translateModel(fModel).dropRight(1)
        case Conditional(condition, body, elseBody) => f"if(${translateModel(condition)}){\n"
            + translateModel(body).split("\n").map(x=>"\t"+x).mkString("\n") + "\n}" + 
            (if elseBody.isDefined then "\nelse {\n" + translateModel(elseBody.get).split("\n").map(x=>"\t"+x).mkString("\n") + "\n}" else "")
        case NegationResult(ref) => f"!(${translateModel(ref)})"
        case EqualsResult(ref1, ref2, _) => f"(${translateModel(ref1)}) == (${translateModel(ref2)})"
        case AndResult(ref1, ref2) => f"(${translateModel(ref1)}) && (${translateModel(ref2)})"
        case OrResult(ref1, ref2) => f"(${translateModel(ref1)}) || (${translateModel(ref2)})"

    def translateFunction(model: FunctionBuilder[?, ?]) : String = {
        val returnType : Type[?] = model.returnRef.getType
        val argType : Type[?] = model.argRef.variableType
        var ret : String = f"${returnType.name} ${model.name}(${argType.name} ${model.argRef.variableName}) {\n"
        ret += translateModel(model.body).split("\n").map(x=>"\t"+x).mkString
        ret += f"return ${translateModel(model.returnRef)};\n"
        ret += "}"
        return ret
    }

    def randomType(vars: Map[String, Any]) : Reference[?] = {
        val literal = randGenerator.nextBoolean()
        if(vars.isEmpty || literal) {
            val types = Array(JavaString, JavaInt, JavaBoolean);
            val t = types(randGenerator.nextInt(types.length))
            return Literal(t, t.randomGenerate())
        } else {
            val keyList = vars.keySet.toList
            val name = keyList(randGenerator.nextInt(keyList.size))
            return new Variable(name, vars.get(name).get.asInstanceOf[Type[?]])
        }
    }

    def randomCondition(vars: Map[String, Any], codeBound : Int = 4) : Reference[Boolean] = {
        val boolVars : List[String] = vars.keySet.toList.filter(s=>vars.getOrElse(s,None) == JavaBoolean)
        if (boolVars.isEmpty) {
            return Literal(JavaBoolean, randGenerator.nextBoolean)
        }
        val code : Int = randGenerator.nextInt(codeBound)
        code match
            case 0 => new Variable(boolVars(randGenerator.nextInt(boolVars.size)), JavaBoolean)
            case 1 => new NegationResult(new Variable(boolVars(randGenerator.nextInt(boolVars.size)), JavaBoolean))
            case 2 => new AndResult(randomCondition(vars, 2), randomCondition(vars, 2))
            case 3 => new OrResult(randomCondition(vars, 2), randomCondition(vars, 2))
    }

    def randomGenerateHelper(vars: Map[String, Any], codeBound : Int = 5) : CodeModel = {
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
                return Repetition(varName, JavaInt, times, randomGenerateHelper(newVars, 2))
            }
            case 3 => {
                val newVars = vars.clone()
                return Conditional(randomCondition(newVars), randomGenerateHelper(newVars, 3), None)
            }
            case 4 => {
                val blockLength = randGenerator.between(1,10)
                return CodeBlock((0 to blockLength map {(_:Int)=>randomGenerateHelper(vars,codeBound=4)}).toList)
            }
    }

    def randomGenerate() : CodeModel = randomGenerateHelper(Map())
}