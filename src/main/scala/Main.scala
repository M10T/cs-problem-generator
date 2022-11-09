@main def hello: Unit = 
  println(JavaString.displayInstance(JavaString.randomGenerate()))
  println(JavaInt.displayInstance(JavaInt.randomGenerate()))
  val model = Repetition("j",2,VariableCreation(JavaString,"test",Literal(JavaString, "test")))
  println(JavaTranslator.translateModel(model))
  println(CodeExecutor.executeModel(model).getData())
