package model

import scala.util.Random

object StringGenerator {
    private val randGenerator = Random()
    private val functions = List(
        (JavaString.equalsProvider, List(
            (x:String)=>JavaString.randomGenerate(),
            x=>x+JavaString.randomGenerate(),
            x=>x.dropRight(randGenerator.nextInt(x.length/3)),
            x=>x,
            x=>x.map(c=>if randGenerator.nextBoolean() then c.toUpper else c.toLower)
        )), 
        (JavaString.equalsIgnoreCaseProvider,List(
            (x:String)=>JavaString.randomGenerate(),
            x=>x+JavaString.randomGenerate(),
            x=>x.dropRight(randGenerator.nextInt(x.length/3)),
            x=>x,
            x=>x.map(c=>if randGenerator.nextBoolean() then c.toUpper else c.toLower)
        )),
        (JavaString.startsWithProvider, List(
            (x:String)=>JavaString.randomGenerate(),
            x=>x+JavaString.randomGenerate(),
            x=>x.dropRight(randGenerator.nextInt(x.length/3)),
            x=>x,
            x=>x.map(c=>if randGenerator.nextBoolean() then c.toUpper else c.toLower)
        ))
    )
    
    def randomGenerate() : CodeModel = {
        val index = randGenerator.nextInt(functions.length)
        val usedFunction = functions(index)(0)
        val generatorList = functions(index)(1)
        val generator = generatorList(randGenerator.nextInt(generatorList.length))
        val s1 = JavaString.randomGenerate()
        val assignment1 = VariableCreation(JavaString, "str1", Literal(JavaString, s1))
        val assignment2 = VariableCreation(JavaString, "str2", Literal(JavaString, generator(s1)))
        val result = VariableCreation(JavaBoolean, "result", FunctionReference(
            FunctionApplication(usedFunction(Variable("str1", JavaString)), Variable("str2", JavaString)), JavaBoolean
        ))
        CodeBlock(List(
            assignment1,
            assignment2,
            result,
            Display(Variable("result", JavaBoolean))
        ))
    }
}
