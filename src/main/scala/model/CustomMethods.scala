package model

object CustomMethods {
  val doubleArg : Variable[Int] = Variable("x", JavaInt)
  val doubleModel = FunctionBuilder("twice", doubleArg, CodeBlock(List()), Multiplication(doubleArg, Literal(JavaInt, 2)))
}
