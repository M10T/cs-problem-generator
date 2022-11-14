@main def hello: Unit = 
  val model = JavaTranslator.randomGenerate()
  println(JavaTranslator.translateModel(model))
  val executedContext = CodeExecutor.executeModel(model)
  println("Displayed: " + executedContext.getDisplayed)
  println("Data: " + executedContext.getData())