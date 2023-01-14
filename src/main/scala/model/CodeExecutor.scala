package model;

import scala.jdk.CollectionConverters
import scala.collection.mutable.Map
import scala.collection.mutable.ListBuffer

class CodeContext(private val outerScope : Option[CodeContext] = None) {
    private val data: Map[String,Any] = Map()
    private val typeData: Map[String, Any] = Map()
    private val displayedObjects: ListBuffer[String] = ListBuffer()

    def createVariable[T](name: String, value: T, varType: Type[T]) = {
        if (outerScope.map(outer=>outer.data.keySet.contains(name)).getOrElse(false))
            throw IllegalArgumentException("Variable exists in outer scope!")
        if (data.keySet.contains(name)) 
            throw IllegalArgumentException("Variable already exists!")
        data.put(name, value)
        typeData.put(name, varType)
    }

    def setVariable[T](name: String, value: T, varType: Type[T]) : Unit = {
        if (outerScope.map(outer=>outer.data.keySet.contains(name)).getOrElse(false))
            return outerScope.get.setVariable(name, value, varType)
        if (!data.keySet.contains(name))
            throw IllegalArgumentException("Variable does not exist!")
        if(typeData.get(name).get != varType) {
            throw IllegalArgumentException("Variable type does not match!")
        }
        data.put(name, value)
    }

    def setWeakVariable[T](name: String, value: T, varType: Type[T]) = {
        data.put(name, value)
        typeData.put(name, varType)
    }

    def hasVariable[T](name: String, varType: Type[T]) : Boolean = {
        if (data.contains(name) && typeData.get(name).get == varType) return true
        else return outerScope.map(outer=>outer.hasVariable(name, varType)).getOrElse(false)
    }
    
    def getVariable[T](name: String, varType: Type[T]) : T = {
        if (!hasVariable(name, varType)) {
            throw IllegalArgumentException(s"Not correct type $varType for variable $name!")
        }
    
        if (data.contains(name) && typeData.get(name).get == varType)
            return data.get(name).get.asInstanceOf[T]
        else {
            return outerScope.get.getVariable(name, varType)
        }
    }

    def addDisplayed(obj: String) : Unit = {
        if (outerScope.isDefined) outerScope.get.addDisplayed(obj)
        else displayedObjects.append(obj)
    }

    def getData() = data.clone()
    def getDisplayed : List[String] = 
        if(!outerScope.isDefined)
            displayedObjects.toList 
        else outerScope.get.getDisplayed
}

object CodeExecutor {

    def executeModel(model: CodeModel) : CodeContext = {
        try { 
            return executeModel(model, CodeContext())
        } catch {
            case e: IllegalArgumentException => {
                println(JavaTranslator.translateModel(model))
                return CodeContext()
            }
        }
    }

    def getValue[T](context: CodeContext, ref: Reference[T]) : T = ref match
        case Literal(variableType, variableValue) => variableValue
        case Variable(variableName, variableType) => context.getVariable(variableName, variableType)
        case op: Addition[T] => op.num.plus(getValue(context, op.ref1), getValue(context, op.ref2))
        case op: Subtraction[T] => op.num.minus(getValue(context, op.ref1), getValue(context, op.ref2))
        case op: Multiplication[T] => op.num.times(getValue(context, op.ref1), getValue(context, op.ref2))
        case FunctionReference(FunctionApplication(fModel, arg), returnType) => fModel match
            case ObjectMethod(obj, name, method) => method(getValue(context, obj), getValue(context, arg))
            case ScopedMethod(name, method) => method(getValue(context,arg))
            case FunctionBuilder(name, argVar, body, returnRef) => {
                if (argVar.getType != arg.getType) throw IllegalArgumentException("Illegal argument type!")
                val newContext = new CodeContext()
                newContext.setVariable(argVar.variableName, getValue(context, arg), arg.getType)
                return getValue(executeModel(body, newContext), returnRef)
            }

    def executeModel(model: CodeModel, context: CodeContext) : CodeContext = {
        model match
            case CodeBlock(sections) => sections.foreach((section)=>executeModel(section, context))
            case Repetition(variableName, varType, times, section) => {
                val forContext = CodeContext(Some(context))
                var i = 0;
                forContext.createVariable(variableName, 0, varType)
                while (i < times) {
                    val innerContext = CodeContext(Some(forContext))
                    executeModel(section, innerContext)
                    i = forContext.getVariable(variableName, varType)
                    i += 1
                    forContext.setVariable(variableName, i, varType)
                }
            }
            case VariableCreation(variableType, variableName, ref)
                => context.createVariable(variableName, getValue(context, ref), ref.getType)
            case VariableAssignment(variableName, ref) 
                => context.setVariable(variableName, getValue(context, ref), ref.getType)
            case Display(ref) => context.addDisplayed(ref.getType.displayInstance(getValue(context, ref)))
            case _ => {}
        return context
    }
}
