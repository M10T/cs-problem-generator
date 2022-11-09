import scala.jdk.CollectionConverters
import scala.collection.mutable.Map

class CodeContext(private val outerScope : Option[CodeContext] = None) {
    private val data: Map[String,Any] = Map()
    private val typeData: Map[String, Any] = Map()

    def createVariable[T](name: String, value: T, varType: Type[T]) = {
        if (outerScope.map(outer=>outer.data.keySet.contains(name)).getOrElse(false))
            throw IllegalArgumentException("Variable exists in outer scope!")
        if (data.keySet.contains(name)) 
            throw IllegalArgumentException("Variable already exists!")
        data.put(name, value)
        typeData.put(name, typeData)
    }

    def setVariable[T](name: String, value: T, varType: Type[T]) : Unit = {
        if (outerScope.map(outer=>outer.data.keySet.contains(name)).getOrElse(false))
            return outerScope.get.setVariable(name, value, varType)
        if (!data.keySet.contains(name))
            throw IllegalArgumentException("Variable does not exist!")
        if(typeData.get(name) != varType) 
            throw IllegalArgumentException("Variable type does not match!")
        data.put(name, value)
    }

    def setWeakVariable[T](name: String, value: T, varType: Type[T]) = {
        data.put(name, value)
        typeData.put(name, varType)
    }
    
    def getVariable[T](name: String, varType: Type[T]) : T = {
        if (outerScope.map(outer=>outer.data.keySet.contains(name)).getOrElse(false))
            return outerScope.get.getVariable(name, varType)
        else if data.contains(name) && typeData.get(name).get == varType
            then return data.get(name).get.asInstanceOf[T]
        else throw IllegalArgumentException("Not correct type!")
    }

    def getData() = data.clone()
}

object CodeExecutor {

    def executeModel(model: CodeModel) : CodeContext = executeModel(model, CodeContext())

    def executeModel(model: CodeModel, context: CodeContext) : CodeContext = {
        model match
            case CodeBlock(sections) => sections.foreach((section)=>executeModel(section, context))
            case Repetition(variableName, times, section) => {
                val forContext = CodeContext(Some(context))
                var i = 0;
                while (i < times) {
                    val innerContext = CodeContext(Some(forContext))
                    executeModel(section, innerContext)
                }
            }
            case VariableCreation(variableType, variableName, Literal(varType, value)) if varType==variableType
                => context.createVariable(variableName, value, varType)
            case VariableCreation(variableType, variableName, Variable(otherName, otherType)) if variableType == otherType
                => context.createVariable(variableName, context.getVariable(otherName, variableType), variableType)
            case VariableAssignment(variableName, Literal(varType, value)) 
                => context.setVariable(variableName, value, varType)
            case VariableAssignment(variableName, Variable(otherName, otherType))
                => context.setVariable(variableName, context.getVariable(otherName, otherType), otherType) 
            case _ => {}
        return context
    }
}
